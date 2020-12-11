package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IAccountService extends UserDetailsService {

    boolean isEmailTaken(String email);

    boolean isUsernameTaken(String username);

    void saveAccount(Account account);

    void createNewUser(Account account);

    void deleteAccount(Account account);
}
