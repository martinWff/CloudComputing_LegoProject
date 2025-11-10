package com.example.legoproject.models;

import com.example.legoproject.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

public class Auction {
    private String id;

    private double startingBid;

    private User auctioneer;

    private LegoSet product;

    private Instant createdAt;

    @JsonSerialize(using = TimestampSerializer.class)
    private Instant endsAt;
}
