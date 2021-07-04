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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.happyselling.niznet.Activities.ProductPage.ProductListActivity;
import com.happyselling.niznet.Models.CategoryResponse.Datum;
import com.happyselling.niznet.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    Context context;
    private List<Datum> categoryModelList;

    public CategoryAdapter(Context context, List<Datum> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_category,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.ViewHolder holder, final int position) {
        Picasso.get().load(categoryModelList.get(position).getCategoryIcon()).into(holder.ivCategory);
        holder.tvCategory.setText(categoryModelList.get(position).getCategoryName());
        holder.cvRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProductListActivity.class);
                i.putExtra("search_param", categoryModelList.get(position).getCategoryName());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {

        return categoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       private ImageView ivCategory;
       private TextView tvCategory;
       private ConstraintLayout clRoot;
       private CardView cvRoot;

        public ViewHolder(View itemView) {

            super(itemView);

            ivCategory = itemView.findViewById(R.id.iv_pop_category);
            tvCategory = itemView.findViewById(R.id.tv_pop_category);
            clRoot = itemView.findViewById(R.id.ly_pop_category);
            cvRoot = itemView.findViewById(R.id.cv_category_home);
        }
    }
}
