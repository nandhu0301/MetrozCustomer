package com.smiligenceUAT1.metrozcustomer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;

import java.util.ArrayList;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.BOOLEAN_TRUE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PRODUCT_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.VIEW_CART_FIREBASE_TABLE;

public class ItemDetailsAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    int counter;
    ArrayList<ItemDetails> itemDetailsArrayList = new ArrayList<> ();
    DatabaseReference viewCartdatabaseRef, wishListRef, itemDataRef;
    ArrayList<ItemDetails> viewCartItemList = new ArrayList<ItemDetails> ();
    String selectedStoreName,selectedStorenNameStr;
    ItemDetails itemDetails;
    int incrementIndicator = 1;
    int decrementIndicator = 0;
    static  int pref=0;
    String saved_id;



    public ItemDetailsAdapter(Context context, ArrayList<ItemDetails> itemDetailsArrayList,
                              String selectedStoreName, String selectedStorenNameStr, ArrayList<ItemDetails> viewCartItemDetail) {
        this.context = context;
        this.itemDetailsArrayList = itemDetailsArrayList;
        inflater = (LayoutInflater.from ( context ));
        this.selectedStoreName = selectedStoreName;
        this.selectedStorenNameStr=selectedStorenNameStr;
        this.viewCartItemList = viewCartItemDetail;
    }

    public class ViewHolder
    {
        ImageView itemImage;
        TextView itemName, itemPrice, itemQuantity;
        RelativeLayout addItem, addQuantity;
        ImageView increaseQty, decreaseQty;
        TextView itemQtyText;
    }

    @Override
    public int getCount() {
        return itemDetailsArrayList.size ();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder ();
        View convertView = view;
        final SharedPreferences loginSharedPreferences = context.getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_id = loginSharedPreferences.getString("customerId", "");
        viewCartdatabaseRef = CommonMethods.fetchFirebaseDatabaseReference ( VIEW_CART_FIREBASE_TABLE ).child ( saved_id );
        wishListRef = CommonMethods.fetchFirebaseDatabaseReference( "WishList" ).child ( saved_id );
        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference ( PRODUCT_DETAILS_FIREBASE_TABLE );


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate ( R.layout.item_list_layout, viewGroup, false );
        }

        itemDetails = itemDetailsArrayList.get ( position );

        holder.itemName = (TextView) convertView.findViewById ( R.id.itemname );
        holder.itemImage = convertView.findViewById ( R.id.itemImage );
        holder.itemPrice = convertView.findViewById ( R.id.itemprice );
        holder.itemQuantity = convertView.findViewById ( R.id.itemqty );
        holder.addItem = convertView.findViewById ( R.id.additem );
        holder.addQuantity = convertView.findViewById ( R.id.addquantity );
        holder.increaseQty = convertView.findViewById ( R.id.incqty );
        holder.decreaseQty = convertView.findViewById ( R.id.decqty );
        holder.itemQtyText = convertView.findViewById ( R.id.textqty );




        holder.itemPrice.setText ( " ₹" + String.valueOf ( itemDetails.getItemPrice () ) );
        holder.itemQuantity.setText ( String.valueOf ( itemDetails.getItemQuantity () ) + " " + itemDetails.getQuantityUnits () );
        holder.itemName.setSelected ( BOOLEAN_TRUE );
        holder.itemName.setText ( itemDetails.getItemName () );

               if (itemDetails.getCategoryName().equalsIgnoreCase("GIFTS AND LIFESTYLES")) {
            if (itemDetails.getGiftWrapOption() != null) {
                if (itemDetails.getGiftWrapOption().equalsIgnoreCase("Yes")) {
                    holder.itemQuantity.setText("Giftwrap available");
                    holder.itemQuantity.setTextColor(Color.RED);
                }
            }
        } else {
            holder.itemQuantity.setTextColor(Color.GRAY);
            holder.itemQuantity.setText(String.valueOf(itemDetails.getItemQuantity()) + " " + itemDetails.getQuantityUnits());
        }


        if (!((Activity) context).isFinishing ()) {
            if(itemDetails.getItemImage().equals("")){


                int width = 0;
                int height = 0;
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
                holder.itemImage.setLayoutParams(parms);

            }else
            {
                if (!((Activity) context).isFinishing ()) {
                    Glide.with(context).load(itemDetails.getItemImage()).into(holder.itemImage);
                }
            }

        }
        if (viewCartItemList != null && viewCartItemList.size () > 0) {

            checkItemCounterValue ( position, holder );
        } else {
            holder.addQuantity.setVisibility ( View.INVISIBLE );
            holder.addItem.setVisibility ( View.VISIBLE );
        }

        holder.increaseQty.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                if (!((Activity) context).isFinishing ()) {
                    insertData(position, holder, incrementIndicator);

                    checkItemCounterValue(position, holder);
                }
            }
        } );

        holder.decreaseQty.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {


                insertData ( position, holder, decrementIndicator );

                checkItemCounterValue ( position, holder );
            }
        } );

        holder.addItem.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                checkItemCounterValue ( position, holder );

                if (!"0".equalsIgnoreCase ( selectedStoreName )) {
                    if (!(itemDetails.getSellerId ().equalsIgnoreCase ( selectedStoreName ))) {

                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( context );
                        bottomSheetDialog.setContentView ( R.layout.clearcart_confirmation );
                        Button cancel = bottomSheetDialog.findViewById ( R.id.cancel_conf );
                        Button clearcart = bottomSheetDialog.findViewById ( R.id.clearcart_conf );
                        TextView textforConfirmation = bottomSheetDialog.findViewById ( R.id.conf_text );

                        if (!((Activity) context).isFinishing ()) {
                            bottomSheetDialog.show ();

                            textforConfirmation.setText ( "Your Cart Contains items from " + selectedStorenNameStr + "." +
                                    "Do you want to clear the cart and add items from " + itemDetails.getStoreName () + "?" );
                            bottomSheetDialog.setCancelable ( false );
                        }

                        cancel.setOnClickListener ( new View.OnClickListener () {
                            @Override
                            public void onClick(View v) {
                                bottomSheetDialog.dismiss ();
                            }
                        } );

                        clearcart.setOnClickListener ( new View.OnClickListener () {
                            @Override
                            public void onClick(View v) {

                                viewCartdatabaseRef.removeValue ();
                                insertData ( position, holder, incrementIndicator );
                                bottomSheetDialog.dismiss ();

                            }
                        } );
                    } else {

                        insertData ( position, holder, incrementIndicator );
                    }
                } else {

                    insertData ( position, holder, incrementIndicator );
                }
            }
        } );

        return convertView;
    }

    public void checkItemCounterValue(int position, ViewHolder holder) {

        itemDetails = itemDetailsArrayList.get ( position );

        Iterator viewCartIterator = viewCartItemList.iterator ();

        while (viewCartIterator.hasNext ()) {
            ItemDetails viewCartItemDetails = (ItemDetails) viewCartIterator.next ();

            if (itemDetails.getItemId () == viewCartItemDetails.getItemId ()) {
                counter = viewCartItemDetails.getItemCounter ();
            } else {
                counter = itemDetails.getItemCounter ();
            }

            if (counter >= 1) {
                holder.addQuantity.setVisibility ( View.VISIBLE );
                holder.addItem.setVisibility ( View.INVISIBLE );
            } else if (counter == 0) {
                holder.addQuantity.setVisibility ( View.INVISIBLE );
                holder.addItem.setVisibility ( View.VISIBLE );
            }

            holder.itemQtyText.setText ( String.valueOf ( counter ) );
            itemDetailsArrayList.get ( position ).setItemCounter ( counter );
        }
    }

    private void insertData(int position, ViewHolder holder, int incDecIndicator) {

        itemDetails = itemDetailsArrayList.get ( position );

        counter = itemDetails.getItemCounter ();


        if (incDecIndicator == incrementIndicator) {


            if(counter==itemDetailsArrayList.get(position).getItemMaxLimitation()){

                if (!((Activity) context).isFinishing ()) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Item Limitation")
                            .setContentText("Item Quantity is limited to "+ itemDetailsArrayList.get(position).getItemMaxLimitation()+" for this item")
                            .show();
                }
                counter = counter + 0;
            }else if(counter>itemDetailsArrayList.get(position).getItemMaxLimitation()){
                if (!((Activity) context).isFinishing ()) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Item Limitation")
                            .setContentText("Item Quantity is limited to "+ itemDetailsArrayList.get(position).getItemMaxLimitation()+" for this item")
                            .show();
                }
                counter = counter + 0;

            }else {
                counter = counter + 1;
            }
        } else if (incDecIndicator == decrementIndicator) {
            counter = counter - 1;
        }
        if(pref==0) {
            if (counter == 0) {
                viewCartdatabaseRef.child(String.valueOf(itemDetailsArrayList.get(position).getItemId())).removeValue();
            } else {
                if (!((Activity) context).isFinishing()) {
                    holder.itemQtyText.setText(String.valueOf(counter));
                    itemDetailsArrayList.get(position).setItemCounter(counter);

                    itemDetails.setItemCounter(counter);
                    itemDetails.setItemBuyQuantity(counter);
                    itemDetails.setTotalItemQtyPrice(counter * itemDetails.getItemPrice());
                    viewCartdatabaseRef.child(String.valueOf(itemDetailsArrayList.get(position).getItemId())).setValue(itemDetails);
                }
            }
        }
    }
}

