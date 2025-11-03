package cc.srv;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource {

    // @POST
    // @Path("/upload")
    // @Consumes(MediaType.MULTIPART_FORM_DATA)
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response uploadImage(@FormDataParam("file") InputStream fileInputStream,
    //         @FormDataParam("file") FormDataContentDisposition fileDetail) {

    //     try {
    //         // 1️⃣ Connect to Azure Blob Storage
    //         String connectStr = "<YOUR_STORAGE_CONNECTION_STRING>";
    //         BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
    //                 .connectionString(connectStr)
    //                 .buildClient();

    //         BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("images");

    //         // 2️⃣ Create a blob name (e.g., with UUID to avoid collisions)
    //         String blobName = UUID.randomUUID().toString() + "-" + fileDetail.getFileName();
    //         BlobClient blobClient = containerClient.getBlobClient(blobName);

    //         // 3️⃣ Upload the file
    //         blobClient.upload(fileInputStream, fileInputStream.available(), true);

    //         // 4️⃣ Build URL to store in Cosmos DB
    //         String imageUrl = blobClient.getBlobUrl();

    //         // 5️⃣ Save URL in Cosmos DB (example for a LegoSet)
    //         LegoSet legoSet = new LegoSet();
    //         legoSet.setName("My Lego Set");
    //         legoSet.setImageUrl(imageUrl); // save the path in DB
    //         LegoSetResponse response = LegoSetsCont.createItem(legoSet); // your Cosmos DB code

    //         return Response.ok(response.getItem()).build();

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
    //                 .entity("{\"error\": \"" + e.getMessage() + "\"}")
    //                 .build();
    //     }
    // }

   
    @GET
    @Path("/{id}")
    @Produces("media/*")
    public Response download(@PathParam("id") String id) {
        throw new ServiceUnavailableException();
    }

    /**
     * Lists the ids of images stored.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> list() {
        throw new ServiceUnavailableException();
    }
}
