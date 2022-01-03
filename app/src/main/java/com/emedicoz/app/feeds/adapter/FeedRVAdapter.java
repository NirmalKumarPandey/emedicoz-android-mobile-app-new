package com.emedicoz.app.feeds.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.customviews.SingleFeedView;
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.feeds.activity.NewProfileActivity;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.modelo.Banner;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.response.PostResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.eMedicozApp;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * TODO: Replace the implementation with code for your data type.
 */
public class FeedRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<PostResponse> feedArrayList;
    Context context;

    Bitmap bitmap;
    Activity activity;
    // added here by sagar

    public FeedRVAdapter(Context context, ArrayList<PostResponse> feedArrayList, Activity activity) {
        this.context = context;
        this.feedArrayList = feedArrayList;
        this.activity = activity;
    }

    public void itemChangeDatePostId(PostResponse postResponse, int type) {
        int i = 0;
        if (postResponse != null) {
            if (type == 0) {
                while (i < feedArrayList.size()) {
                    if ((feedArrayList.get(i).getId()).equals(postResponse.getId())) {
                        feedArrayList.set(i, postResponse);
                        notifyItemChanged(i, postResponse);
                        i = feedArrayList.size();
                    }
                    i++;
                }
            } else if (type == 1) {
                while (i < feedArrayList.size()) {
                    if ((feedArrayList.get(i).getId()).equals(postResponse.getId())) {
                        feedArrayList.remove(i);
                        notifyItemRemoved(i);
                        i = feedArrayList.size();
                    }
                    i++;
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1 || viewType == 2) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_feeds, parent, false);
            return new ViewHolder1(itemView);
        } else if (viewType == 3 || viewType == 4 || viewType == 6) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_people_you_m_k, parent, false);
            return new ViewHolder3(itemView);
        } else if (viewType == 5) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_livestream, parent, false);
            return new ViewHolder2(itemView);
        } else if (viewType == 7) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_lets_talk, parent, false);
            return new LetsTalkVHolder(itemView);
        } else if (viewType == 8) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_feeds_suggested_videos, parent, false);
            return new SuggestedVideosHolder(itemView);
        } else if (viewType == 9) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_feeds_suggested_videos, parent, false);
            return new SuggestedCoursesHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder sholder, int position) {
        int itemType = getItemViewType(position);

        if (feedArrayList.get(position).getPost_data() != null && !GenericUtils.isListEmpty(feedArrayList.get(position).getPost_data().getPost_file())
                && feedArrayList.get(position).getPost_data().getPost_file().get(0).getFile_type().contains("quiz"))
            eMedicozApp.getInstance().postFile = feedArrayList.get(position).getPost_data().getPost_file().get(0);

        if (itemType == 5) {
            final ViewHolder2 holder = (ViewHolder2) sholder;
            PostResponse feed = feedArrayList.get(position);
            holder.mnVideoPlayerRL.setVisibility(View.VISIBLE);
            if (feedArrayList.get(position).getIs_vod().equals("1")) {
                holder.liveIV.setVisibility(View.GONE);
            } else {
                holder.liveIV.setVisibility(View.VISIBLE);
            }
            if (!feedArrayList.get(position).getThumbnail().equals("")) {
                Glide.with(activity).load(feedArrayList.get(position).getThumbnail()).into(holder.videoImage);
            }
            holder.videoImage.setScaleType(ImageView.ScaleType.FIT_XY);

            feed.getPost_owner_info().setName(Helper.CapitalizeText(feed.getPost_owner_info().getName()));

            holder.nameTV.setText(HtmlCompat.fromHtml("<b>" + feed.getPost_owner_info().getName().toUpperCase() + "</b> LIVE", HtmlCompat.FROM_HTML_MODE_LEGACY));

            //Setting up user's profile picture

            if (!TextUtils.isEmpty(feed.getPost_owner_info().getProfile_picture())) {
                holder.profilePicIV.setVisibility(View.VISIBLE);
                holder.profilePicIVText.setVisibility(View.GONE);
                Glide.with(activity)
                        .asBitmap()
                        .load(feed.getPost_owner_info().getProfile_picture())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_pic))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                holder.profilePicIV.setImageBitmap(result);
                            }
                        });
            } else {

                Drawable dr = Helper.GetDrawable(feed.getPost_owner_info().getName(), activity, feed.getPost_owner_info().getId());

                if (dr != null) {
                    holder.profilePicIV.setVisibility(View.GONE);
                    holder.profilePicIVText.setVisibility(View.VISIBLE);
                    holder.profilePicIVText.setImageDrawable(dr);
                } else {
                    holder.profilePicIV.setVisibility(View.VISIBLE);
                    holder.profilePicIVText.setVisibility(View.GONE);
                    holder.profilePicIV.setImageResource(R.mipmap.default_pic);
                }
            }

        } else if (itemType == 3) {
            ((ViewHolder3) sholder).setUpPeopleList(1);

        } else if (itemType == 4) {
            ((ViewHolder3) sholder).setUpBanner(((HomeActivity) activity).getBannerData());

        } else if (itemType == 6) {
            ((ViewHolder3) sholder).setUpPeopleList(0);

        } else if (itemType == 8) {
            ((SuggestedVideosHolder) sholder).setUpSuggestedVideoList();

        } else if (itemType == 9) {
            ((SuggestedCoursesHolder) sholder).setUpSuggestedCourseList();

        } else if (itemType == 7) {
            final PostResponse feed = feedArrayList.get(position);
            final LetsTalkVHolder holder = (LetsTalkVHolder) sholder;
            holder.setUpImage(feed);

        } else {
            final PostResponse feed = feedArrayList.get(position);
            final ViewHolder1 holder = (ViewHolder1) sholder;
            holder.singleFeedView.setFeed(feed, itemType);
        }
    }

    @Override
    public int getItemCount() {
        return feedArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (feedArrayList.get(position).getPost_type()) {
            case Const.POST_TYPE_MCQ:
                return 1;
            case Const.POST_TYPE_NORMAL:
                return 2;
            case Const.POST_TYPE_PEOPLEYMK:
                return 3;
            case Const.POST_TYPE_BANNER:
                return 4;
            case Const.POST_TYPE_LIVE_STREAM:
                return 5;
            case Const.POST_TYPE_MEET_THE_EXPERT:
                return 6;
            case Const.POST_TYPE_LETS_TALK:
                return 7;
            case Const.POST_TYPE_SUGGESTED_VIDEOS:
                return 8;
            case Const.POST_TYPE_SUGGESTED_COURSES:
                return 9;
            default:
                return 1;
        }
    }

    // SingleFeedView for single feed
    public class ViewHolder1 extends RecyclerView.ViewHolder implements SingleFeedView.DeletePostCallback {

        SingleFeedView singleFeedView;

        public ViewHolder1(final View view) {
            super(view);
            singleFeedView = new SingleFeedView(activity, this);
            singleFeedView.initViews(view);
        }

        @Override
        public void deletePost(PostResponse feed) {
            itemChangeDatePostId(feed, 1);
        }
    }

    // Live Video Layout
    public class ViewHolder2 extends RecyclerView.ViewHolder {

        TextView nameTV;

        ImageView profilePicIV, profilePicIVText, videoImage, liveIV;

        RelativeLayout imageRL;

        RelativeLayout mnVideoPlayerRL;

        public ViewHolder2(final View view) {
            super(view);

            imageRL = view.findViewById(R.id.imageRL);

            liveIV = view.findViewById(R.id.liveIV);
            videoImage = view.findViewById(R.id.video_image);
            profilePicIV = view.findViewById(R.id.profilepicIV);
            profilePicIVText = view.findViewById(R.id.profilepicIVText);
            mnVideoPlayerRL = view.findViewById(R.id.mn_videoplayer);

            nameTV = view.findViewById(R.id.nameTV);

            nameTV.setOnClickListener((View v) -> Helper.GoToProfileActivity(activity, feedArrayList.get(getAdapterPosition()).getPost_owner_info().getId()));
            imageRL.setOnClickListener((View v) -> Helper.GoToProfileActivity(activity, feedArrayList.get(getAdapterPosition()).getPost_owner_info().getId()));
            mnVideoPlayerRL.setOnClickListener((View v) -> {
                int pos = getAdapterPosition();
                Helper.GoToLiveClassesWithChat(activity, feedArrayList.get(pos).getHlslink(), Const.VIDEO_LIVE, feedArrayList.get(pos).getChat_platform(), feedArrayList.get(pos).getId(), false,feedArrayList.get(pos).getChat_node());
            });
            liveIV.setOnClickListener((View v) -> {
                int pos = getAdapterPosition();
                Helper.GoToLiveClassesWithChat(activity, feedArrayList.get(pos).getHlslink(), Const.VIDEO_LIVE, feedArrayList.get(pos).getChat_platform(), feedArrayList.get(pos).getId(), false,feedArrayList.get(pos).getChat_node());
            });
        }
    }

    // Let's Talk Layout
    public class LetsTalkVHolder extends RecyclerView.ViewHolder {

        ImageView profilePicIV, profilePicIVText;

        LinearLayout upperPanelFeed;
        RelativeLayout imageRL;

        public LetsTalkVHolder(final View view) {
            super(view);

            upperPanelFeed = view.findViewById(R.id.upperPanelFeed);
            imageRL = view.findViewById(R.id.imageRL);

            profilePicIV = view.findViewById(R.id.profilepicIV);
            profilePicIVText = view.findViewById(R.id.profilepicIVText);

            upperPanelFeed.setOnClickListener((View v) -> Helper.GoToPostActivity(activity, null, Const.POST_FRAG));

        }

        public void setUpImage(final PostResponse feed) {

            imageRL.setOnClickListener((View v) -> {
                if (!(activity instanceof NewProfileActivity))
                    Helper.GoToProfileActivity(activity, feed.getPost_owner_info().getId());
            });

            if (!TextUtils.isEmpty(feed.getPost_owner_info().getProfile_picture())) {
                profilePicIV.setVisibility(View.VISIBLE);
                profilePicIVText.setVisibility(View.GONE);
                Glide.with(activity)
                        .asBitmap()
                        .load(feed.getPost_owner_info().getProfile_picture())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_pic))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                profilePicIV.setImageBitmap(result);
                            }
                        });
            } else {
                Drawable dr = Helper.GetDrawable(feed.getPost_owner_info().getName(), activity, feed.getPost_owner_info().getId());
                if (dr != null) {
                    profilePicIV.setVisibility(View.GONE);
                    profilePicIVText.setVisibility(View.VISIBLE);
                    profilePicIVText.setImageDrawable(dr);
                } else {
                    profilePicIV.setVisibility(View.VISIBLE);
                    profilePicIVText.setVisibility(View.GONE);
                    profilePicIV.setImageResource(R.mipmap.default_pic);
                }
            }

        }
    }

    // People you may Know Layout
    public class ViewHolder3 extends RecyclerView.ViewHolder {

        ImageView imageIV;
        TextView peopleTV, advertiseText, peopleViewAll, adHeading;
        RecyclerView peopleRV;
        RelativeLayout mainRelativeL;
        LinearLayout bannerLL;
        Button actionBtn;
        RelativeLayout pymkRL;
        PeopleRVAdapter peopleRVAdapter;
        ArrayList<People> peopleArrayList;
        Banner currentBanner;

        public ViewHolder3(final View view) {
            super(view);

            peopleTV = view.findViewById(R.id.peopleknownTV1);
            mainRelativeL = view.findViewById(R.id.mainRelativeL);
            imageIV = view.findViewById(R.id.imageIV);
            advertiseText = view.findViewById(R.id.advertismentText);
            adHeading = view.findViewById(R.id.adheading);
            peopleRV = view.findViewById(R.id.peopleRV);
            pymkRL = view.findViewById(R.id.pymkRL);
            peopleViewAll = view.findViewById(R.id.peopleknownViewAll);
            bannerLL = view.findViewById(R.id.bannerLL);

            actionBtn = view.findViewById(R.id.actionbtn);
            peopleRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            peopleRV.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.HORIZONTAL));

        }

        private void networkCallForUpdateBannerHitCount() {
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.updateBannerHitCount(currentBanner.getId());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }

        public void setUpBanner(final Banner banner) {

            if (banner != null) {
                bannerLL.setVisibility(View.VISIBLE);

                Glide.with(activity)
                        .asBitmap()
                        .load(banner.getImage_link())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_pic))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                imageIV.setImageBitmap(result);
                            }
                        });

                peopleTV.setVisibility(View.GONE);
                peopleViewAll.setVisibility(View.GONE);

                adHeading.setText(banner.getBanner_title());
                advertiseText.setText(banner.getText());
            } else {
                bannerLL.setVisibility(View.GONE);

            }

            imageIV.setOnClickListener((View v) -> {
                Helper.GoToWebViewActivity(activity, banner.getWeb_link());
                currentBanner = banner;
                networkCallForUpdateBannerHitCount();//networkCall.NetworkAPICall(API.API_UPDATE_BANNER_HIT_COUNT, false);
            });

            actionBtn.setOnClickListener((View v) -> {
                Helper.GoToWebViewActivity(activity, banner.getWeb_link());
                currentBanner = banner;
                networkCallForUpdateBannerHitCount();//networkCall.NetworkAPICall(API.API_UPDATE_BANNER_HIT_COUNT, false);
            });
        }

        public void setUpPeopleList(final int type) {
            peopleArrayList = new ArrayList<>();
            if (type == 0) {
                peopleArrayList = ((HomeActivity) activity).getExpertPeopleData(0);
                peopleTV.setText(activity.getString(R.string.meet_the_expert));

            } else if (type == 1) {
                peopleArrayList = ((HomeActivity) activity).getPeopleYMKData(0);
                peopleTV.setText(activity.getString(R.string.people_you_may_know));

            }

            peopleViewAll.setOnClickListener((View v) -> {
                Intent intent = new Intent(activity, PostActivity.class); // comment fragment // COMMON_PEOPLE_VIEWALL
                if (type == 0) {
                    intent.putExtra(Const.FRAG_TYPE, Const.COMMON_EXPERT_PEOPLE_VIEWALL);
                    intent.putExtra(Const.PEOPLE_LIST_COMMONS, ((FeedsActivity) activity).getExpertPeopleData(1));
                } else if (type == 1) {
                    intent.putExtra(Const.FRAG_TYPE, Const.COMMON_PEOPLE_VIEWALL);
                    intent.putExtra(Const.PEOPLE_LIST_COMMONS, ((FeedsActivity) activity).getPeopleYMKData(1));
                }
                intent.putExtra(Const.COMMON_PEOPLE_TYPE, 1);
                activity.startActivity(intent);
            });

            bannerLL.setVisibility(View.GONE);

            if (peopleArrayList != null && peopleArrayList.isEmpty()) {
                pymkRL.setVisibility(View.GONE);
                peopleViewAll.setVisibility(View.GONE);
            } else {
                pymkRL.setVisibility(View.VISIBLE);
                peopleViewAll.setVisibility(View.VISIBLE);
            }
            initPeopleAdapter(peopleArrayList, type);

        }

        protected void initPeopleAdapter(ArrayList<People> peopleArrayList, int type) {
            if (!peopleArrayList.isEmpty()) {
                if (type == 0)
                    peopleRVAdapter = new PeopleRVAdapter(peopleArrayList, activity, Const.PEOPLE_LIST_FEEDS, Const.COMMON_EXPERT_PEOPLE_VIEWALL, 0);
                if (type == 1)
                    peopleRVAdapter = new PeopleRVAdapter(peopleArrayList, activity, Const.PEOPLE_LIST_FEEDS, Const.COMMON_PEOPLE_VIEWALL, 0);
                peopleRV.setAdapter(peopleRVAdapter);
                peopleTV.setVisibility(View.VISIBLE);
            } else
                peopleTV.setVisibility(View.GONE);
        }
    } // People you may Know Layout

    //Suggested Videos Layout
    public class SuggestedVideosHolder extends RecyclerView.ViewHolder {

        RelativeLayout mainRelativeL;
        ImageView imageIV;
        TextView textTV, viewAll;
        RecyclerView commonRV;

        ArrayList<Video> videoArrayList;

        public SuggestedVideosHolder(final View view) {
            super(view);

            mainRelativeL = view.findViewById(R.id.mainRelativeL);
            textTV = view.findViewById(R.id.textTV);
            imageIV = view.findViewById(R.id.imageIV);
            viewAll = view.findViewById(R.id.ViewAll);
            commonRV = view.findViewById(R.id.commonRV);

            commonRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        }

        public void setUpSuggestedVideoList() {

            viewAll.setOnClickListener((View v) -> ((BaseABNavActivity) activity).customNavigationClick(Const.VIDEOS));

            videoArrayList = new ArrayList<>();
            videoArrayList.addAll(((HomeActivity) activity).getVideoData());
            //videoArrayList.addAll(((FeedsActivity) activity).getVideoData());

            if (videoArrayList.isEmpty()) {
                mainRelativeL.setVisibility(View.GONE);
                viewAll.setVisibility(View.GONE);
            } else {
                initSuggestedVideoAdapter(videoArrayList);
                mainRelativeL.setVisibility(View.VISIBLE);
                viewAll.setVisibility(View.VISIBLE);
            }

        }

        protected void initSuggestedVideoAdapter(ArrayList<Video> videoArrayList) {
            if (!videoArrayList.isEmpty()) {
                SuggestedVideoRVAdapter suggestedVideoRVAdapter = new SuggestedVideoRVAdapter(activity, videoArrayList);
                commonRV.setAdapter(suggestedVideoRVAdapter);
                commonRV.setVisibility(View.VISIBLE);
            } else
                commonRV.setVisibility(View.GONE);
        }
    }

    //Suggested Course Layout
    public class SuggestedCoursesHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainRelativeL;
        ImageView imageIV;
        TextView textTV, viewAll;
        RecyclerView commonRV;

        ArrayList<Course> courseArrayList;

        public SuggestedCoursesHolder(final View view) {
            super(view);

            mainRelativeL = view.findViewById(R.id.mainRelativeL);
            textTV = view.findViewById(R.id.textTV);
            imageIV = view.findViewById(R.id.imageIV);
            viewAll = view.findViewById(R.id.ViewAll);
            commonRV = view.findViewById(R.id.commonRV);

            textTV.setText(activity.getResources().getString(R.string.suggested_courses));
            commonRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        }

        public void setUpSuggestedCourseList() {

            viewAll.setOnClickListener((View v) -> ((BaseABNavActivity) activity).customNavigationClick(Const.COURSES));

            courseArrayList = new ArrayList<>();
            courseArrayList.addAll(((HomeActivity) activity).getCourseData());

            if (courseArrayList.isEmpty()) {
                mainRelativeL.setVisibility(View.GONE);
                viewAll.setVisibility(View.GONE);
            } else {
                initSuggestedCourseAdapter(courseArrayList);
                mainRelativeL.setVisibility(View.VISIBLE);
                viewAll.setVisibility(View.VISIBLE);
            }

        }

        protected void initSuggestedCourseAdapter(ArrayList<Course> courseArrayList) {
            if (!courseArrayList.isEmpty()) {
                SuggestedCourseRVAdapter suggestedCourseRVAdapter = new SuggestedCourseRVAdapter(activity, courseArrayList);
                commonRV.setAdapter(suggestedCourseRVAdapter);
                commonRV.setVisibility(View.VISIBLE);
            } else
                commonRV.setVisibility(View.GONE);
        }
    }

}
