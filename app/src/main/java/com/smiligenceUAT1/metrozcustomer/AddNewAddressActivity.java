package com.smiligenceUAT1.metrozcustomer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.bean.ShippingAddress;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Constant;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.TextUtils;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

public class AddNewAddressActivity extends AppCompatActivity {
    DatabaseReference shippingDataBaseRef;
    Button saveShippingAddress;
    EditText customerNameShipping, customerPhoneNumberShipping, alternatePhoneNumberShipping, houseNoShipping, areaShipping,
            pincodeShipping, landmarkShipping;
    AutoCompleteTextView cityShipping, stateShiping;

    int shippindAddressMaxId = 0;
    RadioButton homeAddressRadioButton, officeAddressRadioButton;
    RadioGroup radioGroup;
    String addressType;
    ShippingAddress shippingAddressOne = new ShippingAddress();
    TextInputLayout customerNameShippingTxt, customerPhoneNumberShippingTxt, alternatePhoneNumberShippingTxt, houseNoShippingTxt, areaShippingTxt,
            pincodeShippingTxt, landmarkShippingtTxt, cityShippingTxt, stateShipingTxt;
    boolean isAddressSelected, isAutoLoadFunction = false;
    int receivedChildCountValue;
    ImageView backtoaddress;
    private BroadcastReceiver MyReceiver = new MyReceiver();
    String tipAmount, finalBill_tip, finalBill, deliverytType;
    String instructionString, storeAddress;
    boolean intentCheck = false;
    EditText pickCurrentLocationEdt;
    int getMaxIdValueFromMap;
    private Double latValue = 0.00;
    private Double longValue = 0.00;
    String getFullAdreesFromMap, shippingAddressPinCode, shippingCity, shippingState;
    LinearLayout linearlayout;
    String saved_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_add_new_address);
        linearlayout=findViewById(R.id.linearlayout);

        disableAutofill();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final SharedPreferences loginSharedPreferences1 = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences1.getString ( "customerId", "" );
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        deliverytType = getIntent().getStringExtra("deliveryType");
        tipAmount = getIntent().getStringExtra("tips");
        finalBill_tip = getIntent().getStringExtra("finalBillTip");
        finalBill = getIntent().getStringExtra("finalBillAmount");
        instructionString = getIntent().getStringExtra("instructionString");
        storeAddress = getIntent().getStringExtra("storeAddress");
        getFullAdreesFromMap = getIntent().getStringExtra("FullAddress");
        latValue = getIntent().getDoubleExtra("userLatitude", 0.0);
        longValue = getIntent().getDoubleExtra("userLongtitude", 0.0);
        shippingAddressPinCode = getIntent().getStringExtra("shippingAddressPinCode");
        shippingCity = getIntent().getStringExtra("shippingAddressCity");
        shippingState = getIntent().getStringExtra("shippingAddressState");

        getMaxIdValueFromMap = getIntent().getIntExtra("childCountValue", 0);

        backtoaddress = findViewById(R.id.backtoaddress);
        radioGroup = findViewById(R.id.groupradio);


        backtoaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewAddressActivity.this, AddAddressActivity.class);
                intent.putExtra("deliveryType", deliverytType);
                intent.putExtra("tips", String.valueOf(tipAmount));
                intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                intent.putExtra("instructionString", instructionString);
                intent.putExtra("storeAddress", storeAddress);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        receivedChildCountValue = getIntent().getIntExtra("childCountValue", 0);

        shippingDataBaseRef = CommonMethods.fetchFirebaseDatabaseReference("ShippingAddress").child(String.valueOf(saved_id));
        saveShippingAddress = findViewById(R.id.SaveShippingAddress);
        customerNameShipping = findViewById(R.id.shippingName);
        customerPhoneNumberShipping = findViewById(R.id.shippingMobileNumber);
        alternatePhoneNumberShipping = findViewById(R.id.shippingAlternatePhoneNumber);
        houseNoShipping = findViewById(R.id.shippingHousenumber);
        areaShipping = findViewById(R.id.ShippingArea);
        cityShipping = findViewById(R.id.ShippingCity);
        stateShiping = findViewById(R.id.ShippingState);
        pincodeShipping = findViewById(R.id.shippingpincode);
        landmarkShipping = findViewById(R.id.shippinglandmark);
        homeAddressRadioButton = findViewById(R.id.shippingHomeAddress);
        officeAddressRadioButton = findViewById(R.id.shippingOfficeAddress);
        customerNameShippingTxt = findViewById(R.id.shippingnameTxt);
        customerPhoneNumberShippingTxt = findViewById(R.id.shippingMobileNumberTxt);
        alternatePhoneNumberShippingTxt = findViewById(R.id.shippingAlternatePhoneNumberTxt);
        houseNoShippingTxt = findViewById(R.id.shippingHousenumberTxt);
        areaShippingTxt = findViewById(R.id.ShippingAreaTxt);
        pincodeShippingTxt = findViewById(R.id.shippingpincodeTxt);
        landmarkShippingtTxt = findViewById(R.id.shippinglandmarkTxt);
        cityShippingTxt = findViewById(R.id.ShippingCityTxt);
        stateShipingTxt = findViewById(R.id.ShippingStateTxt);
        pickCurrentLocationEdt = findViewById(R.id.pickCurrentLocationTxt);

        if (getFullAdreesFromMap != null && !getFullAdreesFromMap.equalsIgnoreCase(""))
        {
            pickCurrentLocationEdt.setText(getFullAdreesFromMap);
        }

        if (shippingAddressPinCode != null && !shippingAddressPinCode.equalsIgnoreCase(""))
        {
            pincodeShipping.setText(shippingAddressPinCode);
        }
        if (shippingCity != null && !shippingCity.equalsIgnoreCase("") )
        {
            cityShipping.setText(shippingCity);
        }
        if (shippingState != null && !shippingState.equalsIgnoreCase(""))
        {
            stateShiping.setText(shippingState);

        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>
                (this, R.layout.spinner_type, Constant.TAMILNADU_CITY);
        cityShipping.setThreshold(1);
        cityShipping.setAdapter(cityAdapter);

        cityShipping.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cityShipping.showDropDown();
                return false;
            }
        });


        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>
                (this, R.layout.spinner_type, Constant.STATE_STRING);
        stateShiping.setThreshold(1);
        stateShiping.setAdapter(stateAdapter);

        stateShiping.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                stateShiping.showDropDown();
                return false;
            }
        });


        if (receivedChildCountValue != 0) {
            final Query shippindAddressDetails = shippingDataBaseRef.orderByChild("shippingId")
                    .equalTo(String.valueOf(receivedChildCountValue));

            shippindAddressDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount() > 0) {

                        for (DataSnapshot shippindAddressSnap : dataSnapshot.getChildren()) {

                            ShippingAddress shippingAddress = shippindAddressSnap.getValue(ShippingAddress.class);
                            isAutoLoadFunction = true;
                            customerNameShipping.setText(shippingAddress.getFullName());
                            customerPhoneNumberShipping.setText(shippingAddress.getPhoneNumber());
                            alternatePhoneNumberShipping.setText(shippingAddress.getAlternatePhoneNumber());
                            houseNoShipping.setText(shippingAddress.getHouseAddress());
                            areaShipping.setText(shippingAddress.getAreaAddress());



                            landmarkShipping.setText(shippingAddress.getLandMark());
                            if (getFullAdreesFromMap != null && !getFullAdreesFromMap.equals("")) {
                                pickCurrentLocationEdt.setText(getFullAdreesFromMap);
                            } else {
                                pickCurrentLocationEdt.setText(shippingAddress.getFullAddressFromMap());
                            }

                            getFullAdreesFromMap = shippingAddress.getFullAddressFromMap();
                            if (latValue != null && !latValue.equals(0.00)) {
                                latValue = latValue;
                            } else {
                                latValue = shippingAddress.getUserLatitude();
                            }

                            if (longValue != null && !longValue.equals(0.00)) {
                                longValue = longValue;
                            } else {
                                longValue = shippingAddress.getUserLongtitude();
                            }

                            if (shippingAddressPinCode!=null && !shippingAddressPinCode.equals(""))
                            {
                                pincodeShipping.setText(shippingAddressPinCode);
                            }
                            else
                            {
                                pincodeShipping.setText(shippingAddress.getPincode());
                            }

                            if (shippingCity!=null && !shippingCity.equals(""))
                            {
                                cityShipping.setText(shippingCity);
                            }
                            else
                            {
                                cityShipping.setText(shippingAddress.getCity());
                            }

                            if (shippingState!=null && !shippingState.equals(""))
                            {
                                stateShiping.setText(shippingState);
                            }
                            else
                            {
                                stateShiping.setText(shippingAddress.getState());
                            }

                            if (shippingAddress.getAddressType().equals("Home Address")) {
                                homeAddressRadioButton.setChecked(true);
                                isAddressSelected = true;
                                addressType = "Home Address";
                            } else {
                                officeAddressRadioButton.setChecked(true);
                                isAddressSelected = true;
                                addressType = "Work/Office Address";
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        homeAddressRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressType = "Home Address";
                isAddressSelected = true;

            }
        });
        officeAddressRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressType = "Work/Office Address";
                isAddressSelected = true;
            }
        });
        linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddNewAddressActivity.this, FindAddressActivity.class);
                intent.putExtra("deliveryType", deliverytType);
                intent.putExtra("tips", String.valueOf(tipAmount));
                intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                intent.putExtra("instructionString", instructionString);
                intent.putExtra("storeAddress", storeAddress);
                intent.putExtra("shippingAddressPinCode", shippingAddressPinCode);
                intent.putExtra("shippingAddressState", shippingCity);
                intent.putExtra("shippingAddressCity", shippingState);

                if (receivedChildCountValue != 0) {
                    intent.putExtra("childCountValue", receivedChildCountValue);
                } else {
                    intent.putExtra("childCountValue", shippindAddressMaxId + 1);
                }

                intent.putExtra("currentAddress", "ShippingAddress");

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        pickCurrentLocationEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddNewAddressActivity.this, FindAddressActivity.class);
                intent.putExtra("deliveryType", deliverytType);
                intent.putExtra("tips", String.valueOf(tipAmount));
                intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                intent.putExtra("instructionString", instructionString);
                intent.putExtra("storeAddress", storeAddress);
                intent.putExtra("shippingAddressPinCode", shippingAddressPinCode);
                intent.putExtra("shippingAddressState", shippingCity);
                intent.putExtra("shippingAddressCity", shippingState);

                if (receivedChildCountValue != 0) {
                    intent.putExtra("childCountValue", receivedChildCountValue);
                } else {
                    intent.putExtra("childCountValue", shippindAddressMaxId + 1);
                }

                intent.putExtra("currentAddress", "ShippingAddress");

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        saveShippingAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("".equals(customerNameShipping.getText().toString())) {
                    customerNameShipping.setError("Required");
                    return;
                } else if ("".equals(customerPhoneNumberShipping.getText().toString())) {
                    customerPhoneNumberShipping.setError("Required");
                    return;
                } else if (!TextUtils.validatePhoneNumber(customerPhoneNumberShipping.getText().toString())) {
                    customerPhoneNumberShipping.setError("Phone number must contains 10 digits");
                    return;
                } else if (!"".equals(alternatePhoneNumberShipping.getText().toString())
                        && (!TextUtils.validatePhoneNumber(alternatePhoneNumberShipping.getText().toString()))) {
                    alternatePhoneNumberShipping.setError("Phone number must contains 10 digits");
                    return;
                } else if (alternatePhoneNumberShipping.getText().toString().equalsIgnoreCase(customerPhoneNumberShipping.getText().toString())) {
                    alternatePhoneNumberShipping.setError("Alternate Phone Number cannot be same as Primary Phone Number");
                    return;
                } else if ("".equals(houseNoShipping.getText().toString())) {
                    houseNoShipping.setError("Required");
                    return;
                } else if ("".equals(areaShipping.getText().toString())) {
                    areaShipping.setError("Required");
                    return;
                } else if ("".equals(cityShipping.getText().toString())) {
                    cityShipping.setError("Required");
                    return;
                } else if ("".equals(stateShiping.getText().toString())) {
                    cityShipping.setError(null);
                    stateShiping.setError("Required");
                    return;
                } else if ("".equals(pincodeShipping.getText().toString())) {
                    stateShiping.setError(null);
                    pincodeShipping.setError("Required");
                    return;
                } else if (!TextUtils.validPinCode(pincodeShipping.getText().toString())) {
                    Toast.makeText(AddNewAddressActivity.this, "Delivery is not available in this location", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isAddressSelected == false) {
                    Toast.makeText(getApplicationContext(), "Please select address Type", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isAddressSelected) {

                    insertFunction();

                    if (isAutoLoadFunction) {

                        if (intentCheck == true) {
                            shippingAddressOne.setShippingId(String.valueOf(receivedChildCountValue));
                            shippingDataBaseRef.child(String.valueOf(receivedChildCountValue)).setValue(shippingAddressOne);
                            intentCheck = false;
                        }
                    } else {
                        if (intentCheck == true) {
                            shippingAddressOne.setShippingId(String.valueOf(getMaxIdValueFromMap));
                            shippingDataBaseRef.child(String.valueOf(getMaxIdValueFromMap)).setValue(shippingAddressOne);
                            intentCheck = false;
                        }
                    }
                    Intent intent = new Intent(AddNewAddressActivity.this, AddAddressActivity.class);
                    intent.putExtra("deliveryType", deliverytType);
                    intent.putExtra("tips", String.valueOf(tipAmount));
                    intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                    intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }
            }
        });

        shippingDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shippindAddressMaxId = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void insertFunction() {
        if (!((Activity) AddNewAddressActivity.this).isFinishing()) {
            shippingAddressOne.setFullName(customerNameShipping.getText().toString());
            shippingAddressOne.setPhoneNumber(customerPhoneNumberShipping.getText().toString());
            shippingAddressOne.setAlternatePhoneNumber(alternatePhoneNumberShipping.getText().toString());
            shippingAddressOne.setHouseAddress(houseNoShipping.getText().toString());
            shippingAddressOne.setAreaAddress(areaShipping.getText().toString());
            shippingAddressOne.setCity(cityShipping.getText().toString());
            shippingAddressOne.setState(stateShiping.getText().toString());
            shippingAddressOne.setPincode(pincodeShipping.getText().toString());
            shippingAddressOne.setLandMark(landmarkShipping.getText().toString());
            shippingAddressOne.setAddressType(addressType);
            shippingAddressOne.setUserLatitude(latValue);
            shippingAddressOne.setUserLongtitude(longValue);
            shippingAddressOne.setFullAddressFromMap(pickCurrentLocationEdt.getText().toString());

            intentCheck = true;
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
/*
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(AddNewAddressActivity.this, HomePageAcitivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}