package com.smiligenceUAT1.metrozcustomer.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = CommonMethods.getConnectivityStatusString(context);

            if(status.isEmpty()) {
                status="No Internet Connection";
                Toast.makeText(context, status, Toast.LENGTH_LONG).show();
            }
        }
    }

