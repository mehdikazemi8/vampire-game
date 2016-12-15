package ir.ugstudio.vampire.models;

public class TowerMessage extends BaseModel {
    private String username;
    private String message;
    private Integer avatar;

    public TowerMessage() {
    }

    public TowerMessage(String username, String message, Integer avatar) {
        this.username = username;
        this.message = message;
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return true;
        }
        
        TowerMessage other = (TowerMessage) obj;
        return this.message.equals(other.getMessage()) && this.getUsername().equals(other.getUsername());
    }
}
