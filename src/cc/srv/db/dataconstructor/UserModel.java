package cc.srv.db.dataconstructor;

import java.time.Instant; //library to get the instant time of the server.
import java.util.UUID; //library to make password hashing

import com.fasterxml.jackson.annotation.JsonView;
import org.mindrot.jbcrypt.BCrypt;

public class UserModel {

    public interface PublicView {}
    public interface InternalView extends PublicView {}


    private String id;

    private String username;

    @JsonView(InternalView.class)
    private String email;
    @JsonView(InternalView.class)
    private String password;
    private String DateOfCreation;
    @JsonView(InternalView.class)
    private String lastUpdate;
    private Boolean status;

    public UserModel() {

    }

    public UserModel(String uuid,String username, String email, String passwordHash,String dateOfCreation,Boolean status) {

        this.id = uuid; //creates a random num id to use in the db.
        this.username = username;
        this.email = email;
        this.password = passwordHash;
        this.DateOfCreation = dateOfCreation; //gets a current timestamp of the server
        this.lastUpdate = this.DateOfCreation;
        this.status = status;
    }

    public UserModel(UserModel other) {

        this.id = other.id; //creates a random num id to use in the db.
        this.username = other.username;
        this.email = other.email;
        this.password = other.password;
        this.DateOfCreation = other.DateOfCreation; //gets a current timestamp of the server
        this.lastUpdate = other.lastUpdate;
        this.status = other.status;
    }

    public UserModel(String username, String email, String passwordHash,Boolean status) {

        this.id = UUID.randomUUID().toString(); //creates a random num id to use in the db.
        this.username = username;
        this.email = email.toLowerCase();
        this.password = Hashed(passwordHash);
        this.DateOfCreation = Instant.now().toString(); //gets a current timestamp of the server
        this.lastUpdate = this.DateOfCreation;
        this.status = status;
    }

    public static boolean verify(String plainPass, String hashed)
    {
        return BCrypt.checkpw(plainPass, hashed);
    }

    public static String Hashed(String pass)
    {
        //this does the actual salt of the password
        return BCrypt.hashpw(pass, BCrypt.gensalt(12));
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
        this.password = passwordHash;
    }

    public String getDateOfCreation() {
        return DateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.DateOfCreation = dateOfCreation;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate() {
        this.lastUpdate = Instant.now().toString();
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
