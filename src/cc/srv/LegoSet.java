package cc.srv;

public class LegoSet {
    private String productId;
    private String serialNumber;

    private String name;

    private String description;

    private String[] photos;

    public LegoSet() {

    }

    public LegoSet(String productId,String serialNumber,String name,String description,String[] photos) {
        this.productId = productId;
        this.serialNumber = serialNumber;
        this.name = name;
        this.description = description;
        this.photos = photos;
    }

    public LegoSet(String productId,String serialNumber,String name,String description,String photo) {
        this.productId = productId;
        this.serialNumber = serialNumber;
        this.name = name;
        this.description = description;
        this.photos = new String[] {photo};
    }

    public void setProductId(String id) {
        this.productId = id;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setSerialNumber(String n) {
        this.serialNumber = n;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }
}
