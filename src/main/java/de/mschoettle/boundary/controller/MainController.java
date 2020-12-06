package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.i.IAccountService;
import de.mschoettle.control.service.i.IInternalFileSystemService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope("session")
public class MainController {

    @Autowired
    private IInternalFileSystemService internalFileSystemService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IInternalFileSystemService fileSystemService;

    @RequestMapping(value = "/main")
    public String showMain(Model model) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Account account = (Account) accountService.loadUserByUsername(username);
        model.addAttribute("account", account);
        model.addAttribute("currentFolder", account.getRootFolder());
        return "main";
    }

    @RequestMapping(value = "/folder/{folderId}")
    public String changeCurrentFolder(Model model, @PathVariable("folderId") long folderId) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Account account = (Account) accountService.loadUserByUsername(username);
        model.addAttribute("account", account);

        Folder newFolder = (Folder) fileSystemService.getFileSystemObjectById(folderId, account);
        model.addAttribute("currentFolder", newFolder);
        return "main";
    }
}
