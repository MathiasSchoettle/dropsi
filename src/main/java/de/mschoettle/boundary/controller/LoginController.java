package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PreRemove;
import java.security.Principal;

@Controller
@Scope("session")
public class LoginController {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private MainController mainController;

    @RequestMapping(value = {"/login", "/"})
    public String showLoginView(Model model) {
        model.addAttribute("usernameOrPasswordIsWrong", false);
        return "login";
    }

    @RequestMapping(value = "/loginFailed")
    public String loginFailed(Model model){
        model.addAttribute("usernameOrPasswordIsWrong", true);
        return "login";
    }

    @RequestMapping(value = "/deleteAccount")
    public String deleteAccount(Model model, Principal principal) {
        SecurityContextHolder.getContext().setAuthentication(null);
        accountService.deleteAccount(mainController.getAuthenticatedAccount(principal));
        model.addAttribute("usernameOrPasswordIsWrong", false);
        return "login";
    }
}
