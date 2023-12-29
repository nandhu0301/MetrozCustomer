package com.smiligenceUAT1.metrozcustomer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.bean.CustomerDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.TextUtils;
import com.smiligenceUAT1.metrozcustomer.common.Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.INVALID_PASSWORD_SPECIFICATION;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PASSWORD_LENGTH;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PASSWORD_LENGTH_TOO_SHORT;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.REQUIRED_MSG;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.RE_ENTER_PASSWORD;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.TEXT_BLANK;

public class ChangeNewPasswordActivity extends AppCompatActivity {


    EditText changePasswordEdt, changeConfirmPasswordEdt;
    Button changePasswordBtn;
    String passwordStr, confirmPasswordStr;
    DatabaseReference loginDataRef;
    String getPhoneNumberIntent;
    CustomerDetails customerDetails;
    SweetAlertDialog sweetAlertDialog;
    String encryptedpassword, encryptedConfirmPassword;

    TextView showPassword,hidePassword,showConfrimPassword,hideConfrimPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        Utils.getDatabase ();
        setContentView ( R.layout.activity_change_new_password );


        changePasswordEdt = findViewById ( R.id.changepasswordedt );
        changeConfirmPasswordEdt = findViewById ( R.id.changeconfirmpasswordedt );

        changePasswordBtn = findViewById ( R.id.changePasswordBtn );
        loginDataRef =CommonMethods.fetchFirebaseDatabaseReference( "CustomerLoginDetails" );
        getPhoneNumberIntent = getIntent ().getStringExtra ( "phonenumber" );
        showPassword = findViewById ( R.id.ShowPassword );
        hidePassword = findViewById ( R.id.hidePassword );
        showConfrimPassword = findViewById ( R.id.showConfrimPassword );
        hideConfrimPassword = findViewById ( R.id.hideConfrimPassword );

        showPassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                changePasswordEdt.setTransformationMethod ( PasswordTransformationMethod.getInstance () );
                changePasswordEdt.setSelection ( changePasswordEdt.getText ().length () );
                hidePassword.setVisibility ( View.VISIBLE );
                showPassword.setVisibility ( View.INVISIBLE );
            }
        } );
        //Hide Password Onclick Listener
        hidePassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                changePasswordEdt.setTransformationMethod ( HideReturnsTransformationMethod.getInstance () );
                changePasswordEdt.setSelection ( changePasswordEdt.getText ().length () );
                hidePassword.setVisibility ( View.INVISIBLE );
                showPassword.setVisibility ( View.VISIBLE );
            }
        } );
        //viewConfirm Password Onclick Listener
        showConfrimPassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                changeConfirmPasswordEdt.setTransformationMethod ( PasswordTransformationMethod.getInstance () );
                changeConfirmPasswordEdt.setSelection ( changeConfirmPasswordEdt.getText ().length () );
                hideConfrimPassword.setVisibility ( View.VISIBLE );
                showConfrimPassword.setVisibility ( View.INVISIBLE );
            }
        } );
        //HideConfirm Password OnClick Listener
        hideConfrimPassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                changeConfirmPasswordEdt.setTransformationMethod ( HideReturnsTransformationMethod.getInstance () );
                changeConfirmPasswordEdt.setSelection ( changeConfirmPasswordEdt.getText ().length () );
                hideConfrimPassword.setVisibility ( View.INVISIBLE );
                showConfrimPassword.setVisibility ( View.VISIBLE );
            }
        } );

        changePasswordBtn.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick(View v) {
                passwordStr = changePasswordEdt.getText ().toString ();
                confirmPasswordStr = changeConfirmPasswordEdt.getText ().toString ();
                if ("".equals ( passwordStr )) {
                    changePasswordEdt.setError ( REQUIRED_MSG );
                    return;
                } else if (!TextUtils.isValidPassword ( passwordStr )) {
                    changePasswordEdt.setError ( INVALID_PASSWORD_SPECIFICATION );
                    return;
                } else if ("".equals ( confirmPasswordStr )) {
                    changeConfirmPasswordEdt.setError ( REQUIRED_MSG );
                    return;
                } else if (passwordStr.equals ( confirmPasswordStr )
                        && (passwordStr.length () < PASSWORD_LENGTH)) {
                    changePasswordEdt.setError ( PASSWORD_LENGTH_TOO_SHORT );
                    return;
                } else if (!passwordStr.equals ( confirmPasswordStr )) {
                    changeConfirmPasswordEdt.setText ( TEXT_BLANK );
                    changeConfirmPasswordEdt.setError ( RE_ENTER_PASSWORD );
                    return;
                } else
                    {
                    Query phoneNumberBasedQuery = loginDataRef.orderByChild ( "customerPhoneNumber" ).equalTo ( getPhoneNumberIntent );

                    phoneNumberBasedQuery.addValueEventListener ( new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount () > 0) {
                                for ( DataSnapshot phoneSnap : dataSnapshot.getChildren () ) {
                                    customerDetails = phoneSnap.getValue ( CustomerDetails.class );
                                }
                                try {
                                    encryptedpassword = CommonMethods.encrypt ( passwordStr );
                                    encryptedConfirmPassword = CommonMethods.encrypt ( confirmPasswordStr );

                                    if (!((Activity) ChangeNewPasswordActivity.this).isFinishing ())
                                    {
                                        DatabaseReference passwordRef = CommonMethods.fetchFirebaseDatabaseReference( "CustomerLoginDetails" ).child ( customerDetails.getCustomerId () );
                                        passwordRef.child ( "password" ).setValue ( encryptedpassword );
                                        passwordRef.child ( "confirmPassword" ).setValue ( encryptedConfirmPassword );

                                        sweetAlertDialog = new SweetAlertDialog ( ChangeNewPasswordActivity.this, SweetAlertDialog.SUCCESS_TYPE );
                                        sweetAlertDialog.setCancelable ( false );
                                        sweetAlertDialog.setContentText ( "Password Changed Successfully!" )
                                                .setConfirmButton ( "Go to login Page", new SweetAlertDialog.OnSweetClickListener () {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        Intent intent = new Intent ( ChangeNewPasswordActivity.this, LoginActivity.class );
                                                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                        startActivity ( intent );
                                                    }
                                                } )
                                                .show ();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace ();
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
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ChangeNewPasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}