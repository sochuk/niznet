package com.happyselling.niznet.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.LoginActivity;
import com.happyselling.niznet.Models.Meta;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.FileUtils;
import com.happyselling.niznet.Utils.Util;
import com.happyselling.niznet.Utils.Views.ImagePickerActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword, etNama, etAlamat, etPhone;

    private ImageView ivKtp;

    private Button btnNext, btnRegister;

    private TextView tvErrorEmail, tvErrorPassword, tvErrorConfirmPassword, tvLogin;

    private LinearLayout llAkun, llProfil;

    private FrameLayout flKtp;

    private boolean isTrueEmail, isTruePassword, isTrueConfirmPassword;

    private Util util = new Util();
    private ImageView showPass, hidePass, showPassConfirm, hidePassConfirm;

    private BaseApiService mApiService;
    private SweetAlertDialog pdLoading;
    private Uri gambarKtp;
    private static final int PICK_FILE_REQUEST = 1;
    private int typeFile = 0;

    public static final int REQUEST_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponent();
    }

    private void initComponent() {
        setStatusBar();
        mApiService = ApiServices.getApiService(RegisterActivity.this);
        etEmail = findViewById(R.id.et_email_reg);
        etPassword = findViewById(R.id.et_password_reg);
        etConfirmPassword= findViewById(R.id.et_password_confirm_reg);
        etNama = findViewById(R.id.et_nama_reg);
        etAlamat = findViewById(R.id.et_alamat_reg);
        etPhone = findViewById(R.id.et_telp_reg);

        ivKtp = findViewById(R.id.iv_ktp_profile_reg);

        btnNext = findViewById(R.id.btn_next_reg);
        btnRegister = findViewById(R.id.btn_register);

        tvErrorEmail = findViewById(R.id.tv_error_email_reg);
        tvErrorPassword = findViewById(R.id.tv_error_password_reg);
        tvErrorConfirmPassword = findViewById(R.id.tv_error_password_confirm_reg);
        tvLogin = findViewById(R.id.tv_login);

        llAkun = findViewById(R.id.ll_akun_reg);
        llProfil = findViewById(R.id.ll_detail_profile_reg);

        flKtp = findViewById(R.id.fl_ktp_profile_reg);
        llAkun.setVisibility(View.VISIBLE);
        llProfil.setVisibility(View.GONE);

        tvErrorEmail.setVisibility(View.GONE);
        tvErrorPassword.setVisibility(View.GONE);
        tvErrorConfirmPassword.setVisibility(View.GONE);
        showPass = findViewById(R.id.iv_show_pw_reg);
        hidePass = findViewById(R.id.iv_hide_pw_reg);
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
        showPassConfirm = findViewById(R.id.iv_show_pw_reg_confirm);
        hidePassConfirm = findViewById(R.id.iv_hide_pw_reg_confirm);
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
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
        btnNext.setEnabled(false);
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
                    btnNext.setEnabled(false);
                    isTrueEmail = false;
                }else {
                    if (!util.cekEmail(etEmail.getText().toString(), s)) {
                        tvErrorEmail.setVisibility(View.VISIBLE);
                        tvErrorEmail.setText("Format email salah");
                        btnNext.setEnabled(false);
                        isTrueEmail = false;
                    }else{
                        tvErrorEmail.setVisibility(View.GONE);
                        isTrueEmail = true;
                        if(isTruePassword && isTrueConfirmPassword){
                            btnNext.setEnabled(true);
                        }
                    }
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
                    btnNext.setEnabled(false);
                }else{
                    tvErrorPassword.setVisibility(View.GONE);
                    isTruePassword = true;
                    if(isTrueEmail && isTrueConfirmPassword) {
                        btnNext.setEnabled(true);
                    }
                }
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               if (!etConfirmPassword.getText().toString().equals(etPassword.getText().toString())) {
                        tvErrorConfirmPassword.setVisibility(View.VISIBLE);
                        tvErrorConfirmPassword.setText("Password tidak cocok");
                        isTrueConfirmPassword = false;
                        btnNext.setEnabled(false);
                    } else {
                        tvErrorConfirmPassword.setVisibility(View.GONE);
                        isTrueConfirmPassword = true;
                        if (isTrueEmail && isTruePassword) {
                            btnNext.setEnabled(true);
                        }
                    }
                }

        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("email", etEmail.getText().toString());
                mApiService.checkEmailReg(jsonObject).enqueue(new Callback<Meta>() {
                    @Override
                    public void onResponse(Call<Meta> call, Response<Meta> response) {
                        Log.e("###", "chekemail: "+response);
                        if(response.code() == 200){
                                pdLoading.dismiss();
                                llAkun.setVisibility(View.GONE);
                                llProfil.setVisibility(View.VISIBLE);

                        }else if(response.code() == 400){
                            pdLoading.dismiss();
                            llAkun.setVisibility(View.VISIBLE);
                            llProfil.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Email telah terdaftar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Meta> call, Throwable t) {
                        pdLoading.dismiss();
                        llAkun.setVisibility(View.VISIBLE);
                        llProfil.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Periksa jaringan internet anda", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        flKtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeFile = 1;
                onProfileImageClick();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkNullInput() == true){
//                    if(checkKtpNull() == true){
                        submitRegister();
//                    }else{
//                        new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
//                                .setContentText("Harus menyertakan file KTP")
//                                .hideConfirmButton()
//                                .setCancelButtonBackgroundColor(getResources().getColor(R.color.dark_gray))
//                                .setCancelText("Tutup")
//                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                        sweetAlertDialog.dismissWithAnimation();
//                                    }
//                                })
//                                .show();
//                    }
                }else{
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Form tidak boleh kosong")
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
                }
            }
        });
    }

    private void submitRegister() {
        loading();
//        MultipartBody.Part potoKtp = null;
//        if(gambarKtp != null) {
//            File file = FileUtils.getFile(this, gambarKtp);
//            RequestBody requestFile =
//                    RequestBody.create(
//                            MediaType.parse("multipart/form-data"),
//                            file
//                    );
//            potoKtp = MultipartBody.Part.createFormData("ktp", file.getName(), requestFile);
//        }
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"),
                null == etNama.getText() ? null : etNama.getText().toString());
        RequestBody email = RequestBody.create(MediaType.parse("multipart/form-data"),
                null == etEmail.getText() ? null : etEmail.getText().toString());
        RequestBody password = RequestBody.create(MediaType.parse("multipart/form-data"),
                null == etPassword.getText() ? null : etPassword.getText().toString());
        RequestBody passwordConfirm = RequestBody.create(MediaType.parse("multipart/form-data"),
                null == etConfirmPassword.getText() ? null : etConfirmPassword.getText().toString());
        RequestBody phone = RequestBody.create(MediaType.parse("multipart/form-data"),
                null == etPhone.getText() ? null : etPhone.getText().toString());
        RequestBody alamat = RequestBody.create(MediaType.parse("multipart/form-data"),
                null == etAlamat.getText() ? null : etAlamat.getText().toString());
        RequestBody idLevel = RequestBody.create(MediaType.parse("multipart/form-data"),
                "2");
        mApiService.postRegister(name,email,idLevel,password,passwordConfirm,phone,alamat).enqueue(new Callback<Meta>() {
            @Override
            public void onResponse(Call<Meta> call, Response<Meta> response) {
                if(response.code() == 200){
                        pdLoading.dismiss();
                        new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("Register berhasil, silahkan login")
                                .setConfirmButtonBackgroundColor(getResources().getColor(R.color.green))
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        RegisterActivity.this.finish();
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .show();
                }else if(response.code() == 400){
                    pdLoading.dismiss();
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Gagal mendaftarkan akun, silahkan hubungi admin")
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
                }
            }
            @Override
            public void onFailure(Call<Meta> call, Throwable t) {
                pdLoading.dismiss();
                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Periksa koneksi internet anda")
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
            }
        });
    }

    private boolean checkNullInput() {
        String name = (null == etNama.getText() ? "": etNama.getText().toString().trim());
        String alamat = (null == etAlamat.getText() ? "": etAlamat.getText().toString().trim());
        String phone = (null == etPhone.getText() ? "" : etPhone.getText().toString().trim());

        if(name.isEmpty() || name.length() == 0 || name.equals("") || name == null
        || alamat.isEmpty() || alamat.length() == 0 || alamat.equals("") || alamat == null
        || phone.isEmpty() || phone.length() == 0 || phone.equals("") || phone == null) {
            return false;
        }else{
            return true;
        }
    }

    private boolean checkKtpNull(){
        if(null == gambarKtp){
            return false;
        }else{
            return true;
        }
    }

    private void loading(){
        pdLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pdLoading.getProgressHelper().setBarColor(getResources().getColor(R.color.bg_color));
        pdLoading.setContentText("Loading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadKtp(String url) {
        Log.d("###", "Image cache path: " + url);
        Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(ivKtp);
//        cIPoto.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setTranslucentForImageView(RegisterActivity.this, null);

    }
}
