package ir.ugstudio.vampire.models;

import java.util.List;

public class Ranklist extends BaseModel {
    // todo, ask him to send empty near
    private List<User> top;

    public Ranklist() {
    }

    public List<User> getTop() {
        return top;
    }

    public void setTop(List<User> top) {
        this.top = top;
    }
}
