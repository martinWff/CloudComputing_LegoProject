package com.example.legoproject.services;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.core.util.Base64Url;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.example.legoproject.models.MediaData;


@Service
public class MediaService {


    private final BlobContainerClient containerClient;
    private final CosmosContainer mediaContainer;
    @Autowired
    public MediaService(BlobContainerClient containerClient, CosmosDatabase cosmosDB) {

        this.containerClient = containerClient;
        this.mediaContainer = cosmosDB.getContainer("Media");
    }


    public boolean save(MultipartFile multipartFile,String userId) throws IOException {

        //convert body to bytes (in a real app, isolate file data properly)

        //create Azure Blob client
        try {

        //hashing (a perceptual hashing would be better but due to time constraints this will have to suffice)
        MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hashbytes = digest.digest(multipartFile.getBytes());


        String fileHash =Base64Url.encode(hashbytes).toString();

        BlobClient blobClient = containerClient.getBlobClient(fileHash);

        mediaContainer.createItem(new MediaData(multipartFile.getOriginalFilename(),fileHash,"image",userId));

        if (blobClient.exists()) {

            return false;
        }

        //upload file (overwrite if exists)

            BlobHttpHeaders headers = new BlobHttpHeaders();

            Tika tika = new Tika();

            headers.setContentType(tika.detect(multipartFile.getInputStream()));



            blobClient.upload(multipartFile.getInputStream(), multipartFile.getBytes().length, true);
            blobClient.setHttpHeaders(headers);



            //gets blob URL
      //  String blobUrl = blobClient.getBlobUrl();
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }

    }

    public MediaData getImage(String id) {

        SqlQuerySpec s = new SqlQuerySpec("SELECT * FROM c where c.id = @id", Arrays.asList(new SqlParameter("@id",id)));
        CosmosPagedIterable<MediaData> m = mediaContainer.queryItems(s,new CosmosQueryRequestOptions(),MediaData.class);

        if (m.iterator().hasNext()) {
            MediaData mediaData = m.iterator().next();

            return mediaData;
        }


        return null;
    }

    public MediaData getOwnedImage(String id,String userId) {

        SqlQuerySpec s = new SqlQuerySpec("SELECT * FROM c where c.id = @id AND c.owner = @userId", Arrays.asList(new SqlParameter("@id",id), new SqlParameter("@userId",userId)));
        CosmosPagedIterable<MediaData> m = mediaContainer.queryItems(s,new CosmosQueryRequestOptions(),MediaData.class);

        if (m.iterator().hasNext()) {
            MediaData mediaData = m.iterator().next();

            return mediaData;
        }


        return null;
    }


    public BlobClient getBlobClient(String id) {

        BlobClient blobClient = containerClient.getBlobClient(id);

       return blobClient;

    }

}
