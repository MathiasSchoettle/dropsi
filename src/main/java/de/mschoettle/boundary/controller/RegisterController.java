package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.AccountNameTakenException;
import de.mschoettle.control.exception.EmailTakenException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.security.auth.login.CredentialException;

@Controller
@Scope("singleton")
public class RegisterController {

    private IAccountService accountService;

    @Autowired
    public void setInjectedBean(IAccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(value = "/register")
    public String showSignUpView(Model model) {
        return addDataToModel(model, false, false);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String signUpNewAccount(@ModelAttribute Account account)
            throws AccountDoesNotExistsException,
            FileSystemObjectDoesNotExistException,
            CredentialException,
            EmailTakenException,
            AccountNameTakenException {

        accountService.createNewAccount(account);
        return "login";
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
