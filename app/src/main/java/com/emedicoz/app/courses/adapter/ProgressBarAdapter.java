package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.SubjectWiseResultSubject;
import com.emedicoz.app.utilso.Helper;

import java.util.List;

public class ProgressBarAdapter extends RecyclerView.Adapter<ProgressBarAdapter.ProgressBarViewHolder> {

    Activity activity;
    List<SubjectWiseResultSubject> subjectWiseResults;
    String type;

    public ProgressBarAdapter(Activity activity, List<SubjectWiseResultSubject> subjectWiseResults, String type) {
        this.activity = activity;
        this.subjectWiseResults = subjectWiseResults;
        this.type = type;
    }

    @NonNull
    @Override
    public ProgressBarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_graph, parent, false);
        return new ProgressBarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressBarViewHolder holder, int position) {
        if (type.equals("0")) {
            holder.topperLL.setVisibility(View.GONE);
        } else {
            holder.topperLL.setVisibility(View.VISIBLE);
        }
        if (Float.parseFloat(subjectWiseResults.get(position).getPercentge()) <= 0) {
            holder.progressGraph.setVisibility(View.GONE);
            holder.percentage.setText("0 %");
        } else {
            holder.progressGraph.setVisibility(View.VISIBLE);
            holder.progressGraph.setProgress(Math.round(Float.parseFloat(subjectWiseResults.get(position).getPercentge())));
            holder.percentage.setText(Helper.getPercentage(subjectWiseResults.get(position).getPercentge()));
        }
        holder.subName.setText(subjectWiseResults.get(position).getName());
        holder.progressGraphTopper.setProgress(Math.round(Float.parseFloat(subjectWiseResults.get(position).getTopperPercentge())));
        holder.percentageTopper.setText(subjectWiseResults.get(position).getTopperPercentge());
    }

    @Override
    public int getItemCount() {
        return subjectWiseResults.size();
    }

    public class ProgressBarViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressGraph;
        ProgressBar progressGraphTopper;
        TextView subName;
        TextView percentage;
        TextView percentageTopper;
        LinearLayout topperLL;

        public ProgressBarViewHolder(@NonNull View itemView) {
            super(itemView);
            progressGraph = itemView.findViewById(R.id.progressGraph);
            progressGraphTopper = itemView.findViewById(R.id.progressGraphTopper);
            subName = itemView.findViewById(R.id.subName);
            percentage = itemView.findViewById(R.id.percentage);
            topperLL = itemView.findViewById(R.id.topperLL);
            percentageTopper = itemView.findViewById(R.id.percentageTopper);
            progressGraph.setMax(100);
            progressGraphTopper.setMax(100);
        }
    }
}
