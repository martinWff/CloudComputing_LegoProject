package com.example.legoproject.controllers;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.BlobProperties;
import com.example.legoproject.models.MediaData;
import com.example.legoproject.models.User;
import com.example.legoproject.models.UserProfile;
import com.example.legoproject.services.MediaService;
import com.example.legoproject.services.UserService;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/media")
public class MediaController {

    private MediaService mediaService;
    private UserService userService;

    @Autowired
    public MediaController(MediaService s,UserService userService) {
        this.mediaService = s;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file")MultipartFile multipartFile, @CookieValue(name = "Session", required = false) String session) {

        System.out.println("USER!");

        UserProfile user = userService.getUserBySession(session);

        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(("User must be authenticated"));


        //limits to 131kb
        if (multipartFile.getSize() > 131072)
        {
            return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED).body("File is too large, max allowed is 128 KB.");
        }

        System.out.println(multipartFile.getOriginalFilename());

        try {

             boolean status = mediaService.save(multipartFile,user.getId());

             if (status) {
                 return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded sucessfully");
             } else {
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
             }

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id) {


        BlobClient bc = mediaService.getBlobClient(id);

        if (bc == null)
            return null;

        if (!bc.exists())
            return null;



        BlobProperties bp = bc.getProperties();

        InputStream inputStream = bc.openInputStream();

        System.out.println(bp.getContentType());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(bp.getContentType()))
                .body(new InputStreamResource(inputStream));

    }

}
