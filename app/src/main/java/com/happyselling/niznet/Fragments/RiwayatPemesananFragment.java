package com.happyselling.niznet.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;

import io.realm.Realm;
import io.realm.RealmConfiguration;
public class RiwayatPemesananFragment extends Fragment {

    private Realm realm;
    private SharedPreferences sharedPreferences;
    private String status_login;
    private UserRealmObject userProfile;
    private BaseApiService mApiService;
    private Context context;


    private View view;
    private AsyncTask<Void, Void, String> setBrands;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initComponent();
        this.context = getContext();
        return view;
    }

    private void initComponent() {
        mApiService = ApiServices.getApiService(context);
        sharedPreferences = getActivity().getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        Realm.init(getActivity());
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        cekLogin();


    }

    private void cekLogin() {
        status_login = sharedPreferences.getString(Enum.isloggedin, "0");
        if (status_login.equals("1")) {
            userProfile = realm.where(UserRealmObject.class).findFirst();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}