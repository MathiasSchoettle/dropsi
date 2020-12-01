package de.mschoettle.entity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class File extends FileSystemObject {

    private String fileType;

    private String fileReference;

    public File(){}

    @Override
    public void move() {
        // TODO
    }

    @Override
    public void copy() {
        // TODO
    }

    @Override
    public void delete() {
        // TODO
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileReference() {
        return fileReference;
    }
}
