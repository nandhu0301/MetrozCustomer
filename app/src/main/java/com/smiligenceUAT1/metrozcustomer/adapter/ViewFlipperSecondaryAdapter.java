package com.smiligenceUAT1.metrozcustomer.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.AdvertisementDetails;
import com.smiligenceUAT1.metrozcustomer.common.Interface;

import java.util.ArrayList;

public class ViewFlipperSecondaryAdapter extends BaseAdapter {

    private ArrayList<AdvertisementDetails> androidVersions;
    private Context mContext;
    private Interface.ClickedListener clickedListener;


    public ViewFlipperSecondaryAdapter(Context context, ArrayList<AdvertisementDetails> androidVersions, Interface.ClickedListener clickedListener) {
        this.mContext = context;
        this.androidVersions = androidVersions;
        this.clickedListener = clickedListener;
    }

    @Override
    public int getCount() {
        return androidVersions.size();
    }

    @Override
    public Object getItem(int position) {
        return androidVersions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.advertisementimages, parent, false);
        }

    AdvertisementDetails version = androidVersions.get(position);
    ImageView imageView = convertView.findViewById(R.id.advImage);

    if (!((Activity) mContext).isFinishing()) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.error(R.mipmap.ic_launcher);
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(version.getImage()).fitCenter().into(imageView);
    }
    imageView.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            clickedListener.onPictureClicked(position);
        }
    });
        return convertView;
    }
}