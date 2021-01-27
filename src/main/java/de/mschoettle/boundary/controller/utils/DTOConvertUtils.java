package de.mschoettle.boundary.controller.utils;

import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import de.mschoettle.entity.dto.FileDTO;
import de.mschoettle.entity.dto.FileSystemObjectDTO;
import de.mschoettle.entity.dto.FolderDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for folder and file objects to the corresponding DTOs
 */
public class DTOConvertUtils {

    private DTOConvertUtils() {
        throw new UnsupportedOperationException(this.getClass().getName() + " can not be instantiated");
    }

    public static FolderDTO castFolder(Folder folder) {

        List<FileSystemObjectDTO> contents = new ArrayList<>();

        for (FileSystemObject f : folder.getContents()) {
            if (f instanceof Folder) {
                contents.add(castFolder((Folder) f));
            } else if (f instanceof File) {
                contents.add(castFile((File) f));
            }
        }

        return new FolderDTO(folder.getId(), folder.getName(), folder.getCreationDate(), folder.getFileSize(), contents);
    }

    public static FileDTO castFile(File file) {
        return new FileDTO(file.getId(), file.getName(), file.getCreationDate(), file.getFileSize(), file.getFileType(), file.getFileExtension());
    }
}

