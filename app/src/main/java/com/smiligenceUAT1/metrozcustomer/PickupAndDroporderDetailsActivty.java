package com.smiligenceUAT1.metrozcustomer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.bean.OrderDetails;
import com.smiligenceUAT1.metrozcustomer.bean.PickUpAndDrop;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.util.ArrayList;

public class PickupAndDroporderDetailsActivty extends AppCompatActivity {
    TextView order_Date, order_Id, order_Total, type_Of_Payment, amount, shipping, wholeCharge, pickUpAddress, dropAddress, itemDetailsForPickup, baseFair, minimumFair, minimumFairTxt, order_status, orderTimeTxt, distance_txt, deliverPinTxt, noContactDelivery, anyInstructions, customerName, customerPhoneNumber;
    DatabaseReference databaseReference;
    private ArrayList<PickUpAndDrop> pickUpAndDropArrayList = new ArrayList<> ();
    ConstraintLayout orderDetailsLayout, paymentDetailsLayout, orderSummaryLayout, shippingLayout, itemDetailsLayout, specialinstructionLayout;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        Utils.getDatabase ();
        setContentView ( R.layout.activity_pickup_and_droporder_details_activty );

        bottomNavigation = findViewById ( R.id.drawerLayout );
        bottomNavigation.setSelectedItemId ( R.id.order );
        orderDetailsLayout = findViewById ( R.id.order_details_layout );
        order_Date = orderDetailsLayout.findViewById ( R.id.orderdate );
        order_Id = orderDetailsLayout.findViewById ( R.id.bill_num );
        order_Total = orderDetailsLayout.findViewById ( R.id.order_total );
        order_status = orderDetailsLayout.findViewById ( R.id.order_status );
        orderTimeTxt = orderDetailsLayout.findViewById ( R.id.ordertimetxt );
        deliverPinTxt = orderDetailsLayout.findViewById ( R.id.deliver_pin );


        databaseReference = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" );
        //Payment details
        paymentDetailsLayout = findViewById ( R.id.payment_details );
        type_Of_Payment = paymentDetailsLayout.findViewById ( R.id.type_of_payment );

        //Order summary
        orderSummaryLayout = findViewById ( R.id.cart_total_amount_layout );
        amount = orderSummaryLayout.findViewById ( R.id.tips_price1 );
        shipping = orderSummaryLayout.findViewById ( R.id.tips_price );
        wholeCharge = orderSummaryLayout.findViewById ( R.id.total_price );
        baseFair = orderSummaryLayout.findViewById ( R.id.base_fair_txt );
        minimumFair = orderSummaryLayout.findViewById ( R.id.minimumfairtxtres );
        minimumFairTxt = orderSummaryLayout.findViewById ( R.id.minimumfairtxt );
        distance_txt = orderSummaryLayout.findViewById ( R.id.distcnce_txt );


        //
        shippingLayout = findViewById ( R.id.start_end_address );
        pickUpAddress = shippingLayout.findViewById ( R.id.pickUpAddressTxt );
        dropAddress = shippingLayout.findViewById ( R.id.deliveryAddressTxt );
        customerName = shippingLayout.findViewById ( R.id.customerNameshipping );
        customerPhoneNumber = shippingLayout.findViewById ( R.id.customerMobileNumberShipping );

        //
        itemDetailsLayout = findViewById ( R.id.itemDetails );
        itemDetailsForPickup = itemDetailsLayout.findViewById ( R.id.full_name );

        specialinstructionLayout = findViewById ( R.id.special_instructions );

        noContactDelivery = findViewById ( R.id.noContactDelivery );
        anyInstructions = findViewById ( R.id.specialinsructions );


        String getOrderIdValue = getIntent ().getStringExtra ( "OrderidDetails" );


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

        final Query getitemDetailsQuery = databaseReference.orderByChild ( "orderId" ).equalTo ( String.valueOf ( getOrderIdValue ) );

