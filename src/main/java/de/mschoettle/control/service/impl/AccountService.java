package de.mschoettle.control.service.impl;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.repository.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@Qualifier("account")
public class AccountService implements IAccountService {

    @Autowired
    private InternalFileSystemService fileSystemService;

    @Autowired
    private IAccountRepository accountRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void createNewUser(Account account) {
        if(account.getName() == null ||
                account.getName().trim().isEmpty() ||
                account.getEmail() == null ||
                account.getEmail().trim().isEmpty() ||
                account.getHashedPassword() == null ||
                account.getHashedPassword().isEmpty()) {

            // TODO is it ok to print a error message with password if it's empty or null?
            throw new IllegalArgumentException("One or more of the following parameters where empty or null: name:" + account.getName() +
                    ", email: " + account.getEmail() +
                    ", password: " + account.getHashedPassword());
        }

        if(isUsernameTaken(account.getName()) || isEmailTaken(account.getEmail())) {
            throw new IllegalArgumentException("Username or Email was already taken: username: " + account.getName() +
                    ", email: " + account.getEmail());
        }

        // TODO geht das hier auch ohne 2 mal account speichern
        account.setCreationDate(LocalDate.now());
        hashPasswordOfAccount(account);
        accountRepo.save(account);

        fileSystemService.giveAccountRootFolder(account);
        accountRepo.save(account);
    }

    private void hashPasswordOfAccount(Account account) {
        account.setHashedPassword(passwordEncoder.encode(account.getHashedPassword()));
    }

    @Override
    public boolean isEmailTaken(String email) {

        if (email == null)
            throw new IllegalArgumentException("Email can not be null");

        return accountRepo.findByEmail(email).isPresent();
    }

    @Override
    public boolean isUsernameTaken(String username) {

        if (username == null)
            throw new IllegalArgumentException("Username can not be null");

        return accountRepo.findByName(username).isPresent();
    }

    @Override
    public void saveAccount(Account account) {
        accountRepo.save(account);
    }

    @Override
    public void deleteAccount(Account account) {
        accountRepo.delete(account);
    }

    @Override
    public Account getLoggedInAccount() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return this.loadUserByUsername(username);
    }

    @Override
    public Account loadUserByUsername(String s) throws UsernameNotFoundException {
        return accountRepo.findByName(s).orElseThrow(() -> new UsernameNotFoundException("No account found with name: " + s));
    }
}
