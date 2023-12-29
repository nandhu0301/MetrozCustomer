package com.smiligenceUAT1.metrozcustomer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.adapter.ItemDetailsAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.ViewPagerAdapter;
import com.smiligenceUAT1.metrozcustomer.bean.Filters;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.bean.Preferences;
import com.smiligenceUAT1.metrozcustomer.bean.StoreTimings;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Constant;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.CATEGORY_ID;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.CATEGORY_NAME;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PINCODE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.STORE_ID;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.STORE_NAME;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.VIEW_CART_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.TextUtils.removeDuplicatesList;
import static org.apache.commons.text.WordUtils.capitalize;

public class ViewItemsActivity extends AppCompatActivity {

    DatabaseReference itemDataRef, storeTimingDataRef,reviewRatingStore, viewCartdatabaseRef, advertisementref;
    boolean check = false;
    ListView gridView;
    ImageView backButton;
    TextView sub_categoryName;
    ImageView cartIcon;
    TextView textView;
    ItemDetails itemDetails;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<>();
    ArrayList<ItemDetails> viewCartItemDetail = new ArrayList<>();
    private RecyclerView mrecyclerView;
    ItemDetailsAdapter itemAdapter;
    CardView search;
    EditText searchText;
    RelativeLayout sort, filter;
    String selected_sortValue;

    String storeAddress, getfssaiNumber;
    private BroadcastReceiver MyReceiver = new MyReceiver();
    TextView itemCount, totalAmount;

    int amount = 0;


    RelativeLayout purchasedetailslayout;
    Button viewCartFromLayout;
    long items = 0;
    // double getStoreLatitude, getStoreLongtitude;

