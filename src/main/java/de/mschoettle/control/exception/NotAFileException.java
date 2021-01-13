package de.mschoettle.control.exception;

public class NotAFileException extends Exception {

    public NotAFileException(long id) {
        super("FileSystemObject with id " + id + " is not a File");
    }
}
