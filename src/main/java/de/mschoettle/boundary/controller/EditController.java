package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IInternalFileSystemService;
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

@Controller
@Scope("session")
public class EditController {

    @Autowired
    private IInternalFileSystemService fileSystemService;

    @Autowired
    private MainController mainController;

    @RequestMapping(value = {"/editInfo"}, method = RequestMethod.GET)
    public String showFolder(Model model, Principal principal, @RequestParam("fileSystemObjectId") long fileSystemObjectId) {
        Account account = mainController.getAuthenticatedAccount(principal);
        FileSystemObject fileSystemObject = fileSystemService.getFileSystemObject(account, fileSystemObjectId);
        model.addAttribute("fileSystemObject", fileSystemObject);
        return "editInfo";
    }

    @RequestMapping(value = {"/editInfo"}, method = RequestMethod.PUT)
    public String changeFolderName(Model model, Principal principal, @RequestParam("fileSystemObjectId") long fileSystemObjectId, @ModelAttribute("folderName") String fileSystemObjectName) {
        Account account = mainController.getAuthenticatedAccount(principal);
        FileSystemObject fileSystemObject = fileSystemService.getFileSystemObject(account, fileSystemObjectId);
        fileSystemService.changeNameOfFileSystemObject(fileSystemObjectId, account, fileSystemObjectName);
        model.addAttribute("fileSystemObject", fileSystemObject);
        return "editInfo";
    }
}