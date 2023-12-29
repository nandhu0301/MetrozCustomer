package com.smiligenceUAT1.metrozcustomer.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.location.Location;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.CustomerDetails;
import com.smiligenceUAT1.metrozcustomer.bean.ReviewAndRatings;
import com.smiligenceUAT1.metrozcustomer.bean.UserDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.apache.commons.text.WordUtils.capitalize;

public class ClosedStoreAdapter extends RecyclerView.Adapter<ClosedStoreAdapter.ImageViewHolder> {
    private Context mcontext;
    private List<UserDetails> sellerDetailsList;
    private ClosedStoreAdapter.OnItemClicklistener mlistener;
    DatabaseReference userDetailsRef,reviewRatingStore;
    public static double roundOff=0.0;
    float tempNumberOfStars=0;
    double ratingbarResult;
    ReviewAndRatings reviewAndRatings;
    ArrayList<String> numberOfOrders=new ArrayList<>();
    String saved_id;

    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(ClosedStoreAdapter.OnItemClicklistener listener) {
        mlistener = listener;
    }

    public ClosedStoreAdapter(Context context, List<UserDetails> userDetails) {
        mcontext = context;
        sellerDetailsList = userDetails;
    }

    @NonNull

    public ClosedStoreAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from ( mcontext ).inflate ( R.layout.closed_store_list, parent, false );
        ClosedStoreAdapter.ImageViewHolder imageViewHolder = new ClosedStoreAdapter.ImageViewHolder ( v, mlistener );
        return imageViewHolder;
    }

    public void onBindViewHolder(@NonNull final ClosedStoreAdapter.ImageViewHolder holder, final int position) {

        UserDetails sellerUserDetails = sellerDetailsList.get ( position );

        userDetailsRef = CommonMethods.fetchFirebaseDatabaseReference ( "CustomerLoginDetails" );
        reviewRatingStore=CommonMethods.fetchFirebaseDatabaseReference("StoreReviewsRatings");

        holder.storeName.setText ( capitalize ( sellerUserDetails.getStoreName ().toLowerCase () ) );
        final SharedPreferences loginSharedPreferences = mcontext.getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");
        Query dataRef = userDetailsRef.orderByChild ( "customerId" ).equalTo ( saved_id );

        dataRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () > 0) {
                    for ( DataSnapshot customerSnap : dataSnapshot.getChildren () ) {
                        CustomerDetails customerDetails = customerSnap.getValue ( CustomerDetails.class );

                        int Radius = 6371;

                        double lat1 = customerDetails.getUserLatitude ();
                        double lat2 = sellerUserDetails.getStoreLatitude ();

                        double lon1 = customerDetails.getUserLongtitude ();
                        double lon2 = sellerUserDetails.getStoreLongtide ();

                        double dLat = Math.toRadians ( lat2 - lat1 );
                        double dLon = Math.toRadians ( lon2 - lon1 );
                        double a = Math.sin ( dLat / 2 ) * Math.sin ( dLat / 2 )
                                + Math.cos ( Math.toRadians ( lat1 ) )
                                * Math.cos ( Math.toRadians ( lat2 ) ) * Math.sin ( dLon / 2 )
                                * Math.sin ( dLon / 2 );
                        double c = 2 * Math.asin ( Math.sqrt ( a ) );
                        double valueResult = Radius * c;

                        double km = valueResult / 1;


                        int resultKiloMeterRoundOff = (int) Math.round ( km );
                        roundOff = Math.round ( km * 100.0 ) / 100.0;

                        Location location1 = new Location ( "" );
                        location1.setLatitude ( lat1 );
                        location1.setLongitude ( lon1 );

                        Location location2 = new Location ( "" );
                        location2.setLatitude ( lat2 );
                        location2.setLongitude ( lon2 );


                        if (resultKiloMeterRoundOff <= 1) {
                            holder.storeDistance.setText ( roundOff + " Km " );
                        } else {
                            holder.storeDistance.setText ( roundOff + " Km " );
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        RequestOptions requestOptions = new RequestOptions ();
        requestOptions.placeholder ( R.mipmap.ic_launcher );
        requestOptions.error ( R.mipmap.ic_launcher );
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with ( mcontext )
                .setDefaultRequestOptions ( requestOptions )
                .load ( sellerUserDetails.getStoreLogo () ).fitCenter ().into ( holder.storeImages );
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        holder.storeImages.setColorFilter(filter);
    }

    public int getItemCount() {
        return sellerDetailsList.size ();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView storeName, storeDistance;
        ImageView storeImages;
        public ImageViewHolder(@NonNull View itemView, final ClosedStoreAdapter.OnItemClicklistener itemClicklistener) {
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