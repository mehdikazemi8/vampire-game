package ir.ugstudio.vampire.events;

import ir.ugstudio.vampire.models.nearest.Target;

public class StartMostWantedMission {
    private Target target;

    public StartMostWantedMission(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
}
