package ir.ugstudio.vampire.models.nearest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ir.ugstudio.vampire.models.BaseModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Target extends BaseModel {
    private Double distance;
    private Double direction;
    private TargetBasicInfo target;

    public Target() {
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

    public TargetBasicInfo getTarget() {
        return target;
    }

    public void setTarget(TargetBasicInfo target) {
        this.target = target;
    }
}
