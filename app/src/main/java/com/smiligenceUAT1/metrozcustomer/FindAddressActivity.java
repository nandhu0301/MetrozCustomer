package com.smiligenceUAT1.metrozcustomer;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.skyfishjy.library.RippleBackground;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.FetchAddressIntentService;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.SimplePlacePicker;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.PICKUP_DROP_TABLE;

public class FindAddressActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    //location
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    FloatingActionButton floatingActionButton;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    Geocoder geocoder;
    String pincode;
    private View mapView;
    private RippleBackground rippleBg;
    private TextView mDisplayAddressTextView;
    private ProgressBar mProgressBar;
    private ImageView mSmallPinIv;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    DatabaseReference userCurrentLocationDetails;
    //variables
    private String addressOutput;
    private int addressResultCode;
    private boolean isSupportedArea;
    private LatLng currentMarkerPosition;
    Button searchImage;
    EditText addressEditText;

    ImageView backtohome;
    List<Address> addresses;
    private String mApiKey = "AIzaSyA8lfmAWvLMIXYYu25eY-NNUTrF7TcIUvM";
    private String[] mSupportedArea = new String[]{};
    private String mCountry = "India";
    private String mLanguage = "en";
    private Double latValue = 0.00;
    private Double longValue = 0.00;
    private static final int Request_User_Location_Code = 100;
    LatLng latLng;
    Button zoomIn, zoomOut;
    String addressField;
    DatabaseReference ref;
    String getAddressType,getAdType;
    DatabaseReference shippingDataBaseRef;
    private static final String TAG = MainActivity.class.getSimpleName ();
    Button submitLocationButton;
    private BroadcastReceiver MyReceiver = new MyReceiver ();
    int receivedChildCountValue;
    double roundOff = 0.0,getStoreLatitude, getStoreLongtitude;

    private String areaAddress, houseAddress, city, state;
    String resultOrderId,storeAddress,instructionString, paymentType, amount,deliverytType,receiptNumber,tipAmount, finalBill_tip, finalBill;
    String getFullAdreesFromMap,getUserLatitudeFromMap,getUserLongtitudeFromMap;
    AlertDialog alertDialog;
    AlertDialog.Builder dialog;
    String saved_id;
    String adType;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        Utils.getDatabase ();
        setContentView ( R.layout.activity_find_address );


        checkGPSConnection ( getApplicationContext () );
        final SharedPreferences loginSharedPreferences1 = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences1.getString ( "customerId", "" );

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );




        gpsFunction();
        checkPermission ();
        getAddressType = getIntent ().getStringExtra ( "currentAddress" );
        getAdType=getIntent ().getStringExtra ( "automatic" );

        isLocationEnabled ();
        initViews ();
        receiveIntent ();
        initMapsAndPlaces ();
        getDeviceLocation ();


    }


    private void initViews() {
        geocoder = new Geocoder ( this, Locale.getDefault () );
        searchImage = findViewById ( R.id.searchimage );
        addressEditText = findViewById ( R.id.searchBar );
        submitLocationButton = findViewById ( R.id.submit_location_button );
        rippleBg = findViewById ( R.id.ripple_bg );
        mDisplayAddressTextView = findViewById ( R.id.tv_display_marker_location );
        mProgressBar = findViewById ( R.id.progress_bar );
        mSmallPinIv = findViewById ( R.id.small_pin );
        floatingActionButton = findViewById ( R.id.myLocationButton );
        zoomIn = findViewById ( R.id.zoomin );
        zoomOut = findViewById ( R.id.zoomout );
        userCurrentLocationDetails = CommonMethods.fetchFirebaseDatabaseReference ( "CustomerLoginDetails" ).child ( String.valueOf ( saved_id ) );
        shippingDataBaseRef = CommonMethods.fetchFirebaseDatabaseReference("ShippingAddress").child(String.valueOf(saved_id));
        backtohome = findViewById ( R.id.backtohomefromgps );




        instructionString = getIntent ().getStringExtra ( "instructionString" );
        deliverytType = getIntent ().getStringExtra ( "deliveryType" );
        tipAmount = getIntent ().getStringExtra ( "tips" );
        finalBill_tip = getIntent ().getStringExtra ( "finalBillTip" );
        finalBill = getIntent ().getStringExtra ( "finalBillAmount" );
        storeAddress = getIntent ().getStringExtra ( "storeAddress" );
        receivedChildCountValue=getIntent().getIntExtra("childCountValue",0);
        getStoreLatitude = getIntent ().getDoubleExtra ( "storeLatitude", 0.0 );
        getStoreLongtitude = getIntent ().getDoubleExtra ( "storeLongitude", 0.0 );



        backtohome.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (getAddressType.equals ( "CurrentLocation" ))
                {
                    Intent intent = new Intent ( FindAddressActivity.this, HomePageAcitivity.class );

                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                } else if (getAddressType.equals("ShippingAddress"))
                {
                    Intent intent = new Intent ( FindAddressActivity.this, AddNewAddressActivity.class );
                    intent.putExtra("deliveryType", deliverytType);
                    intent.putExtra("tips", String.valueOf(tipAmount));
                    intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                    intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                    intent.putExtra("instructionString", instructionString);
                    intent.putExtra("storeAddress", storeAddress);
                    intent.putExtra("childCountValue", receivedChildCountValue);
                    intent.putExtra ( "storeLatitude", getStoreLatitude );
                    intent.putExtra ( "storeLongitude", getStoreLongtitude );
                    intent.putExtra("FullAddress",mDisplayAddressTextView.getText().toString());
                    intent.putExtra("userLatitude",latValue);
                    intent.putExtra("userLongtitude",longValue);
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                } else if (getAddressType.equals("Splash"))
                {

                    Intent intent = new Intent(FindAddressActivity.this, Current_Location_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent ( FindAddressActivity.this, HomePageAcitivity.class );
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                }
            }
        } );
        final View icPin = findViewById ( R.id.ic_pin );
        new Handler ().postDelayed ( new Runnable () {
            @Override
            public void run() {
                revealView ( icPin );
            }
        }, 1000 );

        submitLocationButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                    if (mDisplayAddressTextView.getText().toString().equalsIgnoreCase("No address found"))
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            checkPermission ();
                        }
                        isLocationEnabled();
                        initViews();
                        receiveIntent();
                        initMapsAndPlaces();
                        getDeviceLocation();
                    }else {
                        submitResultLocation();

                        if (getAddressType.equals("pickupAddress")) {
                            DatabaseReference currentAddressRef = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);
                            currentAddressRef.child("pickupAddress").setValue(mDisplayAddressTextView.getText().toString());
                            currentAddressRef.child("dropAddress").setValue("");
                            currentAddressRef.child("startPickupLatitude").setValue(latValue);
                            currentAddressRef.child("endPickupLongtitude").setValue(longValue);

                            Intent intent = new Intent(FindAddressActivity.this, PickAndDropActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (getAddressType.equals("dropAddress")) {
                            DatabaseReference dropAddressRef = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);
                            dropAddressRef.child("dropAddress").setValue(mDisplayAddressTextView.getText().toString());
                            dropAddressRef.child("startDeliveryLatitude").setValue(latValue);
                            dropAddressRef.child("endDeliveryLongtitude").setValue(longValue);

                            Intent intent = new Intent(FindAddressActivity.this, PickAndDropActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (getAddressType.equals("CurrentLocation")||getAddressType.equals("Splash")) {

                            ref = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails")
                                    .child(String.valueOf(saved_id));

                            ref.child("currentAddress").setValue(mDisplayAddressTextView.getText().toString());
                            ref.child("userLatitude").setValue(latValue);
                            ref.child("userLongtitude").setValue(longValue);

                            if (pincode != null && !"".equals(pincode)) {
                                ref.child("currentPincode").setValue(pincode);
                            } else {
                                Toast.makeText(FindAddressActivity.this, "Make sure your location is on", Toast.LENGTH_SHORT).show();
                            }

                            if (getAdType != null && !getAdType.equals("")) {
                                Intent intent = new Intent(FindAddressActivity.this, HomePageAcitivity.class);
                                intent.putExtra("AdFromLogin", "true");
                                intent.putExtra("automatic", getAdType);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(FindAddressActivity.this, HomePageAcitivity.class);
                                intent.putExtra("AdFromLogin", "true");
                                intent.putExtra("automatic", getAdType);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                        } else if (getAddressType.equals("ShippingAddress")) {

                            Intent intent = new Intent(FindAddressActivity.this, AddNewAddressActivity.class);
                            intent.putExtra("deliveryType", deliverytType);
                            intent.putExtra("tips", String.valueOf(tipAmount));
                            intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
                            intent.putExtra("finalBillAmount", String.valueOf(finalBill));
                            intent.putExtra("instructionString", instructionString);
                            intent.putExtra("storeAddress", storeAddress);
                            intent.putExtra("storeLatitude", getStoreLatitude);
                            intent.putExtra("storeLongitude", getStoreLongtitude);
                            intent.putExtra("FullAddress", mDisplayAddressTextView.getText().toString());
                            intent.putExtra("userLatitude", latValue);
                            intent.putExtra("userLongtitude", longValue);
                            intent.putExtra("currentAddress", "ShippingAddress");
                            intent.putExtra("childCountValue", receivedChildCountValue);
                            intent.putExtra("shippingAddressPinCode", pincode);
                            intent.putExtra("shippingAddressState", state);
                            intent.putExtra("shippingAddressCity", city);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }



            }
        } );

    }

    private void receiveIntent() {
        Intent intent = getIntent ();

        if (intent.hasExtra ( SimplePlacePicker.API_KEY )) {
            mApiKey = intent.getStringExtra ( SimplePlacePicker.API_KEY );
        }

        if (intent.hasExtra ( SimplePlacePicker.COUNTRY )) {
            mCountry = intent.getStringExtra ( SimplePlacePicker.COUNTRY );
        }

        if (intent.hasExtra ( SimplePlacePicker.LANGUAGE )) {
            mLanguage = intent.getStringExtra ( SimplePlacePicker.LANGUAGE );
        }

        if (intent.hasExtra ( SimplePlacePicker.SUPPORTED_AREAS )) {
            mSupportedArea = intent.getStringArrayExtra ( SimplePlacePicker.SUPPORTED_AREAS );
        }
    }

    private void initMapsAndPlaces() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient ( this );
        getDeviceLocation ();
        Places.initialize ( this, mApiKey );
        placesClient = Places.createClient ( this );
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance ();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                .findFragmentById ( R.id.map_fragment );
        mapFragment.getMapAsync ( this );
        mapView = mapFragment.getView ();


        floatingActionButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                checkPermission();
                getDeviceLocation();
                rippleBg.startRippleAnimation ();

            }
        } );

        zoomIn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                mMap.animateCamera ( CameraUpdateFactory.zoomIn () );
            }
        } );

        zoomOut.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                mMap.animateCamera ( CameraUpdateFactory.zoomOut () );
            }
        } );

        searchImage.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                addressField = addressEditText.getText ().toString ();
                MarkerOptions userMarkerOptions = new MarkerOptions ();
                if (!TextUtils.isEmpty ( addressField )) {
                    geocoder = new Geocoder ( FindAddressActivity.this );

                    try {
                        addresses = geocoder.getFromLocationName ( addressField, 6 );

                        if (addresses == null) {
                            Toast.makeText ( FindAddressActivity.this, "Make sure your location is on", Toast.LENGTH_SHORT ).show ();
                        } else if (addresses != null) {
                            for ( int i = 0; i < addresses.size (); i++ ) {
                                Address userAddress = addresses.get ( i );
                                latValue = userAddress.getLatitude ();
                                longValue = userAddress.getLongitude ();
                                latLng = new LatLng ( userAddress.getLatitude (), userAddress.getLongitude () );
                                if (addresses != null) {
                                    for ( int k = 0; k < addresses.size (); k++ ) {
                                        pincode = addresses.get ( 0 ).getPostalCode ();
                                        areaAddress = addresses.get ( 0 ).getSubLocality ();
                                        houseAddress = addresses.get ( 0 ).getSubLocality ();
                                        city = addresses.get ( 0 ).getSubAdminArea ();
                                        state = addresses.get ( 0 ).getAdminArea ();
                                    }
                                    // shippingAddressDetails ( addresses );
                                }
                                userMarkerOptions.position ( latLng );

                                userMarkerOptions.icon ( BitmapDescriptorFactory.defaultMarker ( BitmapDescriptorFactory.HUE_RED ) );
                                mMap.addMarker ( userMarkerOptions );
                                mMap.moveCamera ( CameraUpdateFactory.newLatLng ( latLng ) );
                                mMap.animateCamera ( CameraUpdateFactory.zoomTo ( 14 ) );
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace ();
                    }
                } else {
                    Toast.makeText ( FindAddressActivity.this, "Please Select Location", Toast.LENGTH_LONG ).show ();
                }
            }
        } );


    }

    private void submitResultLocation() {
        if (addressResultCode == SimplePlacePicker.FAILURE_RESULT || !isSupportedArea) {
            Toast.makeText ( FindAddressActivity.this, "failed", Toast.LENGTH_SHORT ).show ();
        } else {
            Intent data = new Intent ();
            data.putExtra ( SimplePlacePicker.SELECTED_ADDRESS, addressOutput );
            data.putExtra ( SimplePlacePicker.LOCATION_LAT_EXTRA, currentMarkerPosition.latitude );
            data.putExtra ( SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition.longitude );
            setResult ( RESULT_OK, data );
        }
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
        return locationManager.isProviderEnabled ( LocationManager.GPS_PROVIDER ) || locationManager.isProviderEnabled (
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = 21f; //This goes up to 21
        mMap.getUiSettings ().setMyLocationButtonEnabled ( true );
        mMap.getUiSettings ().setCompassEnabled ( true );

        if (mapView != null && mapView.findViewById ( Integer.parseInt ( "1" ) ) != null) {
            View locationButton = ((View) mapView.findViewById ( Integer.parseInt ( "1" ) ).getParent ()).findViewById ( Integer.parseInt ( "2" ) );
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams ();
            layoutParams.addRule ( RelativeLayout.ALIGN_PARENT_TOP, 0 );
            layoutParams.addRule ( RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE );
            layoutParams.setMargins ( 0, 0, 60, 500 );
        }

        LocationRequest locationRequest = LocationRequest.create ();
        locationRequest.setInterval ( 1000 );
        locationRequest.setFastestInterval ( 5000 );
        locationRequest.setPriority ( LocationRequest.PRIORITY_HIGH_ACCURACY );

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder ().addLocationRequest ( locationRequest );

        SettingsClient settingsClient = LocationServices.getSettingsClient ( this );
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings ( builder.build () );


        task.addOnSuccessListener ( new OnSuccessListener<LocationSettingsResponse> () {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation ();
            }
        } );

        task.addOnFailureListener ( new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult ( FindAddressActivity.this, 51 );
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace ();
                    }
                }
            }
        } );

        mMap.setOnCameraIdleListener ( new GoogleMap.OnCameraIdleListener () {
            @Override
            public void onCameraIdle() {
                mSmallPinIv.setVisibility ( View.GONE );
                mProgressBar.setVisibility ( View.VISIBLE );
                Log.i ( TAG, "changing address" );
                try {
                    startIntentService ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation ();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            checkPermission();
            if (isLocationEnabled ()) {

                mFusedLocationProviderClient.getLastLocation().addOnCompleteListener
                        (
                                new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {

                                        Location location = task.getResult();
                                        if (location != null && !"".equals(location)) {

                                            try {
                                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1000);
                                                latValue = location.getLatitude();
                                                longValue = location.getLongitude();
                                                float zoomLevel = 22f; //This goes up to 21

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));

                                                        if (addresses != null) {
                                                            for (int i = 0; i < addresses.size(); i++) {
                                                                Address userAddress = addresses.get(i);
                                                                latValue = userAddress.getLatitude();
                                                                longValue = userAddress.getLongitude();
                                                                latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
                                                                if (addresses != null) {
                                                                    for (int k = 0; k < addresses.size(); k++) {
                                                                        pincode = addresses.get(0).getPostalCode();
                                                                        areaAddress = addresses.get(0).getPremises();
                                                                        houseAddress = addresses.get(0).getSubLocality();
                                                                        city = addresses.get(0).getSubAdminArea();
                                                                        state = addresses.get(0).getAdminArea();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }, 500);

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
            }

        }
    }

    protected void startIntentService() throws IOException {

        rippleBg.startRippleAnimation ();
        new Handler ().postDelayed ( new Runnable () {
            @Override
            public void run() {
                rippleBg.stopRippleAnimation ();
            }
        }, 2000 );
        currentMarkerPosition = mMap.getCameraPosition ().target;
        FindAddressActivity.AddressResultReceiver resultReceiver = new FindAddressActivity.AddressResultReceiver ( new Handler () );

        Intent intent = new Intent ( this, FetchAddressIntentService.class );
        intent.putExtra ( SimplePlacePicker.RECEIVER, resultReceiver );
        intent.putExtra ( SimplePlacePicker.LOCATION_LAT_EXTRA, currentMarkerPosition.latitude );
        intent.putExtra ( SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition.longitude );
        latValue = currentMarkerPosition.latitude;
        longValue = currentMarkerPosition.longitude;

        addresses = geocoder.getFromLocation ( latValue, longValue, 1000 );


        if (addresses != null) {
            for ( int i = 0; i < addresses.size (); i++ ) {
                Address userAddress = addresses.get ( i );
                latLng = new LatLng ( userAddress.getLatitude (), userAddress.getLongitude () );
                pincode = addresses.get ( 0 ).getPostalCode ();
                pincode = userAddress.getPostalCode ();
                if (addresses != null) {
                    for ( int k = 0; k < addresses.size (); k++ ) {
                        pincode = addresses.get ( 0 ).getPostalCode ();
                        areaAddress = addresses.get ( 0 ).getPremises ();
                        houseAddress = addresses.get ( 0 ).getSubLocality ();
                        city = addresses.get ( 0 ).getSubAdminArea ();
                        state = addresses.get ( 0 ).getAdminArea ();

                    }

                }

            }
        }


        intent.putExtra ( SimplePlacePicker.LANGUAGE, mLanguage );
        startService ( intent );
    }

    private void updateUi() {

        mDisplayAddressTextView.setVisibility ( View.VISIBLE );
        mProgressBar.setVisibility ( View.GONE );
        mMap.clear ();

        if (addressResultCode == SimplePlacePicker.SUCCESS_RESULT) {
            //check for supported area
            if (isSupportedArea ( mSupportedArea )) {
                //supported
                addressOutput = addressOutput.replace ( "Unnamed Road,", "" );
                addressOutput = addressOutput.replace ( "Unnamed Road،", "" );
                addressOutput = addressOutput.replace ( "Unnamed Road New,", "" );
                mSmallPinIv.setVisibility ( View.VISIBLE );
                isSupportedArea = true;
                mDisplayAddressTextView.setText ( addressOutput );
            } else {
                //not supported
                mSmallPinIv.setVisibility ( View.GONE );
                isSupportedArea = false;
                mDisplayAddressTextView.setText ( getString ( R.string.not_support_area ) );
            }
        } else if (addressResultCode == SimplePlacePicker.FAILURE_RESULT) {
            mSmallPinIv.setVisibility ( View.GONE );
            mDisplayAddressTextView.setText ( addressOutput );
        }
    }

    private boolean isSupportedArea(String[] supportedAreas) {
        if (supportedAreas.length == 0)
            return true;

        boolean isSupported = false;
        for ( String area : supportedAreas ) {
            if (addressOutput.contains ( area )) {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }

    private void revealView(View view) {
        int cx = view.getWidth () / 2;
        int cy = view.getHeight () / 2;
        float finalRadius = (float) Math.hypot ( cx, cy );
        Animator anim = ViewAnimationUtils.createCircularReveal ( view, cx, cy, 0, finalRadius );
        view.setVisibility ( View.VISIBLE );
        anim.start ();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED
        ) {//Can add more as per requirement

            ActivityCompat.requestPermissions ( this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123 );

        }
        return;
    }

    @Override
    public void onClick(View view) {

        gpsFunction();
        checkPermission();
        rippleBg.startRippleAnimation ();
        new Handler ().postDelayed ( new Runnable () {
            @Override
            public void run() {
                rippleBg.stopRippleAnimation ();
            }
        }, 2000 );
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super ( handler );
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            addressResultCode = resultCode;
            if (resultData == null) {
                return;
            }

            addressOutput = resultData.getString ( SimplePlacePicker.RESULT_DATA_KEY );
            if (addressOutput == null) {
                addressOutput = "";
            }
            updateUi ();
        }
    }

    @Override
    public void onBackPressed() {
        if (getAddressType.equals ( "CurrentLocation" ))
        {
            Intent intent = new Intent ( FindAddressActivity.this, HomePageAcitivity.class );

            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (getAddressType.equals("ShippingAddress"))
        {
            Intent intent = new Intent ( FindAddressActivity.this, AddNewAddressActivity.class );
            intent.putExtra("deliveryType", deliverytType);
            intent.putExtra("tips", String.valueOf(tipAmount));
            intent.putExtra("finalBillTip", String.valueOf(finalBill_tip));
            intent.putExtra("finalBillAmount", String.valueOf(finalBill));
            intent.putExtra("instructionString", instructionString);
            intent.putExtra("storeAddress", storeAddress);
            intent.putExtra("childCountValue", receivedChildCountValue);
            intent.putExtra ( "storeLatitude", getStoreLatitude );
            intent.putExtra ( "storeLongitude", getStoreLongtitude );
            intent.putExtra("FullAddress",mDisplayAddressTextView.getText().toString());
            intent.putExtra("userLatitude",latValue);
            intent.putExtra("userLongtitude",longValue);
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (getAddressType.equals("Splash"))
        {
            Intent intent = new Intent(FindAddressActivity.this, Current_Location_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent ( FindAddressActivity.this, HomePageAcitivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }
    }

    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled ( LocationManager.GPS_PROVIDER );


    }
    @Override
    public void onResume() {
        super.onResume();
        getDeviceLocation();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(String permission: permissions)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                if (!ActivityCompat.shouldShowRequestPermissionRationale(FindAddressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
                    // alertDialog = dialog.create();
                    //alertDialog.show();
                    dialog = new AlertDialog.Builder(FindAddressActivity.this);
                    dialog.setTitle("Permission Required");
                    dialog.setCancelable(true);
                    dialog.setMessage("You have to Allow permission to access user location");

                    dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                    FindAddressActivity.this.getPackageName(), null));
                            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(i, 1001);
                        }
                    });
                    alertDialog = dialog.create();
                    alertDialog.show();

                }

                Log.e("set to never ask again", permission);

            }else{
                if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                    //allowed
                    Log.e("allowed", permission);

                } else{
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(FindAddressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
                       // alertDialog = dialog.create();
                        //alertDialog.show();
                        dialog = new AlertDialog.Builder(FindAddressActivity.this);
                        dialog.setTitle("Permission Required");
                        dialog.setCancelable(true);
                        dialog.setMessage("You have to Allow permission to access user location");

                        dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                        FindAddressActivity.this.getPackageName(), null));
                                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(i, 1001);
                            }
                        });
                        alertDialog = dialog.create();
                        alertDialog.show();

                    }

                    Log.e("set to never ask again", permission);
                    //do something here.
                }
            }
        }
    }
    public void gpsFunction()
    {
        LocationManager lm = (LocationManager)FindAddressActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        AlertDialog alert = null;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            getDeviceLocation();
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            getDeviceLocation();
        } catch(Exception ex) {}

        if(!gps_enabled || !network_enabled) {
            // notify user
            alert= new AlertDialog.Builder(FindAddressActivity.this)
                    .setMessage("Please Make sure your GPS is on")
                    .setCancelable(true)
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            FindAddressActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel",null)
                    .show();
        }

    }
}
