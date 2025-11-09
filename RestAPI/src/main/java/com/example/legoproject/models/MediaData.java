package com.example.legoproject.models;

import com.example.legoproject.TimestampSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;
import java.util.UUID;

public class MediaData {
    private String id;

    private String name;

    private String file;

    private String mediaType;

    private String owner;

    @JsonSerialize(using = TimestampSerializer.class)
    private Instant createdAt;

    public MediaData() {

    }

    public MediaData(String name,String file,String mediaType,String owner) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.file = file;
        this.mediaType = mediaType;
        this.owner = owner;
        this.createdAt = Instant.now();
    }


    public MediaData(String id,String name,String file,String mediaType,String owner,Instant createdAt) {
        this.id = id;
        this.name = name;
        this.file = file;
        this.mediaType = mediaType;
        this.owner = owner;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
