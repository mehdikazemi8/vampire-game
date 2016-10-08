package ir.ugstudio.vampire.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapResponse extends BaseModel {
    private List<User> opponents;
    private List<User> allies;

    public MapResponse() {
    }

    public List<User> getOpponents() {
        return opponents;
    }

    public void setOpponents(List<User> opponents) {
        this.opponents = opponents;
    }

    public List<User> getAllies() {
        return allies;
    }

    public void setAllies(List<User> allies) {
        this.allies = allies;
    }
}
