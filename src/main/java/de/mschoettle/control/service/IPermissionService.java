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

    /**
     * @param ownerOrReceiver Either the owning or the receiving {@link Account}
     * @param permissionId    the id of the {@link Permission}
     * @return The corresponding Permission
     * @throws PermissionDoesNotExistException If the requested Permission does not exist
     */
    Permission getPermission(Account ownerOrReceiver, long permissionId) throws PermissionDoesNotExistException;

    /**
     * Gives the receiver a {@link Permission} for the given {@link FileSystemObject}
     * @param receiver The receiving {@link Account}
     * @param provider The {@link Account} which owns the given {@link FileSystemObject}
     * @param fileSystemObject The id of the to be shared {@link FileSystemObject}
     * @throws FileSystemObjectDoesNotExistException if the requested {@link FileSystemObject} does not exist
     */
    void giveAccountPermission(Account receiver, Account provider, FileSystemObject fileSystemObject) throws FileSystemObjectDoesNotExistException;

    /**
     * @param permission The {@link Permission} to be saved
     */
    void savePermission(Permission permission);

    /**
     * @param permission The {@link Permission} to be deleted
     */
    void deletePermission(Permission permission) throws FileSystemObjectDoesNotExistException;

    /**
     * @param provider The {@link Account} for which the Map is requested
     * @return This returns a Map of {@link FileSystemObject} keys which have a List of accounts the object is shared with
     */
    Map<FileSystemObject, List<Account>> getPermissionsGivenMap(Account provider);

    /**
     * Deletes the permission of the given {@link Account} and {@link FileSystemObject}
     * @param owner The owner of the {@link FileSystemObject}
     * @param receiverId The receiving {@link Account}
     * @param fileSystemObjectId The corresponding {@link FileSystemObject}
     * @throws FileSystemObjectDoesNotExistException if the {@link FileSystemObject} does note exist
     * @throws AccountDoesNotExistsException if an {@link Account} does not exist
     */
    void deletePermissionOfAccount(Account owner, long receiverId, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException, AccountDoesNotExistsException;

    /**
     * Deletes all permissions of a {@link FileSystemObject}
     *
     * @param account The owner of the {@link FileSystemObject}
     * @param fileSystemObjectId The corresponding {@link FileSystemObject}
     * @throws FileSystemObjectDoesNotExistException if the {@link FileSystemObject} does not exist
     */
    void deletePermissionsOfFileSystemObject(Account account, long fileSystemObjectId) throws FileSystemObjectDoesNotExistException;

    /**
     * @return A Map of Lists of {@link Permission}s mapped by the owner {@link Account}
     */
    Map<Account, List<Permission>> getPermissionMap(Account account);
}
