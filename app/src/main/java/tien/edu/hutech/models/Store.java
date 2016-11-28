package tien.edu.hutech.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lvant on 02/10/2016.
 */

public class Store implements Serializable{

    private String name;
    private String address;
    private String image;
    private String open;
    private String close;
    private String phone;
    private String district;
    private String brand;
    private double lat;
    private double lng;
    private String keyStore;
    private byte[] bytesImage;

    public Map<String, Boolean> favorite = new HashMap<>();

    public Store() {
    }

    public Store(String name, String address, String image, String open, String close, String phone, String district, String brand, double lat, double lng) {
        this.name = name;
        this.address = address;
        this.image = image;
        this.open = open;
        this.close = close;
        this.phone = phone;
        this.district = district;
        this.brand = brand;
        this.lat = lat;
        this.lng = lng;
    }

    public Store(String name, String address, String image, String open, String close, String phone, String district) {
        this.name = name;
        this.address = address;
        this.image = image;
        this.open = open;
        this.close = close;
        this.phone = phone;
        this.district = district;
    }

    public Store(String name, String address, String image, String open, String close, String phone) {

        this.name = name;
        this.address = address;
        this.image = image;
        this.open = open;
        this.close = close;
        this.phone = phone;
    }

    public byte[] getBytesImage() {
        return bytesImage;
    }

    public void setBytesImage(byte[] bytesImage) {
        this.bytesImage = bytesImage;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getName() {

        return name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
