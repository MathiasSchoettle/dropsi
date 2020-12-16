package de.mschoettle.entity;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ref_type")
public abstract class FileSystemObject {

    @Id
    @GeneratedValue
    private long id = 0;

    private String name = "";

    private LocalDateTime creationDate = LocalDateTime.MIN;

    private LocalDateTime lastUpdateDate = LocalDateTime.MIN;

    private long fileSize = 0;

    @ManyToOne
    private Account owner = null;

    @ManyToOne(fetch = FetchType.EAGER)
    private Folder parent = null;

    @OneToMany(mappedBy = "reference")
    private List<AccessLogEntry> accessLogs = new ArrayList<>();

    @OneToMany(mappedBy = "shared")
    private List<Permission> permissions = new ArrayList<>();

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyy");

    public FileSystemObject(){}

    public FileSystemObject(String name, long fileSize, Account owner, Folder parent) {
        this.name = name;
        this.creationDate = LocalDateTime.now();
        this.lastUpdateDate = this.creationDate;
        this.fileSize = fileSize;
        this.owner = owner;
        this.parent = parent;
        this.accessLogs = new ArrayList<>();
        this.permissions = new ArrayList<>();
    }

    // TODO das hier ist mal wieder absoluter schmu, aber ich weiß ned wies anders geht weil ich thymeleaf ned kann lul
    public boolean isFolder() {
        return this instanceof Folder;
    }

    public String getPrettyCreationDate() {
        return dtf.format(this.creationDate);
    }

    public String getPrettyLastUpdateDate() {
        return dtf.format(this.creationDate);
    }

    // TODO rethink this
    public String getPrettyFileSize() {

        StringBuilder sb = new StringBuilder();
        DecimalFormat df2 = new DecimalFormat("0.00");

        if(fileSize > 1073741824d) {
            sb.append(df2.format((double) fileSize / 1073741824d));
            sb.append(" GB");
        }
        else if(fileSize > 1048576d) {
            sb.append(df2.format((double) fileSize / 1048576d));
            sb.append(" MB");
        }
        else if(fileSize > 1024d) {
            sb.append(df2.format((double) fileSize / 1024d));
            sb.append(" KB");
        }
        else {
            sb.append(df2.format((double) fileSize));
            sb.append(" Byte");
        }

        return sb.toString();
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getLastUpdateDate() {
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

    public void setId(long id) {
        this.id = id;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }
}
