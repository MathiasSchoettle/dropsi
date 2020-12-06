package de.mschoettle.control.service.i;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Folder;

public interface IInternalFileSystemService extends IFileSystemService {

    public void saveFileSystemObject(FileSystemObject fileSystemObject);

    public void giveAccountRootFolder(Account account);
}
