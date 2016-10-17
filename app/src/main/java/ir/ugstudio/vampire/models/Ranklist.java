package ir.ugstudio.vampire.models;

import java.util.List;

public class Ranklist extends BaseModel {
    private List<User> top;
    private List<User> near;

    public Ranklist() {
    }

    public List<User> getTop() {
        return top;
    }

    public void setTop(List<User> top) {
        this.top = top;
    }

    public List<User> getNear() {
        return near;
    }

    public void setNear(List<User> near) {
        this.near = near;
    }
}
