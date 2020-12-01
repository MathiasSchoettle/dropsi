package de.mschoettle.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
public abstract class FileSystemObject {

    @Id
    private long id;

    private String name;

    private LocalDate creationDate;

    private LocalDate lastUpdateDate;

    private long fileSize;

    @ManyToOne
    private Account owner;

    @ManyToOne
    private Folder parent;

    @OneToMany(mappedBy = "reference")
    private List<AccessLogEntry> accessLogs;

    @OneToMany(mappedBy = "shared")
    private List<Permission> permissions;

    public FileSystemObject(){}

    public abstract void move();

    public abstract void copy();

    public abstract void delete();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSystemObject fileSystemObject = (FileSystemObject) o;
        return this.id == fileSystemObject.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getLastUpdateDate() {
        return lastUpdateDate;
    }

    public long getFileSize() {
        return fileSize;
    }

    public Account getOwner() {
        return owner;
    }

    public Folder getParent() {
        return parent;
    }

    public List<Permission> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }

    public List<AccessLogEntry> getAccessLogs() {
        return Collections.unmodifiableList(accessLogs);
    }
}
