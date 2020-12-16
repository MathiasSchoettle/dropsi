package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface IInternalFileSystemService extends IFileSystemService {

    void saveFileSystemObject(FileSystemObject fileSystemObject);

    void deleteFileSystemObject(Account account, long parentFolderId, long fileSystemObjectId);

    void giveAccountRootFolder(Account account);

    void addNewFolderToFolder(Account account, long parentFolderId, String childFolderName);

    void addFileToFolder(Account account, long parentFolderId, MultipartFile[] files);

    Optional<FileSystemObject> getFileSystemObjectById(long id, Account account);

    void recalculateSize(Folder folder);
}
