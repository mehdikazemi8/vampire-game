package ir.ugstudio.vampire.models.nearest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import ir.ugstudio.vampire.models.BaseModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TargetBasicInfo extends BaseModel {
    private String type;
    private String username;
    private Integer avatar;
    private Integer coin;

    @SerializedName("_id")
    private String id;

    public TargetBasicInfo() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }
}
