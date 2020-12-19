package de.mschoettle.control.service.impl;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IInternalFileSystemService;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Service
public class InternalFileSystemService implements IInternalFileSystemService {

    private IFileSystemObjectRepository fileSystemObjectRepository;

    private IAccessLogEntryRepository accessLogEntryRepository;

    private IAccountService accountService;

    @Autowired
    public void setInjectedBean(IAccountService accountService,
                                IAccessLogEntryRepository accessLogEntryRepository,
                                IFileSystemObjectRepository fileSystemObjectRepository) {

        this.accountService = accountService;
        this.accessLogEntryRepository = accessLogEntryRepository;
        this.fileSystemObjectRepository = fileSystemObjectRepository;
    }

    @Override
    @Transactional
    public void deleteFileSystemObject(Account account, long parentFolderId, long fileSystemObjectId) {

        Folder parentFolder = getFolder(account, parentFolderId);
        String childName = parentFolder.removeFileSystemObject(fileSystemObjectId);
        saveFileSystemObject(parentFolder);
        fileSystemObjectRepository.deleteById(fileSystemObjectId);
        addAccessLogEntry(account, parentFolder, AccessType.EDITED, "Deleted child " + childName);
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
        saveFileSystemObject(rootFolder);
        addAccessLogEntry(account, rootFolder, AccessType.CREATED, "created Folder " + rootFolder.getName());
        saveFileSystemObject(rootFolder);

        account.setRootFolder(rootFolder);
        accountService.saveAccount(account);
    }

    @Override
    @Transactional
    public void deleteRootFolderOfAccount(Account account) {
        Folder rootFolder = account.getRootFolder();
        account.setRootFolder(null);
        accountService.saveAccount(account);
        fileSystemObjectRepository.delete(rootFolder);
    }

    @Override
    @Transactional
    public void addNewFolderToFolder(Account account, long parentFolderId, String childFolderName) {

        if (childFolderName == null || childFolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("childFolderName was empty or null");
        }

        Folder parentFolder = getFolder(account, parentFolderId);

        Folder folderToAdd = new Folder(childFolderName, 0, account, parentFolder);
        addAccessLogEntry(account, folderToAdd, AccessType.CREATED, "Created Folder " + childFolderName);
        saveFileSystemObject(folderToAdd);

        parentFolder.addFileSystemObject(folderToAdd);
        addAccessLogEntry(account, parentFolder, AccessType.EDITED, "Added new child folder " + childFolderName);
        saveFileSystemObject(parentFolder);
    }

    // TODO refactor this, multiple files or even directories
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
        if(name.indexOf(".") > 0) {
            extension = name.substring(name.lastIndexOf(".") + 1);
            name = name.substring(0 , name.lastIndexOf("."));
        }

        File file = new File(name, f.getSize(), account, parentFolder, extension, f.getContentType());
        saveFileSystemObject(file);

        // TODO what should the fileRef actually be, is only id sufficient?
        String fileRef = file.getId() + "";
        addAccessLogEntry(account, file, AccessType.CREATED, "Created File " + file.getName());
        file.setFileReference(fileRef);
        saveFileSystemObject(file);

        parentFolder.addFileSystemObject(file);
        addAccessLogEntry(account, parentFolder, AccessType.EDITED, "Added new File " + file.getName());
        recalculateSize(parentFolder);

        // TODO make this constant somewhere
        Files.write(Paths.get(System.getProperty("user.dir"), "files", fileRef), f.getBytes(), StandardOpenOption.CREATE);
    }

    @Override
    public ResponseEntity<ByteArrayResource> getFileResponseEntity(Account account, long fileSystemObjectId) throws IOException {

        File file = getFile(account, fileSystemObjectId);
        addAccessLogEntry(account, file, AccessType.DOWNLAODED, "Downloaded by " + account.getName());

        byte[] data = Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "files", file.getFileReference()));
        HttpHeaders headers = new HttpHeaders(); headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.toString());
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .contentType(MediaType.valueOf(file.getFileType()))
                .body(resource);
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

        if(!(fileSystemObject instanceof Folder)) {
            throw new IllegalArgumentException("fileSystemObject " + folderId + " is not a folder");
        }

        return (Folder) fileSystemObject;
    }

    @Transactional
    @Override
    public File getFile(Account account, long fileId) {

        FileSystemObject fileSystemObject = getFileSystemObject(account, fileId);

        if(!(fileSystemObject instanceof File)) {
            throw new IllegalArgumentException("fileSystemObject " + fileId + " is not a file");
        }

        return (File) fileSystemObject;
    }

    @Transactional
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

        if(name != null && !name.trim().isEmpty()) {
            FileSystemObject fileSystemObject = getFileSystemObject(account, fileSystemObjectId);
            String oldName = fileSystemObject.getName();

            if(oldName.equals(name)) {
                return;
            }

            fileSystemObject.setName(name);
            addAccessLogEntry(account, fileSystemObject, AccessType.EDITED, "Changed name from " + oldName + " to " + fileSystemObject.getName());
            saveFileSystemObject(fileSystemObject);
        }
    }

    @Override
    @Transactional
    public void addAccessLogEntry(Account account, FileSystemObject fileSystemObject, AccessType accessType, String comment) {

        if(fileSystemObject == null) {
            throw new IllegalArgumentException("FileSystemObject is null");
        }

        if(!fileSystemObject.getOwner().equals(account)) {
            throw new IllegalArgumentException("Account doesn't own FileSystemObject");
        }

        if(comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment is null or empty");
        }

        // TODO check this somewhere else maybe? or add boolean to foler isRootFolder
        // don't add AccessLogEntries to root folder -> are not accessible for user anyways
        if(fileSystemObject.getParent() == null) {
            return;
        }

        AccessLogEntry accessLogEntry = new AccessLogEntry(fileSystemObject, accessType, comment);
        accessLogEntryRepository.save(accessLogEntry);
    }
}
