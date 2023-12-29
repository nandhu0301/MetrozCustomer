package com.smiligenceUAT1.metrozcustomer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.OrderDetails;

import java.util.HashMap;
import java.util.List;

public class CustomerOrdersAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> uniqueDateList;

    private HashMap<String, List<OrderDetails>> expandableListDetail;

    String cus_name, billNum, finalAmount, timeStamp, paymentMode, orderStatusText;

    public CustomerOrdersAdapter(Context context, List<String> uniqueDateList,
                                 HashMap<String, List<OrderDetails>> expandableListDetail) {
        this.context = context;
        this.uniqueDateList = uniqueDateList;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {

        return this.expandableListDetail.get ( this.uniqueDateList.get ( listPosition ) )
                .get ( expandedListPosition );
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
            convertView = layoutInflater.inflate ( R.layout.listofitem, null );
        }
        TextView expandedListTextView = convertView
                .findViewById ( R.id.listItem );
        TextView billNumText = convertView.findViewById ( R.id.billnumber );
        TextView timeStampText = convertView.findViewById ( R.id.timeList );
        TextView finalBillAmountText = convertView.findViewById ( R.id.finalbilllist );
        TextView orderStatus = convertView.findViewById ( R.id.orderStatus );
        TextView orderTime=convertView.findViewById(R.id.timelistnew);
        OrderDetails orderDetails = this.expandableListDetail.get(this.uniqueDateList.get(listPosition))
                .get(expandedListPosition);

        cus_name = orderDetails.getCustomerName();
        billNum = orderDetails.getOrderId();
        timeStamp = orderDetails.getPaymentDate();
        finalAmount = String.valueOf(orderDetails.getPaymentamount());
        paymentMode = orderDetails.getPaymentType();
        orderStatusText=orderDetails.getOrderStatus();
        expandedListTextView.setText ( cus_name );
        billNumText.setText ( "#" + billNum );
        timeStampText.setText ( timeStamp );
        finalBillAmountText.setText ( "₹" + String.valueOf ( finalAmount ) );
        orderStatus.setText ( orderStatusText );
        orderTime.setText(orderDetails.getOrderTime());
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get ( this.uniqueDateList.get ( listPosition ) )
                .size ();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.uniqueDateList.get ( listPosition );
    }

    @Override
    public int getGroupCount() {
        return this.uniqueDateList.size ();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup ( listPosition );
        final String[] day = {(String) getGroup ( listPosition )};

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
            convertView = layoutInflater.inflate ( R.layout.list_group, null );
        }
        TextView listTitleTextView = convertView
                .findViewById ( R.id.lblListHeader );

        listTitleTextView.setText ( listTitle );
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}