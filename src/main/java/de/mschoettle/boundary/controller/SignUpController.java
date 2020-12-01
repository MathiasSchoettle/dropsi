package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.AccountService;
import de.mschoettle.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SignUpController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/sign_up", method = RequestMethod.GET)
    public String showSignUpView(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("accountNameTaken", false);
        model.addAttribute("emailTaken", false);
        return "sign_up";
    }

    @RequestMapping(value = "/sign_up", method = RequestMethod.POST)
    public String signUpNewAccount(@ModelAttribute Account account, Model model) {

        if (account != null) {
            model.addAttribute("accountNameTaken", accountService.isUsernameTaken(account.getUsername()));
            model.addAttribute("emailTaken", accountService.isEmailTaken(account.getEmail()));

            if (!(boolean) model.getAttribute("accountNameTaken") && !(boolean) model.getAttribute("emailTaken")) {
                accountService.createNewUser(account);
                return "main";
            }
        }

        return "sign_up";
    }
}