package de.mschoettle.control.service;

import de.mschoettle.control.service.i.IInternalFileSystemService;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.File;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;
import de.mschoettle.entity.repository.IFileSystemObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InternalFileSystemService implements IInternalFileSystemService {

    @Autowired
    private IFileSystemObjectRepository fileSystemObjectRepository;

    public void saveFileSystemObject(FileSystemObject fileSystemObject) {
        fileSystemObjectRepository.save(fileSystemObject);
    }

    @Override
    public void giveAccountRootFolder(Account account) {

        if(account == null) {
            throw new IllegalArgumentException("account is null");
        }

        if(account.getRootFolder() != null) {
            throw new IllegalArgumentException("account already has a rootFolder");
        }

        Folder rootFolder = new Folder("root", 0, account, null);
        fileSystemObjectRepository.save(rootFolder);
        account.setRootFolder(rootFolder);
    }


    @Override
    public FileSystemObject getFileSystemObjectById(long id, Account account) {
        return  fileSystemObjectRepository.findByIdAndOwner(id, account);
    }
}
