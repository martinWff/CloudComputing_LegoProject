package cc.srv.db.dataconstructor;

import cc.TimestampSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.Timestamp;
import java.time.Instant;

public class UserProfile {
    private String id;

    private String username;

    @JsonSerialize(using = TimestampSerializer.class)
    private Instant dateOfCreation;

    private String avatar;

    public UserProfile() {

    }

    public UserProfile(String id, String username, Instant dateOfCreation) {
        this.id = id;
        this.username = username;
        this.dateOfCreation = dateOfCreation;
    }

    public UserProfile(String id, String username, Instant dateOfCreation,String avatar) {
        this.id = id;
        this.username = username;
        this.dateOfCreation = dateOfCreation;
        this.avatar = avatar;
    }

    public UserProfile(UserProfile other) {
        this.id = other.id;
        this.username = other.username;
        this.dateOfCreation = other.dateOfCreation;
        this.avatar = other.avatar;
    }

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

    public Instant getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Instant dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
