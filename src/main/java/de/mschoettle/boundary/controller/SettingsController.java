package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Scope("session")
public class SettingsController {

    @Autowired
    private IAccountService accountService;

    @RequestMapping(value = "/settings")
    public String showSettings(Model model) {

        Account account = accountService.getLoggedInAccount();

        model.addAttribute("name", account.getName());
        model.addAttribute("email", account.getEmail());

        model.addAttribute("triedToChangeUsername", false);
        model.addAttribute("changedUsername", false);
        model.addAttribute("triedToChangeEmail", false);
        model.addAttribute("changedEmail", false);

        model.addAttribute("account", new Account());

        return "settings";
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public String saveSettings(@ModelAttribute Account account, Model model) {

        Account currentAccount = accountService.getLoggedInAccount();

        boolean triedToChangeUsername;
        boolean changedUsername = false;

        boolean triedToChangeEmail;
        boolean changedEmail = false;

        if (triedToChangeUsername = !account.getName().trim().equals("")) {
            if (changedUsername = !accountService.isUsernameTaken(account.getName())) {
                currentAccount.setName(account.getName());
            }
        }

        model.addAttribute("name", currentAccount.getName());

        if (triedToChangeEmail = !account.getEmail().trim().equals("")) {
            if (changedEmail = !accountService.isEmailTaken(account.getEmail())) {
                currentAccount.setEmail(account.getEmail());
            }
        }

        model.addAttribute("email", currentAccount.getEmail());

        model.addAttribute("triedToChangeUsername", triedToChangeUsername);
        model.addAttribute("changedUsername", changedUsername);
        model.addAttribute("triedToChangeEmail", triedToChangeEmail);
        model.addAttribute("changedEmail", changedEmail);

        model.addAttribute("account", new Account());

        accountService.saveAccount(currentAccount);

        return "settings";
    }

    @RequestMapping(value = "/deleteAccount")
    public String deleteAccount(Model model) {
        accountService.deleteAccount(accountService.getLoggedInAccount());
        SecurityContextHolder.getContext().setAuthentication(null);

        return "login";
    }
}