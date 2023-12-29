package com.smiligenceUAT1.metrozcustomer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OtpVerificationActivity extends AppCompatActivity {

    EditText otpEditText, PhoneNumber;
    Button signInButton;
    String verificationId;
    FirebaseAuth mAuth;
    static String saved_id, saved_Phone_number;
    TextView resendOtpButton;
    SweetAlertDialog sweetAlertDialog, errorDialog;
    TextView backtoregister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        Utils.getDatabase ();
        setContentView ( R.layout.activity_otp_verification );

        otpEditText = findViewById(R.id.editTextCode);
        signInButton = findViewById(R.id.buttonSignIn);
        resendOtpButton = findViewById(R.id.resendotp);
        PhoneNumber = findViewById(R.id.editTextPhoneNumber);
        backtoregister = findViewById(R.id.backtoregister);

        final SharedPreferences loginSharedPreferences = getSharedPreferences ( "REGISTER", MODE_PRIVATE );
        saved_id = loginSharedPreferences.getString ( "customerId", "" );
        saved_Phone_number = loginSharedPreferences.getString ( "customerPhoneNumber", "" );

        mAuth = FirebaseAuth.getInstance ();


        sendVerificationCode ( "+91" + saved_Phone_number );

        if (saved_Phone_number != null && !saved_Phone_number.equals ( "" )) {
            resendOtpTimer ();
            PhoneNumber.setText ( "+91" + saved_Phone_number );
        }


        if (!"".equals ( otpEditText.getText ().toString () )) {
            otpEditText.setError ( null );
        }

        signInButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                String code = otpEditText.getText ().toString ().trim ();
                if (PhoneNumber.getText ().toString ().equals ( "" )) {
                    PhoneNumber.setError ( "Required" );
                } else if (code.isEmpty () || code.length () < 6) {
                    otpEditText.setError ( "Enter valid code..." );
                    otpEditText.requestFocus ();
                    return;
                } else {
                    verifyCode ( code );
                }
            }
        } );


        resendOtpButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                sendVerificationCode ( "+91" + saved_Phone_number );
                resendOtpTimer();
            }
        } );

        backtoregister.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent ( OtpVerificationActivity.this, LoginRegister.class );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        } );
    }

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential ( verificationId, code );
            signInWithCredential ( credential );
        } catch (Exception e) {
            Toast toast = Toast.makeText ( getApplicationContext (), "Verification Code is wrong, try again", Toast.LENGTH_SHORT );
            toast.setGravity ( Gravity.CENTER, 0, 0 );
            toast.show ();
        }

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential ( credential )
                .addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ()) {

                            if (!((Activity) OtpVerificationActivity.this).isFinishing ()) {
                                DatabaseReference startTimeDataRef = CommonMethods.fetchFirebaseDatabaseReference ( "CustomerLoginDetails" ).child ( saved_id );
                                startTimeDataRef.child ( "otpVerified" ).setValue ( true );
                            }

                            sweetAlertDialog = new SweetAlertDialog ( OtpVerificationActivity.this, SweetAlertDialog.SUCCESS_TYPE );
                            sweetAlertDialog.setTitleText ( "Sucessfully Registered!" )
                                    .setConfirmButton ( "Go to login Page", new SweetAlertDialog.OnSweetClickListener () {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                                            SharedPreferences sharedPreferences = getSharedPreferences ( "REGISTER", MODE_PRIVATE );
                                            SharedPreferences.Editor editor = sharedPreferences.edit ();
                                            editor.clear ();
                                            editor.commit ();

                                            Intent intent = new Intent ( OtpVerificationActivity.this, LoginActivity.class );
                                            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                            startActivity ( intent );

                                            PhoneNumber.setText ( "");
                                            otpEditText.setText ( "" );

                                        }
                                    } )
                                    .show ();
                            sweetAlertDialog.setCancelable ( false );
                        } else {
                            errorDialog = new SweetAlertDialog ( OtpVerificationActivity.this, SweetAlertDialog.ERROR_TYPE );
                            errorDialog.setCancelable ( false );
                            errorDialog
                                    .setContentText ( "Please enter valid otp!" ).setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener () {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    errorDialog.dismiss ();
                                }
                            } )
                                    .show ();

                        }
                    }
                } );


    }

    private void sendVerificationCode(String number) {


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks () {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent ( s, forceResendingToken );
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode ();


            if (code != null) {
                otpEditText.setText ( code );
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void resendOtpTimer()
    {
        resendOtpButton.setVisibility ( View.VISIBLE );
        resendOtpButton.setClickable ( false );
        resendOtpButton.setEnabled ( false );

        new CountDownTimer( 60000, 1000 ) {
            int secondsLeft = 0;

            public void onTick(long ms) {
                if (Math.round ( (float) ms / 1000.0f ) != secondsLeft) {
                    secondsLeft = Math.round ( (float) ms / 1000.0f );
                    resendOtpButton.setText ( "Resend OTP ( " + secondsLeft + " )" );
                }
            }

            public void onFinish() {
                resendOtpButton.setClickable ( true );
                resendOtpButton.setEnabled ( true );
                resendOtpButton.setText ( "Resend OTP" );

            }
        }.start ();


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

}