        getitemDetailsQuery.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot postdataSnapshot : dataSnapshot.getChildren () ) {

                    OrderDetails orderDetails = postdataSnapshot.getValue ( OrderDetails.class );
                    pickUpAndDropArrayList = (ArrayList<PickUpAndDrop>) orderDetails.getPickUpAndDroplist ();
                    PickUpAndDrop pickUpAndDrop = pickUpAndDropArrayList.get ( 0 );
                    int basic_Fair_int = pickUpAndDrop.getBasic_Fair ();
                    int min_Fair_Txt = pickUpAndDrop.getMinimum_Fair ();
                    int per_Km_Txt = pickUpAndDrop.getPer_km ();
                    baseFair.setText ( "₹ " + basic_Fair_int );
                    if (pickUpAndDrop.getTotalDistance () > 5) {
                        minimumFairTxt.setText ( "Minimum Fair ₹7 Per Km" );
                        minimumFair.setText ( "₹ " + (per_Km_Txt * pickUpAndDrop.getTotalDistance ()) );
                    } else {
                        minimumFairTxt.setText ( "Minimum Fair less than 5 Kms" );
                        minimumFair.setText ( "₹ " + min_Fair_Txt );
                    }
                    if (pickUpAndDrop.getTotalDistance () > 0) {
                        distance_txt.setText ( pickUpAndDrop.getTotalDistance () + " Kms" );
                    } else {
                        distance_txt.setText ( pickUpAndDrop.getTotalDistance () + " Km" );
                    }
                    pickUpAddress.setText ( pickUpAndDrop.getPickupAddress () );
                    dropAddress.setText ( pickUpAndDrop.getDropAddress () );
                    for ( int i = 0; i < pickUpAndDrop.getDeliverObject ().size (); i++ ) {
                        itemDetailsForPickup.append ( pickUpAndDrop.getDeliverObject ().get ( i ) + " , " );
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        final Query getOrderDetails = databaseReference.orderByChild ( "orderId" ).equalTo ( getOrderIdValue );

        getOrderDetails.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () > 0) {
                    for ( DataSnapshot dataSnapshot1 : dataSnapshot.getChildren () ) {
                        OrderDetails orderDetails = dataSnapshot1.getValue ( OrderDetails.class );
                        order_Date.setText ( orderDetails.getPaymentDate() );
                        order_Id.setText ( orderDetails.getOrderId () );
                        order_Total.setText ( " ₹" + String.valueOf ( orderDetails.getPaymentamount () ) );
                        order_status.setText ( orderDetails.getOrderStatus () );
                        orderTimeTxt.setText ( orderDetails.getOrderTime () );
                        deliverPinTxt.setText ( orderDetails.getDeliverOtp () );

                        type_Of_Payment.setText ( orderDetails.getPaymentType () );
                        customerName.setText ( " " + orderDetails.getCustomerName () );
                        customerPhoneNumber.setText ( " " + orderDetails.getCustomerPhoneNumber () );

                        customerName.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_cus, 0, 0, 0 );
                        customerPhoneNumber.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_phonenumicon_01, 0, 0, 0 );

                        amount.setText ( " ₹" + String.valueOf ( orderDetails.getTotalAmount () ) );
                        shipping.setText ( " ₹" + String.valueOf ( orderDetails.getTipAmount () ) );
                        wholeCharge.setText ( " ₹" + String.valueOf ( orderDetails.getPaymentamount () ) );


                        if (orderDetails.getInstructionsToDeliveryBoy () != null && !"".equalsIgnoreCase ( orderDetails.getInstructionsToDeliveryBoy () )) {
                            anyInstructions.setText ( orderDetails.getInstructionsToDeliveryBoy () );
                        }
                        if (orderDetails.getDeliveryType () != null && !"".equals ( orderDetails.getDeliveryType () )) {
                            if (orderDetails.getDeliveryType().equalsIgnoreCase("No Contact Delivery")) {
                                setCompulsoryAsterisk();
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent ( PickupAndDroporderDetailsActivty.this, HomePageAcitivity.class );

        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    public void setCompulsoryAsterisk() {
        String txt_name = "No Contact Delivery";
        String colored = "*";
        SpannableStringBuilder strBuilder = new SpannableStringBuilder ();
        strBuilder.append ( txt_name );
        int start = strBuilder.length ();
        strBuilder.append ( colored );
        int end = strBuilder.length ();
        strBuilder.setSpan ( new ForegroundColorSpan ( Color.RED ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        noContactDelivery.setText ( strBuilder );
    }
}