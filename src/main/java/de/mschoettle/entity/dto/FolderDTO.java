package de.mschoettle.entity.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.LocalDateTime;
import java.util.List;

@JsonTypeName("folder")
public class FolderDTO extends FileSystemObjectDTO {

    private List<FileSystemObjectDTO> contents;

    public FolderDTO() {
    }

    public FolderDTO(long id, String name, LocalDateTime creationDate, long fileSize, List<FileSystemObjectDTO> contents) {
        super(id, name, creationDate, fileSize);
        this.contents = contents;
    }

    public List<FileSystemObjectDTO> getContents() {
        return contents;
    }

    public void setContents(List<FileSystemObjectDTO> contents) {
        this.contents = contents;
    }
}
