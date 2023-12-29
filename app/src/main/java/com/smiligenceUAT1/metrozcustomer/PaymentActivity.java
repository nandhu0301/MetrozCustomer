package com.smiligenceUAT1.metrozcustomer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterViewFlipper;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.smiligenceUAT1.metrozcustomer.adapter.PaymentAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.ViewFlipperAdapter;
import com.smiligenceUAT1.metrozcustomer.bean.AdvertisementDetails;
import com.smiligenceUAT1.metrozcustomer.bean.CustomerDetails;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.bean.MaintainFairDetails;
import com.smiligenceUAT1.metrozcustomer.bean.MetrozStoreTime;
import com.smiligenceUAT1.metrozcustomer.bean.OrderDetails;
import com.smiligenceUAT1.metrozcustomer.bean.StoreTimings;
import com.smiligenceUAT1.metrozcustomer.bean.UserDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Constant;
import com.smiligenceUAT1.metrozcustomer.common.DateUtils;
import com.smiligenceUAT1.metrozcustomer.common.Interface;
import com.smiligenceUAT1.metrozcustomer.common.TextUtils;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.CUSTOMER_ID;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.ORDER_SUMMARY_PAGE_PRIMARY;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.RAZORPAY_KEY_ID;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.RAZORPAY_SECRAT_KEY;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.VIEW_CART_FIREBASE_TABLE;

public class PaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener,Interface.ClickedListener {


    DatabaseReference distanceFeeDataRef, viewCartRef, metrozStoteTimingDataRef, oneTimeDisDataRef, storeTimingDataRef, userCurrentLocationDetails, Orderreference;
    OrderDetails orderDetails = new OrderDetails();
    ItemDetails itemDetails = new ItemDetails();
    ArrayList<ItemDetails> giftWrapItemList = new ArrayList<ItemDetails>();
    ArrayList<String> giftWappingItemList = new ArrayList<>();
    Animation animation;

    int finalBillAmount, tipsAmountInt, temp, giftWrapAmount = 0;
    ListView purchaseListView;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<ItemDetails>();
    TextView totalPurchaseAmount;
    RelativeLayout purchaseLayout;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String metrozStartTime;
    String metrozStopTime;
    String amount;
    boolean checkAsync = true;
    Payment payment;
    String paymentType;
    double getStoreLatitude = 0.0, getStoreLongtitude = 0.0, roundOff = 0.0;
    CustomerDetails userCurrentLocation;
    String storeNameFromAdapter, storeId, categoryName, categoryId, pinCode;
    double sharedPreferenceLatitude = 0.0, sharedPreferenceLongtitude = 0.0;
    int getDistance;
    RazorpayClient razorpay;
    JSONObject jsonObject;
    long maxid = 0;
    String resultOrderId, receiptNumber;
    boolean checkIntent = false;
    TextView addCurrentAddressEditText;
    String deliverytType, tipAmount, finalBill_tip, finalBill, instructionString, storeAddress, getFullAdreesFromMap;
    Double latValue, longValue;
    int resultKiloMeterRoundOff = 0, totaldeliveryFee = 0, perKmDistanceFee = 0, perkmDistanceFeeForDeliveryBoy = 0;
    ImageView backToScreen;
    String sharedPreferenceLat, sharedPreferenceLong, addressPref, shippingAddress, customerName, customerMobilenumber, customerPinCode;
    TextView viewAddress, giftWrapText;
    TextView totalItemValue, tipValue, toPayValue;
    TextView giftText, giftAmount;
    List<String> name;
    List<Integer> id;
    TextView offerName, viewOffers;
    String testPay = "";
    int getDiscountAmount = 0, getMaximumBillAmount = 0, ifPercentMaxAmountForDiscount = 0;
    String discountName, typeOfDiscount;
    TextView deductionAmountTextView;
    String discountAppliedOrNot = "No";
    int discountAmountCalculation = 0;
    String getDiscountGivenBy;
    String getPreference, oneTimeOrderNumber;
    int totalFeeFordeliveryBoy;
    Integer increamentId;
    SweetAlertDialog pDialog, pDialogNew;
    String TIME_SERVER;
    NTPUDPClient timeClient;
    CardView cardView1;
    AdapterViewFlipper adapterViewFlipper, adapterViewFlipper1;
    Query advertisementBannerQuery;
    DatabaseReference advertisementref;
    ArrayList<UserDetails> userDetailsArrayList = new ArrayList<UserDetails>();
    ArrayList<ArrayList<UserDetails>> userDetailsArrayListNew = new ArrayList<ArrayList<UserDetails>>();
    AdvertisementDetails advertisementDetails;
    ArrayList<AdvertisementDetails> advertisementDetailsObjectList = new ArrayList<>();
    ArrayList<AdvertisementDetails> advertisementDetailsObjectList1 = new ArrayList<>();
    ArrayList<String> advertisementPriorOne = new ArrayList<>();
    ArrayList<String> dateStringList = new ArrayList<>();

