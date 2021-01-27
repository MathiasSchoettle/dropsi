package de.mschoettle.control.service.impl;

import de.mschoettle.control.service.IFileService;
import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Qualifier("local")
@Scope("singleton")
public class LocalFileService implements IFileService {

    private static final Map<String, String> env = new HashMap<>() {{
        put("create", "true");
    }};

    @Override
    public void writeFile(byte[] data, String fileRef) throws IOException {
        Files.write(Paths.get(System.getProperty("user.dir"), "files", fileRef), data, StandardOpenOption.CREATE);
    }

    @Override
    public void deleteFile(File file) throws IOException {
        Files.deleteIfExists(Paths.get(System.getProperty("user.dir"), "files", file.getFileReference()));
    }

    @Override
    public byte[] getByteArrayOfFile(File file) throws IOException {
        return Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "files", file.getFileReference()));
    }

    /**
     * As we don't save the real hierarchy of the filesystem and only store the files on the filesystem the folder hierarchy has to be built before we generate the .zip file.
     * We generate a zip archive into which we add the folders and files, get the byte array of the .zip file and finally delete the directory in the end.
     */
    @Override
    public byte[] getByteArrayOfFolder(Folder folder) throws IOException {

        Path path = Paths.get(System.getProperty("user.dir"), "temp", UUID.randomUUID().toString() + ".zip");

        URI p = path.toUri();
        URI uri = URI.create("jar:" + p);

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            addFolder(zipfs, folder, "/");
        }

        byte[] data = Files.readAllBytes(path);
        Files.delete(path);

        return data;
    }

    /**
     * This recursive function builds the hierarchy
     */
    private void addFolder(FileSystem fs, Folder folder, String path) throws IOException {

        Files.createDirectory(fs.getPath(path, folder.getName()));

        for (FileSystemObject f : folder.getContents()) {

            if (f instanceof Folder) {
                addFolder(fs, (Folder) f, path + "/" + folder.getName());
            } else if (f instanceof File) {
                Files.write(fs.getPath(path, folder.getName(), f.toString()), getByteArrayOfFile((File) f));
            }
        }
    }

    @Override
    public String getStringOfFile(File file) throws IOException {
        return Files.readString(Path.of(System.getProperty("user.dir"), "files", file.getFileReference()));
    }
}
