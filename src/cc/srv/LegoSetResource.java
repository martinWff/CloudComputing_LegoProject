package cc.srv;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/legoset")
public class LegoSetResource {

    public String createLegoSet() {

        return null;
    }

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
    public Comment[] listComments(@PathParam("id") int legoSetId, @QueryParam("page") int page, @QueryParam("categoria") String cat) {
        System.out.println("page: "+page);
        System.out.println("categoria: "+cat);
        return new Comment[] {new Comment("Martin","Gostei do lego")};
    }



}
