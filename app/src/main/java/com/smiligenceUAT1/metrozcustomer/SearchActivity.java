package com.smiligenceUAT1.metrozcustomer;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.adapter.ItemAdapter;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.bean.MetrozStoreTime;
import com.smiligenceUAT1.metrozcustomer.bean.StoreTimings;
import com.smiligenceUAT1.metrozcustomer.bean.UserDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.CATEGORY_DETAILS_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.CATEGORY_NAME;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PINCODE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.SELLER_DETAILS;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    SearchView searchView;
    ArrayList<ItemDetails> search_itemDetailsList = new ArrayList<ItemDetails>();
    ArrayList<ItemDetails> search_storeDetailsList = new ArrayList<>();
    DatabaseReference itemDataRef, categoryDataRef, sellerDetailRef, storeTimingDataRef,metrozStoteTimingDataRef;
    ItemDetails itemDetails;
    RecyclerView itemListView, storeNameListView;
    List<UserDetails> sellerList = new ArrayList<>();
    ArrayList<ItemDetails> search_Users = new ArrayList<>();
    ArrayList<ItemDetails> search_Items = new ArrayList<>();
    BottomNavigationView bottomNavigation;
    ItemAdapter itemDetailsAdapter, storeDetailAdapter;
    TabLayout tableLayout_add;
    String pincodeCodeIntent;
    private BroadcastReceiver MyReceiver = new MyReceiver();
    ArrayList<String> openStoreSellerIdList = new ArrayList<>();
    ArrayList<String> closedStoreIdList = new ArrayList<>();
    HashMap<String, ItemDetails> storeList;
    HashMap<String, ItemDetails> itemFilteredList;
    int innerLoopsize;
    int outerLoopSize;
    boolean sizeCheckForOpenedStore = false;
    List<UserDetails> openStoresSellerList = new ArrayList<>();
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String metrozStartTime;
    String metrozStopTime;
    public static String DATE_FORMAT = "MMMM dd, YYYY";
    int getDistance;
    double getUserLatitude,getUserLongtitude,roundOff = 0.0;
    boolean check=true;
    String searchIndicator,categoryNameIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();

        setContentView(R.layout.activity_search);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        searchView = findViewById(R.id.searchbar);
        itemListView = findViewById(R.id.itemListView);
        bottomNavigation = findViewById(R.id.drawerLayout);
        storeNameListView = findViewById(R.id.storeNameList);
        tableLayout_add = findViewById(R.id.tabs_add_sales);
        itemListView.setVisibility(View.VISIBLE);
        storeNameListView.setVisibility(View.INVISIBLE);

        checkGPSConnection(getApplicationContext());
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        bottomNavigation.setSelectedItemId(R.id.searchscreen);

        pincodeCodeIntent = getIntent().getStringExtra(PINCODE);
        searchIndicator= getIntent().getStringExtra("SearchIndicator");

        categoryNameIntent = getIntent().getStringExtra(CATEGORY_NAME);

        categoryDataRef = CommonMethods.fetchFirebaseDatabaseReference(CATEGORY_DETAILS_TABLE);
        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_FIREBASE_TABLE);
        sellerDetailRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS);
        storeTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance");
        final SharedPreferences loginSharedPreferences = getSharedPreferences ( "DISTANCE", MODE_PRIVATE );
        getDistance= loginSharedPreferences.getInt ( "Distance", 0 );
        final SharedPreferences loginSharedPreferences1 = getSharedPreferences ( "SAVE", MODE_PRIVATE );

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        searchView.setOnCloseListener((SearchView.OnCloseListener) this);
        searchView.requestFocus();

        search_Users.clear();
        search_Items.clear();


        itemListView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 1));
        itemListView.setHasFixedSize(true);
        itemListView.setNestedScrollingEnabled(false);
        storeNameListView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 1));
        storeNameListView.setHasFixedSize(true);
        storeNameListView.setNestedScrollingEnabled(false);

        Calendar cal = Calendar.getInstance ();
        currentLocalTime = cal.getTime ();
        date = new SimpleDateFormat ( "HH:mm aa" );
        DateFormat dateFormat = new SimpleDateFormat ( DATE_FORMAT );
        currentDateAndTime = dateFormat.format ( new Date () );
        currentTime = date.format ( currentLocalTime );


        search_itemDetailsList.clear();
        search_storeDetailsList.clear();
        storeList = new HashMap<>();
        itemFilteredList= new HashMap<>();

        sellerDetailRef.orderByChild("approvalStatus").equalTo("Approved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot sellerSnap:dataSnapshot.getChildren())
                {
                    UserDetails sellerUserDetails = sellerSnap.getValue(UserDetails.class);

                    int Radius = 6371;
                    double lat1 = HomePageAcitivity.saved_Userlatitude;
                    double lat2 = sellerUserDetails.getStoreLatitude ();
                    double lon1 = HomePageAcitivity.saved_Userlongtitude;
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

                    if (resultKiloMeterRoundOff <= getDistance) {
                        sellerList.add(sellerUserDetails);
                    }
                }
                metrozStoteTimingDataRef=CommonMethods.fetchFirebaseDatabaseReference("MetrozstoreTiming");

                metrozStoteTimingDataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()>0) {
                            MetrozStoreTime metrozStoreTime = dataSnapshot.getValue(MetrozStoreTime.class);

                            metrozStartTime = metrozStoreTime.getShopStartTime();
                            metrozStopTime = metrozStoreTime.getShopEndTime();
                        }
                        storeTimingDataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    closedStoreIdList.clear();
                                    openStoreSellerIdList.clear();
                                    for (DataSnapshot storeTimingSnap : dataSnapshot.getChildren()) {
                                        StoreTimings storeTimings = storeTimingSnap.getValue(StoreTimings.class);

                                        try {
                                            //if result returns 1 => shop starts time is after metroz time
                                            //if result returns -1 => shops end time is before metroz end time
                                            //if result returns 0 => shop start,stop and metroz starts and stop time are same

                                            //Starting time of shop is after metroz time && ending time of shop is before metroz time

                                            if ((sdf.parse(storeTimings.getShopStartTime()).compareTo(sdf.parse(metrozStartTime)) == 1 &&
                                                    (sdf.parse(storeTimings.getShopEndTime()).compareTo(sdf.parse(metrozStopTime)) == -1))
                                                    ||  // Starting time of shop is equal to metroz &&  ending time of shop is before metroz time
                                                    (sdf.parse(storeTimings.getShopStartTime()).compareTo(sdf.parse(metrozStartTime)) == 0 &&
                                                            (sdf.parse(storeTimings.getShopEndTime()).compareTo(sdf.parse(metrozStopTime)) == -1))
                                                    || //Starting time of shop is after metroz time && ending time of metroz is equal to metroz timing
                                                    (sdf.parse(storeTimings.getShopStartTime()).compareTo(sdf.parse(metrozStartTime)) == 1 &&
                                                            sdf.parse(storeTimings.getShopEndTime()).compareTo(sdf.parse(metrozStopTime)) == 0)
                                                    ||  //start and end time is equal to metroz timings
                                                    (sdf.parse(storeTimings.getShopStartTime()).compareTo(sdf.parse(metrozStartTime)) == 0 &&
                                                            sdf.parse(storeTimings.getShopEndTime()).compareTo(sdf.parse(metrozStopTime)) == 0)) {
                                                //avail stores list
                                                if (storeTimings.getStoreStatus().equalsIgnoreCase("")) {
                                                    if ((sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopStartTime())) == 1) && !(sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopEndTime())) == 1)) {
                                                        openStoreSellerIdList.add(storeTimings.getSellerId());
                                                    }
                                                } else {
                                                    if (storeTimings.getStoreStatus().equalsIgnoreCase("Opened")) {
                                                        openStoreSellerIdList.add(storeTimings.getSellerId());
                                                    }
                                                }
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }



                                    if (openStoreSellerIdList.size() > 0 && openStoreSellerIdList != null) {
                                        if (sellerList.size() > openStoreSellerIdList.size()) {
                                            innerLoopsize = sellerList.size();
                                            outerLoopSize = openStoreSellerIdList.size();
                                            sizeCheckForOpenedStore = true;
                                        } else {
                                            innerLoopsize = openStoreSellerIdList.size();
                                            outerLoopSize = sellerList.size();
                                        }
                                        if (sizeCheckForOpenedStore == true) {
                                            openStoresSellerList.clear();
                                            for (int fromSeller = 0; fromSeller < outerLoopSize; fromSeller++) {
                                                for (int idList = 0; idList < innerLoopsize; idList++) {
                                                    if (sellerList.get(idList).getUserId().equals(openStoreSellerIdList.get(fromSeller))) {
                                                        openStoreQuery(sellerList.get(idList).getUserId());
                                                    }

                                                }
                                            }
                                        } else {
                                            openStoresSellerList.clear();
                                            for (int fromSeller = 0; fromSeller < outerLoopSize; fromSeller++) {
                                                for (int idList = 0; idList < innerLoopsize; idList++) {
                                                    if (sellerList.get(fromSeller).getUserId().equals(openStoreSellerIdList.get(idList))) {
                                                        openStoreQuery(sellerList.get(fromSeller).getUserId());

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.order: {
                        Intent intent = new Intent(getApplicationContext(), CustomersOrdersActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.home: {
                        Intent intent = new Intent(getApplicationContext(), HomePageAcitivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.searchscreen: {
                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    }

                }
                return false;
            }
        });

        tableLayout_add.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        itemListView.setVisibility(View.VISIBLE);
                        storeNameListView.setVisibility(View.INVISIBLE);
                        break;
                    case 1:

                        itemListView.setVisibility(View.INVISIBLE);
                        storeNameListView.setVisibility(View.VISIBLE);

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    public boolean onClose() {
        filterData("");

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterData(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        filterData(query);
        return false;
    }

    public void filterData(String query) {
        query = query.toUpperCase();
        search_Users.clear();
        search_Items.clear();

        if (query.isEmpty()) {


            search_Users.addAll(search_storeDetailsList);
            search_Items.addAll(search_itemDetailsList);

        } else {
            for (ItemDetails itemDetails : search_itemDetailsList) {

                if (itemDetails.getItemName().toUpperCase().contains(query)) {
                    search_Items.add(itemDetails);
                }
            }

            for (ItemDetails itemDetails : search_storeDetailsList) {

                if (itemDetails.getStoreName().toUpperCase().contains(query)) {
                    search_Users.add(itemDetails);

                }
            }

            if (search_Items.size() > 0) {
                itemDetailsAdapter = new ItemAdapter(SearchActivity.this, search_Items, 1);
                itemListView.setAdapter(itemDetailsAdapter);

            } else {


            }


            if (search_Users.size() > 0) {

                storeDetailAdapter = new ItemAdapter(SearchActivity.this, search_Users, 2);
                storeNameListView.setAdapter(storeDetailAdapter);

            } else {

            }

        }

        if (itemDetailsAdapter != null) {
            itemDetailsAdapter.notifyDataSetChanged();

            itemDetailsAdapter.setOnItemclickListener(new ItemAdapter.OnItemClicklistener() {
                @Override
                public void Onitemclick(int Position) {

                    final ItemDetails itemDetails = search_Items.get(Position);

                    navigateToItemDetailScreen(itemDetails);

                }
            });
        }

        if (storeDetailAdapter != null) {
            storeDetailAdapter.notifyDataSetChanged();

            storeDetailAdapter.setOnItemclickListener(
                    new ItemAdapter.OnItemClicklistener() {
                        @Override
                        public void Onitemclick(int Position) {

                            final ItemDetails itemDetails = search_Users.get(Position);

                            navigateToItemDetailScreen(itemDetails);
                        }
                    });
        }


    }

    public void navigateToItemDetailScreen(ItemDetails itemDetails) {

        SharedPreferences sharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        sellerDetailRef.orderByChild("userId").equalTo(itemDetails.getSellerId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (check) {
                    for (DataSnapshot sellerSnap : dataSnapshot.getChildren()) {
                        UserDetails sellerUserDetails = sellerSnap.getValue(UserDetails.class);
                        int Radius = 6371;// radius of earth in Km
                        double lat1 = HomePageAcitivity.saved_Userlatitude;
                        double lat2 = sellerUserDetails.getStoreLatitude();
                        double lon1 = HomePageAcitivity.saved_Userlongtitude;
                        double lon2 = sellerUserDetails.getStoreLongtide();
                        double dLat = Math.toRadians(lat2 - lat1);
                        double dLon = Math.toRadians(lon2 - lon1);
                        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                                + Math.cos(Math.toRadians(lat1))
                                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                                * Math.sin(dLon / 2);
                        double c = 2 * Math.asin(Math.sqrt(a));
                        double valueResult = Radius * c;
                        double km = valueResult / 1;
                        int resultKiloMeterRoundOff = (int) Math.round(km);
                        roundOff = Math.round(km * 100.0) / 100.0;
                        SharedPreferences sharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("STORELATITUDE", String.valueOf(sellerUserDetails.getStoreLatitude()));
                        editor.putString("STORELONGTITUDE", String.valueOf(sellerUserDetails.getStoreLongtide()));
                        editor.putString("STOREDISTANCE", String.valueOf(roundOff));
                        editor.putString("STOREID", String.valueOf(sellerUserDetails.getUserId()));
                        editor.putString("STORENAME", String.valueOf(sellerUserDetails.getStoreName()));
                        editor.putString("STOREADDRESS", String.valueOf(sellerUserDetails.getAddress()));
                        editor.putString("FSSAICERTIFICATE", String.valueOf(sellerUserDetails.getFssaiNumber()));
                        Intent intent = new Intent(SearchActivity.this, ViewItemsActivity.class);
                        intent.putExtra(PINCODE, pincodeCodeIntent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        editor.commit();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SearchActivity.this, HomePageAcitivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);


    }

    public void openStoreQuery(String id)
    {
        if(searchIndicator==null||"".equalsIgnoreCase(searchIndicator)) {

            itemDataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot itemStoreSnap : dataSnapshot.getChildren()) {
                        itemDetails = itemStoreSnap.getValue(ItemDetails.class);

                        if (itemDetails.getSellerId().equals(id)) {
                            storeList.put(String.valueOf(itemDetails.getSellerId()), itemDetails);
                            if (itemDetails.getItemStatus().equals("Active")) {
                                if (itemDetails.getItemApprovalStatus().equals("Approved")) {
                                    search_itemDetailsList.add(itemDetails);
                                }
                            }
                        }
                    }

                    search_storeDetailsList = new ArrayList<>(storeList.values());

                    itemDetailsAdapter = new ItemAdapter(SearchActivity.this, search_itemDetailsList, 1);
                    itemListView.setAdapter(itemDetailsAdapter);
                    itemDetailsAdapter.notifyDataSetChanged();


                    storeDetailAdapter = new ItemAdapter(SearchActivity.this, search_storeDetailsList, 2);
                    storeNameListView.setAdapter(storeDetailAdapter);
                    storeDetailAdapter.notifyDataSetChanged();



                    if (itemDetailsAdapter != null) {

                        itemDetailsAdapter.setOnItemclickListener(new ItemAdapter.OnItemClicklistener() {
                            @Override
                            public void Onitemclick(int Position) {

                                final ItemDetails itemDetails = search_itemDetailsList.get(Position);
                                navigateToItemDetailScreen(itemDetails);
                            }
                        });
                    }

                    if (storeDetailAdapter != null) {
                        storeDetailAdapter.setOnItemclickListener(
                                new ItemAdapter.OnItemClicklistener() {
                                    @Override
                                    public void Onitemclick(int Position) {
                                        final ItemDetails itemDetails = search_storeDetailsList.get(Position);


                                        navigateToItemDetailScreen(itemDetails);
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if(searchIndicator.equalsIgnoreCase("1"))
        {
            itemDataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot itemStoreSnap : dataSnapshot.getChildren())
                    {
                        itemDetails = itemStoreSnap.getValue(ItemDetails.class);

                        if (itemDetails.getSellerId().equals(id)&& ( itemDetails.getCategoryName().equalsIgnoreCase(categoryNameIntent)) ){
                            storeList.put(String.valueOf(itemDetails.getSellerId()), itemDetails);
                            if (itemDetails.getItemStatus().equals("Active") &&
                                    itemDetails.getCategoryName().equalsIgnoreCase(categoryNameIntent)) {
                                search_itemDetailsList.add(itemDetails);
                                itemFilteredList.put(String.valueOf(itemDetails.getItemId()), itemDetails);
                            }
                        }
                    }

                    search_storeDetailsList = new ArrayList<>(storeList.values());
                    search_itemDetailsList = new ArrayList<>(itemFilteredList.values());

                    itemDetailsAdapter = new ItemAdapter(SearchActivity.this, search_itemDetailsList, 1);
                    itemListView.setAdapter(itemDetailsAdapter);
                    itemDetailsAdapter.notifyDataSetChanged();



                    storeDetailAdapter = new ItemAdapter(SearchActivity.this, search_storeDetailsList, 2);
                    storeNameListView.setAdapter(storeDetailAdapter);
                    storeDetailAdapter.notifyDataSetChanged();


                    if (itemDetailsAdapter != null) {

                        itemDetailsAdapter.setOnItemclickListener(new ItemAdapter.OnItemClicklistener() {
                            @Override
                            public void Onitemclick(int Position) {

                                final ItemDetails itemDetails = search_itemDetailsList.get(Position);
                                navigateToItemDetailScreen(itemDetails);
                            }
                        });
                    }

                    if (storeDetailAdapter != null) {
                        storeDetailAdapter.setOnItemclickListener(
                                new ItemAdapter.OnItemClicklistener() {
                                    @Override
                                    public void Onitemclick(int Position)
                                    {
                                        final ItemDetails itemDetails = search_storeDetailsList.get(Position);
                                        navigateToItemDetailScreen(itemDetails);
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}