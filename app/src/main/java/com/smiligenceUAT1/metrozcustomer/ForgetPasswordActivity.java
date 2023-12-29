package com.smiligenceUAT1.metrozcustomer;


import android.content.Intent;
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
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.util.concurrent.TimeUnit;


public class ForgetPasswordActivity extends AppCompatActivity {

    EditText resgisteredMobileNumberEdt, getOtpEdt;
    Button getOtpButton, verifyOtpButton;
    TextView resendOtpText;
    String verificationId;
    FirebaseAuth mAuth;
    DatabaseReference loginDataRef;
    TextView gotoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_forget_password);

        loginDataRef = CommonMethods.fetchFirebaseDatabaseReference("CustomerLoginDetails");
        resgisteredMobileNumberEdt = findViewById(R.id.registeredmobilenumber);
        getOtpEdt = findViewById(R.id.otp);

        getOtpButton = findViewById(R.id.getotp);
        verifyOtpButton = findViewById(R.id.verifyotp);

        gotoLogin = findViewById(R.id.gotologinpage);

        resendOtpText = findViewById(R.id.resendotp);
        mAuth = FirebaseAuth.getInstance();


        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFunction();
            }
        });

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        resendOtpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFunction();
            }
        });

        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = getOtpEdt.getText().toString().trim();
                if (resgisteredMobileNumberEdt.getText().toString().equals("")) {
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter mobile number to verify OTP", Toast.LENGTH_SHORT).show();
                    return;
                } else if (code.isEmpty() || code.length() < 6) {
                    getOtpEdt.setError("Enter valid code....");
                    getOtpEdt.requestFocus();
                    return;
                } else {
                    verifyCode(code);
                    return;
                }
            }
        });
    }

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Verification Code is wrong, try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent changePasswordScreenIntent = new Intent(ForgetPasswordActivity.this, ChangeNewPasswordActivity.class);
                            changePasswordScreenIntent.putExtra("phonenumber", resgisteredMobileNumberEdt.getText().toString());
                            changePasswordScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(changePasswordScreenIntent);
                        } else {
                            Toast.makeText(ForgetPasswordActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                getOtpEdt.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ForgetPasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


    private void resendOtpTimer() {
        resendOtpText.setClickable(false);
        resendOtpText.setEnabled(false);
        resendOtpText.setTextColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.cyanbase));
        new CountDownTimer(60000, 1000) {
            int secondsLeft = 0;

            public void onTick(long ms) {
                if (Math.round((float) ms / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) ms / 1000.0f);
                    resendOtpText.setText("Resend OTP ( " + secondsLeft + " )");
                }
            }

            public void onFinish() {
                resendOtpText.setClickable(true);
                resendOtpText.setEnabled(true);
                resendOtpText.setText("Resend OTP");
                resendOtpText.setTextColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.cyanbase));
            }

        }.start();
    }

    public void checkFunction()
    {
        if ("".equals ( resgisteredMobileNumberEdt.getText ().toString () )) {
            resgisteredMobileNumberEdt.setError ( "Required" );
        } else if (resgisteredMobileNumberEdt.getText ().toString ().length () < 10) {
            resgisteredMobileNumberEdt.setError ( "Invalid Mobile number" );
        } else {

            Query phoneNumberQuery = loginDataRef.orderByChild ( "customerPhoneNumber" ).equalTo ( resgisteredMobileNumberEdt.getText ().toString () );

            phoneNumberQuery.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount () > 0) {
                        sendVerificationCode ( "+91" + resgisteredMobileNumberEdt.getText ().toString () );
                        resendOtpTimer ();
                        getOtpButton.setVisibility ( View.INVISIBLE );
                        verifyOtpButton.setVisibility ( View.VISIBLE );
                        resgisteredMobileNumberEdt.setEnabled ( false );
                    } else {
                        Toast.makeText ( ForgetPasswordActivity.this, "Please enter registered mobile number", Toast.LENGTH_SHORT ).show ();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }

    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}