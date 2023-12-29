package com.smiligenceUAT1.metrozcustomer.adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.ViewCartActivity;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;


public class ViewCartAdapter extends BaseAdapter {
    private Activity context;
    List<ItemDetails> itemDetailsList;
    ItemDetails itemDetails;
    private static LayoutInflater inflater = null;
    DatabaseReference databaseReference;
    int counter=1;
    long childCount;
    int incrementIndicator = 1;
    int decrementIndicator = 0;
    String saved_id;
    public ViewCartAdapter(Activity context, List<ItemDetails> list) {
        this.context = context;
        this.itemDetailsList = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return itemDetailsList.size();
    }
    @Override
    public Object getItem(int position) {
        return itemDetailsList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    private class ViewHolder {
        ImageView itemImage;
        TextView itemName, itemqtyPrice, itemPrice;
        RelativeLayout addItem, addQuantity;
        ImageView increaseQty, decreaseQty;
        TextView itemQtyText;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();
        View listViewItem = convertView;
        listViewItem = (listViewItem == null) ? inflater.inflate(R.layout.view_cart_list_layout, null) : listViewItem;
        holder.itemName = (TextView) listViewItem.findViewById
                (R.id.itemname_viewcart);
        holder.itemqtyPrice = (TextView) listViewItem.findViewById
                (R.id.itemqty_viewcart);
        holder.itemPrice = (TextView) listViewItem.findViewById
                (R.id.itemprice_viewcart);
        ImageView deleteItem = listViewItem.findViewById(R.id.deleteItem);
        holder.addQuantity = listViewItem.findViewById(R.id.addquantity);
        holder.increaseQty = listViewItem.findViewById(R.id.incqty);
        holder.decreaseQty = listViewItem.findViewById(R.id.decqty);
        holder.itemQtyText = listViewItem.findViewById(R.id.textqty);
        final SharedPreferences loginSharedPreferences = context.getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");
        databaseReference = CommonMethods.fetchFirebaseDatabaseReference("ViewCart").child(saved_id);
        itemDetails = itemDetailsList.get(position);
        holder.itemName.setText(itemDetails.getItemName());
        holder.itemqtyPrice.setText(" ₹"+String.valueOf(itemDetails.getTotalItemQtyPrice()));
        holder.itemPrice.setText(String.valueOf(" ₹" + itemDetails.getItemPrice()));
        holder.itemQtyText.setText(String.valueOf(itemDetails.getItemCounter()));
        counter = itemDetails.getItemCounter();
        holder.increaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData(position, holder,incrementIndicator);
            }
        });
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemNameString = holder.itemName.getText().toString();
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("You want to delete item from cart!")
                        .setConfirmText("Delete!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                databaseReference.child(String.valueOf(itemDetailsList.get(position).getItemId())).removeValue();
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        childCount=dataSnapshot.getChildrenCount();
                                        if (childCount == 0)
                                        {
                                            Intent intent=new Intent(context, ViewCartActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            context.startActivity(intent);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
        holder.decreaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDetails = itemDetailsList.get(position);
                counter = itemDetails.getItemCounter();
                if (counter > 0) {
                    counter = counter - 1;
                    holder.itemQtyText.setText(String.valueOf(counter));
                    itemDetailsList.get(position).setItemCounter(counter);
                    itemDetails.setItemCounter(counter);
                    itemDetails.setItemBuyQuantity(counter);
                    itemDetails.setTotalItemQtyPrice(counter*itemDetails.getItemPrice());
                    holder.itemqtyPrice.setText(" ₹"+String.valueOf(itemDetails.getTotalItemQtyPrice()));
                    databaseReference.child(String.valueOf(itemDetailsList.get(position).getItemId())).setValue(itemDetails);
                }
                if (counter == 0) {
                    holder.itemQtyText.setText(String.valueOf(counter));
                    itemDetailsList.get(position).setItemCounter(counter);
                    itemDetails = itemDetailsList.get(position);
                    DatabaseReference query = databaseReference.child(String.valueOf(itemDetailsList.get(position).getItemId()));
                    query.child("itemBuyQuantity").equalTo("0").getRef().getParent().removeValue();
                }
                    if (itemDetailsList.size() == 1)
                    {
                        if (itemDetailsList.get(position).getItemCounter() == 0) {
                            Intent intent = new Intent(context, ViewCartActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                }
            }
        });
        return listViewItem;
    }
    private void insertData(int position, ViewHolder holder,int incDecIndicator) {
        itemDetails = itemDetailsList.get(position);
        counter = itemDetails.getItemCounter();
        if (incDecIndicator == incrementIndicator) {
            if(counter==itemDetailsList.get(position).getItemMaxLimitation()){
                if (!((Activity) context).isFinishing ()) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Item Limitation")
                            .setContentText("Item Quantity is limited to "+ itemDetailsList.get(position).getItemMaxLimitation()+" for this item")
                            .show();
                }
                counter = counter + 0;
            }else if(counter>itemDetailsList.get(position).getItemMaxLimitation()){
                if (!((Activity) context).isFinishing ()) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Item Limitation")
                            .setContentText("Item Quantity is limited to "+ itemDetailsList.get(position).getItemMaxLimitation()+" for this item")
                            .show();
                }
                counter = counter + 0;
            }else {
                counter = counter + 1;
            }
        } else if (incDecIndicator == decrementIndicator) {
            counter = counter - 1;
        }
        holder.itemQtyText.setText(String.valueOf(counter));
        itemDetailsList.get(position).setItemCounter(counter);
        itemDetails.setItemCounter(counter);
        itemDetails.setItemBuyQuantity(counter);
        itemDetails.setTotalItemQtyPrice(counter*itemDetails.getItemPrice());
        holder.itemqtyPrice.setText("₹"+String.valueOf(itemDetails.getTotalItemQtyPrice()));
        databaseReference.child(String.valueOf(itemDetailsList.get(position).getItemId())).setValue(itemDetails);
    }
}
