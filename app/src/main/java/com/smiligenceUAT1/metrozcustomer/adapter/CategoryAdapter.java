package com.smiligenceUAT1.metrozcustomer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.CategoryDetails;

import java.util.List;

import static org.apache.commons.text.WordUtils.capitalize;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ImageViewHolder> {
    private Context mcontext;
    private List<CategoryDetails> categoryDetailsList;
    private OnItemClicklistener mlistener;
    int priority;


    ImageViewHolder imageViewHolder;
    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public CategoryAdapter(Context context, List<CategoryDetails> catagories,int priority) {
        mcontext = context;
        categoryDetailsList = catagories;
        this.priority=priority;

    }

    @NonNull

    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(priority==1) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.primary_category_grid, parent, false);
            imageViewHolder = new ImageViewHolder(v, mlistener);

        }else
        if (priority == 2) {

            View v = LayoutInflater.from(mcontext).inflate(R.layout.secondary_grid, parent, false);
            imageViewHolder = new ImageViewHolder(v, mlistener);

        }else
        if(priority==3){
            View v = LayoutInflater.from(mcontext).inflate(R.layout.catagory_grid, parent, false);
            imageViewHolder = new ImageViewHolder(v, mlistener);

        }

        return imageViewHolder;
    }


    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {


        CategoryDetails categoryDetails = categoryDetailsList.get(position);

        holder.catagoryName.setText(capitalize(categoryDetails.getCategoryName().toLowerCase()));

        holder.catagoryName.setSelected(true);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.error(R.mipmap.ic_launcher);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mcontext)
                .setDefaultRequestOptions(requestOptions)
                .load(categoryDetails.getCategoryImage()).dontAnimate().into(holder.itemImages);


    }


    public int getItemCount() {
        return categoryDetailsList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView catagoryName;
        ImageView itemImages;
        RelativeLayout secondaryGrid;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);
            catagoryName = itemView.findViewById(R.id.catagoryName_adapter);
            itemImages = itemView.findViewById(R.id.catagory_imageAdapter);
            catagoryName.setSelected(true);
            secondaryGrid=itemView.findViewById(R.id.secondarygrid);


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


