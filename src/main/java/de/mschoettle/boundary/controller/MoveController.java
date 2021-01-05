package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@Scope("session")
public class MoveController {

    @Autowired
    private MainController mainController;

    @Autowired
    private IInternalFileSystemService fileSystemService;

    @RequestMapping(value = {"/move"}, method = RequestMethod.GET)
    public String showFolder(Model model, Principal principal, @RequestParam("folderId") long folderId, @RequestParam("fileSystemObjectId") long fileSystemObjectId) {
        Account account = mainController.getAuthenticatedAccount(principal);
        model.addAttribute("folder", fileSystemService.getFolder(account, folderId));
        model.addAttribute("fileSystemObject", fileSystemService.getFileSystemObject(account, fileSystemObjectId));
        return "move";
    }
}