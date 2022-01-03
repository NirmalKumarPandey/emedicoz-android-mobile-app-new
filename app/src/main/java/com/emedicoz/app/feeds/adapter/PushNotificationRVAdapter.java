package com.emedicoz.app.feeds.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.customviews.WebViewActivity;
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.feeds.activity.NewProfileActivity;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.feeds.fragment.CommonFragment;
import com.emedicoz.app.modelo.Notification;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.video.activity.VideoDetail;

import java.util.ArrayList;


/**
 * TODO: Replace the implementation with code for your data type.
 */
public class PushNotificationRVAdapter extends RecyclerView.Adapter<PushNotificationRVAdapter.ViewHolder> {

    ArrayList<Notification> notificationArrayList;
    Activity activity;
    CommonFragment commonFragment;

    public PushNotificationRVAdapter(ArrayList<Notification> notificationArrayList, Activity activity, CommonFragment commonFragment) {
        this.activity = activity;
        this.commonFragment = commonFragment;
        this.notificationArrayList = notificationArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == notificationArrayList.size())
            return 0;
        else return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_notification, parent, false);

        return new ViewHolder(view, activity, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        Notification notification = notificationArrayList.get(position);


        Drawable dr = Helper.GetDrawable(notification.getMessage(), activity, String.valueOf(notification.getNotificationCode()));
        if (dr != null) {
            holder.ImageIV.setVisibility(View.GONE);
            holder.ImageIVText.setVisibility(View.VISIBLE);
            holder.ImageIVText.setImageDrawable(dr);
        } else {
            holder.ImageIV.setVisibility(View.VISIBLE);
            holder.ImageIVText.setVisibility(View.GONE);
            holder.ImageIV.setImageResource(R.mipmap.default_pic);
        }


        holder.notifyRL.setTag(holder);
        holder.DescriptionTV.setText(Html.fromHtml(notification.getMessage()));
        holder.TimeTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(notification.getTime())).equals("0 minutes ago") ? "Just Now" : DateUtils.getRelativeTimeSpanString(Long.parseLong(notification.getTime())));


        holder.notifyRL.setOnClickListener(v -> click(notificationArrayList.get(position)));
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    public void click(Notification notification) {
        Intent intent = null;
        if (notification.getNotificationCode() == 401 || notification.getNotificationCode() == 90001) {// 90001 is anonumous notification to open the feeds fragment
            if (notification.getNotificationData().getMessage_target().equalsIgnoreCase(Const.URL)) {
                intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra(Const.URL, notification.getNotificationData().getUrl());
            } else if (notification.getNotificationData().getMessage_target().equalsIgnoreCase(Const.VIDEO_CHANNEL)) { // to open a video Chanel Specific
                intent = new Intent(activity, VideoDetail.class);
                intent.putExtra(Const.URL, notification.getNotificationData().getUrl());
                intent.putExtra(Constants.Extras.TYPE, Const.NOTIFICATION);
            } else if (notification.getNotificationData().getMessage_target().equalsIgnoreCase(Const.FEED_SECTION)) { // to open a video Chanel Specific
                intent = new Intent(activity, PostActivity.class);
                intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                intent.putExtra(Const.POST_ID, notification.getNotificationData().getUrl());
            } else if (notification.getNotificationData().getMessage_target().equalsIgnoreCase(Const.COURSE_SECTION)) { // to show course Promotion
//                    intent = new Intent(this, VideoDetail.class);
//                    intent.putExtra(Const.URL, notification.getNotificationData().getUrl());
//                    intent.putExtra(Const.TYPE, Const.NOTIFICATION);
                Course course = new Course();
                course.setId(notification.getNotificationData().getUrl());
                intent = new Intent(activity, CourseActivity.class);
                intent.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                intent.putExtra(Constants.Extras.TYPE, Const.NOTIFICATION);
                intent.putExtra(Const.COURSES, course);
            } else {
                intent = new Intent(activity, FeedsActivity.class); // for live notification will go to feeds fragment.
            }
        } else if (notification.getNotificationCode() == 601) {
            SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) + 1);
            intent = new Intent(activity, PostActivity.class); // this is for the user tagged in comment
            intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
            intent.putExtra(Const.COMMENT_ID, notification.getNotificationData().getComment_id());
        } else if (notification.getNotificationCode() == 701) {
            SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) + 1);
            intent = new Intent(activity, NewProfileActivity.class); // this is for following the user
            intent.putExtra(Constants.Extras.TYPE, Const.LAST);
            intent.putExtra(Constants.Extras.ID, notification.getNotificationData().getUser_id());
        } else {
            SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) + 1);
            intent = new Intent(activity, PostActivity.class); // comment fragment to open the related post.
            intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
            intent.putExtra(Const.POST_ID, notification.getNotificationData().getPost_id());
        }

        activity.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout notifyRL;
        TextView DescriptionTV, TimeTV;
        ImageView ImageIV, ImageIVText;


        public ViewHolder(final View view, final Activity activity, int viewType) {
            super(view);
            DescriptionTV = view.findViewById(R.id.descriptionTV);
            notifyRL = view.findViewById(R.id.notifyRL);
            TimeTV = view.findViewById(R.id.timeTV);

            ImageIV = view.findViewById(R.id.imageIV);
            ImageIVText = view.findViewById(R.id.imageIVText);

        }

    }
}
