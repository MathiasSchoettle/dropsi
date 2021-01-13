package de.mschoettle.control.exception;

public class PermissionDoesNotExistException extends Exception {

    public PermissionDoesNotExistException() {
        super("Permission does not exist");
    }

    public PermissionDoesNotExistException(long id) {
        super("No permission found with id " + id);
    }
}
