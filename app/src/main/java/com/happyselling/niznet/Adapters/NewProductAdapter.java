package com.happyselling.niznet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.happyselling.niznet.Activities.DetailProductActivity;
import com.happyselling.niznet.Models.NewProductResponse.Datum;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;


public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.ViewHolder>{

    Context context;
    private List<Datum> newProductList;

    public NewProductAdapter(Context context, List<Datum> newProductList) {
        this.context = context;
        this.newProductList = newProductList;
    }

    @NonNull
    @Override
    public NewProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final NewProductAdapter.ViewHolder holder, final int position) {
        Picasso.get().load(newProductList.get(position).getImage1()).fit().centerCrop().into(holder.ivProduct);

        holder.tvProductName.setText(newProductList.get(position).getProductName());
        holder.tvHargaProduk.setText(Util.parsingRupiah(newProductList.get(position).getPrice()));
        holder.tvStok.setText("Stok tersisa : "+newProductList.get(position).getStock().toString());
        holder.tvBrand.setText(newProductList.get(position).getBrandName());
        holder.tvCategory.setText(newProductList.get(position).getCategoryName());
        holder.cvItemProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailProductActivity.class);
                i.putExtra("nama_produk", newProductList.get(position).getProductName());
                i.putExtra("id_produk", newProductList.get(position).getId().toString());
                i.putExtra("harga_produk", newProductList.get(position).getPrice().toString());
                i.putExtra("stok_produk", newProductList.get(position).getStock().toString());
                i.putExtra("gambar_produk1", newProductList.get(position).getImage1());
                i.putExtra("gambar_produk2", newProductList.get(position).getImage2());
                i.putExtra("gambar_produk3", newProductList.get(position).getImage3());
                i.putExtra("gambar_produk4", newProductList.get(position).getImage4());
                i.putExtra("kategori_produk", newProductList.get(position).getCategoryCode());
                i.putExtra("brand_produk", newProductList.get(position).getBrandCode());
                i.putExtra("deskripsi_produk", newProductList.get(position).getDesc());
                i.putExtra("brand_name", newProductList.get(position).getBrandName());
                i.putExtra("category_name", newProductList.get(position).getCategoryName());
                i.putExtra("variasi", newProductList.get(position).getVariasi());
                i.putExtra("variasi_name", newProductList.get(position).getVariasiName());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {

        return newProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProduct;
        private CardView cvItemProduct;
        private TextView tvProductName, tvHargaProduk, tvStok, tvCategory, tvBrand;

        public ViewHolder(View itemView) {

            super(itemView);

            ivProduct = itemView.findViewById(R.id.iv_product);
            tvProductName = itemView.findViewById(R.id.tv_name_product);
            tvHargaProduk = itemView.findViewById(R.id.tv_harga_product);
            tvStok = itemView.findViewById(R.id.tv_stok);
            cvItemProduct = itemView.findViewById(R.id.cv_item_product);
            tvCategory = itemView.findViewById(R.id.tv_category_product);
            tvBrand = itemView.findViewById(R.id.tv_brand_product);

        }
    }


}