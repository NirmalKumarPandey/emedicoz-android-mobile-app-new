package com.emedicoz.app.feeds.adapter;

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
import com.emedicoz.app.feeds.activity.FeedsActivity;
import com.emedicoz.app.feeds.activity.ImageViewActivity;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.feeds.fragment.CommonFragment;
import com.emedicoz.app.feeds.fragment.PeopleTagSelectionFragment;
import com.emedicoz.app.modelo.Comment;
import com.emedicoz.app.modelo.MediaFile;
import com.emedicoz.app.modelo.People;
import com.emedicoz.app.modelo.PostFile;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack;
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nex3z.flowlayout.FlowLayout;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * TODO: Replace the implementation with code for your data type.
 */
public class CommentRVAdapter extends RecyclerView.Adapter<CommentRVAdapter.ViewHolder> {

    public ViewHolder holder;
    ArrayList<Comment> commentArrayList;
    Activity activity;
    Fragment fragment;

    public CommentRVAdapter(ArrayList<Comment> commentArrayList, Activity activity) {
        this.commentArrayList = commentArrayList;
        this.activity = activity;
        fragment = ((PostActivity) activity).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_comment_two, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Comment comment = commentArrayList.get(position);
        holder.setCommentView(comment);
        holder.likeCommentLL.setOnClickListener((View v) -> {
            if (comment.getIs_like().equalsIgnoreCase("0")) {
                holder.likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.like_blue));
                holder.likeTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                comment.setIs_like("1");

                comment.setLikes(String.valueOf(Integer.parseInt(comment.getLikes()) + 1));
                holder.commentLikeCount.setText(String.format("%d likes", Integer.parseInt(comment.getLikes())));
                holder.networkCallForLikeComment(comment);
            } else {
                holder.likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.like));
                holder.likeTV.setTextColor(ContextCompat.getColor(activity, R.color.black));
                comment.setIs_like("0");

