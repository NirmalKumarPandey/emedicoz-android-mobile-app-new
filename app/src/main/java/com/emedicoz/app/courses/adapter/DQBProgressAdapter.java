package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.TestQuizActivity;
import com.emedicoz.app.courses.fragment.StudyFragment;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.Topics;
import com.emedicoz.app.utilso.Const;

import java.util.ArrayList;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class DQBProgressAdapter extends RecyclerView.Adapter<DQBProgressAdapter.DQBViewHolder> {

    Context activity;
    Fragment fragment;
    ArrayList<Topics> topicsArrayList;
    SingleCourseData singleCourseData;

    public DQBProgressAdapter(Context activity, Fragment fragment, ArrayList<Topics> topicsArrayList, SingleCourseData singleCourseData) {
        this.activity = activity;
        this.fragment = fragment;
        this.topicsArrayList = topicsArrayList;
        this.singleCourseData = singleCourseData;
    }

    @NonNull
    @Override
    public DQBViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dqb_progress_adapter_layout, viewGroup, false);
        return new DQBViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DQBViewHolder dqbViewHolder, int i) {
        Topics topics = topicsArrayList.get(i);
        dqbViewHolder.circularProgressDqbItem.setProgressTextAdapter(((StudyFragment) fragment).TIME_TEXT_ADAPTER);

        if (getPercentage(topics.getTotal(), topics.getCompleted()) <= 33) {
            dqbViewHolder.circularProgressDqbItem.setProgressColor(ContextCompat.getColor(activity, R.color.progress_hard_red));
        } else if (getPercentage(topics.getTotal(), topics.getCompleted()) <= 66) {
            dqbViewHolder.circularProgressDqbItem.setProgressColor(ContextCompat.getColor(activity, R.color.progress_medium_yellow));
        } else {
            dqbViewHolder.circularProgressDqbItem.setProgressColor(ContextCompat.getColor(activity, R.color.progress_easy_green));
        }

        if (!TextUtils.isEmpty(topics.getCompleted())) {
            dqbViewHolder.circularProgressDqbItem.setProgress(Double.parseDouble(topics.getCompleted()), Double.parseDouble(topics.getTotal()));
            dqbViewHolder.tvReadCount.setText(topics.getCompleted() + "/");
        } else {
            dqbViewHolder.circularProgressDqbItem.setProgress(0, Double.parseDouble(topics.getTotal()));
            dqbViewHolder.tvReadCount.setText("0/");
        }

        dqbViewHolder.tvTotalCount.setText(topics.getTotal());
        dqbViewHolder.tvDqbTitle.setText(topics.getTitle());

        dqbViewHolder.itemView.setOnClickListener((View view) -> {
            Intent intent = new Intent(activity, TestQuizActivity.class);
            intent.putExtra(Const.TOPIC, topicsArrayList.get(i));
            intent.putExtra(Const.COURSE_ID, singleCourseData.getId());
            intent.putExtra("is_purchased", singleCourseData.getIs_purchased());
            intent.putExtra(Const.TITLE, topicsArrayList.get(i).getTitle());
            intent.putExtra("titlefrag", topicsArrayList.get(i).getTitle());
            intent.putExtra(Const.COURSE_DESC, singleCourseData);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topicsArrayList.size();
    }

    class DQBViewHolder extends RecyclerView.ViewHolder {

        CircularProgressIndicator circularProgressDqbItem;
        TextView tvReadCount, tvTotalCount, tvDqbTitle;

        public DQBViewHolder(@NonNull View itemView) {
            super(itemView);

            circularProgressDqbItem = itemView.findViewById(R.id.circular_progress_dqb_item);
            tvReadCount = itemView.findViewById(R.id.tv_read_count);
            tvTotalCount = itemView.findViewById(R.id.tv_total_count);
            tvDqbTitle = itemView.findViewById(R.id.tv_dqb_title);
        }
    }

    private int getPercentage(String total, String complete) {
        if (TextUtils.isEmpty(complete) || complete.equals("0")) {
            return 0;
        }
        return Integer.parseInt(complete) * 100 / Integer.parseInt(total);
    }

}