package com.smiligenceUAT1.metrozcustomer;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.bean.CustomerDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import java.util.ArrayList;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.CUSTOMER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.REQUIRED_MSG;


public class LoginActivity extends AppCompatActivity {


    public EditText userName;
    public EditText password;
    public String userNameStr;
    public String passwordStr;
    private Button loginButton;

    DatabaseReference dbref;
    RelativeLayout relativeLayout;
    TextView register, forgetpassword;
    TextView show, hide;
    //ImageView LoginViewPassword, LoginHidePassword;

    public FirebaseAuth mAuth;

    ArrayList<String> customerNameArrayList, customeremailArrayList, customerPhoneNumberArrayList;
    String customerName, customerPhoneNumber;
    CustomerDetails customerDetails;
    boolean check = false;

    private BroadcastReceiver MyReceiver = new MyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.getDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivty);


        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loginButton = findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.editUserName);
        password = findViewById(R.id.editPassword);
        forgetpassword = findViewById(R.id.forgetpassword);
        hide = findViewById(R.id.hideText);
        show = findViewById(R.id.showText);


        customerNameArrayList = new ArrayList<>();
        customeremailArrayList = new ArrayList<>();
        customerPhoneNumberArrayList = new ArrayList<>();
        dbref = CommonMethods.fetchFirebaseDatabaseReference(CUSTOMER_DETAILS_FIREBASE_TABLE);

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginRegister.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                password.setSelection(password.getText().length());
                hide.setVisibility(View.VISIBLE);
                show.setVisibility(View.INVISIBLE);
            }
        });
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                show.setVisibility(View.VISIBLE);
                password.setSelection(password.getText().length());
                hide.setVisibility(View.INVISIBLE);

            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameStr = userName.getText().toString().trim();
                passwordStr = password.getText().toString().trim();

                Query query = dbref.orderByChild("customerPhoneNumber").equalTo(userNameStr);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            for (DataSnapshot customerSnap : dataSnapshot.getChildren()) {
                                customerDetails = customerSnap.getValue(CustomerDetails.class);

                                if (userName.getText().toString().equals("")) {
                                    userName.setError(REQUIRED_MSG);
                                    return;
                                } else if (password.getText().toString().equals("")) {
                                    password.setError(REQUIRED_MSG);
                                    return;
                                } else {
                                    try {
                                        String encrtptPassword = CommonMethods.encrypt(passwordStr);

                                        if (customerDetails.isOtpVerified() == true) {
                                            if (customerDetails.getPassword().equals(encrtptPassword)) {
                                                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("userNameStr", customerDetails.getEmailId());
                                                editor.putString("passwordStr", customerDetails.getPassword());
                                                editor.putString("customerName", customerDetails.getFullName());
                                                editor.putString("customerPhoneNumber", customerDetails.getCustomerPhoneNumber());
                                                editor.putString("customerId", customerDetails.getCustomerId());
                                                editor.commit();
                                                Intent HomeActivity = new Intent(LoginActivity.this, OnBoardScreenActivity.class);
                                                setResult(RESULT_OK, null);
                                                startActivity(HomeActivity);
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Your mobile number is not verified", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Mobile number you provided is not registered,Please register before signIn", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, "Invalid login details ", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}