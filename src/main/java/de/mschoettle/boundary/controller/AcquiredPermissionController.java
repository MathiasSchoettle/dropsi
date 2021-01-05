package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@Controller
@Scope("session")
public class AcquiredPermissionController {

    @Autowired
    private IInternalFileSystemService fileSystemService;

    @Autowired
    private IAccountService accountService;

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
    public ResponseEntity<ByteArrayResource> downloadFile(Model model, Principal principal, @RequestParam("permissionId") long permissionId) throws IOException {
        return fileSystemService.getFileSystemObjectResponseEntityByPermission(mainController.getAuthenticatedAccount(principal), permissionId);
    }

    @RequestMapping(value = "/permissions/received", method = RequestMethod.DELETE)
    public String deletePermission(Model model, Principal principal, @RequestParam("permissionId") long permissionId) {

        Account account = mainController.getAuthenticatedAccount(principal);
        Permission permission = permissionService.getPermission(account, permissionId);
        account.removePermission(permission);
        permissionService.deletePermission(permission);

        model.addAttribute("account", account);
        model.addAttribute("shares", account.getPermissionMap());

        return "permissionsReceived";
    }
}
