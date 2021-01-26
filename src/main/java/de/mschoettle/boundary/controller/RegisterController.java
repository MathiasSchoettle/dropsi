package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.AccountNameTakenException;
import de.mschoettle.control.exception.EmailTakenException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@Scope("session")
public class SignUpController {

    @Autowired
    private IAccountService accountService;

    @RequestMapping(value = "/sign_up")
    public String showSignUpView(Model model) {
        return addDataToModel(model, false, false);
    }

    // TODO make successful sign up automatic login
    @RequestMapping(value = "/sign_up", method = RequestMethod.POST)
    public String signUpNewAccount(@ModelAttribute Account account, Model model, Principal principal)
            throws AccountDoesNotExistsException,
            FileSystemObjectDoesNotExistException,
            CredentialException,
            EmailTakenException,
            AccountNameTakenException {



        accountService.createNewAccount(account);
        Account a = accountService.getAccount(account.getId());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                a,
                null,
                ((UserDetails) a).getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        return "main";
    }

    @ExceptionHandler(AccountNameTakenException.class)
    public String handleAccountNameTakenException(Model model) {
        return addDataToModel(model, true, false);
    }

    @ExceptionHandler(EmailTakenException.class)
    public String handleEmailTakenException(Model model) {
        return addDataToModel(model, false, true);
    }

    private String addDataToModel(Model model, boolean nameTaken, boolean emailTaken) {
        model.addAttribute("account", new Account());
        model.addAttribute("accountNameTaken", nameTaken);
        model.addAttribute("emailTaken", emailTaken);
        return "register";
    }
}
