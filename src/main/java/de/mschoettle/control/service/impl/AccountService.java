package de.mschoettle.control.service.impl;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.Folder;
import de.mschoettle.entity.repository.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@Qualifier("account")
public class AccountService implements IAccountService {

    @Autowired
    private IInternalFileSystemService fileSystemService;

    @Autowired
    private IAccountRepository accountRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void createNewAccount(Account account) {
        if(account.getName() == null ||
                account.getName().trim().isEmpty() ||
                account.getEmail() == null ||
                account.getEmail().trim().isEmpty() ||
                account.getHashedPassword() == null ||
                account.getHashedPassword().isEmpty()) {

            throw new IllegalArgumentException("One or more of the following parameters where empty or null: name:" + account.getName() +
                    ", email: " + account.getEmail() +
                    ", password: " + "* * *");
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
    }

    private void hashPasswordOfAccount(Account account) {
        account.setHashedPassword(passwordEncoder.encode(account.getHashedPassword()));
    }

    @Override
    public boolean isEmailTaken(String email) {

        if (email == null)
            throw new IllegalArgumentException("E-Mail can not be null");

        return accountRepo.findByEmail(email).isPresent();
    }

    @Override
    public boolean isUsernameTaken(String username) {

        if (username == null)
            throw new IllegalArgumentException("Username can not be null");

        return accountRepo.findByName(username).isPresent();
    }

    @Override
    public Account getAccount(long id) {
        return accountRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Account with given id does not exist"));
    }

    @Override
    public List<Account> getAllAccounts() {
        return (List<Account>) accountRepo.findAll();
    }

    @Override
    public void saveAccount(Account account) {

        if(account == null) {
            throw new IllegalArgumentException("Account can not be null");
        }

        accountRepo.save(account);
    }

    @Override
    public void deleteAccount(Account account) {

        if(account == null) {
            throw new IllegalArgumentException("Account can not be null");
        }

        accountRepo.delete(account);
    }

    @Override
    public Account loadUserByUsername(String s) throws UsernameNotFoundException {

        if(s == null) {
            throw new IllegalArgumentException("Name can not be null");
        }

        return accountRepo.findByName(s).orElseThrow(() -> new UsernameNotFoundException("No account found with name: " + s));
    }
}
