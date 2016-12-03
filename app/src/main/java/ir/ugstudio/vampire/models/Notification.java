package ir.ugstudio.vampire.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification extends BaseModel {
    private String notificationType;
    private String message;
    private User killer;
    private String created;
    private Integer lostCoin;

    public Notification() {
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getKiller() {
        return killer;
    }

    public void setKiller(User killer) {
        this.killer = killer;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getLostCoin() {
        return lostCoin;
    }

    public void setLostCoin(Integer lostCoin) {
        this.lostCoin = lostCoin;
    }
}
