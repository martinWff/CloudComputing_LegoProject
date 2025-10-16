package cc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.Body;

@Path("/legoset")
public class LegoSetResource {

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



}
