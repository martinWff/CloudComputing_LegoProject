package com.example.legoproject.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

public class AuctionData {
    private String id;

    private double startingBid;

    private String auctioneer;

    private String product;

    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Instant endsAt;

    public AuctionData() {

    }

    public AuctionData(double startingBid,String auctioneer,String product,Instant endsAt) {
        id = UUID.randomUUID().toString();
        this.startingBid = startingBid;
        this.auctioneer = auctioneer;
        this.product = product;
        this.createdAt = Instant.now();
        this.endsAt = endsAt;
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

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }
}
