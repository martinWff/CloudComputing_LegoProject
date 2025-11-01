package cc.srv.db;

import java.util.Arrays;
import java.util.Iterator;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;

import cc.srv.db.dataconstructor.UserModel;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/UserQuery")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserContFunctions {

    String UserContName = "Users";

    private final CosmosContainer UsersCont = CosmosConnection.getDatabase().getContainer(UserContName);

    @POST
    @Path("/createUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserModel newUserData) {
        //System.out.println("Entry na function insert db");
        try {

            //UserModel test = new UserModel(username, email, password,true);

            UserModel newUser = new UserModel(
                newUserData.getUsername(),
                newUserData.getEmail(),
                newUserData.getPassword(),
                newUserData.getStatus()
            );

            //use this cosmosItemResponse when creating updating or replacing entries on the db
            CosmosItemResponse<UserModel> testResponse = UsersCont.createItem(newUser);

            return Response.status(Response.Status.CREATED)
                    .entity(testResponse.getItem())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/getuser/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByEmail(@PathParam("email") String email) {
        try {

            //Query Cosmos DB for the given email
            String query = "SELECT * FROM c WHERE c.email = @email";
            SqlQuerySpec querySpec = new SqlQuerySpec(query,
                    Arrays.asList(new SqlParameter("@email", email)));

            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            CosmosPagedIterable<UserModel> results = UsersCont.queryItems(
                    querySpec,
                    options,
                    UserModel.class
            );

            //Checks if we found any results
            Iterator<UserModel> iterator = results.iterator();
            if (!iterator.hasNext()) {
                // No user with that email → return 404
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"User not found\"}")
                        .build();
            }

            //Get the first user result
            UserModel user = iterator.next();
            System.out.println("Found user: " + user.getUsername());

            //Return the user object as JSON (JAX-RS will serialize it)
            return Response.ok(user).build();

        } catch (CosmosException e) {
            // Handle Cosmos DB-specific errors (e.g., connection or query failure)
            System.err.println("Cosmos DB error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error: " + e.getMessage() + "\"}")
                    .build();

        } catch (Exception e) {
            // Handle unexpected exceptions
            System.err.println("Unexpected error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    // @GET
    // @Path("/verify/{email}")
    // @Produces(MediaType.APPLICATION_JSON)
    public Response VerifyUser(/*@PathParam("email")*/String email) {
        try {

            //Query Cosmos DB for the given email
            String query = "SELECT * FROM c WHERE c.email = @email";
            SqlQuerySpec querySpec = new SqlQuerySpec(query,
                    Arrays.asList(new SqlParameter("@email", email)));

            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            CosmosPagedIterable<UserModel> results = UsersCont.queryItems(
                    querySpec,
                    options,
                    UserModel.class
            );

            boolean isUserReal = results.iterator().hasNext();
            return Response.ok(isUserReal).build();

        } catch (CosmosException e) {
            // Handle Cosmos DB-specific errors (e.g., connection or query failure)
            System.err.println("Cosmos DB error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error: " + e.getMessage() + "\"}")
                    .build();

        } catch (Exception e) {
            // Handle unexpected exceptions
            System.err.println("Unexpected error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/update/{email}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserMail(
            @PathParam("email") String email,
            UserModel updatedUserData // JSON body from client
    ) {
        try {
            //Find the user by email (query instead of readItem)
            String query = "SELECT * FROM c WHERE c.email = @email";
            SqlQuerySpec querySpec = new SqlQuerySpec(
                    query,
                    Arrays.asList(new SqlParameter("@email", email))
            );

            CosmosPagedIterable<UserModel> users = UsersCont.queryItems(
                    querySpec,
                    new CosmosQueryRequestOptions(),
                    UserModel.class
            );

            //Check if user exists
            UserModel existingUser = users.stream().findFirst().orElse(null);
            if (existingUser == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"User not found\"}")
                        .build();
            }

            //checking if the id is correct and it is
            System.out.println("Updating ID: " + existingUser.getId());

            //Apply updates only to provided fields in the json body
            if (updatedUserData.getUsername() != null) {
                existingUser.setUsername(updatedUserData.getUsername());
            }
            if (updatedUserData.getPassword() != null) {
                existingUser.setPassword(updatedUserData.getPassword());
            }
            if (updatedUserData.getUsername() != null) {
                existingUser.setUsername(updatedUserData.getUsername());
            }

            existingUser.setLastUpdate();//

            //where actual changes happen
            UsersCont.replaceItem(
                    existingUser,
                    existingUser.getId(), // the document’s id (from the DB)
                    new PartitionKey(existingUser.getId()), // partition key = id (because the way that the container is setup)
                    new CosmosItemRequestOptions()
            );

            //Return success response
            return Response.ok(existingUser).build();

        } catch (CosmosException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Cosmos DB error: " + e.getMessage() + "\"}")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}")
                    .build();
        }
    }

}
