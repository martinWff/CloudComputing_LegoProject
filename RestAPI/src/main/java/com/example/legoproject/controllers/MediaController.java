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
import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> upload(@RequestParam("file")MultipartFile multipartFile, @CookieValue(name = "Session", required = false) String session) {

        Map<String,Object> map = new HashMap<>();

        UserProfile user = userService.getUserBySession(session);



        if (user == null)
        {
            map.put("Error","Authentication Required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);

        }


        //limits to 131kb
        if (multipartFile.getSize() > 131072)
        {
            map.put("Error","File is too large, max allowed is 128 KB.");
            return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED).body(map);
        }

        System.out.println(multipartFile.getOriginalFilename());

        try {

             MediaData mediaData = mediaService.save(multipartFile,user.getId());


             return ResponseEntity.status(HttpStatus.CREATED).body(mediaData);

        } catch (IOException e) {

            map.put("Error","Error uploading image");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
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
