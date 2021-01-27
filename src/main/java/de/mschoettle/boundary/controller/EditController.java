package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;

@Controller
@Scope("singleton")
public class EditController {

    private IFileSystemObjectService fileSystemObjectService;

    private IAccountService accountService;

    @Autowired
    public void setInjectedBean(IFileSystemObjectService fileSystemObjectService,
                                IAccountService accountService) {

        this.fileSystemObjectService = fileSystemObjectService;
        this.accountService = accountService;
    }

    @RequestMapping(value = {"/info"}, method = RequestMethod.GET)
    public String showInformation(Model model, Principal principal, @RequestParam("fileSystemObjectId") long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException {

        Account account = accountService.getAuthenticatedAccount(principal);
        FileSystemObject fileSystemObject = fileSystemObjectService.getFileSystemObject(account, fileSystemObjectId);

        model.addAttribute("accessLogMap", fileSystemObjectService.getAccessLogEntriesMap(fileSystemObject));
        model.addAttribute("fileSystemObject", fileSystemObject);

        return "info";
    }

    @RequestMapping(value = {"/info"}, method = RequestMethod.PUT)
    @Transactional
    public String changeFileSystemObjectName(Model model, Principal principal,
                                   @RequestParam("fileSystemObjectId") long fileSystemObjectId,
                                   @ModelAttribute("fileSystemObjectName") String fileSystemObjectName) throws
            FileSystemObjectDoesNotExistException {

        Account account = accountService.getAuthenticatedAccount(principal);
        FileSystemObject fileSystemObject = fileSystemObjectService.getFileSystemObject(account, fileSystemObjectId);

        fileSystemObjectService.changeNameOfFileSystemObject(account, fileSystemObjectId, fileSystemObjectName);

        model.addAttribute("fileSystemObject", fileSystemObject);
        model.addAttribute("accessLogMap", fileSystemObjectService.getAccessLogEntriesMap(fileSystemObject));

        return "info";
    }

    @ExceptionHandler(FileSystemObjectDoesNotExistException.class)
    public String handleFileSystemObjectDoesNotExistException() {
        return "redirect:/home";
    }
}