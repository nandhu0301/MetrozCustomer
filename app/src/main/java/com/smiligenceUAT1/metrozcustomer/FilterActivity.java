package com.smiligenceUAT1.metrozcustomer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.adapter.FilterAdapter;
import com.smiligenceUAT1.metrozcustomer.bean.Filters;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.bean.Preferences;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.SELLER_ID;


public class FilterActivity extends AppCompatActivity {
    DatabaseReference itemDatebaseRef;
    ArrayList<ItemDetails> itemList = new ArrayList<ItemDetails>();
    String getstoreIdIntent, getstoreNameIntent, categoryIdIntent, categoryNameIntent, pinCode,getfssaiNumber;
    private BroadcastReceiver MyReceiver = new MyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase ();
        setContentView(R.layout.activity_filter);
        checkGPSConnection(getApplicationContext());
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        getstoreIdIntent = getIntent().getStringExtra("StoreId");
        getstoreNameIntent = getIntent().getStringExtra("StoreName");
        categoryIdIntent = getIntent().getStringExtra("categoryId");
        categoryNameIntent = getIntent().getStringExtra("categoryName");
        pinCode = getIntent().getStringExtra("PinCode");
        getfssaiNumber=getIntent().getStringExtra("fssaiNumber");

        itemDatebaseRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_FIREBASE_TABLE);
        Query fetchItemQuery = itemDatebaseRef.orderByChild(SELLER_ID).equalTo(getstoreIdIntent);

        fetchItemQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot itemObjectSnap : dataSnapshot.getChildren()) {
                    ItemDetails itemDetails = (ItemDetails) itemObjectSnap.getValue(ItemDetails.class);
                    itemList.add(itemDetails);
                }

                initControls();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initControls() {
        RecyclerView filterRV = findViewById(R.id.filterRV);
        RecyclerView filterValuesRV = findViewById(R.id.filterValuesRV);
        filterRV.setLayoutManager(new LinearLayoutManager(this));
        filterValuesRV.setLayoutManager(new LinearLayoutManager(this));

        List<String> prices = Arrays.asList("0-100", "101-250", "251-500","501-1000");
        if (!Preferences.filters.containsKey(Filters.INDEX_PRICE)) {
            Preferences.filters.put(Filters.INDEX_PRICE, new Filters("Price", prices, new ArrayList()));
        }

        FilterAdapter filterAdapter = new FilterAdapter(Preferences.filters, filterValuesRV);
        filterRV.setAdapter(filterAdapter);

        Button clearB = findViewById(R.id.clearB);

        clearB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Preferences.filters.get(Filters.INDEX_PRICE).setSelected(new ArrayList());
                IntentActivity();
            }
        });

        Button applyB = findViewById(R.id.applyB);
        applyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.filters.size() == 0) {
                    Toast.makeText(FilterActivity.this,
                            "No Items Available in this Price range..select another Price range", Toast.LENGTH_SHORT).show();
                }
                IntentActivity();
            }
        });
    }

    public void IntentActivity() {
        Intent intent = new Intent(FilterActivity.this, ViewItemsActivity.class);
        intent.putExtra("StoreId", getstoreIdIntent);
        intent.putExtra("StoreName", getstoreNameIntent);
        intent.putExtra("categoryName", categoryNameIntent);
        intent.putExtra("categoryId", categoryIdIntent);
        intent.putExtra("pinCode", pinCode);
        intent.putExtra("fssaiNumber",getfssaiNumber);
        intent.putExtra("Filterid","1234");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        IntentActivity();
    }

    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!statusOfGPS)
            Toast.makeText(context.getApplicationContext(), "GPS is disable!", Toast.LENGTH_LONG).show();
    }
}