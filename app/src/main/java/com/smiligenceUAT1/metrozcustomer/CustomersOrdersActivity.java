package com.smiligenceUAT1.metrozcustomer;

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
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.adapter.CustomerOrdersAdapter;
import com.smiligenceUAT1.metrozcustomer.bean.OrderDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Constant;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.TextUtils;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomersOrdersActivity extends AppCompatActivity {
    DatabaseReference orderDetailsRef, logindataRef;
    ExpandableListView orderList;
    ArrayList<OrderDetails> billArrayList = new ArrayList<OrderDetails>();
    List<String> header_list = new ArrayList<String>();
    List<String> phone_list = new ArrayList<String>();
    List<String> billDateList = new ArrayList<String>();
    CustomerOrdersAdapter receiptAdapter;
    int counter = 0;
    String billedDate;
    BottomNavigationView bottomNavigation;
    private BroadcastReceiver MyReceiver = new MyReceiver();
    List<OrderDetails> datewiseBillDetails = new ArrayList<>();
    Map<String, List<OrderDetails>> expandableBillDetail = new HashMap<>();
    String saved_id;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_customers_orders);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final SharedPreferences loginSharedPreferences1 = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences1.getString ( "customerId", "" );
        checkGPSConnection(getApplicationContext());
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        bottomNavigation = findViewById(R.id.bottomsheetlayout);
        bottomNavigation.setSelectedItemId(R.id.order);

        orderList = findViewById(R.id.orderList);
        logindataRef = CommonMethods.fetchFirebaseDatabaseReference("UserDetails");

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

        orderDetailsRef = CommonMethods.fetchFirebaseDatabaseReference(Constant.ORDER_DETAILS_FIREBASE_TABLE);

        Query storebasedQuery = orderDetailsRef.orderByChild("customerId").equalTo(saved_id);

        storebasedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot dateSnap : dataSnapshot.getChildren()) {
                        OrderDetails billDetails = dateSnap.getValue(OrderDetails.class);
                        phone_list.add(billDetails.getCustomerPhoneNumber());
                        billDateList.add(billDetails.getCustomerId());
                    }
                    TextUtils.removeDuplicatesList(phone_list);
                    TextUtils.removeDuplicatesList(billDateList);

                    for (int pincodeIterator = 0; pincodeIterator < billDateList.size(); pincodeIterator++) {
                        if (billDateList.get(pincodeIterator).equals(saved_id)) {
                            Query pincodeBaseQuery = orderDetailsRef.orderByChild("customerId").equalTo
                                    (billDateList.get(pincodeIterator));
                            pincodeBaseQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0)
                                    {
                                        header_list.clear();
                                        billArrayList.clear();
                                        counter = 0;

                                        for (DataSnapshot dateSnap : dataSnapshot.getChildren()) {

                                            OrderDetails billDetails = dateSnap.getValue(OrderDetails.class);
                                            billArrayList.add(billDetails);
                                            header_list.add(billDetails.getPaymentDate());
                                        }

                                        TextUtils.removeDuplicatesList(header_list);

                                        if (header_list.size() > 0) {

                                            Collections.reverse(header_list);

                                            for (int i = 0; i < header_list.size(); i++) {
                                                billedDate = header_list.get(i);
                                                for (int j = 0; j < billArrayList.size(); j++) {

                                                    OrderDetails billDetailsData = billArrayList.get(j);
                                                    if (billDetailsData.getPaymentDate()!=null && billedDate!=null && !billedDate.equals("")) {
                                                        if (billedDate.equalsIgnoreCase(billDetailsData.getPaymentDate())) {

                                                            datewiseBillDetails.add(billDetailsData);

                                                        }
                                                    }
                                                }


                                                if (datewiseBillDetails != null) {

                                                    Collections.reverse(datewiseBillDetails);
                                                    expandableBillDetail.put(header_list.get(counter), datewiseBillDetails);
                                                    counter++;
                                                    datewiseBillDetails = new ArrayList<>();
                                                }
                                            }
                                        }

                                        receiptAdapter = new CustomerOrdersAdapter(CustomersOrdersActivity.this,
                                                header_list, (HashMap<String, List<OrderDetails>>) expandableBillDetail);

                                        orderList.setAdapter(receiptAdapter);

                                        for (int i = 0; i < receiptAdapter.getGroupCount(); i++) {
                                            orderList.expandGroup(i);
                                        }
                                      orderList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                            @Override
                                            public boolean onChildClick(ExpandableListView parent, View v,
                                                                        int groupPosition, int childPosition, long id) {

                                                OrderDetails orderDetails = expandableBillDetail.get(header_list.get(groupPosition))
                                                        .get(childPosition);

                                        if (orderDetails.getCategoryTypeId().equals("1"))
                                        {
                                            Intent intent = new Intent ( CustomersOrdersActivity.this, ViewOrderActivity.class );
                                            intent.putExtra ( "OrderidDetails", String.valueOf(orderDetails.getOrderId()) );
                                            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                            startActivity ( intent );

                                        } else if (orderDetails.getCategoryTypeId().equals("2"))
                                        {
                                            Intent intent = new Intent ( CustomersOrdersActivity.this, PickupAndDroporderDetailsActivty.class );
                                            intent.putExtra ( "OrderidDetails", String.valueOf(orderDetails.getOrderId())  );
                                            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                            startActivity ( intent );
                                        }
                                                return false;
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(CustomersOrdersActivity.this, HomePageAcitivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void checkGPSConnection(Context context) {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}