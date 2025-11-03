package cc.srv.db.dataconstructor;

import java.time.Instant; //library to get the instant time of the server.
import java.util.UUID; //library to make password hashing

public class MediaModel {

    private String id;
    private String MediaPath;
    private Boolean isImage;
    private String LegoSetId;
    private String UserPfpId;
    private String DateOfCreation;
    private String LastUpdate;

    public MediaModel() {

    }

    public MediaModel(String MediaPath, Boolean isImage, String LegoSetId,String UserPfpId) {

        this.id = UUID.randomUUID().toString(); //creates a random num id to use in the db.
        this.MediaPath = MediaPath;
        this.isImage = isImage;
        this.LegoSetId = LegoSetId;
        this.UserPfpId = UserPfpId;
        this.DateOfCreation = Instant.now().toString(); //gets a current timestamp of the server
        this.LastUpdate = this.DateOfCreation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaPath() {
        return MediaPath;
    }

    public void setMediaPath(String MediaPath) {
        this.MediaPath = MediaPath;
    }

    public Boolean getIsImage() {
        return isImage;
    }

    public void setIsImage(Boolean isImage) {
        this.isImage = isImage;
    }

    public String getLegoSetId() {
        return LegoSetId;
    }

    public void setLegoSetId(String LegoSetId) {
        this.LegoSetId = LegoSetId;
    }

    public String getUserPfpId() {
        return UserPfpId;
    }

    public void setUserPfpId(String UserPfpId) {
        this.UserPfpId = UserPfpId;
    }

    public String getDateOfCreation() {
        return DateOfCreation;
    }

    public void setDateOfCreation(String DateOfCreation) {
        this.DateOfCreation = DateOfCreation;
    }

    public String getLastUpdate() {
        return LastUpdate;
    }

    public void setLastUpdate() {
        this.LastUpdate = Instant.now().toString();
    }

    

}