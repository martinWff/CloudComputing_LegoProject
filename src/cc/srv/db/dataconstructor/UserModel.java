package cc.srv.db.dataconstructor;

import java.time.Instant; //library to get the instant time of the server.
import java.util.UUID; //library to make password hashing

import com.fasterxml.jackson.annotation.JsonView;
import org.mindrot.jbcrypt.BCrypt;

public class UserModel extends UserProfile {

    public interface PublicView {}
    public interface InternalView extends PublicView {}

    @JsonView(InternalView.class)
    private String email;
    @JsonView(InternalView.class)
    private String password;
    @JsonView(InternalView.class)
    private String lastUpdate;
    private boolean isDeleted;

    public UserModel() {

    }

    public UserModel(String uuid,String username, String email, String passwordHash,Instant dateOfCreation,String avatar,Boolean status) {

        super(uuid,username,dateOfCreation,avatar);
        this.email = email;
        this.password = passwordHash;
        this.lastUpdate = dateOfCreation.toString();
        this.isDeleted = status;
    }

    public UserModel(UserModel other) {

        super(other.getId(), other.getUsername(), other.getDateOfCreation(),other.getAvatar());
        this.email = other.email;
        this.password = other.password;
        this.lastUpdate = other.lastUpdate;
        this.isDeleted = other.isDeleted;
    }

    public UserModel(UserProfile profile,String email,String password,Boolean status) {
        super(profile);
        this.email = email;
        this.password = password;
        this.isDeleted = status;
    }

    public UserModel(String username, String email, String passwordHash,Boolean status) {

        super(UUID.randomUUID().toString(),username,Instant.now());
        this.email = email.toLowerCase();
        this.password = Hashed(passwordHash);
        this.lastUpdate = this.getDateOfCreation().toString();
        this.isDeleted = status;
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
