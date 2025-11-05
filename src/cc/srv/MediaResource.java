package cc.srv;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import cc.utils.EnvLoader;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/media")
public class MediaResource {

    //private static final String CONTAINER_URL = EnvLoader.GetImg_Container();
    // //private static final String container_url = EnvLoader.GetImg_Container();
    // private static final String container_url = "https://legocontainer.blob.core.windows.net/images";
    // private static final String containerName = container_url.substring(container_url.lastIndexOf("/") + 1);

    // int firstSlashIndex = container_url.indexOf('/', 8); // skip "https://"
    // String baseUrl = (firstSlashIndex != -1) ? container_url.substring(0, firstSlashIndex) : container_url;

    @POST
    @Path("/teste")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadFile(InputStream inputStream, @Context HttpHeaders headers) {
        try {
            // Extract boundary from Content-Type header
            String contentType = headers.getHeaderString("Content-Type");
            if (contentType == null || !contentType.contains("boundary=")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Missing boundary in Content-Type").build();
            }

            String boundary = "--" + contentType.split("boundary=")[1];

            // Read the input into memory (for testing only)
            String body = new String(inputStream.readAllBytes(), StandardCharsets.ISO_8859_1);

            // Extract filename from multipart headers
            String filename = null;
            for (String part : body.split(boundary)) {
                if (part.contains("Content-Disposition")) {
                    int idx = part.indexOf("filename=\"");
                    if (idx != -1) {
                        filename = part.substring(idx + 10, part.indexOf("\"", idx + 10));
                        break;
                    }
                }
            }

            if (filename == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("No file part found").build();
            }

            // ✅ Convert body to bytes (in a real app, isolate file data properly)
            byte[] fileBytes = body.getBytes(StandardCharsets.ISO_8859_1);

            // ✅ Create Azure Blob client
            BlobContainerClient serviceClient = new BlobContainerClientBuilder()
                    .endpoint(EnvLoader.GetImg_Container())
                    //.credential(new DefaultAzureCredentialBuilder().build())
                    .buildClient();

            // BlobContainerClient containerClient = serviceClient.getBlobContainerClient(containerName);
            // if (!containerClient.exists()) {
            //     containerClient.create();
            // }

            BlobClient blobClient = serviceClient.getBlobClient(filename);
            // ✅ Upload file (overwrite if exists)
            blobClient.upload(new ByteArrayInputStream(fileBytes), fileBytes.length, true);

            // ✅ Get blob URL
            String blobUrl = blobClient.getBlobUrl();
            return Response.ok("{\"message\":\"Upload successful\", \"url\":\"" + blobUrl + "\"}").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error reading file: " + e.getMessage()).build();
        }
    }

}
