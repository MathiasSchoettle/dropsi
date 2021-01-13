package de.mschoettle.control.service;

import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.PermissionDoesNotExistException;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Permission;

import java.security.AccessControlContext;

public interface IPermissionService {

    Permission getPermission(Account ownerOrReceiver, long permissionId) throws PermissionDoesNotExistException;

    void giveAccountPermission(Account account, FileSystemObject fileSystemObject) throws FileSystemObjectDoesNotExistException;

    void savePermission(Permission permission);

    void deletePermission(Permission permission) throws FileSystemObjectDoesNotExistException;
}
