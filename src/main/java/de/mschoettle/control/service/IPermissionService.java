package de.mschoettle.control.service;

import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Permission;

import java.security.AccessControlContext;

public interface IPermissionService {

    Permission getPermission(Account ownerOrReceiver, long permissionId);

    void giveAccountPermission(Account account, FileSystemObject fileSystemObject);

    void savePermission(Permission permission);

    void deletePermission(Permission permission);
}
