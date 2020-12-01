package de.mschoettle.entity.repository;

import de.mschoettle.entity.AccessLogEntry;
import org.springframework.data.repository.CrudRepository;

public interface IAccessLogEntryRepository extends CrudRepository<AccessLogEntry, Long> {
}
