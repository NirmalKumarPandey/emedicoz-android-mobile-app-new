package com.emedicoz.app.feeds.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.video.activity.VideoDetail;

import java.util.List;

public class SuggestedVideoRVAdapter extends RecyclerView.Adapter<SuggestedVideoRVAdapter.ViewHolder> {

    Activity activity;
    List<Video> videoArrayList;

    public SuggestedVideoRVAdapter(Activity activity, List<Video> videoArrayList) {
        this.activity = activity;
        this.videoArrayList = videoArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_suggested_video_feeds, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setSuggestedVideoData(videoArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV;
        TextView descriptionTV;
        ImageView videoImage;
        View myView;

        public ViewHolder(final View view) {
            super(view);
            titleTV = view.findViewById(R.id.titleTV);
            descriptionTV = view.findViewById(R.id.descriptionTV);
            videoImage = view.findViewById(R.id.videoImage);
            myView = view;
        }


        public void setSuggestedVideoData(final Video data) {
            descriptionTV.setText(data.getVideo_desc());
            titleTV.setText(data.getVideo_title());

            Glide.with(activity).load(data.getThumbnail_url()).into(videoImage);

            myView.setOnClickListener(view -> {
                Intent intent = new Intent(activity, VideoDetail.class);
                    intent.putExtra(Const.DATA, data);
                    intent.putExtra(Constants.Extras.TYPE, Const.VIDEO);
                    activity.startActivity(intent);
            });
        }

    }
}

