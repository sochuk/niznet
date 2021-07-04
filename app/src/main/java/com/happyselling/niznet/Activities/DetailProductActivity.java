package com.happyselling.niznet.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.LoginActivity;
import com.happyselling.niznet.Models.Meta;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;
import com.happyselling.niznet.Utils.Util;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

public class DetailProductActivity extends AppCompatActivity {

    private TextView tvCategory, tvProductName, tvProductPrice, tvProductDesc, tvProductStock, tvProductBrand, tvChartLot,tvVariasiName;
    private Button btnAddCart;
    private ImageView ivPlus, ivMinus, ivProduct;
    private EditText etLotCart;
    private String nama_produk, id_produk, harga_produk, stok_produk, gambar_produk1, gambar_produk2, gambar_produk3, gambar_produk4,
            kategori_produk, brand_produk, deskripsi_produk, brand_name, category_name,variasi_produk, variasi_name;

    private NestedScrollView scrollView;
    private CarouselView  crs_product_detail;
    private List<String> gambarList;
    private Realm realm;
    private SharedPreferences sharedPreferences;
    private String status_login;
    private UserRealmObject userProfile;
    private BaseApiService mApiService;
    private SweetAlertDialog pdLoading;

    private SegmentedControl sgVarian;
    private LinearLayout lyVarian;

