package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

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
        Account account = accountService.getLoggedInAccount();
        model.addAttribute("account", account);
        model.addAttribute("currentFolder", account.getRootFolder());
        return "main";
    }

    @RequestMapping(value = "/folder/{folderId}")
    public String changeCurrentFolder(Model model, @PathVariable("folderId") long folderId) {
        Account account = accountService.getLoggedInAccount();
        model.addAttribute("account", account);

        Optional<FileSystemObject> newFolderOptional = fileSystemService.getFileSystemObjectById(folderId, account);

        if(newFolderOptional.isPresent()) {
            Folder newFolder = (Folder) newFolderOptional.get();
            model.addAttribute("currentFolder", newFolder);
        }
        else {
            // TODO make this a log and redirect to root? exceptions can be thrown when user inputs url himself
            throw new IllegalArgumentException("Folder with id " + folderId + " does not exist for account: " + account.getId());
        }

        return "main";
    }
}
