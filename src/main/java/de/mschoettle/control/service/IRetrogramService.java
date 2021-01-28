package de.mschoettle.control.service;

import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.NotAFileException;
import de.mschoettle.entity.Account;

import java.io.IOException;

public interface IRetrogramService {

    /**
     * Posts the File of the given FileId to the retrogram account of the linked account object
     */
    boolean postImageToRetrogram(Account account, long fileId) throws IOException, NotAFileException, FileSystemObjectDoesNotExistException;
}
