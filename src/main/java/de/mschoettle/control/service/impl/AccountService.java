package de.mschoettle.control.service.impl;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.AccountNameTakenException;
import de.mschoettle.control.exception.EmailTakenException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Permission;
import de.mschoettle.entity.repository.IAccountRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.CredentialException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Scope("singleton")
public class AccountService implements IAccountService {

    private IFileSystemObjectService fileSystemService;

    private IAccountRepository accountRepo;

    private BCryptPasswordEncoder passwordEncoder;

    private Logger logger;

    @Autowired
    public void setInjectedBean(IFileSystemObjectService fileSystemObjectService,
                           IAccountRepository accountRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           Logger logger) {

        this.fileSystemService = fileSystemObjectService;
        this.accountRepo = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.logger = logger;
    }

    @Override
    @Transactional
    public void createNewAccount(Account account) throws
            AccountDoesNotExistsException,
            CredentialException,
            AccountNameTakenException,
            EmailTakenException {

        if (account.getName() == null ||
                account.getName().trim().isEmpty() ||
                account.getEmail() == null ||
                account.getEmail().trim().isEmpty() ||
                account.getHashedPassword() == null ||
                account.getHashedPassword().isEmpty()) {

            throw new CredentialException("One or more of the following parameters where empty or null: name:" + account.getName() +
                    ", email: " + account.getEmail() +
                    ", password: " + "* * *");
        }


        if (isUsernameTaken(account.getName())) {
            throw new AccountNameTakenException(account.getName());
        }

        if (isEmailTaken(account.getEmail())) {
            throw new EmailTakenException(account.getEmail());
        }

        account.setSecretKey(UUID.randomUUID().toString());
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

        if (email == null) {
            throw new IllegalArgumentException("E-Mail can not be null");
        }

        return accountRepo.findByEmail(email).isPresent();
    }

    @Override
    public boolean isUsernameTaken(String username) {

        if (username == null) {
            throw new IllegalArgumentException("Username can not be null");
        }

        return accountRepo.findByName(username).isPresent();
    }

    @Override
    public Account getAccount(long id) throws AccountDoesNotExistsException {
        return accountRepo.findById(id).orElseThrow(() -> new AccountDoesNotExistsException(id));
    }

    @Override
    public Account getAccountBySecretKey(String secretKey) throws AccountDoesNotExistsException {
        return accountRepo.findBySecretKey(secretKey).orElseThrow(
                () -> new AccountDoesNotExistsException(secretKey));
    }

    @Override
    public List<Account> getAllAccounts() {
        return (List<Account>) accountRepo.findAll();
    }

    @Override
    @Transactional
    public void saveAccount(Account account) {

        if (account == null) {
            throw new IllegalArgumentException("Account can not be null");
        }

        accountRepo.save(account);
        logger.info("saved : " + Account.class.getName() + " - " + account);
    }

    @Override
    @Transactional
    public void deleteAccount(Account account) {

        if (account == null) {
            throw new IllegalArgumentException("Account can not be null");
        }

        accountRepo.delete(account);
        logger.info("deleted : " + Account.class.getName() + " - " + account);
    }

    @Override
    public boolean accountHasPermission(Account account, FileSystemObject fileSystemObject) {
        for (Permission p : account.getPermissions()) {
            if (p.getShared().equals(fileSystemObject)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Account loadUserByUsername(String s) throws UsernameNotFoundException {

        if (s == null) {
            throw new IllegalArgumentException("Name can not be null");
        }

        return accountRepo.findByName(s).orElseThrow(() -> new UsernameNotFoundException("No account found with name: " + s));
    }

    @Override
    public Account getAuthenticatedAccount(Principal principal) {
        return loadUserByUsername(principal.getName());
    }
}
