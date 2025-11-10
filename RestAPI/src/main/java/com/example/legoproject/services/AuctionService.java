package com.example.legoproject.services;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.example.legoproject.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

@Service
public class AuctionService {

    private final UserService userService;
    private final CosmosContainer container;

    private final LegoSetService legoSetService;

    private final JedisPool jedisPool;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    public AuctionService(UserService userService,LegoSetService legoSetService, CosmosDatabase db,JedisPool pool) {
        this.userService = userService;
        this.container = db.getContainer("Auctions");
        this.legoSetService = legoSetService;
        this.jedisPool = pool;
    }


    public Auction createAuction(UserProfile auctioneer, double startingBid, ManufacturedData product, int endsIn) {

        LegoSet legoSet = legoSetService.getLegoSet(product.getLegoSetId());

        Manufactured manufactured = new Manufactured(product.getId(),legoSet,auctioneer,product.getCreatedAt());

        AuctionData auctionData = new AuctionData(startingBid,auctioneer.getId(),product.getId(),endsIn);

        Auction auction = new Auction(auctionData.getId(),auctionData.getStartingBid(),auctioneer,manufactured,auctionData.getCreatedAt(),endsIn);

        container.createItem(auctionData);

        return auction;
    }

    public AuctionData getAuctionDataByProduct(String id) {

        SqlQuerySpec spec = new SqlQuerySpec("SELECT * FROM c WHERE c.product = @id AND c.isClosed = false", Arrays.asList(new SqlParameter("@id",id)));
        CosmosPagedIterable<AuctionData> iterable = container.queryItems(spec,new CosmosQueryRequestOptions(),AuctionData.class);

        if (iterable.iterator().hasNext()) {

            return iterable.iterator().next();
        }

        return null;
    }

    public AuctionData getAuctionDataById(String id) {

        Auction cachedAuction = null;
        try (Jedis jedis = jedisPool.getResource()) {

            String c = jedis.get("auction:"+id);
            try {
                if (c !=null) {
                    cachedAuction = objectMapper.readValue(c, Auction.class);
                }
            } catch (JsonProcessingException e) {
                System.err.println(e);
            }

        }

        if (cachedAuction != null)
        {
            return new AuctionData(cachedAuction.getId(),cachedAuction.getStartingBid(),cachedAuction.getAuctioneer().getId(),cachedAuction.getProduct().getId(),cachedAuction.getCreatedAt(), cachedAuction.getEndsIn(),cachedAuction.getCurrentBid(),cachedAuction.isClosed());
        }


        SqlQuerySpec spec = new SqlQuerySpec("SELECT * FROM c WHERE c.id = @id", Arrays.asList(new SqlParameter("@id",id)));
        CosmosPagedIterable<AuctionData> iterable = container.queryItems(spec,new CosmosQueryRequestOptions(),AuctionData.class);

        if (iterable.iterator().hasNext()) {

            AuctionData result = iterable.iterator().next();


            try (Jedis jedis = jedisPool.getResource()) {

                try {

                    Auction ad = new Auction(result.getId(),result.getStartingBid(),userService.getUser(result.getAuctioneer()),legoSetService.getManufactured(result.getProduct()),result.getCreatedAt(), result.getEndsIn(),result.getCurrentBid(),result.isClosed());

                    jedis.setex("auction:"+id,18000, objectMapper.writeValueAsString(ad));
                } catch (JsonProcessingException e) {
                    System.err.println(e);
                }

            }

            return result;
        }

        return null;

    }

    public Auction getAuctionById(String id) {

        Auction cachedAuction = null;
        try (Jedis jedis = jedisPool.getResource()) {

            String c = jedis.get("auction:"+id);
            try {
                if (c !=null) {
                    cachedAuction = objectMapper.readValue(c, Auction.class);
                }
            } catch (JsonProcessingException e) {
                System.err.println(e);
            }

        }

        if (cachedAuction != null)
        {
            return cachedAuction;
        }


        SqlQuerySpec spec = new SqlQuerySpec("SELECT * FROM c WHERE c.id = @id", Arrays.asList(new SqlParameter("@id",id)));
        CosmosPagedIterable<AuctionData> iterable = container.queryItems(spec,new CosmosQueryRequestOptions(),AuctionData.class);

        if (iterable.iterator().hasNext()) {

            AuctionData ad = iterable.iterator().next();

            try (Jedis jedis = jedisPool.getResource()) {

                try {
                    jedis.setex("auction:"+id,18000,objectMapper.writeValueAsString(ad));
                } catch (JsonProcessingException e) {
                    System.err.println(e);
                }

            }

            return new Auction(ad.getId(),ad.getStartingBid(),userService.getUser(ad.getAuctioneer()),legoSetService.getManufactured(ad.getProduct()),ad.getCreatedAt(),ad.getEndsIn(),ad.getCurrentBid(),ad.isClosed());

        }

        return null;

    }

    public AuctionData updateBid(AuctionData auctionData,BidInfo bidInfo) {

        return updateBidValues(auctionData.getId(),auctionData.getProduct(),bidInfo);
    }

    public AuctionData updateBid(Auction auctionData,BidInfo bidInfo) {

        return updateBidValues(auctionData.getId(),auctionData.getProduct().getId(),bidInfo);
    }

    private AuctionData updateBidValues(String auctionId,String productId,BidInfo info)
    {
        CosmosPatchItemRequestOptions opt = new CosmosPatchItemRequestOptions();
        opt.setContentResponseOnWriteEnabled(true);

        CosmosPatchOperations op = CosmosPatchOperations.create();

        op.replace("/currentBid",info);

        CosmosItemResponse<AuctionData> a= container.patchItem(auctionId,new PartitionKey(productId),op,opt,AuctionData.class);

        AuctionData item = a.getItem();

        if (item != null) {
            try (Jedis jedis = jedisPool.getResource()) {

                try {
                    jedis.setex("auction:"+auctionId,3600 ,objectMapper.writeValueAsString(new Auction(item.getId(),item.getStartingBid(),userService.getUser(item.getAuctioneer()),legoSetService.getManufactured(item.getProduct()),item.getCreatedAt(),item.getEndsIn(),item.getCurrentBid(),item.isClosed())));
                } catch (JsonProcessingException e) {
                    System.err.println(e);
                }

            }
        }

        return item;
    }

}
