
package cc.srv;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;

import cc.utils.EnvLoader;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;




@Path("/media")
public class MediaResource {


    /*
    @POST
    @Path("/upload")
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

            System.out.println(filename);

            //convert body to bytes (in a real app, isolate file data properly)
            byte[] fileBytes = body.getBytes(StandardCharsets.ISO_8859_1);

            //create Azure Blob client
            BlobContainerClient containerClient = new BlobContainerClientBuilder()
                    .endpoint(EnvLoader.getVariable("img_container"))
                    //.credential(new DefaultAzureCredentialBuilder().build())
                    .buildClient();

            //hashing
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashbytes = digest.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashbytes) {
                sb.append(String.format("%02x", b));
            }
            String fileHash = sb.toString();

            BlobClient blobClient = containerClient.getBlobClient(fileHash);

            if (blobClient.exists()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"message\":\"File already exists\", \"filename\":\"" + fileHash + "\"}")
                        .build();
            }

            //upload file (overwrite if exists)
            blobClient.upload(new ByteArrayInputStream(fileBytes), fileBytes.length, true);

            //gets blob URL
            String blobUrl = blobClient.getBlobUrl();
            return Response.ok("{\"message\":\"Upload successful\", \"url\":\"" + blobUrl + "\"}").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error reading file: " + e.getMessage()).build();
        }
    }
     */
    
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(@Context HttpHeaders headers, InputStream inputStream) {
        try {
            //Read all bytes from the input stream 
            byte[] fileBytes = inputStream.readAllBytes();
            // 🔹 Get filename from header (simplified for Insomnia/browser upload)
            String contentDisposition = headers.getHeaderString("Content-Disposition");
            String filename = "unknown";
            if (contentDisposition != null && contentDisposition.contains("filename=")) {
                filename = contentDisposition.split("filename=")[1].replace("\"", "").trim();
            }
            // 🔹 Compute SHA-256 hash of the FILE CONTENT (not filename)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String fileHash = sb.toString();

            //Creates blob client
            BlobContainerClient containerClient = new BlobContainerClientBuilder()
                    .endpoint(EnvLoader.getVariable("img_container"))
                    .buildClient();
            BlobClient blobClient = containerClient.getBlobClient(fileHash);
            // 🔹 If file already exists, don’t upload again
            if (blobClient.exists()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"message\":\"File already exists\", \"filename\":\"" + fileHash + "\"}")
                        .build();
            }
            //Upload new file
            blobClient.upload(new ByteArrayInputStream(fileBytes), fileBytes.length);
            // 🔹 Store original filename as metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("originalname", filename);
            blobClient.setMetadata(metadata);
            
            //Return blob info
            return Response.ok("{\"message\":\"Upload successful\", \"hash\":\"" + fileHash + "\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /* @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@QueryParam("filename") String filename) {
        try {
            System.out.println("Filename is: " + filename);
            // ✅ Build the blob URI safely
            URI blobUri = new URI("https", "legocontainer.blob.core.windows.net", "/images/" + filename, null);
            URL blobUrl = blobUri.toURL();

            // ✅ Open input stream from the blob URL
            System.out.println("Downloading from: " + blobUrl);
            InputStream in = blobUrl.openStream();

            // ✅ Stream output directly to the HTTP response
            StreamingOutput stream = output -> {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
                in.close();
            };

            // ✅ Infer content type if needed
            String contentType = Files.probeContentType(Paths.get(filename));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return Response.ok(stream, contentType)
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                    .build();

        } catch (FileNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"File not found\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
     */
    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@QueryParam("filename") String hash) {
        try {
            BlobContainerClient containerClient = new BlobContainerClientBuilder()
                    .endpoint(EnvLoader.getVariable("img_container"))
                    .buildClient();

            BlobClient blobClient = containerClient.getBlobClient(hash);

            if (!blobClient.exists()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"File not found\"}")
                        .build();
            }

            //Get original name (fallback = hash)
            String originalName = blobClient.getProperties().getMetadata().get("originalname");
            if (originalName == null || originalName.isEmpty()) {
                originalName = hash;
            }

            // 🔹 Stream the file
            InputStream in = blobClient.openInputStream();
            StreamingOutput stream = output -> {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
                in.close();
            };

            // 🔹 Detect MIME type
            String contentType = Files.probeContentType(Paths.get(originalName));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 🔹 Return as if it’s the original file
            return Response.ok(stream, contentType)
                    .header("Content-Disposition", "inline; filename=\"" + originalName + "\"")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    //testing just lists all image blonb on the container
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listBlobs() {
        try {
            // Create Blob Container Client for container
            BlobContainerClient containerClient = new BlobContainerClientBuilder()
                    .endpoint("https://legocontainer.blob.core.windows.net")
                    .containerName("images") // no leading slash
                    .buildClient();

            // List all blobs in the container
            List<String> blobNames = new ArrayList<>();
            for (BlobItem blobItem : containerClient.listBlobs()) {
                blobNames.add(blobItem.getName());
            }

            // Return as JSON
            String json = new com.google.gson.Gson().toJson(blobNames);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

}
