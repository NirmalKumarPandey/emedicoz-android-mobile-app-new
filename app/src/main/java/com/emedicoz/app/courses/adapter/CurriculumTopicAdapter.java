package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.NewTestActivity;
import com.emedicoz.app.courses.activity.TestQuizActivity;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.Topics;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;

public class CurriculumTopicAdapter extends RecyclerView.Adapter<CurriculumTopicAdapter.ReviewsHolder> {

    String isPurchased;
    String courseType = "";
    String startDate = "";
    String endDate = "";
    SingleCourseData singleCourseData;
    private Activity activity;
    private Topics[] topics;
    private String courseId;
    private String titleString;

    public CurriculumTopicAdapter(Activity activity, Topics[] topics, String courseId, String title, String isPurchased) {
        this.activity = activity;
        this.topics = topics;
        this.courseId = courseId;
        this.titleString = title;
        this.isPurchased = isPurchased;
    }

    public CurriculumTopicAdapter(Activity activity, Topics[] topics, String courseId, String title, String isPurchased, String courseType) {
        this.activity = activity;
        this.topics = topics;
        this.courseId = courseId;
        this.titleString = title;
        this.isPurchased = isPurchased;
        this.courseType = courseType;
    }

    public CurriculumTopicAdapter(Activity activity, Topics[] topics, String courseId, String title, String isPurchased, String courseType, SingleCourseData singleCourseData) {
        this.activity = activity;
        this.topics = topics;
        this.courseId = courseId;
        this.titleString = title;
        this.isPurchased = isPurchased;
        this.courseType = courseType;
        this.singleCourseData = singleCourseData;
    }

    public CurriculumTopicAdapter(Activity activity, Topics[] topics, String courseId, String title, String isPurchased, String courseType, SingleCourseData singleCourseData, String startDate, String endDate) {
        this.activity = activity;
        this.topics = topics;
        this.courseId = courseId;
        this.titleString = title;
        this.isPurchased = isPurchased;
        this.courseType = courseType;
        this.singleCourseData = singleCourseData;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public ReviewsHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_adapter, parent, false);
        return new ReviewsHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsHolder reviewsHolder, int i) {
        reviewsHolder.setData(topics[i]);
        Log.e("COURSE_TYPE", courseType);
    }

    @Override
    public int getItemCount() {
        return topics.length;
    }

    public class ReviewsHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        ImageView topicIV;
        ImageView backRightIV;
        private CardView parentCV;
        TextView completedCountTV;
        private TextView title;

        public ReviewsHolder(View itemView) {
            super(itemView);
            parentCV = itemView.findViewById(R.id.parentCV);
            title = itemView.findViewById(R.id.title);
            progressBar = itemView.findViewById(R.id.progressBarTopic);
            completedCountTV = itemView.findViewById(R.id.completedCountTV);
            topicIV = itemView.findViewById(R.id.topicIV);
            backRightIV = itemView.findViewById(R.id.backRightIV);
        }

        public void setData(final Topics topic) {
            if (!GenericUtils.isEmpty(topic.getSub_img_url())) {
                Glide.with(activity).load(topic.getSub_img_url()).into(topicIV);
            }

            String completed = "";
            String total = "";
            if (!GenericUtils.isEmpty(topic.getTotal())) {
                total = topic.getTotal();
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(Integer.parseInt(total));
            } else {
                total = "0";
                progressBar.setMax(0);
            }

            if (!GenericUtils.isEmpty(topic.getCompleted())) {
                completed = topic.getCompleted();
                progressBar.setProgress(Integer.parseInt(topic.getCompleted()));
            } else {
                completed = "0";
                progressBar.setProgress(0);
            }
            completedCountTV.setText(completed + "/" + total + " modules completed");

            final Long currentTimeStamp = System.currentTimeMillis();
            if (!GenericUtils.isEmpty(startDate)) {
                if (currentTimeStamp < Long.parseLong(startDate)) {

                    backRightIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.lock));
                } else {
                    backRightIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.back_right));
                }
            }
            title.setText(topic.getTitle());
            parentCV.setOnClickListener((View view) -> {

                if (courseType.equalsIgnoreCase("3")) {

                    Intent intent = new Intent(activity, TestQuizActivity.class);
                    intent.putExtra(Const.TOPIC, topic);
                    intent.putExtra(Const.COURSE_ID, courseId);
                    intent.putExtra("is_purchased", isPurchased);
                    intent.putExtra(Const.TITLE, titleString);
                    intent.putExtra("titlefrag", topic.getTitle());
                    intent.putExtra(Const.COURSE_DESC, singleCourseData);
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, NewTestActivity.class);
                    intent.putExtra(Const.TOPIC, topic);
                    intent.putExtra(Const.COURSE_ID, courseId);
                    intent.putExtra("is_purchased", isPurchased);
                    intent.putExtra(Const.TITLE, titleString);
                    intent.putExtra("titlefrag", topic.getTitle());
                    intent.putExtra("position", getAdapterPosition() + 1);
                    intent.putExtra(Const.COURSE_DESC, singleCourseData);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
