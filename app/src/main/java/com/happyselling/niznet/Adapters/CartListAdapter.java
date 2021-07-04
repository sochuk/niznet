package com.happyselling.niznet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.happyselling.niznet.Activities.CartListActivity;
import com.happyselling.niznet.Models.CartResponse.Datum;
import com.happyselling.niznet.Models.Meta;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder>{

    private Context context;
    private List<Datum> cartList;
    private TextView tvTotalHarga;
    private BaseApiService mApiService;
    private SweetAlertDialog pdLoading;

    public CartListAdapter(Context context, List<Datum> cartList, TextView tvTotalHarga, BaseApiService mApiService) {
        this.context = context;
        this.cartList = cartList;
        this.tvTotalHarga = tvTotalHarga;
        this.mApiService = mApiService;
    }

    @NonNull
    @Override
    public CartListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Picasso.get().load(cartList.get(position).getImage1()).fit().centerCrop().into(holder.ivProduct);
        if(null != cartList.get(position).getVariasi()) {
            holder.tvProductName.setText(cartList.get(position).getProductName() + " (" + cartList.get(position).getVariasi() + ")");
        }else{
            holder.tvProductName.setText(cartList.get(position).getProductName());
        }
        holder.tvHargaProduk.setText(Util.parsingRupiah(Integer.parseInt(cartList.get(position).getPrice())));
        holder.tvStok.setText("Stok tersisa : "+cartList.get(position).getStock().toString());
        holder.tvCategory.setText(cartList.get(position).getCategoryName());
        holder.tvBrand.setText(cartList.get(position).getBrandName());
        holder.etLotProduct.setText(cartList.get(position).getQuantity());

        Integer totalNya[] = {0};
        Integer number[] = {Integer.parseInt(holder.etLotProduct.getText().toString())};

        if(number[0] == 1){
            holder.ivMinus.setEnabled(false);
        }else if(number[0] > 0){
            holder.ivPlus.setEnabled(true);
        }else if(number[0]
                == Integer.parseInt(cartList.get(position).getStock())){
            holder.ivPlus.setEnabled(false);
        }else if(number[0] < Integer.parseInt(cartList.get(position).getStock())) {
            holder.ivMinus.setEnabled(true);
        }
        holder.ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number[0] == 1){
                    holder.ivMinus.setEnabled(false);
                }else if(number[0] > 0){
                    number[0] -= 1;
                    totalNya[0] = Integer.parseInt(tvTotalHarga.getText().toString().replaceAll("\\D+","")) - Integer.parseInt(cartList.get(position).getPrice());
                    holder.ivPlus.setEnabled(true);
                }
                holder.etLotProduct.setText(""+number[0]);
                tvTotalHarga.setText(Util.parsingRupiah(totalNya[0]));
                cartList.get(position).setQuantity(number[0].toString());
            }


        });

        holder.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (number[0]
                            == Integer.parseInt(cartList.get(position).getStock())) {
                        holder.ivPlus.setEnabled(false);
                     } else if (number[0] < Integer.parseInt(cartList.get(position).getStock())) {
                        number[0] += 1;
                        totalNya[0] = Integer.parseInt(tvTotalHarga.getText().toString().replaceAll("\\D+",""))
                                + Integer.parseInt(cartList.get(position).getPrice());
                        holder.ivMinus.setEnabled(true);
                    }
                    holder.etLotProduct.setText(""+number[0]);
                    tvTotalHarga.setText(Util.parsingRupiah(totalNya[0]));
                    cartList.get(position).setQuantity(number[0].toString());
                }

        });
        holder.btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Apakah anda yakin untuk menghapus pesanan?")
                        .setConfirmText("Hapus")
                        .setConfirmButtonBackgroundColor(context.getResources().getColor(R.color.red_btn_bg_color))
                        .setCancelButtonBackgroundColor(context.getResources().getColor(R.color.dark_gray))
                        .setCancelText("Batal")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                holder.loading();
                                holder.postDeleteCart(cartList.get(position).getId());
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {

        return cartList.size();
    }

    public List<Datum> getItem() {
        return cartList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View binding;
        private ImageView ivProduct, ivPlus, ivMinus,btnHapus;
        private EditText etLotProduct;
        private TextView tvProductName, tvHargaProduk, tvStok, tvBrand, tvCategory;
        public  ViewHolder(View binding) {
            super(binding);
            this.binding = binding;

            this.ivProduct = binding.findViewById(R.id.iv_cart_product);
            this.tvProductName = binding.findViewById(R.id.tv_name_product_cart);
            this.tvHargaProduk = binding.findViewById(R.id.tv_harga_cart_product);
            this.tvStok = binding.findViewById(R.id.tv_stok_cart_product);
            this.tvBrand = binding.findViewById(R.id.tv_brand_cart_product);
            this.tvCategory = binding.findViewById(R.id.tv_category_cart_product);

            this.etLotProduct = binding.findViewById(R.id.et_cart_lot);
            this.ivPlus = binding.findViewById(R.id.iv_plus_stok_cart);
            this.ivMinus = binding.findViewById(R.id.iv_minus_stok_cart);

            this.btnHapus = binding.findViewById(R.id.iv_delete_cart);

        }

        private void loading(){
            pdLoading = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            pdLoading.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimary));
            pdLoading.setTitleText("Loading");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        private void postDeleteCart(Integer idCart){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", idCart);
            mApiService.deleteCartById(jsonObject).enqueue(new Callback<Meta>() {
                @Override
                public void onResponse(Call<Meta> call, Response<Meta> response) {
                    if(response.isSuccessful()){
                        pdLoading.dismiss();
                        Toast.makeText(context, "Berhasil menghapus pesanan", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context,CartListActivity.class);
                        context.startActivity(i);
                        ((Activity)context).finish();
                    }else{
                        pdLoading.dismiss();
                        Toast.makeText(context, "Gagal menghapus pesanan"+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Meta> call, Throwable t) {
                    pdLoading.dismiss();
                    Toast.makeText(context, "Gagal menghapus pesanan"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}