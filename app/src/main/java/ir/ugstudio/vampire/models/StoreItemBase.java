package ir.ugstudio.vampire.models;

public class StoreItemBase extends BaseModel {
    protected Integer imageType;
    private Integer price;
    private String title;

    public StoreItemBase() {
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
