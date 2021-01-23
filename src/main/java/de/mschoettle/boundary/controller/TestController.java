package de.mschoettle.boundary.controller;

import de.mschoettle.entity.dto.FolderDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@Controller
public class TestController {

    @RequestMapping(value ="/test", method = RequestMethod.GET)
    public String test(Model model) {
        return "test";
    }

    @RequestMapping(value = "/test/rest", method = RequestMethod.GET)
    public String testRest(Model model) {

        final String uri = "http://localhost:8922/api/rootfolder?secretKey=fb12247a-fbcb-4c69-a04f-c60d0476c4f2";

        RestTemplate restTemplate = new RestTemplate();
        FolderDTO result = restTemplate.getForObject(uri, FolderDTO.class);

        return "test";
    }
}
