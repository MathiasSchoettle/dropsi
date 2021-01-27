package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.NotAFileException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.entity.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@Controller
@Scope("singleton")
public class ViewFileController {

    private IFileSystemObjectService fileSystemObjectService;

    private IAccountService accountService;

    private IFileService fileService;

    @Autowired
    public void setInjectedBean(IFileSystemObjectService fileSystemObjectService,
                                IAccountService accountService,
                                @Qualifier("local") IFileService fileService) {

        this.fileSystemObjectService = fileSystemObjectService;
        this.accountService = accountService;
        this.fileService = fileService;
    }

    @RequestMapping(value = {"/view"}, method = RequestMethod.GET)
    public String viewFile(Model model, Principal principal, @RequestParam("fileId") long fileId) throws
            IOException,
            FileSystemObjectDoesNotExistException,
            NotAFileException {

        File file = fileSystemObjectService.getFile(accountService.getAuthenticatedAccount(principal), fileId);
        model.addAttribute("parentId", file.getParent().getId());

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

        return "fileViews/viewNa";
    }

    private String prepareForVideo(Model model, File file) throws IOException {

        byte[] bytes = fileService.getByteArrayOfFile(file);
        model.addAttribute("type", file.getFileType());
        model.addAttribute("typeString", "data:" + file.getFileType() + ";base64,");
        model.addAttribute("video", Base64.getEncoder().encodeToString(bytes));

        return "fileViews/viewVideo";
    }

    private String prepareForImage(Model model, File file) throws IOException {

        byte[] bytes = fileService.getByteArrayOfFile(file);
        model.addAttribute("type", "data:" + file.getFileType() + ";base64,");
        model.addAttribute("image", Base64.getEncoder().encodeToString(bytes));

        return "fileViews/viewImage";
    }

    private String prepareForText(Model model, File file) throws IOException {
        model.addAttribute("text", fileService.getStringOfFile(file));
        return "fileViews/viewText";
    }

    @ExceptionHandler(FileSystemObjectDoesNotExistException.class)
    public String handleFileSystemObjectDoesNotExistException() {
        return "redirect:/home";
    }
}