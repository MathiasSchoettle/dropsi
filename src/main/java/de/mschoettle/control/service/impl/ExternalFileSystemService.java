package de.mschoettle.control.service.impl;

import de.mschoettle.control.service.IExternalFileSystemService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;

import java.util.Optional;

public class ExternalFileSystemService implements IExternalFileSystemService {

    @Override
    public void uploadFile(FileSystemObject file, Account account) {
        // TODO impl
    }

    @Override
    public FileSystemObject getRootFolder(Account account) {
        return null;
    }

    @Override
    public Optional<FileSystemObject> getFileSystemObjectById(long id, Account account) {
        return Optional.empty();
    }
}
