package com.happyselling.niznet.Activities.RiwayatTransaksiPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.Adapters.RiwayatTransaksiAdapter;
import com.happyselling.niznet.Adapters.RiwayatTransaksiViewPagerAdapter;
import com.happyselling.niznet.MainActivity;
import com.happyselling.niznet.Models.StatusTrx;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class RiwayatTransaksiActivity extends AppCompatActivity {
    private RiwayatTransaksiViewModel riwayatTransaksiViewModel;
    private RiwayatTransaksiAdapter riwayatTransaksiAdapter;

    private RecyclerView rvRiwayatTransaksi;

    private String searchParam, id_transaksi;
    private ImageView ibBack;
    private ShimmerFrameLayout shimmerFrameLayout;
    private PullRefreshLayout swipeRefreshLayout;
    private final Handler handler = new Handler();
    private BaseApiService mApiService;
    private LinearLayoutManager layoutManager;
    private List<StatusTrx> statusList = new ArrayList<>();
    private ArrayList statusList2 = new ArrayList<>();
    private ViewPager viewPager;

    private Context mContext;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private int currentSize = 0;

    private int currentPage = PAGE_START;

    private TextView searchView;
    private Realm realm;
    private SharedPreferences sharedPreferences;
    private String status_login;
    private UserRealmObject userProfile;
    private TabLayout tabLayout;
    private RiwayatTransaksiViewPagerAdapter riwayatTransaksiViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_transaksi);

        initComponent();
    }

    @SuppressLint("ResourceAsColor")
    private void initComponent() {
        setStatusBar();
        mContext = this;

        Intent i = getIntent();
        id_transaksi = i.getStringExtra("id_transaksi");
        searchParam = i.getStringExtra("search_param");

        tabLayout = findViewById(R.id.tab_status_riwayat_transaksi);
        viewPager = findViewById(R.id.vp_riwayat_transaksi);
        ibBack = findViewById(R.id.iv_back_riwayat_transaksi);
        setTabLayout();


        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setTabLayout(){
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        setTabLayoutAdapter();

    }

    private void setTabLayoutAdapter() {
        StatusTrx statusTrx1 = new StatusTrx();
        StatusTrx statusTrx2 = new StatusTrx();
        StatusTrx statusTrx3 = new StatusTrx();
        StatusTrx statusTrx4 = new StatusTrx();
        statusTrx1.setCodeStatus(9);
        statusTrx1.setNamaStatus("Semua");
        statusTrx2.setCodeStatus(1);
        statusTrx2.setNamaStatus("Menunggu Pembayaran");
        statusTrx3.setCodeStatus(2);
        statusTrx3.setNamaStatus("Menunggu Verifikasi");
        statusTrx4.setCodeStatus(3);
        statusTrx4.setNamaStatus("Transaksi Selesai");
        statusList.add(statusTrx1);
        statusList.add(statusTrx2);
        statusList.add(statusTrx3);
        statusList.add(statusTrx4);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (StatusTrx statusNya: statusList) {

            tabLayout.addTab(tabLayout.newTab().setText(statusNya.getNamaStatus()));
            statusList2.add(statusList);
        }
        riwayatTransaksiViewPagerAdapter = new RiwayatTransaksiViewPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount(), statusList2);
        viewPager.setAdapter(riwayatTransaksiViewPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("ViewPager "," getCurrentItem() "+viewPager.getCurrentItem());
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        tabLayout.setSelectedTabIndicatorColor(getColor(R.color.colorPrimary));
                        tabLayout.setTabTextColors(getColor(R.color.tab_gray), getColor(R.color.colorPrimary));
                        break;
                    case 1:
                        tabLayout.setSelectedTabIndicatorColor(getColor(R.color.black));
                        tabLayout.setTabTextColors(getColor(R.color.tab_gray), getColor(R.color.black));
                        break;
                    case 2:
                        tabLayout.setSelectedTabIndicatorColor(getColor(R.color.yellow));
                        tabLayout.setTabTextColors(getColor(R.color.tab_gray), getColor(R.color.yellow));
                        break;
                    case 3:
                        tabLayout.setSelectedTabIndicatorColor(getColor(R.color.dark_green));
                        tabLayout.setTabTextColors(getColor(R.color.tab_gray), getColor(R.color.dark_green));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

            mApiService = ApiServices.getApiServiceWithToken(userProfile.getToken(), RiwayatTransaksiActivity.this);
        }
    }
}
