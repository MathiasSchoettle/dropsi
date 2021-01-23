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
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletRequest;

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
    /**
     * Creates a new Account with the user given information if this information is valid.
     * If an Exception is thrown the Exception handlers in this class return the sign up view again with updated information
     *
     * @param account an Account Object which is just used as a container for the given inputs
     * @param model the model to be filled for the view
     *
     * @return the String of the login view
     *
     * @throws AccountDoesNotExistsException this Exception can be left unhandled as the account always exists
     * @throws FileSystemObjectDoesNotExistException this Exception can be left unhandled because the rootfolder of the account always exists
     * @throws CredentialException this is thrown when the user enters invalid name, password or email inputs. i.e. when the input is empty or null
     * @throws EmailTakenException this is thrown when an account already exists with the entered email address
     * @throws AccountNameTakenException this is thrown when an account already exists with the entered username
     */
    @RequestMapping(value = "/sign_up", method = RequestMethod.POST)
    public String signUpNewAccount(@ModelAttribute Account account, Model model)
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
        return "sign_up";
    }
}
