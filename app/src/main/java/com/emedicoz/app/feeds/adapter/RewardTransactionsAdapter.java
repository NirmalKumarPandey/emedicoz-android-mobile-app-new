package com.emedicoz.app.feeds.adapter;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.RewardTransaction;

import java.util.List;

/**
 * Created by Sagar on 07-02-2018.
 */

public class RewardTransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<RewardTransaction> rewardTransactionArrayList;

    public RewardTransactionsAdapter(List<RewardTransaction> rewardTransactionArrayList) {
        this.rewardTransactionArrayList = rewardTransactionArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_rewardtransaction_item, parent, false);
        return new ViewHolder1(itemView);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder sholder, int position) {
        final ViewHolder1 holder = (ViewHolder1) sholder;
        holder.setRewardTransaction(rewardTransactionArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return rewardTransactionArrayList.size();
    }

    // SingleFeedView for single feed
    public class ViewHolder1 extends RecyclerView.ViewHolder {

        TextView descriptionTV;
        TextView timeTV;
        TextView pointsTV;

        public ViewHolder1(final View view) {
            super(view);
            descriptionTV = view.findViewById(R.id.descriptionTV);
            pointsTV = view.findViewById(R.id.pointsTV);
            timeTV = view.findViewById(R.id.timeTV);
        }

        public void setRewardTransaction(RewardTransaction rewardTransaction) {
            descriptionTV.setText(rewardTransaction.getArea());
            timeTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(rewardTransaction.getCreation_time())).equals("0 minutes ago") ? "Just Now" : DateUtils.getRelativeTimeSpanString(Long.parseLong(rewardTransaction.getCreation_time())));
            pointsTV.setText(rewardTransaction.getReward());
        }
    }

}
