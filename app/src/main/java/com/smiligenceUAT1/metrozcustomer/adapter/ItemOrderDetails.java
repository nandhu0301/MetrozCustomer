package com.smiligenceUAT1.metrozcustomer.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.ItemDetails;
import com.smiligenceUAT1.metrozcustomer.common.CommonMethods;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.text.WordUtils.capitalize;

public class ItemOrderDetails extends BaseAdapter {

    private Context mcontext;
    private List<ItemDetails> itemList;
    LayoutInflater inflater;
    private List<String> giftWrappingDetails;
    ArrayList<String> giftWrappingOption=new ArrayList<>();
    DatabaseReference itemRatingsAndReviewsRef;
    String orderIdForItemRatings;


    public ItemOrderDetails(Context context, List<ItemDetails> itemListŇew, List<String> orderDetails1,String orderIdForItemRatings1) {
        mcontext = context;
        itemList = itemListŇew;
        inflater = (LayoutInflater.from(context));
        giftWrappingDetails=orderDetails1;
        orderIdForItemRatings=orderIdForItemRatings1;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView images;
        TextView t_name, t_price_percent, t_total_amount;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        giftWrappingOption.clear();
        itemRatingsAndReviewsRef= CommonMethods.fetchFirebaseDatabaseReference("ItemRatingsAndReview");

        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_details_layout, parent, false);

            holder.t_name = row.findViewById(R.id.itemName);
            holder.t_price_percent = row.findViewById(R.id.item_qty);
            holder.t_total_amount = row.findViewById(R.id.itemTotal);


            holder.images = (ImageView) row.findViewById(R.id.itemImage);
            row.setTag(holder);
        } else {

            holder = (ViewHolder) row.getTag();
        }



        ItemDetails itemDetailsObj = itemList.get(position);

        ViewHolder finalHolder = holder;
        if (itemDetailsObj.getCategoryName().equals("GIFTS AND LIFESTYLES"))
        {
            if (giftWrappingDetails.size()>0 && giftWrappingDetails!=null) {
                for (int i = 0; i < giftWrappingDetails.size(); i++) {
                    if (giftWrappingDetails.get(i).equals(itemDetailsObj.getItemName())) {
                        holder.t_name.setText(capitalize(itemDetailsObj.getItemName().toLowerCase()) + " (Requested for gift wrap)");
                        holder.t_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_giftsred_01, 0);
                        break;
                    } else {
                        holder.t_name.setText(capitalize(itemDetailsObj.getItemName().toLowerCase()));
                    }
                }
            }else
            {
                holder.t_name.setText(capitalize(itemDetailsObj.getItemName().toLowerCase()));
            }

        }
        else
        {
            holder.t_name.setText(capitalize(itemDetailsObj.getItemName().toLowerCase()));
        }

        holder.t_price_percent.setText(itemDetailsObj.getItemPrice() + " * " + itemDetailsObj.getItemBuyQuantity());
        holder.t_total_amount.setText("₹" + String.valueOf(itemDetailsObj.getTotalItemQtyPrice()));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.error(R.mipmap.ic_launcher);
        if (!((Activity) mcontext).isFinishing ()) {
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(itemDetailsObj.getItemImage()).fitCenter().into(holder.images);
        }
        return row;


    }
}
