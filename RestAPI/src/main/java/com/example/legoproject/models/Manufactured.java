package com.example.legoproject.models;

import com.example.legoproject.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;
import java.util.UUID;

public class Manufactured {
    private String id;

    private String legoSetId;
    private String owner;

    @JsonSerialize(using = TimestampSerializer.class)
    public Instant createdAt;


    public Manufactured() {

    }

    public Manufactured(LegoSet legoSet,String userId) {
        this.id = UUID.randomUUID().toString();
        this.legoSetId = legoSet.getId();
        this.owner = userId;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLegoSetId() {
        return legoSetId;
    }

    public void setLegoSetId(String legoSetId) {
        this.legoSetId = legoSetId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
