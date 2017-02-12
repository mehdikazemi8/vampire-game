package ir.ugstudio.vampire.models.nearest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.ugstudio.vampire.models.BaseModel;

public class MostWantedList extends BaseModel {
    @SerializedName("mostWanted")
    private List<Target> mostWantedList;

    public MostWantedList() {
    }

    public List<Target> getMostWantedList() {
        return mostWantedList;
    }

    public void setMostWantedList(List<Target> mostWantedList) {
        this.mostWantedList = mostWantedList;
    }
}
