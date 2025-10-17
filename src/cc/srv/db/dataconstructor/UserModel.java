package cc.srv.db.dataconstructor;

import java.time.Instant; //library to get the instant time of the server.

import java.util.UUID;

public class UserModel {

    private String id;
    private String username;
    private String email;
    private String passwordHash;
    private String DateOfCreation;

    public UserModel() {

    }

    public UserModel(String username, String email, String passwordHash) {

        this.id = UUID.randomUUID().toString(); //creates a random num id to use in the db.
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.DateOfCreation = Instant.now().toString(); //gets a current timestamp of the server
    }

    //getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDateOfCreation() {
        return DateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.DateOfCreation = dateOfCreation;
    }

}
