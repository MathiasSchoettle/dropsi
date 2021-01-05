package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IAccountService extends UserDetailsService {

    boolean isEmailTaken(String email);

    boolean isUsernameTaken(String username);

    void saveAccount(Account account);

    void createNewAccount(Account account);

    void deleteAccount(Account account);

    Account getAccount(long id);

    List<Account> getAllAccounts();
}
