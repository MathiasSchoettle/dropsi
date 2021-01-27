package de.mschoettle.control.service.impl;


import de.mschoettle.control.service.IFileService;
import de.mschoettle.entity.File;
import de.mschoettle.entity.Folder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * This is an implementation of the fileService which would be used if an FTP server was set up instead of saving the files to the local fileSystem
 */
@Service
@Qualifier("ftp")
@Scope("singleton")
public class FtpFileService implements IFileService {

    @Override
    public void writeFile(byte[] data, String fileRef) throws IOException {
        // implement
    }

    @Override
    public void deleteFile(File file) {
        // implement
    }

    @Override
    public byte[] getByteArrayOfFile(File file) throws IOException {
        // implement
        return new byte[0];
    }

    @Override
    public byte[] getByteArrayOfFolder(Folder folder) throws IOException {
        // implement
        return new byte[0];
    }

    @Override
    public String getStringOfFile(File file) throws IOException {
        // implement
        return "";
    }
}
