package ir.ugstudio.vampire.models;

import java.util.List;

public class PlacesResponse extends BaseModel {
    private List<Place> places;

    public PlacesResponse() {
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
