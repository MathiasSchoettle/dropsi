package de.mschoettle.entity.pojo;

import de.mschoettle.entity.Folder;

import java.time.LocalDateTime;

public class FilePojo extends FileSystemObjectPojo{

    private String fileType;

    public FilePojo(long id, String name, LocalDateTime creationDate, long fileSize, String fileType) {
        super(id, name, creationDate, fileSize);
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
