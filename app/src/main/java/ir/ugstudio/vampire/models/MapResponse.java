package ir.ugstudio.vampire.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapResponse extends BaseModel {
    private List<Tower> towers;
    private List<User> sheeps;
    private List<User> vampires;
    private List<User> hunters;

    public MapResponse() {
    }

    public List<User> getSheeps() {
        return sheeps;
    }

    public void setSheeps(List<User> sheeps) {
        this.sheeps = sheeps;
    }

    public List<Tower> getTowers() {
        return towers;
    }

    public void setTowers(List<Tower> towers) {
        this.towers = towers;
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
