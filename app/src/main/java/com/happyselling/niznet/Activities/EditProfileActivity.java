package com.happyselling.niznet.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.MainActivity;
import com.happyselling.niznet.Models.LoginResponse.Data;
import com.happyselling.niznet.Models.LoginResponse.UserLoginResponse;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;
import com.happyselling.niznet.Utils.FileUtils;
import com.happyselling.niznet.Utils.PrefUtils;
import com.happyselling.niznet.Utils.Util;
import com.happyselling.niznet.Utils.Views.ImagePickerActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private FrameLayout flPoto, flKtp;

    private EditText etNama, etEmail, etPhone, etAlamat, etPassword, etConfirmPassword;

    private ImageView ivKtp, ivBack, ivSave, showPass, hidePass, showPassConfirm, hidePassConfirm;

    private CircleImageView cvPoto;
    private SweetAlertDialog pdLoading;

    private Realm realm;
    private SharedPreferences sharedPreferences;
    private String status_login;
    private UserRealmObject userProfile;
    private BaseApiService mApiService;
    private Uri gambarFoto, gambarKtp;
    private Util utils = new Util();
    private ArrayList<String> filePaths = new ArrayList<>();
    private static final int PICK_FILE_REQUEST = 1;
    private int typeFile = 0;

    public static final int REQUEST_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initComponent();

    }

    private void initComponent() {
        setStatusBar();
        flKtp = findViewById(R.id.fl_ktp_profile_edit);
        flPoto = findViewById(R.id.fl_poto_profile_edit);

        etNama = findViewById(R.id.et_nama_profile_edit);
        etEmail = findViewById(R.id.et_email_profile_edit);
        etPhone = findViewById(R.id.et_phone_profile_edit);
        etAlamat = findViewById(R.id.et_alamat_profile_edit);
        etPassword = findViewById(R.id.et_password_profile_edit);
        etConfirmPassword = findViewById(R.id.et_confirm_password_profile_edit);

        ivKtp = findViewById(R.id.iv_ktp_profile_edit);
        ivBack = findViewById(R.id.iv_back_detail_profile);
        ivSave = findViewById(R.id.iv_save_edit_profile);

        cvPoto = findViewById(R.id.ci_poto_profile_edit);
        sharedPreferences = getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        cekLogin();
        etNama.setText(userProfile.getName());
        etEmail.setText(userProfile.getEmail());
        etPhone.setText(userProfile.getTelp());
        etAlamat.setText(userProfile.getAddress());
        etPassword.setText(userProfile.getPassword());
        etConfirmPassword.setText(userProfile.getPassword());
        showPass = findViewById(R.id.iv_show_pw_edit);
        hidePass = findViewById(R.id.iv_hide_pw_edit);
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
        showPassConfirm = findViewById(R.id.iv_show_pw_edit_confirm);
        hidePassConfirm = findViewById(R.id.iv_hide_pw_edit_confirm);
        hidePassConfirm.setVisibility(View.GONE);
        showPassConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassConfirm.
                        setVisibility(View.GONE);
                hidePassConfirm.setVisibility(View.VISIBLE);
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        hidePassConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassConfirm.setVisibility(View.VISIBLE);
                hidePassConfirm.setVisibility(View.GONE);
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        if(null != userProfile.getKtp()){
            loadKtp(userProfile.getKtp());
        }

        if(null != userProfile.getPhoto()){
           loadProfile(userProfile.getPhoto());
        }

        flPoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeFile = 0;
                onProfileImageClick();
            }
        });

        flKtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeFile = 1;
                onProfileImageClick();
            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitUpdateProfile();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submitUpdateProfile() {
        loading();
        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            pdLoading.dismiss();
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Password konfirmasi tidak cocok")
                    .hideConfirmButton()
                    .setCancelButtonBackgroundColor(getResources().getColor(R.color.dark_gray))
                    .setCancelText("Tutup")
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        } else {
            MultipartBody.Part potoPropil = null;
            if(gambarFoto != null) {
                File file = FileUtils.getFile(this, gambarFoto);
                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                file
                        );
                potoPropil = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
            }

            MultipartBody.Part potoKtp = null;
            if(gambarKtp != null) {
                File file = FileUtils.getFile(this, gambarKtp);
                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                file
                        );
                potoKtp = MultipartBody.Part.createFormData("ktp", file.getName(), requestFile);
            }
            RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"),
                    null == etNama.getText() ? null : etNama.getText().toString());
            RequestBody email = RequestBody.create(MediaType.parse("multipart/form-data"),
                    null == etEmail.getText() ? null : etEmail.getText().toString());
            RequestBody password = RequestBody.create(MediaType.parse("multipart/form-data"),
                    null == etPassword.getText() ? null : etPassword.getText().toString());
            RequestBody phone = RequestBody.create(MediaType.parse("multipart/form-data"),
                    null == etPhone.getText() ? null : etPhone.getText().toString());
            RequestBody alamat = RequestBody.create(MediaType.parse("multipart/form-data"),
                    null == etAlamat.getText() ? null : etAlamat.getText().toString());
            mApiService.updateProfile(name, email, password,
                    phone,alamat,potoPropil, potoKtp)
                    .enqueue(new Callback<UserLoginResponse>() {
                @Override
                public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                    if(response.isSuccessful()){
                            updateRealmNew(response.body().getData());
                    }else{
                        pdLoading.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Gagal update profil", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                    pdLoading.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Gagal update profil", Toast.LENGTH_SHORT).show();
                    Log.e("####", "onFailure: "+t.getMessage());
                }
            });
        }
    }

    private void updateRealmNew(Data datas) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Enum.isloggedin, "1");
        editor.putString(Enum.logvia, "manual");
        editor.putString(Enum.uid, datas.getId().toString());
        editor.putString(Enum.TOKEN, datas.getToken());
        editor.putString(Enum.ROLE, datas.getIdLevel().toString());
        PrefUtils.setToken(getApplicationContext(), datas.getToken());
        editor.commit();


        realm.beginTransaction();

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
        pdLoading.dismiss();
        realm.commitTransaction();
        startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setColor(this, this.getResources().getColor(R.color.white), 0);
    }

    private void cekLogin() {
        status_login = sharedPreferences.getString(Enum.isloggedin, "0");
        if(status_login.equals("1")) {
            userProfile = realm.where(UserRealmObject.class).findFirst();

            mApiService = ApiServices.getApiServiceWithToken(userProfile.getToken(), EditProfileActivity.this);
        }
    }

    private void onProfileImageClick() {
        showImagePickerOptions();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String selectedFilePath = "";
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    if (typeFile == 1) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                        // loading profile image from local cache
                        gambarKtp = uri;
                        loadKtp(uri.toString());
                    } else if (typeFile == 0) {
                        // You can update this bitmap to your server
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                        // loading profile image from local cache
                        gambarFoto = uri;
                        loadProfile(uri.toString());

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void loadProfile(String url) {
        Log.d("###", "Image cache path: " + url);
        Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(cvPoto);
//        cIPoto.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void loadKtp(String url) {
        Log.d("###", "Image cache path: " + url);
        Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(ivKtp);
//        cIPoto.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void loading(){
        pdLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pdLoading.getProgressHelper().setBarColor(getResources().getColor(R.color.bg_color));
        pdLoading.setContentText("Loading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
    }
}
