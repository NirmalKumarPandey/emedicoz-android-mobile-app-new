package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.liveclass.courses.DescCourseReview;
import com.emedicoz.app.utilso.GenericUtils;

import java.util.List;

public class DescriptionReviewAdapter extends RecyclerView.Adapter<DescriptionReviewAdapter.ReviewsHolder> {
    Activity activity;
    List<DescCourseReview> courseReviewList;
    String screenName = "";

    public DescriptionReviewAdapter(Activity activity, List<DescCourseReview> courseReview, String screenName) {
        this.activity = activity;
        this.courseReviewList = courseReview;
        this.screenName = screenName;
    }

    public void setList(List<DescCourseReview> reviews) {
        this.courseReviewList = reviews;
    }

    @Override
    public DescriptionReviewAdapter.ReviewsHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_review, parent, false);
        return new ReviewsHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsHolder reviewsHolder, int i) {
        reviewsHolder.reviewerNameTV.setText(courseReviewList.get(i).getName());
        reviewsHolder.reviewTimeTV.setText(DateUtils.getRelativeDateTimeString(activity, Long.parseLong(courseReviewList.get(i).getCreationTime()), 0, 1, DateUtils.FORMAT_SHOW_TIME));
        reviewsHolder.reviewRatingRB.setRating(Float.parseFloat(courseReviewList.get(i).getRating()));
        reviewsHolder.reviewTextTv.setText(courseReviewList.get(i).getText());

    }

    @Override
    public int getItemCount() {
        if (screenName.equalsIgnoreCase("ALL REVIEW") && courseReviewList != null) {
            return courseReviewList.size();
        }
        if (!GenericUtils.isListEmpty(courseReviewList) && courseReviewList.size() < 2)
            return courseReviewList.size();
        else if (courseReviewList.size() == 0) {
            return 0;
        } else return 2;
    }

    public class ReviewsHolder extends RecyclerView.ViewHolder {
        TextView reviewerNameTV, reviewTimeTV, reviewTextTv;
        RatingBar reviewRatingRB;
        ImageButton editReviewIBtn;

        public ReviewsHolder(View itemView) {
            super(itemView);
            reviewerNameTV = itemView.findViewById(R.id.nameTV);
            reviewRatingRB = itemView.findViewById(R.id.ratingRB);
            reviewTextTv = itemView.findViewById(R.id.descriptionTV);
            reviewTimeTV = itemView.findViewById(R.id.timeTV);
            editReviewIBtn = itemView.findViewById(R.id.editReviewIBtn);
        }
    }
}
