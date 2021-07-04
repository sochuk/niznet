package com.happyselling.niznet.Activities.RiwayatTransaksiPage;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.happyselling.niznet.Models.MasterTransaksiResponse.MasterTransaksi;
import com.happyselling.niznet.Models.MasterTransaksiResponse.Result;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.NetworkState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatTransaksiDataSource extends PageKeyedDataSource<Long, Result> {

    private static final String TAG = RiwayatTransaksiDataSource.class.getSimpleName();

    private Context mContext;
    private BaseApiService mApiService;
    private MutableLiveData networkState;
    private MutableLiveData initialLoading;
    private String searchParam;

    public RiwayatTransaksiDataSource(Context mContext, BaseApiService mApiService, Integer searchParams) {
        this.mContext = mContext;
        this.mApiService = mApiService;
        this.searchParam = (9 == searchParams?null:searchParams.toString());

        Log.d("#####", "searchParam: "+this.searchParam);
        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
    }


    public MutableLiveData getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<Long, Result> callback) {

        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        mApiService.getMasterTransaksi(1, params.requestedLoadSize,searchParam)
                .enqueue(new Callback<MasterTransaksi>() {
                    @Override
                    public void onResponse(Call<MasterTransaksi> call, Response<MasterTransaksi> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "onResponse: "+response.body().getData().getResults().size());
                            callback.onResult(response.body().getData().getResults(), null, 2l);
                            initialLoading.postValue(NetworkState.LOADED);
                            networkState.postValue(NetworkState.LOADED);

                        } else {
                            initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<MasterTransaksi> call, Throwable t) {
                        String errorMessage = t == null ? "unknown error" : t.getMessage();
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params,
                           @NonNull LoadCallback<Long, Result> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params,
                          @NonNull LoadCallback<Long, Result> callback) {

        Log.i(TAG, "Loading Rang " + params.key + " Count " + params.requestedLoadSize);

        networkState.postValue(NetworkState.LOADING);

        mApiService.getMasterTransaksi(params.key, params.requestedLoadSize,searchParam).enqueue(new Callback<MasterTransaksi>() {
            @Override
            public void onResponse(Call<MasterTransaksi> call, Response<MasterTransaksi> response) {
                if (response.isSuccessful()) {
                    int totalResult = response.body().getData().getTotalResults();
                    long totalResultLong = totalResult;
                    long nextKey = (params.key == totalResultLong) ? 0 : params.key + 1;
                    callback.onResult(response.body().getData().getResults(), nextKey);
                    networkState.postValue(NetworkState.LOADED);

                } else
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
            }

            @Override
            public void onFailure(Call<MasterTransaksi> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });
    }
}