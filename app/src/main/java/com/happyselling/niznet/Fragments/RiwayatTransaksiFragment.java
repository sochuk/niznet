package com.happyselling.niznet.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.happyselling.niznet.Activities.RiwayatTransaksiPage.RiwayatTransaksiViewModel;
import com.happyselling.niznet.Adapters.RiwayatTransaksiAdapter;
import com.happyselling.niznet.Models.MasterTransaksiResponse.Result;
import com.happyselling.niznet.Models.StatusTrx;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RiwayatTransaksiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RiwayatTransaksiFragment extends Fragment {
    private static final String ARG_PARENT_TRANSAKSI = "PARENTS";
    private static List<StatusTrx> statusList;
    private int position = 0;
    private ImageView ivNoresult;
    private ProgressBar progressBar;
    private RecyclerView rvRiwayatTransaksi;
    private ArrayList<Result> riwayatTransaksiList = new ArrayList<>();
    private BaseApiService mApiService;
    private Handler handler = new Handler();
    private LinearLayout lyTitle;
    private TextView tvTitle;
    private View view;
    private PullRefreshLayout swipeRefreshLayout;   
    private Realm realm;
    private SharedPreferences sharedPreferences;
    private String status_login;
    private UserRealmObject userProfile;
    private RiwayatTransaksiViewModel riwayatTransaksiViewModel;
    private Context mContext;
    private RiwayatTransaksiAdapter riwayatTransaksiAdapter;


    public static RiwayatTransaksiFragment newInstance(List<StatusTrx> statusLists, int position) {
        RiwayatTransaksiFragment fragment = new RiwayatTransaksiFragment();
        Bundle args = new Bundle();
        statusList = statusLists;
        args.putInt("KEY_POSITION", position);
        args.putInt("KEY_ID", statusList.get(position).getCodeStatus());
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.position = getArguments().getInt("KEY_POSITION");
        }
    }
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
         view = inflater.inflate(R.layout.fragment_riwayat_transaksi, container, false);
         this.mContext = getContext();
         initComponent();

        return view;
    }

    private void initComponent() {
        rvRiwayatTransaksi = view.findViewById(R.id.rv_riwayat_transaksi);

        swipeRefreshLayout = view.findViewById(R.id.swip_riwayat_transaksi);
        swipeRefreshLayout.setColorSchemeColors(android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);

        rvRiwayatTransaksi.setVisibility(View.GONE);

        sharedPreferences = getActivity().getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        Realm.init(getActivity());
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        cekLogin();


        riwayatTransaksiViewModel = new RiwayatTransaksiViewModel(mContext, mApiService, statusList.get(position).getCodeStatus());

        rvRiwayatTransaksi.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        riwayatTransaksiAdapter = new RiwayatTransaksiAdapter(mContext);

        riwayatTransaksiViewModel.getProductLiveData().observe(this, pagedList -> {
            riwayatTransaksiAdapter.submitList(pagedList);
        });

        rvRiwayatTransaksi.setAdapter(riwayatTransaksiAdapter);
        rvRiwayatTransaksi.setVisibility(View.VISIBLE);
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
    }

    private void cekLogin() {
        status_login = sharedPreferences.getString(Enum.isloggedin, "0");
        if(status_login.equals("1")) {
            userProfile = realm.where(UserRealmObject.class).findFirst();

            mApiService = ApiServices.getApiServiceWithToken(userProfile.getToken(), mContext);
        }
    }
}

