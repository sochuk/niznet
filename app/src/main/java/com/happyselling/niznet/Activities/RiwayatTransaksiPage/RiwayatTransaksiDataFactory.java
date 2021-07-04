package com.happyselling.niznet.Activities.RiwayatTransaksiPage;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.happyselling.niznet.Services.BaseApiService;


public class RiwayatTransaksiDataFactory extends DataSource.Factory {

    private MutableLiveData<RiwayatTransaksiDataSource> mutableLiveData;
    private RiwayatTransaksiDataSource feedDataSource;
    private Context mContext;
    private BaseApiService mApiService;
    private Integer searchParam;
    public RiwayatTransaksiDataFactory(Context mContext, BaseApiService mApiService, Integer searchParam) {
        this.mContext = mContext;
        this.mApiService = mApiService;
        this.mutableLiveData = new MutableLiveData<RiwayatTransaksiDataSource>();
        this.searchParam = searchParam;
    }

    @Override
    public DataSource create() {
        feedDataSource = new RiwayatTransaksiDataSource(mContext, mApiService, searchParam);
        mutableLiveData.postValue(feedDataSource);
        return feedDataSource;
    }


    public MutableLiveData<RiwayatTransaksiDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
