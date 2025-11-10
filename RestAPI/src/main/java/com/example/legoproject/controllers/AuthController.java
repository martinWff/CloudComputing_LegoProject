package com.example.legoproject.controllers;

import com.azure.core.annotation.BodyParam;
import com.azure.cosmos.implementation.HttpConstants;
import com.example.legoproject.models.AuthData;
import com.example.legoproject.models.User;
import com.example.legoproject.models.UserProfile;
import com.example.legoproject.models.UserSessionData;
import com.example.legoproject.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        if (authData.getEmail() == null || authData.getPassword() == null)
            return ResponseEntity.status(HttpConstants.StatusCodes.BADREQUEST).body(null);


        UserSessionData sessionData = userService.login(authData);

        if (sessionData == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

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
    public ResponseEntity<UserProfile> register(@RequestBody User userData) {

        if (userData.getEmail() == null || userData.getPassword() == null || userData.getUsername() == null)
            return ResponseEntity.status(HttpConstants.StatusCodes.BADREQUEST).body(null);


        UserSessionData usd = userService.register(userData.getUsername(),userData.getEmail(),userData.getPassword());

        if (usd == null)
            return null;

        ResponseCookie cookie = ResponseCookie.from("Session",usd.session)
                .sameSite("Lax")
                .path("/")
                .maxAge((long)userService.getSessionExpiration())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).header("Set-Cookie",cookie.toString()).body(usd.profile);
    }


}
