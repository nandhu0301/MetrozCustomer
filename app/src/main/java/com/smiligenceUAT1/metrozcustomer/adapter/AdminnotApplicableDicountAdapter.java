package com.smiligenceUAT1.metrozcustomer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.AdminDiscounts;

import java.util.List;

public class AdminnotApplicableDicountAdapter extends BaseAdapter {

    private Context mcontext;
    private List<AdminDiscounts> discountList;
    LayoutInflater inflater;

    public AdminnotApplicableDicountAdapter(Context context, List<AdminDiscounts> listDiscount) {
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
        TextView t_name, t_price_percent, t_minimumamount;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AdminnotApplicableDicountAdapter.ViewHolder holder = new AdminnotApplicableDicountAdapter.ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.not_applicable_discount_adapter_grid, parent, false);
            holder.t_name = row.findViewById(R.id.discountName);
            holder.t_price_percent = row.findViewById(R.id.discountType);
            holder.t_minimumamount = row.findViewById(R.id.discountValid);
            row.setTag(holder);
        } else {
            holder = (AdminnotApplicableDicountAdapter.ViewHolder) row.getTag();
        }

        AdminDiscounts newDiscount = discountList.get(position);

        if (newDiscount.getTypeOfDiscount().equals("Price")) {
            holder.t_name.setText(newDiscount.getDiscountName());
            holder.t_price_percent.setText("Flat ₹" + newDiscount.getDiscountPrice() + " offer");
            holder.t_minimumamount.setText("Valid on orders above ₹" + newDiscount.getMinmumBillAmount());
        } else {
            holder.t_name.setText(newDiscount.getDiscountName());
            holder.t_price_percent.setText(newDiscount.getDiscountPercentageValue() + "%" + " OFFER");
            holder.t_minimumamount.setText("Valid on orders above ₹" + newDiscount.getMinmumBillAmount());
        }
        return row;
    }
}
