package com.smiligenceUAT1.metrozcustomer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.adapter.AdminApplicableDicountAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.AdminnotApplicableDicountAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.DiscountAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.NotApplicableDiscountsAdapter;
import com.smiligenceUAT1.metrozcustomer.adapter.OneTimeDiscountAdapter;
import com.smiligenceUAT1.metrozcustomer.bean.AdminDiscounts;
import com.smiligenceUAT1.metrozcustomer.bean.Discount;
import com.smiligenceUAT1.metrozcustomer.bean.OneTimeDiscount;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class DiscountActivity extends AppCompatActivity {

    String resultstoreId, storeAddress, instructionString, paymentType, amount, deliverytType, receiptNumber, tipAmount, finalBill_tip, finalBill;
    double roundOff = 0.0, getStoreLatitude, getStoreLongtitude;
    ImageView backButton;
    DatabaseReference oneTimeDiscountRef, discountsDataRef,adminDiscountdataRef;
    Discount discount;
    Discount discount1;
    AdminDiscounts adminDiscounts;
    AdminDiscounts adminDiscounts1;
    ArrayList<Discount> discountArrayList = new ArrayList<>();
    ArrayList<Discount> notApplicableDiscountArrayList = new ArrayList<>();
    ArrayList<OneTimeDiscount> onetimeDisocuntArrayList = new ArrayList<>();

    ArrayList<AdminDiscounts> adminApplicableDiscountsArrayList=new ArrayList<>();
    ArrayList<AdminDiscounts> adminNotApplicableDiscountsArrayList=new ArrayList<>();
    ListView discountListview, customerBasedListview;
    ListView notApplicableListview;
    ListView adminApplicableDiscountsListview;
    OneTimeDiscount oneTimeDiscount;
    com.smiligenceUAT1.metrozcustomer.adapter.OneTimeDiscountAdapter oneTimeDiscountAdapter;
    DiscountAdapter discountAdapter;
    NotApplicableDiscountsAdapter notApplicableDiscountsAdapter;
    AdminApplicableDicountAdapter adminApplicableDicountAdapter;
    AdminnotApplicableDicountAdapter adminnotApplicableDicountAdapter;
    String getStoreName, getStoreId, getCategoryName, getCategoryId, getPincode, getAddressProof,
            getFullAddressFromMap, getLatuValue, getLongValue, getShippingAddress, getcustomerName, getMobileNumber, getCustomerPincode;

    ListView NotApplicableAdminDiscountListview;
    String saved_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);

        backButton = findViewById(R.id.backButtomImageView);
        final SharedPreferences loginSharedPreferences1 = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences1.getString ( "customerId", "" );

        discountsDataRef = CommonMethods.fetchFirebaseDatabaseReference("Discounts");
        oneTimeDiscountRef = CommonMethods.fetchFirebaseDatabaseReference("OneTimeDiscount");
        adminDiscountdataRef=CommonMethods.fetchFirebaseDatabaseReference("Discount");
        discountListview = findViewById(R.id.discountsListview);
        notApplicableListview = findViewById(R.id.notapplicablediscountsListview);
        customerBasedListview = findViewById(R.id.customerBasedDiscounts);
        adminApplicableDiscountsListview=findViewById(R.id.AdminDiscounts);
        NotApplicableAdminDiscountListview=findViewById(R.id.NotApplicableAdminDiscountListview);

        getStoreName = getIntent().getStringExtra("StoreName");
        getStoreId = getIntent().getStringExtra("StoreId");
        getCategoryName = getIntent().getStringExtra("categoryName");
        getCategoryId = getIntent().getStringExtra("categoryId");
        getPincode = getIntent().getStringExtra("pinCode");
        getAddressProof = getIntent().getStringExtra("addressPref");
        getFullAddressFromMap = getIntent().getStringExtra("FullAddress");
        getLatuValue = getIntent().getStringExtra("userLatitude");
        getLongValue = getIntent().getStringExtra("userLongtitude");
        getShippingAddress = getIntent().getStringExtra("shippingAddress");
        getcustomerName = getIntent().getStringExtra("customerName");
        getMobileNumber = getIntent().getStringExtra("customerMobilenumber");
        getCustomerPincode = getIntent().getStringExtra("customerPinCode");
        instructionString = getIntent().getStringExtra("instructionString");
        deliverytType = getIntent().getStringExtra("deliveryType");
        tipAmount = getIntent().getStringExtra("tips");
        finalBill_tip = getIntent().getStringExtra("finalBillTip");
        finalBill = getIntent().getStringExtra("finalBillAmount");
        storeAddress = getIntent().getStringExtra("storeAddress");
        getStoreLatitude = getIntent().getDoubleExtra("storeatitude", 0.0);
        getStoreLongtitude = getIntent().getDoubleExtra("storeLongitude", 0.0);


        final SharedPreferences loginSharedPreferences = getSharedPreferences("SAVE", MODE_PRIVATE);
        resultstoreId = loginSharedPreferences.getString("STOREID", "");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiscountActivity.this, PaymentActivity.class);

                intent.putExtra("StoreName", getStoreName);
                intent.putExtra("StoreId", getStoreId);
                intent.putExtra("categoryName", getCategoryName);
                intent.putExtra("categoryId", getCategoryId);
                intent.putExtra("pinCode", getPincode);
                intent.putExtra("addressPref", getAddressProof);
                intent.putExtra("FullAddress", getFullAddressFromMap);
                intent.putExtra("userLatitude", getLatuValue);
                intent.putExtra("userLongtitude", getLongValue);
                intent.putExtra("deliveryType", deliverytType);
                intent.putExtra("tips", String.valueOf(tipAmount));
                intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                intent.putExtra("instructionString", instructionString);
                intent.putExtra("storeAddress", storeAddress);
                intent.putExtra("shippingAddress", getShippingAddress);
                intent.putExtra("customerName", getcustomerName);
                intent.putExtra("customerMobilenumber", getMobileNumber);
                intent.putExtra("customerPinCode", getCustomerPincode);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        discountsDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    discountArrayList.clear();
                    notApplicableDiscountArrayList.clear();
                    for (DataSnapshot discountSnap : dataSnapshot.getChildren()) {
                        discount = discountSnap.getValue(Discount.class);
                        if (discount.getSellerId().equals(resultstoreId)) {
                            if (discount.getDiscountStatus().equals("Active")) {
                                if (discount.getTypeOfDiscount().equals("Price")) {

                                    try {
                                        if (((Number) NumberFormat.getInstance().parse(finalBill)).intValue() >=
                                                ((Number) NumberFormat.getInstance().parse(discount.getMinmumBillAmount())).intValue()      )  {
                                            discountArrayList.add(discount);
                                        } else {
                                            notApplicableDiscountArrayList.add(discount);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                } else if (discount.getTypeOfDiscount().equals("Percent")) {
                                    try {
                                        if (((Number) NumberFormat.getInstance().parse(finalBill)).intValue() >=
                                                ((Number) NumberFormat.getInstance().parse(discount.getMinmumBillAmount())).intValue()      ) {
                                            discountArrayList.add(discount);
                                        } else {
                                            notApplicableDiscountArrayList.add(discount);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    discountAdapter = new DiscountAdapter(DiscountActivity.this, discountArrayList);
                    discountAdapter.notifyDataSetChanged();
                    discountListview.setAdapter(discountAdapter);
                    if (discountAdapter != null) {
                        int totalHeight = 0;
                        for (int i = 0; i < discountAdapter.getCount(); i++) {
                            View listItem = discountAdapter.getView(i, null, discountListview);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }
                        ViewGroup.LayoutParams params = discountListview.getLayoutParams();
                        params.height = totalHeight + (discountListview.getDividerHeight() * (discountAdapter.getCount() - 1));
                        discountListview.setLayoutParams(params);
                        discountListview.requestLayout();
                        discountListview.setAdapter(discountAdapter);
                        discountAdapter.notifyDataSetChanged();
                    }
                    notApplicableDiscountsAdapter = new NotApplicableDiscountsAdapter(DiscountActivity.this, notApplicableDiscountArrayList);
                    notApplicableDiscountsAdapter.notifyDataSetChanged();
                    notApplicableListview.setAdapter(notApplicableDiscountsAdapter);
                    if (notApplicableDiscountsAdapter != null) {
                        int totalHeight = 0;
                        for (int i = 0; i < notApplicableDiscountsAdapter.getCount(); i++) {
                            View listItem = notApplicableDiscountsAdapter.getView(i, null, notApplicableListview);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }
                        ViewGroup.LayoutParams params = notApplicableListview.getLayoutParams();
                        params.height = totalHeight + (notApplicableListview.getDividerHeight() * (notApplicableDiscountsAdapter.getCount() - 1));
                        notApplicableListview.setLayoutParams(params);
                        notApplicableListview.requestLayout();
                        notApplicableListview.setAdapter(notApplicableDiscountsAdapter);
                        notApplicableDiscountsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        adminDiscountdataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    adminApplicableDiscountsArrayList.clear();
                    adminNotApplicableDiscountsArrayList.clear();
                    for (DataSnapshot discountSnap : dataSnapshot.getChildren()) {
                        adminDiscounts = discountSnap.getValue(AdminDiscounts.class);
                            if (adminDiscounts.getDiscountStatus().equals("Active")) {
                                if (adminDiscounts.getTypeOfDiscount().equals("Price")) {

                                    try {
                                        if (((Number) NumberFormat.getInstance().parse(finalBill)).intValue() >=
                                                ((Number) NumberFormat.getInstance().parse(adminDiscounts.getMinmumBillAmount())).intValue()      )  {
                                            adminApplicableDiscountsArrayList.add(adminDiscounts);
                                        } else {
                                            adminNotApplicableDiscountsArrayList.add(adminDiscounts);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                } else if (adminDiscounts.getTypeOfDiscount().equals("Percent")) {
                                    try {
                                        if (((Number) NumberFormat.getInstance().parse(finalBill)).intValue() >=
                                                ((Number) NumberFormat.getInstance().parse(adminDiscounts.getMinmumBillAmount())).intValue()      ) {
                                            adminApplicableDiscountsArrayList.add(adminDiscounts);
                                        } else {
                                            adminNotApplicableDiscountsArrayList.add(adminDiscounts);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    }
                    adminApplicableDicountAdapter = new AdminApplicableDicountAdapter(DiscountActivity.this, adminApplicableDiscountsArrayList);
                    adminApplicableDicountAdapter.notifyDataSetChanged();
                    adminApplicableDiscountsListview.setAdapter(adminApplicableDicountAdapter);
                    if (adminApplicableDicountAdapter != null) {
                        int totalHeight = 0;
                        for (int i = 0; i < adminApplicableDicountAdapter.getCount(); i++) {
                            View listItem = adminApplicableDicountAdapter.getView(i, null, adminApplicableDiscountsListview);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }
                        ViewGroup.LayoutParams params = adminApplicableDiscountsListview.getLayoutParams();
                        params.height = totalHeight + (adminApplicableDiscountsListview.getDividerHeight() * (adminApplicableDicountAdapter.getCount() - 1));
                        adminApplicableDiscountsListview.setLayoutParams(params);
                        adminApplicableDiscountsListview.requestLayout();
                        adminApplicableDiscountsListview.setAdapter(adminApplicableDicountAdapter);
                        adminApplicableDicountAdapter.notifyDataSetChanged();
                    }
                    adminnotApplicableDicountAdapter = new AdminnotApplicableDicountAdapter(DiscountActivity.this, adminNotApplicableDiscountsArrayList);
                    adminnotApplicableDicountAdapter.notifyDataSetChanged();
                    NotApplicableAdminDiscountListview.setAdapter(adminnotApplicableDicountAdapter);
                    if (adminnotApplicableDicountAdapter != null) {
                        int totalHeight = 0;
                        for (int i = 0; i < adminnotApplicableDicountAdapter.getCount(); i++) {
                            View listItem = adminnotApplicableDicountAdapter.getView(i, null, NotApplicableAdminDiscountListview);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }
                        ViewGroup.LayoutParams params = NotApplicableAdminDiscountListview.getLayoutParams();
                        params.height = totalHeight + (NotApplicableAdminDiscountListview.getDividerHeight() * (adminnotApplicableDicountAdapter.getCount() - 1));
                        NotApplicableAdminDiscountListview.setLayoutParams(params);
                        NotApplicableAdminDiscountListview.requestLayout();
                        NotApplicableAdminDiscountListview.setAdapter(adminnotApplicableDicountAdapter);
                        adminnotApplicableDicountAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        oneTimeDiscountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    onetimeDisocuntArrayList.clear();
                    for (DataSnapshot customerBasedSnap : dataSnapshot.getChildren()) {
                        OneTimeDiscount oneTimeDiscount = customerBasedSnap.getValue(OneTimeDiscount.class);

                        if (oneTimeDiscount.getCustomerId().equals(saved_id))
                        {
                            if (oneTimeDiscount.getUsedTag().equals("false")) {
                                if (oneTimeDiscount.getStoreId().equals(getStoreId)) {
                                    if (Integer.parseInt(finalBill) >= oneTimeDiscount.getTotalAmount()) {
                                        onetimeDisocuntArrayList.add(oneTimeDiscount);
                                    }
                                    oneTimeDiscountAdapter = new OneTimeDiscountAdapter(DiscountActivity.this, onetimeDisocuntArrayList);

                                    oneTimeDiscountAdapter.notifyDataSetChanged();
                                    customerBasedListview.setAdapter(oneTimeDiscountAdapter);
                                    if (oneTimeDiscountAdapter != null) {
                                        int totalHeight = 0;
                                        for (int i = 0; i < oneTimeDiscountAdapter.getCount(); i++) {
                                            View listItem = oneTimeDiscountAdapter.getView(i, null, customerBasedListview);
                                            listItem.measure(0, 0);
                                            totalHeight += listItem.getMeasuredHeight();
                                        }
                                        ViewGroup.LayoutParams params = customerBasedListview.getLayoutParams();
                                        params.height = totalHeight + (customerBasedListview.getDividerHeight() * (oneTimeDiscountAdapter.getCount() - 1));
                                        customerBasedListview.setLayoutParams(params);
                                        customerBasedListview.requestLayout();
                                        customerBasedListview.setAdapter(oneTimeDiscountAdapter);
                                        oneTimeDiscountAdapter.notifyDataSetChanged();
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

        customerBasedListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                oneTimeDiscount=onetimeDisocuntArrayList.get(i);
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra("StoreName", getStoreName);
                intent.putExtra("StoreId", getStoreId);
                intent.putExtra("categoryName", getCategoryName);
                intent.putExtra("categoryId", getCategoryId);
                intent.putExtra("pinCode", getPincode);
                intent.putExtra("addressPref", getAddressProof);
                intent.putExtra("FullAddress", getFullAddressFromMap);
                intent.putExtra("userLatitude", getLatuValue);
                intent.putExtra("userLongtitude", getLongValue);
                intent.putExtra("deliveryType", deliverytType);
                intent.putExtra("tips", String.valueOf(tipAmount));
                intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                intent.putExtra("instructionString", instructionString);
                intent.putExtra("storeAddress", storeAddress);
                intent.putExtra("shippingAddress", getShippingAddress);
                intent.putExtra("customerName", getcustomerName);
                intent.putExtra("customerMobilenumber", getMobileNumber);
                intent.putExtra("customerPinCode", getCustomerPincode);
                intent.putExtra("DiscountName", oneTimeDiscount.getCouponName());
                intent.putExtra("Typeofdiscount", "Price");
                intent.putExtra("DiscountPrice", oneTimeDiscount.getCouponAmount());
                intent.putExtra("DiscountMaxAmount", oneTimeDiscount.getTotalAmount());
                intent.putExtra("BillDiscountMaxAmount", 0);
                intent.putExtra("Preference","1");
                intent.putExtra("orderNumber",oneTimeDiscount.getId());
                intent.putExtra("dicountGivenBy",oneTimeDiscount.getDiscountGivenBy());
                startActivity(intent);
            }
        });

        adminApplicableDiscountsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adminDiscounts1 = adminApplicableDiscountsArrayList.get(i);
                int price = 0;
                int percentage = 0;
                int maximumBillAmount = 0;
                if (adminDiscounts1.getTypeOfDiscount().equals("Price")) {
                    if (!adminDiscounts1.getDiscountPrice().equals("") && adminDiscounts1.getDiscountPrice() != null) {
                        price = Integer.parseInt(adminDiscounts1.getDiscountPrice());
                    } else {
                        price = 0;
                    }
                } else {
                    if (!adminDiscounts1.getDiscountPercentageValue().equals("") && adminDiscounts1.getDiscountPercentageValue() != null) {
                        percentage = Integer.parseInt(adminDiscounts1.getDiscountPercentageValue());
                    } else {
                        percentage = 0;
                    }
                }
                if (!adminDiscounts1.getMinmumBillAmount().equals("") && adminDiscounts1.getMinmumBillAmount() != null) {
                    maximumBillAmount = Integer.parseInt(adminDiscounts1.getMinmumBillAmount());
                } else {
                    maximumBillAmount = 0;
                }
                if (adminDiscounts1.getTypeOfDiscount().equals("Price")) {
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra("StoreName", getStoreName);
                    intent.putExtra("StoreId", getStoreId);
                    intent.putExtra("categoryName", getCategoryName);
                    intent.putExtra("categoryId", getCategoryId);
                    intent.putExtra("pinCode", getPincode);
                    intent.putExtra("addressPref", getAddressProof);
                    intent.putExtra("FullAddress", getFullAddressFromMap);
                    intent.putExtra("userLatitude", getLatuValue);
                    intent.putExtra("userLongtitude", getLongValue);
                    intent.putExtra("deliveryType", deliverytType);
                    intent.putExtra("tips", String.valueOf(tipAmount));
                    intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                    intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("shippingAddress", getShippingAddress);
                    intent.putExtra("customerName", getcustomerName);
                    intent.putExtra("customerMobilenumber", getMobileNumber);
                    intent.putExtra("customerPinCode", getCustomerPincode);
                    intent.putExtra("DiscountName", adminDiscounts1.getDiscountName());
                    intent.putExtra("Typeofdiscount", "Price");
                    intent.putExtra("DiscountPrice", price);
                    intent.putExtra("DiscountMaxAmount", maximumBillAmount);
                    intent.putExtra("BillDiscountMaxAmount", 0);
                    intent.putExtra("Preference","2");
                    intent.putExtra("orderNumber","0");
                    intent.putExtra("dicountGivenBy",adminDiscounts1.getDiscountGivenBy());
                    startActivity(intent);
                } else if (adminDiscounts1.getTypeOfDiscount().equals("Percent")) {
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra("StoreName", getStoreName);
                    intent.putExtra("StoreId", getStoreId);
                    intent.putExtra("categoryName", getCategoryName);
                    intent.putExtra("categoryId", getCategoryId);
                    intent.putExtra("pinCode", getPincode);
                    intent.putExtra("addressPref", getAddressProof);
                    intent.putExtra("FullAddress", getFullAddressFromMap);
                    intent.putExtra("userLatitude", getLatuValue);
                    intent.putExtra("userLongtitude", getLongValue);
                    intent.putExtra("deliveryType", deliverytType);
                    intent.putExtra("tips", String.valueOf(tipAmount));
                    intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                    intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("shippingAddress", getShippingAddress);
                    intent.putExtra("customerName", getcustomerName);
                    intent.putExtra("customerMobilenumber", getMobileNumber);
                    intent.putExtra("customerPinCode", getCustomerPincode);
                    intent.putExtra("DiscountName", adminDiscounts1.getDiscountName());
                    intent.putExtra("Typeofdiscount", "Percent");
                    intent.putExtra("DiscountPrice", percentage);
                    intent.putExtra("DiscountMaxAmount", maximumBillAmount);
                    intent.putExtra("BillDiscountMaxAmount", adminDiscounts1.getMaxAmountForDiscount());
                    intent.putExtra("Preference","3");
                    intent.putExtra("orderNumber","0");
                    intent.putExtra("dicountGivenBy",adminDiscounts1.getDiscountGivenBy());
                    startActivity(intent);
                }
            }
        });
        discountListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                discount1 = discountArrayList.get(i);
                int price = 0;
                int percentage = 0;
                int maximumBillAmount = 0;
                if (discount1.getTypeOfDiscount().equals("Price")) {
                    if (!discount1.getDiscountPrice().equals("") && discount1.getDiscountPrice() != null) {
                        price = Integer.parseInt(discount1.getDiscountPrice());
                    } else {
                        price = 0;
                    }
                } else {
                    if (!discount1.getDiscountPercentageValue().equals("") && discount1.getDiscountPercentageValue() != null) {
                        percentage = Integer.parseInt(discount1.getDiscountPercentageValue());
                    } else {
                        percentage = 0;
                    }
                }
                if (!discount1.getMinmumBillAmount().equals("") && discount1.getMinmumBillAmount() != null) {
                    maximumBillAmount = Integer.parseInt(discount1.getMinmumBillAmount());
                } else {
                    maximumBillAmount = 0;
                }
                if (discount1.getTypeOfDiscount().equals("Price")) {
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra("StoreName", getStoreName);
                    intent.putExtra("StoreId", getStoreId);
                    intent.putExtra("categoryName", getCategoryName);
                    intent.putExtra("categoryId", getCategoryId);
                    intent.putExtra("pinCode", getPincode);
                    intent.putExtra("addressPref", getAddressProof);
                    intent.putExtra("FullAddress", getFullAddressFromMap);
                    intent.putExtra("userLatitude", getLatuValue);
                    intent.putExtra("userLongtitude", getLongValue);
                    intent.putExtra("deliveryType", deliverytType);
                    intent.putExtra("tips", String.valueOf(tipAmount));
                    intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                    intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("shippingAddress", getShippingAddress);
                    intent.putExtra("customerName", getcustomerName);
                    intent.putExtra("customerMobilenumber", getMobileNumber);
                    intent.putExtra("customerPinCode", getCustomerPincode);
                    intent.putExtra("DiscountName", discount1.getDiscountName());
                    intent.putExtra("Typeofdiscount", "Price");
                    intent.putExtra("DiscountPrice", price);
                    intent.putExtra("DiscountMaxAmount", maximumBillAmount);
                    intent.putExtra("BillDiscountMaxAmount", 0);
                    intent.putExtra("Preference","2");
                    intent.putExtra("orderNumber","0");
                    intent.putExtra("dicountGivenBy",discount1.getDiscountGivenBy());
                    startActivity(intent);
                } else if (discount1.getTypeOfDiscount().equals("Percent")) {
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra("StoreName", getStoreName);
                    intent.putExtra("StoreId", getStoreId);
                    intent.putExtra("categoryName", getCategoryName);
                    intent.putExtra("categoryId", getCategoryId);
                    intent.putExtra("pinCode", getPincode);
                    intent.putExtra("addressPref", getAddressProof);
                    intent.putExtra("FullAddress", getFullAddressFromMap);
                    intent.putExtra("userLatitude", getLatuValue);
                    intent.putExtra("userLongtitude", getLongValue);
                    intent.putExtra("deliveryType", deliverytType);
                    intent.putExtra("tips", String.valueOf(tipAmount));
                    intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                    intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("shippingAddress", getShippingAddress);
                    intent.putExtra("customerName", getcustomerName);
                    intent.putExtra("customerMobilenumber", getMobileNumber);
                    intent.putExtra("customerPinCode", getCustomerPincode);
                    intent.putExtra("DiscountName", discount1.getDiscountName());
                    intent.putExtra("Typeofdiscount", "Percent");
                    intent.putExtra("DiscountPrice", percentage);
                    intent.putExtra("DiscountMaxAmount", maximumBillAmount);
                    intent.putExtra("BillDiscountMaxAmount", discount1.getMaxAmountForDiscount());
                    intent.putExtra("Preference","3");
                    intent.putExtra("orderNumber","0");
                    intent.putExtra("dicountGivenBy",discount1.getDiscountGivenBy());
                    startActivity(intent);
                }
            }
        });

    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(DiscountActivity.this, HomePageAcitivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}