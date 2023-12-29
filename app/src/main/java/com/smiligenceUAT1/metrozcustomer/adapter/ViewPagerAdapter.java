package com.smiligenceUAT1.metrozcustomer.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.smiligenceUAT1.metrozcustomer.R;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    Fragment fragment = null;

    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        for (int i = 0; i < mNumOfTabs; i++) {
            if (i == position) {
                fragment = DynamicFragment.newInstance(mNumOfTabs);
                break;
            }
        }
        return fragment;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    public static class DynamicFragment extends Fragment {
        View view;


        public static DynamicFragment newInstance(int val) {
            DynamicFragment fragment = new DynamicFragment();
            Bundle args = new Bundle();
            args.putInt("someInt", val);
            fragment.setArguments(args);
            return fragment;
        }

        int val;
        TextView c;


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_list, container, false);
            val = getArguments().getInt("someInt", 0);
            c = view.findViewById(R.id.textView);
            c.setText("" + val);
            return view;
        }
    }
}
