package de.mschoettle.control.service.impl;

import de.mschoettle.control.service.IFileService;
import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Qualifier("server")
public class ServerFileService implements IFileService {

    @Override
    public void writeFile(File file) throws IOException {
        writeFile(getByteArrayOfFile(file), file.getFileReference());
    }

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

    @Override
    public byte[] getByteArrayOfFolder(Folder folder) throws IOException {

        Path path = Paths.get(System.getProperty("user.dir"), "temp", UUID.randomUUID().toString() + ".zip");

        URI p = path.toUri();
        URI uri = URI.create("jar:" + p);

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            addFolder(zipfs, folder, "/");
        }

        byte[] data = Files.readAllBytes(path);
        Files.delete(path);

        return data;
    }

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
