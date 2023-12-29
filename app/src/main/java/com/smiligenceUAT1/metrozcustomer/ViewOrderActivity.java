package com.smiligenceUAT1.metrozcustomer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterViewFlipper;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.adapter.ItemOrderDetails;
import com.smiligenceUAT1.metrozcustomer.adapter.ViewFlipperAdapter;
import com.smiligenceUAT1.metrozcustomer.bean.AdvertisementDetails;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.bean.OrderDetails;
import com.smiligenceUAT1.metrozcustomer.bean.UserDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.DateUtils;
import com.smiligenceUAT1.metrozcustomer.common.Interface;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.CUSTOMER_ID;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.DATE_FORMAT;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.ORDER_SUCESS_PAGE_SECONDARY;

public class ViewOrderActivity extends AppCompatActivity implements Interface.ClickedListener {
    TextView order_Date, order_Id, order_Total, type_Of_Payment, fullName, shipping_Address, amount, shipping, giftPrice, offerName, deductionAmount, wholeCharge, order_status,
            customerPhoneNumber, storeNameText, orderTimeTxt, deliverPinTxt, noContactDelivery, anyInstructions,clearCart;
    BottomNavigationView bottomNavigation;
    ConstraintLayout orderDetailsLayout, paymentDetailsLayout, shippingAddressLayout, orderSummaryLayout, specialinstructionLayout;
    RelativeLayout itemDetailsLayout, itemHeaderlayout,repeatOrders;
    DatabaseReference databaseReference,viewcartRef;
    private ArrayList<ItemDetails> openTicketItemList = new ArrayList<>();
    ArrayList<String> giftItemList = new ArrayList<String>();
    String orderIdForItemRatings;

    ItemOrderDetails itemOrderDetails;
    ListView listView;
    static String getOrderIdValue,getType;
    boolean checkNotification = true;
    boolean notify = false;
    OrderDetails orderDetailsNotification;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    Animation animation;
    Ringtone r;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetailsList1=new ArrayList<>();

    ArrayList<String> itemIdList=new ArrayList<>();
    ArrayList<ItemDetails> ItemDetailsNew=new ArrayList<>();
    private static final long START_TIME_IN_MILLIS = 61000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private long remainingTimeInMillis;

    boolean checking=true;
    ItemDetails itemDetails;
    CardView adcard1;
    AdapterViewFlipper adapterViewFlipper2;
    Query advertisementBannerQuery;
    DatabaseReference advertisementref;
    ArrayList<AdvertisementDetails> advertisementDetailsObjectList = new ArrayList<>();
    ArrayList<AdvertisementDetails> advertisementDetailsObjectList1 = new ArrayList<>();
    ArrayList<String> advertisementPriorOne = new ArrayList<>();
    ArrayList<String> dateStringList=new ArrayList<>();
    ArrayList<UserDetails> userDetailsArrayList=new ArrayList<UserDetails>();
    ArrayList<ArrayList<UserDetails>> userDetailsArrayListNew=new ArrayList<ArrayList<UserDetails>>();
    AdvertisementDetails advertisementDetails;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat ( pattern );
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String metrozStartTime ;
    String metrozStopTime;
    public static  double roundOff;
    String saved_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        final SharedPreferences loginSharedPreferences1 = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences1.getString ( "customerId", "" );

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        databaseReference = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
        viewcartRef = CommonMethods.fetchFirebaseDatabaseReference("ViewCart").child(saved_id);
        loadFunction();

        itemDetailsLayout = findViewById(R.id.itemdetailslayout);
        itemHeaderlayout = findViewById(R.id.itemdetailslayoutheader);
        bottomNavigation = findViewById(R.id.drawerLayout);
        bottomNavigation.setSelectedItemId(R.id.order);
        animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        checkGPSConnection(getApplicationContext());

        //Order details value
        orderDetailsLayout = findViewById(R.id.order_details_layout);
        order_Date = orderDetailsLayout.findViewById(R.id.orderdate);
        order_Id = orderDetailsLayout.findViewById(R.id.bill_num);
        order_Total = orderDetailsLayout.findViewById(R.id.order_total);
        order_status = orderDetailsLayout.findViewById(R.id.order_status);
        orderTimeTxt = orderDetailsLayout.findViewById(R.id.ordertimetxt);
        deliverPinTxt = orderDetailsLayout.findViewById(R.id.deliver_pin);

        //Payment details
        paymentDetailsLayout = findViewById(R.id.payment_details);
        //card_Details = paymentDetailsLayout.findViewById ( R.id.card_details );
        type_Of_Payment = paymentDetailsLayout.findViewById(R.id.type_of_payment);

        //Shipping Address Details
        shippingAddressLayout = findViewById(R.id.shipping_details_layout);
        fullName = shippingAddressLayout.findViewById(R.id.full_name);
        shipping_Address = shippingAddressLayout.findViewById(R.id.address);
        customerPhoneNumber = shippingAddressLayout.findViewById(R.id.phoneNumber);

