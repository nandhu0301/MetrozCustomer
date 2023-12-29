package com.smiligenceUAT1.metrozcustomer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
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
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.RazorpayException;
import com.skyfishjy.library.RippleBackground;
import com.smiligenceUAT1.metrozcustomer.bean.MetrozStoreTime;
import com.smiligenceUAT1.metrozcustomer.bean.StoreTimings;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.FetchAddressIntentService;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.SimplePlacePicker;
import com.smiligenceUAT1.metrozcustomer.common.SplashActivity;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.PICKUP_DROP_TABLE;

public class Current_Location_Activity extends AppCompatActivity implements  View.OnClickListener {
    //location

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    Geocoder geocoder;
    String pincode;
    private View mapView;
    private String areaAddress, houseAddress, city, state;

    //variables
    private String addressOutput;
    private int addressResultCode;
    private boolean isSupportedArea;
    private LatLng currentMarkerPosition;


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

    AlertDialog alertDialog;
    AlertDialog.Builder dialog;
    private static final String TAG = MainActivity.class.getSimpleName ();
    Button submitLocationButton,pickLocationFromMap;
    private BroadcastReceiver MyReceiver = new MyReceiver();
    int receivedChildCountValue;

    TextView locationText;
     String saved_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        Utils.getDatabase ();
        setContentView ( R.layout.activity_current_location );
        submitLocationButton = findViewById ( R.id.submit_location_button );
        pickLocationFromMap=findViewById(R.id.pick_location_button);
        locationText=findViewById(R.id.locationText);
        final SharedPreferences loginSharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences.getString ( "customerId", "" );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission ();
        }
        isLocationEnabled ();
        initViews ();
        receiveIntent ();


        if ((!CommonMethods.getConnectivityStatusString(getApplicationContext()).equals("No internet is available"))) {

            getDeviceLocation ();

        } else {
            new SweetAlertDialog(Current_Location_Activity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("No Network Connection")
                    .show();

        }

        pickLocationFromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent ( Current_Location_Activity.this,
                        FindAddressActivity.class );
                intent.putExtra ( "currentAddress", "Splash" );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        });
        submitLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((!CommonMethods.getConnectivityStatusString(getApplicationContext()).equals("No internet is available"))) {

                    if (pincode == null || "".equals(pincode) || locationText.getText().toString()==null || locationText.getText().toString().equals("") ) {

                        dialog = new AlertDialog.Builder(Current_Location_Activity.this);
                        dialog.setTitle("Permission Required");
                        dialog.setCancelable(true);
                        dialog.setMessage("You have to Allow permission to access user location");

                        dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                        Current_Location_Activity.this.getPackageName(), null));
                                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(i, 1001);
                            }
                        });
                        alertDialog = dialog.create();
                        alertDialog.show();
                    }
                    else
                    {
                        DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails")
                                .child(String.valueOf(saved_id));
                        ref.child("currentAddress").setValue(locationText.getText().toString());
                        ref.child("userLatitude").setValue(latValue);
                        ref.child("userLongtitude").setValue(longValue);

                        ref.child("currentPincode").setValue(pincode);

                        Intent intent = new Intent ( Current_Location_Activity.this,
                                HomePageAcitivity.class );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( intent );

                       // overridePendingTransition(R.anim.slide_in_up, 0);
                    }

                } else {
                    new SweetAlertDialog(Current_Location_Activity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No Network Connection")
                            .show();

                }


            }
        });
    }


    private void initViews() {
        geocoder = new Geocoder ( this, Locale.getDefault () );







        receivedChildCountValue=getIntent().getIntExtra("childCountValue",0);

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





    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
        return locationManager.isProviderEnabled ( LocationManager.GPS_PROVIDER ) || locationManager.isProviderEnabled (
                LocationManager.NETWORK_PROVIDER
        );
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
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient ( this );

        if (ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {



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
                                                locationText.setText(addresses.get(0).getAddressLine(0));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
            }

        }
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
        getDeviceLocation ();
    }





    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled ( LocationManager.GPS_PROVIDER );


    }
    @Override
    public void onResume() {
        super.onResume ();
        gpsFunction();

        getDeviceLocation();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(String permission: permissions)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                //denied
                checkPermission();
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();

            }else{
                if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                    //allowed
                    Log.e("allowed", permission);
                    getDeviceLocation();;

                } else{
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(Current_Location_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                        dialog = new AlertDialog.Builder(Current_Location_Activity.this);
                        dialog.setTitle("Permission Required");
                        dialog.setCancelable(true);
                        dialog.setMessage("You have to Allow permission to access user location");

                        dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                        Current_Location_Activity.this.getPackageName(), null));

                                startActivityForResult(i, 1001);
                            }
                        });
                        alertDialog = dialog.create();
                        alertDialog.show();

                    }

                    Log.e("set to never ask again", permission);

                }
            }
        }
    }
    public void gpsFunction()
    {
        LocationManager lm = (LocationManager)Current_Location_Activity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            getDeviceLocation();
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            getDeviceLocation();
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(Current_Location_Activity.this)
                    .setMessage("Please Make sure your GPS is on")
                    .setCancelable(true)
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Current_Location_Activity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel",null)
                    .show();
        }
    }
}
