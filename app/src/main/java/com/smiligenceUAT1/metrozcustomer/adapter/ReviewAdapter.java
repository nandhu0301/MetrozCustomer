package com.smiligenceUAT1.metrozcustomer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.smiligenceUAT1.metrozcustomer.R;
import com.smiligenceUAT1.metrozcustomer.bean.ReviewAndRatings;

import java.util.List;

public class ReviewAdapter  extends BaseAdapter
{

    private Context mcontext;
    private List<ReviewAndRatings> itemReviewAndRatingsList;
    LayoutInflater inflater;


    public ReviewAdapter(Context context, List<ReviewAndRatings> itemReviewAndRatings) {
        mcontext = context;
        itemReviewAndRatingsList = itemReviewAndRatings;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return itemReviewAndRatingsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemReviewAndRatingsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView nameOfAPerson, reviewComments;
        RatingBar ratingBar;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ReviewAdapter.ViewHolder holder = new ReviewAdapter.ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.review_layout, parent, false);


            holder.nameOfAPerson = row.findViewById(R.id.personName);
            holder.reviewComments = row.findViewById(R.id.comments);
            holder.ratingBar = row.findViewById(R.id.ratingBar);
            row.setTag(holder);
        } else {
            holder = (ReviewAdapter.ViewHolder) row.getTag();
        }

        ReviewAndRatings itemReviewAndRatings =itemReviewAndRatingsList.get(position);


            holder.nameOfAPerson.setText(itemReviewAndRatings.getCustomerName());
            holder.reviewComments.setText(itemReviewAndRatings.getReviews());
            holder.ratingBar.setRating( itemReviewAndRatings.getStars());
        return row;
    }
}