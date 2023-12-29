package com.smiligenceUAT1.metrozcustomer;

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
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.smiligenceUAT1.metrozcustomer.adapter.AdvertisementAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.ClosedStoreAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.StoreListAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.ViewFlipperAdapter;
import com.smiligenceUAT1.metrozcustomer.bean.AdvertisementDetails;
import com.smiligenceUAT1.metrozcustomer.bean.MetrozStoreTime;
import com.smiligenceUAT1.metrozcustomer.bean.StoreTimings;
import com.smiligenceUAT1.metrozcustomer.bean.UserDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.DateUtils;
import com.smiligenceUAT1.metrozcustomer.common.Interface;
import com.smiligenceUAT1.metrozcustomer.common.Interface1;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.CATEGORY_DETAILS_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.CATEGORY_IMAGE_STORAGE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.CATEGORY_NAME;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.CUSTOMER_ID;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.STORE_LIST_PRIMARY;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.STORE_LIST_SECONDARY;
import static org.apache.commons.text.WordUtils.capitalize;


public class StoreListActivity extends AppCompatActivity implements Interface.ClickedListener, Interface1.ClickedListener   {
    DatabaseReference categoryDataRef, storeTimingDataRef, advertisementref,metrozStoteTimingDataRef;
    StorageReference categoryStorageRef;
    boolean check = false;
    List<UserDetails> sellerList = new ArrayList<> ();
    ListView storeDetailListView;
    TextView categoryNameText;
    ImageView backButton;
    List<UserDetails> closedStoresSellerList = new ArrayList<> ();
    List<UserDetails> openStoresSellerList = new ArrayList<> ();
    TextView closedTextview;
    StoreListAdapter storeListAdapter;
    RecyclerView storeRecyclerView;
    TextView searchText;
    CardView search;
    String pincodeCodeIntent;
    String categoryIdIntent;
    String categoryNameIntent;

    String storeAddress;
    ArrayList<UserDetails> userDetailsArrayList2=new ArrayList<UserDetails>();
    ArrayList<ArrayList<UserDetails>> userDetailsArrayListNew2=new ArrayList<ArrayList<UserDetails>>();

    boolean isCheck = true;

    private BroadcastReceiver MyReceiver = new MyReceiver ();

    Query advertisementQuery, advertisementActiveStatusQuery;
    ArrayList<AdvertisementDetails> advertisementDetailsList = new ArrayList<AdvertisementDetails> ();
    ArrayList<AdvertisementDetails> adv_secondaryList = new ArrayList<> ();
    AdvertisementDetails advertisementDetails;
    Thread thread;
    AdvertisementAdapter advertisementAdapterPrimary;
    Query categoryQuery;
    ClosedStoreAdapter closedStoreAdapter;
    ArrayList<String> openStoreSellerIdList = new ArrayList<> ();
    ArrayList<String> closedStoreIdList = new ArrayList<> ();
    int innerLoopsize;
    int outerLoopSize;
    int innerLoopsizeForClosedStores;
    int outerLoopSizeForClosedStores,getDistance;
    boolean sizeCheckForClosedStore = false;
    boolean sizeCheckForOpenedStore = false;
    CardView cardView,adcard1;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat ( pattern );
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String metrozStartTime ;
    String metrozStopTime;

    RecyclerView closedStoreGrid;
    public static String DATE_FORMAT = "MMMM dd, YYYY";
    double getUserLatitude,getUserLongtitude;
    public static  double roundOff;
    double getStoreLatitude,getStoreLongtitude;
    boolean checkAsync=true;
    private FirebaseAnalytics mFirebaseAnalytics;
    Query advertisementBannerQuery,advertisementBannerQuery1;
    ArrayList<AdvertisementDetails> advertisementDetailsObjectList = new ArrayList<>();
    ArrayList<AdvertisementDetails> advertisementDetailsObjectList1 = new ArrayList<>();
    ArrayList<String> advertisementPriorOne = new ArrayList<>();
    ArrayList<String> dateStringList=new ArrayList<>();
    ArrayList<UserDetails> userDetailsArrayList=new ArrayList<UserDetails>();
    ArrayList<ArrayList<UserDetails>> userDetailsArrayListNew=new ArrayList<ArrayList<UserDetails>>();
    AdapterViewFlipper adapterViewFlipper1,adapterViewFlipper2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        Utils.getDatabase();
        setContentView ( R.layout.storename_details_activity );
        checkGPSConnection ( getApplicationContext () );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        storeDetailListView = findViewById ( R.id.storelistview );
        categoryNameText = findViewById ( R.id.categorynametext );
        backButton = findViewById ( R.id.back );
        storeRecyclerView = findViewById ( R.id.storeGrid );
        search = findViewById ( R.id.searchview );
        searchText = findViewById ( R.id.searchtext );
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        registerReceiver ( MyReceiver, new IntentFilter ( ConnectivityManager.CONNECTIVITY_ACTION ) );

