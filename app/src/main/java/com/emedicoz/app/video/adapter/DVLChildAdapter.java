package com.emedicoz.app.video.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.modelo.dvl.DVLTopic;
import com.emedicoz.app.modelo.dvl.DvlData;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.video.activity.DVLVideosActivity;

import java.util.List;

public class DVLChildAdapter extends RecyclerView.Adapter<DVLChildAdapter.DVLChildViewHolder> {
    Activity activity;
    List<DVLTopic> topics;
    DvlData dvlData;
    String type;

    public DVLChildAdapter(Activity activity, List<DVLTopic> topics, DvlData dvlData, String type) {
        this.activity = activity;
        this.topics = topics;
        this.dvlData = dvlData;
        this.type = type;
    }

    @NonNull
    @Override
    public DVLChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_qbank_subject, viewGroup, false);
        return new DVLChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DVLChildViewHolder holder, final int i) {
        holder.title.setText(topics.get(i).getTitle());
        if (!GenericUtils.isEmpty(topics.get(i).getSubImgUrl())) {
            Glide.with(activity).load(topics.get(i).getSubImgUrl()).into(holder.topicIV);
        }
        if (topics.get(i).getCompleted() != null) {
            if (topics.get(i).getCompleted().equals("")) {
                holder.completedCount.setText("0/" + topics.get(i).getTotal() + " " + Constants.TestStatus.COMPLETED);
            } else {
                holder.completedCount.setText(topics.get(i).getCompleted() + "/" + topics.get(i).getTotal() + " " + Constants.TestStatus.COMPLETED);
            }
        }

        holder.cardView.setOnClickListener(view1 -> {
                Intent intent = new Intent(activity, DVLVideosActivity.class);
            intent.putExtra(Constants.Extras.TOPIC_ID, topics.get(i).getTopicId());
            intent.putExtra(Constants.Extras.TOPIC_TITLE, topics.get(i).getTitle());
            intent.putExtra(Const.PARENT_ID, dvlData.getId());
                intent.putExtra(Const.DATA, dvlData);
                intent.putExtra(Constants.Extras.TYPE, type);
                activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class DVLChildViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView completedCount;
        ImageView topicIV;
        CardView cardView;

        public DVLChildViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            topicIV = itemView.findViewById(R.id.topicIV);
            completedCount = itemView.findViewById(R.id.completedCountTV);
            cardView = itemView.findViewById(R.id.parentCV);
        }
    }
}
