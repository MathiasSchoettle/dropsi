package de.mschoettle.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Permission {

    @Id
    @GeneratedValue
    private long id;

    private LocalDateTime creationDate;

    @ManyToOne
    private Account receiver;

    @ManyToOne
    private Account provider;

    @ManyToOne
    private FileSystemObject shared;

    public Permission() {
    }

    public Permission(Account receiver, Account provider, FileSystemObject shared) {
        this.receiver = receiver;
        this.provider = provider;
        this.shared = shared;
        this.creationDate = LocalDateTime.now();
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

    public Account getReceiver() {
        return receiver;
    }

    public Account getProvider() {
        return provider;
    }

    public FileSystemObject getShared() {
        return shared;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public void setProvider(Account provider) {
        this.provider = provider;
    }

    public void setShared(FileSystemObject shared) {
        this.shared = shared;
    }
}
