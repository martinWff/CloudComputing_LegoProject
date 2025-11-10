package com.example.legoproject.controllers;

import com.example.legoproject.models.AuctionData;
import com.example.legoproject.services.AuctionService;
import com.example.legoproject.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auction")
public class AuctionController {


    private final AuctionService auctionService;
    private final UserService userService;


    public AuctionController(AuctionService s,UserService u) {
        this.auctionService = s;
        this.userService = u;
    }

    @GetMapping("/create")
    public AuctionData createAuction(@CookieValue(name = "Session")String session) {




        return null;
    }



}
