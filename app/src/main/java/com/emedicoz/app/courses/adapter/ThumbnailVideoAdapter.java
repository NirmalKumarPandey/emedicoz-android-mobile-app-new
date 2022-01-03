package com.emedicoz.app.courses.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.LiveCourseActivity;
import com.emedicoz.app.modelo.liveclass.courses.UpComingLiveVideo;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ThumbnailVideoAdapter extends RecyclerView.Adapter<ThumbnailVideoAdapter.ViewHolder> {
    Activity activity;
    List<UpComingLiveVideo> upComingLiveVideoList;
    public String THUMB_IMAGE;

    public ThumbnailVideoAdapter(Activity activity, List<UpComingLiveVideo> upComingLiveVideoList, String THUMB_IMAGE) {
        this.activity = activity;
        this.upComingLiveVideoList = upComingLiveVideoList;
        this.THUMB_IMAGE = THUMB_IMAGE;
    }

    @NonNull
    @Override
    public ThumbnailVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.video_thumb_nail_layout, null);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ThumbnailVideoAdapter.ViewHolder viewHolder, int i) {
        viewHolder.titleTV.setText(upComingLiveVideoList.get(i).getVideoTitle());
        if (THUMB_IMAGE.equalsIgnoreCase("OPEN")) {
            viewHolder.videoplayerRL.setVisibility(View.VISIBLE);
            Date d = new Date(Long.parseLong(GenericUtils.getParsableString(upComingLiveVideoList.get(i).getLiveOn())) * 1000);
            DateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
            String dateString = f.format(d);
            Date date = null;
            try {
                date = f.parse(f.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String[] amPM = dateString.split("\\s+");

            String[] fullDate = String.valueOf(date).split("\\s+");
            final String correctDateFormat = GenericUtils.getFormattedDate(fullDate, amPM);
            viewHolder.ibt_test_tile_RL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!GenericUtils.isEmpty(upComingLiveVideoList.get(i).getLiveOn()) && (Long.parseLong(upComingLiveVideoList.get(i).getLiveOn()) * 1000)
                                <= System.currentTimeMillis()) {
                            if (upComingLiveVideoList.get(i).getIsLive().equalsIgnoreCase("1")) {
                                Intent intent = new Intent(activity, LiveCourseActivity.class);
                                intent.putExtra(Const.VIDEO_LINK, upComingLiveVideoList.get(i).getLiveUrl());
                                intent.putExtra(Const.VIDEO, (Serializable) upComingLiveVideoList);
                                activity.startActivity(intent);
                            } else {
                                if (GenericUtils.isEmpty(upComingLiveVideoList.get(i).getLiveUrl()))
                                    Helper.GoToVideoActivity(activity, upComingLiveVideoList.get(i).getFileUrl(), Const.VIDEO_STREAM, upComingLiveVideoList.get(i).getId(), Const.COURSE_VIDEO_TYPE);
                                else
                                    Helper.GoToVideoActivity(activity, upComingLiveVideoList.get(i).getLiveUrl(), Const.VIDEO_STREAM, upComingLiveVideoList.get(i).getId(), Const.COURSE_VIDEO_TYPE);
                            }

                        } else {
                            Toast.makeText(activity, "This video will be live on: " + correctDateFormat, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            if (upComingLiveVideoList.get(i).getIsLive() != null && upComingLiveVideoList.get(i).getIsLive().equals("1")) {
                viewHolder.subHeaderLL.setBackgroundResource(R.color.green_new);
            } else {
                viewHolder.subHeaderLL.setBackgroundResource(R.color.white);
            }


        } else {
            if (upComingLiveVideoList.get(i).getIsLive() != null && upComingLiveVideoList.get(i).getIsLive().equals("1")) {

                viewHolder.subHeaderLL.setBackgroundResource(R.color.red);
                viewHolder.titleTV.setTextColor(R.color.white);
            } else {
                viewHolder.subHeaderLL.setBackgroundResource(R.color.white);
            }
            viewHolder.videoplayerRL.setVisibility(View.GONE);
        }
        Glide.with(activity).asGif()
                .load(R.drawable.live_gif)
                .into(viewHolder.liveIV);
        if (upComingLiveVideoList.get(i).getIsLive() != null) {
            if (upComingLiveVideoList.get(i).getIsLive().equals("1"))
                viewHolder.liveIV.setVisibility(View.VISIBLE);
            else
                viewHolder.liveIV.setVisibility(View.GONE);
        } else {
            viewHolder.liveIV.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        if (!GenericUtils.isListEmpty(upComingLiveVideoList)) {
            return upComingLiveVideoList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoImage;
        ImageView liveIV;
        TextView titleTV;
        RelativeLayout videoplayerRL, ibt_test_tile_RL;
        LinearLayout subHeaderLL;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoplayerRL = itemView.findViewById(R.id.videoplayerRL);
            videoImage = itemView.findViewById(R.id.videoImage);
            liveIV = itemView.findViewById(R.id.liveIV);
            titleTV = itemView.findViewById(R.id.titleTV);
            subHeaderLL = itemView.findViewById(R.id.subHeaderLL);
            ibt_test_tile_RL = itemView.findViewById(R.id.ibt_test_tile_RL);

        }
    }

}
