package com.emedicoz.app.video.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.feeds.fragment.PeopleTagSelectionFragment;
import com.emedicoz.app.modelo.Comment;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.VideoCommentApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.video.activity.CommentActivity;
import com.emedicoz.app.video.activity.VideoDetail;
import com.emedicoz.app.video.fragment.VideoFragmentViewPager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nex3z.flowlayout.FlowLayout;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin1 on 9/11/17.
 */

public class VideoCommentAdapter extends RecyclerView.Adapter<VideoCommentAdapter.Viewholder> {

    Viewholder holder;
    int commentLength = 50;
    List<Comment> list;
    Activity activity;
    Video video;
    String strComment;
    ClickableSpan readMoreClick;
    ClickableSpan readLessClick;

    public VideoCommentAdapter(List<Comment> list, Activity activity, Video video) {
        this.list = list;
        this.activity = activity;
        this.video = video;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_comment_two, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {
        final Comment comment = list.get(position);
        holder.setCommentView(comment);
        holder.likeCommentLL.setOnClickListener((View v) -> {
            if (comment.getIs_like() != null) {
                if (comment.getIs_like().equalsIgnoreCase("0")) {
                    holder.likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.like_blue));
                    holder.likeCommentTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                    comment.setIs_like("1");
                    if (comment.getLikes().equalsIgnoreCase("0")) {
                        holder.commentLikeCount.setText(String.format("%s like", (Integer.parseInt(comment.getLikes()) + 1)));
                    } else {
                        holder.commentLikeCount.setText(String.format("%s likes", (Integer.parseInt(comment.getLikes()) + 1)));
                    }
                    comment.setLikes(String.valueOf(Integer.parseInt(comment.getLikes()) + 1));
                    holder.networkCallForLikeComment(comment);
                } else {
                    holder.likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.like));
                    holder.likeCommentTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
                    comment.setIs_like("0");
                    if (comment.getLikes().equalsIgnoreCase("1")) {
                        holder.commentLikeCount.setText(String.format("%s like", (Integer.parseInt(comment.getLikes()) - 1)));
                    } else {
                        holder.commentLikeCount.setText(String.format("%s likes", (Integer.parseInt(comment.getLikes()) - 1)));
                    }
                    comment.setLikes(String.valueOf(Integer.parseInt(comment.getLikes()) - 1));
                    holder.networkCallForDislikeComment(comment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView nameTV;
        TextView commentTV;
        TextView dateTV;
        TextView replyTV;
        TextView commentCountTV;
        TextView commentLikeCount;
        TextView likeCommentTV;
        ImageView imageIV;
        ImageView moreOptionIV;
        ImageView imageIVText;
        ImageView replyIV;
        ImageView likeCommentIV;
        ImageView postButton;
        EditText writeCommentET;
        String commentText;
        FlowLayout taggedPeoplesListFL;
        LinearLayout showCommentLL;
        LinearLayout writeCommentLL;
        LinearLayout taggedPeoplesListLL;
        LinearLayout addUserTagCommentListLL;
        RelativeLayout imageRL;
        RelativeLayout commentCountRL;
        Comment mainComment;
        LinearLayout likeCommentLL;
        LinearLayout replyCommentLL;
        ArrayList<String> oldids;
        ArrayList<String> newids;
        String taggedPeopleIdsAdded;
        String taggedPeopleIdsRemoved;
        FlowLayout taggedPeopleCommentListFL;
        ArrayList<People> taggedPeopleArrList;

        public Viewholder(final View view) {
            super(view);

            nameTV = view.findViewById(R.id.nameTV);
            dateTV = view.findViewById(R.id.dateTV);
            commentTV = view.findViewById(R.id.commentTV);
            replyTV = view.findViewById(R.id.replyTV);
            replyIV = view.findViewById(R.id.replyIV);
            commentCountTV = view.findViewById(R.id.commentcountTV);
            commentLikeCount = view.findViewById(R.id.commentLikeCount);
            imageIV = view.findViewById(R.id.imageIV);
            likeCommentLL = view.findViewById(R.id.likeCommentLL);
            replyCommentLL = view.findViewById(R.id.replyCommentLL);
            imageIVText = view.findViewById(R.id.ImageIVText);
            moreOptionIV = view.findViewById(R.id.moreoptionIV);
            postButton = view.findViewById(R.id.postcommentIBtn);
            likeCommentIV = view.findViewById(R.id.likeComment);
            likeCommentTV = view.findViewById(R.id.likeTv);
            showCommentLL = view.findViewById(R.id.showcommentLL);
            writeCommentLL = view.findViewById(R.id.writecommentLL);
            taggedPeoplesListLL = view.findViewById(R.id.taggedpeopleclistLL);
            addUserTagCommentListLL = view.findViewById(R.id.addusertagcommentlistLL);

            taggedPeoplesListFL = view.findViewById(R.id.taggedpeopleclistFL);
            taggedPeopleCommentListFL = view.findViewById(R.id.taggedpeoplecommentlistFL);

            imageRL = view.findViewById(R.id.imageRL);
            commentCountRL = view.findViewById(R.id.commentcountRL);

            writeCommentET = view.findViewById(R.id.writecommentET);
            Helper.CaptializeFirstLetter(writeCommentET);
            addUserTagCommentListLL.setOnClickListener(view1 -> {
                PeopleTagSelectionFragment newpeopletag = (PeopleTagSelectionFragment) Fragment.instantiate(activity, PeopleTagSelectionFragment.class.getName());
                Bundle bn = new Bundle();
                bn.putSerializable(Const.ALREADY_TAGGED_PEOPLE, taggedPeopleArrList);
                newpeopletag.setArguments(bn);
                newpeopletag.show(((PostActivity) activity).getSupportFragmentManager(), "dialog");
            });
            nameTV.setOnClickListener(view1 -> Helper.GoToProfileActivity(activity, list.get(getAdapterPosition()).getUser_id()));
            imageRL.setOnClickListener(view1 -> Helper.GoToProfileActivity(activity, list.get(getAdapterPosition()).getUser_id()));

            replyCommentLL.setOnClickListener(view1 -> goToCommentActivity());
            commentCountTV.setOnClickListener(view1 -> goToCommentActivity());
        }

        private void goToCommentActivity() {
            Intent intent = new Intent(activity, CommentActivity.class);
            intent.putExtra(Constants.Extras.TYPE, Const.COMMENT_LIST);
            intent.putExtra(Const.VIDEO_ID, video.getId());
            intent.putExtra(Const.DATA, video);
            intent.putExtra(Const.PARENT_ID, list.get(getAdapterPosition()).getId());
            intent.putExtra(Const.COMMENT, list.get(getAdapterPosition()));
            activity.startActivity(intent);
        }

        public void setCommentView(final Comment comment) {

            writeCommentLL.setVisibility(View.GONE);
            showCommentLL.setVisibility(View.VISIBLE);

            comment.setName(Helper.CapitalizeText(comment.getName()));
            nameTV.setText(HtmlCompat.fromHtml("<b>" + comment.getName() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));

            setLikesCount(comment);

            if (comment.getIs_like() != null) {
                if (comment.getIs_like().equalsIgnoreCase("1")) {
                    likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.like_blue));
                    likeCommentTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                } else {
                    likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.like));
                    likeCommentTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
                }
            }

