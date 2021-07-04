package com.happyselling.niznet.Models.MasterTransaksiResponse;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

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
    private Object photoPayment;
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

    public Result() {
    }

    public Result(Integer id, String checkoutCode, String idKurir, Object photoPayment, String usersId, String totalPrice, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.checkoutCode = checkoutCode;
        this.idKurir = idKurir;
        this.photoPayment = photoPayment;
        this.usersId = usersId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public Object getPhotoPayment() {
        return photoPayment;
    }

    public void setPhotoPayment(Object photoPayment) {
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
    public static DiffUtil.ItemCallback<Result> DIFF_CALLBACK = new DiffUtil.ItemCallback<Result>() {
        @Override
        public boolean areItemsTheSame(@NonNull Result oldItem, @NonNull Result newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Result oldItem, @NonNull Result newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        Result article = (Result) obj;
        return article.id == this.id;
    }
}
