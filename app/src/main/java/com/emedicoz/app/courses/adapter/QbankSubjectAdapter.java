package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.TestQuizActivity;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.Topics;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;

public class QbankSubjectAdapter extends RecyclerView.Adapter<QbankSubjectAdapter.QbankSubjectViewHolder> {

    Activity activity;
    Topics[] topics;
    SingleCourseData singleCourseData;

    public QbankSubjectAdapter(Activity activity, Topics[] topics, SingleCourseData singleCourseData) {
        this.activity = activity;
        this.topics = topics;
        this.singleCourseData = singleCourseData;
    }

    @NonNull
    @Override
    public QbankSubjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_qbank_subject, viewGroup, false);
        return new QbankSubjectViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull QbankSubjectViewHolder holder, final int i) {
        holder.title.setText(topics[i].getTitle());
        if (topics[i].getSub_img_url() != null && !topics[i].getSub_img_url().equalsIgnoreCase("")) {
            Glide.with(activity).load(topics[i].getSub_img_url()).into(holder.topicIV);
        }
        if (topics[i].getCompleted().equals("")) {
            holder.completedCount.setText("0/" + topics[i].getTotal() + " completed");
        } else {
            holder.completedCount.setText(topics[i].getCompleted() + "/" + topics[i].getTotal() + " completed");
            //progress bar percentage added and call the progress bar
            //holder.progressPercentage.setVisibility(View.VISIBLE);
            //holder.progressPercentage.setProgress((int) topics[i].getProgress());
        }

        if (!GenericUtils.isEmpty(topics[i].getIs_new()) && topics[i].getIs_new().equals("1"))
            holder.newTV.setVisibility(View.VISIBLE);
        else
            holder.newTV.setVisibility(View.GONE);


        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TestQuizActivity.class);
            intent.putExtra(Const.TOPIC, topics[i]);
            intent.putExtra(Const.COURSE_ID, singleCourseData.getId());
            intent.putExtra(Constants.Extras.IS_PURCHASED, singleCourseData.getIs_purchased());
            intent.putExtra(Const.TITLE, topics[i].getTitle());
            intent.putExtra(Constants.Extras.TITLE_FRAG, topics[i].getTitle());
            intent.putExtra(Const.COURSE_DESC, singleCourseData);
            activity.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return topics.length;
    }

    class QbankSubjectViewHolder extends RecyclerView.ViewHolder {

        TextView title, completedCount;
        ImageView topicIV;
        CardView cardView;
        TextView newTV;
        ProgressBar progressPercentage;

        public QbankSubjectViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            topicIV = itemView.findViewById(R.id.topicIV);
            completedCount = itemView.findViewById(R.id.completedCountTV);
            cardView = itemView.findViewById(R.id.parentCV);
            newTV = itemView.findViewById(R.id.newTV);
            progressPercentage = itemView.findViewById(R.id.progressPercentage);
        }
    }
}