        closedStoreGrid = findViewById ( R.id.closedStoreGrid );
        closedTextview = findViewById ( R.id.closedText );
        closedStoreGrid.setLayoutManager ( new GridLayoutManager ( StoreListActivity.this, 1 ) );
        closedStoreGrid.setHasFixedSize ( true );
        storeRecyclerView.setNestedScrollingEnabled(false);
        closedStoreGrid.setNestedScrollingEnabled(false);


        registerReceiver ( MyReceiver, new IntentFilter ( ConnectivityManager.CONNECTIVITY_ACTION ) );


        Calendar cal = Calendar.getInstance ();
        currentLocalTime = cal.getTime ();
        date = new SimpleDateFormat ( "HH:mm aa" );
        DateFormat dateFormat = new SimpleDateFormat ( DATE_FORMAT );
        currentDateAndTime = dateFormat.format ( new Date () );
        currentTime = date.format ( currentLocalTime );



        searchText.clearFocus ();
        searchText.setTextIsSelectable ( false );
        searchText.setFocusable ( false );

        pincodeCodeIntent = getIntent ().getStringExtra ( "PinCode" );
        categoryIdIntent = getIntent ().getStringExtra ( "categoryId" );
        categoryNameIntent = getIntent ().getStringExtra ( "categoryName" );
        getUserLatitude=getIntent().getDoubleExtra("customerLatitude",0.0);
        getUserLongtitude=getIntent().getDoubleExtra("customerLongtitude",0.0);
        getStoreLatitude = getIntent ().getDoubleExtra ( "storeLatitude", 0.0 );
        getStoreLongtitude = getIntent ().getDoubleExtra ( "storeLongitude", 0.0 );
        final SharedPreferences loginSharedPreferences = getSharedPreferences ( "DISTANCE", MODE_PRIVATE );
        getDistance= loginSharedPreferences.getInt ( "Distance", 0 );
        adapterViewFlipper1 = findViewById(R.id.diwalibanners);
        adapterViewFlipper2=findViewById(R.id.diwalibanners1);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        categoryNameText.setText ( capitalize ( categoryNameIntent.toLowerCase()));
        cardView = findViewById(R.id.adcard);
        adcard1=findViewById(R.id.adcard1);

        metrozStoteTimingDataRef=CommonMethods.fetchFirebaseDatabaseReference("MetrozstoreTiming");


