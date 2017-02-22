package ir.ugstudio.vampire.models;

public class StoreItemVirtual extends StoreItemBase {
    private String itemId;

    public StoreItemVirtual(String itemId, Integer imageType) {
        this.itemId = itemId;
        setImageType(imageType);
    }

    public StoreItemVirtual() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
