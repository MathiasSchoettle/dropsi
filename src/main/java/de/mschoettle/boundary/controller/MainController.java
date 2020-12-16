package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.entity.Account;
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

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String showMain(Model model, Principal principal) {
        model.addAttribute("currentFolder", getRootFolderOfPrincipal(principal));
        return "main";
    }

    @RequestMapping(value = "/folder", method = RequestMethod.GET)
    public String showFolder(Model model, Principal principal, @RequestParam("folderId") long folderId) {
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/folder", method = RequestMethod.POST)
    public String addFolder(Model model, Principal principal, @RequestParam("folderId") long folderId,  @ModelAttribute("folderName") String folderName) {
        fileSystemService.addNewFolderToFolder(getAuthenticatedAccount(principal), folderId, folderName);
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/filesystemobject", method = RequestMethod.POST)
    public String uploadFile(Model model, Principal principal, @RequestParam("folderId") long folderId, @RequestParam("uploadedFile") MultipartFile[] files) {
        fileSystemService.addFileToFolder(getAuthenticatedAccount(principal), folderId, files);
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/filesystemobject", method = RequestMethod.DELETE)
    public String deleteFileSystemObject(Model model, Principal principal, @RequestParam("folderId") long folderId, @RequestParam("fileSystemObjectId") long fileSystemObjectId) {
        fileSystemService.deleteFileSystemObject(getAuthenticatedAccount(principal), folderId, fileSystemObjectId);
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    /**
     * Adds a folder with given folder id to the given model.
     * If the folder does not exist or is not owned by the given principal the root folder of the principal is added.
     *
     * @param model the model the folder is added to
     * @param principal the current principal
     * @param folderId the folder id
     */
    private void addFolderToModel(Model model, Principal principal, long folderId) {
        Optional<FileSystemObject> folderOptional = fileSystemService.getFileSystemObjectById(folderId, getAuthenticatedAccount(principal));
        model.addAttribute("currentFolder", (Folder) folderOptional.orElseGet(() -> getRootFolderOfPrincipal(principal)));
    }

    private Folder getRootFolderOfPrincipal(Principal principal) {
        return getAuthenticatedAccount(principal).getRootFolder();
    }

    public Account getAuthenticatedAccount(Principal principal) {
        return (Account) accountService.loadUserByUsername(principal.getName());
    }
}
