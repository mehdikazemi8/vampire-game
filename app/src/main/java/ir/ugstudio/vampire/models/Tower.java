package ir.ugstudio.vampire.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tower {
    private Integer coin;
    private Integer level;
    private List<String> wall;
    private String role;
    private List<Double> geo;

    public Tower() {
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

    public List<String> getWall() {
        return wall;
    }

    public void setWall(List<String> wall) {
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
}
