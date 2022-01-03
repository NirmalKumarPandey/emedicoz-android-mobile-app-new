package com.emedicoz.app.feeds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.SingleFeedView;
import com.emedicoz.app.modelo.Report_reasons;

import java.util.List;

/**
 * Created by app on 4/1/18.
 */

public class ReportAbuseListAdapter extends RecyclerView.Adapter<ReportAbuseListAdapter.ReportAbuseHolder> {

    List<Report_reasons> reportReasonsArrayList;
    Context context;
    SingleFeedView singleFeedView;
    private int lastSelectedPosition = -1;

    public ReportAbuseListAdapter(Context activity, List<Report_reasons> reportReasonsArrayList, SingleFeedView singleFeedView) {
        this.context = activity;
        this.reportReasonsArrayList = reportReasonsArrayList;
        this.singleFeedView = singleFeedView;
    }

    @Override
    public ReportAbuseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_report_abuse_item, parent, false);
        return new ReportAbuseHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReportAbuseHolder holder, final int position) {
        holder.radioButton.setText(reportReasonsArrayList.get(position).getReason());
        holder.radioButton.setChecked(lastSelectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return reportReasonsArrayList.size();
    }

    public class ReportAbuseHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        LinearLayout mainLL;

        public ReportAbuseHolder(View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radioOption);
            mainLL = itemView.findViewById(R.id.mainLL);

            radioButton.setOnClickListener(view -> {
                    lastSelectedPosition = getAdapterPosition();
                    singleFeedView.setReportId(reportReasonsArrayList.get(getAdapterPosition()).getId());
                    notifyDataSetChanged();
            });
        }
    }
}
