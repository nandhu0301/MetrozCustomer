package com.smiligenceUAT1.metrozcustomer.common;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.smiligenceUAT1.metrozcustomer.R;

public class ActionDialog {


    private Activity activity;
    private Dialog dialog;
    TextView textView;
    public int counter=5;
    ImageView cancelAd;

    public ActionDialog( Activity activity) {
        this.activity = activity;
        setDialog();
    }

    public void showDialog(){
        if (!((Activity) activity).isFinishing()) {
            dialog.show();
        }
    }

    public void dismiss(){
        if (!((Activity) activity).isFinishing()) {
            dialog.dismiss();
        }
    }

    private void setDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        textView=dialog.findViewById(R.id.textad);
        cancelAd=dialog.findViewById(R.id.cancelDialog);
        cancelAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((Activity) activity).isFinishing()) {
                    dismiss();
                }
            }
        });
        new CountDownTimer(50000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText("Ad 0:0"+ String.valueOf(counter));
                counter--;
                if (counter==0)
                {
                    if (!((Activity) activity).isFinishing()) {
                        dialog.dismiss();
                    }
                }
            }
            @Override
            public void onFinish() {
            }
        }.start();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
}
