package de.mschoettle.control.service.impl;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.*;
import de.mschoettle.entity.repository.IAccessLogEntryRepository;
import de.mschoettle.entity.repository.IFileSystemObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class InternalFileSystemService implements IInternalFileSystemService {

    private IPermissionService permissionService;

    private IFileSystemObjectRepository fileSystemObjectRepository;

    private IAccessLogEntryRepository accessLogEntryRepository;

    private IAccountService accountService;

    private static final List<String> SUPPORTED_FILE_TYPES = Arrays.asList("image/png", "text/plain");

    @Autowired
    public void setInjectedBean(IAccountService accountService,
                                IAccessLogEntryRepository accessLogEntryRepository,
                                IFileSystemObjectRepository fileSystemObjectRepository,
                                IPermissionService permissionService) {

        this.accountService = accountService;
        this.accessLogEntryRepository = accessLogEntryRepository;
        this.fileSystemObjectRepository = fileSystemObjectRepository;
        this.permissionService = permissionService;
    }

    @Override
    @Transactional
    public void deleteFileSystemObject(Account account, long parentFolderId, long fileSystemObjectId) {

        Folder parentFolder = getFolder(account, parentFolderId);
        String childName = parentFolder.removeFileSystemObject(fileSystemObjectId);
        saveFileSystemObject(parentFolder);

        fileSystemObjectRepository.deleteById(fileSystemObjectId);
        addAccessLogEntry(parentFolder, AccessType.EDITED, "Deleted child \"" + childName + "\"");
        recalculateSize(parentFolder);
    }

    @Override
    @Transactional
    public void giveAccountRootFolder(Account account) {

        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }

        if (account.getRootFolder() != null) {
            throw new IllegalArgumentException("account already has a rootFolder");
        }

        Folder rootFolder = new Folder("root", 0, account, null);
        addAccessLogEntry(rootFolder, AccessType.CREATED, "created Folder \"" + rootFolder.getName() + "\"");
        saveFileSystemObject(rootFolder);

        account.setRootFolder(rootFolder);
        accountService.saveAccount(account);
    }

    @Override
    @Transactional
    public void addNewFolderToFolder(Account account, long parentFolderId, String childFolderName) {

        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }

        if (childFolderName == null || childFolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("childFolderName was empty or null");
        }

        Folder parentFolder = getFolder(account, parentFolderId);

        Folder folderToAdd = new Folder(childFolderName, 0, account, parentFolder);
        addAccessLogEntry(folderToAdd, AccessType.CREATED, "Created Folder \"" + childFolderName + "\"");
        saveFileSystemObject(folderToAdd);

        parentFolder.addFileSystemObject(folderToAdd);
        addAccessLogEntry(parentFolder, AccessType.EDITED, "Added new Folder \"" + childFolderName + "\"");
        saveFileSystemObject(parentFolder);
    }

    // TODO refactor this, multiple files or even directories and rename method to something with upload
    @Override
    @Transactional
    public void addFileToFolder(Account account, long parentFolderId, MultipartFile[] files) throws IOException {

        Folder parentFolder = getFolder(account, parentFolderId);

        if (files == null || files.length < 1 || files[0] == null) {
            throw new IllegalArgumentException("MultiPartFile was null or empty");
        }

        MultipartFile f = files[0];

        if (f.getOriginalFilename() == null || f.getOriginalFilename().trim().isEmpty()) {
            throw new IllegalArgumentException("MultiPartFiles name was null or empty");
        }

        // remove file extension
        String name = f.getOriginalFilename();
        String extension = "";
        if (name.indexOf(".") > 0) {
            extension = name.substring(name.lastIndexOf(".") + 1);
            name = name.substring(0, name.lastIndexOf("."));
        }

        File file = new File(name, f.getSize(), account, parentFolder, extension, f.getContentType());
        saveFileSystemObject(file);

        // TODO what should the fileRef actually be, is only id sufficient?
        String fileRef = file.getId() + "";
        addAccessLogEntry(file, AccessType.CREATED, "Created File \"" + file.getName() + "\"");
        file.setFileReference(fileRef);
        saveFileSystemObject(file);

        parentFolder.addFileSystemObject(file);
        addAccessLogEntry(parentFolder, AccessType.EDITED, "Uploaded new File \"" + file.getName() + "\"");
        recalculateSize(parentFolder);

        // TODO make this constant somewhere
        Files.write(Paths.get(System.getProperty("user.dir"), "files", fileRef), f.getBytes(), StandardOpenOption.CREATE);
    }

    @Override
    @Transactional
    public ResponseEntity<ByteArrayResource> getFileSystemObjectResponseEntity(Account account, long fileSystemObjectId) throws IOException {

        FileSystemObject fileSystemObject = getFileSystemObject(account, fileSystemObjectId);
        addAccessLogEntry(fileSystemObject, AccessType.DOWNLAODED, "Downloaded by Account \"" + account.getName() + "\"");

        if(fileSystemObject instanceof File) {
            return getFileResponseEntity((File) fileSystemObject);
        } else {
            return getFolderResponseEntity((Folder) fileSystemObject, account);
        }
    }

    @Override
    public ResponseEntity<ByteArrayResource> getFileSystemObjectResponseEntityByPermission(Account account, long permissionId) throws IOException {

        Permission permission = permissionService.getPermission(account, permissionId);
        FileSystemObject fileSystemObject = permission.getShared();
        addAccessLogEntry(fileSystemObject, AccessType.DOWNLAODED, "Downloaded by Account \"" + account.getName() + "\"");

        if(fileSystemObject instanceof File) {
            return getFileResponseEntity((File) fileSystemObject);
        } else {
            return getFolderResponseEntity((Folder) fileSystemObject, account);
        }
    }

    private ResponseEntity<ByteArrayResource> getFileResponseEntity(File file) throws IOException {

        byte[] data = Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "files", file.getFileReference()));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.toString());
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .contentType(MediaType.valueOf(file.getFileType()))
                .body(resource);
    }

    private ResponseEntity<ByteArrayResource> getFolderResponseEntity(Folder folder, Account account) throws IOException {

        Path path = Paths.get(System.getProperty("user.dir"), "temp", account.getId() + "" + Timestamp.valueOf(LocalDateTime.now()).getTime() + ".zip");

        URI p = path.toUri();
        URI uri = URI.create( "jar:" + p );

        // TODO make this static maybe?
        Map<String, String> env = new HashMap<>();
        env.put( "create", "true" );

        try ( FileSystem zipfs = FileSystems.newFileSystem( uri, env ) ) {
            addFolder(zipfs, folder, "/");
        }

        byte[] data = Files.readAllBytes(path);
        Files.delete(path);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + folder.getName() + ".zip");
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(resource);
    }

    private void addFolder(FileSystem zipfs, Folder folder, String path) throws IOException {

        Files.createDirectory(zipfs.getPath(path, folder.getName()));

        for(FileSystemObject f : folder.getContents()) {

            if(f instanceof Folder) {
                addFolder(zipfs, (Folder) f, path + "/" + folder.getName());
            } else if(f instanceof File) {
                Files.write( zipfs.getPath(path, folder.getName(), f.toString()), Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "files", f.getId() + "")));
            }
        }
    }

    @Override
    @Transactional
    public void moveFileSystemObject(Account account, long folderId, long fileSystemObjectId) {

        Folder newFolder = getFolder(account, folderId);
        FileSystemObject fileSystemObject = getFileSystemObject(account, fileSystemObjectId);
        Folder oldFolder = fileSystemObject.getParent();

        if(newFolder.equals(fileSystemObject)) {
            throw new IllegalArgumentException("FilesystemObject " + fileSystemObject + " can not be moved into itself");
        }

        // if moved to same folder do nothing
        if(newFolder.equals(oldFolder)) {
            return;
        }

        oldFolder.removeFileSystemObject(fileSystemObjectId);
        addAccessLogEntry(oldFolder, AccessType.EDITED, "Removed \"" + fileSystemObject.getName() + "\"");
        recalculateSize(oldFolder);

        fileSystemObject.setParent(newFolder);
        addAccessLogEntry(newFolder, AccessType.EDITED, "Added \"" + fileSystemObject.getName() + "\"");
        saveFileSystemObject(fileSystemObject);

        newFolder.addFileSystemObject(fileSystemObject);
        addAccessLogEntry(fileSystemObject, AccessType.MOVED, "Moved from \"" + oldFolder.getName() + "\" to \"" + newFolder.getName() + "\"");
        saveFileSystemObject(newFolder);
        recalculateSize(newFolder);
    }

    @Transactional
    @Override
    public FileSystemObject getFileSystemObject(Account account, long fileSystemObjectId) {

        FileSystemObject fileSystemObject = fileSystemObjectRepository.findById(fileSystemObjectId).orElseThrow(
                () -> new IllegalArgumentException("fileSystemObject " + fileSystemObjectId + " does not exist"));

        if (!fileSystemObject.getOwner().equals(account)) {
            throw new IllegalArgumentException("account + " + account + " does not own fileSystemObject " + fileSystemObject);
        }

        return fileSystemObject;
    }

    @Transactional
    @Override
    public Folder getFolder(Account account, long folderId) {

        FileSystemObject fileSystemObject = getFileSystemObject(account, folderId);

        if (!(fileSystemObject instanceof Folder)) {
            throw new IllegalArgumentException("fileSystemObject " + folderId + " is not a folder");
        }

        return (Folder) fileSystemObject;
    }

    @Transactional
    @Override
    public File getFile(Account account, long fileId) {

        FileSystemObject fileSystemObject = getFileSystemObject(account, fileId);

        if (!(fileSystemObject instanceof File)) {
            throw new IllegalArgumentException("fileSystemObject " + fileId + " is not a file");
        }

        return (File) fileSystemObject;
    }

    @Transactional
    @Override
    public void saveFileSystemObject(FileSystemObject fileSystemObject) {

        if (fileSystemObject == null) {
            throw new IllegalArgumentException("fileSystemObject is null");
        }

        fileSystemObjectRepository.save(fileSystemObject);
    }

    @Override
    @Transactional
    public Optional<FileSystemObject> getFileSystemObjectById(long id, Account account) {
        return fileSystemObjectRepository.findByIdAndOwner(id, account);
    }

    @Override
    @Transactional
    public void recalculateSize(Folder folder) {

        long size = 0;

        for (FileSystemObject f : folder.getContents()) {
            size += f.getFileSize();
        }

        folder.setFileSize(size);
        saveFileSystemObject(folder);

        if (folder.getParent() != null) {
            recalculateSize(folder.getParent());
        }
    }

    @Override
    @Transactional
    public void changeNameOfFileSystemObject(long fileSystemObjectId, Account account, String name) {

        // TODO input validation

        if (name != null && !name.trim().isEmpty()) {
            FileSystemObject fileSystemObject = getFileSystemObject(account, fileSystemObjectId);
            String oldName = fileSystemObject.getName();

            if (oldName.equals(name)) {
                return;
            }

            fileSystemObject.setName(name);
            addAccessLogEntry(fileSystemObject, AccessType.EDITED, "Changed name from \"" + oldName + "\" to \"" + fileSystemObject.getName() + "\"");
            saveFileSystemObject(fileSystemObject);
        }
    }

    @Transactional
    @Override
    public void addAccessLogEntry(FileSystemObject fileSystemObject, AccessType accessType, String comment) {

        if (fileSystemObject == null) {
            throw new IllegalArgumentException("FileSystemObject is null");
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment is null or empty");
        }

        // TODO check this somewhere else maybe? or add boolean to folder isRootFolder
        // don't add AccessLogEntries to root folder -> are not accessible for user anyways
        if (fileSystemObject.getParent() == null) {
            return;
        }

        AccessLogEntry accessLogEntry = new AccessLogEntry(fileSystemObject, accessType, comment);
        accessLogEntryRepository.save(accessLogEntry);
    }
}