    String paymentDateStr;
    String formattedDateStr;
    String orderTimeStr;
    String orderCreatedDateStr;
    String orderCreatedTimeStr;
    public String saved_userName, saved_customer, saved_customerPhonenumber, saved_pincode, saved_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        final SharedPreferences loginSharedPreferences1 = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences1.getString("customerId", "");
        saved_userName = loginSharedPreferences1.getString("userNameStr", "");
        saved_customer = loginSharedPreferences1.getString("customerName", "");
        saved_customerPhonenumber = loginSharedPreferences1.getString("customerPhoneNumber", "");

        purchaseListView = findViewById(R.id.purchaselist);
        viewAddress = findViewById(R.id.addresstext);
        totalPurchaseAmount = findViewById(R.id.totalpurchaseamountText);
        purchaseLayout = findViewById(R.id.placeorderlayout);
        totalItemValue = findViewById(R.id.itemtotalvalue);
        offerName = findViewById(R.id.discountTextview);
        viewOffers = findViewById(R.id.viewOffers);

        tipValue = findViewById(R.id.tipsvalueText);
        toPayValue = findViewById(R.id.topayvalue);
        backToScreen = findViewById(R.id.backtoScreen);
        giftWrapText = findViewById(R.id.giftavailabletext);
        animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        giftText = findViewById(R.id.giftText);
        giftAmount = findViewById(R.id.giftTextAmount);
        deductionAmountTextView = findViewById(R.id.deductionAmount);
        distanceFeeDataRef = CommonMethods.fetchFirebaseDatabaseReference("MaintainFairDetails");
        oneTimeDisDataRef = CommonMethods.fetchFirebaseDatabaseReference("OneTimeDiscount");


        storeNameFromAdapter = getIntent().getStringExtra("StoreName");
        storeId = getIntent().getStringExtra("StoreId");
        categoryName = getIntent().getStringExtra("categoryName");
        categoryId = getIntent().getStringExtra("categoryId");
        pinCode = getIntent().getStringExtra("pinCode");
        addressPref = getIntent().getStringExtra("addressPref");
        shippingAddress = getIntent().getStringExtra("shippingAddress");
        customerName = getIntent().getStringExtra("customerName");
        customerMobilenumber = getIntent().getStringExtra("customerMobilenumber");
        customerPinCode = getIntent().getStringExtra("customerPinCode");
        deliverytType = getIntent().getStringExtra("deliveryType");
        tipAmount = getIntent().getStringExtra("tips");
        finalBill_tip = getIntent().getStringExtra("finalBillTip");
        finalBill = getIntent().getStringExtra("finalBillAmount");
        instructionString = getIntent().getStringExtra("instructionString");
        storeAddress = getIntent().getStringExtra("storeAddress");
        getFullAdreesFromMap = getIntent().getStringExtra("FullAddress");
        latValue = getIntent().getDoubleExtra("userLatitude", 0.0);
        longValue = getIntent().getDoubleExtra("userLongtitude", 0.0);
        getDiscountAmount = getIntent().getIntExtra("DiscountPrice", 0);
        getMaximumBillAmount = getIntent().getIntExtra("DiscountMaxAmount", 0);
        discountName = getIntent().getStringExtra("DiscountName");
        typeOfDiscount = getIntent().getStringExtra("Typeofdiscount");
        ifPercentMaxAmountForDiscount = getIntent().getIntExtra("BillDiscountMaxAmount", 0);
        //customerID= HomePageAcitivity.saved_id;
        getPreference = getIntent().getStringExtra("Preference");
        oneTimeOrderNumber = getIntent().getStringExtra("orderNumber");
        getDiscountGivenBy = getIntent().getStringExtra("dicountGivenBy");
        cardView1 = findViewById(R.id.adcard1);
        adapterViewFlipper1 = findViewById(R.id.diwalibanners1);
        advertisementref = CommonMethods.fetchFirebaseDatabaseReference("Advertisements");

        purchaseLayout.setVisibility(View.VISIBLE);

