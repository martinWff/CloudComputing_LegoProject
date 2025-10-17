package cc.srv;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public class AuctionCreationData {
    private String productId;
    private float basePrice;


    public AuctionCreationData() {

    }

    public AuctionCreationData(String id) {
        this.productId = id;
    }

    public void setProductId(String id) {
        this.productId = id;
    }

    public String getProductId() {
        return productId;
    }

    public float getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(float basePrice) {
        this.basePrice = basePrice;
    }
}
