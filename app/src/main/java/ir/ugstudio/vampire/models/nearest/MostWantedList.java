package ir.ugstudio.vampire.models.nearest;

import java.util.List;

import ir.ugstudio.vampire.models.BaseModel;

public class MostWantedList extends BaseModel {
    private List<Target> mostWanted;

    public MostWantedList() {
    }

    public List<Target> getMostWanted() {
        return mostWanted;
    }

    public void setMostWanted(List<Target> mostWanted) {
        this.mostWanted = mostWanted;
    }
}
