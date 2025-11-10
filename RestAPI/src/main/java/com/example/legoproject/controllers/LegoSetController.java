package com.example.legoproject.controllers;

import com.example.legoproject.models.*;
import com.example.legoproject.services.LegoSetService;
import com.example.legoproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/legoset")
public class LegoSetController {

    private final LegoSetService legoSetService;
    private final UserService userService;

    @Autowired
    public LegoSetController(LegoSetService ls,UserService us) {
        this.legoSetService = ls;
        this.userService = us;
    }

    @PostMapping("/{id}/comment/post")
    public Comment postComment(@CookieValue(name = "Session") String session,@PathVariable String id, @RequestBody String content) {

        UserProfile profile = userService.getUserBySession(session);

        if (profile != null) {
            return legoSetService.postComment(profile,content,id);
        }

        return null;
    }

    @GetMapping("/{id}/comment/list")
    public List<Comment> listComments(@PathVariable String id, @RequestParam(required = false) String before) {

        if (before != null) {

            return legoSetService.listComments(id, Instant.parse(before));
        }

        return legoSetService.listComments(id, null);
    }

    @PostMapping("/create")
    public ResponseEntity<LegoSet> createLegoSet(@CookieValue(name = "Session")String session, @RequestBody LegoSet legoSet) {

        UserProfile profile = userService.getUserBySession(session);
        if (profile ==null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);


        if (profile.getPower() < 3)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);



        return ResponseEntity.status(HttpStatus.CREATED).body(legoSetService.createLegoSet(legoSet));
    }


    @PostMapping("/manufactured/create")
    public ResponseEntity<Manufactured> addManufacture(@CookieValue(name = "Session") String session,@RequestBody Map<String,String> map) {

        UserProfile profile = userService.getUserBySession(session);
        if (profile ==null || profile.getPower() < 3)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);


        String ownerId = null;

        if (map.containsKey("owner"))
        {
            ownerId = map.get("owner");
        }


        LegoSet legoSet = null;

        if (map.containsKey("legoSetId"))
        {
            legoSet = legoSetService.getLegoSet(map.get("legoSetId"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }


        return ResponseEntity.status(HttpStatus.CREATED).body(legoSetService.createManufactured(legoSet,ownerId));
    }

    @PutMapping("/manufactured/assign/{id}")
    public ResponseEntity<Manufactured> addManuFacture(@CookieValue(name = "Session") String session,@PathVariable String id,@RequestBody Map<String,String> map) {

        UserProfile profile = userService.getUserBySession(session);
        if (profile ==null || profile.getPower() < 3)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);


        if (id == null)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);


        Manufactured man = legoSetService.getManufactured(id);

        if (man == null)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

        if (man == null)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

        String ownerId = null;

        if (map.containsKey("owner"))
        {
            ownerId = map.get("owner");

        }



        return ResponseEntity.status(HttpStatus.CREATED).body(legoSetService.updateManufacturedOwnership(man,ownerId));
    }

}
