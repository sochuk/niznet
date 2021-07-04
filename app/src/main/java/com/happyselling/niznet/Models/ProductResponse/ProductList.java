package com.happyselling.niznet.Models.ProductResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.happyselling.niznet.Models.Meta;

public class ProductList {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("data")
    @Expose
    private Data data;


    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
