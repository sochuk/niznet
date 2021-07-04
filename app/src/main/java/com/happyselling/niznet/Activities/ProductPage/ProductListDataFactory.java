package com.happyselling.niznet.Activities.ProductPage;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.happyselling.niznet.Services.BaseApiService;


public class ProductListDataFactory extends DataSource.Factory {

    private MutableLiveData<ProductListDataSource> mutableLiveData;
    private ProductListDataSource feedDataSource;
    private Context mContext;
    private BaseApiService mApiService;
    private String productCode,productName,brandCode,categoryCode,searchParam;
    public ProductListDataFactory(Context mContext, BaseApiService mApiService,String productCode, String productName,
                                  String brandCode, String categoryCode, String searchParam) {
        this.mContext = mContext;
        this.mApiService = mApiService;
        this.mutableLiveData = new MutableLiveData<ProductListDataSource>();
        this.productCode = productCode;
        this.productName = productName;
        this.brandCode = brandCode;
        this.categoryCode = categoryCode;
        this.searchParam = searchParam;
    }

    @Override
    public DataSource create() {
        feedDataSource = new ProductListDataSource(mContext, mApiService, productCode,productName,
                brandCode,categoryCode,searchParam);
        mutableLiveData.postValue(feedDataSource);
        return feedDataSource;
    }


    public MutableLiveData<ProductListDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
