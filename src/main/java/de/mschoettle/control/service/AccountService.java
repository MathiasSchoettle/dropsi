package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.repository.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Qualifier("account")
public class AccountService implements UserDetailsService {

    @Autowired
    private IAccountRepository accountRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void createNewUser(Account account) {
        if(account.getName() == null ||
                account.getName().trim().isEmpty() ||
                account.getEmail() == null ||
                account.getEmail().trim().isEmpty() ||
                account.getHashedPassword() == null ||
                account.getHashedPassword().isEmpty()) {

            // TODO is it ok to print a error message with password if its sure its empty or null?
            throw new IllegalArgumentException("One or more of the following parameters where empty or null: name:" + account.getName() +
                    ", email: " + account.getEmail() +
                    ", password: " + account.getHashedPassword());
        }

        if(isUsernameTaken(account.getName()) || isEmailTaken(account.getEmail())) {
            throw new IllegalArgumentException("Username or Email was already taken: username: " + account.getName() +
                    ", email: " + account.getEmail());
        }

        // TODO add root folder with FileSystemService
        account.setCreationDate(LocalDate.now());
        hashPasswordOfAccount(account);
    }

    private void hashPasswordOfAccount(Account account) {
        account.setHashedPassword(passwordEncoder.encode(account.getHashedPassword()));
        accountRepo.save(account);
    }

    public boolean isEmailTaken(String email) {

        if (email == null)
            throw new IllegalArgumentException("Email can not be null");

        return accountRepo.findByEmail(email) != null;
    }

    public boolean isUsernameTaken(String username) {

        if (username == null)
            throw new IllegalArgumentException("Username can not be null");

        return accountRepo.findByName(username) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Account account = accountRepo.findByName(s);
        if(account == null) {
            throw new UsernameNotFoundException("No account found with name: " + s);
        }
        return account;
    }
}
