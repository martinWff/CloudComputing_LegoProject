package cc.srv.resources;

import cc.srv.*;
import cc.srv.db.CosmosConnection;
import cc.srv.db.dataconstructor.UserModel;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import cc.srv.db.dataconstructor.AuctionModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

@Path("/auction")
public class AuctionResource {

    String tableName = "Auction";

    private final CosmosContainer tableModel = CosmosConnection.getDatabase().getContainer(tableName);

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Auction createAction(AuctionCreationData auctionData) {
        System.out.println("creating auction!");

        AuctionModel m =new AuctionModel("lego_1",0, Instant.now());
        tableModel.createItem(m);

        return new Auction(m.getId(),new LegoSet("lego_1","lego_1","Lego1","first lego","firstlego_img1"));
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Auction[] listAuctions(@QueryParam("page") int page) {

        SqlQuerySpec spec = new SqlQuerySpec("Select * from u OFFSET @off LIMIT @lim",Arrays.asList(
                new SqlParameter("@off",page * 10),
                new SqlParameter("@lim",page * 10)
        ));

        CosmosPagedIterable<AuctionModel> results = tableModel.queryItems(spec,new CosmosQueryRequestOptions(),AuctionModel.class);

        ArrayList<Auction> list = new ArrayList<Auction>();

        for (AuctionModel auc : results)
        {
            list.add(new Auction(auc.getId(), new LegoSet(auc.getLegoSetId(),"NotImplemented","lego","...",""),Float.parseFloat(auc.getStartingBid())));
        }

      //  return new Auction[] {new Auction("lego_1",null),new Auction("lego_2",null)};
        return (Auction[]) list.toArray();
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
