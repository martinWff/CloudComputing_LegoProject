package cc.srv.db.dataconstructor;

import java.time.Instant; //library to get the instant time of the server.
import java.util.UUID; //library to make password hashing

import org.mindrot.jbcrypt.BCrypt;

public class UserModel {

    private String id;
    private String username;
    private String email;
    private String password;
    private String DateOfCreation;
    private String LastUpdate;
    private Boolean status;

    public UserModel() {

    }

    public UserModel(String username, String email, String passwordHash,Boolean status) {

        this.id = UUID.randomUUID().toString(); //creates a random num id to use in the db.
        this.username = username;
        this.email = email.toLowerCase();
        this.password = Hashed(passwordHash);
        this.DateOfCreation = Instant.now().toString(); //gets a current timestamp of the server
        this.LastUpdate = this.DateOfCreation;
        this.status = status;
    }

    private String Hashed(String pass)
    {
        //this does the actual salt of the password
        return BCrypt.hashpw(pass, BCrypt.gensalt(12));
    }

    public static boolean verify(String plainPass, String hashed)
    {
        return BCrypt.checkpw(plainPass, hashed);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordHash) {
        this.password = Hashed(passwordHash);
    }

    public String getDateOfCreation() {
        return DateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.DateOfCreation = dateOfCreation;
    }

    public String getLastUpdate() {
        return LastUpdate;
    }

    public void setLastUpdate() {
        this.LastUpdate = Instant.now().toString();
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
