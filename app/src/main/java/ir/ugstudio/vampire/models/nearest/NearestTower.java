package ir.ugstudio.vampire.models.nearest;

import ir.ugstudio.vampire.models.Tower;

public class NearestTower extends NearestResponse {
    private Tower target;

    public NearestTower() {
    }

    public Tower getTarget() {
        return target;
    }

    public void setTarget(Tower target) {
        this.target = target;
    }
}
