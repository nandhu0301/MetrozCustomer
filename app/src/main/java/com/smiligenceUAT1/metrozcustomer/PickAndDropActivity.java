package com.smiligenceUAT1.metrozcustomer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.sayantan.advancedspinner.MultiSpinner;
import com.sayantan.advancedspinner.MultiSpinnerListener;
import com.smiligenceUAT1.metrozcustomer.bean.MetrozStoreTime;
import com.smiligenceUAT1.metrozcustomer.bean.OrderDetails;
import com.smiligenceUAT1.metrozcustomer.bean.PickUpAndDrop;
import com.smiligenceUAT1.metrozcustomer.bean.Tip;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;
import com.smiligenceUAT1.metrozcustomer.common.Constant;
import com.smiligenceUAT1.metrozcustomer.common.CustomScrollView;
import com.smiligenceUAT1.metrozcustomer.common.DateUtils;
import com.smiligenceUAT1.metrozcustomer.common.MyReceiver;
import com.smiligenceUAT1.metrozcustomer.common.Utils;
import com.vinay.stepview.VerticalStepView;
import com.vinay.stepview.models.Step;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.PICKUP_DROP_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.RAZORPAY_KEY_ID;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.RAZORPAY_SECRAT_KEY;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.TEXT_BLANK;
import static com.smiligenceUAT1.metrozcustomer.common.GpsConstant.BASIC_FAIR;
import static com.smiligenceUAT1.metrozcustomer.common.GpsConstant.MINIMUM_FAIR;
import static com.smiligenceUAT1.metrozcustomer.common.GpsConstant.PER_Km;

