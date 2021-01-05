package de.mschoettle.boundary.controller;

import de.mschoettle.control.service.IInternalFileSystemService;
import de.mschoettle.entity.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Base64;

@Controller
@Scope("session")
public class ViewFileController {

    @Autowired
    private IInternalFileSystemService fileSystemService;

    @Autowired
    private MainController mainController;

    @RequestMapping(value = {"/view"}, method = RequestMethod.GET)
    public String viewFile(Model model, Principal principal, @RequestParam("fileId") long fileId) throws IOException {

        File file = fileSystemService.getFile(mainController.getAuthenticatedAccount(principal), fileId);
        model.addAttribute("parentId", file.getParent().getId());

        // TODO move strings into ENUM or something
        switch (file.getFileType()) {
            case "image/png", "image/jpeg", "image/gif" -> {
                return prepareForImage(model, file);
            }
            case "text/plain" -> {
                return prepareForText(model, file);
            }
            case "video/mp4" -> {
                return prepareForVideo(model, file);
            }
        }

        return "viewNa";
    }

    private String prepareForVideo(Model model, File file) throws IOException {

        byte[] bytes = Files.readAllBytes(Path.of(System.getProperty("user.dir"), "files", file.getFileReference()));
        model.addAttribute("type", file.getFileType());
        model.addAttribute("typeString", "data:" + file.getFileType() + ";base64,");
        model.addAttribute("video", Base64.getEncoder().encodeToString(bytes));

        return "viewVideo";
    }

    private String prepareForImage(Model model, File file) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(System.getProperty("user.dir"), "files", file.getFileReference()));
        model.addAttribute("type", "data:" + file.getFileType() + ";base64,");
        model.addAttribute("image", Base64.getEncoder().encodeToString(bytes));
        return "viewImage";
    }

    private String prepareForText(Model model, File file) throws IOException {
        model.addAttribute("text", Files.readString(Path.of(System.getProperty("user.dir"), "files", file.getFileReference())));
        return "viewText";
    }
}