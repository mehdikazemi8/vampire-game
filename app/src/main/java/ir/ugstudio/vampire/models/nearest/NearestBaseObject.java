package ir.ugstudio.vampire.models.nearest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import ir.ugstudio.vampire.models.BaseModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NearestBaseObject extends BaseModel {
    private Double distance;
    private NearestTarget target;

    @SerializedName("radian")
    private Double direction;

    public NearestBaseObject() {
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getDirection() {
        return direction;
    }

    public void setDirection(Double direction) {
        this.direction = direction;
    }

    public NearestTarget getTarget() {
        return target;
    }

    public void setTarget(NearestTarget target) {
        this.target = target;
    }
}
