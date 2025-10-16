package cc.srv;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Auction {
    private int id;

    private LegoSet product;

    private float currentBid;

    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss a")
    private Timestamp startedAt;

    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss a")
    private Timestamp endsAt;

    public Auction() {

    }

    public Auction(int id,LegoSet product) {
        this.id = id;
        this.product = product;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setProduct(LegoSet product) {
        this.product = product;
    }

    public LegoSet getProduct() {
        return this.product;
    }

    public float getCurrentBid() {
        return currentBid;
    }

    public Timestamp getStartedAt() {
        return this.startedAt;
    }

    public void setEndsAt(Timestamp ts) {
        this.endsAt = ts;
    }

    public Timestamp getEndsAt() {
        return this.endsAt;
    }

}
