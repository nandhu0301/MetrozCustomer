package com.smiligenceUAT1.metrozcustomer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.bean.MetrozStoreTime;
import com.smiligenceUAT1.metrozcustomer.bean.OrderDetails;
import com.smiligenceUAT1.metrozcustomer.bean.ShippingAddress;
import com.smiligenceUAT1.metrozcustomer.bean.StoreTimings;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Constant;
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

import static com.smiligenceUAT1.metrozcustomer.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.VIEW_CART_FIREBASE_TABLE;

public class AddAddressActivity extends AppCompatActivity  {
    DatabaseReference shippindAddressDataRef, distanceFeeDataRef,storeTimingDataRef,metrozStoteTimingDataRef,Orderreference, viewcartRef;
    boolean checkIntent = false;
    int perKmDistanceFee,resultKiloMeterRoundOff = 0,totaldeliveryFee = 0;
    double roundOff = 0.0,getStoreLatitude, getStoreLongtitude;
    TextView addNewAddress, firstAddressName, secondAddresName, thirdAddressName, fourthAddressName, firstAddressPhoneNumber, secondAddressPhoneNumber, thirdAddressPhoneNumber, fourthAddressPhoneNumber;
    RadioButton firstAddressRb, secondAddressRb, thirdAddressRb, fourthAddressRb;
    ImageView back;

    RelativeLayout firstRelativeLayout, secondRelelativeLayout, thirdRelativeLayout, fourthRelativeLayout;
    RazorpayClient razorpay;
    Button paymentBtn;
    ShippingAddress shippingAddress;
    ArrayList<ShippingAddress> shippingAddressArrayList = new ArrayList<> ();
    long maxid = 0;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<> ();
    JSONObject jsonObject;
    String resultOrderId,storeAddress,instructionString, paymentType, amount,deliverytType,receiptNumber,tipAmount, finalBill_tip, finalBill;
    ItemDetails itemDetails;
    OrderDetails orderDetails = new OrderDetails ();
    Payment payment;
    private BroadcastReceiver MyReceiver = new MyReceiver ();
    int getCheckValue,getDistance;

    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat ( pattern );
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String metrozStartTime;
    String metrozStopTime;
    String storeIdTxt;
    boolean checkAsync=true;
    double sharedPreferenceLatitude=0.0,sharedPreferenceLongtitude=0.0;
    String sharedPreferenceLat,sharedPreferenceLong;
    int counter;
    TextView addressTypetext,addressTypeTwoText,addressTypeThreetext,addressTypeFourText;
    String selectedChidValue;
    String saved_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        Utils.getDatabase ();
        setContentView ( R.layout.activity_add_address );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        back = findViewById ( R.id.back );
        addressTypetext=findViewById(R.id.addresstypetext);
        addressTypeTwoText=findViewById(R.id.addresstypetwotext);
        addressTypeThreetext=findViewById(R.id.addresstypethreetext);
        addressTypeFourText=findViewById(R.id.addresstypefourtext);
        final SharedPreferences loginSharedPreferences2 = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences2.getString ( "customerId", "" );

        instructionString = getIntent ().getStringExtra ( "instructionString" );
        deliverytType = getIntent ().getStringExtra ( "deliveryType" );
        tipAmount = getIntent ().getStringExtra ( "tips" );
        finalBill_tip = getIntent ().getStringExtra ( "finalBillTip" );
        finalBill = getIntent ().getStringExtra ( "finalBillAmount" );
        storeAddress = getIntent ().getStringExtra ( "storeAddress" );
        getStoreLatitude = getIntent ().getDoubleExtra ( "storeLatitude", 0.0 );
        getStoreLongtitude = getIntent ().getDoubleExtra ( "storeLongitude", 0.0 );
        getCheckValue=getIntent().getIntExtra("childCountValue",0);


        final SharedPreferences loginSharedPreferences = getSharedPreferences ( "SAVE", MODE_PRIVATE );

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

        final SharedPreferences loginSharedPreferences1 = getSharedPreferences ( "DISTANCE", MODE_PRIVATE );
        getDistance= loginSharedPreferences1.getInt ( "Distance", 0 );


        Calendar cal = Calendar.getInstance ();
        currentLocalTime = cal.getTime ();

