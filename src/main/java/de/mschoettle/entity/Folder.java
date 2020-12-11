package de.mschoettle.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Folder extends FileSystemObject {

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileSystemObject> contents;

    public Folder(String name, long fileSize, Account owner, Folder parent) {
        super(name, fileSize, owner, parent);
        this.contents = new ArrayList<>();
    }

    public Folder(){}

    public void addFileSystemObject(FileSystemObject fileSystemObject) {
        if(fileSystemObject == null) {
            throw new IllegalArgumentException("FileSystemObject is null");
        }

        if(this.equals(fileSystemObject)) {
            throw new IllegalArgumentException("Folder and FileSystemObject are the same");
        }

        if(this.contents.contains(fileSystemObject)) {
            throw new IllegalArgumentException("folder already contains FileSystemObject: " + fileSystemObject);
        }

        this.contents.add(fileSystemObject);
    }

    @Override
    public void move() {
        // TODO implement
    }

    @Override
    public void copy() {
        // TODO implement
    }

    @Override
    public void delete() {
        // TODO implement
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(getName());
        sb.append("\n");

        for (FileSystemObject f : this.getContents()) {
            sb.append("└ ");
            sb.append(f.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    public List<FileSystemObject> getContents() {
        return Collections.unmodifiableList(contents);
    }
}
