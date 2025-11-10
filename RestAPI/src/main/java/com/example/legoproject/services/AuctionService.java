package com.example.legoproject.services;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.example.legoproject.models.AuctionData;
import com.example.legoproject.models.LegoSet;
import com.example.legoproject.models.User;
import com.example.legoproject.models.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuctionService {

    private final UserService userService;
    private final CosmosContainer container;

    @Autowired
    public AuctionService(UserService userService, CosmosDatabase db) {
        this.userService = userService;
        this.container = db.getContainer("Auctions");
    }


    public AuctionData createAuction(String auctioneer, double startingBid, LegoSet product, Instant endsAt) {

        AuctionData auctionData = new AuctionData(startingBid,auctioneer,product.getId(),endsAt);

        container.createItem(auctionData);

        return auctionData;
    }

}
