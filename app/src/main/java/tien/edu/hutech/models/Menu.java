package tien.edu.hutech.models;

/**
 * Created by Me on 10/3/2016.
 */

public class Menu {
    private String name;
    private int price;
    private String image;
    private String storeKey;

    public Menu() {
    }

    public Menu(String name, int price, String image, String keyStore) {

        this.name = name;
        this.price = price;
        this.image = image;
        this.storeKey = keyStore;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStoreKey() {
        return storeKey;
    }

    public void setStoreKey(String storeKey) {
        this.storeKey = storeKey;
    }
}
