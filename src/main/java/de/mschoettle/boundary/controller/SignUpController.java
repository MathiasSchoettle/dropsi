package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@Scope("session")
public class SignUpController {

    @Autowired
    private IAccountService accountService;

    @RequestMapping(value = "/sign_up")
    public String showSignUpView(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("accountNameTaken", false);
        model.addAttribute("emailTaken", false);
        return "sign_up";
    }

    // TODO make successful sign up automatic login
    // TODO refactor this because now i have superior knowledge
    @RequestMapping(value = "/sign_up", method = RequestMethod.POST)
    public String signUpNewAccount(@ModelAttribute Account account, Model model, HttpServletRequest request) {

        if (account != null) {
            model.addAttribute("accountNameTaken", accountService.isUsernameTaken(account.getUsername()));
            model.addAttribute("emailTaken", accountService.isEmailTaken(account.getEmail()));

            if (!(boolean) model.getAttribute("accountNameTaken") && !(boolean) model.getAttribute("emailTaken")) {
                accountService.createNewAccount(account);
                return "login";
            }
        }

        return "sign_up";
    }
}
