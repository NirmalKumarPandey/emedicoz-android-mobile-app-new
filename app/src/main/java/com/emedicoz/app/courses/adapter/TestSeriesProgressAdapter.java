package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.courses.TestSeries;

import java.util.List;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class TestSeriesProgressAdapter extends RecyclerView.Adapter<TestSeriesProgressAdapter.TestSeriesProgressViewHolder> {

    Activity activity;
    List<TestSeries> arrayList;

    public TestSeriesProgressAdapter(Activity activity, List<TestSeries> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public TestSeriesProgressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.test_series_progress_adapter, viewGroup, false);
        return new TestSeriesProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestSeriesProgressViewHolder holder, int i) {

        holder.testSeriesNameTV.setText(arrayList.get(i).getTest_series_name());
        holder.progressIndicator.setProgress(100, 100);
        if (arrayList.get(i).getIs_paused().equals("0")) {
            holder.progressIndicator.setProgressColor(ContextCompat.getColor(activity, R.color.progress_complete));
        } else if (arrayList.get(i).getIs_paused().equals("1")) {
            holder.progressIndicator.setProgressColor(ContextCompat.getColor(activity, R.color.progress_paused));
        } else {
            holder.progressIndicator.setProgressColor(ContextCompat.getColor(activity, R.color.progress_start));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class TestSeriesProgressViewHolder extends RecyclerView.ViewHolder {

        TextView testSeriesNameTV;
        CircularProgressIndicator progressIndicator;

        public TestSeriesProgressViewHolder(@NonNull View itemView) {
            super(itemView);

            testSeriesNameTV = itemView.findViewById(R.id.testSeriesNameTV);
            progressIndicator = itemView.findViewById(R.id.circular_progress_test_item);
        }
    }
}
