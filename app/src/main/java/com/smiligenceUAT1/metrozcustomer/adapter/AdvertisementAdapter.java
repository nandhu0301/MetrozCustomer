package com.smiligenceUAT1.metrozcustomer.adapter;

import android.app.Activity;
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
import com.smiligenceUAT1.metrozcustomer.bean.AdvertisementDetails;

import java.util.List;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.BOOLEAN_FALSE;

public class AdvertisementAdapter  extends RecyclerView.Adapter<AdvertisementAdapter.ImageViewHolder> {

    private Context mcontext;
    private List<AdvertisementDetails> advertisementList;
    private AdvertisementAdapter.OnItemClicklistener mlistener;
    AdvertisementDetails advertisementDetails;
    int preference;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(AdvertisementAdapter.OnItemClicklistener listener) {
        mlistener = listener;
    }

    public AdvertisementAdapter(Context context, List<AdvertisementDetails> advertisementDetails,int preference ) {
        mcontext = context;
        advertisementList = advertisementDetails;
        this.preference=preference;

    }

    @NonNull
    @Override
    public AdvertisementAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.advertisement_list, parent, BOOLEAN_FALSE);
        AdvertisementAdapter.ImageViewHolder imageViewHolder = new AdvertisementAdapter.ImageViewHolder(v, mlistener);
        v.invalidate();
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdvertisementAdapter.ImageViewHolder holder, final int position) {
        advertisementDetails = advertisementList.get(position);
     if   (preference==1){
         if (!((Activity) mcontext).isFinishing()) {

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher);
            requestOptions.error(R.mipmap.ic_launcher);
             requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(advertisementDetails.getImage()).fitCenter().into(holder.itemImages);

        }
        }else  if   (preference==2){
         if (!((Activity) mcontext).isFinishing()) {

             RequestOptions requestOptions = new RequestOptions();
             requestOptions.placeholder(R.mipmap.ic_launcher);
             requestOptions.error(R.mipmap.ic_launcher);
             requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
             Glide.with(mcontext)
                     .setDefaultRequestOptions(requestOptions)
                     .load(advertisementDetails.getImage()).fitCenter().into(holder.itemImages);

         }
     }else  if   (preference==3){
         if (!((Activity) mcontext).isFinishing()) {

             RequestOptions requestOptions = new RequestOptions();
             requestOptions.placeholder(R.mipmap.ic_launcher);
             requestOptions.error(R.mipmap.ic_launcher);
             requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
             Glide.with(mcontext)
                     .setDefaultRequestOptions(requestOptions)
                     .load(advertisementDetails.getImage()).fitCenter().into(holder.itemImages);

         }
     }else  if   (preference==4){
         if (!((Activity) mcontext).isFinishing()) {

             RequestOptions requestOptions = new RequestOptions();
             requestOptions.placeholder(R.mipmap.ic_launcher);
             requestOptions.error(R.mipmap.ic_launcher);
             requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
             Glide.with(mcontext)
                     .setDefaultRequestOptions(requestOptions)
                     .load(advertisementDetails.getImage()).fitCenter().into(holder.itemImages);

         }
     }else  if   (preference==5){
         if (!((Activity) mcontext).isFinishing()) {

             RequestOptions requestOptions = new RequestOptions();
             requestOptions.placeholder(R.mipmap.ic_launcher);
             requestOptions.error(R.mipmap.ic_launcher);
             Glide.with(mcontext)
                     .setDefaultRequestOptions(requestOptions)
                     .load(advertisementDetails.getImage()).fitCenter().into(holder.itemImages);

         }
     }else  if   (preference==6){
         if (!((Activity) mcontext).isFinishing()) {

             RequestOptions requestOptions = new RequestOptions();
             requestOptions.placeholder(R.mipmap.ic_launcher);
             requestOptions.error(R.mipmap.ic_launcher);
             Glide.with(mcontext)
                     .setDefaultRequestOptions(requestOptions)
                     .load(advertisementDetails.getImage()).fitCenter().into(holder.itemImages);

         }
     }else  if   (preference==7){
         if (!((Activity) mcontext).isFinishing()) {

             RequestOptions requestOptions = new RequestOptions();
             requestOptions.placeholder(R.mipmap.ic_launcher);
             requestOptions.error(R.mipmap.ic_launcher);
             Glide.with(mcontext)
                     .setDefaultRequestOptions(requestOptions)
                     .load(advertisementDetails.getImage()).fitCenter().into(holder.itemImages);

         }
     }

    }

    @Override
    public int getItemCount() {

        if (advertisementList == null) {
            return 0;
        } else {
            return advertisementList.size();
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView itemname, itemprice,storeName;
        ImageView itemImages;



        public ImageViewHolder(@NonNull View itemView, final AdvertisementAdapter.OnItemClicklistener itemClicklistener) {
            super(itemView);

            itemImages = itemView.findViewById(R.id.adimage);
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