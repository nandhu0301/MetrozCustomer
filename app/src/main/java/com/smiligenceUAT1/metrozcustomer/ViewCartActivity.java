package com.smiligenceUAT1.metrozcustomer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.github.infinitebanner.InfiniteBannerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.smiligenceUAT1.metrozcustomer.adapter.BannerAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.ViewCartAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.ViewFlipperAdapter;
import com.smiligenceUAT1.metrozcustomer.bean.AdvertisementDetails;
import com.smiligenceUAT1.metrozcustomer.bean.CustomerDetails;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.bean.OrderDetails;
import com.smiligenceUAT1.metrozcustomer.bean.Tip;
import com.smiligenceUAT1.metrozcustomer.bean.UserDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Constant;
import com.smiligenceUAT1.metrozcustomer.common.DateUtils;
import com.smiligenceUAT1.metrozcustomer.common.Interface;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.CUSTOMER_ID;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.VIEW_CART_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.VIEW_CART_SECONDARY;

public class ViewCartActivity extends AppCompatActivity implements Interface.ClickedListener {

    ListView viewCartListView;
    DatabaseReference viewcartRef, tipsDataRef, userCurrentLocationDetails, storeTimingDataRef, metrozStoteTimingDataRef, distanceFeeDataRef, Orderreference;
    boolean checkIntent = false;
    Chip chip;
    double getStoreLatitude = 0.0, getStoreLongtitude = 0.0, roundOff = 0.0;
    int resultKiloMeterRoundOff = 0, totaldeliveryFee = 0, tipsAmountInt = 0, temp = 0, perKmDistanceFee, finalBillAmount;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<ItemDetails>();
    TextView storeNameText, finalBillTextView, freeDeliverytext, addmoreText, tipsAmount, addCurrentAddressEditText;
    RelativeLayout addAddressButton, proceedWithCurrentAddress;
    ItemDetails itemDetails;
    ImageView back_button, clearCart;
    boolean check = false;
    RazorpayClient razorpay;
    Payment payment;
    CheckBox nocontactDelivery;
    SweetAlertDialog pDialog;
    private BroadcastReceiver MyReceiver = new MyReceiver();
    EditText instructionText;
    OrderDetails orderDetails = new OrderDetails();
    long maxid = 0;
    JSONObject jsonObject;
    String resultOrderId, paymentId, paymentType, amount, storeAddress, deliveryType,
            receiptNumber, storeNameFromAdapter, storeId, storeIdTxt, categoryName, categoryId, pinCode;
    CustomerDetails userCurrentLocation;
    boolean customerCheck = true;
    double sharedPreferenceLatitude = 0.0, sharedPreferenceLongtitude = 0.0;
    String sharedPreferenceLat, sharedPreferenceLong;

