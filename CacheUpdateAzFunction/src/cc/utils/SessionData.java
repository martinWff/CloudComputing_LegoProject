package cc.utils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

public class SessionData {

    private String id;

    private String user;

    private UserProfile profile;

    @JsonSerialize(using = TimestampSerializer.class)
    private Instant createdAt;

    public SessionData() {

    }

    public SessionData(String id,UserProfile profile) {
        this.id = id;
        this.user = profile.getId();
        this.profile = profile;
        this.createdAt = Instant.now();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }
}
