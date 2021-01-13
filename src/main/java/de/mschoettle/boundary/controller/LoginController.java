package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@Scope("session")
public class LoginController {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private MainController mainController;

    @RequestMapping(value ="/test")
    public String test(Model model) {
        return "test";
    }

    @RequestMapping(value = {"/login", "/"})
    public String showLoginView(Model model) {
        model.addAttribute("usernameOrPasswordIsWrong", false);
        model.addAttribute("date", LocalDate.now());
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