        Bundle bundle2 = new Bundle();
        bundle2.putString("INSTORESCREEN",categoryNameIntent);
        mFirebaseAnalytics.logEvent("Res"+categoryNameIntent , bundle2);


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, categoryNameIntent);
        mFirebaseAnalytics.logEvent("Test"+categoryNameIntent, bundle);

        Bundle bundle1 = new Bundle();
        bundle1.putString(FirebaseAnalytics.Param.METHOD, categoryNameIntent);
        mFirebaseAnalytics.logEvent("Test"+categoryNameIntent, bundle1);

        searchText.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( StoreListActivity.this, SearchActivity.class );
                intent.putExtra ( "PinCode", pincodeCodeIntent );
                intent.putExtra("SearchIndicator","1");
                intent.putExtra(CATEGORY_NAME,categoryNameIntent);
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        } );

        search.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( StoreListActivity.this, SearchActivity.class );
                intent.putExtra ( "PinCode", pincodeCodeIntent );
                intent.putExtra("SearchIndicator","1");
                intent.putExtra(CATEGORY_NAME,categoryNameIntent);
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        } );

        categoryDataRef = CommonMethods.fetchFirebaseDatabaseReference ( CATEGORY_DETAILS_TABLE );
        categoryStorageRef = CommonMethods.fetchFirebaseStorageReference ( CATEGORY_IMAGE_STORAGE );
        advertisementref = CommonMethods.fetchFirebaseDatabaseReference ( "Advertisements" );
        storeTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference ( "storeTimingMaintenance" );



        storeRecyclerView.setLayoutManager ( new GridLayoutManager ( StoreListActivity.this, 1 ) );
        storeRecyclerView.setHasFixedSize ( true );




        advertisementBannerQuery = advertisementref.orderByChild("advertisementPlacing").equalTo(STORE_LIST_PRIMARY);
        advertisementBannerQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0)
                {
                    advertisementDetailsObjectList.clear();
                    advertisementPriorOne.clear();
                    dateStringList.clear();
                    userDetailsArrayList.clear();
                    userDetailsArrayListNew.clear();
                    for (DataSnapshot adSnap:snapshot.getChildren())
                    {
                        advertisementDetails = adSnap.getValue(AdvertisementDetails.class);
                        if(advertisementDetails.getAdvertisementPlacing().equals(STORE_LIST_PRIMARY))
                        {
                            if (!(advertisementDetails.getAdvertisingAgent().equals("Instruction Ad"))) {

                                if (advertisementDetails.getAdvertisingDuration().equals("One Hour")) {
                                    if (advertisementDetails.getAdvertisementEndingDurationDate() != null) {
                                        if (advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate())) {
                                            try {
                                                if ((sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration())) == -1) ||
                                                        (sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration())) == 0)) {
                                                    advertisementDetailsObjectList.add(advertisementDetails);
                                                    userDetailsArrayList = (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew.add(userDetailsArrayList);
                                                    advertisementPriorOne.add(advertisementDetails.getImage());
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else if (advertisementDetails.getAdvertisingDuration().equals("One Day")) {
                                    if (advertisementDetails.getScheduledDate().equals(DateUtils.fetchFormatedCurrentDate())) {
                                        try {
                                            if ((sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getScheduledTime())) == 1 ||
                                                    sdf.parse(advertisementDetails.getScheduledTime()).compareTo(sdf.parse(currentTime)) == 0) ||
                                                    ((!advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate())) &&
                                                            (sdf.parse(advertisementDetails.getAdvertisementExpiringDuration()).compareTo(sdf.parse(currentTime)) == -1))) {
                                                advertisementDetailsObjectList.add(advertisementDetails);
                                                userDetailsArrayList = (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                userDetailsArrayListNew.add(userDetailsArrayList);
                                                advertisementPriorOne.add(advertisementDetails.getImage());
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate())) {
                                        try {
                                            if (sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration())) == -1 ||
                                                    sdf.parse(advertisementDetails.getScheduledTime()).compareTo(sdf.parse(currentTime)) == 0) {
                                                advertisementDetailsObjectList.add(advertisementDetails);
                                                userDetailsArrayList = (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                userDetailsArrayListNew.add(userDetailsArrayList);
                                                advertisementPriorOne.add(advertisementDetails.getImage());
                                            } else {
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else if (advertisementDetails.getAdvertisingDuration().equals("One Week")) {
                                    if (advertisementDetails.getScheduledDate().equals(DateUtils.fetchFormatedCurrentDate())) {
                                        try {
                                            if ((sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getScheduledTime())) == 1 ||
                                                    sdf.parse(advertisementDetails.getScheduledTime()).compareTo(sdf.parse(currentTime)) == 0) ||
                                                    ((!advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate())) &&
                                                            (sdf.parse(advertisementDetails.getAdvertisementExpiringDuration()).compareTo(sdf.parse(currentTime)) == -1))) {
                                                advertisementDetailsObjectList.add(advertisementDetails);
                                                userDetailsArrayList = (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                userDetailsArrayListNew.add(userDetailsArrayList);
                                                advertisementPriorOne.add(advertisementDetails.getImage());
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate())) {
                                        try {
                                            if (sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration())) == -1 ||
                                                    sdf.parse(advertisementDetails.getScheduledTime()).compareTo(sdf.parse(currentTime)) == 0) {
                                                advertisementDetailsObjectList.add(advertisementDetails);
                                                userDetailsArrayList = (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                userDetailsArrayListNew.add(userDetailsArrayList);
                                                advertisementPriorOne.add(advertisementDetails.getImage());
                                            } else {
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } else {

                                        dateStringList.clear();
                                        String dt = advertisementDetails.getScheduledDate();  // Start date
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                        Calendar c = Calendar.getInstance();
                                        try {
                                            c.setTime(sdf.parse(dt));
                                            dateStringList.add(sdf.format(c.getTime()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        for (int i = 0; i < 6; i++) {
                                            c.add(Calendar.DATE, 1);
                                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                                            dateStringList.add(sdf1.format(c.getTime()));
                                        }
                                        if (dateStringList != null && dateStringList.size() > 0) {
                                            for (int i = 1; i < 5; i++) {
                                                if (dateStringList.get(i).equals(DateUtils.fetchFormatedCurrentDate())) {
                                                    advertisementDetailsObjectList.add(advertisementDetails);
                                                    userDetailsArrayList = (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew.add(userDetailsArrayList);
                                                    advertisementPriorOne.add(advertisementDetails.getImage());
                                                }
                                            }
                                        }
                                    }
                                }
                            }else
                            {
                                if (advertisementDetails.getAdvertisementstatus().equals("Active"))
                                {
                                    advertisementDetailsObjectList.add(advertisementDetails);
                                    ArrayList<UserDetails> userDetailsArrayList1=new ArrayList<UserDetails>();
                                    userDetailsArrayListNew.add(userDetailsArrayList1);
                                }
                            }
                        }
                        if (advertisementDetailsObjectList.size() > 0) {
                            cardView.setVisibility(View.VISIBLE);
                            adapterViewFlipper1.setAdapter(new ViewFlipperAdapter(StoreListActivity.this, advertisementDetailsObjectList,StoreListActivity.this::onPictureClicked));
                            adapterViewFlipper1.setFlipInterval(4000);
                            adapterViewFlipper1.startFlipping();
                            adapterViewFlipper1.setAutoStart(true);
                            adapterViewFlipper1.setAnimateFirstView(false);
                        }
                        else {
                            cardView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        advertisementBannerQuery1 = advertisementref.orderByChild("advertisementPlacing").equalTo(STORE_LIST_SECONDARY);
        advertisementBannerQuery1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0)
                {
                    advertisementDetailsObjectList1.clear();
                    advertisementPriorOne.clear();
                    dateStringList.clear();
                    userDetailsArrayList2.clear();
                    userDetailsArrayListNew2.clear();
                    for (DataSnapshot adSnap:snapshot.getChildren())
                    {
                        advertisementDetails = adSnap.getValue(AdvertisementDetails.class);
                        if(advertisementDetails.getAdvertisementPlacing().equals(STORE_LIST_SECONDARY))
                        {
                            if (!(advertisementDetails.getAdvertisingAgent().equals("Instruction Ad"))) {

                                System.out.println("SADSDFDFTGTY"+userDetailsArrayList2.size());
                                if(advertisementDetails.getAdvertisementPlacing().equals(STORE_LIST_SECONDARY))
                                {
                                    if (advertisementDetails.getAdvertisingDuration().equals("One Hour"))
                                    {
                                        if (advertisementDetails.getAdvertisementEndingDurationDate()!=null) {
                                            if (advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate())) {
                                                try {
                                                    if ((sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration())) == -1) ||
                                                            (sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration())) == 0)) {
                                                        advertisementDetailsObjectList1.add(advertisementDetails);
                                                        userDetailsArrayList2 = (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                        userDetailsArrayListNew2.add(userDetailsArrayList2);
                                                        advertisementPriorOne.add(advertisementDetails.getImage());
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                    else if (advertisementDetails.getAdvertisingDuration().equals("One Day"))
                                    {
                                        if(advertisementDetails.getScheduledDate().equals(DateUtils.fetchFormatedCurrentDate()))
                                        {

                                            try {
                                                if ((sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getScheduledTime())) == 1 ||
                                                        sdf.parse(advertisementDetails.getScheduledTime()).compareTo(sdf.parse(currentTime)) == 0) ||
                                                        ((!advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate()))&&
                                                                (sdf.parse(advertisementDetails.getAdvertisementExpiringDuration()).compareTo(sdf.parse(currentTime))==-1)))
                                                {
                                                    advertisementDetailsObjectList1.add(advertisementDetails);
                                                    userDetailsArrayList2= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew2.add(userDetailsArrayList2);
                                                    advertisementPriorOne.add(advertisementDetails.getImage());
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate()))
                                        {
                                            try {
                                                if (sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration()))==-1 ||
                                                        sdf.parse(advertisementDetails.getScheduledTime()).compareTo(sdf.parse(currentTime)) == 0)
                                                {
                                                    advertisementDetailsObjectList1.add(advertisementDetails);
                                                    userDetailsArrayList2= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew2.add(userDetailsArrayList2);
                                                    advertisementPriorOne.add(advertisementDetails.getImage());
                                                }else
                                                {
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    else if (advertisementDetails.getAdvertisingDuration().equals("One Week"))
                                    {
                                        if(advertisementDetails.getScheduledDate().equals(DateUtils.fetchFormatedCurrentDate()))
                                        {
                                            try {
                                                if ((sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getScheduledTime())) == 1 ||
                                                        sdf.parse(advertisementDetails.getScheduledTime()).compareTo(sdf.parse(currentTime)) == 0) ||
                                                        ((!advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate()))&&
                                                                (sdf.parse(advertisementDetails.getAdvertisementExpiringDuration()).compareTo(sdf.parse(currentTime))==-1)))
                                                {
                                                    advertisementDetailsObjectList1.add(advertisementDetails);
                                                    userDetailsArrayList2= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew2.add(userDetailsArrayList2);
                                                    advertisementPriorOne.add(advertisementDetails.getImage());
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate()))
                                        {
                                            try {
                                                if (sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration()))==-1 ||
                                                        sdf.parse(advertisementDetails.getScheduledTime()).compareTo(sdf.parse(currentTime)) == 0)
                                                {
                                                    advertisementDetailsObjectList1.add(advertisementDetails);
                                                    userDetailsArrayList2= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew2.add(userDetailsArrayList2);
                                                    advertisementPriorOne.add(advertisementDetails.getImage());
                                                } else
                                                {

                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }else {

                                            dateStringList.clear();
                                            String dt = advertisementDetails.getScheduledDate();  // Start date
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                            Calendar c = Calendar.getInstance();
                                            try {
                                                c.setTime(sdf.parse(dt));
                                                dateStringList.add(sdf.format(c.getTime()));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            for (int i = 0; i < 6; i++) {
                                                c.add(Calendar.DATE, 1);
                                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                                                dateStringList.add(sdf1.format(c.getTime()));
                                            }
                                            if (dateStringList != null && dateStringList.size() > 0) {
                                                for (int i = 1; i < 5; i++) {
                                                    if (dateStringList.get(i).equals(DateUtils.fetchFormatedCurrentDate())) {
                                                        advertisementDetailsObjectList1.add(advertisementDetails);
                                                        userDetailsArrayList2= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                        userDetailsArrayListNew2.add(userDetailsArrayList2);
                                                        advertisementPriorOne.add(advertisementDetails.getImage());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }else
                            {
                                if (advertisementDetails.getAdvertisementstatus().equals("Active"))
                                {
                                    advertisementDetailsObjectList1.add(advertisementDetails);
                                    ArrayList<UserDetails> userDetailsArrayList1=new ArrayList<UserDetails>();
                                    userDetailsArrayListNew2.add(userDetailsArrayList1);
                                }
                            }
                        }

                        if (advertisementDetailsObjectList1.size() > 0) {
                            cardView.setVisibility(View.VISIBLE);
                            adapterViewFlipper2.setAdapter(new ViewFlipperAdapter1(StoreListActivity.this, advertisementDetailsObjectList1,StoreListActivity.this::onPictureClicked1));

                            adapterViewFlipper2.setFlipInterval(4000);
                            adapterViewFlipper2.startFlipping();
                            adapterViewFlipper2.setAutoStart(false);
                            adapterViewFlipper2.setAnimateFirstView(false);

                        }
                        else {
                            cardView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        backButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( StoreListActivity.this, HomePageAcitivity.class );
                intent.putExtra ( "PinCode", pincodeCodeIntent );
                intent.putExtra ( "categoryName", categoryNameIntent );
                intent.putExtra ( "categoryId", categoryIdIntent );
                intent.putExtra ( "storeLatitude", getStoreLatitude );
                intent.putExtra ( "storeLongitude", getStoreLongtitude );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent ( StoreListActivity.this, HomePageAcitivity.class );
        intent.putExtra ( "PinCode", pincodeCodeIntent );
        intent.putExtra ( "categoryName", categoryNameIntent );
        intent.putExtra ( "categoryId", categoryIdIntent );
        intent.putExtra ( "storeLatitude", getStoreLatitude );
        intent.putExtra ( "storeLongitude", getStoreLongtitude );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    public void checkGPSConnection(Context context) {
        final LocationManager manager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled ( LocationManager.GPS_PROVIDER );

    }

    public void closedStoreQuery(String id) {

        Query openedStoresStatus = categoryDataRef.child ( String.valueOf ( categoryIdIntent ) ).child ( "sellerList" ).orderByChild ( "userId" ).equalTo ( id );
        openedStoresStatus.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () > 0) {
                    for ( DataSnapshot openedStoreSnap : dataSnapshot.getChildren () ) {

                        UserDetails sellerUserDetails = openedStoreSnap.getValue ( UserDetails.class );
                        closedStoresSellerList.add ( sellerUserDetails );

                    }
                    if (closedStoresSellerList != null && closedStoresSellerList.size () > 0) {
                        if (!((Activity) StoreListActivity.this).isFinishing()) {
                            closedStoreAdapter = new ClosedStoreAdapter(StoreListActivity.this, closedStoresSellerList);
                            closedStoreAdapter.notifyDataSetChanged();
                            closedStoreGrid.setAdapter(closedStoreAdapter);
                            closedTextview.setText("  Currently Closed Stores");
                            closedTextview.setBackgroundColor(getResources().getColor(R.color.grey1));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        } );
    }

    public void openStoreQuery(String id)
    {
        Query openedStoresStatus = categoryDataRef.child ( String.valueOf ( categoryIdIntent ) ).child ( "sellerList" ).orderByChild ( "userId" ).equalTo ( id );

        openedStoresStatus.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () > 0) {

                    for ( DataSnapshot openedStoreSnap : dataSnapshot.getChildren () ) {
                        UserDetails sellerUserDetails = openedStoreSnap.getValue ( UserDetails.class );
                        openStoresSellerList.add ( sellerUserDetails );
                    }

                    if (openStoresSellerList != null && openStoresSellerList.size () > 0)
                    {
                        if (!((Activity) StoreListActivity.this).isFinishing()) {
                            storeListAdapter = new StoreListAdapter(StoreListActivity.this, openStoresSellerList);
                            storeRecyclerView.setAdapter(storeListAdapter);
                            storeListAdapter.notifyDataSetChanged();

                        }
                    }

                    if (storeListAdapter != null)
                    {

                        storeListAdapter.setOnItemclickListener ( new StoreListAdapter.OnItemClicklistener () {
                            @Override
                            public void Onitemclick(int Position) {

                                final UserDetails sellerDetails = openStoresSellerList.get ( Position );

                                Intent intent = new Intent ( StoreListActivity.this, ViewItemsActivity.class );
                                intent.putExtra ( "StoreId", sellerDetails.getUserId () );
                                intent.putExtra ( "StoreName", sellerDetails.getStoreName () );
                                intent.putExtra ( "storeLatitude", sellerDetails.getStoreLatitude () );
                                intent.putExtra ( "storeLongitude", sellerDetails.getStoreLongtide () );

                                storeAddress = sellerDetails.getAddress ();

                                intent.putExtra ( "storeAddress", storeAddress );
                                intent.putExtra ( "fssaiNumber", sellerDetails.getFssaiNumber() );
                                intent.putExtra ( "categoryName", categoryNameIntent );
                                intent.putExtra ( "categoryId", categoryIdIntent );
                                intent.putExtra ( "PinCode", pincodeCodeIntent );

                                int Radius = 6371;// radius of earth in Km
                                double lat1 = HomePageAcitivity.saved_Userlatitude;
                                double lat2 = sellerDetails.getStoreLatitude ();
                                double lon1 = HomePageAcitivity.saved_Userlongtitude;
                                double lon2 = sellerDetails.getStoreLongtide ();
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

                                SharedPreferences sharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();


                                editor.putString("STORELATITUDE", String.valueOf(sellerDetails.getStoreLatitude()));
                                editor.putString("STORELONGTITUDE", String.valueOf(sellerDetails.getStoreLongtide()));
                                editor.putString("STOREDISTANCE", String.valueOf(roundOff));
                                editor.putString("STOREID", String.valueOf(sellerDetails.getUserId()));
                                editor.putString("STORENAME", String.valueOf(sellerDetails.getStoreName()));
                                editor.putString("STOREADDRESS", String.valueOf(sellerDetails.getAddress()));
                                editor.putString("FSSAICERTIFICATE", String.valueOf(sellerDetails.getFssaiNumber()));

                                editor.commit();
                                Bundle bundle = new Bundle();
                                bundle.putString("Store",sellerDetails.getStoreName ());
                                mFirebaseAnalytics.logEvent(sellerDetails.getStoreName (), bundle);
                                intent.putExtra ( CUSTOMER_ID, getIntent ().getStringExtra ( CUSTOMER_ID ) );
                                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity ( intent );

                            }
                        } );
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
    @Override
    protected void onStart() {
        super.onStart();
        categoryQuery = categoryDataRef.child(String.valueOf(categoryIdIntent)).child("sellerList");

        if (!((Activity) StoreListActivity.this).isFinishing()) {
            categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    sellerList.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserDetails sellerUserDetails = postSnapshot.getValue(UserDetails.class);

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
                        if (resultKiloMeterRoundOff <= getDistance) {
                            sellerList.add(sellerUserDetails);
                        }
                    }

                    if (sellerList == null || sellerList.size() == 0) {
                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(StoreListActivity.this);
                        bottomSheetDialog.setContentView(R.layout.no_stores_available);
                        Button returntohomefromdialog = bottomSheetDialog.findViewById(R.id.returntohomefromdialog);

                        returntohomefromdialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(StoreListActivity.this, HomePageAcitivity.class);
                                intent.putExtra("PinCode", pincodeCodeIntent);
                                intent.putExtra("categoryName", categoryNameIntent);
                                intent.putExtra("categoryId", categoryIdIntent);
                                intent.putExtra("storeLatitude", getStoreLatitude);
                                intent.putExtra("storeLongitude", getStoreLongtitude);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                        bottomSheetDialog.show();
                        bottomSheetDialog.setCancelable(false);
                    } else {
                        if (!((Activity) StoreListActivity.this).isFinishing()) {

                            metrozStoteTimingDataRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        MetrozStoreTime metrozStoreTime = dataSnapshot.getValue(MetrozStoreTime.class);

                                        metrozStartTime = metrozStoreTime.getShopStartTime();
                                        metrozStopTime = metrozStoreTime.getShopEndTime();
                                    }
                                    storeTimingDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (checkAsync) {

                                                if (dataSnapshot.getChildrenCount() > 0) {
                                                    closedStoreIdList.clear();
                                                    openStoreSellerIdList.clear();
                                                    openStoresSellerList.clear();
                                                    closedStoresSellerList.clear();
                                                    for (DataSnapshot storeTimingSnap : dataSnapshot.getChildren()) {
                                                        StoreTimings storeTimings = storeTimingSnap.getValue(StoreTimings.class);

                                                        try {

                                                            //if result returns 1 => shop starts time is after metroz time
                                                            //if result returns -1 => shops end time is before metroz end time
                                                            //if result returns 0 => shop start,stop and metroz starts and stop time are same

                                                            //Starting time of shop is after metroz time && ending time of shop is before metroz time

                                                            System.out.println("CurrentTime"+sdf.parse(currentTime)+"AS"+currentTime);
                                                            if ((sdf.parse(currentTime).compareTo(sdf.parse(metrozStartTime)) == 1) && !(sdf.parse(currentTime).compareTo(sdf.parse(metrozStopTime)) == 1)) {
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
                                                                        } else if ((sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopEndTime())) == 1) || (sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopStartTime())) == -1)) {
                                                                            closedStoreIdList.add(storeTimings.getSellerId());
                                                                        }
                                                                    } else {
                                                                        if (storeTimings.getStoreStatus().equalsIgnoreCase("Opened")) {
                                                                            openStoreSellerIdList.add(storeTimings.getSellerId());
                                                                        }
                                                                        if (storeTimings.getStoreStatus().equalsIgnoreCase("Closed")) {
                                                                            closedStoreIdList.add(storeTimings.getSellerId());
                                                                        }
                                                                    }

                                                                }
                                                            } else {
                                                                closedStoreIdList.add(storeTimings.getSellerId());
                                                            }
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    if (openStoreSellerIdList.size() > 0 && openStoreSellerIdList != null) {
                                                        openStoresSellerList.clear();
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
                                                    if (!((Activity) StoreListActivity.this).isFinishing()) {
                                                        if (closedStoreIdList.size() > 0 && closedStoreIdList != null) {
                                                            if (sellerList.size() > closedStoreIdList.size()) {
                                                                innerLoopsizeForClosedStores = sellerList.size();
                                                                outerLoopSizeForClosedStores = closedStoreIdList.size();
                                                                sizeCheckForClosedStore = true;
                                                            } else {
                                                                innerLoopsizeForClosedStores = closedStoreIdList.size();
                                                                outerLoopSizeForClosedStores = sellerList.size();
                                                            }
                                                            if (sizeCheckForClosedStore == true) {
                                                                closedStoresSellerList.clear();
                                                                for (int fromSeller = 0; fromSeller < outerLoopSizeForClosedStores; fromSeller++) {
                                                                    for (int idList = 0; idList < innerLoopsizeForClosedStores; idList++) {
                                                                        if (sellerList.get(idList).getUserId().equals(closedStoreIdList.get(fromSeller))) {
                                                                            closedStoreQuery(sellerList.get(idList).getUserId());
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                closedStoresSellerList.clear();
                                                                for (int fromSeller = 0; fromSeller < outerLoopSizeForClosedStores; fromSeller++) {
                                                                    for (int idList = 0; idList < innerLoopsizeForClosedStores; idList++) {
                                                                        if (sellerList.get(fromSeller).getUserId().equals(closedStoreIdList.get(idList))) {
                                                                            closedStoreQuery(sellerList.get(fromSeller).getUserId());
                                                                        }
                                                                    }
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

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    @Override
    public void onPictureClicked(int position) {


        if (userDetailsArrayListNew.size()>0 && userDetailsArrayListNew!=null) {
            Intent intent = new Intent ( StoreListActivity.this, ViewItemsActivity.class );
            intent.putExtra ( "StoreId", userDetailsArrayListNew.get(position).get(0).getUserId () );
            intent.putExtra ( "StoreName", userDetailsArrayListNew.get(position).get(0).getStoreName () );
            intent.putExtra ( "storeLatitude",userDetailsArrayListNew.get(position).get(0).getStoreLatitude () );
            intent.putExtra ( "storeLongitude", userDetailsArrayListNew.get(position).get(0).getStoreLongtide () );


            int Radius = 6371;// radius of earth in Km
            double lat1 = HomePageAcitivity.saved_Userlatitude;
            double lat2 = userDetailsArrayListNew.get(position).get(0).getStoreLatitude ();
            double lon1 = HomePageAcitivity.saved_Userlongtitude;
            double lon2 = userDetailsArrayListNew.get(position).get(0).getStoreLongtide ();
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
            SharedPreferences sharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("STORELATITUDE", String.valueOf(userDetailsArrayListNew.get(position).get(0).getStoreLatitude()));
            editor.putString("STORELONGTITUDE", String.valueOf(userDetailsArrayListNew.get(position).get(0).getStoreLongtide()));
            editor.putString("STOREDISTANCE", String.valueOf(roundOff));
            editor.putString("STOREID", String.valueOf(userDetailsArrayListNew.get(position).get(0).getUserId()));
            editor.putString("STORENAME", String.valueOf(userDetailsArrayListNew.get(position).get(0).getStoreName()));
            editor.putString("STOREADDRESS", String.valueOf(userDetailsArrayListNew.get(position).get(0).getAddress()));
            editor.putString("FSSAICERTIFICATE", String.valueOf(userDetailsArrayListNew.get(position).get(0).getFssaiNumber()));
            editor.commit();
            Bundle bundle = new Bundle();
            bundle.putString("Store",userDetailsArrayListNew.get(position).get(0).getStoreName ());
            mFirebaseAnalytics.logEvent(userDetailsArrayListNew.get(position).get(0).getStoreName (), bundle);
            intent.putExtra ( CUSTOMER_ID, getIntent ().getStringExtra ( CUSTOMER_ID ) );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }
    }
    @Override
    public void onPictureClicked1(int position) {



        if (userDetailsArrayListNew2.size()>0 && userDetailsArrayListNew2!=null) {
            position=position;
            Intent intent = new Intent ( StoreListActivity.this, ViewItemsActivity.class );
            intent.putExtra ( "StoreId", userDetailsArrayListNew2.get(position).get(0).getUserId () );
            intent.putExtra ( "StoreName", userDetailsArrayListNew2.get(position).get(0).getStoreName () );
            intent.putExtra ( "storeLatitude",userDetailsArrayListNew2.get(position).get(0).getStoreLatitude () );
            intent.putExtra ( "storeLongitude", userDetailsArrayListNew2.get(position).get(0).getStoreLongtide () );

            int Radius = 6371;// radius of earth in Km
            double lat1 = HomePageAcitivity.saved_Userlatitude;
            double lat2 = userDetailsArrayListNew2.get(position).get(0).getStoreLatitude ();
            double lon1 = HomePageAcitivity.saved_Userlongtitude;
            double lon2 = userDetailsArrayListNew2.get(position).get(0).getStoreLongtide ();
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
            SharedPreferences sharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("STORELATITUDE", String.valueOf(userDetailsArrayListNew2.get(position).get(0).getStoreLatitude()));
            editor.putString("STORELONGTITUDE", String.valueOf(userDetailsArrayListNew2.get(position).get(0).getStoreLongtide()));
            editor.putString("STOREDISTANCE", String.valueOf(roundOff));
            editor.putString("STOREID", String.valueOf(userDetailsArrayListNew2.get(position).get(0).getUserId()));
            editor.putString("STORENAME", String.valueOf(userDetailsArrayListNew2.get(position).get(0).getStoreName()));
            editor.putString("STOREADDRESS", String.valueOf(userDetailsArrayListNew2.get(position).get(0).getAddress()));
            editor.putString("FSSAICERTIFICATE", String.valueOf(userDetailsArrayListNew2.get(position).get(0).getFssaiNumber()));
            editor.commit();
            Bundle bundle = new Bundle();
            bundle.putString("Store",userDetailsArrayListNew2.get(position).get(0).getStoreName ());
            mFirebaseAnalytics.logEvent(userDetailsArrayListNew2.get(position).get(0).getStoreName (), bundle);
            intent.putExtra ( CUSTOMER_ID, getIntent ().getStringExtra ( CUSTOMER_ID ) );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }
    }
}