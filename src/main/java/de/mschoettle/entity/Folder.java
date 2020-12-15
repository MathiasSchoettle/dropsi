package de.mschoettle.entity;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Folder extends FileSystemObject {

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FileSystemObject> contents = new ArrayList<>();

    public Folder(String name, long fileSize, Account owner, Folder parent) {
        super(name, fileSize, owner, parent);
        this.contents = new ArrayList<>();
    }

    public Folder() {}

    public void addFileSystemObject(FileSystemObject fileSystemObject) {

        if (fileSystemObject == null) {
            throw new IllegalArgumentException("FileSystemObject is null");
        }

        if (this.equals(fileSystemObject)) {
            throw new IllegalArgumentException("Folder and FileSystemObject are the same");
        }

        if (this.contents.contains(fileSystemObject)) {
            throw new IllegalArgumentException("folder already contains FileSystemObject: " + fileSystemObject);
        }

        this.contents.add(fileSystemObject);
    }

    public boolean containsFileSystemObject(long fileSystemObjectId) {

        for(FileSystemObject f : contents) {
            if(f.getId() == fileSystemObjectId) {
                return true;
            }
        }

        return false;
    }

    public void removeFileSystemObject(long fileSystemObjectId) {

        for(FileSystemObject f : contents) {
            if(f.getId() == fileSystemObjectId) {
                boolean wasRemoved = contents.remove(f);
                System.out.println("removed fileSystemObject " + f.getName() + " " + f.getId() + " from folder " + this.getName() + ". Was removed = " + wasRemoved);
                return;
            }
        }

        System.out.println("nichts wurde deleted");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName() + ": " + getId());

        if(contents != null && !contents.isEmpty()) {
            for(FileSystemObject f : contents) {
                sb.append(f);
            }
        }

        return sb.toString();
    }

    public List<FileSystemObject> getContents() {
        return Collections.unmodifiableList(contents);
    }
}
