package com.happyselling.niznet.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.Activities.ProductPage.ProductListActivity;
import com.happyselling.niznet.Adapters.CartListAdapter;
import com.happyselling.niznet.Models.CartResponse.CartList;
import com.happyselling.niznet.Models.CartResponse.Datum;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;
import com.happyselling.niznet.Utils.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartListActivity extends AppCompatActivity {

    private CartListAdapter cartAdapter;

    private RecyclerView rvCart;

    private String product_code,product_name,brand_code,category_code;
    private TextView tvTotalHarga;
    private ImageView ibBack;
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

    private Realm realm;
    private SharedPreferences sharedPreferences;
    private String status_login;
    private UserRealmObject userProfile;
    private Integer totalHarga;
    private List<Datum> cartResultList;
    private Toolbar tbBottom;

    private ConstraintLayout clEmptyCart;
    private Button btnCheckout, btnBelanja, btnTambahBelanja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

        initComponent();
    }

    @SuppressLint("ResourceAsColor")
    public void initComponent() {
        setStatusBar();
        mContext = this;

        Intent i = getIntent();
        product_code = i.getStringExtra("product_code");
        product_name = i.getStringExtra("product_name");
        brand_code = i.getStringExtra("brand_code");
        category_code = i.getStringExtra("category_code");

        rvCart = findViewById(R.id.rv_cart);
        ibBack = findViewById(R.id.iv_back_cart);
        btnCheckout = findViewById(R.id.btn_checkout);

        btnBelanja = findViewById(R.id.btn_belanja_cart);
        btnTambahBelanja = findViewById(R.id.btn_tambah_belanjaan);
        clEmptyCart = findViewById(R.id.cl_empty_cart);

        tvTotalHarga = findViewById(R.id.tv_total_harga_cart);

        swipeRefreshLayout = findViewById(R.id.swip_cart);
        tbBottom = findViewById(R.id.tb_bottom_cart);

        sharedPreferences = getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        cekLogin();
        rvCart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        rvBrands.setLayoutManager(layoutManagerBrands);
        rvCart.setItemAnimator(new DefaultItemAnimator());
        setCartAdapter();

        btnTambahBelanja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProductListActivity.class));
                finish();
            }
        });

        btnBelanja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProductListActivity.class));
                finish();
            }
        });

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

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postCheckout();
            }
        });

    }

    private void setCartAdapter() {
        totalHarga = 0;
        cartResultList = new ArrayList<>();
        mApiService.getCartList().enqueue(new Callback<CartList>() {
            @Override
            public void onResponse(Call<CartList> call, Response<CartList> response) {
                if(response.isSuccessful()){
                    if(response.body().getMeta().getCode() == 200){
                        cartResultList = response.body().getData();
                        cartAdapter = new CartListAdapter(CartListActivity.this,cartResultList, tvTotalHarga, mApiService);
                        cartAdapter.notifyDataSetChanged();
                        rvCart.setAdapter(cartAdapter);
                        for(int j=0; j<cartResultList.size(); j++){
                            totalHarga += Integer.parseInt(cartResultList.get(j).getTotalHarga());
                        }
                        tvTotalHarga.setText(Util.parsingRupiah(totalHarga));
                        if(cartResultList.size()>0){
                            tbBottom.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            clEmptyCart.setVisibility(View.GONE);
                            btnTambahBelanja.setVisibility(View.VISIBLE);
                        }else{
                            tbBottom.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            clEmptyCart.setVisibility(View.VISIBLE);
                            btnTambahBelanja.setVisibility(View.GONE);
                        }
                    }else{
                        Toast.makeText(mContext, "Gagal memuat data, silahkan mencoba beberapa saat lagi", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext, "Gagal memuat data, silahkan mencoba beberapa saat lagi", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<CartList> call, Throwable t) {
                Toast.makeText(mContext, "Gagal memuat data, silahkan mencoba beberapa saat lagi", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void postCheckout(){
        List<Datum> datumList = cartAdapter.getItem();
        Intent intent = new Intent(CartListActivity.this, DetailCheckoutActivity.class);
        intent.putExtra("cartList", (Serializable) datumList);
        intent.putExtra("totalHarga", tvTotalHarga.getText().toString());
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

    }

    private void setStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setColor(this, this.getResources().getColor(R.color.white), 0);
    }

    private void cekLogin() {
        status_login = sharedPreferences.getString(Enum.isloggedin, "0");
        if(status_login.equals("1")) {
            userProfile = realm.where(UserRealmObject.class).findFirst();

            mApiService = ApiServices.getApiServiceWithToken(userProfile.getToken(), CartListActivity.this);
        }
    }

}
