package de.mschoettle.entity.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use=JsonTypeInfo.Id.NAME,
        property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value= FolderDTO.class, name = "folder"),
        @JsonSubTypes.Type(value= FileDTO.class, name = "file")})

public abstract class FileSystemObjectDTO {

    private long id = 0;

    private String name = "";

    private LocalDateTime creationDate = LocalDateTime.MIN;

    private long fileSize = 0;

    public FileSystemObjectDTO() {
    }

    public FileSystemObjectDTO(long id, String name, LocalDateTime creationDate, long fileSize) {
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
