package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@Scope("session")
public class GivenPermissionController {

    @Autowired
    private MainController mainController;

    @Autowired
    private IPermissionService permissionService;

    @RequestMapping(value = "/permissions/given", method = RequestMethod.GET)
    public String viewPermissions(Model model, Principal principal) {
        addDataToModel(model, principal);
        return "permissionsGiven";
    }

    @RequestMapping(value = "/permissions/given", method = RequestMethod.DELETE)
    public String deletePermission(Model model, Principal principal, @PathParam("accountId") long accountId, @PathParam("fileSystemObjectId") long fileSystemObjectId) throws
            AccountDoesNotExistsException,
            FileSystemObjectDoesNotExistException {

        Account owner = mainController.getAuthenticatedAccount(principal);
        permissionService.deletePermissionOfAccount(owner, accountId, fileSystemObjectId);

        addDataToModel(model, principal);
        return "permissionsGiven";
    }

    @RequestMapping(value = "/permissions/given/all", method = RequestMethod.DELETE)
    public String deletePermissions(Model model, Principal principal, @PathParam("fileSystemObjectId") long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException {

        permissionService.deletePermissionsOfFileSystemObject(mainController.getAuthenticatedAccount(principal), fileSystemObjectId);

        addDataToModel(model, principal);
        return "permissionsGiven";
    }

    @ExceptionHandler({AccountDoesNotExistsException.class, FileSystemObjectDoesNotExistException.class})
    public String handleExceptions(Model model, Principal principal) {
        return viewPermissions(model, principal);
    }

    private void addDataToModel(Model model, Principal principal) {
        Account provider = mainController.getAuthenticatedAccount(principal);
        model.addAttribute("account", provider);
        Map<FileSystemObject, List<Account>> permissions = permissionService.getPermissionsGivenMap(provider);
        model.addAttribute("permissions", permissions);
    }
}
