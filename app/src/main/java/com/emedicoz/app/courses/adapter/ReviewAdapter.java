package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.courses.Reviews;
import com.emedicoz.app.utilso.Helper;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    Context context;
    List<Reviews> descCourseReviewList;

    // calling from SingleOverviewAdapter, SingleCourse, CourseReviews, InstructorFragment for review list.
    public ReviewAdapter(Context context, List<Reviews> descCourseReviewList) {
        this.context = context;
        this.descCourseReviewList = descCourseReviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_row_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Reviews reviews = descCourseReviewList.get(position);

        holder.reviewerNameTV.setText(Helper.CapitalizeText(reviews.getName()));
        holder.reviewTimeTV.setText(DateUtils.getRelativeDateTimeString(context, Long.parseLong(reviews.getCreation_time()), 0, 1, DateUtils.FORMAT_SHOW_TIME));
        holder.reviewRatingRB.setRating(Float.parseFloat(reviews.getRating()));
        holder.reviewTextTv.setText(reviews.getText());
    }

    @Override
    public int getItemCount() {
        return descCourseReviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView reviewerNameTV, reviewTimeTV, reviewTextTv;
        RatingBar reviewRatingRB;

        public ReviewViewHolder(@NonNull View view) {
            super(view);

            reviewerNameTV = view.findViewById(R.id.nameTV);
            reviewRatingRB = view.findViewById(R.id.ratingRB);
            reviewTextTv = view.findViewById(R.id.descriptionTV);
            reviewTimeTV = view.findViewById(R.id.timeTV);
        }
    }
}
