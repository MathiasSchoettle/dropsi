package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("session")
public class ShareController {

    @Autowired
    private IInternalFileSystemService fileSystemService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private MainController mainController;

    private List<Account> accountList = new ArrayList<>();

    @RequestMapping(value = "/share", method = RequestMethod.GET)
    public String showShareView(Model model, Principal principal, @RequestParam("fileSystemObjectId") long fileSystemObjectId) {

        accountList = new ArrayList<>();
        model.addAttribute("accountList", accountList);

        addAccountsAndFileSystemObjectToModel(model, principal, fileSystemObjectId);

        return "share";
    }

    @RequestMapping(value = "/share", method = RequestMethod.PATCH)
    public String addAccount(Model model, Principal principal, @RequestParam("fileSystemObjectId") long fileSystemObjectId,  @RequestParam("accountId") long accountId) {

        Account accountToAdd = accountService.getAccount(accountId);

        if(!accountList.contains(accountToAdd)) {
            accountList.add(accountToAdd);
        }

        addAccountsAndFileSystemObjectToModel(model, principal, fileSystemObjectId);

        return "share";
    }

    @RequestMapping(value = "/share", method = RequestMethod.DELETE)
    public String removeAccount(Model model, Principal principal, @RequestParam("fileSystemObjectId") long fileSystemObjectId,  @RequestParam("accountId") long accountId) {

        Account accountToRemove = accountService.getAccount(accountId);
        accountList.remove(accountToRemove);

        addAccountsAndFileSystemObjectToModel(model, principal, fileSystemObjectId);

        return "share";
    }

    @RequestMapping(value = "/share", method = RequestMethod.POST)
    public String givePermissions(Model model, Principal principal, @RequestParam("fileSystemObjectId") long fileSystemObjectId,  @RequestParam("folderId") long folderId) {

        mainController.addFolderToModel(model, principal, folderId);
        FileSystemObject fileSystemObject = fileSystemService.getFileSystemObject(mainController.getAuthenticatedAccount(principal), fileSystemObjectId);

        for(Account a : accountList) {
            permissionService.giveAccountPermission(a, fileSystemObject);
        }

        return "main";
    }

    private void addAccountsAndFileSystemObjectToModel(Model model, Principal principal, long fileSystemObjectId) {
        Account account = mainController.getAuthenticatedAccount(principal);
        FileSystemObject fileSystemObject = fileSystemService.getFileSystemObject(account, fileSystemObjectId);
        model.addAttribute("fileSystemObject", fileSystemObject);

        List<Account> accounts = accountService.getAllAccounts();
        accounts.remove(account);
        accounts.removeAll(accountList);

        model.addAttribute("accounts", accounts);
    }

    @ModelAttribute
    public List<Account> getAccountList() {
        return accountList;
    }
}
