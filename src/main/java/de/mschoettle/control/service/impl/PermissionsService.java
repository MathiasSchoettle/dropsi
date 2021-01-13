package de.mschoettle.control.service.impl;

import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.PermissionDoesNotExistException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemService;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.AccessType;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Permission;
import de.mschoettle.entity.repository.IPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PermissionsService implements IPermissionService {

    @Autowired
    private IPermissionRepository permissionRepository;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IFileSystemService fileSystemService;

    @Override
    public Permission getPermission(Account ownerOrReceiver, long permissionId) throws PermissionDoesNotExistException {

        Permission permission = permissionRepository.findById(permissionId).orElseThrow(
                () -> new PermissionDoesNotExistException(permissionId));

        if(!permission.getReceiver().equals(ownerOrReceiver) && !permission.getShared().getOwner().equals(ownerOrReceiver)) {
            throw new IllegalArgumentException("Given Account " + ownerOrReceiver + " has no connection to permission");
        }

        return permission;
    }

    @Override
    @Transactional
    public void giveAccountPermission(Account account, FileSystemObject fileSystemObject) throws FileSystemObjectDoesNotExistException {

        if(fileSystemObject.getOwner().equals(account)) {
            throw new IllegalArgumentException("Account " + account + " owns FileSystemObject " + fileSystemObject.getId());
        }

        if(account.hasPermission(fileSystemObject)) {
            return;
        }

        Permission permission = new Permission(account, fileSystemObject);
        permissionRepository.save(permission);

        fileSystemService.addAccessLogEntry(fileSystemObject, AccessType.PERMISSIONS_GIVEN, "Shared with " + account.getName());
        fileSystemService.saveFileSystemObject(fileSystemObject);

        account.addPermission(permission);
        accountService.saveAccount(account);
    }

    @Override
    public void savePermission(Permission permission) {

        if(permission == null) {
            throw new IllegalArgumentException("Permission was null");
        }

        permissionRepository.save(permission);
    }

    @Override
    public void deletePermission(Permission permission) throws FileSystemObjectDoesNotExistException {

        if(permission == null) {
            throw new IllegalArgumentException("Permission was null");
        }

        FileSystemObject fileSystemObject = permission.getShared();
        fileSystemService.addAccessLogEntry(fileSystemObject, AccessType.PERMISSIONS_GIVEN, "Stopped sharing with " + permission.getReceiver().getName());
        fileSystemService.saveFileSystemObject(fileSystemObject);

        permissionRepository.delete(permission);
    }
}