        //Order summary
        orderSummaryLayout = findViewById(R.id.cart_total_amount_layout);
        amount = orderSummaryLayout.findViewById(R.id.tips_price1);
        shipping = orderSummaryLayout.findViewById(R.id.tips_price);
        wholeCharge = orderSummaryLayout.findViewById(R.id.total_price);
        giftPrice = orderSummaryLayout.findViewById(R.id.gift_price);
        offerName = orderSummaryLayout.findViewById(R.id.offerNameTextViewResult);
        deductionAmount = orderSummaryLayout.findViewById(R.id.deductionAmountTextViewResult);

        adcard1=findViewById(R.id.adcard1);
        adapterViewFlipper2=findViewById(R.id.diwalibanners1);
        advertisementref = CommonMethods.fetchFirebaseDatabaseReference ( "Advertisements" );
        Calendar cal = Calendar.getInstance ();
        currentLocalTime = cal.getTime ();
        date = new SimpleDateFormat ( "HH:mm aa" );
        DateFormat dateFormat = new SimpleDateFormat ( DATE_FORMAT );
        currentDateAndTime = dateFormat.format ( new Date () );
        currentTime = date.format ( currentLocalTime );

        //Special instruction layout

        specialinstructionLayout = findViewById(R.id.special_instructions);

        noContactDelivery = findViewById(R.id.noContactDelivery);
        anyInstructions = findViewById(R.id.specialinsructions);


        //ItemDetails
        listView = itemDetailsLayout.findViewById(R.id.itemDetailslist);

        //ItemHeaderlayout
        storeNameText = itemHeaderlayout.findViewById(R.id.storeName);


        getOrderIdValue = getIntent().getStringExtra("OrderidDetails");
        getType=getIntent().getStringExtra("type");


        advertisementBannerQuery = advertisementref.orderByChild("advertisementPlacing").equalTo(ORDER_SUCESS_PAGE_SECONDARY);
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
                        System.out.println("SADSDFDFTGTY"+userDetailsArrayListNew.size());
                        if(advertisementDetails.getAdvertisementPlacing().equals(ORDER_SUCESS_PAGE_SECONDARY))
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

