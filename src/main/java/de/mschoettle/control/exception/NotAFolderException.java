package de.mschoettle.control.exception;

public class NotAFolderException extends Exception {

    public NotAFolderException(long id) {
        super("FileSystemObject with id " + id + " is not a Folder");
    }
}
