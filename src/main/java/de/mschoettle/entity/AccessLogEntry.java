package de.mschoettle.entity;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

@Entity
public class AccessLogEntry implements Comparable<AccessLogEntry> {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private FileSystemObject reference;

    private AccessType type;

    private String comment;

    private LocalDateTime creationDate;

    public AccessLogEntry(){}

    public AccessLogEntry(FileSystemObject reference, AccessType type, String comment) {
        this.reference = reference;
        this.type = type;
        this.comment = comment;
        this.creationDate = LocalDateTime.now();
    }

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

    @Override
    public int compareTo(AccessLogEntry accessLogEntry) {
        return accessLogEntry.getCreationDate().compareTo(creationDate);
    }

    @Override
    public String toString() {
        return "AccessLogEntry{" +
                "id=" + id +
                "reference=" + reference +
                ", type=" + type +
                ", comment='" + comment + '\'' +
                ", creationDate=" + creationDate +
                '}';
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

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
