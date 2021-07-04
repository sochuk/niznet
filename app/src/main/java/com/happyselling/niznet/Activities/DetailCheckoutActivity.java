package com.happyselling.niznet.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.happyselling.niznet.Models.MasterBankResponse.MasterBank;
import com.happyselling.niznet.Models.MasterPaymentMethod.PaymentMethod;
import com.happyselling.niznet.Models.MasterTokoResponse.MasterToko;
import com.happyselling.niznet.Utils.ConvertStringListToStringArray;
import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.Activities.RiwayatTransaksiPage.RiwayatTransaksiActivity;
import com.happyselling.niznet.Adapters.DetailCheckoutAdapter;
import com.happyselling.niznet.Models.CartResponse.Datum;
import com.happyselling.niznet.Models.Meta;
import com.happyselling.niznet.Models.SharedCartItem;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import jrizani.jrspinner.JRSpinner;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailCheckoutActivity extends AppCompatActivity {

    private DetailCheckoutAdapter detailCheckoutAdapter;

    private RecyclerView recyclerView;

    private ImageView ibBack;
    private ShimmerFrameLayout shimmerFrameLayout;
    private PullRefreshLayout swipeRefreshLayout;
    private final Handler handler = new Handler();
    private BaseApiService mApiService, mApiServiceWithoutToken;
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
    private String totalHarga;
    private List<Datum> cartResultList;
    private SharedCartItem sharedCartItem;

    private Button btnCheckout;

    private TextView tvTotalHarga;
    private JRSpinner spinToko, spinPaymentMethod, spinNamaBank;
    private SweetAlertDialog pdLoading;
    private EditText etToko, etPaymentMethod, etNamaBank;


    private List<com.happyselling.niznet.Models.MasterTokoResponse.Datum> tokoList;
    private List<com.happyselling.niznet.Models.MasterPaymentMethod.Datum> paymentMethodList;
    private List<com.happyselling.niznet.Models.MasterBankResponse.Datum> masterBankList;

    private LinearLayout lyNamaBank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_checkout);
        initComponent();
    }

    private void initComponent() {
        setStatusBar();
        mContext = this;
        mApiServiceWithoutToken = ApiServices.getApiService(DetailCheckoutActivity.this);

        Intent i = getIntent();
        cartResultList = (List<Datum>) i.getSerializableExtra("cartList");
        totalHarga = i.getStringExtra("totalHarga");


        recyclerView = findViewById(R.id.rv_detail_checkout);

        ibBack = findViewById(R.id.iv_back_detail_checkout);

        tvTotalHarga = findViewById(R.id.tv_total_harga_detail_checkout);
        spinToko = findViewById(R.id.spin_reg_toko);
        etToko = findViewById(R.id.et_toko);
        setMasterToko();

        spinPaymentMethod = findViewById(R.id.spin_payment_method);
        etPaymentMethod = findViewById(R.id.et_payment_method);
        setPaymentMethod();

        lyNamaBank = findViewById(R.id.ly_nama_bank);
        spinNamaBank = findViewById(R.id.spin_nama_bank);
        etNamaBank = findViewById(R.id.et_nama_bank);
        setNamaBank();
        btnCheckout = findViewById(R.id.btn_detail_checkout);

        sharedPreferences = getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        cekLogin();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        rvBrands.setLayoutManager(layoutManagerBrands);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setCartAdapter();

        tvTotalHarga.setText(totalHarga);


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

    private void setNamaBank() {
        mApiServiceWithoutToken.getMasterBank().enqueue(new Callback<MasterBank>() {
            @Override
            public void onResponse(Call<MasterBank> call, Response<MasterBank> response) {
                if(response.isSuccessful()) {
                    masterBankList = response.body().getData();
                    ArrayList<String> datumArrayList = new ArrayList<String>();
                    com.happyselling.niznet.Models.MasterBankResponse.Datum datum =
                            new com.happyselling.niznet.Models.MasterBankResponse.Datum();
                    for(int i=0; i< masterBankList.size(); i++){

                        datum = masterBankList.get(i);

                        datumArrayList.add(datum.getNama());
                    }
                    String[] countrys = ConvertStringListToStringArray.GetStringArray(datumArrayList);
                    spinNamaBank.setItems(countrys);
                    spinNamaBank.setTitle(getResources().getString(R.string.pilih_bank));
                    spinNamaBank.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            etNamaBank.setText("");
                            etNamaBank.setText(masterBankList.get(position).getNama().toString());
                        }
                    });

                    spinNamaBank.select(0);
                }else{
                    Toast.makeText(mContext, response.body().getMeta().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MasterBank> call, Throwable t) {

            }
        });
    }

    private void setPaymentMethod() {
        mApiServiceWithoutToken.getPaymentMethod().enqueue(new Callback<PaymentMethod>() {
            @Override
            public void onResponse(Call<PaymentMethod> call, Response<PaymentMethod> response) {
                if(response.isSuccessful()) {
                    paymentMethodList = response.body().getData();
                    ArrayList<String> datumArrayList = new ArrayList<String>();
                    com.happyselling.niznet.Models.MasterPaymentMethod.Datum datum =
                            new com.happyselling.niznet.Models.MasterPaymentMethod.Datum();
                    for(int i=0; i< paymentMethodList.size(); i++){

                        datum = paymentMethodList.get(i);

                        datumArrayList.add(datum.getNama());
                    }
                    String[] countrys = ConvertStringListToStringArray.GetStringArray(datumArrayList);
                    spinPaymentMethod.setItems(countrys);
                    spinPaymentMethod.setTitle(getResources().getString(R.string.pilih_tempat_pengambilan));
                    spinPaymentMethod.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            etPaymentMethod.setText("");
                            etPaymentMethod.setText(paymentMethodList.get(position).getId().toString());
                            if(!paymentMethodList.get(position).getId().equals("1")){
                                lyNamaBank.setVisibility(View.VISIBLE);
                            }else{
                                lyNamaBank.setVisibility(View.GONE);
                            }
                        }
                    });

                    spinPaymentMethod.select(0);
                }else{
                    Toast.makeText(mContext, response.body().getMeta().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentMethod> call, Throwable t) {

            }
        });
    }

    private void postCheckout() {
        loading();
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Integer> quantitys = new ArrayList<>();
        for(Datum datum : cartResultList) {
            ids.add(datum.getId());
            quantitys.add(Integer.parseInt(datum.getQuantity()));
        }
        Integer totalHargaReq =  Integer.parseInt(tvTotalHarga.getText().toString().replaceAll("\\D+",""));
        RequestBody totalHargas = RequestBody.create(MediaType.parse("multipart/form-data"), totalHargaReq.toString());
        String namaBankNya = "";
       if(null == etPaymentMethod.getText() || etPaymentMethod.getText().toString().equals("")){
           Toast.makeText(mContext, "Silahkan pilih metode pembayaran", Toast.LENGTH_SHORT).show();
       }else if(null == etToko.getText() || etToko.getText().toString().equals("")){
           Toast.makeText(mContext, "Silahkan pilih tempat pengambilan barang", Toast.LENGTH_SHORT).show();
       }else {
           if(!etPaymentMethod.getText().toString().equals("1")){
               if(null == etNamaBank.getText() || etNamaBank.getText().toString().equals("")){
                   Toast.makeText(mContext, "Harap Pilih Bank Pengirim", Toast.LENGTH_SHORT).show();
               }else{
                    namaBankNya = etNamaBank.getText().toString();
               }
           }
           mApiService.postCheckout(ids, quantitys, totalHargas, Integer.parseInt(etToko.getText().toString()), Integer.parseInt(etPaymentMethod.getText().toString()), namaBankNya).enqueue(new Callback<Meta>() {
               @Override
               public void onResponse(Call<Meta> call, Response<Meta> response) {
                   if (response.isSuccessful()) {
                       pdLoading.dismiss();
//                    Toast.makeText(mContext, "Berhasil memesan produk yang anda inginkan", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(getApplicationContext(), RiwayatTransaksiActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(intent);
                       finish();
                   } else {
                       pdLoading.dismiss();
                       Toast.makeText(mContext, "Gagal memesan pesanan", Toast.LENGTH_SHORT).show();
                   }
               }

               @Override
               public void onFailure(Call<Meta> call, Throwable t) {
                   pdLoading.dismiss();
                   Toast.makeText(mContext, "Gagal memesan pesanan", Toast.LENGTH_SHORT).show();
               }
           });

       }
    }

    private void setCartAdapter() {
        detailCheckoutAdapter = new DetailCheckoutAdapter(DetailCheckoutActivity.this,cartResultList);
        detailCheckoutAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(detailCheckoutAdapter);
    }

    private void setMasterToko(){
        mApiServiceWithoutToken.getMasterToko().enqueue(new Callback<MasterToko>() {
            @Override
            public void onResponse(Call<MasterToko> call, Response<MasterToko> response) {
                if(response.isSuccessful()) {
                    tokoList = response.body().getData();
                    ArrayList<String> datumArrayList = new ArrayList<String>();
                    com.happyselling.niznet.Models.MasterTokoResponse.Datum datum = new com.happyselling.niznet.Models.MasterTokoResponse.Datum();
                    for(int i=0; i< tokoList.size(); i++){
                        datum = tokoList.get(i);
                        datumArrayList.add(datum.getName());
                    }

                    String[] countrys = ConvertStringListToStringArray.GetStringArray(datumArrayList);
                    spinToko.setItems(countrys);
                    spinToko.setTitle(getResources().getString(R.string.pilih_tempat_pengambilan));
                    spinToko.setOnItemClickListener(new JRSpinner.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            etToko.setText("");
                            etToko.setText(tokoList.get(position).getId().toString());
                        }
                    });

                     spinToko.select(0);
                }else{
                    Toast.makeText(mContext, response.body().getMeta().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MasterToko> call, Throwable t) {

            }
        });

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

            mApiService = ApiServices.getApiServiceWithToken(userProfile.getToken(), DetailCheckoutActivity.this);
        }
    }

    private void loading(){
        pdLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pdLoading.getProgressHelper().setBarColor(getResources().getColor(R.color.bg_color));
        pdLoading.setContentText("Loading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
    }
}
