package ir.ugstudio.vampire.models.nearest;

import ir.ugstudio.vampire.models.User;

public class NearestPlayer {
    private User target;

    public NearestPlayer() {
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }
}
