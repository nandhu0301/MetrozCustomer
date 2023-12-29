package com.smiligenceUAT1.metrozcustomer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.OneTimeDiscount;

import java.util.List;

public class OneTimeDiscountAdapter extends BaseAdapter {

    private Context mcontext;
    private List<OneTimeDiscount> discountList;
    LayoutInflater inflater;

    public OneTimeDiscountAdapter(Context context, List<OneTimeDiscount> listDiscount) {
        mcontext = context;
        discountList = listDiscount;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return discountList.size();
    }

    @Override
    public Object getItem(int position) {
        return discountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView t_name, t_price_percent,t_minimumamount;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.one_time_disocunt_layout, parent, false);
            holder.t_name = row.findViewById(R.id.oneTimediscountName);
            holder.t_price_percent = row.findViewById(R.id.oneTimediscountType);
            holder.t_minimumamount=row.findViewById(R.id.oneTimediscountValid);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        OneTimeDiscount newDiscount = discountList.get(position);


            holder.t_name.setText(newDiscount.getCouponName());
            holder.t_price_percent.setText("Flat ₹"+newDiscount.getCouponAmount()+" offer");
            holder.t_minimumamount.setText("Valid on orders above ₹"+newDiscount.getTotalAmount());

        return row;
    }
}
