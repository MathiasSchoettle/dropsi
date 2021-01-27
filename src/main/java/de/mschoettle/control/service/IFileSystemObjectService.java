package de.mschoettle.control.service;

import de.mschoettle.control.exception.*;
import de.mschoettle.entity.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IFileSystemObjectService {

    /**
     * Save a {@link FileSystemObject}
     */
    void saveFileSystemObject(FileSystemObject fileSystemObject);

    /**
     * Delete the given fileSystemObject and update the parent
     *
     * @throws FileSystemObjectDoesNotExistException if the parent {@link Folder} does not exist
     * @throws IOException                           if a problem occurred while writing the file
     * @throws NotAFolderException                   if the given parent was not a {@link Folder}
     */
    void deleteFileSystemObject(Account account, long parentFolderId, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException, IOException, NotAFolderException;

    /**
     * Copies a {@link FileSystemObject} into the same parent
     *
     * @param account          The owner of the {@link FileSystemObject}
     * @param parentFolderId   The parent of the given {@link FileSystemObject}
     * @param fileSystemObject The {@link FileSystemObject} to be copied
     * @throws FileSystemObjectDoesNotExistException if the parent {@link Folder} does not exist
     * @throws IOException                           if a problem occurred while writing the file
     * @throws NotAFolderException                   if the given parent was not a {@link Folder}
     */
    void copyFileSystemObject(Account account, long parentFolderId, long fileSystemObject) throws FileSystemObjectDoesNotExistException, NotAFileException, IOException, NotAFolderException;

    /**
     * Gives the {@link Account} a root {@link Folder}
     *
     * @param account The {@link Account} to add the root {@link Folder} to
     * @throws AccountDoesNotExistsException if the given {@link Account} does not exist
     */
    void giveAccountRootFolder(Account account) throws AccountDoesNotExistsException;

    /**
     * Adds a new {@link Folder} to the given parent with the given name
     *
     * @throws AccountDoesNotExistsException         if the given {@link Account} does not exist
     * @throws FileSystemObjectDoesNotExistException if the given parent {@link Folder} does note exist
     * @throws NotAFolderException                   If the given folderId does not reference a Folder
     */
    void addNewFolderToFolder(Account account, long parentFolderId, String childFolderName) throws AccountDoesNotExistsException, FileSystemObjectDoesNotExistException, NotAFolderException;

    /**
     * Saves the given {@link MultipartFile} as a {@link File} and adds this to the given {@link Folder}
     *
     * @throws IOException                           if a problem occurred while writing the file
     * @throws FileSystemObjectDoesNotExistException if the given {@link Folder} does not exist
     * @throws NotAFolderException                   if the given folderId does not reference a {@link Folder}
     */
    void addMultipartFileToFolder(Account account, long parentFolderId, MultipartFile multipartFile) throws IOException, FileSystemObjectDoesNotExistException, NotAFolderException;

    /**
     * @return The {@link ResponseEntity} of the requested {@link FileSystemObject}
     * @throws IOException                           if a problem occurred while reading the files
     * @throws FileSystemObjectDoesNotExistException if the requested {@link FileSystemObject} does not exist
     */
    ResponseEntity<ByteArrayResource> getFileSystemObjectResponseEntity(Account account, long fileSystemObjectId) throws IOException, FileSystemObjectDoesNotExistException;

    /**
     * @return The {@link ResponseEntity} of the requested {@link FileSystemObject}
     * @throws IOException                           if a problem occurred while reading the files
     * @throws FileSystemObjectDoesNotExistException if the requested {@link FileSystemObject} does not exist
     * @throws PermissionDoesNotExistException       If the given {@link Permission} does not exist
     */
    ResponseEntity<ByteArrayResource> getFileSystemObjectResponseEntityByPermission(Account account, long permissionId) throws IOException, FileSystemObjectDoesNotExistException, PermissionDoesNotExistException;

    /**
     * @return The requested {@link FileSystemObject}
     */
    Optional<FileSystemObject> getFileSystemObjectById(Account account, long fileSystemObjectId);

    /**
     * Changes the name of the given {@link FileSystemObject}
     *
     * @throws FileSystemObjectDoesNotExistException if the {@link FileSystemObject} does not exist
     */
    void changeNameOfFileSystemObject(Account account, long fileSystemObjectId, String name) throws FileSystemObjectDoesNotExistException;

    /**
     * Recalculates the size of the given {@link Folder} and its parents.
     */
    void recalculateSize(Folder folder);

    /**
     * Checks if the given name already exists in the given {@link Folder}s children and returns a unique name
     *
     * @param folder The given parent Folder
     * @param name   The desired name
     * @return The desired name transformed to be unique
     */
    String getAvailableName(Folder folder, String name);

    /**
     * Moves the given {@link FileSystemObject} to the desired Folder
     *
     * @param account            The owner of the {@link Folder} and {@link FileSystemObject}
     * @param folderId           The {@link Folder} to move to
     * @param fileSystemObjectId The {@link FileSystemObject} to be moved
     * @throws FileSystemObjectDoesNotExistException If the {@link Folder} or the {@link FileSystemObject} do not exist
     * @throws NotAFolderException                   If the given FolderId does not correspond to a Folder
     */
    void moveFileSystemObject(Account account, long folderId, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException, NotAFolderException;

    /**
     * Add a {@link AccessLogEntry} to a {@link FileSystemObject}
     *
     * @param fileSystemObject The given {@link FileSystemObject}
     * @param accessType       The corresponding {@link AccessType}
     * @param comment          A comment {@link String}
     * @throws FileSystemObjectDoesNotExistException If the {@link FileSystemObject} does not exist
     */
    void addAccessLogEntry(FileSystemObject fileSystemObject, AccessType accessType, String comment) throws FileSystemObjectDoesNotExistException;

    /**
     * This gives a map of {@link AccessLogEntry} of a {@link FileSystemObject} mapped by the {@link LocalDate} part of the creationDate
     *
     * @param fileSystemObject The given {@link FileSystemObject}
     * @return A Map of {@link AccessLogEntry}
     */
    Map<LocalDate, List<AccessLogEntry>> getAccessLogEntriesMap(FileSystemObject fileSystemObject);

    /**
     * @param account The given {@link Account}
     * @param id      The id of the {@link FileSystemObject}
     * @return the requested {@link FileSystemObject}
     * @throws FileSystemObjectDoesNotExistException if the {@link FileSystemObject} does not exist
     */
    FileSystemObject getFileSystemObject(Account account, long id) throws FileSystemObjectDoesNotExistException;

    /**
     * @param account The given {@link Account}
     * @param id      The id of the {@link Folder}
     * @return the requested {@link Folder}
     * @throws FileSystemObjectDoesNotExistException if the {@link FileSystemObject} does not exist
     * @throws NotAFolderException                   if the id does not correspond to a {@link Folder}
     */
    Folder getFolder(Account account, long id) throws FileSystemObjectDoesNotExistException, NotAFolderException;

    /**
     * @param account The given {@link Account}
     * @param id      The id of the {@link File}
     * @return the requested {@link File}
     * @throws FileSystemObjectDoesNotExistException if the {@link FileSystemObject} does not exist
     * @throws NotAFileException                     if the id does not correspond to a {@link File}
     */
    File getFile(Account account, long id) throws FileSystemObjectDoesNotExistException, NotAFileException;

    /**
     * @param secretKey The given secretKey of an {@link Account}
     * @return The requested {@link Account}
     * @throws AccountDoesNotExistsException if no {@link Account} exists with the given secretKey
     */
    Folder getRootFolderBySecretKey(String secretKey) throws AccountDoesNotExistsException;

    /**
     * @param secretKey secretKey The given secretKey of an {@link Account}
     * @param id        The id of the requested File
     * @return A {@link ResponseEntity} of type {@link ByteArrayResource} containing the data of the requested {@link FileSystemObject}
     * @throws AccountDoesNotExistsException         if no {@link Account} exists with the given secretKey
     * @throws FileSystemObjectDoesNotExistException if the requested {@link FileSystemObject} does not exist
     * @throws IOException                           if a problem occurred while reading the file
     * @throws NotAFileException                     if the id does not correspond to a {@link File}
     */
    ResponseEntity<ByteArrayResource> getFileResponseEntityBySecretKeyAndId(String secretKey, long id) throws AccountDoesNotExistsException, FileSystemObjectDoesNotExistException, IOException, NotAFileException;
}
