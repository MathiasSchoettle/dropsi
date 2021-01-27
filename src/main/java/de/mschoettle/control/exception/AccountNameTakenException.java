package de.mschoettle.control.exception;

public class AccountNameTakenException extends Exception {

    public AccountNameTakenException(String accountName) {
        super("Account name " + accountName + " is taken");
    }
}
