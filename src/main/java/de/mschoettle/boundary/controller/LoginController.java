package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
@Scope("session")
public class LoginController {

    private IAccountService accountService;

    @Autowired
    public void setInjectedBean(IAccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(value = {"/login", "/"}, method = RequestMethod.GET)
    public String showLoginView(Model model) {
        model.addAttribute("usernameOrPasswordIsWrong", false);
        return "login";
    }

    @RequestMapping(value = "/loginFailed", method = RequestMethod.GET)
    public String loginFailed(Model model){
        model.addAttribute("usernameOrPasswordIsWrong", true);
        return "login";
    }

    @RequestMapping(value = "/deleteAccount", method = RequestMethod.GET)
    public String deleteAccount(Model model, Principal principal) {
        SecurityContextHolder.getContext().setAuthentication(null);
        accountService.deleteAccount(accountService.getAuthenticatedAccount(principal));
        model.addAttribute("usernameOrPasswordIsWrong", false);
        return "login";
    }
}
