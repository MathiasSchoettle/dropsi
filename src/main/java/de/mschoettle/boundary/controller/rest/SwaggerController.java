package de.mschoettle.boundary.controller.rest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Scope("singleton")
public class SwaggerController {

    @RequestMapping(value ="swagger-ui", method = RequestMethod.GET)
    public String showSwagger() {
        return "redirect:/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config";
    }
}
