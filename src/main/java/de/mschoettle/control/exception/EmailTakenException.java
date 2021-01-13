package de.mschoettle.control.exception;

public class EmailTakenException extends Exception {

    public EmailTakenException(String email) {
        super("Email " + email + " is taken");
    }
}
