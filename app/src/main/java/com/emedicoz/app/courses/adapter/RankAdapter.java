package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.customviews.CircleImageView;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.modelo.RankList;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;

import java.util.ArrayList;
import java.util.List;

public class RankAdapter extends NonScrollRecyclerView.Adapter<RankAdapter.MyViewHolder> {
    Context context;
    List<RankList> topHundredLists;
    ResultTestSeries resultTestSeries;
    int timeSpentInMins;

    public RankAdapter(Context context, ArrayList<RankList> topHundredLists, ResultTestSeries resultTestSeries) {
        this.context = context;
        this.topHundredLists = topHundredLists;
        this.resultTestSeries = resultTestSeries;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (resultTestSeries != null && !GenericUtils.isEmpty(resultTestSeries.getSetType())) {
            holder.timeTakenLL.setVisibility(View.VISIBLE);
        } else {
            holder.timeTakenLL.setVisibility(View.GONE);
        }

        if ((position + 4) > 10) {
            holder.textScore.setTextSize(11);
            holder.textRank.setTextSize(11);
            holder.textName.setTextSize(11);
            holder.rightTV.setTextSize(11);
            holder.imageIVText.setVisibility(View.VISIBLE);
            holder.dropdown.setVisibility(View.GONE);
            holder.imgThumb.setVisibility(View.GONE);
            if (topHundredLists.get(position).getName() != null)
                holder.textName.setText(topHundredLists.get(position).getName());
            setTestTime(holder.textScore, topHundredLists.get(position).getTimeSpent());

            holder.textRank.setText(String.valueOf(position + 4));
            holder.rightTV.setText(topHundredLists.get(position).getCorrect_count());
            holder.resultCorrectTV.setText(topHundredLists.get(position).getCorrect_count());
            holder.resultIncorrectTV.setText(topHundredLists.get(position).getIncorrect_count());
            holder.resultSkippedTV.setText(topHundredLists.get(position).getNon_attempt());
            holder.showMore.setClickable(false);
        } else {
            if (topHundredLists.get(position).getName() != null)
                holder.textName.setText(topHundredLists.get(position).getName());
            setTestTime(holder.textScore, topHundredLists.get(position).getTimeSpent());

            holder.textRank.setText(String.valueOf(position + 4));
            holder.rightTV.setText(topHundredLists.get(position).getCorrect_count());
            holder.resultCorrectTV.setText(topHundredLists.get(position).getCorrect_count());
            holder.resultIncorrectTV.setText(topHundredLists.get(position).getIncorrect_count());
            holder.resultSkippedTV.setText(topHundredLists.get(position).getNon_attempt());
            setTestTime(holder.timeSpent, topHundredLists.get(position).getTimeSpent());

            holder.showMore.setOnClickListener(v -> {
                if (holder.detailLL.getVisibility() == View.GONE) {
                    holder.detailLL.setVisibility(View.VISIBLE);
                    holder.dropdown.setImageResource(R.mipmap.result_show_more_up);
                } else {
                    holder.detailLL.setVisibility(View.GONE);
                    holder.dropdown.setImageResource(R.mipmap.result_show_more_down);
                }
            });
        }

        if (!TextUtils.isEmpty(topHundredLists.get(position).getProfilePicture())) {
            holder.imgThumb.setVisibility(View.VISIBLE);
            holder.imageIVText.setVisibility(View.GONE);
            Glide.with(context).load(topHundredLists.get(position).getProfilePicture())
                    .into(holder.imgThumb);
        } else {
            Drawable dr = Helper.GetDrawable(topHundredLists.get(position).getName(), context,
                    topHundredLists.get(position).getUserId());
            if (dr != null) {
                holder.imgThumb.setVisibility(View.GONE);
                holder.imageIVText.setVisibility(View.VISIBLE);
                holder.imageIVText.setImageDrawable(dr);
            } else {
                holder.imgThumb.setVisibility(View.VISIBLE);
                holder.imageIVText.setVisibility(View.GONE);
                holder.imgThumb.setImageResource(R.mipmap.default_pic);
            }

        }
    }

    private void setTestTime(TextView textView, String timeSpent) {
        if (Integer.parseInt(timeSpent) < 60) {
            textView.setText(String.format("%s sec", timeSpent));

        } else {
            textView.setText(String.format("%d mins", Math.round(Integer.parseInt(timeSpent) / 60)));
        }
    }

    @Override
    public int getItemCount() {
        return topHundredLists.size();
    }


    public class MyViewHolder extends NonScrollRecyclerView.ViewHolder {
        TextView textName;
        TextView textRank;
        TextView textScore;
        TextView resultCorrectTV;
        TextView resultIncorrectTV;
        TextView resultSkippedTV;
        TextView rightTV;
        TextView timeSpent;
        CircleImageView imgThumb;
        ImageView imageIVText;
        ImageView dropdown;
        LinearLayout detailLL;
        LinearLayout showMore;
        LinearLayout timeTakenLL;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgthumb);
            textName = itemView.findViewById(R.id.nameTV);
            timeSpent = itemView.findViewById(R.id.timeSpent);
            textRank = itemView.findViewById(R.id.rankTV);
            textScore = itemView.findViewById(R.id.marksTV);
            imageIVText = itemView.findViewById(R.id.imageIVText);
            rightTV = itemView.findViewById(R.id.rightTV);
            timeTakenLL = itemView.findViewById(R.id.timeTakenLL);
            resultCorrectTV = itemView.findViewById(R.id.resultCorrectTV);
            resultIncorrectTV = itemView.findViewById(R.id.resultIncorrectTV);
            resultSkippedTV = itemView.findViewById(R.id.resultSkippedTV);
            dropdown = itemView.findViewById(R.id.dropdown);
            detailLL = itemView.findViewById(R.id.detailLL);
            showMore = itemView.findViewById(R.id.showMore);

        }
    }
}
