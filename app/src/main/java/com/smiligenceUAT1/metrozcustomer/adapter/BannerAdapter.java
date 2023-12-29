package com.smiligenceUAT1.metrozcustomer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.infinitebanner.AbsBannerAdapter;
import com.github.infinitebanner.InfiniteBannerView;

import java.util.ArrayList;

public class BannerAdapter extends AbsBannerAdapter {

    ArrayList<Integer> imageList=new ArrayList<>();
    Context context;

    @Override
    public int getCount() {
        return imageList.size();
    }

    public BannerAdapter(Context context1, ArrayList<Integer> imageList1)
    {

        context=context1;
        imageList=imageList1;

    }

    @Override
    protected View makeView(InfiniteBannerView parent) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    protected void bind(View view, int position) {

        for (int i=0;i<imageList.size();i++)
        {

            if (position == i) {
                ((ImageView) view).setImageResource(imageList.get(i));
            }else
            {
                ((ImageView) view).setImageResource(imageList.get(0));
            }
        }

      /*  if (position == 0) {
            ((ImageView) view).setImageResource(R.drawable.img_0);
        } else if (position == 1) {
            ((ImageView) view).setImageResource(R.drawable.img_1);
        }else if (position==2)
        {
            ((ImageView) view).setImageResource(R.drawable.groceriesmod1);
        } else{
            ((ImageView) view).setImageResource(R.drawable.img_2);
        }*/
    }
}
