package ir.ugstudio.vampire.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapResponse extends BaseModel {
    private List<User> vampires;
    private List<User> hunters;

    public MapResponse() {
    }

    public List<User> getVampires() {
        return vampires;
    }

    public void setVampires(List<User> vampires) {
        this.vampires = vampires;
    }

    public List<User> getHunters() {
        return hunters;
    }

    public void setHunters(List<User> hunters) {
        this.hunters = hunters;
    }
}
