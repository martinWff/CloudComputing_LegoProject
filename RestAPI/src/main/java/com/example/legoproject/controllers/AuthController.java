package com.example.legoproject.controllers;

import com.azure.core.annotation.BodyParam;
import com.example.legoproject.models.AuthData;
import com.example.legoproject.models.User;
import com.example.legoproject.models.UserProfile;
import com.example.legoproject.models.UserSessionData;
import com.example.legoproject.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;

    @Autowired
    public AuthController(UserService userService)
    {
        this.userService =userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserProfile> login(@RequestBody AuthData authData,@CookieValue(name = "Session", required = false) String session) {

        if (session != null)
        {
            UserProfile p = userService.getUserBySession(session);

            if (p != null)
                return ResponseEntity.ok().body(p);
        }


        UserSessionData sessionData = userService.login(authData);

        if (sessionData == null)
            return null;

        ResponseCookie cookie = ResponseCookie.from("Session",sessionData.session)
                .sameSite("Lax")
                .path("/")
                .maxAge((long)userService.getSessionExpiration())
                .build();



        return ResponseEntity.ok().header("Set-Cookie",cookie.toString()).body(sessionData.profile);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "Session") String session) {

        ResponseCookie cookie = ResponseCookie.from("Session",null)
                .sameSite("Lax")
                .path("/")
                .maxAge(1)
                .build();


        userService.logout(session);


        return ResponseEntity.ok().header("Set-Cookie",cookie.toString()).body("Disconnected");
    }

    @PostMapping("/register")
    public UserProfile register(@RequestBody User userData) {

        String id = UUID.randomUUID().toString();
        User user =new User(id,userData.getUsername(),userData.getEmail(),userService.hashPassword(userData.getPassword()), Instant.now(),userData.getAvatar(),false,1);
        if (userService.createUser(user))
            return new UserProfile(user.getId(),user.getUsername(),user.getDateOfCreation(),user.getPower());
        else
            return null;
    }


}
