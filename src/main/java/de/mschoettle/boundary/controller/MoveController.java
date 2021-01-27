package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.NotAFolderException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@Scope("singleton")
public class MoveController {

    private IFileSystemObjectService fileSystemObjectService;

    private IAccountService accountService;

    private MainController mainController;

    @Autowired
    public void setInjectedBean(IFileSystemObjectService fileSystemObjectService,
                                IAccountService accountService,
                                MainController mainController) {

        this.fileSystemObjectService = fileSystemObjectService;
        this.accountService = accountService;
        this.mainController = mainController;
    }

    @RequestMapping(value = {"/move"}, method = RequestMethod.GET)
    public String showFolder(Model model, Principal principal,
                             @RequestParam("folderId") long folderId,
                             @RequestParam("fileSystemObjectId") long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        Account account = accountService.getAuthenticatedAccount(principal);
        model.addAttribute("folder", fileSystemObjectService.getFolder(account, folderId));
        model.addAttribute("fileSystemObject", fileSystemObjectService.getFileSystemObject(account, fileSystemObjectId));
        return "move";
    }

    @RequestMapping(value = "/filesystemobject", method = RequestMethod.PATCH)
    public String moveFileSystemObject(Model model, Principal principal,
                                       @RequestParam("folderId") long folderId,
                                       @RequestParam("fileSystemObjectId") long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        fileSystemObjectService.moveFileSystemObject(accountService.getAuthenticatedAccount(principal), folderId, fileSystemObjectId);
        mainController.addFolderToModel(model, principal, folderId);
        return "main";
    }

    @ExceptionHandler({FileSystemObjectDoesNotExistException.class, NotAFolderException.class})
    public String handleFileSystemObjectDoesNotExistException() {
        return "redirect:/home";
    }
}