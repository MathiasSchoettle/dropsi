package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.ManyToOne;
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

        model.addAttribute("name", mainController.getAuthenticatedAccount(principal).getName());
        model.addAttribute("email", mainController.getAuthenticatedAccount(principal).getEmail());

        model.addAttribute("triedToChangeUsername", false);
        model.addAttribute("changedUsername", false);
        model.addAttribute("triedToChangeEmail", false);
        model.addAttribute("changedEmail", false);

        model.addAttribute("account", new Account());

        return "settings";
    }

    // TODO refactor this maybe
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public String saveSettings(Model model, Principal principal, @ModelAttribute Account account) {

        boolean triedToChangeUsername;
        boolean changedUsername = false;

        boolean triedToChangeEmail;
        boolean changedEmail = false;

        Account auhenticatedAccount = mainController.getAuthenticatedAccount(principal);

        if (triedToChangeUsername = !account.getName().trim().equals("")) {
            if (changedUsername = !accountService.isUsernameTaken(account.getName())) {
                auhenticatedAccount.setName(account.getName());
            }
        }

        model.addAttribute("name", auhenticatedAccount.getName());

        if (triedToChangeEmail = !account.getEmail().trim().equals("")) {
            if (changedEmail = !accountService.isEmailTaken(account.getEmail())) {
                auhenticatedAccount.setEmail(account.getEmail());
            }
        }

        model.addAttribute("email", auhenticatedAccount.getEmail());

        model.addAttribute("triedToChangeUsername", triedToChangeUsername);
        model.addAttribute("changedUsername", changedUsername);
        model.addAttribute("triedToChangeEmail", triedToChangeEmail);
        model.addAttribute("changedEmail", changedEmail);

        model.addAttribute("account", new Account());

        accountService.saveAccount(auhenticatedAccount);

        return "settings";
    }
}