                            adcard1.setVisibility(View.VISIBLE);
                            adapterViewFlipper2.setAdapter(new ViewFlipperAdapter(ViewOrderActivity.this, advertisementDetailsObjectList,ViewOrderActivity.this::onPictureClicked));
                            adapterViewFlipper2.setFlipInterval(4000);
                            adapterViewFlipper2.startFlipping();
                            adapterViewFlipper2.setAutoStart(true);
                            adapterViewFlipper2.setAnimateFirstView(false);
                        }
                        else {
                            adcard1.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        final Query getitemDetailsQuery = databaseReference.orderByChild("orderId").equalTo(String.valueOf(getOrderIdValue));


        getitemDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postdataSnapshot : dataSnapshot.getChildren()) {

                    OrderDetails openTickets = postdataSnapshot.getValue(OrderDetails.class);
                    openTicketItemList = (ArrayList<ItemDetails>) openTickets.getItemDetailList();

                    giftItemList = openTickets.getGiftWrappingItemName();
                    orderIdForItemRatings=openTickets.getOrderId();
                    itemOrderDetails = new ItemOrderDetails(ViewOrderActivity.this, openTicketItemList, giftItemList,orderIdForItemRatings);
                    itemOrderDetails.notifyDataSetChanged();
                    listView.setAdapter(itemOrderDetails);

                    if (listView != null) {
                        int totalHeight = 0;
                        for (int i = 0; i < itemOrderDetails.getCount(); i++)
                        {
                            View listItem = itemOrderDetails.getView(i, null, listView);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }
                        ViewGroup.LayoutParams params = listView.getLayoutParams();
                        params.height = totalHeight + (listView.getDividerHeight() * (listView.getCount() - 1));
                        listView.setLayoutParams(params);
                        listView.requestLayout();
                        listView.setAdapter(itemOrderDetails);
                        itemOrderDetails.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final Query getOrderDetails = databaseReference.orderByChild("orderId").equalTo(getOrderIdValue);


        getOrderDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        orderDetails = dataSnapshot1.getValue(OrderDetails.class);
                        itemDetailsList1=(ArrayList<ItemDetails>)orderDetails.getItemDetailList();
                        order_Date.setText(orderDetails.getPaymentDate());

                        order_Id.setText("#" + orderDetails.getOrderId());
                        order_Total.setText(" ₹" + String.valueOf(orderDetails.getPaymentamount()));
                        order_status.setText(orderDetails.getOrderStatus());
                        storeNameText.setText(orderDetails.getStoreName());
                        orderTimeTxt.setText(orderDetails.getOrderTime());
                        deliverPinTxt.setText(orderDetails.getDeliverOtp());
                        type_Of_Payment.setText(orderDetails.getPaymentType());

                        fullName.setText(" " + orderDetails.getFullName());
                        shipping_Address.setText(" " + orderDetails.getShippingaddress());
                        customerPhoneNumber.setText(" " + orderDetails.getCustomerPhoneNumber());
                        fullName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cus, 0, 0, 0);
                        shipping_Address.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_01, 0, 0, 0);
                        customerPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phonenumicon_01, 0, 0, 0);

                        amount.setText(" ₹" + String.valueOf(orderDetails.getTotalAmount()));
                        shipping.setText(" ₹" + String.valueOf(orderDetails.getTipAmount()));
                        wholeCharge.setText(" ₹" + String.valueOf(orderDetails.getPaymentamount()));
                        giftPrice.setText(" ₹" + String.valueOf(orderDetails.getGiftWrapCharge()));
                        offerName.setText(orderDetails.getDiscountName());
                        deductionAmount.setText(" - ₹" + String.valueOf(orderDetails.getDiscountAmount()));


                        if (orderDetails.getInstructionsToDeliveryBoy() != null && !"".equalsIgnoreCase(orderDetails.getInstructionsToDeliveryBoy())) {
                            anyInstructions.setText(orderDetails.getInstructionsToDeliveryBoy());
                        }
                        if (orderDetails.getDeliveryType() != null && !"".equals(orderDetails.getDeliveryType())) {
                            if (orderDetails.getDeliveryType().equalsIgnoreCase("No Contact Delivery")) {
                                //noContactDelivery.setText ( "No Contact Delivery" );
                                setCompulsoryAsterisk();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });





    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {

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

        return true;
    }


    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ViewOrderActivity.this, HomePageAcitivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void loadFunction() {
        getOrderIdValue = getIntent().getStringExtra("OrderidDetails");
        databaseReference = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);

        Query placedOrderQuery = databaseReference.orderByChild("orderId").equalTo(String.valueOf(getOrderIdValue));

        placedOrderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notify = true;
                if (checkNotification) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot detailsSnap : dataSnapshot.getChildren()) {
                            orderDetailsNotification = detailsSnap.getValue(OrderDetails.class);
                        }
                        if (orderDetailsNotification.getOrderStatus().equals("Order Placed")) {

                            //   if (sellerIdIntent.equals(itemDetails.get(0).getSellerId())) {
                            if ("false".equalsIgnoreCase(orderDetailsNotification.getNotificationStatusForCustomer())) {
                                if (notify) {
                                    if (!((Activity) ViewOrderActivity.this).isFinishing()) {
                                        DatabaseReference orderDetailsRef = CommonMethods.fetchFirebaseDatabaseReference("OrderDetails").child(orderDetailsNotification.getOrderId());
                                        orderDetailsRef.child("notificationStatusForCustomer").setValue("true");
                                    }
                                    notify = false;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new weatherTask().execute(orderDetailsNotification.getOrderId());
                                        }
                                    }, 3000);
                                }
                            }
                            // }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class weatherTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String orderId = strings[0];

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotification("Order Id #" + orderDetailsNotification.getOrderId() + " is Placed Successfully", orderDetailsNotification.getOrderId());
                notify = false;
            }
            return null;
        }
    }

    public void createNotification(String res, String orderId) {
        int count = 0;


        if (count == 0) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ViewOrderActivity.this, default_notification_channel_id)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("New Order")
                    .setContentText(res)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));


            Intent secondActivityIntent = new Intent(this, ViewOrderActivity.class);
            secondActivityIntent.putExtra("OrderidDetails", getOrderIdValue);
            secondActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            PendingIntent secondActivityPendingIntent = PendingIntent.getActivity(this, 0, secondActivityIntent, PendingIntent.FLAG_ONE_SHOT);
            getOrderIdValue = getIntent().getStringExtra("OrderidDetails");
            mBuilder.addAction(R.mipmap.ic_launcher, "View", secondActivityPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                try {
                    Uri path = Uri.parse("android.resource://com.smiligence.metrozcustomer/" + R.raw.old_telephone_tone);
                    r = RingtoneManager.getRingtone(getApplicationContext(), path);
                    r.play();
                } catch (Exception e)
                {

                }

                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Integer.parseInt(orderId), notification);
            count = count + 1;
        }
    }

    public void setCompulsoryAsterisk() {
        String txt_name = "No Contact Delivery";
        String colored = "*";
        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        strBuilder.append(txt_name);
        int start = strBuilder.length();
        strBuilder.append(colored);
        int end = strBuilder.length();
        strBuilder.setSpan(
                new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        noContactDelivery.setText(strBuilder);
    }





    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", System.currentTimeMillis());
        editor.apply();

    }

    @Override
    public void onPictureClicked(int position) {


        if (userDetailsArrayListNew.size()>0 && userDetailsArrayListNew!=null) {
            Intent intent = new Intent ( ViewOrderActivity.this, ViewItemsActivity.class );
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