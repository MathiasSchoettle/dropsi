package de.mschoettle.entity.pojo;

import de.mschoettle.entity.Folder;

import java.time.LocalDateTime;

public abstract class FileSystemObjectPojo {

    private long id = 0;

    private String name = "";

    private LocalDateTime creationDate = LocalDateTime.MIN;

    private long fileSize = 0;

    public FileSystemObjectPojo(long id, String name, LocalDateTime creationDate, long fileSize) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.fileSize = fileSize;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
