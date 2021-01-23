package de.mschoettle.control.service;

import de.mschoettle.control.exception.*;
import de.mschoettle.entity.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface IFileSystemService {

    void saveFileSystemObject(FileSystemObject fileSystemObject);

    void deleteFileSystemObject(Account account, long parentFolderId, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException, IOException, NotAFolderException;

    void copyFileSystemObject(Account account, long fileSystemObject, long parentId) throws FileSystemObjectDoesNotExistException, NotAFileException, IOException, NotAFolderException;

    void giveAccountRootFolder(Account account) throws AccountDoesNotExistsException, FileSystemObjectDoesNotExistException;

    void addNewFolderToFolder(Account account, long parentFolderId, String childFolderName) throws AccountDoesNotExistsException, FileSystemObjectDoesNotExistException, NotAFolderException;

    void addMultipartFileToFolder(Account account, long parentFolderId, MultipartFile f) throws IOException, FileSystemObjectDoesNotExistException, NotAFolderException;

    ResponseEntity<ByteArrayResource> getFileSystemObjectResponseEntity(Account account, long fileId) throws IOException, FileSystemObjectDoesNotExistException;

    ResponseEntity<ByteArrayResource> getFileSystemObjectResponseEntityByPermission(Account account, long permissionId) throws IOException, FileSystemObjectDoesNotExistException, PermissionDoesNotExistException;

    byte[] getByteArrayOfFile(Account account, long fileId) throws NotAFileException, FileSystemObjectDoesNotExistException, IOException;

    Optional<FileSystemObject> getFileSystemObjectById(long id, Account account);

    void changeNameOfFileSystemObject(long id, Account account, String name) throws FileSystemObjectDoesNotExistException;

    void recalculateSize(Folder folder);

    String getAvailableName(Folder folder, String name);

    void moveFileSystemObject(Account account, long folderId, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException, NotAFolderException;

    void addAccessLogEntry(FileSystemObject fileSystemObject, AccessType accessType, String comment) throws FileSystemObjectDoesNotExistException;

    FileSystemObject getFileSystemObject(Account account, long id) throws FileSystemObjectDoesNotExistException;

    Folder getFolder(Account account, long id) throws FileSystemObjectDoesNotExistException, NotAFolderException;

    File getFile(Account account, long id) throws FileSystemObjectDoesNotExistException, NotAFileException;

    Folder getRootFolderBySecretKey(String secretKey) throws AccountDoesNotExistsException;

    ResponseEntity<ByteArrayResource> getFileResponseEntityBySecretKeyAndId(String secretKey, long id) throws AccountDoesNotExistsException, FileSystemObjectDoesNotExistException, IOException, NotAFileException;
}
