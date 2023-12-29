package com.smiligenceUAT1.metrozcustomer;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smiligenceUAT1.metrozcustomer.adapter.CategoryAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.ViewFlipperAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.ViewFlipperAdapter1;
import com.smiligenceUAT1.metrozcustomer.bean.AdvertisementDetails;
import com.smiligenceUAT1.metrozcustomer.bean.CategoryDetails;
import com.smiligenceUAT1.metrozcustomer.bean.ContactDetails;
import com.smiligenceUAT1.metrozcustomer.bean.CustomerDetails;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.bean.StoreTimings;
import com.smiligenceUAT1.metrozcustomer.bean.UserDetails;
import com.smiligenceUAT1.metrozcustomer.common.ActionDialog;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.DateUtils;
import com.smiligenceUAT1.metrozcustomer.common.Interface;
import com.smiligenceUAT1.metrozcustomer.common.Interface1;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.CATEGORY_DETAILS_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.CATEGORY_IMAGE_STORAGE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.CUSTOMER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.CUSTOMER_ID;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.DATE_FORMAT;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.HOME_PAGE_PRIMARY;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.HOME_PAGE_SECONDARY;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.VIEW_CART_FIREBASE_TABLE;

public class HomePageAcitivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, Interface.ClickedListener, Interface1.ClickedListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static final int Request_User_Location_Code = 99;
    TextView displayLocationTextView;
    CategoryDetails categoryDetails;
    String postalCode, fullAddress;
    ImageView customerProfile;
    String categoryIDfromAdv, categoryNamefromAdv;
    ItemDetails itemDetails;
    long maxid = 0;
    RecyclerView primarycategoryRecyclerView, secondaryCategoryGrid, teritaryCategoryGrid;
    DatabaseReference categoryRef,bannerDataRef, userCurrentLocationDetails, storeTimingDataRef, advertisementref, itemDataRef, contactRef;
    boolean checkRef=true;
    String categoryIDforAd;
    String categoryNameAd;
    StorageReference category_storage;
    CustomerDetails userCurrentLocation;
    CategoryAdapter categoryAdapter1, categoryAdapter2, categoryAdapter3;
    ArrayList<AdvertisementDetails> advertisementDetailsList = new ArrayList<AdvertisementDetails> ();
    ArrayList<AdvertisementDetails> adv_secondaryList = new ArrayList<AdvertisementDetails> ();
    ArrayList<AdvertisementDetails> timeDetailsList = new ArrayList<AdvertisementDetails> ();
    ArrayList<String> expiringTimeList = new ArrayList<> ();

    private ArrayList<CategoryDetails> priority1catagoryList = new ArrayList<CategoryDetails> ();
    private ArrayList<CategoryDetails> priority2catagoryList = new ArrayList<CategoryDetails> ();
    private ArrayList<CategoryDetails> priority3catagoryList = new ArrayList<CategoryDetails> ();
    public static ArrayList<ItemDetails> advertisementListforBrand = new ArrayList<ItemDetails> ();

    public String saved_userName, saved_customer, saved_customerPhonenumber, saved_pincode, saved_id;
    public static double saved_Userlatitude, saved_Userlongtitude;
    String categoryNameString;
    BottomNavigationView bottomNavigation;

    ImageView locationImageView;
    private BroadcastReceiver MyReceiver = new MyReceiver ();
    TextView cartText;
    ImageView cartIcon;

    Query advertisementQuery,advertisementBannerQuery, advertisementBannerQuery1,advertisementActiveStatusQuery;
    Thread thread;
    AdvertisementDetails advertisementDetails;
    ArrayList<String> dateStringList=new ArrayList<>();
    boolean isCheck = false;
    String currentDateAndTime;
    ArrayList<AdvertisementDetails> advertisementDetailsObjectList = new ArrayList<>();
    ArrayList<AdvertisementDetails> advertisementDetailsObjectList1 = new ArrayList<>();
    ArrayList<String> advertisementPriorOne = new ArrayList<>();
    RelativeLayout whatsAppIcon;
    CardView cardView,cardView1;
    double getStoreLatitude,getStoreLongtitude;
    private FirebaseAnalytics mFirebaseAnalytics;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;

    AdapterViewFlipper adapterViewFlipper,adapterViewFlipper1;
    ArrayList<UserDetails> userDetailsArrayList=new ArrayList<UserDetails>();
    ArrayList<ArrayList<UserDetails>> userDetailsArrayListNew=new ArrayList<ArrayList<UserDetails>>();
    private TextView[] dots;
    LinearLayout dotsLayout;
    public static  double roundOff;
    private ActionDialog dialog;
    String checkAdStatus,getAdType;
    ArrayList<UserDetails> userDetailsArrayList1=new ArrayList<UserDetails>();
    ArrayList<ArrayList<UserDetails>> userDetailsArrayListNew1=new ArrayList<ArrayList<UserDetails>>();
    private final int UPDATE_REQUEST_CODE=1612;
    private AppUpdateManager appUpdateManager;
    private static final int RCQ=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        Utils.getDatabase();
        CommonMethods commonMethods = new CommonMethods ( getApplicationContext () );
        commonMethods.checkGPSConnection ();

        registerReceiver ( MyReceiver, new IntentFilter ( ConnectivityManager.CONNECTIVITY_ACTION ) );

        setContentView ( R.layout.activity_home_page_acitivity );

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        appUpdateManager=AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability()==UpdateAvailability.UPDATE_AVAILABLE &&
                        result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
                {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result,AppUpdateType.IMMEDIATE,HomePageAcitivity.this,RCQ
                        );
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        customerProfile = findViewById ( R.id.cus_profile );
        displayLocationTextView = findViewById ( R.id.displyLocationTextView );
        displayLocationTextView.setSelected ( true );
        primarycategoryRecyclerView = findViewById ( R.id.catagoryGrid1 );
        primarycategoryRecyclerView.setNestedScrollingEnabled(false);
        secondaryCategoryGrid = findViewById ( R.id.catagoryGrid2 );
        secondaryCategoryGrid.setNestedScrollingEnabled(false);
        teritaryCategoryGrid = findViewById ( R.id.catagoryGrid3 );
        teritaryCategoryGrid.setNestedScrollingEnabled(false);

        //advertisementRecyclerView = findViewById ( R.id.my_recycler_view );
        // primaryAdView = findViewById ( R.id.primaryadLayout );
        // advertisementRecyclerView.setNestedScrollingEnabled(false);
        // advertisementLinearLayout = findViewById ( R.id.advertisementLinearLayout );

        whatsAppIcon = findViewById(R.id.whatsappIcon);
        //adapterViewFlipper = findViewById(R.id.diwalibanners);
        locationImageView = findViewById ( R.id.location_image );
        cartText = findViewById ( R.id.cart_badge );
        cartIcon = findViewById ( R.id.carctIcon );
        cardView = findViewById(R.id.adcard);
        cardView1=findViewById(R.id.adcard1);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        adapterViewFlipper = findViewById(R.id.diwalibanners);
        bottomNavigation = findViewById ( R.id.drawerLayout );
        bottomNavigation.setSelectedItemId ( R.id.home );
        adapterViewFlipper1=findViewById(R.id.diwalibanners1);
        checkAdStatus=getIntent().getStringExtra("AdFromLogin");
        getAdType=getIntent ().getStringExtra ( "automatic" );

        advertisementref = CommonMethods.fetchFirebaseDatabaseReference ( "Advertisements" );
        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference ( PRODUCT_DETAILS_FIREBASE_TABLE );
        contactRef = CommonMethods.fetchFirebaseDatabaseReference("constant");
        advertisementref.keepSynced ( true );
        itemDataRef.keepSynced ( true );
        final SharedPreferences loginSharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences.getString ( "customerId", "" );
        saved_userName = loginSharedPreferences.getString ( "userNameStr", "" );
        saved_customer = loginSharedPreferences.getString ( "customerName", "" );
        saved_customerPhonenumber = loginSharedPreferences.getString ( "customerPhoneNumber", "" );


        getStoreLatitude = getIntent ().getDoubleExtra ( "storeLatitude", 0.0 );
        getStoreLongtitude = getIntent ().getDoubleExtra ( "storeLongitude", 0.0 );
        DateFormat dateFormat = new SimpleDateFormat ( DATE_FORMAT );
        currentDateAndTime = dateFormat.format ( new Date () );
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("MMMM dd, YYYY");
        String strDate = "Current Date : " + mdformat.format(calendar.getTime());
        Calendar cal = Calendar.getInstance();
        currentLocalTime = cal.getTime();
        date = new SimpleDateFormat("HH:mm aa");
        currentTime = date.format(currentLocalTime);
        storeTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference ( "storeTimingMaintenance" );
        storeTimingDataRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () > 0) {

                    for ( DataSnapshot storeTimingSnap : dataSnapshot.getChildren () ) {
                        StoreTimings storeTimings = storeTimingSnap.getValue(StoreTimings.class);

                        if (!storeTimings.getCreationDate().equals(""))
                        {
                            if (!storeTimings.getCreationDate().equals(currentDateAndTime)) {
                                if (!((Activity) HomePageAcitivity.this).isFinishing()) {
                                    if(storeTimings.getSellerId()!=null && !storeTimings.getSellerId().equals("")) {
                                        DatabaseReference startTimeDataRef = CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance").child(storeTimings.getSellerId());
                                        startTimeDataRef.child("storeStatus").setValue("");
                                        startTimeDataRef.child("creationDate").setValue(currentDateAndTime);
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
        } );

        advertisementBannerQuery = advertisementref.orderByChild("advertisementPlacing").equalTo(HOME_PAGE_PRIMARY);
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
                        System.out.println("KSDLJJKV"+advertisementDetails.getAdvertisingAgent());
                        if(advertisementDetails.getAdvertisementPlacing().equals(HOME_PAGE_PRIMARY))
                        {
                            if(advertisementDetails.getAdvertisingAgent()!=null ) {
                                if (!(advertisementDetails.getAdvertisingAgent().equals("Instruction Ad")))
                                {
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
                                } else {
                                    if (advertisementDetails.getAdvertisementstatus().equals("Active")) {
                                        advertisementDetailsObjectList.add(advertisementDetails);
                                        ArrayList<UserDetails> userDetailsArrayList1 = new ArrayList<UserDetails>();
                                        userDetailsArrayListNew.add(userDetailsArrayList1);
                                    }
                                }
                            }
                        }
                        if (advertisementDetailsObjectList.size() > 0) {
                            cardView.setVisibility(View.VISIBLE);
                            adapterViewFlipper.setAdapter(new ViewFlipperAdapter(HomePageAcitivity.this, advertisementDetailsObjectList,HomePageAcitivity.this::onPictureClicked));
                            adapterViewFlipper.setFlipInterval(4000);
                            adapterViewFlipper.startFlipping();
                            adapterViewFlipper.setAutoStart(true);
                            adapterViewFlipper.setAnimateFirstView(false);
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

        advertisementBannerQuery1 = advertisementref.orderByChild("advertisementPlacing").equalTo(HOME_PAGE_SECONDARY);
        advertisementBannerQuery1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0)
                {
                    advertisementDetailsObjectList1.clear();
                    advertisementPriorOne.clear();
                    dateStringList.clear();
                    userDetailsArrayList1.clear();
                    userDetailsArrayListNew1.clear();
                    for (DataSnapshot adSnap:snapshot.getChildren())
                    {
                        advertisementDetails = adSnap.getValue(AdvertisementDetails.class);
                        if(advertisementDetails.getAdvertisementPlacing().equals(HOME_PAGE_SECONDARY))
                        {
                            if (!(advertisementDetails.getAdvertisingAgent().equals("Instruction Ad"))) {

                                if(advertisementDetails.getAdvertisementPlacing().equals(HOME_PAGE_SECONDARY))
                                {
                                    if (advertisementDetails.getAdvertisingDuration().equals("One Hour"))
                                    {
                                        if (advertisementDetails.getAdvertisementEndingDurationDate()!=null) {
                                            if (advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate())) {
                                                try {
                                                    if ((sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration())) == -1) ||
                                                            (sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration())) == 0)) {
                                                        advertisementDetailsObjectList1.add(advertisementDetails);
                                                        userDetailsArrayList1 = (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                        userDetailsArrayListNew1.add(userDetailsArrayList1);
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
                                                    userDetailsArrayList1= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew1.add(userDetailsArrayList1);
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
                                                    userDetailsArrayList1= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew1.add(userDetailsArrayList1);
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
                                                    userDetailsArrayList1= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew1.add(userDetailsArrayList1);
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
                                                    userDetailsArrayList1= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew1.add(userDetailsArrayList1);
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
                                                        userDetailsArrayList1= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                        userDetailsArrayListNew1.add(userDetailsArrayList1);
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
                                    userDetailsArrayListNew1.add(userDetailsArrayList1);
                                }
                            }
                        }

                        if (advertisementDetailsObjectList1.size() > 0) {
                            cardView1.setVisibility(View.VISIBLE);
                            adapterViewFlipper1.setAdapter(new ViewFlipperAdapter1(HomePageAcitivity.this, advertisementDetailsObjectList1,HomePageAcitivity.this::onPictureClicked1));

                            adapterViewFlipper1.setFlipInterval(4000);
                            adapterViewFlipper1.startFlipping();
                            adapterViewFlipper1.setAutoStart(false);
                            adapterViewFlipper1.setAnimateFirstView(false);

                        }
                        else {
                            cardView1.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        categoryRef = CommonMethods.fetchFirebaseDatabaseReference( CATEGORY_DETAILS_TABLE );
        categoryRef.keepSynced ( true );
        category_storage = FirebaseStorage.getInstance ().getReference ( CATEGORY_IMAGE_STORAGE );



        whatsAppIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(checkRef) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                ContactDetails contactDetails = dataSnapshot.getValue(ContactDetails.class);
                                String url = "https://api.whatsapp.com/send?phone=" + "+91" + contactDetails.getWhatsAppContact();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError databaseError) {

                    }}); }
        });





        DatabaseReference databaseReference = CommonMethods.fetchFirebaseDatabaseReference ( VIEW_CART_FIREBASE_TABLE ).child ( saved_id );

        databaseReference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () > 0) {
                    cartText.setText ( String.valueOf ( dataSnapshot.getChildrenCount () ) );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        userCurrentLocationDetails = CommonMethods.fetchFirebaseDatabaseReference ( CUSTOMER_DETAILS_FIREBASE_TABLE ).child ( String.valueOf ( saved_id ) );

        userCurrentLocationDetails.keepSynced ( true );

        userCurrentLocationDetails.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () > 0) {
                    userCurrentLocation = dataSnapshot.getValue ( CustomerDetails.class );
                    saved_pincode = userCurrentLocation.getCurrentPincode ();
                    saved_Userlatitude = userCurrentLocation.getUserLatitude ();
                    saved_Userlongtitude = userCurrentLocation.getUserLongtitude ();
                    if (userCurrentLocation.getCurrentAddress () != null && !"".equals ( userCurrentLocation.getCurrentAddress () )) {
                        displayLocationTextView.setText ( userCurrentLocation.getCurrentAddress () + "." );

                    } else {
                        Intent intent = new Intent ( HomePageAcitivity.this, FindAddressActivity.class );
                        intent.putExtra ( "currentAddress", "CurrentLocation" );
                        intent.putExtra ( "automatic", "true" );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( intent );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        locationImageView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent ( HomePageAcitivity.this, FindAddressActivity.class );
                intent.putExtra ( "currentAddress", "CurrentLocation" );
                intent.putExtra ( "automatic", "false" );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        } );


        primarycategoryRecyclerView.setLayoutManager ( new GridLayoutManager ( HomePageAcitivity.this, 1 ) );
        primarycategoryRecyclerView.setHasFixedSize ( true );

        secondaryCategoryGrid.setLayoutManager ( new GridLayoutManager ( HomePageAcitivity.this, 2 ) );
        secondaryCategoryGrid.setHasFixedSize ( true );

        int mNoOfColumns_teritory = Utility.calculateNoOfColumns ( getApplicationContext (), 170 );
        teritaryCategoryGrid.setLayoutManager ( new GridLayoutManager ( HomePageAcitivity.this, mNoOfColumns_teritory ) );
        teritaryCategoryGrid.setHasFixedSize ( true );

        bottomNavigation.setOnNavigationItemSelectedListener ( new BottomNavigationView.OnNavigationItemSelectedListener () {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId ()) {
                    case R.id.order: {
                        Intent intent = new Intent ( getApplicationContext (), CustomersOrdersActivity.class );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( intent );
                        return true;
                    }
                    case R.id.home: {
                        Intent intent = new Intent ( getApplicationContext (), HomePageAcitivity.class );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( intent );
                        return true;
                    }
                    case R.id.searchscreen: {
                        Intent intent = new Intent ( getApplicationContext (), SearchActivity.class );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( intent );
                        return true;
                    }
                }
                return false;
            }
        } );

        cartIcon.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent ( HomePageAcitivity.this, ViewCartActivity.class );
                intent.putExtra ( "storeLatitude", getStoreLatitude );
                intent.putExtra ( "storeLongitude", getStoreLongtitude );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        });


        customerProfile.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( HomePageAcitivity.this, CustomerProfileActivity.class );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        } );

        categoryRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                priority1catagoryList.clear ();
                priority2catagoryList.clear ();
                priority3catagoryList.clear ();

                for ( DataSnapshot postSnapshot : dataSnapshot.getChildren () ) {
                    CategoryDetails categoryDetails = postSnapshot.getValue ( CategoryDetails.class );

                    if ("1".equalsIgnoreCase ( categoryDetails.getCategoryPriority () )) {
                        priority1catagoryList.add ( categoryDetails );
                    } else if ("2".equalsIgnoreCase ( categoryDetails.getCategoryPriority () )) {
                        priority2catagoryList.add ( categoryDetails );
                    } else if ("3".equalsIgnoreCase ( categoryDetails.getCategoryPriority () )) {
                        priority3catagoryList.add ( categoryDetails );
                    }
                }

                if (priority1catagoryList.size () > 0) {
                    categoryAdapter1 = new CategoryAdapter ( HomePageAcitivity.this, priority1catagoryList, 1 );
                    categoryAdapter1.notifyDataSetChanged ();
                    primarycategoryRecyclerView.setAdapter ( categoryAdapter1 );
                }

                if (priority2catagoryList.size () > 0) {
                    categoryAdapter2 = new CategoryAdapter ( HomePageAcitivity.this, priority2catagoryList, 2 );
                    categoryAdapter2.notifyDataSetChanged ();
                    secondaryCategoryGrid.setAdapter ( categoryAdapter2 );
                }

                if (priority3catagoryList.size () > 0) {
                    categoryAdapter3 = new CategoryAdapter ( HomePageAcitivity.this, priority3catagoryList, 3 );
                    categoryAdapter3.notifyDataSetChanged ();
                    teritaryCategoryGrid.setAdapter ( categoryAdapter3 );
                }


                if (categoryAdapter1 != null) {
                    categoryAdapter1.setOnItemclickListener ( new CategoryAdapter.OnItemClicklistener () {
                        @Override
                        public void Onitemclick(int Position) {

                            categoryDetails = priority1catagoryList.get ( Position );
                            categoryNameString = categoryDetails.getCategoryName ();

                            if (("PICKUP AND DROP").equalsIgnoreCase ( categoryNameString )) {
                                Intent pickUpIntent = new Intent ( HomePageAcitivity.this, PickAndDropActivity.class );
                                startActivity ( pickUpIntent );
                            } else {
                                navigateToStoreList ();
                            }
                        }
                    } );
                }

                if (categoryAdapter2 != null) {

                    categoryAdapter2.setOnItemclickListener ( new CategoryAdapter.OnItemClicklistener () {
                        @Override
                        public void Onitemclick(int Position) {
                            categoryDetails = priority2catagoryList.get ( Position );
                            categoryNameString = categoryDetails.getCategoryName ();

                            if (("PICKUP AND DROP").equalsIgnoreCase ( categoryNameString )) {
                                Intent pickUpIntent = new Intent ( HomePageAcitivity.this, PickAndDropActivity.class );
                                startActivity ( pickUpIntent );
                            } else {
                                navigateToStoreList ();

                            }
                        }
                    } );

                }
                if (categoryAdapter3 != null) {

                    categoryAdapter3.setOnItemclickListener ( new CategoryAdapter.OnItemClicklistener () {
                        @Override
                        public void Onitemclick(int Position) {

                            categoryDetails = priority3catagoryList.get ( Position );
                            categoryNameString = categoryDetails.getCategoryName ();

                            if (("PICKUP AND DROP").equalsIgnoreCase ( categoryNameString )) {
                                Intent pickUpIntent = new Intent ( HomePageAcitivity.this, PickAndDropActivity.class );
                                startActivity ( pickUpIntent );
                            } else {
                                navigateToStoreList ();

                            }
                        }
                    } );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
    }

    private void navigateToStoreList() {

        Intent intent = new Intent ( HomePageAcitivity.this, StoreListActivity.class );
        intent.putExtra ( "PinCode", userCurrentLocation.getCurrentPincode () );
        intent.putExtra ( "categoryName", categoryDetails.getCategoryName () );
        intent.putExtra ( "categoryId", categoryDetails.getCategoryid () );
        intent.putExtra("customerLatitude",userCurrentLocation.getUserLatitude());
        intent.putExtra("customerLongtitude",userCurrentLocation.getUserLongtitude());
        intent.putExtra ( CUSTOMER_ID, saved_id );
        SharedPreferences sharedPreferences = getSharedPreferences("DISTANCE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Distance", categoryDetails.getNormalDistanceDelivery());
        editor.commit();
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    @Override
    public void onLocationChanged(Location location) {
        final LatLng latlng = new LatLng ( location.getLatitude (), location.getLongitude () );
        Geocoder geocoder = new Geocoder ( HomePageAcitivity.this, Locale.getDefault () );
        try {
            List<Address> addressList = geocoder.getFromLocation ( latlng.latitude, latlng.longitude, 100 );
            if (addressList != null && !addressList.isEmpty ()) {
                String address = (String) addressList.get ( 0 ).getAddressLine ( 0 );
                String locality = addressList.get ( 0 ).getLocality ();
                String city = addressList.get ( 0 ).getSubAdminArea ();
                String subLocality = addressList.get ( 0 ).getSubLocality ();
                postalCode = addressList.get ( 0 ).getPostalCode ();
                fullAddress = subLocality + ", " + locality + "," + city;
                displayLocationTextView.setText ( fullAddress + "," + postalCode );
                DatabaseReference ref = FirebaseDatabase.getInstance ("https://uat-testing-metroz-default-rtdb.firebaseio.com/").getReference ( "CustomerLoginDetails" ).child ( String.valueOf ( saved_id ) );
                ref.child ( "currentAddress" ).setValue ( address );
                ref.child ( "currentPincode" ).setValue ( postalCode );
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates ( googleApiClient, HomePageAcitivity.this );
        }
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest ();
        locationRequest.setInterval ( 1100 );
        locationRequest.setFastestInterval ( 1100 );
        locationRequest.setPriority ( LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );
        if (ContextCompat.checkSelfPermission ( HomePageAcitivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates ( googleApiClient, locationRequest, HomePageAcitivity.this );
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public static class Utility {
        public static int calculateNoOfColumns(Context context, float columnWidthDp)
        {
            DisplayMetrics displayMetrics = context.getResources ().getDisplayMetrics ();
            float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
            int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
            return noOfColumns;
        }
    }

    @Override
    public void onBackPressed() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( HomePageAcitivity.this );
        bottomSheetDialog.setContentView ( R.layout.application_exiting_dialog );
        Button quit = bottomSheetDialog.findViewById ( R.id.quit_dialog );
        Button cancel = bottomSheetDialog.findViewById ( R.id.cancel_dialog );

        bottomSheetDialog.show ();
        bottomSheetDialog.setCancelable ( false );

        quit.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                moveTaskToBack ( true );
                bottomSheetDialog.dismiss ();
            }
        } );
        cancel.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss ();
            }
        } );
    }

    @Override
    public void onPictureClicked(int position) {

        if (userDetailsArrayListNew.size()>0 && userDetailsArrayListNew!=null) {
            Intent intent = new Intent ( HomePageAcitivity.this, ViewItemsActivity.class );
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
    public void onPictureClicked1(int position1) {



        if (userDetailsArrayListNew1.size()>0 && userDetailsArrayListNew1!=null) {
            int position=position1;
            Intent intent = new Intent ( HomePageAcitivity.this, ViewItemsActivity.class );
            intent.putExtra ( "StoreId", userDetailsArrayListNew1.get(position).get(0).getUserId () );
            intent.putExtra ( "StoreName", userDetailsArrayListNew1.get(position).get(0).getStoreName () );
            intent.putExtra ( "storeLatitude",userDetailsArrayListNew1.get(position).get(0).getStoreLatitude () );
            intent.putExtra ( "storeLongitude", userDetailsArrayListNew1.get(position).get(0).getStoreLongtide () );

            int Radius = 6371;// radius of earth in Km
            double lat1 = HomePageAcitivity.saved_Userlatitude;
            double lat2 = userDetailsArrayListNew1.get(position).get(0).getStoreLatitude ();
            double lon1 = HomePageAcitivity.saved_Userlongtitude;
            double lon2 = userDetailsArrayListNew1.get(position).get(0).getStoreLongtide ();
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
            editor.putString("STORELATITUDE", String.valueOf(userDetailsArrayListNew1.get(position).get(0).getStoreLatitude()));
            editor.putString("STORELONGTITUDE", String.valueOf(userDetailsArrayListNew1.get(position).get(0).getStoreLongtide()));
            editor.putString("STOREDISTANCE", String.valueOf(roundOff));
            editor.putString("STOREID", String.valueOf(userDetailsArrayListNew1.get(position).get(0).getUserId()));
            editor.putString("STORENAME", String.valueOf(userDetailsArrayListNew1.get(position).get(0).getStoreName()));
            editor.putString("STOREADDRESS", String.valueOf(userDetailsArrayListNew1.get(position).get(0).getAddress()));
            editor.putString("FSSAICERTIFICATE", String.valueOf(userDetailsArrayListNew1.get(position).get(0).getFssaiNumber()));
            editor.commit();
            Bundle bundle = new Bundle();
            bundle.putString("Store",userDetailsArrayListNew1.get(position).get(0).getStoreName ());
            mFirebaseAnalytics.logEvent(userDetailsArrayListNew1.get(position).get(0).getStoreName (), bundle);
            intent.putExtra ( CUSTOMER_ID, getIntent ().getStringExtra ( CUSTOMER_ID ) );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }
    }
    private InstallStateUpdatedListener installStateUpdatedListener=new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState state) {

            if (state.installStatus()== InstallStatus.DOWNLOADED)
            {
                showCompletedUpdate();
            }
        }
    };

    private void showCompletedUpdate() {
        Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content),"New app isready",Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability()==UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
                {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result,AppUpdateType.IMMEDIATE,HomePageAcitivity.this,RCQ
                        );
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==RCQ&&resultCode!=RESULT_OK)
        {
            Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}