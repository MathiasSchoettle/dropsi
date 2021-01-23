package de.mschoettle.entity.repository;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Permission;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface IPermissionRepository extends CrudRepository<Permission, Long> {

    Optional<Permission> findById(long id);

    List<Permission> findAllByProvider(Account account);

    void deleteAllByShared(FileSystemObject fileSystemObject);
}
