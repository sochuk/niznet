package com.happyselling.niznet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.happyselling.niznet.Models.CartResponse.Datum;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailCheckoutAdapter extends RecyclerView.Adapter<DetailCheckoutAdapter.ViewHolder>{

    private Context context;
    private List<Datum> cartList;
    private SweetAlertDialog pdLoading;

    public DetailCheckoutAdapter(Context context, List<Datum> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public DetailCheckoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_checkout,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Picasso.get().load(cartList.get(position).getImage1()).fit().centerCrop().into(holder.ivProduct);
        if(null == cartList.get(position).getVariasi()) {
            holder.tvProductName.setText(cartList.get(position).getProductName());
        }else{
            holder.tvProductName.setText(cartList.get(position).getProductName()+" ("+cartList.get(position).getVariasi()+")");
        }
        holder.tvHargaProduk.setText(Util.parsingRupiah(Integer.parseInt(cartList.get(position).getPrice())));
        holder.tvQty.setText(cartList.get(position).getQuantity()+" barang");
        holder.tvBrand.setText(cartList.get(position).getBrandName());

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View binding;
        private RoundedImageView ivProduct;
        private TextView tvProductName, tvHargaProduk, tvQty, tvBrand;
        public  ViewHolder(View binding) {
            super(binding);
            this.binding = binding;

            this.ivProduct = binding.findViewById(R.id.img_detail_checkout);
            this.tvProductName = binding.findViewById(R.id.tv_name_product_detail_checkout);
            this.tvHargaProduk = binding.findViewById(R.id.tv_harga_detail_checkout);
            this.tvBrand = binding.findViewById(R.id.tv_brand_detail_checkout);
            this.tvQty = binding.findViewById(R.id.tv_qty_detail_checkout);

        }

        private void loading(){
            pdLoading = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            pdLoading.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimary));
            pdLoading.setTitleText("Loading");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
    }
}