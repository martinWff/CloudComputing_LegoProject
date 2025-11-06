package cc.srv.db.dataconstructor;

import org.mindrot.jbcrypt.BCrypt;

public class AuthModel {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean verify(String pass) {

        return BCrypt.checkpw(pass,password);
    }

    public static Boolean Verify(String pass,String hashedPass) {

        return BCrypt.checkpw(pass,hashedPass);
    }

}
