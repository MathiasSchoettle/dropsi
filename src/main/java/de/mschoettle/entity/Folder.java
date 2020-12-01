package de.mschoettle.entity;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Folder extends FileSystemObject {

    @OneToMany(mappedBy = "parent")
    private List<FileSystemObject> contents;

    public Folder(){}

    @Override
    public void move() {

    }

    @Override
    public void copy() {

    }

    @Override
    public void delete() {

    }

    public List<FileSystemObject> getContents() {
        return Collections.unmodifiableList(contents);
    }
}
