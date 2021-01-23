package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.PermissionDoesNotExistException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemService;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;

@Controller
@Scope("session")
public class AcquiredPermissionController {

    @Autowired
    private IFileSystemService fileSystemService;

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private MainController mainController;

    @RequestMapping(value = "/permissions/received", method = RequestMethod.GET)
    public String viewPermissions(Model model, Principal principal) {

        Account account = mainController.getAuthenticatedAccount(principal);
        model.addAttribute("account", account);
        model.addAttribute("shares", account.getPermissionMap());

        return "permissionsReceived";
    }

    @RequestMapping(value = "/permissions/received/fileSystemObject", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> downloadFile(Model model, Principal principal, @RequestParam("permissionId") long permissionId) throws
            IOException,
            FileSystemObjectDoesNotExistException,
            PermissionDoesNotExistException {

        return fileSystemService.getFileSystemObjectResponseEntityByPermission(mainController.getAuthenticatedAccount(principal), permissionId);
    }

    @RequestMapping(value = "/permissions/received", method = RequestMethod.DELETE)
    public String deletePermission(Model model, Principal principal, @RequestParam("permissionId") long permissionId) throws
            FileSystemObjectDoesNotExistException,
            PermissionDoesNotExistException {

        Account account = mainController.getAuthenticatedAccount(principal);
        Permission permission = permissionService.getPermission(account, permissionId);
        account.removePermission(permission);
        permissionService.deletePermission(permission);

        model.addAttribute("account", account);
        model.addAttribute("shares", account.getPermissionMap());

        return "permissionsReceived";
    }

    /**
     * If a IOException is thrown return a internal server error
     *
     * @return not found response
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleException() {
        return ResponseEntity.status(500).body("A problem occured internally");
    }

    /**
     * If a FileSystemObjectDoesNotExistException is thrown the main permissions received view should be shown again
     *
     * @return the name of the view
     */
    @ExceptionHandler({FileSystemObjectDoesNotExistException.class, PermissionDoesNotExistException.class})
    public String handleFileSystemDoesNoteExistException(Model model, Principal principal) {
        return viewPermissions(model, principal);
    }
}
