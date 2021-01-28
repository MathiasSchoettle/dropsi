package de.mschoettle.boundary.controller.rest;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.NotAFileException;
import de.mschoettle.control.exception.NotAFolderException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.utils.dto.DTOConvertUtils;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.dto.FolderDTO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Scope("singleton")
@RequestMapping("/api")
public class ExternalRestController {

    private IFileSystemObjectService fileSystemObjectService;

    private IAccountService accountService;

    private Logger logger;

    @Autowired
    public void setInjectedBean(IAccountService accountService,
                                IFileSystemObjectService fileSystemObjectService,
                                Logger logger) {
        this.accountService = accountService;
        this.fileSystemObjectService = fileSystemObjectService;
        this.logger = logger;
    }

    @RequestMapping(value = "/rootfolder", method = RequestMethod.GET)
    public FolderDTO getRootFolder(@RequestParam("secretKey") String secretKey) throws AccountDoesNotExistsException {
        logger.info("secretKey: " + secretKey + " requested Rootfolder");
        return DTOConvertUtils.castFolder(fileSystemObjectService.getRootFolderBySecretKey(secretKey));
    }

    @RequestMapping(value = "/files/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> getFileResponseEntity(@RequestParam("secretKey") String secretKey, @PathVariable long fileId) throws
            AccountDoesNotExistsException,
            IOException,
            FileSystemObjectDoesNotExistException,
            NotAFileException {

        logger.info("secretKey: " + secretKey + " requested File: " + fileId);
        return fileSystemObjectService.getFileResponseEntityBySecretKeyAndId(secretKey, fileId);
    }

    @RequestMapping(value = "/file", consumes = "multipart/form-data", method = RequestMethod.POST)
    public void uploadFile(@RequestParam String secretKey, @RequestParam long folderId, @RequestBody MultipartFile file) throws
            AccountDoesNotExistsException,
            NotAFolderException,
            IOException,
            FileSystemObjectDoesNotExistException {

        logger.info("secretKey: " + secretKey + " requested upload to: " + folderId);
        Account a = accountService.getAccountBySecretKey(secretKey);
        fileSystemObjectService.addMultipartFileToFolder(a, folderId, file);
    }

    @ExceptionHandler({AccountDoesNotExistsException.class, IOException.class, FileSystemObjectDoesNotExistException.class, NotAFileException.class, NotAFolderException.class})
    public ResponseEntity<String> handleException(Exception e) {

        ResponseEntity.BodyBuilder bb = ResponseEntity.status(HttpStatus.NOT_FOUND);

        if (e instanceof AccountDoesNotExistsException) {
            return bb.body("No Account exists with given secret Key");
        } else if (e instanceof FileSystemObjectDoesNotExistException) {
            return bb.body("The given FileSystemObject does not exist");
        } else if (e instanceof NotAFileException) {
            return bb.body("The given FileSystemObjectId was not a file");
        } else if (e instanceof NotAFolderException) {
            return bb.body("The given FileSystemObjectId was not a folder");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A problem occurred internally");
    }
}
