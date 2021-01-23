package de.mschoettle.boundary.controller.rest;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.NotAFileException;
import de.mschoettle.control.exception.NotAFolderException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemService;
import de.mschoettle.control.utils.DTOConvertUtils;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.dto.FolderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ExternalRestController {

    @Autowired
    private IFileSystemService fileSystemService;

    @Autowired
    private IAccountService accountService;

    @RequestMapping(value = "/validateSecretKey", method = RequestMethod.GET)
    public boolean accountExists(@PathParam("secretKey") String secretKey) throws AccountDoesNotExistsException {
        return accountService.accountWithSecretKeyExists(secretKey);
    }

    @RequestMapping(value = "/rootfolder", method = RequestMethod.GET)
    public FolderDTO getRootFolder(@PathParam("secretKey") String secretKey) throws AccountDoesNotExistsException {
        return DTOConvertUtils.castFolder(fileSystemService.getRootFolderBySecretKey(secretKey));
    }

    @RequestMapping(value = "/files/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> getFileResponseEntity(@PathParam("secretKey") String secretKey, @PathVariable long fileId) throws
            AccountDoesNotExistsException,
            IOException,
            FileSystemObjectDoesNotExistException,
            NotAFileException {

        return fileSystemService.getFileResponseEntityBySecretKeyAndId(secretKey, fileId);
    }

    @RequestMapping(value = "/file", consumes = "multipart/form-data", method = RequestMethod.POST)
    public void uploadFile(@RequestParam String secretKey, @RequestParam long folderId, @RequestBody MultipartFile file) throws
            AccountDoesNotExistsException,
            NotAFolderException,
            IOException,
            FileSystemObjectDoesNotExistException {

        Account a = accountService.getAccountBySecretKey(secretKey);
        fileSystemService.addMultipartFileToFolder(a, folderId, file);
    }

    @ExceptionHandler({AccountDoesNotExistsException.class, IOException.class, FileSystemObjectDoesNotExistException.class, NotAFileException.class, NotAFolderException.class})
    public ResponseEntity<String> handleException(Exception e) {

        ResponseEntity.BodyBuilder bb = ResponseEntity.status(403);

        if(e instanceof AccountDoesNotExistsException) {
            return bb.body("No Account exists with given secret Key");
        }
        else if(e instanceof FileSystemObjectDoesNotExistException) {
            return bb.body("The given FileSystemObject does not exist");
        }
        else if(e instanceof NotAFileException) {
            return bb.body("The given FileSystemObjectId was not a file");
        }
        else if(e instanceof NotAFolderException) {
            return bb.body("The given FileSystemObjectId was not a folder");
        }

        return ResponseEntity.status(500).body("A problem occurred internally");
    }
}