public class PickAndDropActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    ViewFlipper viewFlipper;
    public String saved_userName, saved_customer, saved_customerPhonenumber, saved_pincode, saved_id;
    TextView linkText;
    EditText pickUpAddressEdt, deliverAddressEdt;
    MultiSpinner documetsEdt;
    RelativeLayout noContactLayout, otpLayout, tipsLayout, payementlayout, parcelCheckLayout;
    CustomScrollView scrollView;
    List<Step> stepList;
    Button payButton, confirmButton;
    boolean check = true;
    JSONObject jsonObject;
    String resultOrderId, paymentId;
    String paymentType, amount;
    RazorpayClient razorpay;
    String receiptNumber;
    int temp = 0;
    Payment payment;
    OrderDetails orderDetails = new OrderDetails();
    long maxid = 0;
    boolean checkAsync = true;
    DatabaseReference Orderreference, metrozStoteTimingDataRef, storeTimingDataRef, pickupdropRef, tipsDataRef, distanceFeeDataRef;
    int perKmDistanceFee = 0;
    int totaldeliveryFee = 0;
    int distanceFee;
    ArrayList<BitmapDrawable> imageListForTipsList = new ArrayList<BitmapDrawable>();
    ArrayList<Integer> amountForTipsList = new ArrayList<>();
    ArrayList<String> tagNameForTipsList = new ArrayList<>();
    PickUpAndDrop pickUpAndDrop;
    ArrayList<PickUpAndDrop> pickUpAndDroplist = new ArrayList<PickUpAndDrop>();
    TextView descriptionText, totalCostText, totalPartnerDelieryFee;
    int resultAmount = 0;
    TextView partnerdeliveryfee;
    EditText instructionToDeliveryBoyEdt;
    CheckBox noContactDeliveryCheckBox;
    String orderDeliveryType = "Contact Delivery";
    TextView tipsDetails;
    TextView tipsTotal;
    ImageView backtohome;
    int resultKiloMaterRoundOff = 0;
    private BroadcastReceiver MyReceiver = new MyReceiver();
    VerticalStepView horizontalStepView;
    CheckBox parcelWeightCheckbox, parcelCarriedInBikeCheckbox;
    Chip chip;
    String TIME_SERVER;
    NTPUDPClient timeClient;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String currentDateAndTime;
    String metrozStartTime;
    String metrozStopTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getDatabase();
        setContentView(R.layout.activity_pick_and_drop);
        checkGPSConnection(getApplicationContext());
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        final SharedPreferences loginSharedPreferences1 = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
        saved_id = loginSharedPreferences1.getString ( "customerId", "" );
        saved_userName = loginSharedPreferences1.getString ( "userNameStr", "" );
        saved_customer = loginSharedPreferences1.getString ( "customerName", "" );
        saved_customerPhonenumber = loginSharedPreferences1.getString ( "customerPhoneNumber", "" );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Calendar cal = Calendar.getInstance();
        currentLocalTime = cal.getTime();
        date = new SimpleDateFormat("HH:mm aa");
        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT);
        currentDateAndTime = dateFormat.format(new Date());
        currentTime = date.format(currentLocalTime);
        backtohome = findViewById(R.id.backfrompickupanddrop);
        backtohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PickAndDropActivity.this, HomePageAcitivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Orderreference = CommonMethods.fetchFirebaseDatabaseReference("OrderDetails");
        distanceFeeDataRef = CommonMethods.fetchFirebaseDatabaseReference("DeliveryCharges");
        metrozStoteTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference("MetrozstoreTiming");
        storeTimingDataRef = CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance");
        distanceFeeDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    distanceFee = Integer.parseInt(String.valueOf(dataSnapshot.child("DISTANCE_FEE_PER_KM").getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        retriveFunction();
        horizontalStepView = findViewById(R.id.vertical_step_view);
        noContactDeliveryCheckBox = findViewById(R.id.nocontactdeliverycheckBox);
        parcelCheckLayout = findViewById(R.id.parcelweightcheck);
        parcelWeightCheckbox = findViewById(R.id.parcelweight);
        parcelCarriedInBikeCheckbox = findViewById(R.id.parcelinbike);
        tipsDataRef = CommonMethods.fetchFirebaseDatabaseReference("Tips");


        amountForTipsList.clear();
        imageListForTipsList.clear();
        tagNameForTipsList.clear();


        pickupdropRef = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);

        viewFlipper = findViewById(R.id.viewFlipper);
        linkText = findViewById(R.id.linktext);
        pickUpAddressEdt = findViewById(R.id.pickAddressEdt);
        deliverAddressEdt = findViewById(R.id.deliveryAddressEdt);
        documetsEdt = findViewById(R.id.documentsEdt);
        noContactLayout = findViewById(R.id.contactDelivery);
        otpLayout = findViewById(R.id.otpCheck);
        tipsLayout = findViewById(R.id.paymentTips);
        payementlayout = findViewById(R.id.amountDetails);
        scrollView = findViewById(R.id.scrollview);
        scrollView.setEnableScrolling(false);
        payButton = findViewById(R.id.payButton);
        confirmButton = findViewById(R.id.confirmButton);
        descriptionText = findViewById(R.id.descriptionText);
        totalCostText = findViewById(R.id.chargeText);
        partnerdeliveryfee = findViewById(R.id.partnerdeliveryfeetxt);
        instructionToDeliveryBoyEdt = findViewById(R.id.instrctionEdt);
        totalPartnerDelieryFee = findViewById(R.id.totalpartnerfee);
        tipsDetails = findViewById(R.id.partnerdeliveryfee);
        tipsTotal = findViewById(R.id.tipstotal);
        horizontalStepView.setLineLength(75);
        stepList = new ArrayList<>();
        stepList.add(new Step("Select Package Contents"));
        stepList.add(new Step("Delivery Address"));
        stepList.add(new Step("Pickup Address"));


        horizontalStepView.setSteps(stepList);
        stepList.get(0).setState(Step.State.CURRENT);
        stepList.get(1).setState(Step.State.CURRENT);
        stepList.get(2).setState(Step.State.CURRENT);
        horizontalStepView.setSteps(stepList);


        DatabaseReference AddressRef = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);
        AddressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    PickUpAndDrop pickUpAndDrop = dataSnapshot.getValue(PickUpAndDrop.class);
                    if (!"".equals(pickUpAndDrop.getPickupAddress())) {
                        pickUpAddressEdt.setText(pickUpAndDrop.getPickupAddress());
                        stepList.get(2).setState(Step.State.COMPLETED);
                        horizontalStepView.setSteps(stepList);
                    }

                    if (!"".equals(pickUpAndDrop.getDropAddress())) {
                        deliverAddressEdt.setText(pickUpAndDrop.getDropAddress());
                        stepList.get(1).setState(Step.State.COMPLETED);
                        horizontalStepView.setSteps(stepList);


                    }

                    totalCostText.setText("₹ " + pickUpAndDrop.getTipAmount());
                    partnerdeliveryfee.setText("₹ " + pickUpAndDrop.getTotalAmountFair());
                    totalPartnerDelieryFee.setText("₹ " + pickUpAndDrop.getTotalAmountToPaid());

                    if (pickUpAndDrop.getPickupAddress() != null || !"".equals(pickUpAndDrop.getPickupAddress())) {
                        deliverAddressEdt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PickAndDropActivity.this, FindAddressActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("currentAddress", "dropAddress");
                                startActivity(intent);

                            }
                        });
                    }
                    if (pickUpAndDrop.getPickupAddress() != null || !"".equals(pickUpAndDrop.getPickupAddress()) && pickUpAndDrop.getDropAddress() != null || !"".equals(pickUpAndDrop.getDropAddress())) {
                        documetsEdt.addOnItemsSelectedListener(new MultiSpinnerListener() {
                            @Override
                            public void onItemsSelected(List<String> choices, boolean[] selected) {

                                for (int p = 0; p < choices.size(); p++) {

                                }
                                DatabaseReference currentAddressRef = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);
                                currentAddressRef.child("deliverObject").setValue(documetsEdt.getSelectedItems());
                                currentAddressRef.child("basic_Fair").setValue(BASIC_FAIR);
                                currentAddressRef.child("minimum_Fair").setValue(MINIMUM_FAIR);
                                currentAddressRef.child("per_km").setValue(PER_Km);


                                pickupdropRef = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);

                                pickupdropRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (check ) {
                                            if (dataSnapshot.getChildrenCount() > 0) {
                                                PickUpAndDrop pickUpAndDrop = dataSnapshot.getValue(PickUpAndDrop.class);
                                                int Radius = 6371;// radius of earth in Km
                                                double lat1 = pickUpAndDrop.getStartPickupLatitude();
                                                double lat2 = pickUpAndDrop.getStartDeliveryLatitude();
                                                double lon1 = pickUpAndDrop.getEndPickupLongtitude();
                                                double lon2 = pickUpAndDrop.getEndDeliveryLongtitude();
                                                double dLat = Math.toRadians(lat2 - lat1);
                                                double dLon = Math.toRadians(lon2 - lon1);
                                                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                                                        + Math.cos(Math.toRadians(lat1))
                                                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                                                        * Math.sin(dLon / 2);
                                                double c = 2 * Math.asin(Math.sqrt(a));
                                                double valueResult = Radius * c;

                                                double km = valueResult / 1;

                                                resultKiloMaterRoundOff = (int) Math.round(km);
                                                if (resultKiloMaterRoundOff <= 5) {
                                                    resultAmount = (BASIC_FAIR + MINIMUM_FAIR);

                                                    visibleFunction();
                                                } else {
                                                    resultAmount = (BASIC_FAIR + (resultKiloMaterRoundOff * PER_Km));

                                                    visibleFunction();
                                                }
                                                DatabaseReference currentAddressRef = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);
                                                currentAddressRef.child("totalDistance").setValue(resultKiloMaterRoundOff);
                                                currentAddressRef.child("totalAmountFair").setValue(resultAmount);
                                                totalCostText.setText("₹ " + (resultAmount + temp));
                                                partnerdeliveryfee.setText("₹ " + resultAmount);
                                                totalPartnerDelieryFee.setText("₹ " + (resultAmount + temp));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                distanceFeeDataRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getChildrenCount() > 0) {
                                            perKmDistanceFee = Integer.parseInt(String.valueOf(dataSnapshot.child("DISTANCE_FEE_PER_KM").getValue()));
                                            totaldeliveryFee = resultKiloMaterRoundOff * perKmDistanceFee;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tipsDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog();
            }
        });

        tipsDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    amountForTipsList.clear();

                    tagNameForTipsList.clear();
                    for (DataSnapshot tipsSnap : dataSnapshot.getChildren()) {
                        Tip tip = tipsSnap.getValue(Tip.class);
                        amountForTipsList.add(tip.getTipsAmount());
                        tagNameForTipsList.add(tip.getTipsName());
                    }
                    setTag(amountForTipsList, imageListForTipsList, tagNameForTipsList);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pickupdropRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pickUpAndDroplist.clear();
                if (check) {
                    pickUpAndDrop = dataSnapshot.getValue(PickUpAndDrop.class);
                    pickUpAndDroplist.add(pickUpAndDrop);

                    orderDetails.setPickUpAndDroplist(pickUpAndDroplist);
                    orderDetails.setPaymentDate(DateUtils.fetchCurrentDate());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pickUpAddressEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PickAndDropActivity.this, FindAddressActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("currentAddress", "pickupAddress");
                startActivity(intent);
            }
        });
        noContactDeliveryCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    orderDeliveryType = "No Contact Delivery";
                } else {
                    orderDeliveryType = "Contact Delivery";
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (documetsEdt.getSelectedItems().size() == 0) {
                    new SweetAlertDialog(PickAndDropActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Please Select Package contents to place order")
                            .show();
                    return;

                }else if (parcelCarriedInBikeCheckbox.isChecked() == true && parcelWeightCheckbox.isChecked() == false) {
                    Toast.makeText(PickAndDropActivity.this, "Please do self declaration", Toast.LENGTH_SHORT).show();
                    return;
                } else if (parcelCarriedInBikeCheckbox.isChecked() == false && parcelWeightCheckbox.isChecked() == true) {
                    Toast.makeText(PickAndDropActivity.this, "Please do self declaration", Toast.LENGTH_SHORT).show();
                    return;
                } else if (parcelCarriedInBikeCheckbox.isChecked() == false && parcelWeightCheckbox.isChecked() == false) {
                    Toast.makeText(PickAndDropActivity.this, "Please do self declaration", Toast.LENGTH_SHORT).show();
                    return;
                } else if (parcelCarriedInBikeCheckbox.isChecked() == true && parcelWeightCheckbox.isChecked() == true) {
                    if (documetsEdt.getSelectedItems().size() > 0) {
                        if (resultKiloMaterRoundOff > 10) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(PickAndDropActivity.this);
                            builder.setMessage("Delivery not applicable for more than 10 Kms, Order not Placed")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            pickUpAndDrop.setParcelWeightLessThanFiveKgs(true);
                            pickUpAndDrop.setParcelCanBeCarriedinBike(true);
                            payButton.setVisibility(View.VISIBLE);
                            totalCostText.setVisibility(View.VISIBLE);
                            descriptionText.setVisibility(View.VISIBLE);
                            confirmButton.setVisibility(View.INVISIBLE);
                            parcelWeightCheckbox.setEnabled(false);
                            parcelCarriedInBikeCheckbox.setEnabled(false);
                        }

                    }
                }
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultAmount == 0) {


                } else if (documetsEdt.getSelectedItems().size() == 0) {
                    new SweetAlertDialog(PickAndDropActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Please Select Package contents to place order.")
                            .show();

                    return;

                } else {
                    if (!CommonMethods.getConnectivityStatusString(getApplicationContext()).equals("No internet is available")) {

                        //new PaymentActivity.backGroundClass1().execute();
                        metrozStoteTimingDataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    MetrozStoreTime metrozStoreTime = dataSnapshot.getValue(MetrozStoreTime.class);
                                    metrozStartTime = metrozStoreTime.getShopStartTime();
                                    metrozStopTime = metrozStoreTime.getShopEndTime();
                                }
                                try {

                                    if ((sdf.parse(currentTime).compareTo(sdf.parse(metrozStartTime)) == 1) && !(sdf.parse(currentTime).compareTo(sdf.parse(metrozStopTime)) == 1)) {
                                        Checkout.preload ( getApplicationContext () );
                                        new PickAndDropActivity.backGroundClass ().execute ();
                                    } else {
                                        new SweetAlertDialog(PickAndDropActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Error")
                                                .setContentText("Closed,currently not accepting orders.")
                                                .show();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        new SweetAlertDialog(PickAndDropActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("No Network Connection")
                                .show();

                    }

                }


            }
        });

        String text = "By confirming I accept this order doesn't contain illegal/restricted items.Metroz partenr may ask to verify the contents of the package and could choose to refuse the task if the items aren't verified.Terms and Conditions.";
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        spannableString.setSpan(clickableSpan, 201, 222, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linkText.setText(spannableString);
        linkText.setMovementMethod(LinkMovementMethod.getInstance());

        int images[] = {R.drawable.nopurchases, R.drawable.watchtheweight, R.drawable.readytogo};

        for (int image : images) {
            flipperImage(image);
        }
    }


    public void flipperImage(int image) {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);
        viewFlipper.setAnimateFirstView(true);
        viewFlipper.setInAnimation(PickAndDropActivity.this, android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(PickAndDropActivity.this, android.R.anim.slide_out_right);
    }

    private void startpayment() throws JSONException, RazorpayException {
        razorpay = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRAT_KEY);
        JSONObject options = new JSONObject();

        options.put("amount", (resultAmount + temp) * 100);
        options.put("currency", "INR");
        options.put("receipt", "ORIRK_3343");

        Order order = razorpay.Orders.create(options);
        jsonObject = new JSONObject(String.valueOf(order));
        resultOrderId = jsonObject.getString("id");
        receiptNumber = jsonObject.getString("receipt");

        checkOutFunction(resultOrderId);

    }

    private void checkOutFunction(String orderId) throws JSONException {

        final Activity activity = this;
        Checkout checkout = new Checkout();
        checkout.setKeyID(RAZORPAY_KEY_ID);
        checkout.setImage(R.mipmap.ic_launcher);
        JSONObject options = new JSONObject();
        options.put("payment_capture", true);
        options.put("order_id", orderId);

        JSONObject preFill = new JSONObject();


        preFill.put("email", saved_userName);
        preFill.put("contact", saved_customerPhonenumber);
        options.put("prefill", preFill);

        checkout.open(activity, options);

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        if (check) {
            orderDetails.setPaymentId(paymentData.getPaymentId());
            try {
                razorpay = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRAT_KEY);
                payment = razorpay.Payments.fetch(paymentData.getPaymentId());
                jsonObject = new JSONObject(String.valueOf(payment));
                paymentType = jsonObject.getString("method");
                amount = jsonObject.getString("amount");
            } catch (RazorpayException | JSONException e) {

            }

            orderDetails.setPaymentType(paymentType);
            orderDetails.setOrderIdfromPaymentGateway(resultOrderId);
            orderDetails.setOrderStatus("Order Placed");
            orderDetails.setOrderTime(DateUtils.fetchCurrentTime());
            orderDetails.setPaymentamount(resultAmount + temp);
            orderDetails.setAssignedTo("");
            orderDetails.setCustomerName(saved_customer);
            orderDetails.setCustomerPhoneNumber(saved_customerPhonenumber);
            orderDetails.setTotalAmount(resultAmount);
            orderDetails.setTipAmount(temp);
            orderDetails.setDeliveryType(orderDeliveryType);
            orderDetails.setCustomerId(saved_id);
            orderDetails.setCategoryTypeId("2");
            orderDetails.setStoreName("Parcel");
            orderDetails.setDeliveryType(orderDeliveryType);
            orderDetails.setOrderCreateDate(DateUtils.fetchCurrentDateAndTime());
            orderDetails.setDeliverOtp(generateOTP());
            orderDetails.setStoreAddress(pickUpAddressEdt.getText().toString());
            orderDetails.setShippingaddress(deliverAddressEdt.getText().toString());
            orderDetails.setNotificationStatus("false");
            orderDetails.setNotificationStatusForSeller("false");
            orderDetails.setTotalDistanceTraveled(resultKiloMaterRoundOff);
            orderDetails.setDeliveryFee(totaldeliveryFee);
            orderDetails.setFormattedDate(DateUtils.fetchFormatedCurrentDate());
            orderDetails.setNotificationStatusForCustomer("false");
            orderDetails.setDiscountName("");
            orderDetails.setTimerForCancelDelivery("0");
            orderDetails.setInstructionsToDeliveryBoy(instructionToDeliveryBoyEdt.getText().toString());
            retriveFunction();
            orderDetails.setOrderId(String.valueOf(maxid + 1));
            Orderreference.child(String.valueOf(maxid + 1)).setValue(orderDetails);
            pickupdropRef.removeValue();
            pickUpAddressEdt.setText(TEXT_BLANK);
            deliverAddressEdt.setText(TEXT_BLANK);
            documetsEdt.setSelection(0);
            Intent intent = new Intent(PickAndDropActivity.this, PickupAndDroporderDetailsActivty.class);
            intent.putExtra("OrderidDetails", String.valueOf(maxid + 1));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        String paymentId = paymentData.getPaymentId();

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(PickAndDropActivity.this, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText("Payment cancelled...").setContentText("Please try again!").show();
    }

    private class backGroundClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                startpayment();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (RazorpayException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void tipsDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(PickAndDropActivity.this);
        bottomSheetDialog.setContentView(R.layout.fair_details);
        ImageView cancel = bottomSheetDialog.findViewById(R.id.cancelfairdetailslayout);
        TextView distance = bottomSheetDialog.findViewById(R.id.total_distance);
        TextView minimumFairChargeTxt = bottomSheetDialog.findViewById(R.id.minimumfaircharge);
        TextView minimumfairtextDes = bottomSheetDialog.findViewById(R.id.textminimumfair);


        View view = bottomSheetDialog.findViewById(R.id.divider6);
        view.setVisibility(View.VISIBLE);
        TextView baseFair = bottomSheetDialog.findViewById(R.id.basedeliverycharge);
        TextView totalFairCost = bottomSheetDialog.findViewById(R.id.totalfaircharge);
        int width = (int) (PickAndDropActivity.this.getResources().getDisplayMetrics().widthPixels * 0.6);
        int height = (int) (PickAndDropActivity.this.getResources().getDisplayMetrics().heightPixels * 0.4);
        bottomSheetDialog.getWindow().setLayout(width, height);
        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);

        pickupdropRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    PickUpAndDrop pickUpAndDrop = dataSnapshot.getValue(PickUpAndDrop.class);
                    if (pickUpAndDrop.getTotalDistance() > 5) {
                        minimumfairtextDes.setText("Minimum Fair ₹7 Per Km");
                        minimumFairChargeTxt.setText("₹ " + (PER_Km * resultKiloMaterRoundOff));
                    } else {
                        minimumfairtextDes.setText("Minimum Fair less than 5 Kms");
                        minimumFairChargeTxt.setText("₹ " + MINIMUM_FAIR);
                    }

                    if (pickUpAndDrop.getTotalDistance() > 0) {
                        distance.setText(pickUpAndDrop.getTotalDistance() + " Kms");
                    } else {
                        distance.setText(pickUpAndDrop.getTotalDistance() + " Km");
                    }
                    baseFair.setText("₹ " + BASIC_FAIR);
                    totalFairCost.setText("₹ " + pickUpAndDrop.getTotalAmountFair());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

    }

    public void retriveFunction() {
        Orderreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTag(final ArrayList<Integer> tagList, ArrayList<BitmapDrawable> tagList1, ArrayList<String> tagList2) {
        final ChipGroup chipGroup = findViewById(R.id.tips);
        chipGroup.setSingleSelection(true);

        for (int index = 0; index < tagList.size(); index++) {

            final int tagPrice = tagList.get(index);

            final String tagName = tagList2.get(index);

            chip = new Chip(this);
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics());
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            chip.setText("₹ " + String.valueOf(tagPrice));

            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));

            chipGroup.addView(chip);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (temp == 0) {
                        temp = tagPrice;
                        tipsTotal.setText("₹ " + String.valueOf(temp));
                        DatabaseReference currentAddressRef = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);
                        int totamAmountToPayResult = resultAmount + temp;
                        currentAddressRef.child("tipAmount").setValue(temp);
                        DatabaseReference currentAddressRef1 = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);
                        currentAddressRef1.child("totalAmountToPaid").setValue(totamAmountToPayResult);


                    } else {
                        if (temp == tagPrice) {
                            temp = 0;

                            tipsTotal.setText("₹ " + String.valueOf(temp));
                            DatabaseReference currentAddressRef = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);
                            currentAddressRef.child("tipAmount").setValue(0);
                            int totamAmountToPayResult = resultAmount + 0;
                            currentAddressRef.child("totalAmountToPaid").setValue(totamAmountToPayResult);
                        } else {
                            temp = tagPrice;
                            tipsTotal.setText("₹ " + String.valueOf(tagPrice));
                            DatabaseReference currentAddressRef = CommonMethods.fetchFirebaseDatabaseReference(PICKUP_DROP_TABLE).child(saved_id);
                            currentAddressRef.child("tipAmount").setValue(tagPrice);
                            int totamAmountToPayResult = resultAmount + tagPrice;
                            currentAddressRef.child("totalAmountToPaid").setValue(totamAmountToPayResult);

                        }
                    }
                }
            });


        }
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(PickAndDropActivity.this, HomePageAcitivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void checkGPSConnection(Context context) {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);


    }

    public void visibleFunction() {
        noContactLayout.setVisibility(View.VISIBLE);
        otpLayout.setVisibility(View.VISIBLE);
        tipsLayout.setVisibility(View.VISIBLE);
        payementlayout.setVisibility(View.VISIBLE);
        scrollView.setEnableScrolling(true);
        stepList.get(0).setState(Step.State.COMPLETED);
        horizontalStepView.setSteps(stepList);
        confirmButton.setEnabled(true);
        parcelCheckLayout.setVisibility(View.VISIBLE);
    }

    public static String generateOTP() {
        int randomPin = (int) (Math.random() * 900000) + 100000;
        String otp = String.valueOf(randomPin);
        return otp;
    }


}