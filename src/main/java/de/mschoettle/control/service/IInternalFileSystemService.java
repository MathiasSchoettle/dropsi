package de.mschoettle.control.service;

import de.mschoettle.entity.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface IInternalFileSystemService extends IFileSystemService {

    void deleteFileSystemObject(Account account, long parentFolderId, long fileSystemObjectId);

    void giveAccountRootFolder(Account account);

    void deleteRootFolderOfAccount(Account account);

    void addNewFolderToFolder(Account account, long parentFolderId, String childFolderName);

    void addFileToFolder(Account account, long parentFolderId, MultipartFile[] files) throws IOException;

    ResponseEntity<ByteArrayResource> getFileResponseEntity(Account account, long fileSystemObjectId) throws IOException;

    Optional<FileSystemObject> getFileSystemObjectById(long id, Account account);

    void changeNameOfFileSystemObject(long id, Account account, String name);

    void recalculateSize(Folder folder);

    FileSystemObject getFileSystemObject(Account account, long id);

    Folder getFolder(Account account, long id);

    File getFile(Account account, long id);

    public void addAccessLogEntry(Account account, FileSystemObject fileSystemObject, AccessType accessType, String comment);
}
