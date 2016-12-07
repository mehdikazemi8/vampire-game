package ir.ugstudio.vampire.events;

import ir.ugstudio.vampire.models.Tower;

public class OpenTowerWallFragment {
    private Tower tower;

    public OpenTowerWallFragment(Tower tower) {
        this.tower = tower;
    }

    public Tower getTower() {
        return tower;
    }

    public void setTower(Tower tower) {
        this.tower = tower;
    }
}
