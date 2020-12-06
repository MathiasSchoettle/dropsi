package de.mschoettle.entity.repository;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import org.springframework.data.repository.CrudRepository;

public interface IFileSystemObjectRepository extends CrudRepository<FileSystemObject, Long> {
    FileSystemObject findByIdAndOwner(long id, Account account);
}
