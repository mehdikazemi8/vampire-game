package ir.ugstudio.vampire.events;

import ir.ugstudio.vampire.models.nearest.Target;

public class NearestResponseEvent {
    private Target target;
    private Boolean foundNearest;

    public NearestResponseEvent(Target target, Boolean foundNearest) {
        this.target = target;
        this.foundNearest = foundNearest;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Boolean getFoundNearest() {
        return foundNearest;
    }

    public void setFoundNearest(Boolean foundNearest) {
        this.foundNearest = foundNearest;
    }
}
