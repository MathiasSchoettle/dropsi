package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


/**
 * This controller needs to have scope session as the list "accountList" is different for every session
 */
@Controller
@Scope("session")
public class ShareController {

    private IFileSystemObjectService fileSystemObjectService;

    private IAccountService accountService;

    private IPermissionService permissionService;

    private MainController mainController;

    private List<Account> accountList = new ArrayList<>();

    @Autowired
    public void setInjectedBean(IFileSystemObjectService fileSystemObjectService,
                                IAccountService accountService,
                                IPermissionService permissionService,
                                MainController mainController) {

        this.fileSystemObjectService = fileSystemObjectService;
        this.accountService = accountService;
        this.permissionService = permissionService;
        this.mainController = mainController;
    }

    @RequestMapping(value = "/share", method = RequestMethod.GET)
    public String showShareView(Model model, Principal principal, @RequestParam("fileSystemObjectId") long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException {

        accountList = new ArrayList<>();
        model.addAttribute("accountList", accountList);

        addAccountsAndFileSystemObjectToModel(model, principal, fileSystemObjectId);

        return "share";
    }

    @RequestMapping(value = "/share", method = RequestMethod.PATCH)
    public String addAccount(Model model, Principal principal,
                             @RequestParam("fileSystemObjectId") long fileSystemObjectId,
                             @RequestParam("accountId") long accountId)
            throws AccountDoesNotExistsException,
            FileSystemObjectDoesNotExistException {

        Account accountToAdd = accountService.getAccount(accountId);

        if(!accountList.contains(accountToAdd)) {
            accountList.add(accountToAdd);
        }

        addAccountsAndFileSystemObjectToModel(model, principal, fileSystemObjectId);

        return "share";
    }

    @RequestMapping(value = "/share", method = RequestMethod.DELETE)
    public String removeAccount(Model model, Principal principal,
                                @RequestParam("fileSystemObjectId") long fileSystemObjectId,
                                @RequestParam("accountId") long accountId)
            throws AccountDoesNotExistsException,
            FileSystemObjectDoesNotExistException {

        Account accountToRemove = accountService.getAccount(accountId);
        accountList.remove(accountToRemove);

        addAccountsAndFileSystemObjectToModel(model, principal, fileSystemObjectId);

        return "share";
    }

    @RequestMapping(value = "/share", method = RequestMethod.POST)
    public String givePermissions(Model model, Principal principal,
                                  @RequestParam("fileSystemObjectId") long fileSystemObjectId,
                                  @RequestParam("folderId") long folderId)
            throws FileSystemObjectDoesNotExistException {

        mainController.addFolderToModel(model, principal, folderId);
        Account provider = accountService.getAuthenticatedAccount(principal);
        FileSystemObject fileSystemObject = fileSystemObjectService.getFileSystemObject(accountService.getAuthenticatedAccount(principal), fileSystemObjectId);

        for(Account a : accountList) {
            permissionService.giveAccountPermission(a, provider, fileSystemObject);
        }

        return "main";
    }

    private void addAccountsAndFileSystemObjectToModel(Model model, Principal principal, long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException {

        Account account = accountService.getAuthenticatedAccount(principal);
        FileSystemObject fileSystemObject = fileSystemObjectService.getFileSystemObject(account, fileSystemObjectId);
        model.addAttribute("fileSystemObject", fileSystemObject);

        List<Account> accounts = accountService.getAllAccounts();
        accounts.remove(account);
        accounts.removeAll(accountList);

        model.addAttribute("accounts", accounts);
    }

    @ExceptionHandler(FileSystemObjectDoesNotExistException.class)
    public String handleAccountDoesNotExistException() {
        return "redirect:/home";
    }

    @ModelAttribute
    public List<Account> getAccountList() {
        return accountList;
    }
}
