package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.File;

public class FileSystemService implements IFileSystemService, IExternalFileSystemService {

    @Override
    public void uploadFile(File file, Account account) {
        // TODO implement
    }

    @Override
    public long getFileId(Account account) {
        // TODO implement
        return 0;
    }

    @Override
    public File getFileById(long id, Account account) {
        // TODO implement
        return null;
    }
}
