package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.TestSeries;

import java.util.ArrayList;

public class TestQuizCourseAdapterOne extends RecyclerView.Adapter<TestQuizCourseAdapterOne.TestQuizCourseViewHolderOne> {

    Activity activity;
    ArrayList<String> topics;
    ArrayList<TestSeries> test_series;
    ArrayList<TestSeries> testSeriesByTopic;
    String is_purchased;
    SingleCourseData singleCourseData;

    public TestQuizCourseAdapterOne(Activity activity, ArrayList<String> topics, ArrayList<TestSeries> test_series, String is_purchased) {
        this.activity = activity;
        this.topics = topics;
        this.test_series = test_series;
        this.is_purchased = is_purchased;
        testSeriesByTopic = new ArrayList<>();
    }

    public TestQuizCourseAdapterOne(Activity activity, ArrayList<String> topics, ArrayList<TestSeries> test_series, String is_purchased, SingleCourseData singleCourseData) {
        this.activity = activity;
        this.topics = topics;
        this.test_series = test_series;
        this.is_purchased = is_purchased;
        this.singleCourseData = singleCourseData;
        testSeriesByTopic = new ArrayList<>();
    }

    @NonNull
    @Override
    public TestQuizCourseViewHolderOne onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_child_test_quiz_course_one, viewGroup, false);
        return new TestQuizCourseViewHolderOne(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestQuizCourseViewHolderOne holder, int position) {

/*        holder.textView.setVisibility(View.VISIBLE);
        holder.textView.setText(topics.get(position));*/
        if (test_series.size() > 0) {
            if (testSeriesByTopic.size() > 0) {
                testSeriesByTopic.clear();
            }
            for (int i = 0; i < test_series.size(); i++) {
                if (topics.get(position).equalsIgnoreCase(test_series.get(i).getTopic())) {
                    testSeriesByTopic.add(test_series.get(i));
                }
            }
            if (testSeriesByTopic != null) {
                if (testSeriesByTopic.size() > 0) {
                    holder.linearlayoutChildOne.setVisibility(View.VISIBLE);
                    holder.textView.setVisibility(View.VISIBLE);
                    holder.recyclerView.setVisibility(View.VISIBLE);
                    holder.textView.setText(topics.get(position).toUpperCase());
                    holder.recyclerView.setAdapter(new TestQuizCourseAdapter(activity, testSeriesByTopic, is_purchased, singleCourseData));
                } else {
                    holder.textView.setVisibility(View.GONE);
                    holder.linearlayoutChildOne.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public class TestQuizCourseViewHolderOne extends RecyclerView.ViewHolder {

        TextView textView;
        RecyclerView recyclerView;
        LinearLayout linearlayoutChildOne;

        public TestQuizCourseViewHolderOne(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.masterTitleOne);
            linearlayoutChildOne = itemView.findViewById(R.id.linearlayoutChildOne);
            recyclerView = itemView.findViewById(R.id.recyclerViewChildOne);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        }
    }
}
