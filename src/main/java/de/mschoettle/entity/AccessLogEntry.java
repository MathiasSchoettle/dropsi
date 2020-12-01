package de.mschoettle.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class AccessLogEntry {

    @Id
    private long id;

    @ManyToOne
    private FileSystemObject reference;

    private AccessType type;

    private long fileSize;

    private LocalDate creationDate;

    public AccessLogEntry(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessLogEntry accessLogEntry = (AccessLogEntry) o;
        return this.id == accessLogEntry.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public long getId() {
        return id;
    }

    public FileSystemObject getReference() {
        return reference;
    }

    public AccessType getType() {
        return type;
    }

    public long getFileSize() {
        return fileSize;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }
}
