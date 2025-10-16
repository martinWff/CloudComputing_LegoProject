package cc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auction")
public class AuctionResource {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Auction createAction(AuctionCreationData auctionData) {
        System.out.println("creating auction!");
        return new Auction(auctionData.getId(),new LegoSet(1,1,"Lego1","first lego","firstlego_img1"));
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Auction[] listAuctions(@QueryParam("page") int page) {

        return new Auction[] {new Auction(1,null),new Auction(2,null)};
    }

    @POST
    @Path("/{id}/bid")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public StatusMessage bidAuction(@PathParam("id")int id, AuctionBid bid) {

        return StatusMessage.Success();
    }

    @POST
    @Path("/{id}/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusMessage cancelAuction() {

        return StatusMessage.Success();
    }
}
