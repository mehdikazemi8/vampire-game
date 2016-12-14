package ir.ugstudio.vampire.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateVersion extends BaseModel {
    private Integer currentVersion;
    private Integer minimumSupportVersion;
    private List<String> lastestUpdates;

    public UpdateVersion() {
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Integer getMinimumSupportVersion() {
        return minimumSupportVersion;
    }

    public void setMinimumSupportVersion(Integer minimumSupportVersion) {
        this.minimumSupportVersion = minimumSupportVersion;
    }

    public List<String> getLastestUpdates() {
        return lastestUpdates;
    }

    public void setLastestUpdates(List<String> lastestUpdates) {
        this.lastestUpdates = lastestUpdates;
    }
}
