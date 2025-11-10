package com.example.legoproject.models;

import com.example.legoproject.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

public class Auction {
    private String id;

    private double startingBid;

    private UserProfile auctioneer;

    private Manufactured product;

    private BidInfo currentBid;

    @JsonSerialize(using = TimestampSerializer.class)
    private Instant createdAt;

    private int endsIn;

    private boolean isClosed;

    public Auction() {

    }

    public Auction(String id,double startingBid,UserProfile auctioneer,Manufactured product,Instant createdAt,int endsAt) {
        this.id = id;
        this.startingBid = startingBid;
        this.auctioneer = auctioneer;
        this.product = product;
        this.createdAt = createdAt;
        this.endsIn = endsAt;
    }

    public Auction(String id,double startingBid,UserProfile auctioneer,Manufactured product,Instant createdAt,int endsAt,BidInfo info,boolean isClosed) {
        this.id = id;
        this.startingBid = startingBid;
        this.auctioneer = auctioneer;
        this.product = product;
        this.createdAt = createdAt;
        this.endsIn = endsAt;
        this.currentBid = info;
        this.isClosed = isClosed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getStartingBid() {
        return startingBid;
    }

    public void setStartingBid(double startingBid) {
        this.startingBid = startingBid;
    }

    public UserProfile getAuctioneer() {
        return auctioneer;
    }

    public void setAuctioneer(UserProfile auctioneer) {
        this.auctioneer = auctioneer;
    }

    public Manufactured getProduct() {
        return product;
    }

    public void setProduct(Manufactured product) {
        this.product = product;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public int getEndsIn() {
        return endsIn;
    }

    public void setEndsIn(int endsIn) {
        this.endsIn = endsIn;
    }

    public BidInfo getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(BidInfo currentBid) {
        this.currentBid = currentBid;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
