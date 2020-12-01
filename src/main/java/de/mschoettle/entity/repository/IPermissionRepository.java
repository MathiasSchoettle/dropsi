package de.mschoettle.entity.repository;

import de.mschoettle.entity.Permission;
import org.springframework.data.repository.CrudRepository;

public interface IPermissionRepository extends CrudRepository<Permission, Long> {
}
