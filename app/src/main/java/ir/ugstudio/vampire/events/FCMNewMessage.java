package ir.ugstudio.vampire.events;

public class FCMNewMessage {
    private String towerId;

    public FCMNewMessage(String towerId) {
        this.towerId = towerId;
    }

    public String getTowerId() {
        return towerId;
    }

    public void setTowerId(String towerId) {
        this.towerId = towerId;
    }
}
