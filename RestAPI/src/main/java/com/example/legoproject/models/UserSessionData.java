package com.example.legoproject.models;

public class UserSessionData {

    public UserProfile profile;

    public String session;

    public UserSessionData(UserProfile p,String s) {
        this.profile = p;
        this.session = s;
    }
}
