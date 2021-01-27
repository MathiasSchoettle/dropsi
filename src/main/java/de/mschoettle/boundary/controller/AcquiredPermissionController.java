package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.PermissionDoesNotExistException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.Permission;
import de.othr.sw.hamilton.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
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

    private IFileSystemObjectService fileSystemService;

    private IPermissionService permissionService;

    private IAccountService accountService;

    private Payment payment;

    @Autowired
    public void setInjectedBean(IFileSystemObjectService fileSystemService,
                           IPermissionService permissionService,
                           IAccountService accountService,
                           Payment payment) {

        this.fileSystemService = fileSystemService;
        this.permissionService = permissionService;
        this.accountService = accountService;
        this.payment = payment;
    }

    @RequestMapping(value = "/permissions/received", method = RequestMethod.GET)
    public String viewPermissions(Model model, Principal principal) {

        Account account = accountService.getAuthenticatedAccount(principal);
        model.addAttribute("account", account);
        model.addAttribute("shares", permissionService.getPermissionMap(account));
        model.addAttribute("payment", payment);

        return "permissionsReceived";
    }

    @RequestMapping(value = "/permissions/received/fileSystemObject", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> downloadFile(Principal principal, @RequestParam("permissionId") long permissionId) throws
            IOException,
            FileSystemObjectDoesNotExistException,
            PermissionDoesNotExistException {

        return fileSystemService.getFileSystemObjectResponseEntityByPermission(accountService.getAuthenticatedAccount(principal), permissionId);
    }

    @RequestMapping(value = "/permissions/received", method = RequestMethod.DELETE)
    public String deletePermission(Model model, Principal principal, @RequestParam("permissionId") long permissionId) throws
            FileSystemObjectDoesNotExistException,
            PermissionDoesNotExistException {

        Account account = accountService.getAuthenticatedAccount(principal);
        Permission permission = permissionService.getPermission(account, permissionId);
        account.removePermission(permission);
        permissionService.deletePermission(permission);

        model.addAttribute("account", account);
        model.addAttribute("shares", permissionService.getPermissionMap(account));
        model.addAttribute("payment", payment);

        return "permissionsReceived";
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A problem occurred internally");
    }

    @ExceptionHandler({FileSystemObjectDoesNotExistException.class, PermissionDoesNotExistException.class})
    public String handleFileSystemDoesNoteExistException(Model model, Principal principal) {
        return viewPermissions(model, principal);
    }
}
