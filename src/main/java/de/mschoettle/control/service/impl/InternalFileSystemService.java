package de.mschoettle.control.service.impl;

import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import de.mschoettle.entity.repository.IAccountRepository;
import de.mschoettle.entity.repository.IFileSystemObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IFileSystemObjectRepository fileSystemObjectRepository;

    @Autowired
    private IAccountRepository accountRepository;

    public void saveFileSystemObject(FileSystemObject fileSystemObject) {

        if (fileSystemObject == null) {
            throw new IllegalArgumentException("fileSystemObject is null");
        }

        fileSystemObjectRepository.save(fileSystemObject);
    }

    @Override
    @Transactional
    public void deleteFileSystemObject(Account account, long parentFolderId, long fileSystemObjectId) {

        Folder parentFolder = getFolder(account, parentFolderId);
        FileSystemObject fileSystemObject = getFileSystemObject(account, fileSystemObjectId);

        // if file is deleted the real file in the server filesystem also needs to be removed
        if(fileSystemObject instanceof File) {
            deleteFileFromServerFilesystem(((File) fileSystemObject).getFileReference());
        }

        // remove fileSystemObject from parent list
        parentFolder.removeFileSystemObject(fileSystemObjectId);

        // save parent and hibernate will remove the foreign key from child
        saveFileSystemObject(parentFolder);

        fileSystemObjectRepository.deleteById(fileSystemObjectId);

        recalculateSize(parentFolder);
    }

    private void deleteFileFromServerFilesystem(String fileRef) {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir"), "files", fileRef));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        fileSystemObjectRepository.save(new Folder("root", 0, account, null));
        account.setRootFolder(rootFolder);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void addNewFolderToFolder(Account account, long parentFolderId, String childFolderName) {

        if (childFolderName == null || childFolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("childFolderName was empty or null");
        }

        Folder parentFolder = getFolder(account, parentFolderId);

        Folder folderToAdd = new Folder(childFolderName, 0, account, parentFolder);
        saveFileSystemObject(folderToAdd);

        parentFolder.addFileSystemObject(folderToAdd);
        saveFileSystemObject(parentFolder);
    }

    // TODO this has to be done different at some time
    @Override
    @Transactional
    public void addFileToFolder(Account account, long parentFolderId, MultipartFile[] files) {

        Folder parentFolder = getFolder(account, parentFolderId);

        if (files == null || files.length < 1 || files[0] == null) {
            throw new IllegalArgumentException("MultiPartFile was null or empty");
        }

        MultipartFile f = files[0];

        if (f.getOriginalFilename() == null || f.getOriginalFilename().trim().isEmpty()) {
            throw new IllegalArgumentException("MultiPartFiles name was null or empty");
        }

        File file = new File(f.getOriginalFilename(), f.getSize(), account, parentFolder, f.getContentType());
        saveFileSystemObject(file);

        // TODO what should the fileRef actually be, is only id sufficient?
        String fileRef = file.getId() + "_" + file.getName();
        file.setFileReference(fileRef);
        saveFileSystemObject(file);
        parentFolder.addFileSystemObject(file);

        saveFileSystemObject(parentFolder);

        recalculateSize(parentFolder);

        try {
            // TODO make this constant somewhere
            Files.write(Paths.get(System.getProperty("user.dir"), "files", fileRef), f.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public FileSystemObject getFileSystemObject(Account account, long fileSystemObjectId) {

        FileSystemObject fileSystemObject = fileSystemObjectRepository.findById(fileSystemObjectId).orElseThrow(
                () -> new IllegalArgumentException("fileSystemObject " + fileSystemObjectId + " does not exist"));

        if (!fileSystemObject.getOwner().equals(account)) {
            throw new IllegalArgumentException("account + " + account + " does not own fileSystemObject " + fileSystemObject);
        }

        return fileSystemObject;
    }

    @Transactional
    public Folder getFolder(Account account, long folderId) {

        FileSystemObject fileSystemObject = getFileSystemObject(account, folderId);

        if(!(fileSystemObject instanceof Folder)) {
            throw new IllegalArgumentException("fileSystemObject " + folderId + " is not a folder");
        }

        return (Folder) fileSystemObject;
    }

    @Transactional
    public File getFile(Account account, long fileId) {

        FileSystemObject fileSystemObject = getFileSystemObject(account, fileId);

        if(!(fileSystemObject instanceof File)) {
            throw new IllegalArgumentException("fileSystemObject " + fileId + " is not a file");
        }

        return (File) fileSystemObject;
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
}
