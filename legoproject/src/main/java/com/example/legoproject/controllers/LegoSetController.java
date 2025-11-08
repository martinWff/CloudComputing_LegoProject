package com.example.legoproject.controllers;

import com.example.legoproject.models.Comment;
import com.example.legoproject.models.CommentData;
import com.example.legoproject.models.LegoSet;
import com.example.legoproject.models.UserProfile;
import com.example.legoproject.services.LegoSetService;
import com.example.legoproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

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
    public LegoSet createLegoSet(@RequestBody LegoSet legoSet) {

        return legoSetService.createLegoSet(legoSet);
    }

}
