package cc.srv;

import cc.srv.db.CosmosConnection;
import cc.srv.db.dataconstructor.LegoSetModel;
import cc.srv.db.dataconstructor.MediaModel;
import cc.srv.db.dataconstructor.UserModel;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;

@Path("/legoset")
public class LegoSetResource {

    private final CosmosContainer LegoSetCont = CosmosConnection.getDatabase().getContainer("LegoSets");


    @POST
    @Path("/{id}/comment/post")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response postComment(@PathParam("id") int legoSetId, String text) {
        System.out.println(text);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/{id}/comment/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Comment[] listComments(@PathParam("id") int legoSetId, @QueryParam("page") int page) {
        System.out.println("page: "+page);
        return new Comment[] {new Comment("Martin","Gostei do lego")};
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@QueryParam("pageSize") @DefaultValue("25") int pageSize, @QueryParam("continuationToken") String continuationToken) {

        System.out.println("list");
        try {

            //Query Cosmos DB for the given email
            String query = "SELECT * FROM c";
            SqlQuerySpec querySpec = new SqlQuerySpec(query);

            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            CosmosPagedIterable<LegoSetModel> pageResults = LegoSetCont.queryItems(
                    querySpec,
                    options,
                    LegoSetModel.class
            );


            ArrayList<LegoSetModel> users = new ArrayList<>();
            String nextContinuationToken = null;

            // Retrieve one page
            Iterator<FeedResponse<LegoSetModel>> iterator =
                    (continuationToken == null)
                            ? pageResults.iterableByPage(pageSize).iterator()
                            : pageResults.iterableByPage(continuationToken, pageSize).iterator();

            if (iterator.hasNext()) {
                FeedResponse<LegoSetModel> page = iterator.next();
                users.addAll(page.getResults());
                nextContinuationToken = page.getContinuationToken();
            }

            // Build response

            Map<String, Object> resp = new HashMap<String, Object>();
            resp.put("items", users);
            resp.put("continuationToken", nextContinuationToken);

            return Response.ok(resp).header("X-Continuation-Token", nextContinuationToken).build();
     } catch (CosmosException e) {
            // Handle Cosmos DB-specific errors (e.g., connection or query failure)
            System.err.println("Cosmos DB error: " + e.getMessage());
            return null;

        } catch (Exception e) {
            // Handle unexpected exceptions
            System.err.println("Unexpected error: " + e.getMessage());
            return null;
        }
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LegoSetModel createLegoSet(LegoSetCreationData m) {
        System.out.println(m.getName());
        System.out.println(m.getDescription());
        System.out.println(m.getPhotos());

        for (String a : m.getPhotos()) {
            System.out.println(a);
        }

        LegoSetModel createdModel = new LegoSetModel(m.getName(),m.getDescription(),m.getYearOfProduction());
        try {

            LegoSetCont.createItem(createdModel);

        } catch (CosmosException e) {
            System.err.println("Cosmos DB error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Error: "+e.getMessage());

            return null;
        }

        return createdModel;
    }

    @GET
    @Path("/{id}/retrieve")
    @Produces(MediaType.APPLICATION_JSON)
    public LegoSetModel retrieveLegoSet(@PathParam("id") String id) {

        try {
            String query = "SELECT * FROM c WHERE c.id = @id";
            SqlQuerySpec querySpec = new SqlQuerySpec(query, Arrays.asList(new SqlParameter("@id",id)));

            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            CosmosPagedIterable<LegoSetModel> results = LegoSetCont.queryItems(
                    querySpec,
                    options,
                    LegoSetModel.class
            );

            LegoSetModel model = results.stream().findFirst().orElse(null);

            return model;

        } catch (CosmosException e) {
            System.err.println("Cosmos DB error: " + e.getMessage());

            return null;
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());

            return null;
        }
    }

    @DELETE
    @Path("/{id}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusMessage removeLegoSet(@PathParam("id")String id) {

        try {

            LegoSetCont.deleteItem(id,new PartitionKey(id),new CosmosItemRequestOptions());

            return StatusMessage.Success();
        } catch (CosmosException e) {

            System.err.println("Cosmos DB error: " + e.getMessage());

            return StatusMessage.Failed();
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());

            return StatusMessage.Failed();
        }
    }

    @PATCH
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LegoSetModel updateLegoSet(LegoSetModel model) {

        try {
            LegoSetCont.replaceItem(
                    model,
                    model.getId(),
                    new PartitionKey(model.getId()),
                    new CosmosItemRequestOptions()
            );
        } catch (CosmosException e) {
            System.err.println("Cosmos DB error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }

        return model;
    }

}
