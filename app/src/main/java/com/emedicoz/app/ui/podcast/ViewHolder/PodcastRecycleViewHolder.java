package com.emedicoz.app.ui.podcast.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.ui.views.RoundedImageView;

/**
 * Created by Shri Ram
 */

public class PodcastRecycleViewHolder extends RecyclerView.ViewHolder {

    public RoundedImageView profile_pic;
    public TextView podcast_size;
    public TextView publisher;
    public TextView episodetitle;
    public RelativeLayout mainRL;
    public ImageView imgDownload;
    public ImageView imgDelete;
    public ImageView imgBookmark;
    public LinearLayout podcastTitleLL;

    public PodcastRecycleViewHolder(Context mContext, View itemView) {
        super(itemView);

        episodetitle = (TextView) itemView.findViewById(R.id.episodetitle);
        publisher = (TextView) itemView.findViewById(R.id.publisher);
        profile_pic = (RoundedImageView) itemView.findViewById(R.id.profile_pic);
        podcast_size = (TextView) itemView.findViewById(R.id.podcast_size);
        mainRL = itemView.findViewById(R.id.mainRL);
        imgDownload = itemView.findViewById(R.id.downloadIV);
        imgDelete = itemView.findViewById(R.id.deleteIV);
        imgBookmark = itemView.findViewById(R.id.fan_image);
        podcastTitleLL = itemView.findViewById(R.id.podcastTitleLL);
    }


}
