package de.mschoettle.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
public class Account implements UserDetails {

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String email;

    private LocalDate creationDate;

    private String hashedPassword;

    private boolean isVerified;

    @Lob
    private byte[] avatar = new byte[0];

    private String secretKey;

    private String retrogramToken;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Folder rootFolder;

    @OneToMany(mappedBy = "receiver", fetch = FetchType.EAGER)
    private List<Permission> permissions = new ArrayList<>();

    public Account() {
    }

    public Account(String name, String email, String hashedPassword) {
        this.name = name;
        this.email = email;
        this.creationDate = LocalDate.now();
        this.hashedPassword = hashedPassword;
    }

    public void addPermission(Permission permission) {

        if (permissions.contains(permission)) {
            throw new IllegalArgumentException("Account already has permission");
        }

        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public String getAvatarByteEncoded() {
        return Base64.getEncoder().encodeToString(avatar);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return this.id == account.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Folder getRootFolder() {
        return rootFolder;
    }

    public List<Permission> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public void setSecretKey(String securityToken) {
        this.secretKey = securityToken;
    }

    public void setRootFolder(Folder rootFolder) {
        this.rootFolder = rootFolder;
    }

    public String getRetrogramToken() {
        return retrogramToken;
    }

    public void setRetrogramToken(String retrogramToken) {
        this.retrogramToken = retrogramToken;
    }
}
