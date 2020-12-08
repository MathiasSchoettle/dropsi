package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;

public interface IExternalFileSystemService extends IFileSystemService {

    public void uploadFile(FileSystemObject file, Account account);

    public FileSystemObject getRootFolder(Account account);
}
