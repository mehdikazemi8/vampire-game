package ir.ugstudio.vampire.events;

import ir.ugstudio.vampire.models.nearest.NearestObject;

public class NearestResponseEvent {
    private NearestObject nearestObject;
    private Boolean foundNearest;

    public NearestResponseEvent(NearestObject nearestObject, Boolean foundNearest) {
        this.nearestObject = nearestObject;
        this.foundNearest = foundNearest;
    }

    public NearestObject getNearestObject() {
        return nearestObject;
    }

    public void setNearestObject(NearestObject nearestObject) {
        this.nearestObject = nearestObject;
    }

    public Boolean getFoundNearest() {
        return foundNearest;
    }

    public void setFoundNearest(Boolean foundNearest) {
        this.foundNearest = foundNearest;
    }
}
