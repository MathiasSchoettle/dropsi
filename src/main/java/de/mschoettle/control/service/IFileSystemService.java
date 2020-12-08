package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;

import java.util.Optional;

public interface IFileSystemService {
    Optional<FileSystemObject> getFileSystemObjectById(long id, Account account);
}
