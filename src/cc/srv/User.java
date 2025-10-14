package cc.srv;

public class User {
    private int userId;
    private String nickname;

    private String photo;

    public User() {

    }

    public User(int userId,String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setNickname(String n) {
        this.nickname = n;
    }

    public String getNickname() {
        return nickname;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

}
