package cc.utils;

public class MediaDataDTO {

    private String id;

    private String file;

    public MediaDataDTO() {

    }

    public MediaDataDTO(String id,String file) {
        this.id = id;
        this.file = file;
    }


    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
