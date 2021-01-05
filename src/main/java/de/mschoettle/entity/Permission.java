package de.mschoettle.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Permission {

    @Id
    @GeneratedValue
    private long id;

    private LocalDateTime creationDate;

    private LocalDateTime expirationDate;

    @ManyToOne
    private Account receiver;

    @ManyToOne
    private FileSystemObject shared;

    public Permission() {}

    public Permission(Account receiver, FileSystemObject shared) {
        this.receiver = receiver;
        this.shared = shared;
        this.creationDate = LocalDateTime.now();

        // TODO either remove this or make it have some kind of meaning (if there is time)
    }

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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public Account getReceiver() {
        return receiver;
    }

    public FileSystemObject getShared() {
        return shared;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public void setShared(FileSystemObject shared) {
        this.shared = shared;
    }
}
