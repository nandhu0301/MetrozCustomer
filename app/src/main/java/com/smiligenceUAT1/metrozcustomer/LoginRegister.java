package com.smiligenceUAT1.metrozcustomer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.bean.CustomerDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.DateUtils;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.TextUtils;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.INVALID_EMAIL;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.INVALID_FIRSTNAME_SPECIFICATION;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.INVALID_PASSWORD;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.INVALID_PASSWORD_SPECIFICATION;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.INVALID_PHONENUMBER;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PASSWORD_LENGTH;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PASSWORD_LENGTH_TOO_SHORT;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.REQUIRED_MSG;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.RE_ENTER_PASSWORD;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.TEXT_BLANK;


public class LoginRegister extends AppCompatActivity {
    private static final String TAG = "";
    Button registerButton;
    TextView loginButton;
    EditText userNameEdt, passwordEdt, confirmPasswordEdt, emailIdEdt, phoneNumberedt;
    long phoneNumbermaxId = 0;
    long customerDetailsMaxId = 0;
    String encryptedpassword;
    boolean check;
    DatabaseReference customerDetails;
    CustomerDetails customerDetailsData;
    CustomerDetails loginDetails =
            new CustomerDetails ();
    TextView goToOtpScreen;
    private BroadcastReceiver MyReceiver = new MyReceiver();

    TextView showPassword,hidePassword,showConfrimPassword,hideConfrimPassword;

    protected void onCreate(Bundle savedInstanceState) {
        Utils.getDatabase ();
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_register );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        registerReceiver ( MyReceiver, new IntentFilter ( ConnectivityManager.CONNECTIVITY_ACTION ) );

        loginButton = findViewById ( R.id.login );
        registerButton = findViewById ( R.id.registerButton );
        userNameEdt = findViewById ( R.id.editTextPersonName );
        passwordEdt = findViewById ( R.id.editTextPassword );
        confirmPasswordEdt = findViewById ( R.id.editTextRePassword );
        emailIdEdt = findViewById ( R.id.editTextEmailAddress );
        phoneNumberedt = findViewById ( R.id.editTextPhoneNumber );
        goToOtpScreen = findViewById ( R.id.otpscreen );

        customerDetails = CommonMethods.fetchFirebaseDatabaseReference ( "CustomerLoginDetails" );

        showPassword = findViewById ( R.id.ShowPassword );
        hidePassword = findViewById ( R.id.hidePassword );
        showConfrimPassword = findViewById ( R.id.showConfrimPassword );
        hideConfrimPassword = findViewById ( R.id.hideConfrimPassword );

        showPassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                passwordEdt.setTransformationMethod ( PasswordTransformationMethod.getInstance () );
                passwordEdt.setSelection ( passwordEdt.getText ().length () );
                hidePassword.setVisibility ( View.VISIBLE );
                showPassword.setVisibility ( View.INVISIBLE );
            }
        } );
        //Hide Password Onclick Listener
        hidePassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                passwordEdt.setTransformationMethod ( HideReturnsTransformationMethod.getInstance () );
                passwordEdt.setSelection ( passwordEdt.getText ().length () );
                hidePassword.setVisibility ( View.INVISIBLE );
                showPassword.setVisibility ( View.VISIBLE );
            }
        } );
        //viewConfirm Password Onclick Listener
        showConfrimPassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                confirmPasswordEdt.setTransformationMethod ( PasswordTransformationMethod.getInstance () );
                confirmPasswordEdt.setSelection ( confirmPasswordEdt.getText ().length () );
                hideConfrimPassword.setVisibility ( View.VISIBLE );
                showConfrimPassword.setVisibility ( View.INVISIBLE );
            }
        } );
        //HideConfirm Password OnClick Listener
        hideConfrimPassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                confirmPasswordEdt.setTransformationMethod ( HideReturnsTransformationMethod.getInstance () );
                confirmPasswordEdt.setSelection ( confirmPasswordEdt.getText ().length () );
                hideConfrimPassword.setVisibility ( View.INVISIBLE );
                showConfrimPassword.setVisibility ( View.VISIBLE );
            }
        } );


        goToOtpScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("REGISTER", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String customerId = sharedPreferences.getString("customerId", "");
                String customerPhoneNumber = sharedPreferences.getString("customerPhoneNumber", "");

                if (customerId != null && !"".equalsIgnoreCase(customerId) && customerPhoneNumber != null && !"".equalsIgnoreCase(customerPhoneNumber)) {
                    Intent intent = new Intent(LoginRegister.this, OtpVerificationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginRegister.this, "Please Complete Registration Process ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( LoginRegister.this, LoginActivity.class );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        } );

        registerButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                CommonMethods commonMethods = new CommonMethods ( getApplicationContext () );


                final String emailStr = emailIdEdt.getText ().toString ();
                final String userNameStr = userNameEdt.getText ().toString ();
                final String passwordStr = passwordEdt.getText ().toString ();
                final String confirmPasswordStr = confirmPasswordEdt.getText ().toString ();
                final String phoneNumberStr = phoneNumberedt.getText ().toString ();
                if ("".equals ( userNameStr )) {
                    userNameEdt.setError ( REQUIRED_MSG );
                    return;
                } else if (!TextUtils.isValidlastName ( userNameStr )) {
                    userNameEdt.setError ( INVALID_FIRSTNAME_SPECIFICATION );
                    return;
                } else if ("".equals ( emailStr )) {
                    emailIdEdt.setError ( REQUIRED_MSG );
                    return;
                } else if (!TextUtils.isValidEmail ( emailStr )) {
                    emailIdEdt.setError ( INVALID_EMAIL );
                } else if ("".equals ( phoneNumberStr )) {
                    phoneNumberedt.setError ( REQUIRED_MSG );
                    return;
                } else if (!TextUtils.validatePhoneNumber ( phoneNumberStr )) {
                    phoneNumberedt.setError ( INVALID_PHONENUMBER );
                    return;
                } else if ("".equals ( passwordStr )) {
                    passwordEdt.setError ( REQUIRED_MSG );
                    return;
                } else if (!TextUtils.isValidPassword ( passwordStr )) {
                    passwordEdt.setError ( INVALID_PASSWORD_SPECIFICATION );
                    return;
                } else if ("".equals ( confirmPasswordStr )) {
                    confirmPasswordEdt.setError ( REQUIRED_MSG );
                    return;
                } else if (passwordStr.equals ( confirmPasswordStr )
                        && (passwordStr.length () < PASSWORD_LENGTH)) {
                    passwordEdt.setError ( PASSWORD_LENGTH_TOO_SHORT );
                    return;
                } else if (!passwordStr.equals ( confirmPasswordStr )) {
                    confirmPasswordEdt.setText ( TEXT_BLANK );
                    confirmPasswordEdt.setError ( RE_ENTER_PASSWORD );
                    return;
                } else {
                    check = false;

                    SharedPreferences sharedPreferences = getSharedPreferences ( "REGISTER", MODE_PRIVATE );
                    SharedPreferences.Editor editor = sharedPreferences.edit ();
                    editor.clear ();
                    editor.commit ();

                    Query phoneNumberAlreadyExists = customerDetails.orderByChild ( "customerPhoneNumber" ).equalTo ( phoneNumberStr );
                    phoneNumberAlreadyExists.addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount () > 0) {
                                if (check == false) {
                                    for ( DataSnapshot phoneSnap : dataSnapshot.getChildren () ) {
                                        customerDetailsData = phoneSnap.getValue ( CustomerDetails.class );
                                        break;
                                    }
                                    if (customerDetailsData.isOtpVerified () == false) {
                                        new SweetAlertDialog( LoginRegister.this, SweetAlertDialog.WARNING_TYPE )
                                                .setContentText ( "Phone number already exists..You want to override existing details?" )
                                                .setConfirmText ( "Yes!" )
                                                .setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener () {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        if ("".equals ( emailStr ) == false
                                                                && TextUtils.isValidEmail ( emailStr )) {
                                                            if ("".equals ( phoneNumberStr ) == false
                                                                    && TextUtils.validatePhoneNumber ( phoneNumberStr )) {
                                                                if (TextUtils.isValidPassword ( passwordStr )
                                                                        && passwordStr.equals ( confirmPasswordStr )) {
                                                                    try {
                                                                        encryptedpassword = CommonMethods.encrypt ( passwordStr );
                                                                        String encryptedConfirmPassword = CommonMethods.encrypt ( confirmPasswordStr );
                                                                        String createdDate = DateUtils.fetchCurrentDateAndTime ();

                                                                        if (!((Activity) LoginRegister.this).isFinishing ()) {
                                                                            DatabaseReference startTimeDataRef = FirebaseDatabase.getInstance ("https://uat-testing-metroz-default-rtdb.firebaseio.com/")
                                                                                    .getReference ( "CustomerLoginDetails" ).child ( customerDetailsData.getCustomerId () );

                                                                            startTimeDataRef.child ( "fullName" ).setValue ( userNameStr );
                                                                            startTimeDataRef.child ( "emailId" ).setValue ( emailStr );
                                                                            startTimeDataRef.child ( "password" ).setValue ( encryptedpassword );
                                                                            startTimeDataRef.child ( "confirmPassword" ).setValue ( encryptedConfirmPassword );
                                                                            startTimeDataRef.child ( "creationDate" ).setValue ( createdDate );
                                                                            startTimeDataRef.child ( "customerPhoneNumber" ).setValue ( phoneNumberStr );
                                                                            startTimeDataRef.child ( "otpVerified" ).setValue ( false );
                                                                            Intent intent = new Intent ( LoginRegister.this, OtpVerificationActivity.class );
                                                                            SharedPreferences sharedPreferences = getSharedPreferences ( "REGISTER", MODE_PRIVATE );
                                                                            SharedPreferences.Editor editor = sharedPreferences.edit ();
                                                                            editor.putString ( "customerId", String.valueOf ( customerDetailsData.getCustomerId () ) );
                                                                            editor.putString ( "customerPhoneNumber", phoneNumberStr );
                                                                            editor.commit ();
                                                                            startActivity ( intent );
                                                                            clearFunction ();
                                                                            check = true;
                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace ();
                                                                    }
                                                                } else {
                                                                    Toast.makeText ( getApplicationContext (), INVALID_PASSWORD, Toast.LENGTH_LONG ).show ();
                                                                }
                                                            } else {
                                                                Toast.makeText ( getApplicationContext (), INVALID_PHONENUMBER, Toast.LENGTH_LONG ).show ();
                                                            }
                                                        } else {
                                                            Toast.makeText ( getApplicationContext (), INVALID_EMAIL, Toast.LENGTH_LONG ).show ();
                                                        }

                                                        sDialog.dismissWithAnimation ();
                                                    }
                                                } )
                                                .setCancelButton ( "No", new SweetAlertDialog.OnSweetClickListener () {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog)
                                                    {
                                                        sDialog.dismissWithAnimation ();
                                                    }
                                                } )
                                                .show ();
                                    } else {
                                        Toast.makeText ( LoginRegister.this, "Your Mobile number is already registered,Please login to proceed ", Toast.LENGTH_SHORT ).show ();
                                    }
                                }
                            } else {
                                if ("".equals ( emailStr ) == false
                                        && TextUtils.isValidEmail ( emailStr )) {
                                    if ("".equals ( phoneNumberStr ) == false
                                            && TextUtils.validatePhoneNumber ( phoneNumberStr )) {
                                        if (TextUtils.isValidPassword ( passwordStr )
                                                && passwordStr.equals ( confirmPasswordStr )) {
                                            try {
                                                encryptedpassword = CommonMethods.encrypt ( passwordStr );
                                                String encryptedConfirmPassword = CommonMethods.encrypt ( confirmPasswordStr );
                                                String createdDate = DateUtils.fetchCurrentDateAndTime ();
                                                loginDetails.setCustomerId ( String.valueOf ( customerDetailsMaxId + 1 ) );
                                                loginDetails.setFullName ( userNameStr );
                                                loginDetails.setEmailId ( emailStr );
                                                loginDetails.setPassword ( encryptedpassword );
                                                loginDetails.setConfirmPassword ( encryptedConfirmPassword );
                                                loginDetails.setCustomerPhoneNumber ( phoneNumberStr );
                                                loginDetails.setCreationDate ( createdDate );
                                                loginDetails.setCurrentAddress ( "" );
                                                loginDetails.setCurrentPincode ( "" );
                                                loginDetails.setOtpVerified ( false );
                                                if (!((Activity) LoginRegister.this).isFinishing ()) {
                                                    customerDetails.child ( String.valueOf ( customerDetailsMaxId + 1 ) ).setValue ( loginDetails );

                                                    Intent intent = new Intent ( LoginRegister.this, OtpVerificationActivity.class );
                                                    SharedPreferences sharedPreferences = getSharedPreferences ( "REGISTER", MODE_PRIVATE );
                                                    SharedPreferences.Editor editor = sharedPreferences.edit ();
                                                    editor.putString ( "customerId", String.valueOf ( customerDetailsMaxId + 1 ) );
                                                    editor.putString ( "customerPhoneNumber", phoneNumberStr );
                                                    editor.commit ();
                                                    Toast.makeText ( LoginRegister.this, "Registered Sucessfully", Toast.LENGTH_SHORT ).show ();
                                                    startActivity ( intent );
                                                    clearFunction ();
                                                    check = true;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace ();
                                            }
                                        } else {
                                            Toast.makeText ( getApplicationContext (), INVALID_PASSWORD, Toast.LENGTH_LONG ).show ();
                                        }
                                    } else {
                                        Toast.makeText ( getApplicationContext (), INVALID_PHONENUMBER, Toast.LENGTH_LONG ).show ();
                                    }
                                } else {
                                    Toast.makeText ( getApplicationContext (), INVALID_EMAIL, Toast.LENGTH_LONG ).show ();
                                }

                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );


                }
            }
        } );


        customerDetails.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerDetailsMaxId = dataSnapshot.getChildrenCount ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


    }

    public void clearFunction() {
        userNameEdt.setText ( TEXT_BLANK );
        passwordEdt.setText ( TEXT_BLANK );
        confirmPasswordEdt.setText ( TEXT_BLANK );
        emailIdEdt.setText ( TEXT_BLANK );
        phoneNumberedt.setText ( TEXT_BLANK );
    }


}