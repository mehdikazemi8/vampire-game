package ir.ugstudio.vampire.models;

import java.util.List;

public class StoreItems extends BaseModel {
    private List<StoreItemVirtual> virtuals;
    private List<StoreItemReal> reals;

    public StoreItems() {
    }

    public List<StoreItemVirtual> getVirtuals() {
        return virtuals;
    }

    public void setVirtuals(List<StoreItemVirtual> virtuals) {
        this.virtuals = virtuals;
    }

    public List<StoreItemReal> getReals() {
        return reals;
    }

    public void setReals(List<StoreItemReal> reals) {
        this.reals = reals;
    }
}
