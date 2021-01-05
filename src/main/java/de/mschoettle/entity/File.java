package de.mschoettle.entity;

import javax.persistence.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class File extends FileSystemObject {

    private String fileExtension;

    private String fileType;

    private String fileReference;

    public File(){}

    public File(String name, long fileSize, Account owner, Folder parent, String fileExtension, String fileType){
        super(name, fileSize, owner, parent);
        this.fileExtension = fileExtension;
        this.fileType = fileType;
    }

    // TODO do this manually in a service method. Is it possible?
    @PreRemove
    public void preRemove() throws IOException {
        Files.deleteIfExists(Paths.get(System.getProperty("user.dir"), "files", fileReference));
    }

    @Override
    public String toString() {
        return getName() + "." + fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }
}
