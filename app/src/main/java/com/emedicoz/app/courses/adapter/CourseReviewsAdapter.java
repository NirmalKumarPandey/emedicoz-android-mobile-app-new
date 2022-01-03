package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.fragment.CourseReviews;
import com.emedicoz.app.modelo.courses.Reviews;

import java.util.List;

/**
 * Created by Abhi on 9/28/2017.
 */

public class CourseReviewsAdapter extends RecyclerView.Adapter<CourseReviewsAdapter.ReviewsHolder> {
    List<Reviews> reviews;
    Context context;
    CourseReviews courseReviews;

    View.OnClickListener onEditButtonClickListener = (View v) -> {
        Reviews reviews = (Reviews) v.getTag();
        courseReviews.clearText(reviews);
    };

    public CourseReviewsAdapter(Context courseReviews, List<Reviews> reviewses, CourseReviews reviews) {
        this.context = courseReviews;
        this.reviews = reviewses;
        this.courseReviews = reviews;
    }

    @Override
    public ReviewsHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_review, parent, false);
        return new ReviewsHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsHolder reviewsHolder, int i) {
        reviewsHolder.reviewerNameTV.setText(reviews.get(i).getName());
        reviewsHolder.reviewTimeTV.setText(DateUtils.getRelativeDateTimeString(context, Long.parseLong(reviews.get(i).getCreation_time()), 0, 1, DateUtils.FORMAT_SHOW_TIME));
        reviewsHolder.reviewRatingRB.setRating(Float.parseFloat(reviews.get(i).getRating()));
        reviewsHolder.reviewTextTv.setText(reviews.get(i).getText());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewsHolder extends RecyclerView.ViewHolder {
        TextView reviewerNameTV;
        TextView reviewTimeTV;
        TextView reviewTextTv;
        RatingBar reviewRatingRB;

        public ReviewsHolder(View itemView) {
            super(itemView);
            reviewerNameTV = itemView.findViewById(R.id.nameTV);
            reviewRatingRB = itemView.findViewById(R.id.ratingRB);
            reviewTextTv = itemView.findViewById(R.id.descriptionTV);
            reviewTimeTV = itemView.findViewById(R.id.timeTV);
        }
    }
}
