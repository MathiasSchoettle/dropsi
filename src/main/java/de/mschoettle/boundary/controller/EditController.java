package de.mschoettle.boundary.controller;

import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.service.IFileSystemService;
import de.mschoettle.entity.AccessLogEntry;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
@Scope("session")
public class EditController {

    @Autowired
    private IFileSystemService fileSystemService;

    @Autowired
    private MainController mainController;

    @RequestMapping(value = {"/editInfo"}, method = RequestMethod.GET)
    public String showInformation(Model model, Principal principal, @RequestParam("fileSystemObjectId") long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException {

        Account account = mainController.getAuthenticatedAccount(principal);
        FileSystemObject fileSystemObject = fileSystemService.getFileSystemObject(account, fileSystemObjectId);

        model.addAttribute("accessLogMap", getAccessLogEntriesMap(fileSystemObject));
        model.addAttribute("fileSystemObject", fileSystemObject);

        return "editInfo";
    }

    @RequestMapping(value = {"/editInfo"}, method = RequestMethod.PUT)
    public String changeFileSystemObjectName(Model model, Principal principal,
                                   @RequestParam("fileSystemObjectId") long fileSystemObjectId,
                                   @ModelAttribute("fileSystemObjectName") String fileSystemObjectName) throws
            FileSystemObjectDoesNotExistException {

        Account account = mainController.getAuthenticatedAccount(principal);
        FileSystemObject fileSystemObject = fileSystemService.getFileSystemObject(account, fileSystemObjectId);

        fileSystemService.changeNameOfFileSystemObject(fileSystemObjectId, account, fileSystemObjectName);

        model.addAttribute("fileSystemObject", fileSystemObject);
        model.addAttribute("accessLogMap", getAccessLogEntriesMap(fileSystemObject));

        return "editInfo";
    }

    private Map<LocalDate, List<AccessLogEntry>> getAccessLogEntriesMap(FileSystemObject fileSystemObject) {

        Map<LocalDate, List<AccessLogEntry>> map = new TreeMap<>(Collections.reverseOrder());

        for (AccessLogEntry a : fileSystemObject.getAccessLogs()) {

            LocalDate ld = a.getCreationDate().toLocalDate();

            if (map.containsKey(ld)) {
                map.get(ld).add(a);
            } else {
                List<AccessLogEntry> list = new ArrayList<>();
                list.add(a);
                map.put(ld, list);
            }
        }

        return map;
    }

    @ExceptionHandler(FileSystemObjectDoesNotExistException.class)
    public String handleFileSystemObjectDoesNotExistException(Model model, Principal principal) {
        return mainController.showMain(model, principal);
    }
}