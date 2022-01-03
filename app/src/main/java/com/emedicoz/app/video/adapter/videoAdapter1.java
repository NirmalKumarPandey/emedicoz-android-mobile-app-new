package com.emedicoz.app.video.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emedicoz.app.R;
import com.emedicoz.app.imageslider.CirclePageIndicator;
import com.emedicoz.app.imageslider.sliderAdapter;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.Constants;
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.video.activity.VideoDetail;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Cbc-03 on 11/17/17.
 */
@SuppressLint("SetTextI18n")
public class videoAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Video> videoArrayList;
    Activity activity;
    int type;
    Video liveVideo;

    public videoAdapter1(List<Video> videoArrayList, Activity activity) {
        this.videoArrayList = videoArrayList;
        this.activity = activity;
    }

    public videoAdapter1(List<Video> videoArrayList, Activity activity, int type, Video liveVideo) {
        this.videoArrayList = videoArrayList;
        this.activity = activity;
        this.type = type;
        this.liveVideo = liveVideo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_slider_video, parent, false);
            return new videoAdapter1.ViewHolderSlider(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_fragment_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) holder;
            vh.setView(videoArrayList.get(position));
        } else {
            ViewHolderSlider vh = (ViewHolderSlider) holder;
            vh.setSliderVideo();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (type == 1) {
            if (position == 0) {
                return 1;
            } else
                return 0;
        } else return 0;
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public void itemChangedAtVideoId(Video videoresponse) {
        int i;
        if (type == 1)
            i = 1;
        else i = 0;
        if (videoresponse != null) {

            while (i < videoArrayList.size()) {
                if ((videoArrayList.get(i).getId()).equals(videoresponse.getId())) {
                    videoArrayList.set(i, videoresponse);
                    notifyItemChanged(i, videoresponse);
                    i = videoArrayList.size();
                }
                i++;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView des;
        TextView views;
        TextView date;
        ImageView imageView;
        ImageButton imgBookmark;
        CardView videoItemRL;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            des = itemView.findViewById(R.id.description);
            views = itemView.findViewById(R.id.tvViews);
            date = itemView.findViewById(R.id.date);

            imageView = itemView.findViewById(R.id.video_image);
            imgBookmark = itemView.findViewById(R.id.imgbookmark);
            videoItemRL = itemView.findViewById(R.id.videoitemRL);
        }

        //setting up data to each single view
        public void setView(final Video video) {
            imgBookmark.setOnClickListener(v -> {
                if (video.getIs_bookmarked().equalsIgnoreCase("0")) {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.white_bookmark_icon));
                    video.setIs_bookmarked("1");
                    notifyDataSetChanged();
                    networkCallToBookmark(video.getId());
                } else {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_bookmark_blankk));
                    video.setIs_bookmarked("0");
                    notifyDataSetChanged();
                    networkCallToRemoveBookmark(video.getId());
                }
            });

            videoItemRL.setOnClickListener(v -> {
                Constants.UPDATE_LIST = "false";
                Intent intent = new Intent(activity, VideoDetail.class);
                intent.putExtra(Const.DATA, video);
                intent.putExtra("is_bookmarked", video.getIs_bookmarked());
                intent.putExtra(com.emedicoz.app.utilso.Constants.Extras.TYPE, Const.VIDEO);
                activity.startActivity(intent);
            });
            title.setText(video.getVideo_title());
            des.setText(video.getVideo_desc());

            if (!TextUtils.isEmpty(video.getCreation_time())) {
                date.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(video.getCreation_time())).equals(activity.getString(R.string.string_minutes_ago)) ?
                        Const.JUST_NOW :
                        DateUtils.getRelativeTimeSpanString(Long.parseLong(video.getCreation_time())));
            }
            des.setText(video.getAuthor_name());
            if (!TextUtils.isEmpty(video.getViews())) {
                if (video.getViews().equals("0") || video.getViews().equals("1")) {
                    views.setText(video.getViews() + Const.VIEW);
                } else {
                    views.setText(video.getViews() + " Views");
                }
            }
            if (video.getIs_bookmarked() != null) {
                if (video.getIs_bookmarked().equalsIgnoreCase("0")) {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_bookmark_blankk));
                } else {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.white_bookmark_icon));
                }
            }

            Glide.with(activity)
                    .load(video.getThumbnail_url())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.landscape_placeholder).error(R.drawable.landscape_placeholder)
                    )
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        private void networkCallToRemoveBookmark(String videoId) {
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            SinglecatVideoDataApiInterface apiInterface = ApiClient.createService(SinglecatVideoDataApiInterface.class);
            Call<JsonObject> response = apiInterface.removeVideoBookmark(SharedPreference.getInstance().getLoggedInUser().getId(), videoId);
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            Constants.UPDATE_LIST = "true";
                            jsonResponse = new JSONObject(jsonObject.toString());
                            Toast.makeText(activity, jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Helper.showExceptionMsg(activity, t);
                }
            });

        }

        private void networkCallToBookmark(String videoId) {
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            SinglecatVideoDataApiInterface apiInterface = ApiClient.createService(SinglecatVideoDataApiInterface.class);
            Call<JsonObject> response = apiInterface.createBookmark(SharedPreference.getInstance().getLoggedInUser().getId(), videoId);
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            Constants.UPDATE_LIST = "true";
                            jsonResponse = new JSONObject(jsonObject.toString());
                            Toast.makeText(activity, jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Helper.showExceptionMsg(activity, t);
                }
            });

        }
    }

    public class ViewHolderSlider extends RecyclerView.ViewHolder {
        private static final long ANIM_VIEWPAGER_DELAY = 5000;
        private static final long ANIM_VIEWPAGER_DELAY_USER_VIEW = 10000;
        TextView sliderTitle;
        TextView sliderDescription;
        TextView sliderView;
        TextView sliderTime;
        ImageView imageView;
        ViewPager slideViewpager;
        CirclePageIndicator slidePageIndicator;
        boolean stopSliding = false;
        FrameLayout flipperFL;
        List<Video> sliderArrayList = new ArrayList<>();
        com.emedicoz.app.imageslider.sliderAdapter sliderAdapter;
        private Handler handler;
        private Runnable animateViewPager;


        public ViewHolderSlider(View itemView) {
            super(itemView);

            slideViewpager = itemView.findViewById(R.id.view_pager);
            slidePageIndicator = itemView.findViewById(R.id.indicator);
            sliderTitle = itemView.findViewById(R.id.title);
            sliderDescription = itemView.findViewById(R.id.description);
            sliderView = itemView.findViewById(R.id.views);
            sliderTime = itemView.findViewById(R.id.uploaddate);
            flipperFL = itemView.findViewById(R.id.flipperFL);
            imageView = itemView.findViewById(R.id.video_image);
        }

        //setting up data to each single view
        @SuppressLint("ClickableViewAccessibility")
        public void setSliderVideo() {
            sliderArrayList = new ArrayList<>();
            if (type == 1) {
                int i = 1;
                while (i < videoArrayList.size()) {
                    if (!TextUtils.isEmpty(videoArrayList.get(i).getId())) {
                        if (videoArrayList.get(i).getFeatured().equals("1") || videoArrayList.get(i).getIs_new().equals("1")) {
                            sliderArrayList.add(videoArrayList.get(i));
                        }
                    }
                    i++;
                }

                initSliderView();
                slideViewpager.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (event.getAction()) {

                            case MotionEvent.ACTION_CANCEL:
                                break;

                            case MotionEvent.ACTION_UP:
                                // calls when touch release on ViewPager
                                if (sliderArrayList != null && !sliderArrayList.isEmpty()) {
                                    stopSliding = false;
                                    handler.removeCallbacks(animateViewPager);

                                    runnable(sliderArrayList.size());
                                    handler.postDelayed(animateViewPager,
                                            ANIM_VIEWPAGER_DELAY_USER_VIEW);
                                }
                                break;

                            case MotionEvent.ACTION_MOVE:
                                // calls when ViewPager touch
                                if (handler != null && !stopSliding) {
                                    stopSliding = true;
                                    handler.removeCallbacks(animateViewPager);
                                }
                                break;
                        }
                        return false;
                    }
                });
            }
        }

        public void runnable(final int size) {
            if (handler != null)
                handler.removeCallbacks(animateViewPager);
            handler = new Handler();
            animateViewPager = () -> {
                if (!stopSliding) {
                    if (slideViewpager.getCurrentItem() == size - 1) {
                        slideViewpager.setCurrentItem(0);
                    } else {
                        slideViewpager.setCurrentItem(
                                slideViewpager.getCurrentItem() + 1, true);

                    }
                    handler.postDelayed(animateViewPager, 6000);
                }
            };
        }

        protected void initSliderView() {
            if (!sliderArrayList.isEmpty()) {
                flipperFL.setVisibility(View.VISIBLE);
                sliderAdapter = new sliderAdapter(activity, sliderArrayList, activity);

                slideViewpager.setAdapter(sliderAdapter);
                slidePageIndicator.setViewPager(slideViewpager);
                Video slidervideo = sliderArrayList.get(slideViewpager.getCurrentItem());
                runnable(sliderArrayList.size());
                //Re-run callback
                handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
                slideViewpager.addOnPageChangeListener(new PageChangeListener());
                slidePageIndicator.setOnPageChangeListener(new PageChangeListener());

                if (slidervideo.getVideo_type().equalsIgnoreCase(Const.VIDEO_LIVE)) {
                    sliderTitle.setText(slidervideo.getVideo_title());

                    sliderView.setText("");
                    sliderTime.setText("");
                    sliderDescription.setText(slidervideo.getVideo_title());

                } else {
                    sliderTitle.setText(slidervideo.getVideo_title());

                    if (slidervideo.getViews().equals("0") || slidervideo.getViews().equals("1")) {
                        sliderView.setText(slidervideo.getViews() + Const.VIEW);
                    } else {
                        sliderView.setText(slidervideo.getViews() + " Views");
                    }
                    CharSequence date = DateUtils.getRelativeTimeSpanString(Long.parseLong(slidervideo.getCreation_time())).equals(activity.getString(R.string.string_minutes_ago)) ? Const.JUST_NOW : DateUtils.getRelativeTimeSpanString(Long.parseLong(slidervideo.getCreation_time()));
                    sliderTime.setText(date.toString());
                    sliderDescription.setText(slidervideo.getVideo_desc());
                }
            } else {
                flipperFL.setVisibility(View.GONE);
            }
        }

        private class PageChangeListener implements ViewPager.OnPageChangeListener {

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (sliderArrayList != null && !sliderArrayList.isEmpty()) {

                        Video slidervideo = sliderArrayList.get(slideViewpager.getCurrentItem());

                        if (slidervideo.getVideo_type().equalsIgnoreCase(Const.VIDEO_LIVE)) {
                            sliderTitle.setText(slidervideo.getVideo_title());

                            sliderView.setText("");
                            sliderTime.setText("");

                            sliderDescription.setText(slidervideo.getVideo_title());

                        } else {
                            sliderTitle.setText(slidervideo.getVideo_title());

                            sliderDescription.setText(slidervideo.getVideo_desc());
                            sliderTime.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(slidervideo.getCreation_time())).equals(activity.getString(R.string.string_minutes_ago)) ? Const.JUST_NOW : DateUtils.getRelativeTimeSpanString(Long.parseLong(slidervideo.getCreation_time())));
                            if (slidervideo.getViews().equals("0") || slidervideo.getViews().equals("1")) {
                                sliderView.setText(slidervideo.getViews() + Const.VIEW);
                            } else {
                                sliderView.setText(slidervideo.getViews() + " Views");
                            }
                        }
                    }
                }
            }


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

        }
    }
}