            strComment = comment.getComment();
            if (strComment.length() > commentLength) {
                strComment = strComment.substring(0, commentLength) + "...";
                commentTV.setText(String.format("%s%s", strComment, HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Show More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                readMoreClick = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        commentTV.setText(String.format("%s %s", HtmlCompat.fromHtml(comment.getComment(), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Show Less</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                        Helper.makeLinks(commentTV, "Show Less", readLessClick);
                    }
                };

                readLessClick = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        commentTV.setText(String.format("%s %s", HtmlCompat.fromHtml(strComment, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Show More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                        Helper.makeLinks(commentTV, "Show More", readMoreClick);
                    }
                };
                Helper.makeLinks(commentTV, "Show More", readMoreClick);
            } else {
                commentTV.setText(HtmlCompat.fromHtml(strComment, HtmlCompat.FROM_HTML_MODE_LEGACY));
            }

            String date = (String) DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getTime()));
            String[] dates = date.split("\\s+");
            if (dates.length > 1) {
                if (dates[1].equalsIgnoreCase("minutes")) {
                    dates[1] = dates[1].substring(0, 1);
                } else {
                    dates[1] = dates[1].substring(0, 1);
                }
                if (DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getTime())).equals("0 minutes ago")) {
                    dateTV.setText("Just Now");
                } else {
                    String newDate = dates[0] + " " + dates[1];
                    dateTV.setText(newDate);
                }
            } else {
                dateTV.setText(dates[0]);
            }

            String count = comment.getSub_comment_count();

