package com.emedicoz.app.feeds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.Report_reasons;
import com.emedicoz.app.video.exoplayer2.ExoPlayerFragment;

import java.util.List;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ReportAbuseHolder> {

    List<Report_reasons> reportReasonsArrayList;
    Context context;
    private int lastSelectedPosition = -1;
    ExoPlayerFragment exoPlayerFragment;


    public ReportListAdapter(Context activity, List<Report_reasons> reportReasonsArrayList, ExoPlayerFragment exoPlayerFragment) {
        this.context = activity;
        this.reportReasonsArrayList = reportReasonsArrayList;
        this.exoPlayerFragment = exoPlayerFragment;
    }

    @Override
    public ReportAbuseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_layout_item, parent, false);
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
                exoPlayerFragment.setReportId(reportReasonsArrayList.get(getAdapterPosition()).getId());
                notifyDataSetChanged();
            });
        }
    }
}