    RecyclerView primaryrecyclerView;
    HorizontalScrollView primaryAdView;
    ArrayList<String> subCategoryList = new ArrayList<String>();
    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList<ItemDetails> refinedItemList = new ArrayList<ItemDetails>();
    public static int selectedTabPosition;
    Switch vegList, NonVegList;
    Query fetchItemsByStorequery;
    TextView fssaiNumber;
    double sharedPreferenceLatitude, sharedPreferenceLongtitude;
    TextView storeDistanceTxt, storeTimeTxt, storeAddressTxt, storeClosedTExtview;
    String getStoreId, getstoreAddress,storeDistance;
    String categoryIdIntent, categoryNameIntent;
    public static String temp = "";;
    public static int counter;
    String sharedPreferenceLat,sharedPreferenceLong;
    String filterId="1";
    LinearLayout vegNonVegLayout;
    String getstoreNameIntent;
    private FirebaseAnalytics mFirebaseAnalytics;
    String saved_id;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_view_items);

        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        checkGPSConnection(getApplicationContext());
        cartIcon = findViewById(R.id.carctIcon);
        textView = findViewById(R.id.cart_badge);
        gridView = findViewById(R.id.t_grid);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        backButton = findViewById(R.id.back_button);
        sub_categoryName = findViewById(R.id.sub_category);
        search = findViewById(R.id.searchview);
        searchText = findViewById(R.id.searchtext);
        sort = findViewById(R.id.sort);
        filter = findViewById(R.id.filter);
        purchasedetailslayout = findViewById(R.id.purchasesheet);
        itemCount = findViewById(R.id.items);
        totalAmount = findViewById(R.id.totalamount);
        viewCartFromLayout = findViewById(R.id.viewcartIntent);
        primaryrecyclerView = findViewById(R.id.primaryad);
        primaryAdView = findViewById(R.id.primaryscroll);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.frameLayout);
        vegList = findViewById(R.id.veglist);
        vegList.setTypeface(ResourcesCompat.getFont(ViewItemsActivity.this, R.font.coustard));
        NonVegList = findViewById(R.id.nonveglist);
        NonVegList.setTypeface(ResourcesCompat.getFont(ViewItemsActivity.this, R.font.coustard));
        tabLayout.setVisibility(View.INVISIBLE);
        viewPager.setVisibility(View.INVISIBLE);
        fssaiNumber = findViewById(R.id.fssaiNumber);
        storeDistanceTxt = findViewById(R.id.storeTextTxt);
        storeTimeTxt = findViewById(R.id.storeTimeTxt);
        storeAddressTxt = findViewById(R.id.storeAddressTxt);
        storeClosedTExtview = findViewById(R.id.storeclosedtext);
        vegNonVegLayout=findViewById(R.id.veglayout);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final SharedPreferences loginSharedPreferences1 = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences1.getString ( "customerId", "" );

        categoryIdIntent = getIntent().getStringExtra(CATEGORY_ID);
        categoryNameIntent = getIntent().getStringExtra(CATEGORY_NAME);
        final String pinCode = getIntent().getStringExtra(PINCODE);
        filterId=getIntent().getStringExtra("Filterid");


        advertisementref = CommonMethods.fetchFirebaseDatabaseReference("Advertisements");
        storeTimingDataRef =CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance");
        reviewRatingStore=CommonMethods.fetchFirebaseDatabaseReference("StoreReviewsRatings");



        final SharedPreferences loginSharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE);


        if(loginSharedPreferences!= null && !loginSharedPreferences.equals(""))
        {
            sharedPreferenceLat= loginSharedPreferences.getString("STORELATITUDE", "");
            sharedPreferenceLong=loginSharedPreferences.getString("STORELONGTITUDE", "");
        }
        if(sharedPreferenceLat!=null && !sharedPreferenceLat.equals(""))
        {
            sharedPreferenceLatitude = Double.parseDouble(sharedPreferenceLat);
        }
        if(sharedPreferenceLong!=null && !sharedPreferenceLong.equals(""))
        {
            sharedPreferenceLongtitude = Double.parseDouble(sharedPreferenceLong);
        }


        getStoreId = loginSharedPreferences.getString("STOREID", "");
        getstoreNameIntent =  loginSharedPreferences.getString("STORENAME", "");
        getstoreAddress = loginSharedPreferences.getString("STOREADDRESS", "");
        storeDistance=loginSharedPreferences.getString("STOREDISTANCE","");
        Bundle bundle2 = new Bundle();
        bundle2.putString("INVIEWITEMS",getstoreNameIntent);
        mFirebaseAnalytics.logEvent("Res"+getstoreNameIntent , bundle2);
        //Fssai number
        getfssaiNumber = loginSharedPreferences.getString("FSSAICERTIFICATE", "");
        fssaiNumber.setText("Lic. No.  " + loginSharedPreferences.getString("FSSAICERTIFICATE", ""));

        if (getstoreAddress != null && !("".equals(getstoreAddress))) {
            storeAddressTxt.setText(getstoreAddress);
        }

        if (filterId==null)
        {
            filterId="1";
        }


        if (getStoreId != null && !("".equals(getStoreId))) {



            storeTimingDataRef.child(String.valueOf(getStoreId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0)
                    {
                        StoreTimings storeTimings = dataSnapshot.getValue(StoreTimings.class);
                        final String time = storeTimings.getShopStartTime();

                        try {
                            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                            final Date dateObj = sdf.parse(time);
                            storeTimeTxt.setText(new SimpleDateFormat("hh:mm aa").format(dateObj).toLowerCase()+" - ");

                        } catch (final ParseException e) {
                            e.printStackTrace();
                        }

                        final String endtime = storeTimings.getShopEndTime();

                        try {
                            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                            final Date dateObj = sdf.parse(endtime);
                            storeClosedTExtview.setText(new SimpleDateFormat("hh:mm aa").format(dateObj).toLowerCase());
                        } catch (final ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        storeDistanceTxt.setText(" " + String.valueOf(storeDistance) + " Kms");
        storeDistanceTxt.setGravity(Gravity.CENTER);
        storeDistanceTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_directions_run_24, 0, 0, 0);
        searchText.clearFocus();

        searchText.setTextIsSelectable(false);
        searchText.setFocusable(false);
        gridView.setFocusable(false);
        gridView.setSmoothScrollbarEnabled(true);


        viewCartdatabaseRef = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(saved_id);

        sortFunction();
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewItemsActivity.this, SearchActivity.class);
                intent.putExtra(PINCODE, pinCode);

                intent.putExtra("searchIndiactor", "1");
                if (filterId=="12234") {
                    Preferences.filters.get(Filters.INDEX_PRICE).setSelected(new ArrayList());
                }
                intent.putExtra(CATEGORY_ID, categoryIdIntent);
                intent.putExtra(CATEGORY_NAME, categoryNameIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(ViewItemsActivity.this, SearchActivity.class);
                intent.putExtra(PINCODE, pinCode);

                Preferences.filters.get(Filters.INDEX_PRICE).setSelected(new ArrayList());

                intent.putExtra("searchIndiactor", "1");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        sub_categoryName.setText(capitalize(getstoreNameIntent.toLowerCase()));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewItemsActivity.this, HomePageAcitivity.class);
                intent.putExtra(STORE_ID, getStoreId);
                intent.putExtra(STORE_NAME, getstoreNameIntent);
                intent.putExtra(CATEGORY_ID, categoryIdIntent);
                intent.putExtra(CATEGORY_NAME, categoryNameIntent);
                intent.putExtra(PINCODE, pinCode);
                intent.putExtra("storeLatitude", sharedPreferenceLatitude);
                intent.putExtra("storeLongitude", sharedPreferenceLongtitude);
                intent.putExtra("storeAddress", storeAddress);
                // Preferences.filters.get(Filters.INDEX_PRICE).setSelected(new ArrayList());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        itemDataRef =CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_FIREBASE_TABLE);

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewItemsActivity.this, ViewCartActivity.class);
                intent.putExtra(STORE_ID, getStoreId);
                intent.putExtra(STORE_NAME, getstoreNameIntent);
                intent.putExtra(CATEGORY_ID, categoryIdIntent);
                intent.putExtra(CATEGORY_NAME, categoryNameIntent);
                intent.putExtra(PINCODE, pinCode);
                intent.putExtra("storeLatitude", sharedPreferenceLatitude);
                intent.putExtra("storeLongitude", sharedPreferenceLongtitude);
                intent.putExtra("storeAddress", storeAddress);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });



        viewCartFromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewItemsActivity.this, ViewCartActivity.class);
                intent.putExtra(STORE_ID, getStoreId);
                intent.putExtra(STORE_NAME, getstoreNameIntent);
                intent.putExtra(CATEGORY_ID, categoryIdIntent);
                intent.putExtra(CATEGORY_NAME, categoryNameIntent);
                intent.putExtra(PINCODE, pinCode);
                intent.putExtra("storeLatitude", sharedPreferenceLatitude);
                intent.putExtra("storeLongitude", sharedPreferenceLongtitude);

                intent.putExtra("storeAddress", storeAddress);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        purchasedetailslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewItemsActivity.this, ViewCartActivity.class);
                intent.putExtra(STORE_ID, getStoreId);
                intent.putExtra(STORE_NAME, getstoreNameIntent);
                intent.putExtra(CATEGORY_ID, categoryIdIntent);
                intent.putExtra(CATEGORY_NAME, categoryNameIntent);
                intent.putExtra(PINCODE, pinCode);
                intent.putExtra("storeLatitude", sharedPreferenceLatitude);
                intent.putExtra("storeLongitude", sharedPreferenceLongtitude);
                intent.putExtra("storeAddress", storeAddress);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        fetchItemsByStorequery = itemDataRef.orderByChild("sellerId").equalTo(getStoreId);
        if (!((Activity) ViewItemsActivity.this).isFinishing())
        {
            autoLoadFunction("");
        }
        vegList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    NonVegList.setChecked(false);
                    autoLoadFunction("Veg");
                } else {
                    autoLoadFunction("");
                }
            }
        });
        NonVegList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    vegList.setChecked(false);
                    autoLoadFunction("NonVeg");
                } else {
                    autoLoadFunction("");
                }
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewItemsActivity.this);
                bottomSheetDialog.setContentView(R.layout.sort_dialog);

                RadioGroup sortGroup = bottomSheetDialog.findViewById(R.id.sort_radiogroup);
                RadioButton sortbyname = bottomSheetDialog.findViewById(R.id.sort_name);
                RadioButton sortprice_low_to_high = bottomSheetDialog.findViewById(R.id.sort_lowtohigh);
                RadioButton sortprice_high_to_low = bottomSheetDialog.findViewById(R.id.sort_hightolow);
                RadioButton sort_latest = bottomSheetDialog.findViewById(R.id.sort_latest);

                if (temp.equals("Sort by Name")) {
                    sortbyname.setChecked(true);
                } else if (temp.equals("Price-Low to high")) {
                    sortprice_low_to_high.setChecked(true);
                } else if (temp.equals("Price-High to Low")) {
                    sortprice_high_to_low.setChecked(true);
                } else if (temp.equals("Latest on Top")) {
                    sort_latest.setChecked(true);
                }

                sortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        checkedId = sortGroup.getCheckedRadioButtonId();


                        RadioButton radioButton = (RadioButton) bottomSheetDialog.findViewById(checkedId);
                        selected_sortValue = radioButton.getText().toString();


                        if (selected_sortValue.equals("Sort by Name")) {

                            temp = "Sort by Name";
                            counter = 1;

                            sortFunction();
                            radioButton.setChecked(true);
                            sortbyname.setChecked(true);
                            if (itemAdapter != null) {

                                itemAdapter.notifyDataSetChanged();
                            }
                            bottomSheetDialog.dismiss();

                        } else if (selected_sortValue.equals("Price-Low to high")) {
                            temp = "Price-Low to high";
                            counter = 2;
                            sortFunction();
                            if (itemAdapter != null) {

                                itemAdapter.notifyDataSetChanged();
                            }

                            bottomSheetDialog.dismiss();


                        } else if (selected_sortValue.equals("Price-High to Low")) {
                            temp = "Price-High to Low";

                            counter = 3;
                            sortFunction();

                            if (itemAdapter != null) {

                                itemAdapter.notifyDataSetChanged();
                            }

                            bottomSheetDialog.dismiss();
                        } else if (selected_sortValue.equals("Latest on Top")) {
                            temp = "Latest on Top";

                            counter = 4;
                            sortFunction();

                            sortGroup.getCheckedRadioButtonId();
                            radioButton.setChecked(true);
                            bottomSheetDialog.dismiss();
                        }
                        if (itemAdapter != null) {

                            itemAdapter.notifyDataSetChanged();
                        }


                    }

                });

                int width = (int) (ViewItemsActivity.this.getResources().getDisplayMetrics().widthPixels * 0.6);
                int height = (int) (ViewItemsActivity.this.getResources().getDisplayMetrics().heightPixels * 0.5);
                bottomSheetDialog.getWindow().setLayout(width, height);
                bottomSheetDialog.show();
                bottomSheetDialog.setCancelable(true);

            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 10;
                initControls();

                Intent intent = new Intent(ViewItemsActivity.this, FilterActivity.class);
                intent.putExtra("StoreId", getStoreId);
                intent.putExtra("StoreName", getstoreNameIntent);
                intent.putExtra("fssaiNumber", getfssaiNumber);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    private void fetchItemsByStore(String itemType, String existingStoreInDB,String existingStoreInDBstr, Query fetchItemsByStorequery, int layoutDirection, ArrayList<ItemDetails> viewCartItemDetail) {

        fetchItemsByStorequery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    itemDetailList.clear();
                    subCategoryList.clear();

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        itemDetails = childSnapshot.getValue(ItemDetails.class);
                        if (itemDetails.getCategoryName().equals("FOOD DELIVERY"))
                        {
                            vegNonVegLayout.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            vegNonVegLayout.setVisibility(View.INVISIBLE);
                        }
                        if (itemDetails.getItemApprovalStatus().equalsIgnoreCase("Approved")) {
                            if (itemDetails.getItemStatus().equalsIgnoreCase("Active")) {
                                if (!((Activity) ViewItemsActivity.this).isFinishing()) {
                                    if (itemType.equals("Veg")) {
                                        if (itemDetails.getItemType().equals("Veg")) {
                                            if (itemAdapter != null) {

                                                itemAdapter.notifyDataSetChanged();
                                            }
                                            itemDetailList.add(itemDetails);
                                        }
                                    } else if (itemType.equals("NonVeg")) {
                                        if (itemDetails.getItemType().equals("NonVeg")) {
                                            if (itemAdapter != null)
                                            {
                                                itemAdapter.notifyDataSetChanged();
                                            }
                                            itemDetailList.add(itemDetails);
                                        }
                                    } else {
                                        if (itemAdapter != null) {

                                            itemAdapter.notifyDataSetChanged();
                                        }
                                        itemDetailList.add(itemDetails);
                                    }
                                }
                            }
                        }

                        if (filterId.equals("1234"))
                        {
                            if (!Preferences.filters.isEmpty ()) {

                                List<String> prices = Preferences.filters.get(Filters.INDEX_PRICE).getSelected();

                                if (itemDetails.getItemApprovalStatus().equals("Approved")) {
                                    if (itemDetails.getItemStatus().equalsIgnoreCase("Active"))
                                    {
                                        if (prices.size() > 0 && !priceContains(prices, itemDetails.getItemPrice())) {


                                        }
                                        else
                                        {
                                            if(itemDetails.getItemApprovalStatus().equals("Approved"))
                                            {
                                                if (itemDetails.getItemStatus().equals("Active"))
                                                {

                                                    subCategoryList.add(itemDetails.getSubCategoryName());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if(itemDetails.getItemApprovalStatus().equals("Approved"))
                            {
                                if (itemDetails.getItemStatus().equals("Active"))
                                {

                                    subCategoryList.add(itemDetails.getSubCategoryName());
                                }
                            }
                        }


                    }
                }
                removeDuplicatesList(subCategoryList);


                if (subCategoryList.size() != 0) {

                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    tabLayout.removeAllTabs();


                    for (int i = 0; i < subCategoryList.size(); i++) {

                        if (i == selectedTabPosition) {
                            tabLayout.addTab(tabLayout.newTab().setText(subCategoryList.get(i)), Constant.BOOLEAN_TRUE);
                        } else {
                            tabLayout.addTab(tabLayout.newTab().setText(subCategoryList.get(i)), Constant.BOOLEAN_FALSE);
                        }
                    }
                    if (!((Activity) ViewItemsActivity.this).isFinishing()) {
                        if (selectedTabPosition >= subCategoryList.size()) {
                            tabLayout.getTabAt(0).select();
                            LoadItem(subCategoryList.get(0));
                            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
                            viewPager.setAdapter(adapter);
                        } else {
                            LoadItem(subCategoryList.get(selectedTabPosition));
                            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
                            viewPager.setAdapter(adapter);
                        }

                    }


                    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {

                            viewPager.setCurrentItem(tab.getPosition());

                            selectedTabPosition = tab.getPosition();
                            LoadItem(tab.getText().toString());

                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            //selectedTabPosition = tab.getPosition();
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            selectedTabPosition = tab.getPosition();
                            LoadItem(tab.getText().toString());

                        }
                    });
                } else if (subCategoryList.size() == 0) {
                    tabLayout.setVisibility(View.INVISIBLE);
                    viewPager.setVisibility(View.INVISIBLE);
                    sortFunction();

                    itemAdapter = new ItemDetailsAdapter(ViewItemsActivity.this, refinedItemList, existingStoreInDB,existingStoreInDBstr, viewCartItemDetail);


                    int totalHeight = 0;


                    if (itemAdapter != null) {


                        for (int i = 0; i < itemAdapter.getCount(); i++) {
                            View listItem = itemAdapter.getView(i, null, gridView);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }

                        ViewGroup.LayoutParams params = gridView.getLayoutParams();
                        params.height = totalHeight + (gridView.getDividerHeight() * (itemAdapter.getCount() - 1));
                        gridView.setLayoutParams(params);
                        gridView.requestLayout();
                        gridView.setAdapter(itemAdapter);
                        itemAdapter.notifyDataSetChanged();
                    }
                    itemAdapter.notifyDataSetChanged();

                }
            }

            private void LoadItem(String tabString) {


                fetchItemsByStorequery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            refinedItemList.clear();
                            ArrayList<ItemDetails> filteredItems = new ArrayList<> ();
                            filteredItems.clear();;
                            for (DataSnapshot subCategoryName : dataSnapshot.getChildren()) {
                                itemDetails = subCategoryName.getValue(ItemDetails.class);
                                if (filterId.equals("1234"))
                                {
                                    if (!Preferences.filters.isEmpty ()) {

                                        List<String> prices = Preferences.filters.get(Filters.INDEX_PRICE).getSelected();
                                        {
                                            if (itemDetails.getItemApprovalStatus().equals("Approved")) {

                                                if (itemDetails.getItemStatus().equalsIgnoreCase("Active")) {

                                                    if (itemDetails.getSubCategoryName().equalsIgnoreCase(tabString)) {

                                                        if (prices.size() > 0 && !priceContains(prices, itemDetails.getItemPrice())) {

                                                        } else {
                                                            filteredItems.add(itemDetails);

                                                        }
                                                    }
                                                }
                                            }
                                            refinedItemList = filteredItems;
                                            if (filteredItems.size()<0 || filteredItems.isEmpty())
                                            {
                                                refinedItemList.clear();
                                                filteredItems.clear();
                                            }
                                        }
                                    }
                                }
                                else {
                                    if (itemDetails.getItemApprovalStatus().equals("Approved")) {

                                        if (itemDetails.getItemStatus().equalsIgnoreCase("Active")) {

                                            if (itemDetails.getSubCategoryName().equalsIgnoreCase(tabString)) {
                                                if (itemType.equals("Veg")) {
                                                    if (itemDetails.getItemType().equals("Veg")) {
                                                        if (itemAdapter != null) {

                                                            itemAdapter.notifyDataSetChanged();
                                                        }
                                                        refinedItemList.add(itemDetails);
                                                    }
                                                } else if (itemType.equals("NonVeg")) {
                                                    if (itemDetails.getItemType().equals("NonVeg")) {
                                                        if (itemAdapter != null) {

                                                            itemAdapter.notifyDataSetChanged();
                                                        }
                                                        refinedItemList.add(itemDetails);
                                                    }
                                                } else {

                                                    refinedItemList.add(itemDetails);
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                            //initControls();

                            sortFunction();
                            if (refinedItemList != null && refinedItemList.size() > 0) {
                                if (filterId.equals("1234")) {
                                    itemAdapter = new ItemDetailsAdapter(ViewItemsActivity.this, refinedItemList, existingStoreInDB,existingStoreInDBstr, viewCartItemDetail);

                                    if (itemAdapter != null && !itemAdapter.isEmpty()) {
                                        int totalHeight = 0;

                                        for (int i = 0; i < itemAdapter.getCount(); i++) {
                                            View listItem = itemAdapter.getView(i, null, gridView);
                                            listItem.measure(0, 0);
                                            totalHeight += listItem.getMeasuredHeight();
                                        }

                                        ViewGroup.LayoutParams params = gridView.getLayoutParams();
                                        params.height = totalHeight + (gridView.getDividerHeight() * (itemAdapter.getCount() - 1));
                                        gridView.setLayoutParams(params);
                                        gridView.requestLayout();
                                        gridView.setAdapter(itemAdapter);
                                        itemAdapter.notifyDataSetChanged();
                                    }
                                }else
                                {
                                    itemAdapter = new ItemDetailsAdapter(ViewItemsActivity.this, refinedItemList, existingStoreInDB,existingStoreInDBstr, viewCartItemDetail);

                                    if (itemAdapter != null && !itemAdapter.isEmpty()) {
                                        int totalHeight = 0;

                                        for (int i = 0; i < itemAdapter.getCount(); i++) {
                                            View listItem = itemAdapter.getView(i, null, gridView);
                                            listItem.measure(0, 0);
                                            totalHeight += listItem.getMeasuredHeight();
                                        }

                                        ViewGroup.LayoutParams params = gridView.getLayoutParams();
                                        params.height = totalHeight + (gridView.getDividerHeight() * (itemAdapter.getCount() - 1));
                                        gridView.setLayoutParams(params);
                                        gridView.requestLayout();
                                        gridView.setAdapter(itemAdapter);
                                        itemAdapter.notifyDataSetChanged();
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


    private void initControls() {
        if (counter == 10) {

            if (itemAdapter != null) {

                ArrayList<ItemDetails> filteredItems = new ArrayList<>();

                if (!Preferences.filters.isEmpty()) {

                    List<String> prices = Preferences.filters.get(Filters.INDEX_PRICE).getSelected();

                    for (ItemDetails item : refinedItemList) {

                        boolean priceMatched = true;

                        if (prices.size() > 0 && !priceContains(prices, item.getItemPrice())) {
                            priceMatched = false;
                        }
                        if (priceMatched) {
                            filteredItems.add(item);
                        }
                    }
                    refinedItemList = filteredItems;
                }


            }
        }
    }

    private void sortFunction() {


        if (counter == 1) {

            Collections.sort(refinedItemList, new Comparator<ItemDetails>() {
                @Override
                public int compare(ItemDetails o1, ItemDetails o2) {
                    return o1.getItemName().compareTo(o2.getItemName());

                }

            });
            if (itemAdapter != null) {

                itemAdapter.notifyDataSetChanged();
            }
            temp = "Sort by Name";
            counter=1;
        } else if (counter == 2) {

            Collections.sort(refinedItemList, new Comparator<ItemDetails>() {
                @Override
                public int compare(ItemDetails o1, ItemDetails o2) {
                    return o1.getItemPrice() - o2.getItemPrice();
                }
            });
            if (itemAdapter != null) {


                itemAdapter.notifyDataSetChanged();

            }
            counter=2;
            temp = "Price-Low to high";
        } else if (counter == 3) {

            Collections.sort(refinedItemList, new Comparator<ItemDetails>() {
                @Override
                public int compare(ItemDetails o1, ItemDetails o2) {
                    return o2.getItemPrice() - o1.getItemPrice();

                }
            });
            if (itemAdapter != null) {


                itemAdapter.notifyDataSetChanged();

            }
            counter=3;
            temp = "Price-High to Low";
        } else if (counter == 4) {
            Collections.sort(refinedItemList, new Comparator<ItemDetails>() {
                @Override
                public int compare(ItemDetails o1, ItemDetails o2) {
                    return o2.getItemId() - o1.getItemId();
                }
            });
            if (itemAdapter != null) {

                itemAdapter.notifyDataSetChanged();

            }
            counter=4;
            temp = "Latest on Top";
        }
    }

    private boolean priceContains(List<String> prices, Integer price) {
        boolean flag = false;
        for (String p : prices) {
            String[] tmpPrices = p.split("-");
            if (price >= Integer.parseInt(tmpPrices[0]) && price <= Integer.parseInt(tmpPrices[1])) {


                flag = true;
                break;
            }
        }
        return flag;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ViewItemsActivity.this, HomePageAcitivity.class);
        if (filterId=="12234") {
            Preferences.filters.get(Filters.INDEX_PRICE).setSelected(new ArrayList());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public void autoLoadFunction(String itemType)
    {
        if (!((Activity) ViewItemsActivity.this).isFinishing()) {
            viewCartdatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String existingStoreInDB = "0";
                    String existingStoreInDBnew = "0";
                    viewCartItemDetail.clear();

                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewItemsActivity.this);
                    bottomSheetDialog.setContentView(R.layout.purchase_details_dialog);

                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot itemDetailSnap : dataSnapshot.getChildren()) {
                            ItemDetails viewCartItemDetails = itemDetailSnap.getValue(ItemDetails.class);
                            viewCartItemDetail.add(viewCartItemDetails);
                            existingStoreInDB = viewCartItemDetails.getSellerId();
                            existingStoreInDBnew=viewCartItemDetails.getStoreName();

                        }
                        fetchItemsByStore(itemType, existingStoreInDB, existingStoreInDBnew,fetchItemsByStorequery, gridView.getLayoutDirection(), viewCartItemDetail);
                    } else {

                        bottomSheetDialog.dismiss();
                        fetchItemsByStore(itemType, "0","0", fetchItemsByStorequery, gridView.getLayoutDirection(), viewCartItemDetail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStart() {

        super.onStart();

        viewCartdatabaseRef = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).child(saved_id);
        viewCartdatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    purchasedetailslayout.setVisibility(View.VISIBLE);
                    amount = 0;
                    for (DataSnapshot viewCartItemDetails : dataSnapshot.getChildren()) {
                        ItemDetails itemDetails = viewCartItemDetails.getValue(ItemDetails.class);

                        amount = amount + itemDetails.getTotalItemQtyPrice();
                        items = dataSnapshot.getChildrenCount();
                    }

                    textView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    if (items == 1) {
                        itemCount.setText(String.valueOf(dataSnapshot.getChildrenCount()) + " item");
                    } else {
                        itemCount.setText(String.valueOf(dataSnapshot.getChildrenCount()) + " items");
                    }
                    totalAmount.setText(" ₹" + String.valueOf(amount));

                } else if (dataSnapshot.getChildrenCount() == 0) {
                    textView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    purchasedetailslayout.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}