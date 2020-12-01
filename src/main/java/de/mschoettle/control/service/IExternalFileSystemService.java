package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.File;

public interface IExternalFileSystemService {

    void uploadFile(File file, Account account);

    long getFileId(Account account);

    File getFileById(long id, Account account);
}
