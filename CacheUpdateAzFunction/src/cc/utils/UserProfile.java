package cc.utils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

public class UserProfile {

    private String id;

    private String username;

    @JsonSerialize(using = TimestampSerializer.class)
    private Instant dateOfCreation;

    private MediaDataDTO avatar;
    private int power;

    public UserProfile() {

    }

    public UserProfile(String id, String username, Instant dateOfCreation,int power) {
        this.id = id;
        this.username = username;
        this.dateOfCreation = dateOfCreation;
        this.power = power;
    }

    public UserProfile(String id, String username, Instant dateOfCreation, MediaDataDTO avatar,int power) {
        this.id = id;
        this.username = username;
        this.dateOfCreation = dateOfCreation;
        this.avatar = avatar;
        this.power = power;
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

    public MediaDataDTO getAvatar() {
        return avatar;
    }

    public void setAvatar(MediaDataDTO avatar) {
        this.avatar = avatar;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

}
