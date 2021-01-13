package de.mschoettle.control.exception;

public class AccountDoesNotExistsException extends Exception {

    public AccountDoesNotExistsException() {
        super("Account does not exist");
    }

    public AccountDoesNotExistsException(long id) {
        super("No account found with id " + id);
    }

    public AccountDoesNotExistsException(String token) {
        super("No account found with token " + token);
    }
}
