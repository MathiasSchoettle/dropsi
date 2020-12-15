package de.mschoettle.control.service.impl;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import de.mschoettle.entity.repository.IFileSystemObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;

@Service
public class InternalFileSystemService implements IInternalFileSystemService {

    @Autowired
    private IFileSystemObjectRepository fileSystemObjectRepository;

    public void saveFileSystemObject(FileSystemObject fileSystemObject) {

        if (fileSystemObject == null) {
            throw new IllegalArgumentException("fileSystemObject is null");
        }

        fileSystemObjectRepository.save(fileSystemObject);
    }

    @Override
    @Transactional
    public void deleteFileSystemObject(Account account, Folder parent, long fileSystemObjectId) {

        if (!parent.getOwner().equals(account)) {
            throw new IllegalArgumentException("account + " + account + " does not own parent folder " + parent);
        }

        if (!parent.containsFileSystemObject(fileSystemObjectId)) {
            throw new IllegalArgumentException("account + " + account + " does not own parent folder " + parent);
        }

        // remove fileSystemObject from parent list
        parent.removeFileSystemObject(fileSystemObjectId);

        // save parent and hibernate will remove the foreign key from child
        saveFileSystemObject(parent);

        // delete child from database
        fileSystemObjectRepository.deleteById(fileSystemObjectId);

        recalculateSize(parent);
    }

    @Override
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
    }

    @Override
    public void addNewFolderToFolder(Account account, Folder parentFolder, String childFolderName) {
        Folder folderToAdd = new Folder(childFolderName, 0, account, parentFolder);
        parentFolder.addFileSystemObject(folderToAdd);
        saveFileSystemObject(folderToAdd);
    }

    @Override
    @Transactional
    public void addFileToFolder(Account account, Folder parentFolder, MultipartFile[] files) {

        if(files.length < 1 || files[0] == null) {
            throw new IllegalArgumentException("MultiPartFile was null or empty");
        }

        MultipartFile f = files[0];

        if(f.getOriginalFilename() == null || f.getOriginalFilename().trim().isEmpty()) {
            throw new IllegalArgumentException("MultiPartFiles name was null or empty");
        }

        File file = new File(f.getOriginalFilename(), f.getSize(), account, parentFolder, f.getContentType());
        saveFileSystemObject(file);

        recalculateSize(parentFolder);

        String fileRef = file.getId() + "_" + file.getName();
        file.setFileReference(fileRef);
        saveFileSystemObject(file);
        parentFolder.addFileSystemObject(file);

        // TODO make this constant somewhere

        try {
            Files.write(Paths.get(System.getProperty("user.dir"), "files", fileRef), f.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<FileSystemObject> getFileSystemObjectById(long id, Account account) {
        return fileSystemObjectRepository.findByIdAndOwner(id, account);
    }

    @Override
    @Transactional
    public void recalculateSize(Folder folder) {

        long size = 0;

        for(FileSystemObject f : folder.getContents()) {
            size += f.getFileSize();
        }

        folder.setFileSize(size);
        saveFileSystemObject(folder);

        if(folder.getParent() != null) {
            recalculateSize(folder.getParent());
        }
    }
}
