package com.happyselling.niznet.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.JsonObject;
import com.happyselling.niznet.Models.Meta;
import com.happyselling.niznet.Utils.FileUtils;
import com.happyselling.niznet.Utils.Views.ImagePickerActivity;
import com.jaeger.library.StatusBarUtil;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.happyselling.niznet.Activities.RiwayatTransaksiPage.RiwayatTransaksiActivity;
import com.happyselling.niznet.Adapters.ProductListDetailTrxAdapter;
import com.happyselling.niznet.Models.DetailTransaksiResponse.Data;
import com.happyselling.niznet.Models.DetailTransaksiResponse.DetailTrx;
import com.happyselling.niznet.Models.DetailTransaksiResponse.Product;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;
import com.happyselling.niznet.Utils.Util;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTrxActivity extends AppCompatActivity {

    private CircularProgressBar circularProgressBar;
    private ImageView ivVerif,ivSend;
    private View lineVerif;

    private TextView tvPaymentMethod, tvPersenProgress, tvKodeCheckout, tvStatusTopRect, tvNamaPenerima, tvAlamatPenerima, tvTelpPenerima, tvHargaPenerima;

    private LinearLayout llTopRect;

    private int typeFile = 0;
    private Uri uriGambar;

    public static final int REQUEST_IMAGE = 100;

    private Integer idTrx, statusPengiriman;
    private SweetAlertDialog pdLoading;

    private ImageView ibBack;
    private Realm realm;
    private SharedPreferences sharedPreferences;
    private String status_login;
    private UserRealmObject userProfile;
    private BaseApiService mApiService;
    private Handler handler = new Handler();

    private ProductListDetailTrxAdapter productListDetailTrxAdapter;

    private RecyclerView rvProductDetail;
    private List<Product> listProductDetail;
    private ProgressBar progressBar;
    private NestedScrollView scrollView;
    private RelativeLayout relativeLayout;
    private PullRefreshLayout pullRefreshLayout;
    private LinearLayout llBuktiPembayaran;
    private ImageView ivBuktiPembayaran;
    private Button btnUploadPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trx);
        initComponent();
    }

    private void initComponent() {
        setStatusBar();

        sharedPreferences = this.getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        cekLogin();
        circularProgressBar = findViewById(R.id.circle_persen_progress);
        progressBar = findViewById(R.id.pb_detail_trx);
        scrollView = findViewById(R.id.scroll_detail_trx);
        relativeLayout = findViewById(R.id.rl_detail_trx);
        pullRefreshLayout = findViewById(R.id.swip_detail_trx);

        ivVerif = findViewById(R.id.iv_verif_progres);
        ivSend = findViewById(R.id.iv_send_progres);

        lineVerif = findViewById(R.id.line_verif_progres);

        tvPersenProgress = findViewById(R.id.tv_persen_progress);
        tvKodeCheckout = findViewById(R.id.tv_code_checkout_detail_trx);
        tvStatusTopRect = findViewById(R.id.tv_status_trx_detail_trx);
        tvNamaPenerima = findViewById(R.id.tv_nama_user_detail_trx);
        tvAlamatPenerima = findViewById(R.id.tv_alamat_user_detail_trx);
        tvTelpPenerima = findViewById(R.id.tv_telp_user_detail_trx);
        tvHargaPenerima = findViewById(R.id.tv_total_harga_detail_trx);
        tvPaymentMethod = findViewById(R.id.tv_payment_method_detail_trx);

        btnUploadPayment = findViewById(R.id.btn_upload_payment);

        rvProductDetail = findViewById(R.id.rv_product_detail_trx);
        rvProductDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        rvBrands.setLayoutManager(layoutManagerBrands);
        rvProductDetail.setItemAnimator(new DefaultItemAnimator());

        llTopRect = findViewById(R.id.ll_top_rect_detail_trx);
        llBuktiPembayaran = findViewById(R.id.ll_bukti_pembayaran_detail_trx);
        ivBuktiPembayaran = findViewById(R.id.iv_buktiPembayaran_detail_trx);


        ibBack = findViewById(R.id.iv_back_detail_checkout);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailTrxActivity.this, RiwayatTransaksiActivity.class));
                overridePendingTransition(R.anim.zoom, R.anim.zoom);
                finish();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);
        Intent i = getIntent();
        idTrx = i.getIntExtra("idTrx", 0);
        initValue(idTrx);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initComponent();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });
    }


    private void initValue(Integer idTrxs) {
        mApiService.getDetailTransaksi(idTrxs).enqueue(new Callback<DetailTrx>() {
            @Override
            public void onResponse(Call<DetailTrx> call, Response<DetailTrx> response) {
                if(response.isSuccessful()){
                    setProgresDeliver(Integer.parseInt(response.body().getData().getStatus()), response.body().getData());
                    setDetailUser(response.body().getData());
                    listProductDetail = response.body().getData().getProduct();
                    productListDetailTrxAdapter = new ProductListDetailTrxAdapter(
                            DetailTrxActivity.this,listProductDetail);
                    productListDetailTrxAdapter.notifyDataSetChanged();
                    rvProductDetail.setAdapter(productListDetailTrxAdapter);
                    progressBar.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(DetailTrxActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DetailTrx> call, Throwable t) {
                Toast.makeText(DetailTrxActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDetailUser(Data data) {
        tvKodeCheckout.setText(data.getCheckoutCode());
        tvNamaPenerima.setText(data.getName());
        tvAlamatPenerima.setText(data.getAddress());
        tvTelpPenerima.setText(data.getTelp());
        tvHargaPenerima.setText(Util.parsingRupiah(Integer.parseInt(data.getTotalPrice().replaceAll("\\.0*$", ""))));
        tvPaymentMethod.setText(data.getPaymentMethod());
        if(data.getPaymentMethod().equals("TUNAI")){
           btnUploadPayment.setText("KONFIRMASI PEMBAYARAN");
           btnUploadPayment.setCompoundDrawables(null, null, null,null);
           btnUploadPayment.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    submitPesanan();
               }
           });
        }else{
            btnUploadPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeFile = 1;
                    postBuktiPembayaran();
                }
            });
        }

        loadGambar(data.getPhotoPayment());
    }

    private void setProgresDeliver(Integer statusPengirimanss, Data datas){
        switch (statusPengirimanss){
            case 1:
                llTopRect.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_gray)));
                tvStatusTopRect.setText("Menunggu Pembayaran");
                circularProgressBar.setBackgroundColor(getResources().getColor(R.color.yellow));
                tvPersenProgress.setText("25%");
                tvPersenProgress.setTextColor(getResources().getColor(R.color.yellow));
                llBuktiPembayaran.setVisibility(View.GONE);
                btnUploadPayment.setVisibility(View.VISIBLE);
                break;
            case 2:
                llTopRect.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
                tvStatusTopRect.setText("Menunggu Konfirmasi");
                circularProgressBar.setBackgroundColor(getResources().getColor(R.color.yellow));
                tvPersenProgress.setText("70%");
                ivVerif.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_crircle));
                tvPersenProgress.setTextColor(getResources().getColor(R.color.yellow));
                  if(datas.getPaymentMethod().equals("TUNAI")){
                    llBuktiPembayaran.setVisibility(View.GONE);
                }else {
                    llBuktiPembayaran.setVisibility(View.VISIBLE);
                }
                btnUploadPayment.setVisibility(View.GONE);
                  break;
            case 3:
                llTopRect.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_green)));
                tvStatusTopRect.setText("Transaksi Selesai");
                circularProgressBar.setBackgroundColor(getResources().getColor(R.color.dark_green));
                lineVerif.setBackgroundColor(getResources().getColor(R.color.dark_green));
                tvPersenProgress.setText("100%");
                tvPersenProgress.setTextColor(getResources().getColor(R.color.dark_green));
                ivVerif.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_crircle));
                ivSend.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_crircle));
                if(datas.getPaymentMethod().equals("TUNAI")){
                    llBuktiPembayaran.setVisibility(View.GONE);
                }else {
                    llBuktiPembayaran.setVisibility(View.VISIBLE);
                }
                btnUploadPayment.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void postBuktiPembayaran() {
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
                        uriGambar = uri;
                        postBuktiPembayaranToServer(uri);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void postBuktiPembayaranToServer(Uri uris) {
        loading();
        MultipartBody.Part potoBukti = null;
        if(uris != null) {
            File file = FileUtils.getFile(this, uris);
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            file
                    );
            potoBukti = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
        }
        RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"),
                idTrx.toString());
        mApiService.postBuktiPembayaran(id, potoBukti).enqueue(new Callback<Meta>() {
            @Override
            public void onResponse(Call<Meta> call, Response<Meta> response) {
                if(response.isSuccessful()) {
                    pdLoading.dismiss();
                    new SweetAlertDialog(DetailTrxActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Selamat, transaksi anda telah berhasil")
                            .setConfirmButtonBackgroundColor(getResources().getColor(R.color.green))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    startActivity(new Intent(DetailTrxActivity.this, RiwayatTransaksiActivity.class));
                                    DetailTrxActivity.this.overridePendingTransition(R.anim.zoom, R.anim.zoom);
                                    DetailTrxActivity.this.finish();
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                }else{
                    pdLoading.dismiss();
                    new SweetAlertDialog(DetailTrxActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Sayang sekali, transaksi anda gagal")
                            .setConfirmButtonBackgroundColor(getResources().getColor(R.color.green))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                }
            }
            @Override
            public void onFailure(Call<Meta> call, Throwable t) {
                pdLoading.dismiss();
                new SweetAlertDialog(DetailTrxActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Sayang sekali, transaksi anda gagal")
                        .setConfirmButtonBackgroundColor(getResources().getColor(R.color.green))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }


    private void submitPesanan() {
        loading();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id_master_trx", idTrx);
        jsonObject.addProperty("status", 2);
        mApiService.postSubmitPesanan(jsonObject).enqueue(new Callback<Meta>() {
            @Override
            public void onResponse(Call<Meta> call, Response<Meta> response) {
                if(response.isSuccessful()){
                    pdLoading.dismiss();
                    new SweetAlertDialog(DetailTrxActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Selamat, transaksi anda berhasil")
                            .setConfirmButtonBackgroundColor(getResources().getColor(R.color.green))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    startActivity(new Intent(DetailTrxActivity.this, RiwayatTransaksiActivity.class));
                                    DetailTrxActivity.this.overridePendingTransition(R.anim.zoom, R.anim.zoom);
                                    DetailTrxActivity.this.finish();
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                }else{
                    pdLoading.dismiss();
                    Toast.makeText(DetailTrxActivity.this, "Gagal menanggapi pesanan, silahkan mencoba beberapa saat lagi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Meta> call, Throwable t) {
                pdLoading.dismiss();
                Toast.makeText(DetailTrxActivity.this, "Gagal menanggapi pesanan, silahkan mencoba beberapa saat lagi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setColor(DetailTrxActivity.this, getResources().getColor(R.color.white), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DetailTrxActivity.this, RiwayatTransaksiActivity.class));
        overridePendingTransition(R.anim.zoom, R.anim.zoom);
        finish();
    }

    private void cekLogin() {
        status_login = sharedPreferences.getString(Enum.isloggedin, "0");
        if (status_login.equals("1")) {
            userProfile = realm.where(UserRealmObject.class).findFirst();

            mApiService = ApiServices.getApiServiceWithToken(userProfile.getToken(), DetailTrxActivity.this);
        }
    }

    private void loading(){
        pdLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pdLoading.getProgressHelper().setBarColor(getResources().getColor(R.color.bg_color));
        pdLoading.setContentText("Loading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
    }

    private void loadGambar(String url) {
        Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(ivBuktiPembayaran);
//        cIPoto.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

}
