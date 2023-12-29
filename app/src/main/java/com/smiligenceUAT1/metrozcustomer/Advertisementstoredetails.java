package com.smiligenceUAT1.metrozcustomer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.adapter.AdvertisementStoreListAdapter;
import com.smiligenceUAT1.metrozcustomer.bean.AdvertisementDetails;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.util.ArrayList;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.CUSTOMER_ID;
import static com.smiligenceUAT1.metrozcustomer.common.TextUtils.removeDuplicatesList;

public class Advertisementstoredetails extends AppCompatActivity {

    RecyclerView storeNameListView;
    ImageView adBanner;
    String pincodeCodeIntent, categoryIdIntent, categoryNameIntent,advertisementId;
    ArrayList<ItemDetails> itemDetailsArrayList = new ArrayList<>();
    AdvertisementStoreListAdapter storeListAdapter;
    DatabaseReference advertisementRef;
    ItemDetails itemDetails;
    ImageView backIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase ();
        setContentView(R.layout.activity_advertisement_item_details);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        storeNameListView = findViewById(R.id.storeNameFromad);
        adBanner = findViewById(R.id.banner);
        backIndicator=findViewById(R.id.backindicator);



        pincodeCodeIntent = getIntent().getStringExtra("PinCode");
        categoryIdIntent = getIntent().getStringExtra("categoryId");
        categoryNameIntent = getIntent().getStringExtra("categoryName");
        advertisementId=getIntent().getStringExtra("advertisementId");

        backIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Advertisementstoredetails.this, HomePageAcitivity.class);
                intent.putExtra("PinCode", pincodeCodeIntent);
                intent.putExtra("categoryName", categoryNameIntent);
                intent.putExtra("categoryId", categoryIdIntent);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        advertisementRef= CommonMethods.fetchFirebaseDatabaseReference("Advertisements");
        Query advertisementBanner=advertisementRef.child(advertisementId);
        advertisementBanner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AdvertisementDetails advertisementDetails=dataSnapshot.getValue(AdvertisementDetails.class);
                advertisementDetails.getImage();

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.mipmap.ic_launcher);
                requestOptions.error(R.mipmap.ic_launcher);
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(Advertisementstoredetails.this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(advertisementDetails.getImage()).fitCenter().into(adBanner);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        itemDetailsArrayList = HomePageAcitivity.advertisementListforBrand;


        storeNameListView.setLayoutManager(new GridLayoutManager(Advertisementstoredetails.this, 1));
        storeNameListView.setHasFixedSize(true);

        removeDuplicatesList(itemDetailsArrayList);

        if (itemDetailsArrayList != null && itemDetailsArrayList.size() > 0) {
            storeListAdapter = new AdvertisementStoreListAdapter(Advertisementstoredetails.this, itemDetailsArrayList);
            storeListAdapter.notifyDataSetChanged();
            storeNameListView.setAdapter(storeListAdapter);

        }
        storeListAdapter.setOnItemclickListener(new AdvertisementStoreListAdapter.OnItemClicklistener() {
            @Override
            public void Onitemclick(int Position) {
                itemDetails=itemDetailsArrayList.get(Position);
                Intent intent = new Intent(Advertisementstoredetails.this, ViewItemsActivity.class);
                intent.putExtra("StoreId", itemDetails.getSellerId());
                intent.putExtra("StoreName", itemDetails.getStoreName());
               intent.putExtra("storeAddress", itemDetails.getStoreAdress());
                intent.putExtra("categoryName", categoryNameIntent);
                intent.putExtra("categoryId", categoryIdIntent);
                intent.putExtra("PinCode", pincodeCodeIntent);

                intent.putExtra(CUSTOMER_ID, getIntent().getStringExtra(CUSTOMER_ID));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Advertisementstoredetails.this, HomePageAcitivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}