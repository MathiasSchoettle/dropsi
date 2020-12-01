package de.mschoettle.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Permission {

    @Id
    private long id;

    private LocalDate creationDate;

    private LocalDate expirationDate;

    @ManyToOne
    private Account receiver;

    @ManyToOne
    private FileSystemObject shared;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission permission = (Permission) o;
        return this.id == permission.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public long getId() {
        return id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public Account getReceiver() {
        return receiver;
    }

    public FileSystemObject getShared() {
        return shared;
    }
}