    private String[] variasiProduk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        initComponent();

    }
    @SuppressLint("ResourceType")
    private void initComponent() {

        scrollView = findViewById(R.id.sv_detail_produk);
        setStatusBar();
        crs_product_detail = findViewById(R.id.crs_product_detail);
        tvCategory = findViewById(R.id.tv_category_detail_product);
        tvProductName = findViewById(R.id.tv_nama_detail_product);
        tvProductPrice = findViewById(R.id.tv_harga_detail_product);
        tvProductDesc = findViewById(R.id.tv_deskripsi_detail_product);
        tvProductStock = findViewById(R.id.tv_stok_detail_product);
        tvProductBrand = findViewById(R.id.tv_brand_detail_product);
        etLotCart = findViewById(R.id.et_cart_lot_detail_product);
        etLotCart.setText("0");
        btnAddCart = findViewById(R.id.btn_add_cart_detail_product);
        ivPlus = findViewById(R.id.iv_plus_stok_detail_product);
        ivMinus = findViewById(R.id.iv_minus_stok_detail_product);
        tvVariasiName = findViewById(R.id.txt_variasi_name);

        sgVarian = (SegmentedControl)findViewById(R.id.sg_varian);
        lyVarian = findViewById(R.id.ly_variasi);
        lyVarian.setVisibility(View.GONE);

        Intent i = getIntent();
        nama_produk = i.getStringExtra("nama_produk");
        id_produk = i.getStringExtra("id_produk");
        harga_produk = i.getStringExtra("harga_produk");
        stok_produk = i.getStringExtra("stok_produk");
        gambar_produk1 = i.getStringExtra("gambar_produk1");
        gambar_produk2 = i.getStringExtra("gambar_produk2");
        gambar_produk3 = i.getStringExtra("gambar_produk3");
        gambar_produk4 = i.getStringExtra("gambar_produk4");
        kategori_produk = i.getStringExtra("category_code");
        brand_produk = i.getStringExtra("brand_code");
        deskripsi_produk = i.getStringExtra("deskripsi_produk");
        brand_name = i.getStringExtra("brand_name");
        category_name = i.getStringExtra("category_name");
        variasi_produk = i.getStringExtra("variasi");
        variasi_name = i.getStringExtra("variasi_name");
        Log.d("###", "variasi: "+variasi_produk);
        if(null != variasi_produk){
            if(!variasi_produk.equals("")) {
                tvVariasiName.setText(variasi_name);
                variasiProduk = variasi_produk.split("\\,", -1);
                lyVarian.setVisibility(View.VISIBLE);
                sgVarian.addSegments(variasiProduk);
                sgVarian.setColumnCount(variasiProduk.length);
                sgVarian.setSelectedSegment(0);
                sgVarian.notifyConfigIsChanged();
//            variasiProduk[sgVarian.getLastSelectedAbsolutePosition()]
            }
        }

        gambarList = new ArrayList<>();
        gambarList.add(gambar_produk1);
        gambarList.add(gambar_produk2);
        gambarList.add(gambar_produk3);
        gambarList.add(gambar_produk4);
        addPhoto();
        tvCategory.setText(category_name);
        tvProductName.setText(nama_produk);
        tvProductPrice.setText(Util.parsingRupiah(Integer.parseInt(harga_produk)));
        tvProductDesc.setText(Html.fromHtml(deskripsi_produk));
        tvProductStock.setText("Stok tersedia : "+stok_produk);
        tvProductBrand.setText(brand_name);
        final int [] number = {0};
        etLotCart.setText(""+number[0]);
        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number[0] == 0){
                    etLotCart.setText(""+number[0]);
                }
                if(number[0]>0){
                    number[0] = number[0]-1;
                    etLotCart.setText(""+number[0]);
                }
            }
        });

        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number[0]==Integer.parseInt(stok_produk)){
                    etLotCart.setText(""+number[0]);
                }
                if (number[0]<Integer.parseInt(stok_produk)){
                    number[0] = number[0] + 1;
                    etLotCart.setText(""+number[0]);
                }
            }
        });
        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtnAddCart();
            }
        });
    }

    private void cekLogin() {
        status_login = sharedPreferences.getString(Enum.isloggedin, "0");
        if(status_login.equals("1")) {
            userProfile = realm.where(UserRealmObject.class).findFirst();

            mApiService = ApiServices.getApiServiceWithToken(userProfile.getToken(), DetailProductActivity.this);
        }
    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
           getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setTranslucentForImageView(DetailProductActivity.this, null);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY>scrollX){
//                    appBarLayout.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                    StatusBarUtil.setColor(DetailProductActivity.this, getResources().getColor(R.color.white), 0);
                } else {
//                    appBarLayout.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                    StatusBarUtil.setTranslucentForImageView(DetailProductActivity.this, null);
                }
            }
        });
    }


    @SuppressLint("ResourceType")
    private void setSegmentVariasiButton(){

    }

    private void addPhoto() {
        crs_product_detail.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Picasso.get().load(gambarList.get(position))
                        .into(imageView);
            }
        });
        crs_product_detail.setPageCount(gambarList.size());
    }

    private void setBtnAddCart(){
        sharedPreferences = getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        cekLogin();

        if(status_login.equals("1")){
            if(etLotCart.getText().toString().equals("0")){
                Toast.makeText(this, "Anda harus menambahkan jumlah pesanan", Toast.LENGTH_SHORT).show();
            }else {
                loading();
                Integer totalHarga = Integer.parseInt(harga_produk) * Integer.parseInt(etLotCart.getText().toString());
                JsonObject cartParams = new JsonObject();
                cartParams.addProperty("id_product", Integer.parseInt(id_produk));
                cartParams.addProperty("id_user", Integer.parseInt(userProfile.getId().toString()));
                cartParams.addProperty("quantity", Integer.parseInt(etLotCart.getText().toString()));
                cartParams.addProperty("total_harga", totalHarga);
                if (null != variasi_produk){
                    if(!variasi_produk.equals(""))
                    cartParams.addProperty("variasi", variasiProduk[sgVarian.getLastSelectedAbsolutePosition()]);
                    else
                        cartParams.addProperty("variasi", "");
                }else{
                    cartParams.addProperty("variasi", "");
                }

                mApiService.postCart(cartParams).enqueue(new Callback<Meta>() {
                    @Override
                    public void onResponse(Call<Meta> call, Response<Meta> response) {
                        if (response.isSuccessful()) {
                                pdLoading.dismiss();
                                Intent intent = new Intent(DetailProductActivity.this, CartListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                        } else {
                            pdLoading.dismiss();
                            Toast.makeText(DetailProductActivity.this, "Gagal menambahkan kedalam keranjang :"+response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Meta> call, Throwable t) {
                        pdLoading.dismiss();
                        Toast.makeText(DetailProductActivity.this, "Gagal menambahkan kedalam keranjang :"+t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }else{
            Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    public void onBackClick(View view) {
        finish();
    }

    private void loading(){
        pdLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pdLoading.getProgressHelper().setBarColor(getResources().getColor(R.color.bg_color));
        pdLoading.setContentText("Loading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
    }
}
