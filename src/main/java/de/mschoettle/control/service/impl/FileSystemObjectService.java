package de.mschoettle.control.service.impl;

import de.mschoettle.control.exception.*;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.*;
import de.mschoettle.entity.repository.IAccessLogEntryRepository;
import de.mschoettle.entity.repository.IFileSystemObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class FileSystemObjectObjectService implements IFileSystemObjectService {

    private IPermissionService permissionService;

    private IFileSystemObjectRepository fileSystemObjectRepository;

    private IAccessLogEntryRepository accessLogEntryRepository;

    private IAccountService accountService;

    private IFileService fileService;

    @Autowired
    public void setInjectedBean(IAccountService accountService,
                                IAccessLogEntryRepository accessLogEntryRepository,
                                IFileSystemObjectRepository fileSystemObjectRepository,
                                IPermissionService permissionService,
                                @Qualifier("local") IFileService fileService) {

        this.accountService = accountService;
        this.accessLogEntryRepository = accessLogEntryRepository;
        this.fileSystemObjectRepository = fileSystemObjectRepository;
        this.permissionService = permissionService;
        this.fileService = fileService;
    }

    @Override
    @Transactional
    public void deleteFileSystemObject(Account account, long parentFolderId, long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException,
            IOException,
            NotAFolderException {

        Folder parentFolder = getFolder(account, parentFolderId);
        String childName = parentFolder.removeFileSystemObject(fileSystemObjectId);
        saveFileSystemObject(parentFolder);

        FileSystemObject fileSystemObject = getFileSystemObject(account, fileSystemObjectId);
        removePhysicalFilesOfChildren(fileSystemObject);

        fileSystemObjectRepository.deleteById(fileSystemObjectId);

        addAccessLogEntry(parentFolder, AccessType.EDITED, "Deleted child \"" + childName + "\"");
        recalculateSize(parentFolder);
    }

    @Override
    @Transactional
    public void copyFileSystemObject(Account account, long fileSystemObjectId, long parentId) throws
            FileSystemObjectDoesNotExistException,
            NotAFileException,
            IOException,
            NotAFolderException {

        cloneFileSystemObject(account, fileSystemObjectId, parentId);
    }

    @Transactional
    public FileSystemObject cloneFileSystemObject(Account account, long fileSystemObjectId, long parentId) throws
            FileSystemObjectDoesNotExistException,
            NotAFileException,
            IOException,
            NotAFolderException {

        FileSystemObject fileSystemObject = getFileSystemObject(account, fileSystemObjectId);

        if(fileSystemObject instanceof Folder) {
            return cloneFolder(account, fileSystemObjectId, parentId);
        } else {
            return cloneFile(account, fileSystemObjectId, parentId);
        }
    }

    @Transactional
    public Folder cloneFolder(Account account, long folderId, long parentId) throws
            NotAFolderException,
            FileSystemObjectDoesNotExistException,
            IOException,
            NotAFileException {

        Folder folder = getFolder(account, folderId);
        Folder parent = getFolder(account, parentId);
        Folder copied = new Folder(getAvailableName(parent, folder.getName()), folder.getFileSize(), account, parent);

        saveFileSystemObject(copied);

        parent.addFileSystemObject(copied);
        saveFileSystemObject(parent);

        for(FileSystemObject f : folder.getContents()) {
            copied.addFileSystemObject(cloneFileSystemObject(account, f.getId(), copied.getId()));
        }

        saveFileSystemObject(copied);
        return copied;
    }

    @Transactional
    public File cloneFile(Account account, long fileId, long parentId) throws NotAFileException, FileSystemObjectDoesNotExistException, NotAFolderException, IOException {

        File file = getFile(account, fileId);
        Folder parent = getFolder(account, parentId);
        File copied = new File(getAvailableName(parent, file.getName()) + "", file.getFileSize(), account, parent, file.getFileExtension(), file.getFileType());
        addAccessLogEntry(file, AccessType.COPIED, "File was copied");
        saveFileSystemObject(copied);
        copied.setFileReference(copied.getId() + "");

        saveFileSystemObject(copied);

        parent.addFileSystemObject(copied);
        saveFileSystemObject(parent);

        fileService.writeFile(fileService.getByteArrayOfFile(file), copied.getFileReference());

        return copied;
    }

    private void removePhysicalFilesOfChildren(FileSystemObject fileSystemObject) throws IOException {
        if(fileSystemObject instanceof File) {
            fileService.deleteFile((File) fileSystemObject);
        } else if(fileSystemObject instanceof Folder) {
            for(FileSystemObject f : ((Folder) fileSystemObject).getContents()) {
                removePhysicalFilesOfChildren(f);
            }
        }
    }

    @Override
    @Transactional
    public void giveAccountRootFolder(Account account) throws AccountDoesNotExistsException, FileSystemObjectDoesNotExistException {

        if (account == null) {
            throw new AccountDoesNotExistsException();
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
    public void addNewFolderToFolder(Account account, long parentFolderId, String childFolderName) throws
            AccountDoesNotExistsException,
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        if (account == null) {
            throw new AccountDoesNotExistsException();
        }

        if (childFolderName == null || childFolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("childFolderName was empty or null");
        }

        Folder parentFolder = getFolder(account, parentFolderId);

        Folder folderToAdd = new Folder(getAvailableName(parentFolder, childFolderName), 0, account, parentFolder);
        addAccessLogEntry(folderToAdd, AccessType.CREATED, "Created Folder \"" + childFolderName + "\"");
        saveFileSystemObject(folderToAdd);

        parentFolder.addFileSystemObject(folderToAdd);
        addAccessLogEntry(parentFolder, AccessType.EDITED, "Added new Folder \"" + childFolderName + "\"");
        saveFileSystemObject(parentFolder);
    }

    @Override
    @Transactional
    public void addMultipartFileToFolder(Account account, long parentFolderId, MultipartFile f) throws
            IOException,
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        Folder parentFolder = getFolder(account, parentFolderId);

        if (f == null || f.getSize() < 0) {
            throw new IllegalArgumentException("MultiPartFile was null or empty");
        }

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

        File file = new File(getAvailableName(parentFolder, name), f.getSize(), account, parentFolder, extension, f.getContentType());
        saveFileSystemObject(file);

        String fileRef = file.getId() + "";
        addAccessLogEntry(file, AccessType.CREATED, "Created File \"" + file.getName() + "\"");
        file.setFileReference(fileRef);
        saveFileSystemObject(file);

        parentFolder.addFileSystemObject(file);
        addAccessLogEntry(parentFolder, AccessType.EDITED, "Uploaded new File \"" + file.getName() + "\"");
        recalculateSize(parentFolder);

        fileService.writeFile(f.getBytes(), fileRef);
    }

    @Override
    @Transactional
    public ResponseEntity<ByteArrayResource> getFileSystemObjectResponseEntity(Account account, long fileSystemObjectId) throws IOException, FileSystemObjectDoesNotExistException {

        FileSystemObject fileSystemObject = getFileSystemObject(account, fileSystemObjectId);
        addAccessLogEntry(fileSystemObject, AccessType.DOWNLAODED, "Downloaded by Account \"" + account.getName() + "\"");

        if (fileSystemObject instanceof File) {
            return getFileResponseEntity((File) fileSystemObject);
        } else {
            return getFolderResponseEntity((Folder) fileSystemObject);
        }
    }

    @Override
    public ResponseEntity<ByteArrayResource> getFileSystemObjectResponseEntityByPermission(Account account, long permissionId) throws IOException, FileSystemObjectDoesNotExistException, PermissionDoesNotExistException {

        Permission permission = permissionService.getPermission(account, permissionId);
        FileSystemObject fileSystemObject = permission.getShared();
        addAccessLogEntry(fileSystemObject, AccessType.DOWNLAODED, "Downloaded by Account \"" + account.getName() + "\"");

        if (fileSystemObject instanceof File) {
            return getFileResponseEntity((File) fileSystemObject);
        } else {
            return getFolderResponseEntity((Folder) fileSystemObject);
        }
    }

    private ResponseEntity<ByteArrayResource> getFileResponseEntity(File file) throws IOException {

        byte[] data = fileService.getByteArrayOfFile(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.toString());
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .contentType(MediaType.valueOf(file.getFileType()))
                .body(resource);
    }

    private ResponseEntity<ByteArrayResource> getFolderResponseEntity(Folder folder) throws IOException {

        byte[] data = fileService.getByteArrayOfFolder(folder);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + folder.getName() + ".zip");
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(resource);
    }

    @Override
    @Transactional
    public void moveFileSystemObject(Account account, long folderId, long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        Folder newFolder = getFolder(account, folderId);
        FileSystemObject fileSystemObject = getFileSystemObject(account, fileSystemObjectId);
        Folder oldFolder = fileSystemObject.getParent();

        if (newFolder.equals(fileSystemObject)) {
            throw new IllegalArgumentException("FilesystemObject " + fileSystemObject + " can not be moved into itself");
        }

        // if moved to same folder do nothing
        if (newFolder.equals(oldFolder)) {
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
    public FileSystemObject getFileSystemObject(Account account, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException {

        FileSystemObject fileSystemObject = fileSystemObjectRepository.findById(fileSystemObjectId).orElseThrow(
                () -> new FileSystemObjectDoesNotExistException(fileSystemObjectId));

        if (!fileSystemObject.getOwner().equals(account)) {
            throw new IllegalArgumentException("account + " + account + " does not own fileSystemObject " + fileSystemObject);
        }

        return fileSystemObject;
    }

    @Transactional
    @Override
    public Folder getFolder(Account account, long folderId) throws
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        FileSystemObject fileSystemObject = getFileSystemObject(account, folderId);

        if (!(fileSystemObject instanceof Folder)) {
            throw new NotAFolderException(folderId);
        }

        return (Folder) fileSystemObject;
    }

    @Transactional
    @Override
    public File getFile(Account account, long fileId) throws
            FileSystemObjectDoesNotExistException,
            NotAFileException {

        FileSystemObject fileSystemObject = getFileSystemObject(account, fileId);

        if (!(fileSystemObject instanceof File)) {
            throw new NotAFileException(fileId);
        }

        return (File) fileSystemObject;
    }

    @Override
    public Folder getRootFolderBySecretKey(String secretKey) throws AccountDoesNotExistsException {
        return accountService.getAccountBySecretKey(secretKey).getRootFolder();
    }

    @Override
    public ResponseEntity<ByteArrayResource> getFileResponseEntityBySecretKeyAndId(String secretKey, long fileId) throws
            AccountDoesNotExistsException,
            FileSystemObjectDoesNotExistException,
            IOException,
            NotAFileException {

        Account account = accountService.getAccountBySecretKey(secretKey);
        File file = getFile(account, fileId);

        return getFileResponseEntity(file);
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
    public String getAvailableName(Folder folder, String name) {

        if(folder == null || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("folder or name was not valid");
        }

        name = name.trim();

        for(FileSystemObject f : folder.getContents()) {
            if(f.getName().equals(name)) {
                if(name.matches(".*\\([0-9]*\\)$")) {
                    int number = Integer.parseInt(name.charAt(name.length() - 2) + "");
                    return getAvailableName(folder, name.substring(0, name.length() - 3) + " (" + (number + 1) + ")");
                } else {
                    return getAvailableName(folder, name + " (1)");
                }
            }
        }

        return name;
    }

    @Override
    @Transactional
    public void changeNameOfFileSystemObject(long fileSystemObjectId, Account account, String name) throws FileSystemObjectDoesNotExistException {

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
    public void addAccessLogEntry(FileSystemObject fileSystemObject, AccessType accessType, String comment) throws FileSystemObjectDoesNotExistException {

        if (fileSystemObject == null) {
            throw new FileSystemObjectDoesNotExistException();
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment is null or empty");
        }

        // don't add AccessLogEntries to root folder -> are not accessible for user anyways
        if (fileSystemObject.getParent() == null) {
            return;
        }

        AccessLogEntry accessLogEntry = new AccessLogEntry(fileSystemObject, accessType, comment);
        accessLogEntryRepository.save(accessLogEntry);
    }

    @Override
    public Map<LocalDate, List<AccessLogEntry>> getAccessLogEntriesMap(FileSystemObject fileSystemObject) {

        Map<LocalDate, List<AccessLogEntry>> map = new TreeMap<>(Collections.reverseOrder());

        for (AccessLogEntry a : fileSystemObject.getAccessLogs()) {

            LocalDate ld = a.getCreationDate().toLocalDate();

            if (map.containsKey(ld)) {
                map.get(ld).add(a);
            } else {
                List<AccessLogEntry> list = new ArrayList<>();
                list.add(a);
                map.put(ld, list);
            }
        }

        return map;
    }
}
