package com.happyselling.niznet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.Activities.RegisterActivity;
import com.happyselling.niznet.Models.LoginResponse.Data;
import com.happyselling.niznet.Models.LoginResponse.UserLoginResponse;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;
import com.happyselling.niznet.Utils.PrefUtils;
import com.happyselling.niznet.Utils.Util;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Util util;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvErrorEmail, tvErrorPassword, tvSignUp;
    private boolean isTrueEmail, isTruePassword;
    private BaseApiService mApiService;
    private boolean doubleBackToExitPressedOnce = false;
    private SharedPreferences sharedPreferences;
    private SweetAlertDialog pdLoading;

    private ImageView showPass, hidePass;
    private Realm realm;
    private MainActivity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponent();
    }

    private void initComponent() {
        setStatusBar();
        mainActivity = new MainActivity();
        util = new Util();
        util.hideStatusBar(this);
        isTrueEmail = false;
        isTruePassword = false;

        mApiService = ApiServices.getApiService(LoginActivity.this);
        sharedPreferences = getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tvErrorEmail = findViewById(R.id.tv_error_email);
        tvErrorPassword = findViewById(R.id.tv_error_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_register);

        btnLogin.setEnabled(false);
        tvErrorEmail.setVisibility(View.GONE);
        tvErrorPassword.setVisibility(View.GONE);

        showPass = findViewById(R.id.iv_show_pw_login);
        hidePass = findViewById(R.id.iv_hide_pw_login);
        hidePass.setVisibility(View.GONE);
        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPass.
                        setVisibility(View.GONE);
                hidePass.setVisibility(View.VISIBLE);
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        hidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPass.setVisibility(View.VISIBLE);
                hidePass.setVisibility(View.GONE);
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(etEmail.getText() == null || etEmail.getText().equals("") || etEmail.getText().length() == 0){
                    tvErrorEmail.setVisibility(View.VISIBLE);
                    tvErrorEmail.setText("Email tidak boleh kosong");
                    btnLogin.setEnabled(false);
                    isTrueEmail = false;
                }else {
//                    if (!util.cekEmail(etEmail.getText().toString(), s)) {
//                        tvErrorEmail.setVisibility(View.VISIBLE);
//                        tvErrorEmail.setText("Format email salah");
//                        btnLogin.setEnabled(false);
//                        isTrueEmail = false;
//                    }else{
                        tvErrorEmail.setVisibility(View.GONE);
                        isTrueEmail = true;
                        if(isTruePassword){
                            btnLogin.setEnabled(true);
                        }
//                    }
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(etPassword.getText() == null || etPassword.getText().equals("") || etPassword.getText().length() == 0){
                    tvErrorPassword.setVisibility(View.VISIBLE);
                    tvErrorPassword.setText("Password tidak boleh kosong");
                    isTruePassword = false;
                    btnLogin.setEnabled(false);
                }else{
                    tvErrorPassword.setVisibility(View.GONE);
                    isTruePassword = true;
                    if(isTrueEmail) {
                        btnLogin.setEnabled(true);
                    }
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProccess();
            }
        });
    }

    private void loginProccess(){

        loading();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", etEmail.getText().toString());
        jsonObject.addProperty("password", etPassword.getText().toString());

        mApiService.postLogin(jsonObject).enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                Log.e("###", "onResponse: " +response);
                if(response.isSuccessful()){
                    if(response.body().getMeta().getCode() == 200){
                        pdLoading.dismiss();
                        updateUserToRealm(response.body().getData());
                    }else{

                        pdLoading.dismiss();
                        Toast.makeText(LoginActivity.this, response.body().getMeta().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else if(response.code() == 401){
                    pdLoading.dismiss();
                    Toast.makeText(LoginActivity.this, "Email atau password tidak salah", Toast.LENGTH_SHORT).show();
                }else if(response.code() == 400){
                    pdLoading.dismiss();
                    Toast.makeText(LoginActivity.this, "Data sedang diverifikasi oleh admin", Toast.LENGTH_SHORT).show();
                }else if(response.code() == 404){
                    pdLoading.dismiss();
                    Toast.makeText(LoginActivity.this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {

                pdLoading.dismiss();
                Toast.makeText(LoginActivity.this, "Periksa jaringan internet anda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserToRealm(Data datas) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Enum.isloggedin, "1");
        editor.putString(Enum.logvia, "manual");
        editor.putString(Enum.uid, datas.getId().toString());
        editor.putString(Enum.TOKEN, datas.getToken());
        editor.putString(Enum.ROLE, datas.getIdLevel().toString());
        PrefUtils.setToken(getApplicationContext(), datas.getToken());
        editor.commit();


        realm.beginTransaction();
        UserRealmObject userProfile = realm.createObject(UserRealmObject.class);
        userProfile.setId(datas.getId());
        try{
            userProfile.setId(datas.getId());
        }catch (NullPointerException e){
            userProfile.setId(0);
        }
        try{
            userProfile.setName(datas.getName());
        }catch (NullPointerException e){
            userProfile.setName("");
        }
        try{
            userProfile.setEmail(datas.getEmail());
        }catch (NullPointerException e){
            userProfile.setEmail("");
        }

        try{
            userProfile.setEmailVerifiedAt(datas.getEmailVerifiedAt());
        }catch (NullPointerException e){
            userProfile.setEmailVerifiedAt("");
        }

        try{
            userProfile.setIdLevel(datas.getIdLevel());
        }catch (NullPointerException e){
            userProfile.setIdLevel(0);
        }

        try{
            userProfile.setStatus(datas.getStatus());
        }catch (NullPointerException e){
            userProfile.setStatus(0);
        }
        try{
            userProfile.setTelp(datas.getTelp());
        }catch (NullPointerException e){
            userProfile.setTelp("");
        }
        try{
            userProfile.setAddress(datas.getAddress());
        }catch (NullPointerException e){
            userProfile.setAddress("");
        }
        try{
            userProfile.setPhoto(datas.getPhoto());
        }catch (NullPointerException e){
            userProfile.setPhoto("");
        }
        try{
            userProfile.setKtp(datas.getKtp());
        }catch (NullPointerException e){
            userProfile.setKtp("");
        }
        try{
            userProfile.setToken(datas.getToken());
        }catch (NullPointerException e){
            userProfile.setToken("");
        }
        try{
            userProfile.setCreatedAt(datas.getCreatedAt());
        }catch (NullPointerException e){
            userProfile.setCreatedAt("");
        }
        try{
            userProfile.setUpdatedAt(datas.getUpdatedAt());
        }catch (NullPointerException e){
            userProfile.setUpdatedAt("");
        }

        try{
            userProfile.setPassword(etPassword.getText().toString());
        }catch (NullPointerException e){
            userProfile.setPassword("");
        }
        realm.commitTransaction();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
    private void loading(){
        pdLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pdLoading.getProgressHelper().setBarColor(getResources().getColor(R.color.bg_color));
        pdLoading.setContentText("Loading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setTranslucentForImageView(LoginActivity.this, null);

    }
}
