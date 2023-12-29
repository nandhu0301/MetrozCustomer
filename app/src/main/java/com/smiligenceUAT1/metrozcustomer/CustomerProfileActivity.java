package com.smiligenceUAT1.metrozcustomer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.bean.CustomerDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.Utils;



public class CustomerProfileActivity extends AppCompatActivity {
    ImageView backToHome;
    DatabaseReference databaseReference;
    TextView customerName, customerPhoneNumber, customerEmail, customerAddress;

    CustomerDetails customerDetails;
    String customerAddressIntent;
    ImageView logout;
    TextView whatsapptext;



    private BroadcastReceiver MyReceiver = new MyReceiver ();
    String saved_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        Utils.getDatabase ();
        setContentView ( R.layout.activity_customer_profile );
        whatsapptext=findViewById(R.id.whatsapptext);


        checkGPSConnection ( getApplicationContext () );
        registerReceiver ( MyReceiver, new IntentFilter ( ConnectivityManager.CONNECTIVITY_ACTION ) );

        backToHome = findViewById ( R.id.backto_home );
        customerName = findViewById ( R.id.customername );
        customerPhoneNumber = findViewById ( R.id.customernumber );
        customerEmail = findViewById ( R.id.customeremail );
        customerAddress = findViewById ( R.id.cutomerAddress );
        logout = findViewById ( R.id.logout );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        customerAddress.setSelected ( true );
        databaseReference = CommonMethods.fetchFirebaseDatabaseReference ( "CustomerLoginDetails" );

        customerAddressIntent = getIntent ().getStringExtra ( "userAddress" );

        final SharedPreferences loginSharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences.getString ( "customerId", "" );

        Query query = databaseReference.orderByChild ( "customerId" ).equalTo ( saved_id );

        query.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount () > 0) {
                    for ( DataSnapshot customerSnap : dataSnapshot.getChildren () ) {
                        customerDetails = customerSnap.getValue ( CustomerDetails.class );
                    }
                    customerName.setText ( "" + customerDetails.getFullName () );
                    customerPhoneNumber.setText ( "" + customerDetails.getCustomerPhoneNumber () );
                    customerEmail.setText ( "" + customerDetails.getEmailId () );
                    customerAddress.setText ( "" + customerDetails.getCurrentAddress () );
                    customerAddress.setSelected ( true );

                    customerAddress.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_location_01, 0, 0, 0 );
                    customerName.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_cus, 0, 0, 0 );
                    customerEmail.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_mailicon_01, 0, 0, 0 );
                    customerPhoneNumber.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_phonenumicon_01, 0, 0, 0 );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        whatsapptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=" + whatsapptext.getText ().toString ();
                Intent i = new Intent ( Intent.ACTION_VIEW );
                i.setData ( Uri.parse ( url ) );
                startActivity ( i );
            }
        });


        backToHome.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent ( CustomerProfileActivity.this, HomePageAcitivity.class );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        } );

        logout.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {


                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( CustomerProfileActivity.this );
                bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
                Button logout = bottomSheetDialog.findViewById ( R.id.logout );
                Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );

                bottomSheetDialog.show ();
                bottomSheetDialog.setCancelable ( false );

                logout.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences myPrefs = getSharedPreferences ( "LOGIN",
                                MODE_PRIVATE );
                        SharedPreferences.Editor editor = myPrefs.edit ();
                        editor.clear ();
                        editor.commit ();
                        Intent intent = new Intent ( CustomerProfileActivity.this,
                                LoginActivity.class );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                        startActivity ( intent );
                        bottomSheetDialog.dismiss ();
                    }
                } );
                stayinapp.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss ();
                    }
                } );


            }
        } );
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent ( CustomerProfileActivity.this, HomePageAcitivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled ( LocationManager.GPS_PROVIDER );

    }
}