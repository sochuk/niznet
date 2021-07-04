package com.happyselling.niznet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.happyselling.niznet.Activities.DetailProductActivity;
import com.happyselling.niznet.Models.ProductResponse.Result;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Utils.NetworkState;
import com.happyselling.niznet.Utils.Util;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductAdapter extends PagedListAdapter<Result, RecyclerView.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private NetworkState networkState;
    private SweetAlertDialog pdLoading;

    public ProductAdapter(Context context) {
        super(Result.DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROGRESS) {
            View headerBinding = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_network_state, parent, false);
            NetworkStateItemViewHolder viewHolder = new NetworkStateItemViewHolder(headerBinding);
            return viewHolder;

        } else {
            View itemBinding = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
//
            ItemViewHolder viewHolder = new ItemViewHolder(itemBinding);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bindTo(getItem(position));
        } else {
            ((NetworkStateItemViewHolder) holder).bindView(networkState);
        }
    }


    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private View binding;
        private ImageView ivProduct;

        private TextView tvProductName, tvHargaProduk, tvStok, tvBrand, tvCategory;
        private CardView cvProduct;

        public ItemViewHolder(View binding) {
            super(binding);
            this.binding = binding;

            this.ivProduct = binding.findViewById(R.id.iv_product);
            this.tvProductName = binding.findViewById(R.id.tv_name_product);
            this.tvHargaProduk = binding.findViewById(R.id.tv_harga_product);
            this.tvStok = binding.findViewById(R.id.tv_stok);
            this.cvProduct = binding.findViewById(R.id.cv_item_product);

            this.tvCategory = binding.findViewById(R.id.tv_category_product);
            this.tvBrand = binding.findViewById(R.id.tv_brand_product);


        }


        public void bindTo(Result items) {
            Picasso.get().load(items.getImage1()).fit().centerCrop().into(ivProduct);
           tvProductName.setText(items.getProductName());
           tvHargaProduk.setText(Util.parsingRupiah(Integer.parseInt(items.getPrice())));
            tvStok.setText("Stok tersisa : "+items.getStock().toString());
            tvBrand.setText(items.getBrandName());
            tvCategory.setText(items.getCategoryName());
            cvProduct.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                           Intent i = new Intent(context, DetailProductActivity.class);
                           i.putExtra("nama_produk", items.getProductName());
                           i.putExtra("id_produk", items.getId().toString());
                           i.putExtra("harga_produk", items.getPrice().toString());
                           i.putExtra("stok_produk", items.getStock().toString());
                           i.putExtra("gambar_produk1", items.getImage1());
                           i.putExtra("gambar_produk2", items.getImage2());
                           i.putExtra("gambar_produk3", items.getImage3());
                           i.putExtra("gambar_produk4", items.getImage4());
                           i.putExtra("kategori_produk", items.getCategoryCode());
                           i.putExtra("brand_produk", items.getBrandCode());
                           i.putExtra("deskripsi_produk", items.getDesc());
                           i.putExtra("brand_name", items.getBrandName());
                           i.putExtra("category_name", items.getCategoryName());
                           i.putExtra("variasi", items.getVariasi());
                           i.putExtra("variasi_name", items.getVariasi_name());

//                   Log.d("###", "variasi adapter: "+items.getVariasi());
                           context.startActivity(i);
               }
           });
        }
    }


    public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        private View binding;
        ProgressBar progressBar;
        TextView errorMsg;
        public NetworkStateItemViewHolder(@NonNull View binding) {
            super(binding);
            this.binding = binding;
            this.progressBar = binding.findViewById(R.id.progress_bar);
            this.errorMsg = binding.findViewById(R.id.error_msg);
        }

        public void bindView(NetworkState networkState) {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                errorMsg.setVisibility(View.VISIBLE);
                errorMsg.setText(networkState.getMsg());
            } else {
                errorMsg.setVisibility(View.GONE);
            }
        }
    }

    private void loading(){
        pdLoading = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pdLoading.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimary));
        pdLoading.setTitleText("Loading");
        pdLoading.setCancelable(false);
        pdLoading.show();
    }
}
