package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Optional;

@Controller
@Scope("session")
public class MainController {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IInternalFileSystemService fileSystemService;

    private Account authenticatedAccount;

    private Folder currentFolder;

    @RequestMapping(value = "/main")
    public String showMain(Model model, Principal principal) {
        authenticatedAccount = (Account) accountService.loadUserByUsername(principal.getName());
        currentFolder = authenticatedAccount.getRootFolder();
        model.addAttribute("currentFolder", currentFolder);
        return "main";
    }

    @RequestMapping(value = "/folder/{folderId}")
    public String changeCurrentFolder(Model model, @PathVariable("folderId") long folderId) {

        Optional<FileSystemObject> folderOptional = fileSystemService.getFileSystemObjectById(folderId, authenticatedAccount);

        // if folder doesn't exist show last folder
        currentFolder = (Folder) folderOptional.orElseGet(() -> currentFolder);
        model.addAttribute("currentFolder", currentFolder);
        return "main";
    }

    @RequestMapping(value = "/addFolder", method = RequestMethod.POST)
    public String addNewFolder(@ModelAttribute Folder folderToAdd, Model model) {
        fileSystemService.addNewFolderToFolder(authenticatedAccount, currentFolder, folderToAdd.getName());
        return "main";
    }

    @RequestMapping(value = "/deleteFileSystemObject/{fileSystemObjectId}")
    public String deleteFileSystemObject(@ModelAttribute Folder folderToAdd, Model model,  @PathVariable("fileSystemObjectId") long fileSystemObjectId) {
        fileSystemService.deleteFileSystemObject(authenticatedAccount, currentFolder, fileSystemObjectId);
        return "main";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("uploadedFile") MultipartFile[] files, Model model) {
        fileSystemService.addFileToFolder(authenticatedAccount, currentFolder, files);
        return "main";
    }

    @ModelAttribute("account")
    public Account getAuthenticatedAccount() {
        return authenticatedAccount != null ? authenticatedAccount : new Account();
    }

    @ModelAttribute("currentFolder")
    public Folder getCurrentFolder() {
        return currentFolder != null ? currentFolder : new Folder();
    }

    @ModelAttribute("folderToAdd")
    public Folder getFolderToAdd() {
        return new Folder();
    }
}