                comment.setLikes(String.valueOf(Integer.parseInt(comment.getLikes()) - 1));
                holder.commentLikeCount.setText(String.format("%d likes", Integer.parseInt(comment.getLikes())));
                holder.networkCallForDislikeComment(comment);
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public void onChangeImage(Comment comment, ViewHolder viewHolder) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AmazonCallBack {
        public ImageView imageView, moreOptionIV, imageIVText;
        public ImageView postBtn;
        public ImageView addCommentIV;
        public ImageView showCommentIV;
        public ImageView imageIV, imageIV1;
        public ImageView deleteIV;
        public ImageView replyIV;
        public ImageView likeCommentIV;
        public RelativeLayout commentImageRL;
        public MediaFile mediaFile;
        public int commentLength = 50;
        String strComment = "";
        TextView nameTV;
        TextView nameExpertIV;
        TextView commentTV;
        TextView dateTV;
        TextView commentCountTV;
        TextView commentLikeCount;
        TextView likeTV;
        EditText writeCommentET;
        String commentText, commentImages;
        FlowLayout taggedPeopleListFL;
        LinearLayout showCommentLL;
        LinearLayout writeCommentLL;
        LinearLayout taggedPeopleListLL;
        LinearLayout addUserTagCommentListLL;
        LinearLayout replyCommentLL;
        LinearLayout likeCommentLL;
        RelativeLayout imageRL, commentCountRL;
        Comment mainComment;
        ArrayList<String> oldIds, newIds;
        String taggedPeopleIdsAdded;
        String taggedPeopleIdsRemoved;
        FlowLayout taggedPeopleCommentListFL;
        ArrayList<People> taggedPeopleArrList;
        s3ImageUploading s3ImageUploading;
        ClickableSpan readMoreClick, readLessClick;


        public ViewHolder(final View view) {
            super(view);

            replyIV = view.findViewById(R.id.replyIV);
            commentCountTV = view.findViewById(R.id.commentcountTV);
            commentLikeCount = view.findViewById(R.id.commentLikeCount);
            nameTV = view.findViewById(R.id.nameTV);
            dateTV = view.findViewById(R.id.dateTV);
            commentTV = view.findViewById(R.id.commentTV);
            likeTV = view.findViewById(R.id.likeTv);
            likeCommentIV = view.findViewById(R.id.likeComment);
            imageView = view.findViewById(R.id.imageIV);
            likeCommentLL = view.findViewById(R.id.likeCommentLL);
            replyCommentLL = view.findViewById(R.id.replyCommentLL);
            imageIVText = view.findViewById(R.id.ImageIVText);
            moreOptionIV = view.findViewById(R.id.moreoptionIV);
            postBtn = view.findViewById(R.id.postcommentIBtn);
            addCommentIV = view.findViewById(R.id.addcommentIV);
            showCommentIV = view.findViewById(R.id.showCommentIV);
            nameExpertIV = view.findViewById(R.id.nameExpertIV);

            showCommentLL = view.findViewById(R.id.showcommentLL);
            writeCommentLL = view.findViewById(R.id.writecommentLL);
            taggedPeopleListLL = view.findViewById(R.id.taggedpeopleclistLL);
            addUserTagCommentListLL = view.findViewById(R.id.addusertagcommentlistLL);
            imageIV = view.findViewById(R.id.commentimageIV);
            imageIV1 = view.findViewById(R.id.commentimageIV1);
            deleteIV = view.findViewById(R.id.commentdeleteIV);
            commentImageRL = view.findViewById(R.id.commentimageRL);

            taggedPeopleListFL = view.findViewById(R.id.taggedpeopleclistFL);
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
            nameTV.setOnClickListener(view1 -> Helper.GoToProfileActivity(activity, commentArrayList.get(getAdapterPosition()).getUser_id()));
            imageRL.setOnClickListener(view1 -> Helper.GoToProfileActivity(activity, commentArrayList.get(getAdapterPosition()).getUser_id()));

            replyCommentLL.setOnClickListener(view1 -> goToReplyCommentScreen());
            commentCountTV.setOnClickListener(view1 -> goToReplyCommentScreen());
        }

        private void goToReplyCommentScreen() {
            // comment fragment to open the related comment replies from CommentRVAdapter

            Intent intent = new Intent(activity, PostActivity.class);
            intent.putExtra(Const.FRAG_TYPE, Const.COMMENT_LIST);
            intent.putExtra(Const.POST, ((CommonFragment) fragment).post);
            intent.putExtra(Const.COMMENT_ID, commentArrayList.get(getAdapterPosition()).getId());
            intent.putExtra(Const.POST_ID, commentArrayList.get(getAdapterPosition()).getPost_id());
            intent.putExtra(Constants.Extras.NAME, commentArrayList.get(getAdapterPosition()).getName());
            intent.putExtra(Const.COMMENT, commentArrayList.get(getAdapterPosition()));
            activity.startActivity(intent);
        }

        private void networkCallForLikeComment(final Comment comment) {
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.likeComment(SharedPreference.getInstance().getLoggedInUser().getId(), comment.getId());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
 /*                           likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity,R.mipmap.active_comment_like));
                            comment.setIs_like("1");
                            if (comment.getLikes().equalsIgnoreCase("0")){
                            commentLikeCount.setText(String.valueOf(Integer.parseInt(comment.getLikes())+1)+" like");
                            comment.setLikes(String.valueOf(Integer.parseInt(comment.getLikes())+1));
                            }
                            else {
                                commentLikeCount.setText(String.valueOf(Integer.parseInt(comment.getLikes())+1)+" likes");
                                comment.setLikes(String.valueOf(Integer.parseInt(comment.getLikes())+1));
                            }*/
/*                            if (jsonResponse.optString(Const.MESSAGE).equalsIgnoreCase("post liked.")) {
                                Toast.makeText(activity, "Comment Liked.", Toast.LENGTH_SHORT).show();
                            }*/
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
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.dislikeComment(SharedPreference.getInstance().getLoggedInUser().getId(), comment.getId());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
               /*             likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity,R.mipmap.default_comment_like));
                            comment.setIs_like("0");
                            if (comment.getLikes().equalsIgnoreCase("1")){
                                commentLikeCount.setText(String.valueOf(Integer.parseInt(comment.getLikes())-1)+" like");
                                comment.setLikes(String.valueOf(Integer.parseInt(comment.getLikes())-1));
                            }
                            else {
                                commentLikeCount.setText(String.valueOf(Integer.parseInt(comment.getLikes())-1)+" likes");
                                comment.setLikes(String.valueOf(Integer.parseInt(comment.getLikes())-1));
                            }*/
/*                            if (jsonResponse.optString(Const.MESSAGE).equalsIgnoreCase("Post disliked.")) {
                                Toast.makeText(activity, "Comment disliked.", Toast.LENGTH_SHORT).show();
                            }*/
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

        private void networkCallForEditComment() {
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.editComment(mainComment.getId(), mainComment.getUser_id()
                    , mainComment.getPost_id(), StringEscapeUtils.escapeJava(mainComment.getComment()), commentImages,
                    (TextUtils.isEmpty(taggedPeopleIdsAdded) ? "" : taggedPeopleIdsAdded),
                    (TextUtils.isEmpty(taggedPeopleIdsRemoved) ? "" : taggedPeopleIdsRemoved));
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                taggedPeopleListFL.setVisibility(View.VISIBLE);
                                int pos = commentArrayList.indexOf(mainComment);
                                commentImages = "";
                                ((CommonFragment) fragment).commentImages = "";
                                mainComment = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), Comment.class);
                                commentArrayList.set(pos, mainComment);
                                notifyItemChanged(pos, mainComment);
                                imageRL.setVisibility(View.VISIBLE);
                                strComment = mainComment.getComment();

                                if (strComment.length() > commentLength) {
                                    strComment = strComment.substring(0, commentLength) + "...";
                                    commentTV.setText(String.format("%s%s", strComment, HtmlCompat.fromHtml("<font color='#cccccc'> <u>Show More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                                    readMoreClick = new ClickableSpan() {
                                        @Override
                                        public void onClick(@NonNull View view) {
                                            commentTV.setText(String.format("%s %s", HtmlCompat.fromHtml(mainComment.getComment(), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#cccccc'> <u>Show Less</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                                            Helper.makeLinks(commentTV, "Show Less", readLessClick);
                                        }
                                    };

                                    readLessClick = new ClickableSpan() {
                                        @Override
                                        public void onClick(@NonNull View view) {
                                            commentTV.setText(String.format("%s %s", HtmlCompat.fromHtml(strComment, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.fromHtml("<font color='#cccccc'> <u>Show More</u></font>", HtmlCompat.FROM_HTML_MODE_LEGACY)));
                                            Helper.makeLinks(commentTV, Const.SHOW_MORE, readMoreClick);
                                        }
                                    };
                                    Helper.makeLinks(commentTV, Const.SHOW_MORE, readMoreClick);
                                } else {
                                    commentTV.setText(HtmlCompat.fromHtml(strComment, HtmlCompat.FROM_HTML_MODE_LEGACY));
                                }
                                ((CommonFragment) fragment).writeCommentLL.setVisibility(View.VISIBLE);
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

        private void networkCallForDeleteComment() {
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.deleteComment(mainComment.getId(), mainComment.getUser_id(),
                    mainComment.getPost_id());
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                ((CommonFragment) fragment).setComments(0);
                                FeedsActivity.isCommentRefreshed = true;
                                commentImages = "";
                                ((CommonFragment) fragment).commentImages = "";
                                ((CommonFragment) fragment).isImageAdded = false;
                                int pos = commentArrayList.indexOf(mainComment);
                                SharedPreference.getInstance().setPost(((CommonFragment) fragment).post);
                                commentArrayList.remove(pos);
                                if (commentArrayList.isEmpty()) {
                                    ((CommonFragment) fragment).lastCommentId = "";
                                    ((CommonFragment) fragment).initCommentAdapter();
                                } else {
                                    notifyItemRemoved(pos);
                                }
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
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

        // this is to show the tagged people in the post
        public void addViewToTagPeople(final People response) {

            View v = View.inflate(activity, R.layout.single_textview_people_tag, null);
            TextView tv = v.findViewById(R.id.nameTV);
            ImageView delete = v.findViewById(R.id.deleteIV);
            delete.setVisibility(View.GONE);
            tv.setText(response.getName());
            v.setTag(response);
            v.setOnClickListener((View view) -> Helper.GoToProfileActivity(activity, response.getId()));
            taggedPeopleListFL.addView(v);
        }

        public void setCommentView(final Comment comment) {

            writeCommentLL.setVisibility(View.GONE);
            showCommentIV.setVisibility(View.GONE);
            nameExpertIV.setVisibility(View.GONE);
            showCommentLL.setVisibility(View.VISIBLE);

            comment.setName(Helper.CapitalizeText(comment.getName()));
            if (comment.getLikes() != null) {
                if (comment.getLikes().equalsIgnoreCase("0") || comment.getLikes().equalsIgnoreCase("1")) {
                    commentLikeCount.setText(String.format("%s like", comment.getLikes()));
                } else {
                    commentLikeCount.setText(String.format("%s likes", comment.getLikes()));
                }
            }

            if (comment.getIs_like() != null) {
                if (comment.getIs_like().equalsIgnoreCase("1")) {
                    likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.like_blue));
                    ((TextView) likeCommentLL.getChildAt(1)).setTextColor(ContextCompat.getColor(activity, R.color.blue));
                } else {
                    likeCommentIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.like));
                    ((TextView) likeCommentLL.getChildAt(1)).setTextColor(ContextCompat.getColor(activity, R.color.black));
                }
            }

            nameTV.setText(HtmlCompat.fromHtml("<b>" + comment.getName() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));

            // TODO to set the tick mark on Expert Profile // start crashing here.
            if (!TextUtils.isEmpty(comment.getIs_expert()) && comment.getIs_expert().equals("1")) {
                nameExpertIV.setVisibility(View.VISIBLE);
            }

            // TODO to set the comment image
            if (!TextUtils.isEmpty(comment.getImage())) {
                Glide.with(activity)
                        .asBitmap()
                        .load(comment.getImage())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_profile_img))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                showCommentIV.setImageBitmap(result);
                            }
                        });
            } else {
                imageIV.setImageResource(R.mipmap.default_profile_img);
            }

