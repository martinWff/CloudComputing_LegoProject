package com.example.legoproject.models;

import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.UUID;

public class User extends UserProfile {

    private String email;
    private String password;
    private String lastUpdate;
    private boolean isDeleted;

    public User() {

    }

    public User(String uuid, String username, String email,String passwordHash, Instant dateOfCreation, MediaDataDTO avatar, Boolean status, int power) {

        super(uuid, username, dateOfCreation, avatar,power);
        this.email = email;
        this.password = passwordHash;
        this.lastUpdate = dateOfCreation.toString();
        this.isDeleted = status;
    }

    public User(User other) {

        super(other.getId(), other.getUsername(), other.getDateOfCreation(), other.getAvatar(),other.getPower());
        this.email = other.email;
        this.password = other.password;
        this.lastUpdate = other.lastUpdate;
        this.isDeleted = other.isDeleted;
    }

    public User(UserProfile profile, String email, String password, Boolean status) {
        super(profile);
        this.email = email;
        this.password = password;
        this.isDeleted = status;
    }

    public User(String username, String email, String passwordHash, Boolean status, int power) {

        super(UUID.randomUUID().toString(), username, Instant.now(),power);
        this.email = email.toLowerCase();
        this.password = Hashed(passwordHash);
        this.lastUpdate = this.getDateOfCreation().toString();
        this.isDeleted = status;
    }

    public static boolean verify(String plainPass, String hashed) {
        return BCrypt.checkpw(plainPass, hashed);
    }

    public static String Hashed(String pass) {
        //this does the actual salt of the password
        return BCrypt.hashpw(pass, BCrypt.gensalt(12));
    }

    //getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordHash) {
        this.password = passwordHash;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate() {
        this.lastUpdate = Instant.now().toString();
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
