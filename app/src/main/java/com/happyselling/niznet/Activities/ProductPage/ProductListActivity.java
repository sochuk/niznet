package com.happyselling.niznet.Activities.ProductPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.Activities.SearchActivity;
import com.happyselling.niznet.Adapters.ProductAdapter;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;

public class ProductListActivity extends AppCompatActivity {

    private ProductListViewModel productListViewModel;
    private ProductAdapter productAdapter;

    private RecyclerView rvProduct;

    private String searchParam,product_code,product_name,brand_code,category_code;
    private ImageButton ibBack;
    private ShimmerFrameLayout shimmerFrameLayout;
    private PullRefreshLayout swipeRefreshLayout;
    private final Handler handler = new Handler();
    private BaseApiService mApiService;
    private GridLayoutManager layoutManager;

    private Context mContext;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private int currentSize = 0;

    private int currentPage = PAGE_START;

    private CardView cvSearch;

    private TextView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        initComponent();
    }

    @SuppressLint("ResourceAsColor")
    private void initComponent() {
        setStatusBar();
        mContext = this;

        Intent i = getIntent();
        product_code = i.getStringExtra("product_code");
        product_name = i.getStringExtra("product_name");
        brand_code = i.getStringExtra("brand_code");
        category_code = i.getStringExtra("category_code");
        searchParam = i.getStringExtra("search_param");

        rvProduct = findViewById(R.id.rv_list_produk);
        ibBack = findViewById(R.id.ib_back_product_list);
        searchView = findViewById(R.id.et_search);
        if(null != searchParam){
            if(searchParam.equals("")){
                searchView.setText("Search...");
            }else{
                searchView.setText(searchParam);
            }
        }


        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmer_list_produk);
        swipeRefreshLayout = findViewById(R.id.swip_product);
        swipeRefreshLayout.setColorSchemeColors(android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);

        shimmerFrameLayout.setVisibility(View.VISIBLE);

        rvProduct.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();

        mApiService = ApiServices.getApiService(ProductListActivity.this);
        productListViewModel = new ProductListViewModel(mContext, mApiService, product_code, product_name, brand_code, category_code, searchParam);

        rvProduct.setLayoutManager(new GridLayoutManager(ProductListActivity.this, 2));//untuk 2 rows);
        productAdapter = new ProductAdapter(this);

        productListViewModel.getProductLiveData().observe(this, pagedList -> {
            productAdapter.submitList(pagedList);
        });

        rvProduct.setAdapter(productAdapter);
        rvProduct.setVisibility(View.VISIBLE);
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmer();
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initComponent();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cvSearch = findViewById(R.id.search_bar_list_produk);
        cvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductListActivity.this, SearchActivity.class));
                finish();
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }

    private void setStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setColor(this, this.getResources().getColor(R.color.white), 0);
    }
}
