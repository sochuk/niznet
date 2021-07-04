package com.happyselling.niznet.Models.DetailTransaksiResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("checkout_code")
    @Expose
    private String checkoutCode;
    @SerializedName("id_kurir")
    @Expose
    private String idKurir;
    @SerializedName("photo_payment")
    @Expose
    private String photoPayment;
    @SerializedName("users_id")
    @Expose
    private String usersId;
    @SerializedName("total_price")
    @Expose
    private String totalPrice;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("telp")
    @Expose
    private String telp;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("paymentMethod")
    @Expose
    private String paymentMethod;
    @SerializedName("product")
    @Expose
    private List<Product> product = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCheckoutCode() {
        return checkoutCode;
    }

    public void setCheckoutCode(String checkoutCode) {
        this.checkoutCode = checkoutCode;
    }

    public String getIdKurir() {
        return idKurir;
    }

    public void setIdKurir(String idKurir) {
        this.idKurir = idKurir;
    }

    public String getPhotoPayment() {
        return photoPayment;
    }

    public void setPhotoPayment(String photoPayment) {
        this.photoPayment = photoPayment;
    }

    public String getUsersId() {
        return usersId;
    }

    public void setUsersId(String usersId) {
        this.usersId = usersId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }

}