    int getDistance;
    int storeIdFromList;

    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    TextView viewOffers, SelectDiscount;
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
    String saved_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_view_cart);
        checkGPSConnection(getApplicationContext());
        final SharedPreferences loginSharedPreferences2 = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences2.getString ( "customerId", "" );
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        viewCartListView = findViewById(R.id.viewcart);
        addAddressButton = findViewById(R.id.adddresButton);
        storeNameText = findViewById(R.id.storeNameTextview);
        finalBillTextView = findViewById(R.id.itemTotaltxt);
        freeDeliverytext = findViewById(R.id.partnerfee);
        back_button = findViewById(R.id.back_buttontohome);
        clearCart = findViewById(R.id.clearcart);
        addmoreText = findViewById(R.id.addmoreText);
        nocontactDelivery = findViewById(R.id.checkBox);
        tipsAmount = findViewById(R.id.tipamount);
        instructionText = findViewById(R.id.instrctionEdt);
        addCurrentAddressEditText = findViewById(R.id.addanaddresstxt);
        proceedWithCurrentAddress = findViewById(R.id.currentAddressPayment);
        viewOffers = findViewById(R.id.viewOffers);
        SelectDiscount = findViewById(R.id.discountTextview);

        storeNameFromAdapter = getIntent().getStringExtra("StoreName");
        storeId = getIntent().getStringExtra("StoreId");
        categoryName = getIntent().getStringExtra("categoryName");
        categoryId = getIntent().getStringExtra("categoryId");
        pinCode = getIntent().getStringExtra("pinCode");


        cardView1 = findViewById(R.id.adcard1);
        adapterViewFlipper1 = findViewById(R.id.diwalibanners1);
        Calendar cal = Calendar.getInstance();
        currentLocalTime = cal.getTime();


        date = new SimpleDateFormat("HH:mm aa");


        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT);
        currentDateAndTime = dateFormat.format(new Date());
        currentTime = date.format(currentLocalTime);

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

        final SharedPreferences loginSharedPreferences1 = getSharedPreferences("DISTANCE", MODE_PRIVATE);
        getDistance = loginSharedPreferences1.getInt("Distance", 0);


        Orderreference = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
        userCurrentLocationDetails = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails").child(String.valueOf(saved_id));

        viewcartRef = CommonMethods.fetchFirebaseDatabaseReference("ViewCart").child(saved_id);
        distanceFeeDataRef = CommonMethods.fetchFirebaseDatabaseReference("DeliveryCharges");
        storeTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance");
        metrozStoteTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference("MetrozstoreTiming");
        advertisementref = CommonMethods.fetchFirebaseDatabaseReference("Advertisements");


        loadFunction();


        advertisementBannerQuery = advertisementref.orderByChild("advertisementPlacing").equalTo(VIEW_CART_SECONDARY);
        advertisementBannerQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0)
                {
                    advertisementDetailsObjectList1.clear();
                    advertisementPriorOne.clear();
                    dateStringList.clear();
                    userDetailsArrayList.clear();
                    userDetailsArrayListNew.clear();
                    for (DataSnapshot adSnap:snapshot.getChildren())
                    {
                        advertisementDetails = adSnap.getValue(AdvertisementDetails.class);
                        if(advertisementDetails.getAdvertisementPlacing().equals(VIEW_CART_SECONDARY))
                        {
                            if (!(advertisementDetails.getAdvertisingAgent().equals("Instruction Ad"))) {

                                System.out.println("SADSDFDFTGTY"+userDetailsArrayListNew.size());
                                if(advertisementDetails.getAdvertisementPlacing().equals(VIEW_CART_SECONDARY))
                                {
                                    if (advertisementDetails.getAdvertisingDuration().equals("One Hour"))
                                    {
                                        if (advertisementDetails.getAdvertisementEndingDurationDate()!=null) {
                                            if (advertisementDetails.getAdvertisementEndingDurationDate().equals(DateUtils.fetchFormatedCurrentDate())) {
                                                try {
                                                    if ((sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration())) == -1) ||
                                                            (sdf.parse(currentTime).compareTo(sdf.parse(advertisementDetails.getAdvertisementExpiringDuration())) == 0)) {
                                                        advertisementDetailsObjectList1.add(advertisementDetails);
                                                        userDetailsArrayList = (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                        userDetailsArrayListNew.add(userDetailsArrayList);
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
                                                    userDetailsArrayList= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew.add(userDetailsArrayList);
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
                                                    userDetailsArrayList= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew.add(userDetailsArrayList);
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
                                                    userDetailsArrayList= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew.add(userDetailsArrayList);
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
                                                    userDetailsArrayList= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                    userDetailsArrayListNew.add(userDetailsArrayList);
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
                                                        userDetailsArrayList= (ArrayList<UserDetails>) advertisementDetails.getAdvertisingStoreList();
                                                        userDetailsArrayListNew.add(userDetailsArrayList);
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
                                    userDetailsArrayListNew.add(userDetailsArrayList1);
                                }
                            }
                        }

                        if (advertisementDetailsObjectList1.size() > 0) {
                            cardView1.setVisibility(View.VISIBLE);
                            adapterViewFlipper1.setAdapter(new ViewFlipperAdapter(ViewCartActivity.this, advertisementDetailsObjectList1,ViewCartActivity.this::onPictureClicked));

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
        distanceFeeDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    perKmDistanceFee = Integer.parseInt(String.valueOf(dataSnapshot.child("DISTANCE_FEE_PER_KM").getValue()));

                    int Radius = 6371;// radius of earth in Km

                    double lat1 = HomePageAcitivity.saved_Userlatitude;
                    double lat2 = getStoreLatitude;

                    double lon1 = HomePageAcitivity.saved_Userlongtitude;
                    double lon2 = getStoreLongtitude;

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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        viewcartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot storeSnap : dataSnapshot.getChildren()) {
                        itemDetails = storeSnap.getValue(ItemDetails.class);

                    }
                    storeNameText.setText(itemDetails.getStoreName());
                    storeAddress = itemDetails.getStoreAdress();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        onStart();
        tipsDataRef = CommonMethods.fetchFirebaseDatabaseReference("Tips");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final ChipGroup chipGroup = findViewById(R.id.tips);
        chipGroup.setSingleSelection(true);
        tipsDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot tipsSnap : dataSnapshot.getChildren()) {
                        Tip tip = tipsSnap.getValue(Tip.class);

                        chip = new Chip(ViewCartActivity.this);
                        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));
                        chip.setText("₹ " + String.valueOf(tip.getTipsAmount()));
                        //chip.setId(Integer.parseInt(tip.getTipsName()));
                        chipGroup.addView(chip);


                        chip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (temp == tip.getTipsAmount()) {
                                    temp = 0;
                                    tipsAmountInt = finalBillAmount - temp;
                                } else {
                                    temp = tip.getTipsAmount();
                                    tipsAmountInt = finalBillAmount + temp;
                                }
                                tipsAmount.setText(" ₹" + String.valueOf(temp));
                                freeDeliverytext.setText(" ₹" + String.valueOf(tipsAmountInt));
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        clearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemDetailList != null && !itemDetailList.isEmpty()) {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewCartActivity.this);
                    bottomSheetDialog.setContentView(R.layout.clearcart_confirmation_dialog_viewcart);
                    Button clearCart = bottomSheetDialog.findViewById(R.id.clear_cart_buttondialog);
                    Button cancel = bottomSheetDialog.findViewById(R.id.cancel_buttondialog);
                    if(!isFinishing())
                    {
                        bottomSheetDialog.show();
                        bottomSheetDialog.setCancelable(false);
                    }
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                        }
                    });
                    clearCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!((Activity) ViewCartActivity.this).isFinishing()) {
                                viewcartRef.removeValue();
                                Intent intent = new Intent(ViewCartActivity.this, ViewCartActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

        addmoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storeNameFromAdapter == null) {
                    Intent intent = new Intent(ViewCartActivity.this, HomePageAcitivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (!(storeNameFromAdapter == null)) {
                    Intent intent = new Intent(ViewCartActivity.this, ViewItemsActivity.class);
                    intent.putExtra("StoreName", storeNameFromAdapter);
                    intent.putExtra("StoreId", storeId);
                    intent.putExtra("categoryName", categoryName);
                    intent.putExtra("categoryId", categoryId);
                    intent.putExtra("pinCode", pinCode);
                    intent.putExtra("storeLatitude", getStoreLatitude);
                    intent.putExtra("storeLongitude", getStoreLongtitude);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCartActivity.this, HomePageAcitivity.class);
                intent.putExtra("StoreName", storeNameFromAdapter);
                intent.putExtra("StoreId", storeId);
                intent.putExtra("categoryName", categoryName);
                intent.putExtra("categoryId", categoryId);
                intent.putExtra("pinCode", pinCode);
                intent.putExtra("storeLatitude", getStoreLatitude);
                intent.putExtra("storeLongitude", getStoreLongtitude);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        proceedWithCurrentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemDetailList.size() == 0) {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(ViewCartActivity.this, SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.setTitleText("No items available in the cart").show();
                } else {
                    if (nocontactDelivery.isChecked()) {
                        deliveryType = "No Contact Delivery";
                    } else {
                        deliveryType = "Contact Delivery";
                    }

                    String instructionString = instructionText.getText().toString();
                    Intent intent = new Intent(ViewCartActivity.this, PaymentActivity.class);
                    intent.putExtra("finalBillAmount", String.valueOf(finalBillAmount));
                    intent.putExtra("deliveryType", deliveryType);
                    intent.putExtra("tips", String.valueOf(temp));
                    intent.putExtra("finalBillTip", String.valueOf(tipsAmountInt));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("storeLatitude", getStoreLatitude);
                    intent.putExtra("storeLongitude", getStoreLongtitude);
                    intent.putExtra("addressPref", "1");
                    intent.putExtra("shippingAddress", "");
                    intent.putExtra("shippingAddress", "");
                    intent.putExtra("customerName", "");
                    intent.putExtra("customerMobilenumber", "");
                    intent.putExtra("customerPinCode", "");

                    intent.putExtra("StoreId", storeId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        });
        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemDetailList.size() == 0) {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(ViewCartActivity.this, SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.setTitleText("No items available in the cart").show();
                } else {

                    if (nocontactDelivery.isChecked()) {
                        deliveryType = "No Contact Delivery";
                    } else {
                        deliveryType = "Contact Delivery";
                    }

                    String instructionString = instructionText.getText().toString();
                    Intent intent = new Intent(ViewCartActivity.this, AddAddressActivity.class);
                    intent.putExtra("finalBillAmount", String.valueOf(finalBillAmount));
                    intent.putExtra("deliveryType", deliveryType);
                    intent.putExtra("tips", String.valueOf(temp));
                    intent.putExtra("finalBillTip", String.valueOf(tipsAmountInt));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("storeLatitude", getStoreLatitude);
                    intent.putExtra("storeLongitude", getStoreLongtitude);
                    intent.putExtra("StoreId", storeId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewCartActivity.this, HomePageAcitivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

        viewcartRef = CommonMethods.fetchFirebaseDatabaseReference(VIEW_CART_FIREBASE_TABLE).
                child(saved_id);
        viewcartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    itemDetailList.clear();
                    finalBillAmount = 0;
                    tipsAmountInt = 0;
                    for (DataSnapshot viewCartsnap : dataSnapshot.getChildren()) {
                        itemDetails = viewCartsnap.getValue(ItemDetails.class);
                        finalBillAmount = finalBillAmount + (itemDetails.getTotalItemQtyPrice());
                        tipsAmountInt = finalBillAmount;
                        itemDetailList.add(itemDetails);
                    }
                    orderDetails.setItemDetailList(itemDetailList);
                    orderDetails.setStoreName(itemDetails.getStoreName());
                    orderDetails.setStorePincode(itemDetails.getStorePincode());
                    orderDetails.setStoreAddress(itemDetails.getStoreAdress());

                    orderDetails.setTotalAmount(Integer.parseInt(String.valueOf(finalBillAmount)));
                    if (itemDetailList != null && itemDetailList.size() > 0) {
                        storeIdTxt = itemDetailList.get(0).getSellerId();
                    }

                    tipsAmountInt = finalBillAmount + temp;
                }

                ViewCartAdapter viewCartAdapter = new ViewCartAdapter(ViewCartActivity.this, itemDetailList);
                viewCartListView.setAdapter(viewCartAdapter);
                viewCartAdapter.notifyDataSetChanged();

                if (viewCartAdapter != null) {
                    int totalHeight = 0;

                    for (int i = 0; i < viewCartAdapter.getCount(); i++) {
                        View listItem = viewCartAdapter.getView(i, null, viewCartListView);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = viewCartListView.getLayoutParams();
                    params.height = totalHeight + (viewCartListView.getDividerHeight() * (viewCartAdapter.getCount() - 1));
                    viewCartListView.setLayoutParams(params);
                    viewCartListView.requestLayout();
                    viewCartListView.setAdapter(viewCartAdapter);
                    viewCartAdapter.notifyDataSetChanged();
                    finalBillTextView.setText(" ₹" + String.valueOf(finalBillAmount));
                    freeDeliverytext.setText(" ₹" + String.valueOf(tipsAmountInt));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void loadFunction() {
        userCurrentLocationDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    userCurrentLocation = dataSnapshot.getValue(CustomerDetails.class);
                    orderDetails.setShippingPincode(userCurrentLocation.getCurrentPincode());
                    orderDetails.setFullName(userCurrentLocation.getFullName());
                    orderDetails.setShippingaddress(userCurrentLocation.getCurrentAddress());
                    addCurrentAddressEditText.setText(userCurrentLocation.getCurrentAddress());
                    checkIntent = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!statusOfGPS)
            Toast.makeText(context.getApplicationContext(), "GPS is disable!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPictureClicked(int position) {

        if (userDetailsArrayListNew.size() > 0 && userDetailsArrayListNew != null) {
            Intent intent = new Intent(ViewCartActivity.this, ViewItemsActivity.class);
            intent.putExtra("StoreId", userDetailsArrayListNew.get(position).get(0).getUserId());
            intent.putExtra("StoreName", userDetailsArrayListNew.get(position).get(0).getStoreName());
            intent.putExtra("storeLatitude", userDetailsArrayListNew.get(position).get(0).getStoreLatitude());
            intent.putExtra("storeLongitude", userDetailsArrayListNew.get(position).get(0).getStoreLongtide());


            int Radius = 6371;// radius of earth in Km
            double lat1 = HomePageAcitivity.saved_Userlatitude;
            double lat2 = userDetailsArrayListNew.get(position).get(0).getStoreLatitude();
            double lon1 = HomePageAcitivity.saved_Userlongtitude;
            double lon2 = userDetailsArrayListNew.get(position).get(0).getStoreLongtide();
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
            editor.putString("STORELATITUDE", String.valueOf(userDetailsArrayListNew.get(position).get(0).getStoreLatitude()));
            editor.putString("STORELONGTITUDE", String.valueOf(userDetailsArrayListNew.get(position).get(0).getStoreLongtide()));
            editor.putString("STOREDISTANCE", String.valueOf(roundOff));
            editor.putString("STOREID", String.valueOf(userDetailsArrayListNew.get(position).get(0).getUserId()));
            editor.putString("STORENAME", String.valueOf(userDetailsArrayListNew.get(position).get(0).getStoreName()));
            editor.putString("STOREADDRESS", String.valueOf(userDetailsArrayListNew.get(position).get(0).getAddress()));
            editor.putString("FSSAICERTIFICATE", String.valueOf(userDetailsArrayListNew.get(position).get(0).getFssaiNumber()));
            editor.commit();
            intent.putExtra(CUSTOMER_ID, getIntent().getStringExtra(CUSTOMER_ID));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}