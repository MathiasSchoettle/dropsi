package de.mschoettle.control.exception;

public class FileSystemObjectDoesNotExistException extends Exception {

    public FileSystemObjectDoesNotExistException() {
        super("FileSystemObject does not exist");
    }

    public FileSystemObjectDoesNotExistException(long id) {
        super("No fileSystemObject found with id " + id);
    }
}
