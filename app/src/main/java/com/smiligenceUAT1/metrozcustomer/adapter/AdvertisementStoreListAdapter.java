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
import com.google.firebase.database.DatabaseReference;
import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;

import java.util.List;

import static org.apache.commons.text.WordUtils.capitalize;

public class AdvertisementStoreListAdapter extends RecyclerView.Adapter<AdvertisementStoreListAdapter.ImageViewHolder> {
    private Context mcontext;
    private List<ItemDetails> sellerDetailsList;
    private AdvertisementStoreListAdapter.OnItemClicklistener mlistener;
    DatabaseReference userDetailsRef;


    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(AdvertisementStoreListAdapter.OnItemClicklistener listener) {
        mlistener = listener;
    }

    public AdvertisementStoreListAdapter(Context context, List<ItemDetails> userDetails) {
        mcontext = context;
        sellerDetailsList = userDetails;
    }

    @NonNull

    public AdvertisementStoreListAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from ( mcontext ).inflate ( R.layout.storelist_layout, parent, false );
        AdvertisementStoreListAdapter.ImageViewHolder imageViewHolder = new AdvertisementStoreListAdapter.ImageViewHolder( v, mlistener );
        return imageViewHolder;
    }

    public void onBindViewHolder(@NonNull final AdvertisementStoreListAdapter.ImageViewHolder holder, final int position) {

        ItemDetails itemDetails = sellerDetailsList.get ( position );
        userDetailsRef = CommonMethods.fetchFirebaseDatabaseReference ( "CustomerLoginDetails" );

        holder.storeName.setText ( capitalize ( itemDetails.getStoreName ().toLowerCase () ) );


        RequestOptions requestOptions = new RequestOptions ();
        requestOptions.placeholder ( R.mipmap.ic_launcher );
        requestOptions.error ( R.mipmap.ic_launcher );
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with ( mcontext )
                .setDefaultRequestOptions ( requestOptions )
                .load ( itemDetails.getStoreLogo () ).fitCenter ().into ( holder.storeImages );
    }

    public int getItemCount() {
        return sellerDetailsList.size ();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView storeName, storeDistance;
        ImageView storeImages;

        public ImageViewHolder(@NonNull View itemView, final AdvertisementStoreListAdapter.OnItemClicklistener itemClicklistener) {
            super ( itemView );
            storeName = itemView.findViewById ( R.id.storename );
            storeImages = itemView.findViewById ( R.id.storeimage );
            storeDistance = itemView.findViewById ( R.id.store_distance );

            storeName.setSelected ( true );


            itemView.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    if (itemClicklistener != null) {
                        int Position = getAdapterPosition ();
                        if (Position != RecyclerView.NO_POSITION) {
                            itemClicklistener.Onitemclick ( Position );

                        }
                    }
                }
            } );
        }
    }
}
