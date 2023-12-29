package com.smiligenceUAT1.metrozcustomer.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.smiligenceUAT1.metrozcustomer.BuildConfig;
import com.smiligenceUAT1.metrozcustomer.Current_Location_Activity;
import com.smiligenceUAT1.metrozcustomer.HomePageAcitivity;
import com.smiligenceUAT1.metrozcustomer.LoginActivity;
import com.smiligenceUAT1.metrozcustomer.R;

public class SplashActivity extends AppCompatActivity {
    public static SplashActivity loginObject;
    private static int splashTimeOut = 4500;
    private Handler handler;
    private long startTime, currentTime, finishedTime = 0L;
    private int duration = 22000 / 4;// 1 character is equal to 1 second. if want to
    // reduce. can use as divide
    // by 2,4,8
    private int endTime = 0;
    TextView poweredByTExt,versionText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_splash );
        poweredByTExt=findViewById(R.id.powredBySmiligence);
        versionText=findViewById(R.id.Version);
        poweredByTExt.setText("Powered by Smiligence ");
        String versionName = BuildConfig.VERSION_NAME.replace("-DEBUG","");
        versionText.setText("Version "+versionName);
        loginObject = this;

        new Handler ().postDelayed ( new Runnable () {

            @Override
            public void run() {

                SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                String username = sharedPreferences.getString ( "userNameStr", "" );

                if (username != null && !username.equals ( "" )) {
                    Intent intent = new Intent ( SplashActivity.this, Current_Location_Activity.class );
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                    finish ();
                    //overridePendingTransition(R.anim.slide_in_up, 0);
                } else {
                    Intent intent = new Intent ( SplashActivity.this, LoginActivity.class );
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                    finish ();
                    //overridePendingTransition(R.anim.slide_in_up, 0);
                }
            }
        }, splashTimeOut );

        handler = new Handler();
        startTime = Long.valueOf(System.currentTimeMillis());
        currentTime = startTime;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                currentTime = Long.valueOf(System.currentTimeMillis());
                finishedTime = Long.valueOf(currentTime)
                        - Long.valueOf(startTime);

                if (finishedTime >= duration + 30) {

                } else {
                    endTime = (int) (finishedTime / 250);// divide this by
                    // 1000,500,250,125
                    Spannable spannableString = new SpannableString(poweredByTExt
                            .getText());
                    spannableString.setSpan(new ForegroundColorSpan(
                                    Color.YELLOW), 0, endTime,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    poweredByTExt.setText(spannableString);
                    handler.postDelayed(this, 10);
                }
            }
        }, 10);

    }
}

