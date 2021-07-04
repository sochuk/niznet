package com.happyselling.niznet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.happyselling.niznet.Activities.ProductPage.ProductListActivity;
import com.happyselling.niznet.Activities.RiwayatTransaksiPage.RiwayatTransaksiActivity;
import com.happyselling.niznet.Fragments.HomeFragment;
import com.happyselling.niznet.Fragments.ProfileFragment;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.Utils.Enum;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity  extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Realm realm;
    private SharedPreferences sharedPreferences;
    private String status_login;
    private UserRealmObject userProfile;
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
    }

    public void initComponent(){
        sharedPreferences = getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        cekLogin();
//        if(status_login.equals("0")) {
            grantPermissions();
//        }
        setupWindowAnimation();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
//        setStatusBarGradiant(this);
        loadFragment(new HomeFragment());
        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {

            case R.id.bottomNavigationHome:
                  fragment = new HomeFragment();
                break;
            case R.id.bottomNavigationPesanan:
                if(status_login.equals("1")){
                    startActivity(new Intent(this, RiwayatTransaksiActivity.class));
                }else{
                    Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.bottomNavigationProfile:
                if(status_login.equals("1")){
                    fragment = new ProfileFragment();
                }else{
                    Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentLoad, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void grantPermissions(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    private void cekLogin() {
        status_login = sharedPreferences.getString(Enum.isloggedin, "0");
    }


    private void setupWindowAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Explode explode = new Explode();
            explode.setDuration(2000);
            getWindow().setEnterTransition(explode);
        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
//                Intent startIntent = new Intent(getApplicationContext(), NotifyService.class);
//                startActivity(startIntent);
                doubleBackToExitPressedOnce=false;


            }
        }, 2000);
    }

    public void GoToProductListPage(View view) {
        Intent i = new Intent(MainActivity.this, ProductListActivity.class);
        i.putExtra("parameter_search", "");
        startActivity(i);
    }

    public void showFragTwo(){
        sharedPreferences = getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        cekLogin();
//        if(status_login.equals("0")) {
        grantPermissions();
//        }
        setupWindowAnimation();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        FragmentManager manager = getSupportFragmentManager();
        ProfileFragment frag = new ProfileFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_profile, frag);
        transaction.commit();

    }
}

