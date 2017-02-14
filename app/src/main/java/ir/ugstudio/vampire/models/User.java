package ir.ugstudio.vampire.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseModel {
    private String username;
    private String lifestat;
    private String role;
    private String city;
    private Integer rank;
    private Integer score;
    private List<Double> geo;
    private List<String> towers;
    private List<Tower> towersList;
    private String token;
    private Integer coin;
    private Integer attackRange;
    private Integer sightRange;
    private Integer avatar;
    private UpdateVersion updateVersion;

    public User() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public UpdateVersion getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(UpdateVersion updateVersion) {
        this.updateVersion = updateVersion;
    }

    public String getLifestat() {
        return lifestat;
    }

    public void setLifestat(String lifestat) {
        this.lifestat = lifestat;
    }

    public Integer getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(Integer attackRange) {
        this.attackRange = attackRange;
    }

    public Integer getSightRange() {
        return sightRange;
    }

    public void setSightRange(Integer sightRange) {
        this.sightRange = sightRange;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public List<String> getTowers() {
        return towers;
    }

    public void setTowers(List<String> towers) {
        this.towers = towers;
    }

    public List<Tower> getTowersList() {
        return towersList;
    }

    public void setTowersList(List<Tower> towersList) {
        this.towersList = towersList;
    }
}
