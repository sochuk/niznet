package com.happyselling.niznet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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

import com.happyselling.niznet.Activities.DetailTrxActivity;
import com.happyselling.niznet.Models.MasterTransaksiResponse.Result;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Utils.NetworkState;
import com.happyselling.niznet.Utils.Util;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RiwayatTransaksiAdapter extends PagedListAdapter<Result, RecyclerView.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private NetworkState networkState;
    private SweetAlertDialog pdLoading;
    private Util utils = new Util();

    public RiwayatTransaksiAdapter(Context context) {
        super(Result.DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROGRESS) {
            View headerBinding = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_network_state, parent, false);
//            NetworkItemBinding headerBinding = NetworkItemBinding.inflate(layoutInflater, parent, false);
            NetworkStateItemViewHolder viewHolder = new NetworkStateItemViewHolder(headerBinding);
            return viewHolder;

        } else {
            View itemBinding = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_transaksi, parent, false);
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
        private ImageView iv_item_riwayat_trx;
        private TextView tv_code_checkout,tv_tgl_trx,tv_status_trx, tv_total_harga_cart,tv_kd_trx;
        private CardView cardView;

        public ItemViewHolder(View binding) {
            super(binding);
            this.binding = binding;
            this.iv_item_riwayat_trx = binding.findViewById(R.id.iv_item_riwayat_trx);
            this.tv_code_checkout = binding.findViewById(R.id.tv_code_checkout);
            this.tv_tgl_trx = binding.findViewById(R.id.tv_tgl_trx);
            this.tv_status_trx = binding.findViewById(R.id.tv_status_trx);
            this.tv_total_harga_cart = binding.findViewById(R.id.tv_total_harga_cart);
            this.tv_kd_trx = binding.findViewById(R.id.tv_kd_trx);
            this.cardView = binding.findViewById(R.id.cv_item_trx);
        }


        public void bindTo(Result items) {
            int colors = context.getResources().getColor(R.color.dark_gray);
            String statusDesc = "";
            switch (Integer.parseInt(items.getStatus())){
                case 1:
                    statusDesc = "Menunggu Pembayaran";
                    colors = context.getResources().getColor(R.color.dark_gray);
                    break;
                case 2:
                    statusDesc = "Menunggu Verifikasi";
                    colors = context.getResources().getColor(R.color.yellow);
                    break;
                case 3:
                    statusDesc = "Transaksi Selesai";
                    colors = context.getResources().getColor(R.color.dark_green);
                    break;

            }
            this.tv_code_checkout.setText(items.getCheckoutCode());
            this.tv_code_checkout.setTextColor(colors);
            this.iv_item_riwayat_trx.setBackgroundTintList(ColorStateList.valueOf(colors));
            String tglNya = utils.formatDateToIndonesianDate(items.getCreatedAt());
            this.tv_tgl_trx.setText(tglNya);
            this.tv_status_trx.setText(statusDesc);
            this.tv_status_trx.setTextColor(colors);
            this.setTextViewDrawableColor(tv_status_trx, colors);
            this.tv_kd_trx.setTextColor(colors);
            this.tv_total_harga_cart.setText(Util.parsingRupiah(Integer.parseInt(items.getTotalPrice().replaceAll("\\.0*$", ""))));
            this.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DetailTrxActivity.class);
                    i.putExtra("idTrx", items.getId());

                    context.startActivity(i);
                    ((Activity)context).
                            overridePendingTransition(R.anim.zoom, R.anim.zoom);
                    ((Activity)context).finish();
                }

            });
        }

        private void setTextViewDrawableColor(TextView textView, int color) {
            for (Drawable drawable : textView.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                }
            }
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
