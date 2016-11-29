package ir.ugstudio.vampire.events;

import ir.ugstudio.vampire.models.StoreItemReal;

public class StartRealPurchase {
    private StoreItemReal item;

    public StartRealPurchase(StoreItemReal item) {
        this.item = item;
    }

    public StoreItemReal getItem() {
        return item;
    }

    public void setItem(StoreItemReal item) {
        this.item = item;
    }
}
