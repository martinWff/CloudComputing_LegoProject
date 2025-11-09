package com.example.legoproject.controllers;

import com.example.legoproject.models.UserProfile;
import com.example.legoproject.models.UserUpdateData;
import com.example.legoproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public UserProfile updateProfile(@RequestBody UserUpdateData user,@CookieValue(name = "Session") String session) {

        System.out.println("user "+user);
        UserProfile profile = userService.getUserBySession(session);

        if (profile == null)
            return  null;

        return userService.updateUser(profile.getId(),user);
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
