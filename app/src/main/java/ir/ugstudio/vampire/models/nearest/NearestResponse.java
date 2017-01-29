package ir.ugstudio.vampire.models.nearest;

import com.google.gson.annotations.SerializedName;

import ir.ugstudio.vampire.models.BaseModel;

public class NearestResponse extends BaseModel {
    private Integer distance;
    @SerializedName("radian")
    private Double direction;

    public NearestResponse() {
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Double getDirection() {
        return direction;
    }

    public void setDirection(Double direction) {
        this.direction = direction;
    }
}
