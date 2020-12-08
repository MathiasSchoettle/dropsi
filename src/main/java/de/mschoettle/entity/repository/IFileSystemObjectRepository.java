package de.mschoettle.entity.repository;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IFileSystemObjectRepository extends CrudRepository<FileSystemObject, Long> {
    Optional<FileSystemObject> findByIdAndOwner(long id, Account account);
}
