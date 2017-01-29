package ir.ugstudio.vampire.events;

public class StartNearestMissionEvent {
    private String targetType;

    public StartNearestMissionEvent(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}
