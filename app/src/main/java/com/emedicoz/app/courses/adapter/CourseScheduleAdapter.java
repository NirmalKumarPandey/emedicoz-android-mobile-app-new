package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.liveclass.courses.DescCourseSchedule;

import java.util.List;

public class CourseScheduleAdapter extends RecyclerView.Adapter<CourseScheduleAdapter.ReviewsHolder> {
    Activity activity;
    List<DescCourseSchedule> descCourseSchedules;

    public CourseScheduleAdapter(Activity activity, List<DescCourseSchedule> descCourseSchedules) {
        this.activity = activity;
        this.descCourseSchedules = descCourseSchedules;
    }

    @Override
    public CourseScheduleAdapter.ReviewsHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_schedule_layout, parent, false);
        return new ReviewsHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseScheduleAdapter.ReviewsHolder reviewsHolder, int i) {
        reviewsHolder.courseDateTV.setText(descCourseSchedules.get(i).getDate());
        reviewsHolder.courseTimeTV.setText(descCourseSchedules.get(i).getTime());
        reviewsHolder.titleNameTV.setText(descCourseSchedules.get(i).getTitle());

    }

    @Override
    public int getItemCount() {
        return descCourseSchedules.size();
    }

    public class ReviewsHolder extends RecyclerView.ViewHolder {
        TextView courseDateTV, titleNameTV, courseTimeTV;

        public ReviewsHolder(View itemView) {
            super(itemView);
            courseDateTV = itemView.findViewById(R.id.courseDateTV);
            titleNameTV = itemView.findViewById(R.id.titleNameTV);
            courseTimeTV = itemView.findViewById(R.id.courseTimeTV);

        }
    }
}
