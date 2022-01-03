package com.emedicoz.app.feeds.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.emedicoz.app.R;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.feeds.fragment.CommonFragment;
import com.emedicoz.app.response.NotificationResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.NotificationApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * TODO: Replace the implementation with code for your data type.
 */
public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ViewHolder> {

    ArrayList<NotificationResponse> notificationArrayList;
    Activity activity;
    CommonFragment commonFragment;
    Bitmap bitmap;

    public NotificationRVAdapter(ArrayList<NotificationResponse> notificationArrayList, Activity activity) { //CommonFragment commonFragment
        this.activity = activity;
        //this.commonFragment = commonFragment;
        this.notificationArrayList = notificationArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == notificationArrayList.size())
            return 0;
        else return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_notification, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_loadmore, parent, false);
        }
        return new ViewHolder(view, activity, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if (getItemViewType(position) == 0) {

            final NotificationResponse notification = notificationArrayList.get(position);

            notification.getAction_performed_by().setName(Helper.CapitalizeText(notification.getAction_performed_by().getName()));

            if (!TextUtils.isEmpty(notification.getAction_performed_by().getProfile_picture())) {
                holder.imageIV.setVisibility(View.GONE);
                holder.imageIVText.setVisibility(View.GONE);

                Glide.with(activity)
                        .load(notification.getAction_performed_by().getProfile_picture())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_pic))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Drawable dr = Helper.GetDrawable(notification.getAction_performed_by().getName(), activity, notification.getAction_performed_by().getId());
                                if (dr != null) {
                                    holder.imageIV.setVisibility(View.GONE);
                                    holder.imageIVText.setVisibility(View.GONE);
                                    holder.imageIVText.setImageDrawable(dr);
                                } else {
                                    holder.imageIV.setVisibility(View.VISIBLE);
                                    holder.imageIVText.setVisibility(View.GONE);
                                    holder.imageIV.setImageResource(R.mipmap.default_pic);
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(holder.imageIV);
            } else {
                Drawable dr = Helper.GetDrawable(notification.getAction_performed_by().getName(), activity, notification.getAction_performed_by().getId());
                if (dr != null) {
                    holder.imageIV.setVisibility(View.GONE);
                    holder.imageIVText.setVisibility(View.GONE);
                    holder.imageIVText.setImageDrawable(dr);
                } else {
                    holder.imageIV.setVisibility(View.GONE);
                    holder.imageIVText.setVisibility(View.GONE);
                    holder.imageIV.setImageResource(R.mipmap.default_pic);
                }
            }

            String notifyaction = "";
            notifyaction = getNotificationText(notification.getActivity_type(), notification.getAction_performed_by().getName(), notification.getView_state());

            holder.notifyRL.setTag(holder);
            holder.descriptionTV.setText(HtmlCompat.fromHtml(notifyaction,HtmlCompat.FROM_HTML_MODE_LEGACY));

            holder.timeTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(notification.getCreation_time())).equals("0 minutes ago") ? "Just Now" : DateUtils.getRelativeTimeSpanString(Long.parseLong(notification.getCreation_time())));
        } else {

        }
    }

    public String getNotificationText(String type, String name, int viewstate) {
        String str = null;
        switch (type) {
            case Const.LIKE_POST:
                str = name + activity.getString(R.string.liked_your_post);
                break;
            case Const.COMMENT_POST:
                str = name + activity.getString(R.string.commented_your_post);
                break;
            case Const.SHARE_POST:
                str = name + activity.getString(R.string.shared_your_post);
                break;
            case Const.TAGGED_ON_POST:
                str = name + activity.getString(R.string.tagged_in_post);
                break;
            case Const.TAGGED_ON_COMMENT:
                str = name + activity.getString(R.string.tagged_in_comment);
                break;
            case Const.FOLLOWING_USER:
                str = name + activity.getString(R.string.following_you);
                break;
            case Const.POST_EXPERT:
                str = name + activity.getString(R.string.newpost);
                break;
        }
        if (viewstate == 0)
            return "<b>" + str + "</b>";
        else return str;
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout notifyRL;
        TextView descriptionTV, timeTV;
        Button loadMoreBtn;
        ImageView imageIV, imageIVText;
        Activity activity;
        int pos;

        public ViewHolder(final View view, final Activity activity, int viewType) {
            super(view);
            if (viewType == 0) {
                this.activity = activity;
                descriptionTV = view.findViewById(R.id.descriptionTV);
                notifyRL = view.findViewById(R.id.notifyRL);
                timeTV = view.findViewById(R.id.timeTV);

                imageIV = view.findViewById(R.id.imageIV);
                imageIVText = view.findViewById(R.id.imageIVText);

                view.setOnClickListener(v -> {
                        pos = getAdapterPosition();
                        ViewHolder vholder = (ViewHolder) view.getTag();
                        NotificationResponse notification = notificationArrayList.get(pos);

                        // this is to change the state of the notification message
                        if (notification.getView_state() == 0) {
                            SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) - 1);
                            ShortcutBadger.applyCount(activity.getApplicationContext(), SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT));
                            notificationArrayList.get(pos).setView_state(1);
                            vholder.descriptionTV.setText(HtmlCompat.fromHtml(getNotificationText(notification.getActivity_type(), notification.getAction_performed_by().getName(), 1),HtmlCompat.FROM_HTML_MODE_LEGACY));

                            networkCallForNotificationState();//networkCall.NetworkAPICall(API.API_CHANGE_NOTIFICATION_STATE, false);
                        }

                        //this is to call the newtag intent to pass the notification to the respective screen
                        if (notification.getActivity_type().equals(Const.TAGGED_ON_COMMENT)) {
                            Log.e( "onClick: ", Const.TAGGED_ON_COMMENT);
                            Intent intent = new Intent(NotificationRVAdapter.this.activity, PostActivity.class); // Comment Fragment // to show the post
                            intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                            intent.putExtra(Const.COMMENT_ID, notification.getComment_id());
                            NotificationRVAdapter.this.activity.startActivity(intent);

                        } else if (!notification.getActivity_type().equals(Const.FOLLOWING_USER) &&
                                !notification.getActivity_type().equals(Const.TAGGED_ON_COMMENT)) {
                            Log.e( "onClick: ","comment_tagged_off" );
                            Intent intent = new Intent(NotificationRVAdapter.this.activity, PostActivity.class); // Comment Fragment // to show the post
                            intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                            intent.putExtra(Const.POST_ID, notification.getPost_id());
                            NotificationRVAdapter.this.activity.startActivity(intent);
                        } else
                            Helper.GoToProfileActivity(NotificationRVAdapter.this.activity, notificationArrayList.get(pos).getAction_performed_by().getId());
                });
            } else {
                loadMoreBtn = view.findViewById(R.id.loadMoreBtn);

                loadMoreBtn.setOnClickListener(v -> {
                        commonFragment.lastActivityId = notificationArrayList.get(notificationArrayList.size() - 1).getId();
                        commonFragment.refreshCommon(false);
                });
            }
        }

        private void networkCallForNotificationState() {
            NotificationApiInterface apiInterface = ApiClient.createService(NotificationApiInterface.class);
            Call<JsonObject> response = apiInterface.notificationstatechange(SharedPreference.getInstance().getLoggedInUser().getId(),
                    notificationArrayList.get(pos).getId());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });

        }
    }

}
