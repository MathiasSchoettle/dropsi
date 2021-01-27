package de.mschoettle.control.service;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.AccountNameTakenException;
import de.mschoettle.control.exception.EmailTakenException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Permission;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.security.auth.login.CredentialException;
import java.security.Principal;
import java.util.List;

public interface IAccountService extends UserDetailsService {

    /**
     * @param email The given email {@link String}
     * @return true if the email is already linked to another {@link Account}
     */
    boolean isEmailTaken(String email);

    /**
     * @param username The given username {@link String}
     * @return true if the username is already linked to another {@link Account}
     */
    boolean isUsernameTaken(String username);

    /**
     * @param account the to be saved {@link Account}
     */
    void saveAccount(Account account);

    /**
     * @param account The {@link Account} to be created
     * @throws CredentialException       if the given Email, password or name where empty or null
     * @throws AccountNameTakenException if the given Email is already in use. Check with isUsernameTaken first
     * @throws EmailTakenException       if the given Email is already in use. Check with isEmailTaken first
     */
    void createNewAccount(Account account) throws AccountDoesNotExistsException, FileSystemObjectDoesNotExistException, CredentialException, AccountNameTakenException, EmailTakenException;

    /**
     * @param account The {@link Account} to be deleted
     */
    void deleteAccount(Account account);

    /**
     * Checks if the given {@link Account} has a {@link Permission} to access the given {@link FileSystemObject}
     *
     * @param account          the {@link Account} to check for
     * @param fileSystemObject The given {@link FileSystemObject}
     * @return True if the {@link Account} has permission. False otherwise.
     */
    boolean accountHasPermission(Account account, FileSystemObject fileSystemObject);

    /**
     * @return the {@link Account} with the given id
     * @throws AccountDoesNotExistsException if no {@link Account} exists with the given id
     */
    Account getAccount(long id) throws AccountDoesNotExistsException;

    /**
     * @return the {@link Account} with the given secretKey
     * @throws AccountDoesNotExistsException if no {@link Account} exists with the given secretKey
     */
    Account getAccountBySecretKey(String token) throws AccountDoesNotExistsException;

    /**
     * @return a {@link List} of all registered {@link Account}s
     */
    List<Account> getAllAccounts();

    /**
     * Handy method to avoid boilerplate code
     */
    Account getAuthenticatedAccount(Principal principal);
}
