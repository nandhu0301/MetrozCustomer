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
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;

import java.util.List;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.BOOLEAN_FALSE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.BOOLEAN_TRUE;
import static org.apache.commons.text.WordUtils.capitalize;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ImageViewHolder> {

    private Context mcontext;
    private List<ItemDetails> itemDetailsList;
    private OnItemClicklistener mlistener;
    ItemDetails currentItemDetails;
    int preference;


    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    public ItemAdapter(Context context, List<ItemDetails> itemDetails,int preference ) {
        mcontext = context;
        itemDetailsList = itemDetails;
        this.preference=preference;

    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.store_listsearch, parent, BOOLEAN_FALSE);
        ImageViewHolder imageViewHolder = new ImageViewHolder(v, mlistener);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
         currentItemDetails = itemDetailsList.get(position);

        if(preference==1) {

            holder.storeName.setText(capitalize(currentItemDetails.getStoreName().toLowerCase()));
            holder.itemname.setText(capitalize(currentItemDetails.getItemName().toLowerCase()));
            holder.itemprice.setText(String.valueOf("₹ " + currentItemDetails.getItemPrice()));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher);
            requestOptions.error(R.mipmap.ic_launcher);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(currentItemDetails.getItemImage()).fitCenter().into(holder.itemImages);


        }
        else if(preference==2){
            holder.storeName.setText(currentItemDetails.getStoreName());
            holder.itemname.setVisibility(View.INVISIBLE);
            holder.itemprice.setVisibility(View.INVISIBLE);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher);
            requestOptions.error(R.mipmap.ic_launcher);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(currentItemDetails.getStoreLogo()).fitCenter().into(holder.itemImages);
        }
    }

    @Override
    public int getItemCount() {

        if (itemDetailsList == null) {
            return 0;
        } else {
            return itemDetailsList.size();
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView itemname, itemprice,storeName;
        ImageView itemImages;



        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super(itemView);
            storeName = itemView.findViewById(R.id.storenamesearch);
            itemprice = itemView.findViewById(R.id.pricesearch);
            itemname=itemView.findViewById(R.id.itemNamesearch);
            itemImages = itemView.findViewById(R.id.storeimagesearch);

            storeName.setSelected(BOOLEAN_TRUE);


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