package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.i.IAccountService;
import de.mschoettle.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SettingsController {

    @Autowired
    private IAccountService accountService;

    @RequestMapping(value = "/settings")
    public String showSettings(Model model) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Account account = (Account) accountService.loadUserByUsername(username);
        model.addAttribute("account", account);
        return "settings";
    }
}
