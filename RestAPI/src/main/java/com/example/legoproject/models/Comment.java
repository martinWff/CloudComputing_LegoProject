package com.example.legoproject.models;

import com.example.legoproject.TimestampSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

public class Comment {

    private String id;

    private UserProfile author;

    private String content;

    @JsonSerialize(using = TimestampSerializer.class)
    private Instant timestamp;

    public Comment() {

    }
    public Comment(String id,UserProfile author,String content,Instant ts) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.timestamp = ts;
    }

    public Comment(String id,UserProfile author,String content) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.timestamp = Instant.now();
    }

    public UserProfile getAuthor() {
        return this.author;
    }

    public void setAuthor(UserProfile a) {
        this.author = a;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String c) {
        this.content = c;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant t) {
        this.timestamp = t;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