        date = new SimpleDateFormat ( "HH:mm aa" );


        DateFormat dateFormat = new SimpleDateFormat (Constant.DATE_FORMAT);
        currentDateAndTime = dateFormat.format ( new Date () );
        currentTime = date.format ( currentLocalTime );

        registerReceiver ( MyReceiver, new IntentFilter ( ConnectivityManager.CONNECTIVITY_ACTION ) );

        back.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( AddAddressActivity.this, ViewCartActivity.class );
                intent.putExtra ( "deliveryType", deliverytType );
                intent.putExtra ( "tips", String.valueOf ( tipAmount ) );
                intent.putExtra ( "finalBillTip", String.valueOf ( finalBill_tip ) );
                intent.putExtra ( "finalBillAmount", String.valueOf ( finalBill ) );
                intent.putExtra ( "instructionString", instructionString );
                intent.putExtra ( "storeAddress", storeAddress );
                intent.putExtra ( "storeLatitude", getStoreLatitude );
                intent.putExtra ( "storeLongitude", getStoreLongtitude );

                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );

            }
        } );

        Orderreference = CommonMethods.fetchFirebaseDatabaseReference ( ORDER_DETAILS_FIREBASE_TABLE );
        viewcartRef = CommonMethods.fetchFirebaseDatabaseReference ( VIEW_CART_FIREBASE_TABLE ).child (saved_id );
        retriveFunction ();

        paymentBtn = findViewById ( R.id.paymentBtn );


        firstAddressRb = findViewById ( R.id.AddressOneRb );
        secondAddressRb = findViewById ( R.id.AddresstwoRb );
        thirdAddressRb = findViewById ( R.id.AddressthreeRb );
        fourthAddressRb = findViewById ( R.id.AddressfourRb );

        firstRelativeLayout = findViewById ( R.id.firstAddressLayoutRelative );
        secondRelelativeLayout = findViewById ( R.id.SecondAddressLayoutRelative );
        thirdRelativeLayout = findViewById ( R.id.thirdAddresslayoutRelative );
        fourthRelativeLayout = findViewById ( R.id.fourthAddresslayoutRelative );
        distanceFeeDataRef = CommonMethods.fetchFirebaseDatabaseReference( "DeliveryCharges" );
        storeTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference( "storeTimingMaintenance" );

        metrozStoteTimingDataRef=CommonMethods.fetchFirebaseDatabaseReference("MetrozstoreTiming");



        distanceFeeDataRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () > 0) {
                    perKmDistanceFee = Integer.parseInt ( String.valueOf ( dataSnapshot.child ( "DISTANCE_FEE_PER_KM" ).getValue () ) );

                    int Radius = 6371;

                    double lat1 = HomePageAcitivity.saved_Userlatitude;
                    double lat2 = getStoreLatitude;

                    double lon1 = HomePageAcitivity.saved_Userlongtitude;
                    double lon2 = getStoreLongtitude;

                    double dLat = Math.toRadians ( lat2 - lat1 );
                    double dLon = Math.toRadians ( lon2 - lon1 );
                    double a = Math.sin ( dLat / 2 ) * Math.sin ( dLat / 2 )
                            + Math.cos ( Math.toRadians ( lat1 ) )
                            * Math.cos ( Math.toRadians ( lat2 ) ) * Math.sin ( dLon / 2 )
                            * Math.sin ( dLon / 2 );
                    double c = 2 * Math.asin ( Math.sqrt ( a ) );
                    double valueResult = Radius * c;

                    double km = valueResult / 1;

                    resultKiloMeterRoundOff = (int) Math.round ( km );
                    roundOff = Math.round ( km * 100.0 ) / 100.0;
                    totaldeliveryFee = resultKiloMeterRoundOff * perKmDistanceFee;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        shippindAddressDataRef = CommonMethods.fetchFirebaseDatabaseReference("ShippingAddress").child(String.valueOf(saved_id));

        firstAddressName = findViewById ( R.id.customerNameOneTxt );
        secondAddresName = findViewById ( R.id.customerNametwoTxt );
        thirdAddressName = findViewById ( R.id.customerNamethreeTxt );
        fourthAddressName = findViewById ( R.id.customerNamefourTxt );

        firstAddressPhoneNumber = findViewById ( R.id.phoneNumberOneTxt );
        secondAddressPhoneNumber = findViewById ( R.id.phoneNumbertwoTxt );
        thirdAddressPhoneNumber = findViewById ( R.id.phoneNumberthreeTxt );
        fourthAddressPhoneNumber = findViewById ( R.id.phoneNumberfourTxt );



        firstRelativeLayout.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                intentFunction (1);

            }
        } );

        secondRelelativeLayout.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                intentFunction (2);

            }
        } );

        thirdRelativeLayout.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                intentFunction (3);

            }
        } );

        fourthRelativeLayout.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                intentFunction (4);

            }
        } );

        shippindAddressDataRef.addValueEventListener ( new ValueEventListener ()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getChildrenCount () > 0) {
                    for ( DataSnapshot detailsSnap : dataSnapshot.getChildren () ) {
                        shippingAddress = detailsSnap.getValue ( ShippingAddress.class );
                        shippingAddressArrayList.add ( shippingAddress );
                    }
                    if (dataSnapshot.getChildrenCount () == 1) {
                        firstRelativeLayout.setVisibility ( View.VISIBLE );
                        addressFunctionOne ( shippingAddressArrayList.get ( 0 ).getFullName (),
                                shippingAddressArrayList.get ( 0 ).getPhoneNumber (),
                                shippingAddressArrayList.get ( 0 ).getHouseAddress (),
                                shippingAddressArrayList.get ( 0 ).getAreaAddress (),
                                shippingAddressArrayList.get ( 0 ).getCity (),
                                shippingAddressArrayList.get ( 0 ).getPincode (),
                                shippingAddressArrayList.get(0).getAddressType() );
                    }
                    if (dataSnapshot.getChildrenCount () == 2) {
                        firstRelativeLayout.setVisibility ( View.VISIBLE );
                        secondRelelativeLayout.setVisibility ( View.VISIBLE );
                        addressFunctionOne ( shippingAddressArrayList.get ( 0 ).getFullName (),
                                shippingAddressArrayList.get ( 0 ).getPhoneNumber (),
                                shippingAddressArrayList.get ( 0 ).getHouseAddress (),
                                shippingAddressArrayList.get ( 0 ).getAreaAddress (),
                                shippingAddressArrayList.get ( 0 ).getCity (),
                                shippingAddressArrayList.get ( 0 ).getPincode (),
                                shippingAddressArrayList.get(0).getAddressType());
                        addressFunctionTwo ( shippingAddressArrayList.get ( 1 ).getFullName (),
                                shippingAddressArrayList.get ( 1 ).getPhoneNumber (),
                                shippingAddressArrayList.get ( 1 ).getHouseAddress (),
                                shippingAddressArrayList.get ( 1 ).getAreaAddress (),
                                shippingAddressArrayList.get ( 1 ).getCity (),
                                shippingAddressArrayList.get ( 1 ).getPincode () ,
                                shippingAddressArrayList.get(1).getAddressType());
                    }
                    if (dataSnapshot.getChildrenCount () == 3) {
                        firstRelativeLayout.setVisibility ( View.VISIBLE );
                        secondRelelativeLayout.setVisibility ( View.VISIBLE );
                        thirdRelativeLayout.setVisibility ( View.VISIBLE );
                        addressFunctionOne ( shippingAddressArrayList.get ( 0 ).getFullName (),
                                shippingAddressArrayList.get ( 0 ).getPhoneNumber (),
                                shippingAddressArrayList.get ( 0 ).getHouseAddress (),
                                shippingAddressArrayList.get ( 0 ).getAreaAddress (),
                                shippingAddressArrayList.get ( 0 ).getCity (),
                                shippingAddressArrayList.get ( 0 ).getPincode (),
                                shippingAddressArrayList.get(0).getAddressType());
                        addressFunctionTwo ( shippingAddressArrayList.get ( 1 ).getFullName (),
                                shippingAddressArrayList.get ( 1 ).getPhoneNumber (),
                                shippingAddressArrayList.get ( 1 ).getHouseAddress (),
                                shippingAddressArrayList.get ( 1 ).getAreaAddress (),
                                shippingAddressArrayList.get ( 1 ).getCity (),
                                shippingAddressArrayList.get ( 1 ).getPincode () ,
                                shippingAddressArrayList.get(1).getAddressType());
                        addressFunctionThird ( shippingAddressArrayList.get ( 2 ).getFullName (),
                                shippingAddressArrayList.get ( 2 ).getPhoneNumber (),
                                shippingAddressArrayList.get ( 2 ).getHouseAddress (),
                                shippingAddressArrayList.get ( 2 ).getAreaAddress (),
                                shippingAddressArrayList.get ( 2 ).getCity (),
                                shippingAddressArrayList.get ( 2 ).getPincode () ,
                                shippingAddressArrayList.get(2).getAddressType());

                    }
                    if (dataSnapshot.getChildrenCount () == 4) {
                        firstRelativeLayout.setVisibility ( View.VISIBLE );
                        secondRelelativeLayout.setVisibility ( View.VISIBLE );
                        thirdRelativeLayout.setVisibility ( View.VISIBLE );
                        fourthRelativeLayout.setVisibility ( View.VISIBLE );
                        addressFunctionOne (
                                shippingAddressArrayList.get ( 0 ).getFullName (),
                                shippingAddressArrayList.get ( 0 ).getPhoneNumber (),
                                shippingAddressArrayList.get ( 0 ).getHouseAddress (),
                                shippingAddressArrayList.get ( 0 ).getAreaAddress (),
                                shippingAddressArrayList.get ( 0 ).getCity (),
                                shippingAddressArrayList.get ( 0 ).getPincode () ,
                                shippingAddressArrayList.get(0).getAddressType());

                        addressFunctionTwo (
                                shippingAddressArrayList.get ( 1 ).getFullName (),
                                shippingAddressArrayList.get ( 1 ).getPhoneNumber (),
                                shippingAddressArrayList.get ( 1 ).getHouseAddress (),
                                shippingAddressArrayList.get ( 1 ).getAreaAddress (),
                                shippingAddressArrayList.get ( 1 ).getCity (),
                                shippingAddressArrayList.get ( 1 ).getPincode (),
                                shippingAddressArrayList.get(1).getAddressType());
                        addressFunctionThird (
                                shippingAddressArrayList.get ( 2 ).getFullName (),
                                shippingAddressArrayList.get ( 2 ).getPhoneNumber (),
                                shippingAddressArrayList.get ( 2 ).getHouseAddress (),
                                shippingAddressArrayList.get ( 2 ).getAreaAddress (),
                                shippingAddressArrayList.get ( 2 ).getCity (),
                                shippingAddressArrayList.get ( 2 ).getPincode (),
                                shippingAddressArrayList.get(2).getAddressType());
                        addressFunctionFourth (
                                shippingAddressArrayList.get ( 3 ).getFullName (),
                                shippingAddressArrayList.get ( 3 ).getPhoneNumber (),
                                shippingAddressArrayList.get ( 3 ).getHouseAddress (),
                                shippingAddressArrayList.get ( 3 ).getAreaAddress (),
                                shippingAddressArrayList.get ( 3 ).getCity (),
                                shippingAddressArrayList.get ( 3 ).getPincode (),
                                shippingAddressArrayList.get(3).getAddressType());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        addNewAddress = findViewById ( R.id.addnewAddress );



        addNewAddress.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                shippindAddressDataRef.addListenerForSingleValueEvent ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.getChildrenCount () < 4) {
                            Intent intent = new Intent ( AddAddressActivity.this, AddNewAddressActivity.class );
                            intent.putExtra ( "deliveryType", deliverytType );
                            intent.putExtra ( "tips", String.valueOf ( tipAmount ) );
                            intent.putExtra ( "finalBillTip", String.valueOf ( finalBill_tip ) );
                            intent.putExtra ( "finalBillAmount", String.valueOf ( finalBill ) );
                            intent.putExtra ( "instructionString", instructionString );
                            intent.putExtra ( "storeAddress", storeAddress );
                            startActivity ( intent );
                        } else {
                            Toast.makeText ( AddAddressActivity.this, "Please edit address", Toast.LENGTH_SHORT ).show ();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
            }
        } );

        if (getCheckValue==1)
        {
            secondAddressRb.setChecked ( false );
            thirdAddressRb.setChecked ( false );
            fourthAddressRb.setChecked ( false );
            firstAddressRb.setChecked(true);
        }
        if (getCheckValue==2)
        {
            secondAddressRb.setChecked ( true );
            thirdAddressRb.setChecked ( false );
            fourthAddressRb.setChecked ( false );
            firstAddressRb.setChecked(false);
        }
        if (getCheckValue==3)
        {
            secondAddressRb.setChecked ( false );
            thirdAddressRb.setChecked ( true );
            fourthAddressRb.setChecked ( false );
            firstAddressRb.setChecked(false);
        }
        if (getCheckValue==4)
        {

            secondAddressRb.setChecked ( false );
            thirdAddressRb.setChecked ( false );
            fourthAddressRb.setChecked ( true );
            firstAddressRb.setChecked(false);
        }



        firstAddressRb.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter=0;
                    selectedChidValue="1";
                    secondAddressRb.setChecked ( false );
                    thirdAddressRb.setChecked ( false );
                    fourthAddressRb.setChecked ( false );
                }
            }
        } );

        secondAddressRb.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter=1;
                    selectedChidValue="2";
                    firstAddressRb.setChecked ( false );
                    thirdAddressRb.setChecked ( false );
                    fourthAddressRb.setChecked ( false );
                }
            }
        } );

        thirdAddressRb.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter=2;
                    selectedChidValue="3";
                    firstAddressRb.setChecked ( false );
                    secondAddressRb.setChecked ( false );
                    fourthAddressRb.setChecked ( false );

                }
            }
        } );

        fourthAddressRb.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter=3;
                    selectedChidValue="4";
                    firstAddressRb.setChecked ( false );
                    secondAddressRb.setChecked ( false );
                    thirdAddressRb.setChecked ( false );

                }
            }
        });


        paymentBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                paymentBtn.setEnabled(false);
                metrozStoteTimingDataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()>0) {
                            MetrozStoreTime metrozStoreTime = dataSnapshot.getValue(MetrozStoreTime.class);

                            metrozStartTime = metrozStoreTime.getShopStartTime();
                            metrozStopTime = metrozStoreTime.getShopEndTime();
                        }
                        storeTimingDataRef.child(storeIdTxt).addListenerForSingleValueEvent(new ValueEventListener() {
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

                                            if ((sdf.parse(currentTime).compareTo(sdf.parse(metrozStartTime)) == 1) && !(sdf.parse(currentTime).compareTo(sdf.parse(metrozStopTime)) == 1))
                                            {
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
                                                    if (storeTimings.getStoreStatus().equalsIgnoreCase(""))
                                                    {
                                                        if ((sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopStartTime())) == 1) && !(sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopEndTime())) == 1)) {
                                                            loadFunction();
                                                        } else if ((sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopEndTime())) == 1) || (sdf.parse(currentTime).compareTo(sdf.parse(storeTimings.getShopStartTime())) == -1)) {
                                                            new SweetAlertDialog(AddAddressActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                    .setTitleText("Error")
                                                                    .setContentText("Closed,currently not accepting orders.")
                                                                    .show();
                                                            paymentBtn.setEnabled(true);
                                                        }
                                                    } else {
                                                        if (storeTimings.getStoreStatus().equalsIgnoreCase("Opened")) {
                                                            loadFunction();
                                                        }
                                                        if (storeTimings.getStoreStatus().equalsIgnoreCase("Closed")) {
                                                            new SweetAlertDialog(AddAddressActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                    .setTitleText("Error")
                                                                    .setContentText("Closed,currently not accepting orders.")
                                                                    .show();
                                                            paymentBtn.setEnabled(true);
                                                        }
                                                    }
                                                }
                                            }else {
                                                new SweetAlertDialog(AddAddressActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Error")
                                                        .setContentText("Closed,currently not accepting orders.")
                                                        .show();
                                                paymentBtn.setEnabled(true);
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
            }
        } );
    }





    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void bottomSheetDialog()
    {
        if (!CommonMethods.getConnectivityStatusString(getApplicationContext()).equals("No internet is available")) {
            if (saved_id != null && !"".equals(saved_id)) {

                if (!((Activity) AddAddressActivity.this).isFinishing()) {
                    if (itemDetailList.size() == 0) {
                        Toast.makeText(AddAddressActivity.this, "" +
                                "No items available in the cart ", Toast.LENGTH_SHORT).show();
                    } else {

                        Intent intent = new Intent(AddAddressActivity.this, PaymentActivity.class);


                        intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                        intent.putExtra("deliveryType", deliverytType);
                        intent.putExtra("tips", String.valueOf(tipAmount));
                        intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                        intent.putExtra("instructionString", instructionString);
                        intent.putExtra("storeAddress", storeAddress);
                        intent.putExtra("storeLatitude", getStoreLatitude);
                        intent.putExtra("storeLongitude", getStoreLongtitude);
                        intent.putExtra("addressPref", "2");
                        intent.putExtra("StoreId", storeIdTxt);
                        intent.putExtra("FromNewAddress", "FromNewAddress");
                        String customerName = shippingAddressArrayList.get(counter).getFullName().toUpperCase();
                        String customerMobilenumber = shippingAddressArrayList.get(counter).getPhoneNumber();
                        String customerPinCode = shippingAddressArrayList.get(counter).getPincode();


                        String address = (shippingAddressArrayList.get(counter).getHouseAddress() + "," + shippingAddressArrayList.get(counter).getAreaAddress() + "," +
                                shippingAddressArrayList.get(counter).getCity() + "," + shippingAddressArrayList.get(counter).getState() + "," + shippingAddressArrayList.get(counter).getPincode());

                        intent.putExtra("shippingAddress", address);
                        intent.putExtra("customerName", customerName);
                        intent.putExtra("customerMobilenumber", customerMobilenumber);
                        intent.putExtra("customerPinCode", customerPinCode);
                        intent.putExtra("AddressId",selectedChidValue);


                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }
            } else {
                Toast.makeText(AddAddressActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                paymentBtn.setEnabled(true);
            }
        }
        else
        {
            new SweetAlertDialog(AddAddressActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("No Network Connection")
                    .show();
            paymentBtn.setEnabled(true);
        }
    }




    public void intentFunction(int i)
    {
        Intent intent = new Intent ( getApplicationContext (), AddNewAddressActivity.class );
        intent.putExtra ( "childCountValue", i );
        intent.putExtra ( "deliveryType", deliverytType );
        intent.putExtra ( "tips", String.valueOf ( tipAmount ) );
        intent.putExtra ( "finalBillTip", String.valueOf ( finalBill_tip ) );
        intent.putExtra ( "finalBillAmount", String.valueOf ( finalBill ) );
        intent.putExtra ( "instructionString", instructionString );
        intent.putExtra ( "storeAddress", storeAddress );
        intent.putExtra ( "storeLatitude", getStoreLatitude );
        intent.putExtra ( "storeLongitude", getStoreLongtitude );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    @Override
    protected void onStart() {
        super.onStart ();

        Orderreference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid = dataSnapshot.getChildrenCount ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    public void retriveFunction() {

        viewcartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                itemDetailList.clear();

                for (DataSnapshot viewCartsnap : dataSnapshot.getChildren()) {
                    itemDetails = viewCartsnap.getValue(ItemDetails.class);
                    itemDetailList.add(itemDetails);
                }
                if (itemDetailList!=null && itemDetailList.size()>0) {
                    storeIdTxt = itemDetailList.get(0).getSellerId();
                }

                orderDetails.setItemDetailList(itemDetailList);
                orderDetails.setStoreName(itemDetails.getStoreName());
                orderDetails.setStorePincode(itemDetails.getStorePincode());
                orderDetails.setStoreAddress(itemDetails.getStoreAdress());
                orderDetails.setDeliveryType(deliverytType);
                orderDetails.setTotalAmount(Integer.parseInt(finalBill));
                checkIntent = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent ( AddAddressActivity.this, HomePageAcitivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }


    public void addressFunctionOne(String customerName, String phoneNumber, String homeAddress, String areaAddress, String cityAddress, String pincode,String addressType) {
        firstAddressName.setText ( customerName );
        firstAddressPhoneNumber.setText ( phoneNumber );

        if (addressType.equals("Work/Office Address"))
        {
            addressTypetext.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_baseline_work_24, 0, 0, 0 );
            addressTypetext.setText(addressType);
        }else
        {
            addressTypetext.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_baseline_home_24, 0, 0, 0 );
            addressTypetext.setText(addressType);
        }
        firstAddressRb.setText ( homeAddress + "," + "\n" + areaAddress + "," + "\n" + cityAddress + "-" + pincode );
    }

    public void addressFunctionTwo(String customerName, String phoneNumber, String homeAddress, String areaAddress, String cityAddress, String pincode,String addressType) {
        secondAddresName.setText ( customerName );
        secondAddressPhoneNumber.setText ( phoneNumber );
        if (addressType.equals("Work/Office Address"))
        {
            addressTypeTwoText.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_baseline_work_24, 0, 0, 0 );
            addressTypeTwoText.setText(addressType);
        }else
        {
            addressTypeTwoText.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_baseline_home_24, 0, 0, 0 );
            addressTypeTwoText.setText(addressType);
        }

        secondAddressRb.setText ( homeAddress + "," + "\n" + areaAddress + "," + "\n" + cityAddress + "-" + pincode );
    }

    public void addressFunctionThird(String customerName, String phoneNumber, String homeAddress, String areaAddress, String cityAddress, String pincode,String addressType) {
        thirdAddressName.setText ( customerName );
        thirdAddressPhoneNumber.setText ( phoneNumber );


        if (addressType.equals("Work/Office Address"))
        {
            addressTypeThreetext.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_baseline_work_24, 0, 0, 0 );
            addressTypeThreetext.setText(addressType);
        }else
        {
            addressTypeThreetext.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_baseline_home_24, 0, 0, 0 );
            addressTypeThreetext.setText(addressType);
        }

        thirdAddressRb.setText ( homeAddress + "," + "\n" + areaAddress + "," + "\n" + cityAddress + "-" + pincode );
    }

    public void addressFunctionFourth(String customerName, String phoneNumber, String homeAddress, String areaAddress, String cityAddress, String pincode,String addressType) {
        fourthAddressName.setText ( customerName );
        fourthAddressPhoneNumber.setText ( phoneNumber );

        if (addressType.equals("Work/Office Address"))
        {
            addressTypeFourText.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_baseline_work_24, 0, 0, 0 );
            addressTypeFourText.setText(addressType);
        }else
        {
            addressTypeFourText.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_baseline_home_24, 0, 0, 0 );
            addressTypeFourText.setText(addressType);
        }

        fourthAddressRb.setText ( homeAddress + "," + "\n" + areaAddress + "," + "\n" + cityAddress + "-" + pincode );
    }


    public void loadFunction()
    {
        if (firstAddressRb.isChecked() == true)
        {
            int Radius = 6371;// radius of earth in Km
            double lat1 = shippingAddressArrayList.get(0).getUserLatitude();
            double lat2 = sharedPreferenceLatitude;
            double lon1 =  shippingAddressArrayList.get(0).getUserLongtitude();
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
                bottomSheetDialog();
                orderDetails.setFullName(shippingAddressArrayList.get(0).getFullName().toUpperCase());
                orderDetails.setShippingaddress(shippingAddressArrayList.get(0).getHouseAddress() + "," + shippingAddressArrayList.get(0).getAreaAddress() + "," +
                        shippingAddressArrayList.get(0).getCity() + "," + shippingAddressArrayList.get(0).getState() + "," + shippingAddressArrayList.get(0).getPincode());
                orderDetails.setShippingPincode(shippingAddressArrayList.get(0).getPincode());
                orderDetails.setPhoneNumber(shippingAddressArrayList.get(0).getPhoneNumber());
            }else
            {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(AddAddressActivity.this, SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        firstAddressRb.setChecked(false);
                        secondAddressRb.setChecked(false);
                        thirdAddressRb.setChecked(false);
                        fourthAddressRb.setChecked(false);

                    }
                });
                sweetAlertDialog.setTitleText("Delivery is not available in this location").show();
                paymentBtn.setEnabled(true);

            }
        } else if (secondAddressRb.isChecked() == true) {
            int Radius = 6371;// radius of earth in Km
            double lat1 = shippingAddressArrayList.get(1).getUserLatitude();
            double lat2 = sharedPreferenceLatitude;
            double lon1 =  shippingAddressArrayList.get(1).getUserLongtitude();
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
                bottomSheetDialog();
                orderDetails.setFullName(shippingAddressArrayList.get(1).getFullName().toUpperCase());
                orderDetails.setShippingaddress(shippingAddressArrayList.get(1).getHouseAddress() + "," + shippingAddressArrayList.get(1).getAreaAddress() + "," +
                        shippingAddressArrayList.get(1).getCity() + "," + shippingAddressArrayList.get(1).getState() + "," + shippingAddressArrayList.get(1).getPincode());
                orderDetails.setShippingPincode(shippingAddressArrayList.get(1).getPincode());
                orderDetails.setPhoneNumber(shippingAddressArrayList.get(1).getPhoneNumber());
            }else {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(AddAddressActivity.this, SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        firstAddressRb.setChecked(false);
                        secondAddressRb.setChecked(false);
                        thirdAddressRb.setChecked(false);
                        fourthAddressRb.setChecked(false);
                    }
                });
                sweetAlertDialog.setTitleText("Delivery is not available in this location").show();
                paymentBtn.setEnabled(true);
            }
        } else if (thirdAddressRb.isChecked() == true)
        {
            int Radius = 6371;// radius of earth in Km
            double lat1 =  shippingAddressArrayList.get(2).getUserLatitude();
            double lat2 = sharedPreferenceLatitude;
            double lon1 =  shippingAddressArrayList.get(2).getUserLongtitude();
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
                bottomSheetDialog();
                orderDetails.setFullName(shippingAddressArrayList.get(2).getFullName().toUpperCase());
                orderDetails.setShippingaddress(shippingAddressArrayList.get(2).getHouseAddress() + "," + shippingAddressArrayList.get(2).getAreaAddress() + "," +
                        shippingAddressArrayList.get(2).getCity() + "," + shippingAddressArrayList.get(2).getState() + "," + shippingAddressArrayList.get(2).getPincode());
                orderDetails.setShippingPincode(shippingAddressArrayList.get(2).getPincode());
                orderDetails.setPhoneNumber(shippingAddressArrayList.get(2).getPhoneNumber());
            }else
            {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(AddAddressActivity.this, SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        firstAddressRb.setChecked(false);
                        secondAddressRb.setChecked(false);
                        thirdAddressRb.setChecked(false);
                        fourthAddressRb.setChecked(false);
                    }
                });
                sweetAlertDialog.setTitleText("Delivery is not available in this location").show();
                paymentBtn.setEnabled(true);

            }
        } else if (fourthAddressRb.isChecked() == true) {
            int Radius = 6371;// radius of earth in Km
            double lat1 = shippingAddressArrayList.get(3).getUserLatitude();
            double lat2 = sharedPreferenceLatitude;
            double lon1 = shippingAddressArrayList.get(3).getUserLongtitude();
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

            if (resultKiloMeterRoundOff <= getDistance)
            {
                bottomSheetDialog();
                orderDetails.setFullName(shippingAddressArrayList.get(3).getFullName().toUpperCase());
                orderDetails.setShippingaddress(shippingAddressArrayList.get(3).getHouseAddress() + "," + shippingAddressArrayList.get(3).getAreaAddress() + "," +
                        shippingAddressArrayList.get(3).getCity() + "," + shippingAddressArrayList.get(3).getState() + "," + shippingAddressArrayList.get(3).getPincode());
                orderDetails.setShippingPincode(shippingAddressArrayList.get(3).getPincode());
                orderDetails.setPhoneNumber(shippingAddressArrayList.get(3).getPhoneNumber());
            }else
            {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(AddAddressActivity.this, SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        firstAddressRb.setChecked(false);
                        secondAddressRb.setChecked(false);
                        thirdAddressRb.setChecked(false);
                        fourthAddressRb.setChecked(false);
                    }
                });
                sweetAlertDialog.setTitleText("Delivery is not available in this location").show();
                paymentBtn.setEnabled(true);
            }
        } else if (firstAddressRb.isChecked() == false && secondAddressRb.isChecked() == false &&
                thirdAddressRb.isChecked() == false && fourthAddressRb.isChecked() == false) {
            Toast.makeText(AddAddressActivity.this, "Please Select address", Toast.LENGTH_SHORT).show();
            paymentBtn.setEnabled(true);
        } else {
            Toast.makeText(AddAddressActivity.this, "Please add address", Toast.LENGTH_SHORT).show();
            paymentBtn.setEnabled(true);
        }
    }
}





