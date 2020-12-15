package de.mschoettle.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class File extends FileSystemObject {

    private String fileType;

    private String fileReference;

    public File(){}

    public File(String name, long fileSize, Account owner, Folder parent, String fileType){
        super(name, fileSize, owner, parent);
        this.fileType = fileType;
    }

    @Override
    public String toString() {
        return getName() + "." + this.fileType;
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
