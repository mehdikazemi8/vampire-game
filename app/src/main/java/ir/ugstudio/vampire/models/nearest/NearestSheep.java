package ir.ugstudio.vampire.models.nearest;

import ir.ugstudio.vampire.models.User;

public class NearestSheep extends NearestResponse {
    private User target;

    public NearestSheep() {
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }
}
