package de.mschoettle.control.service.impl;

import com.othr.jonasrombach.retrogram.dto.PostDto;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.NotAFileException;
import de.mschoettle.control.service.IFileService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.control.service.IRetrogramService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.File;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * RetrogramService belongs in boundary as it communicates with the interface of the hamilton bank
 */
@Service
@Scope("singleton")
public class RetrogramService implements IRetrogramService {

    private IFileSystemObjectService fileSystemObjectService;

    private IFileService fileService;

    private RestTemplate restTemplate;

    private Logger logger;

    @Value("${appconfig.retrogram.url}")
    private String url;

    @Value("${appconfig.retrogram.description}")
    private String description;

    @Autowired
    public void setInjectedBean(IFileSystemObjectService fileSystemObjectService,
                                @Qualifier("local") IFileService fileService,
                                Logger logger,
                                RestTemplate restTemplate) {

        this.fileSystemObjectService = fileSystemObjectService;
        this.fileService = fileService;
        this.logger = logger;
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean postImageToRetrogram(Account account, long fileId) throws IOException, NotAFileException, FileSystemObjectDoesNotExistException {

        File file = fileSystemObjectService.getFile(account, fileId);

        PostDto post = new PostDto();
        post.setDescription(description);
        post.setImageBytes(fileService.getByteArrayOfFile(file));
        post.setImageType(file.getFileExtension());

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url + "/api/post").queryParam("secretToken", account.getRetrogramToken());
        RequestEntity<PostDto> requestEntity = RequestEntity.post(uriComponentsBuilder.toUriString()).body(post);

        try {
            ResponseEntity<PostDto> responseEntity = restTemplate.exchange(requestEntity, PostDto.class);
            logger.info("posting image to retrogram: " + responseEntity.getStatusCode());
            return responseEntity.getStatusCode().equals(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception " + e.getMessage() + " while connecting to retrogram");
        }

        return false;
    }
}
