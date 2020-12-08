package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;

public interface IInternalFileSystemService extends IFileSystemService {

    public void saveFileSystemObject(FileSystemObject fileSystemObject);

    public void giveAccountRootFolder(Account account);
}
