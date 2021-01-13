package de.mschoettle.boundary.controller.rest;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.NotAFileException;
import de.mschoettle.control.service.IFileSystemService;
import de.mschoettle.control.utils.PojoConvertUtils;
import de.mschoettle.entity.*;
import de.mschoettle.entity.pojo.FolderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ExternalRestController {

    @Autowired
    private IFileSystemService fileSystemService;

    @RequestMapping(value = "/rootfolder", method = RequestMethod.GET)
    public FolderPojo getRootFolder(@PathParam("secretKey") String secretKey) throws AccountDoesNotExistsException {
        return PojoConvertUtils.castFolder(fileSystemService.getRootFolderBySecretKey(secretKey));
    }

    @RequestMapping(value = "/files/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> getFileResponseEntity(@PathParam("secretKey") String secretKey, @PathVariable long fileId) throws
            AccountDoesNotExistsException,
            IOException,
            FileSystemObjectDoesNotExistException,
            NotAFileException {

        return fileSystemService.getFileResponseEntityBySecretKeyAndId(secretKey, fileId);
    }

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public Folder uploadFile(@RequestBody Account account, @RequestBody File file) {
        return null;
    }

    @ExceptionHandler({AccountDoesNotExistsException.class, IOException.class, FileSystemObjectDoesNotExistException.class})
    public ResponseEntity<String> handleException() {
        return ResponseEntity.status(403).body("Account does not have permission to access fileSystemObject or the fileSystemObject does not exist");
    }

    @ExceptionHandler(NotAFileException.class)
    public ResponseEntity<String> handleNotAFileException() {
        return ResponseEntity.status(403).body("The requested FileSystemObject is not a File");
    }
}