        getStoreLatitude = getIntent().getDoubleExtra("storeLatitude", 0);
        getStoreLongtitude = getIntent().getDoubleExtra("storeLongitude", 0);
        final SharedPreferences loginSharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE);
        if (loginSharedPreferences != null && !loginSharedPreferences.equals("")) {
            sharedPreferenceLat = loginSharedPreferences.getString("STORELATITUDE", "");
            sharedPreferenceLong = loginSharedPreferences.getString("STORELONGTITUDE", "");
        }
        if (sharedPreferenceLat != null && !sharedPreferenceLat.equals("")) {
            sharedPreferenceLatitude = Double.parseDouble(sharedPreferenceLat);
        }
        if (sharedPreferenceLong != null && !sharedPreferenceLong.equals("")) {
            sharedPreferenceLongtitude = Double.parseDouble(sharedPreferenceLong);
        }

        final SharedPreferences loginSharedPreferences2 = getSharedPreferences("DISTANCE", MODE_PRIVATE);
        getDistance = loginSharedPreferences2.getInt("Distance", 0);
        metrozStoteTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference("MetrozstoreTiming");
        storeTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance");
        userCurrentLocationDetails = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails").child(String.valueOf(saved_id));
        Orderreference = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);

        viewCartRef = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).
                child(saved_id);

        Calendar cal = Calendar.getInstance();
        currentLocalTime = cal.getTime();
        date = new SimpleDateFormat("HH:mm aa");
        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT);
        currentDateAndTime = dateFormat.format(new Date());
        currentTime = date.format(currentLocalTime);

        autoLoadFunction();
        loadFunction();

        advertisementBannerQuery = advertisementref.orderByChild("advertisementPlacing").equalTo(ORDER_SUMMARY_PAGE_PRIMARY);
        advertisementBannerQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    advertisementDetailsObjectList.clear();
                    advertisementPriorOne.clear();
                    dateStringList.clear();
                    userDetailsArrayList.clear();
                    userDetailsArrayListNew.clear();
                    for (DataSnapshot adSnap : snapshot.getChildren()) {
                        advertisementDetails = adSnap.getValue(AdvertisementDetails.class);
                        System.out.println("SADSDFDFTGTY" + userDetailsArrayListNew.size());
                        if (advertisementDetails.getAdvertisementPlacing().equals(ORDER_SUMMARY_PAGE_PRIMARY)) {
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
                            } else {
                                if (advertisementDetails.getAdvertisementstatus().equals("Active")) {
                                    advertisementDetailsObjectList.add(advertisementDetails);
                                    ArrayList<UserDetails> userDetailsArrayList1 = new ArrayList<UserDetails>();
                                    userDetailsArrayListNew.add(userDetailsArrayList1);
                                }
                            }
                        }
                        if (advertisementDetailsObjectList.size() > 0) {


                            cardView1.setVisibility(View.VISIBLE);
                            adapterViewFlipper1.setAdapter(new ViewFlipperAdapter(PaymentActivity.this, advertisementDetailsObjectList, PaymentActivity.this::onPictureClicked));
                            adapterViewFlipper1.setFlipInterval(4000);
                            adapterViewFlipper1.startFlipping();
                            adapterViewFlipper1.setAutoStart(true);
                            adapterViewFlipper1.setAnimateFirstView(false);
                        } else {

                            cardView1.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        distanceFeeDataRef.child("OtherCategory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    MaintainFairDetails maintainFairDetails = dataSnapshot.getValue(MaintainFairDetails.class);
                    perKmDistanceFee = maintainFairDetails.getPerKmOtherCategory();
                    perkmDistanceFeeForDeliveryBoy = maintainFairDetails.getPerKmForDeliveryBoy();
                    int Radius = 6371;// radius of earth in Km
                    double lat1 = HomePageAcitivity.saved_Userlatitude;
                    double lat2 = sharedPreferenceLatitude;
                    double lon1 = HomePageAcitivity.saved_Userlongtitude;
                    double lon2 = sharedPreferenceLongtitude;
                    double dLat = Math.toRadians(lat2 - lat1);
                    double dLon = Math.toRadians(lon2 - lon1);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                            + Math.cos(Math.toRadians(lat1))
                            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                            * Math.sin(dLon / 2);
                    double c = 2 * Math.asin(Math.sqrt(a));
                    double valueResult = Radius * c;
                    double km = valueResult / 1;
                    resultKiloMeterRoundOff = (int) Math.round(km);
                    roundOff = Math.round(km * 100.0) / 100.0;
                    totaldeliveryFee = resultKiloMeterRoundOff * perKmDistanceFee;
                    totalFeeFordeliveryBoy = resultKiloMeterRoundOff * perkmDistanceFeeForDeliveryBoy;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        distanceFeeDataRef.child("DeliveryBoyCharges").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    MaintainFairDetails maintainFairDetails = dataSnapshot.getValue(MaintainFairDetails.class);
                    perkmDistanceFeeForDeliveryBoy = maintainFairDetails.getPerKmForDeliveryBoy();
                    int Radius = 6371;// radius of earth in Km
                    double lat1 = HomePageAcitivity.saved_Userlatitude;
                    double lat2 = sharedPreferenceLatitude;
                    double lon1 = HomePageAcitivity.saved_Userlongtitude;
                    double lon2 = sharedPreferenceLongtitude;
                    double dLat = Math.toRadians(lat2 - lat1);
                    double dLon = Math.toRadians(lon2 - lon1);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                            + Math.cos(Math.toRadians(lat1))
                            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                            * Math.sin(dLon / 2);
                    double c = 2 * Math.asin(Math.sqrt(a));
                    double valueResult = Radius * c;
                    double km = valueResult / 1;
                    resultKiloMeterRoundOff = (int) Math.round(km);
                    roundOff = Math.round(km * 100.0) / 100.0;
                    totalFeeFordeliveryBoy = resultKiloMeterRoundOff * perkmDistanceFeeForDeliveryBoy;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewOffers.getText().toString().equals("View Offers")) {
                    Intent intent = new Intent(PaymentActivity.this, DiscountActivity.class);
                    intent.putExtra("StoreName", storeNameFromAdapter);
                    intent.putExtra("StoreId", storeId);
                    intent.putExtra("categoryName", categoryName);
                    intent.putExtra("categoryId", categoryId);
                    intent.putExtra("pinCode", pinCode);
                    intent.putExtra("addressPref", addressPref);
                    intent.putExtra("FullAddress", getFullAdreesFromMap);
                    intent.putExtra("userLatitude", latValue);
                    intent.putExtra("userLongtitude", longValue);
                    intent.putExtra("deliveryType", deliverytType);
                    intent.putExtra("tips", String.valueOf(tipAmount));
                    intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                    intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("shippingAddress", shippingAddress);
                    intent.putExtra("customerName", customerName);
                    intent.putExtra("customerMobilenumber", customerMobilenumber);
                    intent.putExtra("customerPinCode", customerPinCode);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (viewOffers.getText().toString().equals("Remove")) {
                    typeOfDiscount = null;
                    getDiscountAmount = 0;
                    getMaximumBillAmount = 0;
                    discountName = "-";
                    getPreference = "";
                    viewOffers.setText("View Offers");
                    autoLoadFunction();
                }
            }
        });

        backToScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this, ViewCartActivity.class);
                intent.putExtra("deliveryType", deliverytType);
                intent.putExtra("tips", String.valueOf(tipAmount));
                intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                intent.putExtra("instructionString", instructionString);
                intent.putExtra("storeAddress", storeAddress);
                intent.putExtra("shippingAddress", shippingAddress);
                intent.putExtra("customerName", customerName);
                intent.putExtra("customerMobilenumber", customerMobilenumber);
                intent.putExtra("customerPinCode", customerPinCode);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        purchaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemDetailList.size() == 0) {
                    purchaseLayout.setVisibility(View.VISIBLE);
                    new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No items available in the cart.")
                            .show();
                    return;
                } else {
                    if (!CommonMethods.getConnectivityStatusString(getApplicationContext()).equals("No internet is available")) {


                        metrozStoteTimingDataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    MetrozStoreTime metrozStoreTime = dataSnapshot.getValue(MetrozStoreTime.class);
                                    metrozStartTime = metrozStoreTime.getShopStartTime();
                                    metrozStopTime = metrozStoreTime.getShopEndTime();
                                }
                                storeTimingDataRef.child(String.valueOf(storeId)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (checkAsync) {

                                            if (dataSnapshot.getChildrenCount() > 0) {

                                                StoreTimings storeTimings = dataSnapshot.getValue(StoreTimings.class);
                                                try {
                                                    //if result returns 1 => shop starts time is after metroz time
                                                    //if result returns -1 => shops end time is before metroz end time
                                                    //if result returns 0 => shop start,stop and metroz starts and stop time are same

                                                    //Starting time of shop is after metroz time && ending time of shop is before metroz time
                                                    //currentTime = new SimpleDateFormat("HH:mm aa").format(loadingFiunction());
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

                                                            purchaseLayout.setVisibility(View.VISIBLE);
                                                            //avail stores list
                                                            if (storeTimings.getStoreStatus().equalsIgnoreCase("")) {
                                                                if ((sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopStartTime())) == 1) && !(sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopEndTime())) == 1)) {

                                                                    resultChecking();
                                                                    purchaseLayout.setVisibility(View.INVISIBLE);
                                                                } else if ((sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopEndTime())) == 1) || (sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopStartTime())) == -1)) {
                                                                    new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                            .setTitleText("Error")
                                                                            .setContentText("Closed,currently not accepting orders.")
                                                                            .show();
                                                                    purchaseLayout.setVisibility(View.VISIBLE);
                                                                }
                                                            } else {
                                                                if (storeTimings.getStoreStatus().equalsIgnoreCase("Opened")) {

                                                                    resultChecking();
                                                                    purchaseLayout.setVisibility(View.INVISIBLE);
                                                                }
                                                                if (storeTimings.getStoreStatus().equalsIgnoreCase("Closed")) {
                                                                    new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                            .setTitleText("Error")
                                                                            .setContentText("Closed,currently not accepting orders.")
                                                                            .show();
                                                                    purchaseLayout.setVisibility(View.VISIBLE);
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                .setTitleText("Error")
                                                                .setContentText("Closed,currently not accepting orders.")
                                                                .show();
                                                        purchaseLayout.setVisibility(View.VISIBLE);
                                                    }
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
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
                    } else {
                        new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("No Network Connection")
                                .show();
                        purchaseLayout.setVisibility(View.VISIBLE);
                    }


                }
            }
        });

    }

    private void startpayment() throws JSONException, RazorpayException {
        razorpay = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRAT_KEY);
        JSONObject options = new JSONObject();

        options.put("amount", ((tipsAmountInt + giftWrapAmount - getDiscountAmount) * 100));
        options.put("currency", "INR");
        options.put("receipt", generateString(10));
        Order order = razorpay.Orders.create(options);
        jsonObject = new JSONObject(String.valueOf(order));
        resultOrderId = jsonObject.getString("id");
        receiptNumber = jsonObject.getString("receipt");
        checkOutFunction(resultOrderId);
    }

    private void checkOutFunction(String orderId) throws JSONException {

        final Activity activity = this;
        Checkout checkout = new Checkout();
        checkout.setKeyID(RAZORPAY_KEY_ID);
        checkout.setImage(R.mipmap.ic_launcher);
        JSONObject options = new JSONObject();
        options.put("payment_capture", true);
        options.put("order_id", orderId);
        JSONObject preFill = new JSONObject();
        //to set predefined mail and contact
        preFill.put("email", saved_userName);
        preFill.put("contact", saved_customerPhonenumber);
        options.put("prefill", preFill);

        checkout.open(activity, options);

    }

    public static String generateOTP() {
        int randomPin = (int) (Math.random() * 900000) + 100000;
        String otp = String.valueOf(randomPin);
        return otp;
    }

    protected void onStart() {
        super.onStart();

        Orderreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    giftWrapItemList.clear();
                    itemDetailList.clear();
                    finalBillAmount = 0;
                    tipsAmountInt = 0;
                    name = new ArrayList<>();
                    id = new ArrayList<>();
                    name.clear();
                    id.clear();
                    for (DataSnapshot viewCartsnap : dataSnapshot.getChildren()) {
                        itemDetails = viewCartsnap.getValue(ItemDetails.class);

                        if (itemDetails.getCategoryName().equalsIgnoreCase("GIFTS AND LIFESTYLES")) {
                            if (itemDetails.getGiftWrapOption() != null) {
                                if (itemDetails.getGiftWrapOption().equals("Yes")) {
                                    giftWrapItemList.add(itemDetails);
                                }
                            }
                        }
                        if (giftWrapItemList.size() > 0) {

                            giftWrapText.setVisibility(View.VISIBLE);
                            giftWrapText.startAnimation(animation);
                            if (!testPay.equals("Failed")) {
                                RelativeLayout my_layout = findViewById(R.id.tetef);

                                name.clear();
                                id.clear();

                                for (int i = 0; i < giftWrapItemList.size(); i++) {
                                    //name.add("C" + (i + 1));
                                    if (giftWrapItemList != null) {
                                        if (giftWrapItemList.get(i).getGiftAmount() == 0) {
                                            name.add(giftWrapItemList.get(i).getItemName() + " (Free Wrapping)");
                                        } else {
                                            name.add(giftWrapItemList.get(i).getItemName() + " (Wrapping Charge ₹" + giftWrapItemList.get(i).getGiftAmount() + ")");
                                        }
                                        id.add(i + 1);
                                    }
                                }

                                for (int i = 0; i < name.size(); i++) {
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    if (i == 0) {
                                        params.topMargin = 50;
                                    }
                                    CheckBox checkBox = new CheckBox(PaymentActivity.this);
                                    checkBox.setId(id.get(i));
                                    checkBox.setText(name.get(i));

                                    //checkBox.setTextColor(Color.BLUE);
                                    checkBox.setTextAppearance(PaymentActivity.this, R.style.CustomActivityTheme);
                                    // checkBox.setTextColor(getResources().getColor(R.color.grey));
                                    checkBox.setTypeface(ResourcesCompat.getFont(PaymentActivity.this, R.font.coustard));
                                    checkBox.setChecked(false);

                                    if (i != 0) {
                                        params.addRule(RelativeLayout.BELOW, id.get(i - 1));
                                    }


                                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                                            if (b) {
                                                int id = checkBox.getId();
                                                giftWrapAmount = giftWrapAmount + giftWrapItemList.get(id - 1).getGiftAmount();
                                                giftWappingItemList.add(giftWrapItemList.get(id - 1).getItemName());
                                                giftText.setText("Gift Wrap");
                                                giftAmount.setText(" ₹" + giftWrapAmount);
                                                if (discountAppliedOrNot.equals("Yes")) {
                                                    toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount - getDiscountAmount));
                                                    totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount - getDiscountAmount));
                                                } else {
                                                    toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount));
                                                    totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount));
                                                }
                                            } else {
                                                int id = checkBox.getId();
                                                giftWrapAmount = giftWrapAmount - giftWrapItemList.get(id - 1).getGiftAmount();
                                                giftWappingItemList.remove(giftWrapItemList.get(id - 1).getItemName());
                                                giftText.setText("Gift Wrap");
                                                giftAmount.setText(" ₹" + giftWrapAmount);
                                                if (discountAppliedOrNot.equals("Yes")) {
                                                    toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount - getDiscountAmount));
                                                    totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount - getDiscountAmount));
                                                } else {
                                                    toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount));
                                                    totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount));
                                                }
                                            }
                                        }
                                    });
                                    checkBox.setLayoutParams(params);
                                    my_layout.addView(checkBox, params);
                                }
                            }
                        }
                        finalBillAmount = finalBillAmount + (itemDetails.getTotalItemQtyPrice());
                        tipsAmountInt = finalBillAmount;
                        itemDetailList.add(itemDetails);
                    }
                    orderDetails.setItemDetailList(itemDetailList);
                    orderDetails.setStoreName(itemDetails.getStoreName());
                    orderDetails.setStorePincode(itemDetails.getStorePincode());
                    orderDetails.setStoreAddress(itemDetails.getStoreAdress());
                    orderDetails.setGiftWrappingItemName(giftWappingItemList);
                    orderDetails.setTotalAmount(Integer.parseInt(String.valueOf(finalBillAmount)));

                    if (itemDetailList != null && itemDetailList.size() > 0) {
                        storeId = itemDetailList.get(0).getSellerId();
                    }
                    if ("".equalsIgnoreCase(tipAmount) || tipAmount == null) {
                        temp = 0;
                    } else {
                        temp = Integer.parseInt(tipAmount);
                    }
                    tipsAmountInt = finalBillAmount + temp;
                }


                PaymentAdapter viewCartAdapter = new PaymentAdapter(PaymentActivity.this, itemDetailList);
                purchaseListView.setAdapter(viewCartAdapter);
                viewCartAdapter.notifyDataSetChanged();

                if (viewCartAdapter != null) {
                    int totalHeight = 0;

                    for (int i = 0; i < viewCartAdapter.getCount(); i++) {
                        View listItem = viewCartAdapter.getView(i, null, purchaseListView);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = purchaseListView.getLayoutParams();
                    params.height = totalHeight + (purchaseListView.getDividerHeight() * (viewCartAdapter.getCount() - 1));
                    purchaseListView.setLayoutParams(params);
                    purchaseListView.requestLayout();
                    purchaseListView.setAdapter(viewCartAdapter);
                    viewCartAdapter.notifyDataSetChanged();


                    totalItemValue.setText(" ₹" + String.valueOf(finalBill));
                    tipValue.setText(" ₹" + String.valueOf(temp));
                    if (discountAppliedOrNot.equals("Yes")) {
                        totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount - getDiscountAmount));
                        toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount - getDiscountAmount));
                    } else {
                        totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount));
                        toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        if (!((Activity) PaymentActivity.this).isFinishing()) {
            pDialog = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#06519A"));
            pDialog.setTitleText("Loading ...");
            pDialog.setCancelable(false);
            if (!((Activity) PaymentActivity.this).isFinishing()) {
                pDialog.show();
            }
        }
        loadFunction();
        if (checkIntent == true) {
            orderDetails.setPaymentId(paymentData.getPaymentId());
            try {
                razorpay = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRAT_KEY);
                payment = razorpay.Payments.fetch(paymentData.getPaymentId());
                jsonObject = new JSONObject(String.valueOf(payment));
                paymentType = jsonObject.getString("method");
                amount = jsonObject.getString("amount");
            } catch (RazorpayException | JSONException e) {

            }
            orderDetails.setPaymentType(paymentType);
            orderDetails.setOrderIdfromPaymentGateway(resultOrderId);
            orderDetails.setOrderStatus("Order Placed");

            orderDetails.setTotalAmount(finalBillAmount);
            if (discountAppliedOrNot.equals("Yes")) {
                orderDetails.setDiscountName(String.valueOf(discountName));
                orderDetails.setDiscountAmount(getDiscountAmount);
            } else {
                orderDetails.setDiscountName("");
                orderDetails.setDiscountAmount(0);
            }
            orderDetails.setAssignedTo("");
            orderDetails.setTipAmount(temp);
            orderDetails.setDeliverOtp(generateOTP());
            orderDetails.setCategoryTypeId("1");
            orderDetails.setFormattedDate(DateUtils.fetchFormatedCurrentDate());
            orderDetails.setPaymentamount(tipsAmountInt + giftWrapAmount - getDiscountAmount);
            // orderDetails.setPaymentDate(paymentDateStr);
            orderDetails.setOrderTime(DateUtils.fetchCurrentTime());
            orderDetails.setOrderCreateDate(DateUtils.fetchCurrentDateAndTime());
            orderDetails.setPaymentDate(DateUtils.fetchCurrentDate());
            // orderDetails.setFormattedDate(formattedDateStr);
            //orderDetails.setOrderTime(orderTimeStr);
            //orderDetails.setOrderCreateDate(orderCreatedDateStr);
            orderDetails.setCustomerName(saved_customer);
            orderDetails.setCustomerId(saved_id);
            orderDetails.setTotalDistanceTraveled(resultKiloMeterRoundOff);
            orderDetails.setDeliveryFee(totaldeliveryFee);
            orderDetails.setTotalFeeForDeliveryBoy(totalFeeFordeliveryBoy);
            orderDetails.setDeliveryType(deliverytType);
            orderDetails.setGiftWrapCharge(giftWrapAmount);
            orderDetails.setStoreAddress(storeAddress);
            orderDetails.setNotificationStatus("false");
            orderDetails.setNotificationStatusForCustomer("false");
            orderDetails.setNotificationStatusForSeller("false");
            orderDetails.setInstructionsToDeliveryBoy(instructionString);
            orderDetails.setDiscountGivenBy(getDiscountGivenBy);
            orderDetails.setSellerLatitude(sharedPreferenceLatitude);
            orderDetails.setSellerLongtitude(sharedPreferenceLongtitude);
            orderDetails.setOrderPlacedTiming(orderCreatedTimeStr);
            orderDetails.setReadyForPickupTiming("");
            orderDetails.setDeliveryIsOnTheWayTiming("");
            orderDetails.setDeliveredTiming("");
            orderDetails.setTimerForCancelDelivery("");

            onTransaction(Orderreference);

            if (getPreference != null && !getPreference.equals("")) {
                if (getPreference.equals("1")) {
                    if (!((Activity) PaymentActivity.this).isFinishing()) {
                        DatabaseReference orderDetailsRef = CommonMethods.fetchFirebaseDatabaseReference("OneTimeDiscount").child(getPreference);
                        orderDetailsRef.child("usedTag").setValue("true");
                    }
                }
            }

        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        testPay = "Failed";
        if (discountAppliedOrNot.equals("Yes")) {
            toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount - getDiscountAmount));
            totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount - getDiscountAmount));
            purchaseLayout.setVisibility(View.VISIBLE);
        } else {
            toPayValue.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount));
            totalPurchaseAmount.setText(" ₹" + String.valueOf(tipsAmountInt + giftWrapAmount));
            purchaseLayout.setVisibility(View.VISIBLE);
        }

        if (!((Activity) PaymentActivity.this).isFinishing()) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Payment cancelled").setContentText("Please try again!").show();
            purchaseLayout.setVisibility(View.VISIBLE);
        }
    }

    public void resultChecking() {
        purchaseLayout.setVisibility(View.VISIBLE);
        if (!CommonMethods.getConnectivityStatusString(getApplicationContext()).equals("No internet is available")) {
            // new backGroundClass1().execute();
            userCurrentLocationDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount() > 0) {
                        userCurrentLocation = dataSnapshot.getValue(CustomerDetails.class);

                        int Radius = 6371;// radius of earth in Km
                        double lat1 = userCurrentLocation.getUserLatitude();
                        double lat2 = sharedPreferenceLatitude;
                        double lon1 = userCurrentLocation.getUserLongtitude();
                        double lon2 = sharedPreferenceLongtitude;
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
                            if (userCurrentLocation.getCurrentPincode() == null || userCurrentLocation.getCurrentPincode().equals("")) {
                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE);
                                sweetAlertDialog.setCancelable(false);
                                sweetAlertDialog.setTitleText("Delivery is not available in this location").show();
                                purchaseLayout.setVisibility(View.VISIBLE);
                            } else if (userCurrentLocation.getCurrentPincode() != null && !userCurrentLocation.getCurrentPincode().equals("") &&
                                    !TextUtils.validPinCode(userCurrentLocation.getCurrentPincode())) {
                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE);
                                sweetAlertDialog.setCancelable(false);
                                sweetAlertDialog.setTitleText("Delivery is not available in this location").show();
                                purchaseLayout.setVisibility(View.VISIBLE);
                            } else if (userCurrentLocation.getCurrentPincode() != null && !userCurrentLocation.getCurrentPincode().equals("") &&
                                    TextUtils.validPinCode(userCurrentLocation.getCurrentPincode())) {
                                if ((saved_id != null && !"".equals(saved_id))) {
                                    if (!((Activity) PaymentActivity.this).isFinishing()) {
                                        purchaseLayout.setVisibility(View.INVISIBLE);
                                        Checkout.preload(getApplicationContext());
                                        new backGroundClass().execute();
                                    }
                                } else {
                                    Toast.makeText(PaymentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PaymentActivity.this, HomePageAcitivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    purchaseLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (!((Activity) PaymentActivity.this).isFinishing()) {
                                    Toast.makeText(PaymentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PaymentActivity.this, HomePageAcitivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    purchaseLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE);
                            sweetAlertDialog.setCancelable(false);
                            sweetAlertDialog.setTitleText("Delivery is not available in this location").show();
                            purchaseLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("No Network Connection")
                    .show();
        }
    }

    private class backGroundClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                startpayment();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (RazorpayException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void loadFunction() {
        userCurrentLocationDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    userCurrentLocation = dataSnapshot.getValue(CustomerDetails.class);
                    if ("2".equalsIgnoreCase(addressPref)) {
                        viewAddress.setText(shippingAddress);
                        orderDetails.setShippingaddress(shippingAddress);
                        orderDetails.setFullName(customerName);
                        orderDetails.setShippingPincode(customerPinCode);
                        orderDetails.setCustomerPhoneNumber(customerMobilenumber);
                    } else if ("1".equalsIgnoreCase(addressPref)) {
                        viewAddress.setText(userCurrentLocation.getCurrentAddress());
                        orderDetails.setFullName(userCurrentLocation.getFullName());
                        orderDetails.setCustomerPhoneNumber(saved_customerPhonenumber);
                        orderDetails.setShippingPincode(userCurrentLocation.getCurrentPincode());
                        orderDetails.setShippingaddress(userCurrentLocation.getCurrentAddress());
                    }
                    checkIntent = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PaymentActivity.this, HomePageAcitivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void checkGPSConnection(Context context) {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS)
            Toast.makeText(context.getApplicationContext(), "GPS is disable!", Toast.LENGTH_LONG).show();
    }

    public void autoLoadFunction() {
        if (typeOfDiscount != null) {
            viewOffers.setText("Remove");
            if (typeOfDiscount.equals("Price")) {
                if ((Integer.parseInt(finalBill) >= getMaximumBillAmount)) {
                    deductionAmountTextView.setText("- ₹" + String.valueOf(getDiscountAmount));
                    offerName.setText(discountName);
                    discountAppliedOrNot = "Yes";

                    final AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this, R.style.CustomAlertDialog);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.customlayout, viewGroup, false);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            alertDialog.dismiss();
                        }
                    }, 2000);
                } else {
                    offerName.setText("Offer is not applicable");
                    discountAppliedOrNot = "No";
                    offerName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_crossline_01, 0, 0, 0);
                }
            } else if (typeOfDiscount.equals("Percent")) {
                if ((Integer.parseInt(finalBill) >= getMaximumBillAmount)) {

                    discountAmountCalculation = (getDiscountAmount * Integer.parseInt(finalBill)) / 100;

                    if (ifPercentMaxAmountForDiscount <= discountAmountCalculation) {
                        getDiscountAmount = discountAmountCalculation;
                        deductionAmountTextView.setText("- ₹" + String.valueOf(getDiscountAmount));
                    } else {
                        getDiscountAmount = ifPercentMaxAmountForDiscount;
                        deductionAmountTextView.setText("- ₹" + String.valueOf(getDiscountAmount));
                    }
                    offerName.setText(discountName);
                    discountAppliedOrNot = "Yes";

                    final AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this, R.style.CustomAlertDialog);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.customlayout, viewGroup, false);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            alertDialog.dismiss();
                        }
                    }, 2000);

                } else {
                    offerName.setText("Offer is not applicable");
                    discountAppliedOrNot = "No";
                    offerName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_crossline_01, 0, 0, 0);
                }
            }
        } else {
            deductionAmountTextView.setText("- ₹" + String.valueOf(getDiscountAmount));
            onStart();
        }

    }

    private void onTransaction(DatabaseReference postRef) {

        postRef.runTransaction(new Transaction.Handler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                increamentId = Math.toIntExact(mutableData.getChildrenCount());
                if (increamentId == null) {
                    orderDetails.setOrderId(String.valueOf(1));
                    mutableData.child(String.valueOf(1)).setValue(orderDetails);
                    return Transaction.success(mutableData);
                } else {
                    increamentId = increamentId + 1;
                    orderDetails.setOrderId(String.valueOf(increamentId));
                    mutableData.child(String.valueOf(increamentId)).setValue(orderDetails);
                    return Transaction.success(mutableData);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                viewCartRef.removeValue();
                Intent intent = new Intent(PaymentActivity.this, ViewOrderActivity.class);
                intent.putExtra("OrderidDetails", String.valueOf(increamentId));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    public static String generateString(int length) {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789!@#$%^&*".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
    public Date loadingFiunction()  {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            TIME_SERVER = "time-a.nist.gov";
            timeClient = new NTPUDPClient();
            timeClient.open();
            timeClient.setSoTimeout(100000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(TIME_SERVER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        TimeInfo timeInfo = null;
        try {
            timeInfo = timeClient.getTime(inetAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (timeInfo != null) {
            long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            Date time = new Date(returnTime);
            return time;
        }
        return  null;
    }

    @Override
    public void onPictureClicked(int position)
    {
        if (userDetailsArrayListNew.size()>0 && userDetailsArrayListNew!=null)
        {
            Intent intent = new Intent ( PaymentActivity.this, ViewItemsActivity.class );
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
            intent.putExtra ( CUSTOMER_ID, getIntent ().getStringExtra ( CUSTOMER_ID ) );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }
    }
}