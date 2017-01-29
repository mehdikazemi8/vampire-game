package ir.ugstudio.vampire.models.nearest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ir.ugstudio.vampire.models.BaseModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NearestObject extends BaseModel {
    private Double distance;
    private Double direction;
    private NearestTarget target;

    public NearestObject() {
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
