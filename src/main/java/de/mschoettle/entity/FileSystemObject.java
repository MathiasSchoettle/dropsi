package de.mschoettle.entity;

import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ref_type")
public abstract class FileSystemObject {

    @Id
    @GeneratedValue
    private long id = 0;

    private String name = "";

    private LocalDateTime creationDate = LocalDateTime.MIN;

    private long fileSize = 0;

    @ManyToOne
    private Account owner = null;

    @ManyToOne(fetch = FetchType.EAGER)
    private Folder parent = null;

    @SortNatural
    @OrderBy("creationDate DESC")
    @OneToMany(mappedBy = "reference", cascade = CascadeType.REMOVE)
    private List<AccessLogEntry> accessLogs = new ArrayList<>();

    @OneToMany(mappedBy = "shared", cascade = CascadeType.REMOVE)
    private List<Permission> permissions = new ArrayList<>();

    public FileSystemObject() {
    }

    public FileSystemObject(String name, long fileSize, Account owner, Folder parent) {
        this.name = name;
        this.creationDate = LocalDateTime.now();
        this.fileSize = fileSize;
        this.owner = owner;
        this.parent = parent;
    }

    // this is used in thymeleaf to render the file and folder instances
    public boolean isFolder() {
        return this instanceof Folder;
    }

    // TODO remove or in service
    public Optional<Permission> removePermissionByAccount(Account account) {

        for (Permission p : permissions) {
            if (p.getReceiver().equals(account)) {
                permissions.remove(p);
                return Optional.of(p);
            }
        }

        return Optional.empty();
    }

    public String getPrettyFileSize() {

        StringBuilder sb = new StringBuilder();
        DecimalFormat df2 = new DecimalFormat("0.00");

        if (fileSize > 1073741824d) {
            sb.append(df2.format((double) fileSize / 1073741824d));
            sb.append(" GB");
        } else if (fileSize > 1048576d) {
            sb.append(df2.format((double) fileSize / 1048576d));
            sb.append(" MB");
        } else if (fileSize > 1024d) {
            sb.append(df2.format((double) fileSize / 1024d));
            sb.append(" KB");
        } else {
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

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
