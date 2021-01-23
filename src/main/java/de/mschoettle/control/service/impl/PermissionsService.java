package de.mschoettle.control.service.impl;

import de.mschoettle.control.exception.AccountDoesNotExistsException;
import de.mschoettle.control.exception.FileSystemObjectDoesNotExistException;
import de.mschoettle.control.exception.PermissionDoesNotExistException;
import de.mschoettle.control.service.IAccountService;
import de.mschoettle.control.service.IFileSystemService;
import de.mschoettle.control.service.IPermissionService;
import de.mschoettle.entity.*;
import de.mschoettle.entity.repository.IPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

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

        if (receiver.hasPermission(fileSystemObject)) {
            return;
        }

        Permission permission = new Permission(receiver, provider, fileSystemObject);
        permissionRepository.save(permission);

        fileSystemService.addAccessLogEntry(fileSystemObject, AccessType.PERMISSIONS_GIVEN, "Shared with " + receiver.getName());
        fileSystemService.saveFileSystemObject(fileSystemObject);

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
        fileSystemService.addAccessLogEntry(fileSystemObject, AccessType.PERMISSIONS_GIVEN, "Stopped sharing with " + permission.getReceiver().getName());
        fileSystemService.saveFileSystemObject(fileSystemObject);

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
        FileSystemObject fileSystemObject = fileSystemService.getFileSystemObject(owner, fileSystemObjectId);
        Account receiver = accountService.getAccount(receiverId);
        Optional<Permission> permissionOptional = fileSystemObject.removePermissionByAccount(receiver);

        if(permissionOptional.isPresent()) {
            fileSystemService.addAccessLogEntry(fileSystemObject, AccessType.PERMISSIONS_REVOKED, "Stopped sharing with " + permissionOptional.get().getReceiver().getName());
            permissionRepository.delete(permissionOptional.get());
        }
    }

    @Override
    @Transactional
    public void deletePermissionsOfFileSystemObject(Account account, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException {
        FileSystemObject fileSystemObject = fileSystemService.getFileSystemObject(account, fileSystemObjectId);
        fileSystemService.addAccessLogEntry(fileSystemObject, AccessType.PERMISSIONS_GIVEN, "Stopped sharing with all users");
        permissionRepository.deleteAllByShared(fileSystemObject);
        fileSystemObject.setPermissions(new ArrayList<>());
        fileSystemService.saveFileSystemObject(fileSystemObject);
    }
}
