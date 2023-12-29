package com.smiligenceUAT1.metrozcustomer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.CategoryDetails;

import java.util.List;

public class HomeScreenCategoryAdapter extends RecyclerView.Adapter<HomeScreenCategoryAdapter.ImageViewHolder> {
    private Context mcontext;
    private List<CategoryDetails> categoryDetailsList;
    private OnItemClicklistener mlistener;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    @NonNull

    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.catagory_grid, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(v, mlistener);
        return imageViewHolder;
    }


    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        //TODO Need to test
        // holder.setIsRecyclable(false);

        CategoryDetails categoryDetails = categoryDetailsList.get(position);

        holder.catagoryName.setText(categoryDetails.getCategoryName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.error(R.mipmap.ic_launcher);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mcontext)
                .setDefaultRequestOptions(requestOptions)
                .load(categoryDetails.getCategoryImage()).fitCenter().into(holder.itemImages);

    }


    public int getItemCount() {
        return categoryDetailsList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView catagoryName;
        ImageView itemImages;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);
            catagoryName = itemView.findViewById(R.id.catagoryName_adapter);
            itemImages = itemView.findViewById(R.id.catagory_imageAdapter);
            catagoryName.setSelected(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClicklistener != null) {
                        int Position = getAdapterPosition();
                        if (Position != RecyclerView.NO_POSITION) {
                            itemClicklistener.Onitemclick(Position);
                        }
                    }
                }
            });
        }
    }
}


