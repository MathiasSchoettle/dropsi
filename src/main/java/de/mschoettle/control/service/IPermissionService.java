package de.mschoettle.control.service;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.PermissionDoesNotExistException;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Permission;

import java.util.List;
import java.util.Map;

public interface IPermissionService {

    Permission getPermission(Account ownerOrReceiver, long permissionId) throws PermissionDoesNotExistException;

    void giveAccountPermission(Account receiver, Account provider, FileSystemObject fileSystemObject) throws FileSystemObjectDoesNotExistException;

    void savePermission(Permission permission);

    void deletePermission(Permission permission) throws FileSystemObjectDoesNotExistException;

    Map<FileSystemObject, List<Account>> getPermissionsGivenMap(Account provider);

    void deletePermissionOfAccount(Account owner, long receiverId, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException, AccountDoesNotExistsException;

    void deletePermissionsOfFileSystemObject(Account account, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException;
}
