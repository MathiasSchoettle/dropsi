package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.AccountService;
import de.mschoettle.control.service.i.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Scope("session")
public class LoginController {

    @Autowired
    private IAccountService accountService;

    @RequestMapping(value = {"/login", "/"})
    public String showLoginView(Model model) {
        model.addAttribute("usernameOrPasswordIsWrong", false);
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginToAccount(Model model) {
        model.addAttribute("usernameOrPasswordIsWrong", false);
        return "main";
    }

    @RequestMapping(value = "/loginFailed")
    public String loginFailed(Model model){
        model.addAttribute("usernameOrPasswordIsWrong", true);
        return "login";
    }
}
