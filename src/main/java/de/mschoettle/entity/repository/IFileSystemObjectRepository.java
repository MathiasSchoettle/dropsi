package de.mschoettle.entity.repository;

import de.mschoettle.entity.FileSystemObject;
import org.springframework.data.repository.CrudRepository;

public interface IFileSystemObjectRepository extends CrudRepository<FileSystemObject, Long> {
}
