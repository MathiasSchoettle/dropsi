package de.mschoettle.control.utils;

import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import de.mschoettle.entity.pojo.FilePojo;
import de.mschoettle.entity.pojo.FileSystemObjectPojo;
import de.mschoettle.entity.pojo.FolderPojo;

import java.util.ArrayList;
import java.util.List;

public class PojoConvertUtils {

    private PojoConvertUtils() {
        throw new UnsupportedOperationException(this.getClass().getName() + " can not be instantiated");
    }

    public static FolderPojo castFolder(Folder folder) {

        List<FileSystemObjectPojo> contents = new ArrayList<>();

        for(FileSystemObject f : folder.getContents()) {
            if(f instanceof Folder) {
                contents.add(castFolder((Folder) f));
            } else if(f instanceof File) {
                contents.add(castFile((File) f));
            }
        }

        return new FolderPojo(folder.getId(), folder.getName(), folder.getCreationDate(), folder.getFileSize(), contents);
    }

    public static FilePojo castFile(File file) {
        return new FilePojo(file.getId(), file.getName(), file.getCreationDate(), file.getFileSize(), file.getFileType());
    }
}

