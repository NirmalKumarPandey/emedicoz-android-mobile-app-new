package com.emedicoz.app.flashcard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.CircularTextView;
import com.emedicoz.app.flashcard.model.myprogress.GraphProgress;
import com.emedicoz.app.utilso.Helper;

import java.util.List;

public class FlashCardProgressAdapter extends RecyclerView.Adapter<FlashCardProgressAdapter.ProgressViewHolder> {
    private static final String TAG = "FlashCardProgressAdapter";

    Context context;
    List<GraphProgress> graphProgressArrayList;
    List<Integer> integerArrayList;

    public FlashCardProgressAdapter(Context context, List<GraphProgress> graphProgressArrayList, List<Integer> integerArrayList) {
        this.context = context;
        this.graphProgressArrayList = graphProgressArrayList;
        this.integerArrayList = integerArrayList;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.flash_card_progress_graph_adapter_item, viewGroup, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder progressViewHolder, int i) {
        GraphProgress graphProgress = graphProgressArrayList.get(i);

        if (graphProgress.getDays().equalsIgnoreCase("sun")) {
            progressViewHolder.tvDay.setTextColor(ContextCompat.getColor(context,R.color.blue));
        } else {
            progressViewHolder.tvDay.setTextColor(ContextCompat.getColor(context,R.color.black));
        }

        progressViewHolder.tvDay.setText(String.format("%s", graphProgress.getDays().charAt(0)));
        progressViewHolder.tvDate.setText(Helper.parseDateToddMM(graphProgress.getDate()));
        progressViewHolder.ctvNumber.setText(graphProgress.getRead_card());
        progressViewHolder.ctvNumber.setSolidColor(ContextCompat.getColor(context,R.color.redeem));
        Log.e(TAG, "onBindViewHolder: " + (integerArrayList.get(integerArrayList.size() - 1)));
        progressViewHolder.progressBar.setMax(integerArrayList.get(integerArrayList.size() - 1));
        progressViewHolder.progressBar.setProgress(Integer.parseInt(graphProgress.getRead_card()));
    }

    @Override
    public int getItemCount() {
        return graphProgressArrayList.size();
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;
        CircularTextView ctvNumber;
        TextView tvDate;
        TextView tvDay;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);

            ctvNumber = itemView.findViewById(R.id.ctv_number);
            progressBar = itemView.findViewById(R.id.progerssbar);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvDate = itemView.findViewById(R.id.tv_date);

        }
    }
}
