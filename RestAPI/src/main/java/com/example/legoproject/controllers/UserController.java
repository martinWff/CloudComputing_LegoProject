package com.example.legoproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.legoproject.models.UserProfile;
import com.example.legoproject.services.UserService;

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
