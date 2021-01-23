package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@Scope("session")
public class SettingsController {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private MainController mainController;

    @RequestMapping(value = "/settings")
    public String showSettings(Model model, Principal principal) {

        Account account = mainController.getAuthenticatedAccount(principal);
        model.addAttribute("name", account.getName());
        model.addAttribute("email", account.getEmail());
        model.addAttribute("secretKey", account.getSecretKey());

        model.addAttribute("triedToChangeUsername", false);
        model.addAttribute("changedUsername", false);
        model.addAttribute("triedToChangeEmail", false);
        model.addAttribute("changedEmail", false);

        model.addAttribute("account", new Account());

        return "settings";
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public String saveSettings(Model model, Principal principal,
                               @ModelAttribute Account account,
                               @RequestParam("avatarImage") MultipartFile[] avatarImage)
            throws IOException {

        Account authenticatedAccount = mainController.getAuthenticatedAccount(principal);

        boolean triedToChangeUsername = !account.getName().trim().equals("");
        boolean changedUsername = !accountService.isUsernameTaken(account.getName());

        boolean triedToChangeEmail = !account.getEmail().trim().equals("");
        boolean changedEmail = !accountService.isEmailTaken(account.getEmail());


        if (triedToChangeUsername && changedUsername) {
            authenticatedAccount.setName(account.getName());
        }

        if (triedToChangeEmail && changedEmail) {
            authenticatedAccount.setEmail(account.getEmail());
        }

        if (avatarImage.length > 0 && avatarImage[0].getSize() > 0 && avatarImage[0].getSize() < 500000) {
            authenticatedAccount.setAvatar(avatarImage[0].getBytes());
        }

        model.addAttribute("name", authenticatedAccount.getName());
        model.addAttribute("email", authenticatedAccount.getEmail());
        model.addAttribute("secretKey", authenticatedAccount.getSecretKey());

        model.addAttribute("triedToChangeUsername", triedToChangeUsername);
        model.addAttribute("changedUsername", changedUsername);
        model.addAttribute("triedToChangeEmail", triedToChangeEmail);
        model.addAttribute("changedEmail", changedEmail);

        model.addAttribute("account", new Account());

        accountService.saveAccount(authenticatedAccount);

        Authentication a = new UsernamePasswordAuthenticationToken(
                authenticatedAccount,
                null,
                ((UserDetails) authenticatedAccount).getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(a);

        return "settings";
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Void> handleIOException() {
        return ResponseEntity.badRequest().build();
    }
}