            if (comment.getParent_id().equals("0")) {
                replyCommentLL.setVisibility(View.VISIBLE);
            } else replyCommentLL.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(count) && !count.equals("0")) {
                commentCountRL.setVisibility(View.VISIBLE);
                if (count.equals("1"))
                    commentCountTV.setText(String.format("See %s previous reply", count));
                else
                    commentCountTV.setText(String.format("See %s previous replies", count));
            } else commentCountRL.setVisibility(View.GONE);


            if (!TextUtils.isEmpty(comment.getProfile_picture())) {
                imageIV.setVisibility(View.VISIBLE);
                imageIVText.setVisibility(View.GONE);
                Glide.with(activity)
                        .asBitmap()
                        .load(comment.getProfile_picture())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_pic).error(R.mipmap.default_pic))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                imageIV.setImageBitmap(result);
                            }
                        });
            } else {
                Drawable dr = Helper.GetDrawable(comment.getName(), activity, comment.getUser_id());
                if (dr != null) {
                    imageIV.setVisibility(View.GONE);
                    imageIVText.setVisibility(View.VISIBLE);
                    imageIVText.setImageDrawable(dr);
                } else {
                    imageIV.setVisibility(View.VISIBLE);
                    imageIVText.setVisibility(View.GONE);
                    imageIV.setImageResource(R.mipmap.default_pic);
                }
            }

            if (!GenericUtils.isListEmpty(comment.getTagged_people())) {
                taggedPeoplesListLL.setVisibility(View.VISIBLE);
                if (taggedPeoplesListFL.getChildCount() > 0) taggedPeoplesListFL.removeAllViews();


            } else taggedPeoplesListLL.setVisibility(View.GONE);


            if (comment.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId())
                    ||
                    (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getIs_moderate()) &&
                            SharedPreference.getInstance().getLoggedInUser().getIs_moderate().equalsIgnoreCase("1"))
            ) {
                moreOptionIV.setVisibility(View.VISIBLE);
            } else moreOptionIV.setVisibility(View.GONE);
            moreOptionIV.setTag(this);
            moreOptionIV.setOnClickListener(view -> {
                mainComment = comment;
                holder = (Viewholder) view.getTag();

                int temp = 0;
                if (comment.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
                    temp++;
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getIs_moderate()) && SharedPreference.getInstance().getLoggedInUser().getIs_moderate().equals("1"))
                    temp += 2;
                showPopMenu(view, temp);
            });
        }

        private void setLikesCount(Comment comment) {
            if (comment.getLikes() != null) {
                if (comment.getLikes().equalsIgnoreCase("0") || comment.getLikes().equalsIgnoreCase("1")) {
                    commentLikeCount.setText(String.format("%s like", comment.getLikes()));
                } else {
                    commentLikeCount.setText(String.format("%s likes", comment.getLikes()));
                }
            }
        }


        public void deleteComment() {
            View v = Helper.newCustomDialog(activity, false, activity.getString(R.string.delete), activity.getString(R.string.do_you_really_want_to_delete));

            Button btnCancel;
            Button btnSubmit;

            btnCancel = v.findViewById(R.id.btn_cancel);
            btnSubmit = v.findViewById(R.id.btn_submit);

            btnCancel.setText(activity.getString(R.string.cancel).toUpperCase());
            btnSubmit.setText(activity.getString(R.string.ok).toUpperCase());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.dp120), (int) activity.getResources().getDimension(R.dimen.dp35));
            params.setMargins(0, (int) activity.getResources().getDimension(R.dimen.dp20), 0, (int) activity.getResources().getDimension(R.dimen.dp20));
            btnCancel.setLayoutParams(params);
            btnSubmit.setLayoutParams(params);

            btnCancel.setOnClickListener((View view) -> Helper.dismissDialog());

            btnSubmit.setOnClickListener((View view) -> {
                Helper.dismissDialog();
                networkCallForDeleteComment();
            });
        }

        public void editComment() {
            taggedPeopleArrList = null;
            taggedPeopleCommentListFL.removeAllViews();
            if (activity instanceof VideoDetail) {
                ((VideoDetail) activity).writeCommentLL.setVisibility(View.GONE);
                ((VideoDetail) activity).isUserEditingComment = true;
            } else {
                ((CommentActivity) activity).commentLayout.setVisibility(View.GONE);
            }

            moreOptionIV.setVisibility(View.GONE);
            writeCommentLL.setVisibility(View.VISIBLE);
            showCommentLL.setVisibility(View.GONE);
            imageRL.setVisibility(View.GONE);
            addUserTagCommentListLL.setVisibility(View.GONE);
            writeCommentET.setText(StringEscapeUtils.unescapeJava(mainComment.getComment()));
            writeCommentET.setSelection(StringEscapeUtils.unescapeJava(mainComment.getComment()).length());

            postButton.setOnClickListener(v -> {
                if (Helper.isConnected(activity)) {
                    checkValidation();
                } else {
                    Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void checkValidation() {
            commentText = Helper.GetText(writeCommentET);
            boolean isDataValid = true;

            if (!GenericUtils.isListEmpty(newids)) {
                for (String str : newids) {
                    if (!oldids.contains(str)) {
                        if (TextUtils.isEmpty(taggedPeopleIdsAdded))
                            taggedPeopleIdsAdded = str;
                        else taggedPeopleIdsAdded = taggedPeopleIdsAdded + "," + str;
                    }
                }
            }

            if (!GenericUtils.isListEmpty(oldids)) {
                for (String str : oldids) {
                    if (newids != null && !newids.contains(str)) {
                        if (TextUtils.isEmpty(taggedPeopleIdsRemoved))
                            taggedPeopleIdsRemoved = str;
                        else taggedPeopleIdsRemoved = taggedPeopleIdsRemoved + "," + str;
                    }
                }
            }

            if (TextUtils.isEmpty(commentText))
                isDataValid = Helper.DataNotValid(writeCommentET);

            if (isDataValid) {
                mainComment.setComment(commentText);
                networkCallForEditComment();//networkCall.NetworkAPICall(API.API_EDIT_VIDEO_COMMENT, true);
            }
        }


        public void showPopMenu(final View v, int userDecider) {
            PopupMenu popup = new PopupMenu(activity, v);
            popup.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.editIM:
                        editComment();
                        return true;
                    case R.id.deleteIM:
                        deleteComment();
                        return true;
                    default:
                        return false;
                }
            });
            popup.inflate(R.menu.comment_menu);
            Menu menu = popup.getMenu();

            menu.findItem(R.id.editIM).setVisible((userDecider != 0));
            menu.findItem(R.id.deleteIM).setVisible((userDecider != 0));

            popup.show();
        }

        private void networkCallForEditComment() {
            VideoCommentApiInterface apiInterface = ApiClient.createService(VideoCommentApiInterface.class);
            Log.e("EDIT_COMMENT", "ID : " + mainComment.getId() + "USER_ID : " + mainComment.getUser_id() + "VIDEO_ID : " + video.getId() + "COMMENT : " + mainComment.getComment());
            Call<JsonObject> response = apiInterface.editVideoComment(mainComment.getId(), mainComment.getUser_id(),
                    video.getId(), StringEscapeUtils.escapeJava(mainComment.getComment()));
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                int pos = list.indexOf(mainComment);
                                mainComment = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), Comment.class);
                                list.set(pos, mainComment);
                                notifyItemChanged(pos, mainComment);
                                imageRL.setVisibility(View.VISIBLE);
                                commentTV.setText(StringEscapeUtils.unescapeJava(mainComment.getComment()));
                                writeCommentLL.setVisibility(View.VISIBLE);
                                if (activity instanceof VideoDetail) {
                                    ((VideoDetail) activity).writeCommentLL.setVisibility(View.VISIBLE);
                                } else {
                                    ((CommentActivity) activity).commentLayout.setVisibility(View.VISIBLE);
                                    SharedPreference.getInstance().putBoolean(Const.IS_FROM_COMMENT_ACTIVITY, true);
                                }
                                notifyDataSetChanged();
                            } else {
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("onFailure: ", Objects.requireNonNull(t.getMessage()));
                }
            });
        }

        private void networkCallForDeleteComment() {
            VideoCommentApiInterface apiInterface = ApiClient.createService(VideoCommentApiInterface.class);
            Call<JsonObject> response = apiInterface.deleteVideoComment(mainComment.getId(), mainComment.getUser_id(),
                    video.getId());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                if (activity instanceof CommentActivity) {
                                    SharedPreference.getInstance().putBoolean(Const.IS_FROM_COMMENT_ACTIVITY, true);
                                }
                                int pos = list.indexOf(mainComment);
                                list.remove(pos);
                                if (list.isEmpty()) {
                                    SharedPreference sharedPreference = SharedPreference.getInstance();
                                    sharedPreference.setCommentUpdate(video);
                                    if (activity instanceof VideoDetail) {
                                        ((VideoDetail) activity).Comment_Update();
                                        ((VideoDetail) activity).callcommentview();
                                    } else {
                                        ((CommentActivity) activity).commentUpdate();
                                        ((CommentActivity) activity).callCommentView();
                                    }
                                }
                                notifyItemRemoved(pos);
                                if (activity instanceof VideoDetail) {
                                    if (((VideoDetail) activity).type.equalsIgnoreCase(Const.COMMENT_LIST))
                                        VideoFragmentViewPager.IS_COMMENT_UPDATE = true;
                                } else if (activity instanceof CommentActivity) {
                                    VideoFragmentViewPager.IS_COMMENT_UPDATE = true;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("onFailure: ", Objects.requireNonNull(t.getMessage()));
                }
            });
        }

        private void networkCallForLikeComment(final Comment comment) {
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.likeVideoComment(SharedPreference.getInstance().getLoggedInUser().getId(), comment.getId());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                //Toast.makeText(activity, jsonResponse.optString(Const.MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
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

        private void networkCallForDislikeComment(final Comment comment) {
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.dislikeVideoComment(SharedPreference.getInstance().getLoggedInUser().getId(), comment.getId());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            } else {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


}


