package com.example.legoproject.models;

import com.example.legoproject.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

public class BidInfo {
    private double value;
    private String userId;

    @JsonSerialize(using = TimestampSerializer.class)
    private Instant timestamp;

    public BidInfo() {

    }

    public BidInfo(double v,String userId) {
        this.value = v;
        this.userId = userId;
        this.timestamp = Instant.now();
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
