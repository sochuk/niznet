package com.happyselling.niznet.Activities.ProductPage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import com.happyselling.niznet.Models.ProductResponse.Result;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.NetworkState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProductListViewModel extends ViewModel {

    private Executor executor;
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<Result>> productLiveData;


    private Context mContext;
    private BaseApiService mApiService;
    private String productCode,productName,brandCode,categoryCode,searchParam;
    public ProductListViewModel(@NonNull Context mContext, @NonNull BaseApiService mApiService, String productCode, String productName,
                                String brandCode, String categoryCode, String searchParam) {
        this.mContext = mContext;
        this.mApiService = mApiService;
        this.productCode = productCode;
        this.productName = productName;
        this.brandCode = brandCode;
        this.categoryCode = categoryCode;
        this.searchParam = searchParam;
        init();
    }

    private void init() {
        executor = Executors.newFixedThreadPool(5);

        ProductListDataFactory feedDataFactory = new ProductListDataFactory(mContext, mApiService,productCode,productName,
                brandCode,categoryCode,searchParam);
        networkState = Transformations.switchMap(feedDataFactory.getMutableLiveData(),
                dataSource -> dataSource.getNetworkState());

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(5)
                        .setPageSize(10).build();

        productLiveData = (new LivePagedListBuilder(feedDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
    }


    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<Result>> getProductLiveData() {
        return productLiveData;
    }
}
