package de.mschoettle.boundary.controller;

import com.othr.jonasrombach.retrogram.dto.PostDto;
import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.NotAFileException;
import de.mschoettle.control.exception.NotAFolderException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import de.othr.sw.hamilton.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Controller
@Scope("singleton")
public class MainController {

    private IAccountService accountService;

    private IFileSystemObjectService fileSystemObjectService;

    private RestTemplate restTemplate;

    private IFileService fileService;

    private Payment payment;

    @Autowired
    public void setInjectedBean(IAccountService accountService,
                                IFileSystemObjectService fileSystemObjectService,
                                RestTemplate restTemplate,
                                @Qualifier("local") IFileService fileService,
                                Payment payment) {

        this.accountService = accountService;
        this.fileSystemObjectService = fileSystemObjectService;
        this.restTemplate = restTemplate;
        this.fileService = fileService;
        this.payment = payment;
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String showMain(Model model, Principal principal) {
        model.addAttribute("currentFolder", getRootFolderOfPrincipal(principal));
        model.addAttribute("account", accountService.getAuthenticatedAccount(principal));
        model.addAttribute("payment", payment);
        return "main";
    }

    @RequestMapping(value = "/payCoffee", method = RequestMethod.GET)
    public String payCoffee() {
        payment.setFulfilled(true);
        return "redirect:home";
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String showFolder(Model model, Principal principal, @RequestParam("folderId") long folderId) {
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/folder", method = RequestMethod.POST)
    public String addFolder(Model model, Principal principal,
                            @RequestParam("folderId") long folderId,
                            @ModelAttribute("folderName") String folderName) throws
            AccountDoesNotExistsException,
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        fileSystemObjectService.addNewFolderToFolder(accountService.getAuthenticatedAccount(principal), folderId, folderName);
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/fileSystemObject", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> downloadFile(Principal principal, @RequestParam("fileSystemObjectId") long fileSystemObjectId) throws
            IOException,
            FileSystemObjectDoesNotExistException {

        return fileSystemObjectService.getFileSystemObjectResponseEntity(accountService.getAuthenticatedAccount(principal), fileSystemObjectId);
    }

    @RequestMapping(value = "/filesystemobject", method = RequestMethod.POST)
    public String uploadFile(Model model, Principal principal,
                             @RequestParam("folderId") long folderId,
                             @RequestParam("uploadedFile") MultipartFile file) throws
            IOException,
            FileSystemObjectDoesNotExistException,
            NotAFolderException {

        fileSystemObjectService.addMultipartFileToFolder(accountService.getAuthenticatedAccount(principal), folderId, file);
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/filesystemobject/copy", method = RequestMethod.POST)
    public String copyFileSystemObject(Model model, Principal principal,
                                       @RequestParam("fileSystemObjectId") long fileSystemObjectId,
                                       @RequestParam("parentId") long parentId) throws
            IOException,
            FileSystemObjectDoesNotExistException,
            NotAFolderException,
            NotAFileException {

        Account account = accountService.getAuthenticatedAccount(principal);
        fileSystemObjectService.copyFileSystemObject(account, parentId, fileSystemObjectId);

        addFolderToModel(model, principal, parentId);
        return "main";
    }

    @RequestMapping(value = "/filesystemobject", method = RequestMethod.DELETE)
    public String deleteFileSystemObject(Model model, Principal principal,
                                         @RequestParam("folderId") long folderId,
                                         @RequestParam("fileSystemObjectId") long fileSystemObjectId) throws
            FileSystemObjectDoesNotExistException,
            IOException,
            NotAFolderException {

        fileSystemObjectService.deleteFileSystemObject(accountService.getAuthenticatedAccount(principal), folderId, fileSystemObjectId);
        addFolderToModel(model, principal, folderId);
        return "main";
    }

    @RequestMapping(value = "/share/retrogram", method = RequestMethod.POST)
    public String postToRetrogram(Model model, Principal principal,
                                  @RequestParam("fileId") long fileId,
                                  @RequestParam("folderId") long folderId) throws
            NotAFileException,
            FileSystemObjectDoesNotExistException,
            IOException {

        // TODO service
        File file = fileSystemObjectService.getFile(accountService.getAuthenticatedAccount(principal), fileId);
        Account account = accountService.getAuthenticatedAccount(principal);

        PostDto post = new PostDto();

        String description = "posted by Dropsi";
        byte[] data = fileService.getByteArrayOfFile(file);
        String fileType = file.getFileExtension();

        post.setDescription(description);
        post.setImageBytes(data);
        post.setImageType(fileType);

        // String retrogramUrl = "http://im-codd:8925/api/post";
        String test = "http://localhost:8922/api/post";

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(test).queryParam("secretToken", account.getRetrogramToken());

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<PostDto> request = new HttpEntity<>(post, headers);
        ResponseEntity<PostDto> responseEntity = restTemplate.exchange(uriComponentsBuilder.toUriString(), HttpMethod.POST, request, PostDto.class);

        String popupString = responseEntity.getStatusCode().equals(HttpStatus.OK) ? "❤ Posted Image to Retrogram" : "❌ Could not post Image to Retrogram";
        model.addAttribute("popupString", popupString);

        addFolderToModel(model, principal, folderId);
        return "main";
    }

    public void addFolderToModel(Model model, Principal principal, long folderId) {
        Optional<FileSystemObject> folderOptional = fileSystemObjectService.getFileSystemObjectById(accountService.getAuthenticatedAccount(principal), folderId);
        model.addAttribute("currentFolder", folderOptional.orElseGet(() -> getRootFolderOfPrincipal(principal)));
        model.addAttribute("account", accountService.getAuthenticatedAccount(principal));
        model.addAttribute("payment", payment);
    }

    private Folder getRootFolderOfPrincipal(Principal principal) {
        return accountService.getAuthenticatedAccount(principal).getRootFolder();
    }

    @ModelAttribute("popupString")
    public String getPopupMessageString() {
        return "";
    }

    // TODO exception handling
}
