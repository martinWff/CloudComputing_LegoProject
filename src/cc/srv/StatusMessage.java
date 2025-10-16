package cc.srv;

public class StatusMessage {
    private String status;

    public StatusMessage() {

    }

    public StatusMessage(String msg) {
        this.status = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        this.status = v;
    }

    public static StatusMessage Success()
    {
        return new StatusMessage("Success");
    }

    public static StatusMessage Failed()
    {
        return new StatusMessage("Failed");
    }

}
