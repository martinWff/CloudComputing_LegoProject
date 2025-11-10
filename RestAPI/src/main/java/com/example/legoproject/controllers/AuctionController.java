package com.example.legoproject.controllers;

import com.example.legoproject.models.*;
import com.example.legoproject.services.AuctionService;
import com.example.legoproject.services.LegoSetService;
import com.example.legoproject.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auction")
public class AuctionController {


    private final AuctionService auctionService;
    private final UserService userService;

    private final LegoSetService legoSetService;


    public AuctionController(AuctionService s,UserService u,LegoSetService legoSetService) {
        this.auctionService = s;
        this.userService = u;
        this.legoSetService = legoSetService;
    }

    @PostMapping("/create")
    public ResponseEntity<Auction> createAuction(@CookieValue(name = "Session")String session,@RequestBody Map<String,Object> auctionData) {

        UserProfile p = userService.getUserBySession(session);

        if (p == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        if (!auctionData.containsKey("product") || auctionData.get("product") == null)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

        if (!auctionData.containsKey("endsIn") || auctionData.get("endsIn") == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }


        ManufacturedData manufactured = legoSetService.getManufacturedData((String) auctionData.get("product"));

        if (manufactured == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        if (!p.getId().equals(manufactured.getOwner()))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);


        double startingBid = 0;
        if (auctionData.containsKey("startingBid")) {
            startingBid = (double) auctionData.get("startingBid");
        }

        int endsIn = (int)auctionData.get("endsIn");

        if (endsIn > 60)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

        //AuctionData m = new AuctionData(auctionData.getStartingBid(),p.getId(),auctionData.getProduct(),auctionData.getEndsAt());

        AuctionData ad = auctionService.getAuctionDataByProduct(manufactured.getId());

        if (ad != null)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

        Auction m = auctionService.createAuction(p,startingBid,manufactured,endsIn);


        return ResponseEntity.status(HttpStatus.CREATED).body(m);
    }

    @PostMapping("/bid/{id}")
    public ResponseEntity<Map<String,String>> bidAuction(@CookieValue(name = "Session") String session,@PathVariable String id,@RequestBody BidData bid) {

        UserProfile profile = userService.getUserBySession(session);

        Map<String,String> _map = new HashMap<>();

        if (profile == null)
        {
            _map.put("Status","Not logged in");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(_map);
        }


        Auction auctionData = auctionService.getAuctionById(id);

        if (auctionData == null || auctionData.isClosed())
        {
            _map.put("Status","Auction Unavailable");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(_map);
        }


        double v = bid.getValue();

         BidInfo bidInfo = auctionData.getCurrentBid();

        if (v < auctionData.getStartingBid())
        {
            _map.put("Status","Bid not enough");

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(_map);
        }

        if (bidInfo != null && bidInfo.getValue() >= v) {
            _map.put("Status","Bid not enough");

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(_map);
        }

        auctionService.updateBid(auctionData,new BidInfo(v,profile.getId()));

        _map.put("Status","Success");

        return ResponseEntity.ok().body(_map);

    }

    @GetMapping("/list")
    public void listAuctions() {

    }

    @GetMapping("/view/{id}")
    public Auction viewAuction(@PathVariable String id) {

        AuctionData auction = auctionService.getAuctionDataById(id);

        if (auction == null)
            return null;


        Manufactured md = legoSetService.getManufactured(auction.getProduct());

        UserProfile profile = userService.getUser(id);



        Auction a = new Auction(auction.getId(),auction.getStartingBid(),profile,md,auction.getCreatedAt(),auction.getEndsIn());


        return a;
    }



}
