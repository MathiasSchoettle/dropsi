package de.mschoettle.entity.pojo;

import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;

import java.time.LocalDateTime;
import java.util.List;

public class FolderPojo extends FileSystemObjectPojo {

    private List<FileSystemObjectPojo> contents;

    public FolderPojo(long id, String name, LocalDateTime creationDate, long fileSize, List<FileSystemObjectPojo> contents) {
        super(id, name, creationDate, fileSize);
        this.contents = contents;
    }

    public List<FileSystemObjectPojo> getContents() {
        return contents;
    }

    public void setContents(List<FileSystemObjectPojo> contents) {
        this.contents = contents;
    }
}
