package de.mschoettle.entity.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.LocalDateTime;

@JsonTypeName("file")
public class FileDTO extends FileSystemObjectDTO {

    private String fileExtension;

    private String fileType;

    public FileDTO() {
    }

    public FileDTO(long id, String name, LocalDateTime creationDate, long fileSize, String fileType, String fileExtension) {
        super(id, name, creationDate, fileSize);
        this.fileType = fileType;
        this.fileExtension = fileExtension;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
