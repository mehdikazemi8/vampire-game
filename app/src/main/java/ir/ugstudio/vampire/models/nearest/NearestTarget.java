package ir.ugstudio.vampire.models.nearest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import ir.ugstudio.vampire.models.BaseModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NearestTarget extends BaseModel {
    private String name;
    private Integer avatar;
    private Integer coin;

    @SerializedName("_id")
    private String id;

    public NearestTarget() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
