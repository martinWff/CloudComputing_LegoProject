package com.example.legoproject.controllers;

import com.example.legoproject.models.UserProfile;
import com.example.legoproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;


    @Autowired
    public UserController(UserService userService) {
      this.userService = userService;
    }


    @GetMapping("/retrieve")
    public UserProfile getUser(@RequestParam String id) {

        UserProfile user = userService.getUser(id);

        return user;
    }

    @GetMapping("/profile")
    public UserProfile getProfile(@CookieValue(name = "Session") String session) {

        return userService.getUserBySession(session);
    }


}
