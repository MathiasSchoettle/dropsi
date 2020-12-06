package de.mschoettle.control.service;

import de.mschoettle.control.service.i.IExternalFileSystemService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;

public class ExternalFileSystemService implements IExternalFileSystemService {

    @Override
    public void uploadFile(FileSystemObject file, Account account) {

    }

    @Override
    public FileSystemObject getRootFolder(Account account) {
        return null;
    }

    @Override
    public FileSystemObject getFileSystemObjectById(long id, Account account) {
        return null;
    }
}
