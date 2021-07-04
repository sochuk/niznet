package com.happyselling.niznet.Models.ProductResponse;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("product_code")
    @Expose
    private String productCode;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("brand_code")
    @Expose
    private String brandCode;
    @SerializedName("category_code")
    @Expose
    private String categoryCode;
    @SerializedName("image1")
    @Expose
    private String image1;
    @SerializedName("image2")
    @Expose
    private String image2;
    @SerializedName("image3")
    @Expose
    private String image3;
    @SerializedName("image4")
    @Expose
    private String image4;
    @SerializedName("image5")
    @Expose
    private String image5;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("stock")
    @Expose
    private String stock;
    @SerializedName("isdelete")
    @Expose
    private String isdelete;
    @SerializedName("variasi")
    @Expose
    private String variasi;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("variasi_name")
    @Expose
    private String variasi_name;

    /**
     * No args constructor for use in serialization
     *
     */
    public Result() {
    }

    /**
     *
     * @param image5
     * @param image3
     * @param image4
     * @param categoryCode
     * @param image1
     * @param image2
     * @param productName
     * @param createdAt
     * @param productCode
     * @param price
     * @param id
     * @param isdelete
     * @param stock
     * @param updatedAt
     * @param brandCode
     * @param desc
     */
    public Result(Integer id, String createdAt, String updatedAt, String productCode, String productName, String brandCode, String categoryCode, String image1, String image2, String image3, String image4, String image5, String desc, String price, String stock, String isdelete, String brandName, String categoryName) {
        super();
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.productCode = productCode;
        this.productName = productName;
        this.brandCode = brandCode;
        this.categoryCode = categoryCode;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.image5 = image5;
        this.desc = desc;
        this.price = price;
        this.stock = stock;
        this.isdelete = isdelete;
        this.brandName = brandName;
        this.categoryName = categoryName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public String getImage5() {
        return image5;
    }

    public void setImage5(String image5) {
        this.image5 = image5;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(String isdelete) {
        this.isdelete = isdelete;
    }

    public String getVariasi() {
        return variasi;
    }

    public void setVariasi(String variasi) {
        this.variasi = variasi;
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

    public String getVariasi_name() {
        return variasi_name;
    }

    public void setVariasi_name(String variasi_name) {
        this.variasi_name = variasi_name;
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
