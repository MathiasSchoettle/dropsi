package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.AccountService;
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
    AccountService accountService;

    @RequestMapping(value = "/")
    public String uponLoad(){
        return "login";
    }

    @RequestMapping(value = "/login")
    public String showLoginView(){
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginToAccount(Model model) {
        return "main";
    }

    // TODO remove
    @RequestMapping(value = "/test")
    public String test(){
        return "test";
    }
}
