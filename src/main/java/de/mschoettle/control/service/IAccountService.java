package de.mschoettle.control.service;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.AccountNameTakenException;
import de.mschoettle.control.exception.EmailTakenException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.security.auth.login.CredentialException;
import java.util.List;

public interface IAccountService extends UserDetailsService {

    boolean isEmailTaken(String email);

    boolean isUsernameTaken(String username);

    void saveAccount(Account account);

    void createNewAccount(Account account) throws AccountDoesNotExistsException, FileSystemObjectDoesNotExistException, CredentialException, AccountNameTakenException, EmailTakenException;

    void deleteAccount(Account account);

    Account getAccount(long id) throws AccountDoesNotExistsException;

    Account getAccountBySecretKey(String token) throws AccountDoesNotExistsException;

    List<Account> getAllAccounts();

    boolean accountWithSecretKeyExists(String secretKey);
}
