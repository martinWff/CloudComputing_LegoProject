package cc.srv.db;

import java.util.Arrays;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;

import cc.srv.db.dataconstructor.UserModel;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
//import azure.cosmos.models.CosmosItemResponse;
//import jakarta.validation.constraints.Email;

@Path("/UserQuery")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserContFunctions {

    String UserContName = "Users";

    private final CosmosContainer UsersCont = CosmosConnection.getDatabase().getContainer(UserContName);

    @GET
    @Path("/create/{email}/{username}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(
            @PathParam("email") String email,
            @PathParam("username") String username,
            @PathParam("password") String password) {
        //System.out.println("Entry na function insert db");
        try {

            UserModel test = new UserModel(username, email, password);

            //use this cosmosItemResponse when creating updating or replacing entries on the db
            CosmosItemResponse<UserModel> testResponse = UsersCont.createItem(test);

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
    public Response getUser(@PathParam("email") String inputMail) {
        try {
            // u here is just and alias same as making "from users as u"
            String query = "Select * from u where u.email=@email";

            //creates a parameter to send in the query
            //SqlParameter param = new SqlParameter("@email", inputMail);

            //using Arrays.asList to simply the variables to send for the query 
            //(extra tip you can chain multiple params using " , " after the new SqlParameter)
            SqlQuerySpec queryspec = new SqlQuerySpec(query, Arrays.asList(
                    new SqlParameter("@email", inputMail)
            ));

            //use this cosmosPagedIterables when making selects in the db as the can return multiple entries
            CosmosPagedIterable<UserModel> results
                    = UsersCont.queryItems(queryspec, new CosmosQueryRequestOptions(), UserModel.class);

            for (UserModel gettedUser : results) {
                return Response.ok(gettedUser).build();
            }

            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"User not found\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    
    // @GET
    // @Path("/update/{email}")
    // @Consume(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response updateUserMail(@PathParam("emai") String email) {
    //     try {

    //         CosmosItemResponse<UserModel> response = UsersCont.readItem(email, new parti, itemType)

    //     } 
    //     catch
    //     {

    //     }
    // }

}
