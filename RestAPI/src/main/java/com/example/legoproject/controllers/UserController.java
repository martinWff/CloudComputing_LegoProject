package com.example.legoproject.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/list")
    public Map<String,Object> listUsers(@RequestParam(required = false) String token) {
        Map<String,Object> profiles = userService.getProfilesList(token,5);
        return profiles;
    }

    @GetMapping("/profile")
    public UserProfile getProfile(@CookieValue(name = "Session") String session) {

        return userService.getUserBySession(session);
    }

    @PutMapping("/update")
    public ResponseEntity<UserProfile> updateProfile(@RequestBody Map<String,String> userData,@CookieValue(name = "Session") String session) {

        UserProfile profile = userService.getUserBySession(session);

        if (profile == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        profile = userService.updateUser(profile.getId(),userData);

        return ResponseEntity.ok().body(profile);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@CookieValue(name = "Session") String session) {
        if (session == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User must be logged in");

        UserProfile p = userService.getUserBySession(session);

        if (p == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User must be logged in");

        userService.deleteUser(p.getId());
        userService.logout(session);


        return ResponseEntity.status(HttpStatus.OK).body("Successful");

    }

}
