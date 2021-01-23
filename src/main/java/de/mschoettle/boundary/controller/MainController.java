package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.NotAFileException;
import de.mschoettle.control.exception.NotAFolderException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.Random;

@Controller
@Scope("session")
public class MainController {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IFileSystemService fileSystemService;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String showMain(Model model, Principal principal) {
        model.addAttribute("currentFolder", getRootFolderOfPrincipal(principal));
        model.addAttribute("account", getAuthenticatedAccount(principal));
        return "main";
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String showFolder(Model model, Principal principal, @RequestParam("folderId") long folderId) {
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/folder", method = RequestMethod.POST)
    public String addFolder(Model model, Principal principal,
                            @RequestParam("folderId") long folderId,
                            @ModelAttribute("folderName") String folderName) throws
            AccountDoesNotExistsException,
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        fileSystemService.addNewFolderToFolder(getAuthenticatedAccount(principal), folderId, folderName);
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/fileSystemObject", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> downloadFile(Model model, Principal principal,
                                                          @RequestParam("fileSystemObjectId") long fileSystemObjectId)
            throws IOException, FileSystemObjectDoesNotExistException {

        return fileSystemService.getFileSystemObjectResponseEntity(getAuthenticatedAccount(principal), fileSystemObjectId);
    }

    @RequestMapping(value = "/filesystemobject", method = RequestMethod.POST)
    public String uploadFile(Model model, Principal principal,
                             @RequestParam("folderId") long folderId,
                             @RequestParam("uploadedFile") MultipartFile file) throws
            IOException,
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        fileSystemService.addMultipartFileToFolder(getAuthenticatedAccount(principal), folderId, file);
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/filesystemobject/copy", method = RequestMethod.POST)
    public String copyFileSystemObject(Model model, Principal principal,
                             @RequestParam("fileSystemObjectId") long fileSystemObjectId,
                             @RequestParam("parentId") long parentId) throws
            IOException,
            FileSystemObjectDoesNotExistException,
            NotAFolderException,
            NotAFileException {

        Account account = getAuthenticatedAccount(principal);
        fileSystemService.copyFileSystemObject(account, fileSystemObjectId, parentId);

        addFolderToModel(model, principal, parentId);
        return "main";
    }

    @RequestMapping(value = "/filesystemobject", method = RequestMethod.DELETE)
    public String deleteFileSystemObject(Model model, Principal principal,
                                         @RequestParam("folderId") long folderId,
                                         @RequestParam("fileSystemObjectId") long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException,
            IOException,
            NotAFolderException {

        fileSystemService.deleteFileSystemObject(getAuthenticatedAccount(principal), folderId, fileSystemObjectId);
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/share/retrogram", method = RequestMethod.POST)
    public String postToRetrogram(Model model, Principal principal,
                                  @RequestParam("fileId") long fileId,
                                  @RequestParam("folderId") long folderId) throws
            NotAFileException,
            FileSystemObjectDoesNotExistException,
            IOException {

        // TODO do this all in service and build Post object of Retrogram
        File file = fileSystemService.getFile(getAuthenticatedAccount(principal), fileId);


        String description = "posted by Dropsi";
        byte[] data = fileSystemService.getByteArrayOfFile(getAuthenticatedAccount(principal), fileId);
        String fileType = file.getFileExtension();

        model.addAttribute("popupString", new Random().nextBoolean() ? "❤ Posted Image to Retrogram" : "❌ Could not post Image to Retrogram");
        addFolderToModel(model, principal, folderId);
        // TODO call post rest method of Retrogram
        return "main";
    }

    /**
     * Adds a folder with given folder id to the given model.
     * If the folder does not exist or is not owned by the given principal the root folder of the principal is added.
     *
     * @param model     the model the folder is added to
     * @param principal the current principal
     * @param folderId  the folder id
     */
    public void addFolderToModel(Model model, Principal principal, long folderId) {
        Optional<FileSystemObject> folderOptional = fileSystemService.getFileSystemObjectById(folderId, getAuthenticatedAccount(principal));
        // cast to folder so there are less thymeleaf errors
        model.addAttribute("currentFolder", (Folder) folderOptional.orElseGet(() -> getRootFolderOfPrincipal(principal)));
        model.addAttribute("account", getAuthenticatedAccount(principal));
    }

    private Folder getRootFolderOfPrincipal(Principal principal) {
        return getAuthenticatedAccount(principal).getRootFolder();
    }

    public Account getAuthenticatedAccount(Principal principal) {
        return (Account) accountService.loadUserByUsername(principal.getName());
    }

    @ModelAttribute("popupString")
    public String getPopupMessageString() {
        return "";
    }

    // TODO exception handling
}
