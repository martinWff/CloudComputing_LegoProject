package cc.srv;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
public class UserResource {
    @GET
    @Path("/list/{bruh}")
    @Produces(MediaType.APPLICATION_JSON)
    public User[] listOfUsers(@PathParam("bruh") String bruh ) {
        return new User[] {new User(1,"Martin"),new User(2,"Hooman Beans"),new User(3,"Rackday")};
    }

    @GET
    @Path("/retrieve/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User retrieveUser(@PathParam("id") int id) {

        return new User(id,"Martin");
    }

    @GET
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") int id) {
        return Response.status(200).build();
    }

}
