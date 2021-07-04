package com.happyselling.niznet.Models.DetailTransaksiResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("image1")
    @Expose
    private String image1;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("variasi")
    @Expose
    private String variasi;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getVariasi() {
        return variasi;
    }

    public void setVariasi(String variasi) {
        this.variasi = variasi;
    }
}