package com.example.legoproject.models;

import com.example.legoproject.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class AuctionData {
    private String id;

    private double startingBid;

    private String auctioneer;

    private String product;

    private Instant createdAt;

    private int endsIn;

    private boolean isClosed;

    private BidInfo currentBid;

    public AuctionData() {

    }

    public AuctionData(double startingBid,String auctioneer,String product,int endsIn) {
        id = UUID.randomUUID().toString();
        this.startingBid = startingBid;
        this.auctioneer = auctioneer;
        this.product = product;
        this.createdAt = Instant.now();
        this.endsIn = endsIn;
    }

    public AuctionData(String id,double startingBid,String auctioneer,String product,int endsIn) {
        this.id = id;
        this.startingBid = startingBid;
        this.auctioneer = auctioneer;
        this.product = product;
        this.createdAt = Instant.now();
        this.endsIn = endsIn;
    }

    public AuctionData(String id,double startingBid,String auctioneer,String product,Instant createdAt,int endsIn,BidInfo b,boolean isClosed) {
        this.id = id;
        this.startingBid = startingBid;
        this.auctioneer = auctioneer;
        this.product = product;
        this.createdAt = createdAt;
        this.endsIn = endsIn;
        this.currentBid = b;
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

    public String getAuctioneer() {
        return auctioneer;
    }

    public void setAuctioneer(String auctioneer) {
        this.auctioneer = auctioneer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
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

    public void setEndsIn(int endsAt) {
        this.endsIn = endsAt;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public BidInfo getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(BidInfo currentBid) {
        this.currentBid = currentBid;
    }
}
