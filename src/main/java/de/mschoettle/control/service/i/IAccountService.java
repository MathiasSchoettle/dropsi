package de.mschoettle.control.service.i;

import de.mschoettle.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IAccountService extends UserDetailsService {

    public boolean isEmailTaken(String email);

    public boolean isUsernameTaken(String username);

    public void createNewUser(Account account);
}
