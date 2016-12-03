package ir.ugstudio.vampire.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationList extends BaseModel {
    private List<Notification> victim;

    public NotificationList() {
    }

    public List<Notification> getVictim() {
        return victim;
    }

    public void setVictim(List<Notification> victim) {
        this.victim = victim;
    }
}