            showCommentIV.setOnClickListener((View v) -> {
                ArrayList<PostFile> postFileArrayList = new ArrayList<>();
                PostFile postFile = new PostFile();
                postFile.setLink(comment.getImage());
                postFileArrayList.add(postFile);

                Intent intent = new Intent(activity, ImageViewActivity.class);
                intent.putExtra(Const.POSITION, "0");
                intent.putExtra(Const.IMAGES, postFileArrayList);
                activity.startActivity(intent);
            });

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
                        Helper.makeLinks(commentTV, Const.SHOW_MORE, readMoreClick);
                    }
                };
                Helper.makeLinks(commentTV, Const.SHOW_MORE, readMoreClick);
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
            if (!TextUtils.isEmpty(count) && !count.equals("0")) {
                commentCountRL.setVisibility(View.VISIBLE);
                if (count.equals("1"))
                    commentCountTV.setText(String.format("See %s reply", count));
                else
                    commentCountTV.setText(String.format("See %s replies", count));
            } else commentCountRL.setVisibility(View.GONE);

            if (comment.getParent_id().equals("0")) {
                replyCommentLL.setVisibility(View.VISIBLE);
            } else replyCommentLL.setVisibility(View.GONE);


            if (!TextUtils.isEmpty(comment.getImage())) {
                imageIV1.setVisibility(View.VISIBLE);
                Glide.with(activity)
                        .asBitmap()
                        .load(comment.getImage())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_profile_img))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                imageIV1.setImageBitmap(result);
                            }
                        });
            } else {
                imageIV1.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(comment.getProfile_picture())) {
                imageView.setVisibility(View.VISIBLE);
                imageIVText.setVisibility(View.GONE);
                Glide.with(activity)
                        .asBitmap()
                        .load(comment.getProfile_picture())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_pic))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                imageView.setImageBitmap(result);
                            }
                        });
            } else {
                Drawable dr = Helper.GetDrawable(comment.getName(), activity, comment.getUser_id());
                if (dr != null) {
                    imageView.setVisibility(View.GONE);
                    imageIVText.setVisibility(View.VISIBLE);
                    imageIVText.setImageDrawable(dr);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                    imageIVText.setVisibility(View.GONE);
                    imageView.setImageResource(R.mipmap.default_pic);
                }
            }

            int counter = 0;
            if (comment.getTagged_people() != null && !comment.getTagged_people().isEmpty()) {
                taggedPeopleListLL.setVisibility(View.VISIBLE);
                if (taggedPeopleListFL.getChildCount() > 0) taggedPeopleListFL.removeAllViews();

                if (TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    for (People ppl : comment.getTagged_people()) {
                        if (!ppl.getId().equals(Const.DAMS_MOPUP_USER_ID)) {
                            addViewToTagPeople(ppl);
                            counter++;
                        }
                    }
                    if (counter == 0) taggedPeopleListLL.setVisibility(View.GONE);
                } else {
                    for (People ppl : comment.getTagged_people()) {
                        addViewToTagPeople(ppl);
                    }
                }

            } else taggedPeopleListLL.setVisibility(View.GONE);


            if (comment.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId())
                    || (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getIs_moderate()) && SharedPreference.getInstance().getLoggedInUser().getIs_moderate().equalsIgnoreCase("1"))) {
                moreOptionIV.setVisibility(View.VISIBLE);
            } else moreOptionIV.setVisibility(View.GONE);
            moreOptionIV.setTag(this);
            moreOptionIV.setOnClickListener((View view) -> {
                mainComment = comment;
                holder = (ViewHolder) view.getTag();
                int temp = 0;
                if (comment.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
                    temp++;
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getIs_moderate()) && SharedPreference.getInstance().getLoggedInUser().getIs_moderate().equals("1"))
                    temp += 2;
                showPopMenu(view, temp);
            });

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

        public void deleteComment() {
            View v = Helper.newCustomDialog(activity, false, activity.getString(R.string.delete), activity.getString(R.string.do_you_really_want_to_delete));

            Button btnCancel, btnSubmit;

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
            taggedPeopleListFL.setVisibility(View.GONE);
            taggedPeopleCommentListFL.removeAllViews();
            ((CommonFragment) fragment).writeCommentLL.setVisibility(View.GONE);
//            ((CommonFragment) fragment).isCommentEditing = true;API
            ((PostActivity) activity).isUserEditingComment = true;


            if (!mainComment.getTagged_people().isEmpty()) {
                oldIds = new ArrayList<>();
                newIds = new ArrayList<>();
                taggedPeopleIdsAdded = "";
                taggedPeopleIdsRemoved = "";

                for (People ppl : mainComment.getTagged_people()) {
                    oldIds.add(ppl.getId());
                    addViewToEditTagPeople(ppl);
                }
            }
            moreOptionIV.setVisibility(View.GONE);
            writeCommentLL.setVisibility(View.VISIBLE);
            showCommentLL.setVisibility(View.GONE);
            imageRL.setVisibility(View.GONE);

            addCommentIV.setVisibility(View.VISIBLE);

            writeCommentET.setText(mainComment.getComment());

            // todo to set the old image in that section
            if (!TextUtils.isEmpty(mainComment.getImage())) {
                Glide.with(activity)
                        .asBitmap()
                        .load(mainComment.getImage())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.default_profile_img))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                                imageIV.setImageBitmap(result);
                            }
                        });
                deleteIV.setVisibility(View.VISIBLE);
                commentImageRL.setVisibility(View.VISIBLE);
            } else {
                commentImageRL.setVisibility(View.GONE);
                imageIV.setImageResource(R.mipmap.default_profile_img);
            }

            //todo this is to open gallery popup and to choose image to upload it on comment Section
            addCommentIV.setOnClickListener((View v) -> {
                onChangeImage(mainComment, holder);
                ((CommonFragment) fragment).isCommentEditing = true;
                ((CommonFragment) fragment).takeImageClass.getImagePickerDialog(activity, "Upload Image", activity.getString(R.string.choose_image));
            });

            deleteIV.setOnClickListener((View v) -> {  // to make sure to clear the imageFile
                ((CommonFragment) fragment).isCommentEditing = false;
                ((CommonFragment) fragment).isImageAdded = false;
                mediaFile = new MediaFile();
                commentImages = "";
                ((CommonFragment) fragment).commentImages = "";
                commentImageRL.setVisibility(View.GONE);
            });

            postBtn.setOnClickListener((View v) -> checkValidation());
        }

        public void addViewToEditTagPeople(People response) {
            if (newIds == null) newIds = new ArrayList<>();
            newIds.add(response.getId());

            if (taggedPeopleArrList == null) taggedPeopleArrList = new ArrayList<>();
            taggedPeopleArrList.add(response);

            View v = View.inflate(activity, R.layout.single_textview_people_tag, null);
            TextView tv = v.findViewById(R.id.nameTV);
            ImageView delete = v.findViewById(R.id.deleteIV);
            tv.setText(response.getName());
            v.setTag(response);
            delete.setTag(response);
            delete.setOnClickListener((View view) -> {
                People rep = (People) view.getTag();
                int pos = taggedPeopleArrList.indexOf(rep);
                taggedPeopleArrList.remove(rep);
                taggedPeopleCommentListFL.removeViewAt(pos);
                newIds.remove(pos);
            });

            taggedPeopleCommentListFL.addView(v);
        }

        public void checkValidation() {
            commentText = Helper.GetText(writeCommentET);
            boolean isDataValid = true;

            taggedPeopleIdsRemoved = "";

            if (newIds != null && !newIds.isEmpty()) {
                for (String str : newIds) {
                    if (oldIds != null && !oldIds.contains(str)) {
                        if (TextUtils.isEmpty(taggedPeopleIdsAdded))
                            taggedPeopleIdsAdded = str;
                        else taggedPeopleIdsAdded = taggedPeopleIdsAdded + "," + str;
                    } else {
                        if (TextUtils.isEmpty(taggedPeopleIdsAdded))
                            taggedPeopleIdsAdded = str;
                        else taggedPeopleIdsAdded = taggedPeopleIdsAdded + "," + str;
                    }
                }
            }

            if (oldIds != null && !oldIds.isEmpty()) {
                for (String str : oldIds) {
                    if (newIds != null && !newIds.contains(str)) {
                        if (TextUtils.isEmpty(taggedPeopleIdsRemoved))
                            taggedPeopleIdsRemoved = str;
                        else taggedPeopleIdsRemoved = taggedPeopleIdsRemoved + "," + str;
                    } else {
                        if (TextUtils.isEmpty(taggedPeopleIdsAdded))
                            taggedPeopleIdsAdded = str;
                        else taggedPeopleIdsAdded = taggedPeopleIdsAdded + "," + str;
                    }
                }
            }

            if (TextUtils.isEmpty(commentText))
                isDataValid = Helper.DataNotValid(writeCommentET);

            if (isDataValid) {
                mainComment.setComment(commentText);
                if (((CommonFragment) fragment).isImageAdded) {
                    ArrayList<MediaFile> mediaFiles = new ArrayList<>();
                    mediaFiles.add(mediaFile);
                    s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_COMMENT, activity, this, null);
                    s3ImageUploading.execute(mediaFiles);
                } else
                    networkCallForEditComment();//networkCall.NetworkAPICall(API.API_EDIT_COMMENT, true);
            }
        }

        @Override
        public void onS3UploadData(ArrayList<MediaFile> images) {
            if (!images.isEmpty()) {
                for (MediaFile media : images) {
                    commentImages = media.getFile();
                    Log.e("Tag", commentImages);
                    networkCallForEditComment();//networkCall.NetworkAPICall(API.API_EDIT_COMMENT, true);
                }
            }
        }
    }
}
