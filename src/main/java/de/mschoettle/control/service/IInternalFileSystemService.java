package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;

public interface IInternalFileSystemService extends IFileSystemService {

    public void saveFileSystemObject(FileSystemObject fileSystemObject);

    public void giveAccountRootFolder(Account account);

    public void addNewEmptyFolderToFolder(Account account, Folder parentFolder, String childFolderName);
}
