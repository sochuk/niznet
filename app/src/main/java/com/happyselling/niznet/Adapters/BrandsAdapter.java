package com.happyselling.niznet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.happyselling.niznet.Models.BrandsResponse.Datum;
import com.happyselling.niznet.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder>{

    Context context;
    private List<Datum> brandsModelArrayList;

    public BrandsAdapter(Context context, List<Datum> brandsModelArrayList) {
        this.context = context;
        this.brandsModelArrayList = brandsModelArrayList;
    }

    @NonNull
    @Override
    public BrandsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brands,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BrandsAdapter.ViewHolder holder, final int position) {
        Picasso.get().load(brandsModelArrayList.get(position).getBrandIcon()).into(holder.ivBrands);
        holder.tvBrands.setText(brandsModelArrayList.get(position).getBrandName());
    }

    @Override
    public int getItemCount() {

        return brandsModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBrands;
        TextView tvBrands;

        public ViewHolder(View itemView) {

            super(itemView);

            ivBrands = itemView.findViewById(R.id.iv_brands);
            tvBrands = itemView.findViewById(R.id.tv_brands);
        }
    }
}
