package ir.ugstudio.vampire.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tower implements Serializable {
    private Integer coin;
    private Integer level;
    private List<TowerMessage> wall;
    private String role;
    private List<Double> geo;
    private String _id;
    private List<User> owners;

    public Tower() {
    }

    public List<User> getOwners() {
        return owners;
    }

    public void setOwners(List<User> owners) {
        this.owners = owners;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<TowerMessage> getWall() {
        return wall;
    }

    public void setWall(List<TowerMessage> wall) {
        this.wall = wall;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Double> getGeo() {
        return geo;
    }

    public void setGeo(List<Double> geo) {
        this.geo = geo;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
