package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.NotAFolderException;
import de.mschoettle.control.service.IFileSystemService;
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
@Scope("session")
public class MoveController {

    @Autowired
    private MainController mainController;

    @Autowired
    private IFileSystemService fileSystemService;

    @RequestMapping(value = {"/move"}, method = RequestMethod.GET)
    public String showFolder(Model model, Principal principal,
                             @RequestParam("folderId") long folderId,
                             @RequestParam("fileSystemObjectId") long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        Account account = mainController.getAuthenticatedAccount(principal);
        model.addAttribute("folder", fileSystemService.getFolder(account, folderId));
        model.addAttribute("fileSystemObject", fileSystemService.getFileSystemObject(account, fileSystemObjectId));
        return "move";
    }

    @RequestMapping(value = "/filesystemobject", method = RequestMethod.PATCH)
    public String moveFileSystemObject(Model model, Principal principal,
                                       @RequestParam("folderId") long folderId,
                                       @RequestParam("fileSystemObjectId") long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        fileSystemService.moveFileSystemObject(mainController.getAuthenticatedAccount(principal), folderId, fileSystemObjectId);
        mainController.addFolderToModel(model, principal, folderId);
        return "main";
    }

    @ExceptionHandler(FileSystemObjectDoesNotExistException.class)
    public String handleFileSystemObjectDoesNotExistException(Model model, Principal principal) {
        return mainController.showMain(model, principal);
    }
}