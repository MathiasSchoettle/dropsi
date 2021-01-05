package de.mschoettle.control.service;

import de.mschoettle.entity.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface IInternalFileSystemService extends IFileSystemService {

    void saveFileSystemObject(FileSystemObject fileSystemObject);

    void deleteFileSystemObject(Account account, long parentFolderId, long fileSystemObjectId);

    void giveAccountRootFolder(Account account);

    void addNewFolderToFolder(Account account, long parentFolderId, String childFolderName);

    void addFileToFolder(Account account, long parentFolderId, MultipartFile[] files) throws IOException;

    ResponseEntity<ByteArrayResource> getFileSystemObjectResponseEntity(Account account, long fileId) throws IOException;

    ResponseEntity<ByteArrayResource> getFileSystemObjectResponseEntityByPermission(Account account, long permissionId) throws IOException;

    Optional<FileSystemObject> getFileSystemObjectById(long id, Account account);

    void changeNameOfFileSystemObject(long id, Account account, String name);

    void recalculateSize(Folder folder);

    void moveFileSystemObject(Account account, long folderId, long fileSystemObjectId);

    void addAccessLogEntry(FileSystemObject fileSystemObject, AccessType accessType, String comment);

    FileSystemObject getFileSystemObject(Account account, long id);

    Folder getFolder(Account account, long id);

    File getFile(Account account, long id);
}
