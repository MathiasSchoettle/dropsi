package de.mschoettle.control.service;

import de.mschoettle.entity.File;
import de.mschoettle.entity.Folder;

import java.io.IOException;

public interface IFileService {

    /**
     * Writes a new File to the filesystem with the given data and names it after the fileref
     *
     * @param data    the file data
     * @param fileRef the {@link File} reference
     * @throws IOException when a problem occurs while writing
     */
    void writeFile(byte[] data, String fileRef) throws IOException;

    /**
     * Deletes the file which is referenced in the given {@link File}
     *
     * @param file the file which corresponds to the to be deleted file on the filesystem
     * @throws IOException when a problem occurs while deleting
     */
    void deleteFile(File file) throws IOException;

    /**
     * @param file The corresponding {@link File}
     * @return the byte Array of the file on the filesystem which corresponds to the given {@link File}
     * @throws IOException when a problem occurs while reading
     */
    byte[] getByteArrayOfFile(File file) throws IOException;

    /**
     * This method returns a zip representation of the requested Folder.
     *
     * @param folder The corresponding {@link Folder}
     * @return the byte Array of the file on the filesystem which corresponds to the given {@link Folder}
     * @throws IOException when a problem occurs while reading
     */
    byte[] getByteArrayOfFolder(Folder folder) throws IOException;

    /**
     * @param file The corresponding {@link File}
     * @return returns a String representation of the data which corresponds to the given {@link File}
     * @throws IOException when a problem occurs while reading
     */
    String getStringOfFile(File file) throws IOException;
}
