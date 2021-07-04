package com.happyselling.niznet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.happyselling.niznet.Models.DetailTransaksiResponse.Product;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductListDetailTrxAdapter extends RecyclerView.Adapter<ProductListDetailTrxAdapter.ViewHolder>{

    private Context context;
    private List<Product> productList;
    private SweetAlertDialog pdLoading;

    public ProductListDetailTrxAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductListDetailTrxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_detail_trx,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Picasso.get().load(productList.get(position).getImage1()).fit().centerCrop().into(holder.ivProduct);
        if(null != productList.get(position).getVariasi()) {
            holder.tvProductName.setText(productList.get(position).getProductName()+"\n("+productList.get(position).getVariasi()+")");
        }else{
            holder.tvProductName.setText(productList.get(position).getProductName());
        }
        holder.tvQty.setText(productList.get(position).getQuantity()+" barang");
        holder.tvHargaProduk.setText(Util.parsingRupiah(Integer.parseInt(productList.get(position).getPrice())));
    }

    @Override
    public int getItemCount() {

        return productList.size();
    }

    public List<Product> getItem() {
        return productList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View binding;
        private ImageView ivProduct;
        private TextView tvProductName, tvHargaProduk, tvQty;
        public  ViewHolder(View binding) {
            super(binding);
            this.binding = binding;

            this.ivProduct = binding.findViewById(R.id.iv_item_product_detail_trx);
            this.tvProductName = binding.findViewById(R.id.tv_nama_produk_detail_trx);
            this.tvHargaProduk = binding.findViewById(R.id.tv_total_harga_detail_trx);
            this.tvQty = binding.findViewById(R.id.tv_qty_detail_trx);


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