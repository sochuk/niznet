package com.happyselling.niznet.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class SharedCartItem implements Parcelable {
    private Integer id;
    private String idProduct;
    private String idUser;
    private String quantity;
    private String createdAt;
    private String updatedAt;
    private String totalHarga;
    private String productName;
    private String stock;
    private String productCode;
    private String price;
    private String image1;
    private String brandName;
    private String categoryName;

    public SharedCartItem() {
        this.id = 0;
        this.idProduct = "";
        this.idUser = "";
        this.quantity = "";
        this.createdAt = "";
        this.updatedAt = "";
        this.totalHarga = "";
        this.productName = "";
        this.stock = "";
        this.productCode = "";
        this.price = "";
        this.image1 = "";
        this.brandName = "";
        this.categoryName = "";
    }

    public SharedCartItem(Parcel in) {
        this.id = in.readInt();
        this.idProduct = in.readString();
        this.idUser = in.readString();
        this.quantity = in.readString();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
        this.totalHarga = in.readString();
        this.productName = in.readString();
        this.stock = in.readString();
        this.productCode = in.readString();
        this.price = in.readString();
        this.image1 = in.readString();
        this.brandName = in.readString();
        this.categoryName = in.readString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        this.totalHarga = totalHarga;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(idProduct);
        dest.writeString(idUser);
        dest.writeString(quantity);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(totalHarga);
        dest.writeString(productName);
        dest.writeString(stock);
        dest.writeString(productCode);
        dest.writeString(price);
        dest.writeString(image1);
        dest.writeString(brandName);
        dest.writeString(categoryName);

    }

    public static final Creator<SharedCartItem> CREATOR = new Creator<SharedCartItem>() {
        @Override
        public SharedCartItem createFromParcel(Parcel in) {
            return new SharedCartItem(in);
        }

        @Override
        public SharedCartItem[] newArray(int size) {
            return new SharedCartItem[size];
        }
    };

}
