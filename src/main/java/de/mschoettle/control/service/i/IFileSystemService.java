package de.mschoettle.control.service.i;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;

public interface IFileSystemService {
    public FileSystemObject getFileSystemObjectById(long id, Account account);
}
