package de.mschoettle.control.service.impl;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.PermissionDoesNotExistException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemObjectService;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.AccessType;
import de.mschoettle.entity.Account;
import de.mschoettle.entity.FileSystemObject;
import de.mschoettle.entity.Permission;
import de.mschoettle.entity.repository.IPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope("singleton")
public class PermissionsService implements IPermissionService {

    private IFileSystemObjectService fileSystemObjectService;

    private IAccountService accountService;

    private IPermissionRepository permissionRepository;

    @Autowired
    public void setInjectedBean(IFileSystemObjectService fileSystemObjectService,
                                IAccountService accountService,
                                IPermissionRepository permissionRepository ) {

        this.fileSystemObjectService = fileSystemObjectService;
        this.accountService = accountService;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Permission getPermission(Account ownerOrReceiver, long permissionId) throws PermissionDoesNotExistException {

        Permission permission = permissionRepository.findById(permissionId).orElseThrow(
                () -> new PermissionDoesNotExistException(permissionId));

        if (!permission.getReceiver().equals(ownerOrReceiver) && !permission.getShared().getOwner().equals(ownerOrReceiver)) {
            throw new IllegalArgumentException("Given Account " + ownerOrReceiver + " has no connection to permission");
        }

        return permission;
    }

    @Override
    @Transactional
    public void giveAccountPermission(Account receiver, Account provider, FileSystemObject fileSystemObject) throws FileSystemObjectDoesNotExistException {

        if (fileSystemObject.getOwner().equals(receiver)) {
            throw new IllegalArgumentException("Account " + receiver + " owns FileSystemObject " + fileSystemObject.getId());
        }

        if (accountService.accountHasPermission(receiver, fileSystemObject)) {
            return;
        }

        Permission permission = new Permission(receiver, provider, fileSystemObject);
        permissionRepository.save(permission);

        fileSystemObjectService.addAccessLogEntry(fileSystemObject, AccessType.PERMISSIONS_GIVEN, "Shared with " + receiver.getName());
        fileSystemObjectService.saveFileSystemObject(fileSystemObject);

        receiver.addPermission(permission);
        accountService.saveAccount(receiver);
    }

    @Override
    public void savePermission(Permission permission) {

        if (permission == null) {
            throw new IllegalArgumentException("Permission was null");
        }

        permissionRepository.save(permission);
    }

    @Override
    public void deletePermission(Permission permission) throws FileSystemObjectDoesNotExistException {

        if (permission == null) {
            throw new IllegalArgumentException("Permission was null");
        }

        FileSystemObject fileSystemObject = permission.getShared();
        fileSystemObjectService.addAccessLogEntry(fileSystemObject, AccessType.PERMISSIONS_GIVEN, "Stopped sharing with " + permission.getReceiver().getName());
        fileSystemObject.removePermission(permission);
        fileSystemObjectService.saveFileSystemObject(fileSystemObject);
        permissionRepository.delete(permission);
    }

    @Override
    public Map<FileSystemObject, List<Account>> getPermissionsGivenMap(Account provider) {

        if (provider == null) {
            throw new IllegalArgumentException("Account is null");
        }

        List<Permission> permissions = permissionRepository.findAllByProvider(provider);
        Map<FileSystemObject, List<Account>> permissionMap = new HashMap<>();

        for (Permission p : permissions) {
            if (permissionMap.containsKey(p.getShared())) {
                permissionMap.get(p.getShared()).add(p.getReceiver());
            } else {
                List<Account> accounts = new ArrayList<>();
                accounts.add(p.getReceiver());
                permissionMap.put(p.getShared(), accounts);
            }
        }

        return permissionMap;
    }

    @Override
    @Transactional
    public void deletePermissionOfAccount(Account owner, long receiverId, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException, AccountDoesNotExistsException {
        FileSystemObject fileSystemObject = fileSystemObjectService.getFileSystemObject(owner, fileSystemObjectId);
        Account receiver = accountService.getAccount(receiverId);

        for (Permission p : fileSystemObject.getPermissions()) {
            if (p.getReceiver().equals(receiver)) {
                deletePermission(p);
                break;
            }
        }
    }

    @Override
    @Transactional
    public void deletePermissionsOfFileSystemObject(Account account, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException {
        FileSystemObject fileSystemObject = fileSystemObjectService.getFileSystemObject(account, fileSystemObjectId);
        fileSystemObjectService.addAccessLogEntry(fileSystemObject, AccessType.PERMISSIONS_GIVEN, "Stopped sharing with all users");
        permissionRepository.deleteAllByShared(fileSystemObject);
        fileSystemObjectService.saveFileSystemObject(fileSystemObject);
    }

    @Override
    public Map<Account, List<Permission>> getPermissionMap(Account account) {

        Map<Account, List<Permission>> permissionMap = new HashMap<>();

        for (Permission p : account.getPermissions()) {
            if (permissionMap.containsKey(p.getShared().getOwner())) {
                permissionMap.get(p.getShared().getOwner()).add(p);
            } else {
                List<Permission> temp = new ArrayList<>();
                temp.add(p);
                permissionMap.put(p.getShared().getOwner(), temp);
            }
        }

        return permissionMap;
    }
}
