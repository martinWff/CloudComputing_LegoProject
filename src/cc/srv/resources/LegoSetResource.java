package cc.srv.resources;

import cc.UserDataAccess;
import cc.srv.Comment;
import cc.srv.LegoSetCreationData;
import cc.srv.StatusMessage;
import cc.srv.db.CosmosConnection;
import cc.srv.db.dataconstructor.*;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Path("/legoset")
public class LegoSetResource {

    private final CosmosContainer LegoSetCont = CosmosConnection.getDatabase().getContainer("LegoSets");

    private final CosmosContainer UsersCont = CosmosConnection.getDatabase().getContainer("Users");
    private final CosmosContainer CommentsCont = CosmosConnection.getDatabase().getContainer("Comments");


    @POST
    @Path("/{id}/comment/post")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public StatusMessage postComment(@CookieParam("Session") String session,@PathParam("id") String legoSetId, String text) {
        System.out.println("session "+session);
        if (session == null) {

            return new StatusMessage("Not Logged In");
        }

        UserProfile profile = AuthResource.getUserFromToken(session);
        if (profile != null) {

            try {

                CommentsCont.createItem(new CommentModel(UUID.randomUUID().toString(),legoSetId, profile.getId(),text));

                System.out.println(legoSetId);

                return StatusMessage.Success();

            } catch (Exception e) {
                System.err.println("Err: "+e.getMessage());

                return StatusMessage.Failed();

            }

        }

        return new StatusMessage("Not Logged In");
    }

    @GET
    @Path("/{id}/comment/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Comment> listComments(@PathParam("id") String legoSetId, @QueryParam("before") String page,@QueryParam("limit") Integer limit) {

        try {

            SqlQuerySpec commentQuerySpec;

            int lim = 25;
            if (limit != null) {
                lim = Math.min(limit, 25);
            }

            if (page != null) {
                Instant instant = Instant.parse(page);
                long ts = instant.getEpochSecond();

                commentQuerySpec = new SqlQuerySpec("SELECT TOP @lim * FROM c WHERE c.productId = @productId AND c.timestamp < @timestamp ORDER BY c.timestamp DESC", Arrays.asList(new SqlParameter("@productId",legoSetId),new SqlParameter("@timestamp",ts), new SqlParameter("@lim",lim)));

            } else {
                commentQuerySpec = new SqlQuerySpec("SELECT TOP @lim * FROM c WHERE c.productId = @productId ORDER BY c.timestamp DESC", Arrays.asList(new SqlParameter("@productId",legoSetId), new SqlParameter("@lim",lim)));
            }


            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            CosmosPagedIterable<CommentModel> commentResults = CommentsCont.queryItems(commentQuerySpec,options, CommentModel.class);


            Set<String> userIds = commentResults.stream()
                    .map(CommentModel::getAuthor)
                    .collect(Collectors.toSet());

            //SqlQuerySpec usersQuerySpec = new SqlQuerySpec("SELECT * FROM c WHERE c.isDeleted = false AND ARRAY_CONTAINS(@userIds,c.id)", Arrays.asList(new SqlParameter("@userIds",new ArrayList<>(userIds))));


           // CosmosPagedIterable<UserProfile> usersResults = UsersCont.queryItems(usersQuerySpec,options, UserProfile.class);

          /*  Map<String, UserProfile> usersById = usersResults.stream()
                    .collect(Collectors.toMap(UserProfile::getId, u -> u));*/
            UserProfile[] payload = UserDataAccess.retrieveUserProfiles(userIds.toArray(String[]::new));
            Map<String,UserProfile> usersById = Arrays.stream(payload).collect(Collectors.toMap(UserProfile::getId, u -> u));


            List<Comment> result = commentResults.stream()
                    .map(commentModel -> {
                        UserProfile userProfile = usersById.getOrDefault(commentModel.getAuthor(),UserDataAccess.DeletedUser);
                        return new Comment(commentModel.getId(),userProfile,commentModel.getContent(), commentModel.getTimestamp());
                    })
                    .collect(Collectors.toList());

            return result;

        } catch (Exception e) {
            System.out.println("Err: "+e.getMessage());

            return null;
        }
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
