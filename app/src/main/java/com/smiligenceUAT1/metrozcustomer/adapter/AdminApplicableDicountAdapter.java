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

public class AdminApplicableDicountAdapter extends BaseAdapter {

    private Context mcontext;
    private List<AdminDiscounts> AdminDiscounts;
    LayoutInflater inflater;

    public AdminApplicableDicountAdapter(Context context, List<AdminDiscounts> AdminDiscounts1) {
        mcontext = context;
        AdminDiscounts = AdminDiscounts1;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return AdminDiscounts.size();
    }

    @Override
    public Object getItem(int position) {
        return AdminDiscounts.get(position);
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
        AdminApplicableDicountAdapter.ViewHolder holder = new AdminApplicableDicountAdapter.ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.discount_adapter_grid, parent, false);
            holder.t_name = row.findViewById(R.id.discountName);
            holder.t_price_percent = row.findViewById(R.id.discountType);
            holder.t_minimumamount = row.findViewById(R.id.discountValid);
            row.setTag(holder);
        } else {
            holder = (AdminApplicableDicountAdapter.ViewHolder) row.getTag();
        }

        AdminDiscounts newDiscount = AdminDiscounts.get(position);

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