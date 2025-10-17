package cc.srv.db.dataconstructor;

import java.time.Instant;
import java.util.UUID;

public class AuctionModel {
    private String id;

    private String legoSetId;

    private String startingBid;

    private String currentBid;

    private String start_timestamp;
    private String end_timestamp;

    public AuctionModel() {

    }

    public AuctionModel(String legoSetId, float startingBid, float currentBid, Instant start_timestamp,Instant end_timestamp) {

        this.id = UUID.randomUUID().toString();
        this.legoSetId = legoSetId;
        this.startingBid = Float.toString(startingBid);
        this.currentBid = Float.toString(currentBid);
        this.startingBid = start_timestamp.toString();
        this.end_timestamp = end_timestamp.toString();
    }

    public AuctionModel(String legoSetId, float startingBid, float currentBid,Instant end_timestamp) {

        this.id = UUID.randomUUID().toString();
        this.legoSetId = legoSetId;
        this.startingBid = Float.toString(startingBid);
        this.currentBid = Float.toString(currentBid);
        this.startingBid = Instant.now().toString();
        this.end_timestamp = end_timestamp.toString();
    }

    public AuctionModel(String legoSetId, float startingBid,Instant end_timestamp) {

        this.id = UUID.randomUUID().toString();
        this.legoSetId = legoSetId;
        this.startingBid = Float.toString(startingBid);
        this.currentBid = Float.toString(0);
        this.startingBid = Instant.now().toString();
        this.end_timestamp = end_timestamp.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLegoSetId() {
        return legoSetId;
    }

    public void setLegoSetId(String legoSetId) {
        this.legoSetId = legoSetId;
    }

    public String getStartingBid() {
        return startingBid;
    }

    public void setStartingBid(String startingBid) {
        this.startingBid = startingBid;
    }

    public String getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(String currentBid) {
        this.currentBid = currentBid;
    }

    public String getStart_timestamp() {
        return start_timestamp;
    }

    public void setStart_timestamp(String start_timestamp) {
        this.start_timestamp = start_timestamp;
    }

    public String getEnd_timestamp() {
        return end_timestamp;
    }

    public void setEnd_timestamp(String end_timestamp) {
        this.end_timestamp = end_timestamp;
    }

}
