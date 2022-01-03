package com.emedicoz.app.video.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.Comment;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.video.adapter.VideoCommentAdapter;
import com.emedicoz.app.video.fragment.VideoFragmentViewPager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";
    int firstVisibleItem;
    int visibleItemCount;
    int totalItemCount;
    public LinearLayout commentLayout;
    RecyclerView commentRV;
    String commentText;
    String videoId = "";
    String type = "";
    String parentId = "";
    String lastCommentId = "";
    VideoCommentAdapter commentAdapter;
    ImageView imageIV;
    TextView nameTV;
    TextView nameExpertIV;
    TextView dateTV;
    TextView commentTV;
    TextView likeComment;
    LinearLayoutManager linearLayoutManager;
    Progress mprogress;
    private ArrayList<Comment> commentList;
    private Comment comment;
    private Video data;
    private boolean isScrolling = false;
    private EditText write_comment;
    private ImageButton submitComment;
    private TextView noComment;

    public CommentActivity() {
        commentList = new ArrayList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        getIntentData();
        initViews();
        networkCallForGetVideoComment(false);
    }

    private void getIntentData() {
        type = getIntent().getStringExtra(Constants.Extras.TYPE);
        data = (Video) getIntent().getSerializableExtra(Const.DATA);
        videoId = getIntent().getStringExtra(Const.VIDEO_ID);
        parentId = getIntent().getStringExtra(Const.PARENT_ID);
        comment = (Comment) getIntent().getSerializableExtra(Const.COMMENT);
    }

    private void initViews() {
        mprogress = new Progress(CommentActivity.this);
        mprogress.setCancelable(false);
        imageIV = findViewById(R.id.imageIV);
        nameTV = findViewById(R.id.nameTV);
        nameExpertIV = findViewById(R.id.nameExpertIV);
        dateTV = findViewById(R.id.dateTV);
        commentTV = findViewById(R.id.commentTV);
        likeComment = findViewById(R.id.commentLikeCount);
        write_comment = findViewById(R.id.writecommentET);
        submitComment = findViewById(R.id.postcommentIBtn);
        commentLayout = findViewById(R.id.comment_layout);
        noComment = findViewById(R.id.no_comment);

        commentRV = findViewById(R.id.commentRV);
        linearLayoutManager = new LinearLayoutManager(this);
        commentRV.setLayoutManager(linearLayoutManager);

        submitComment.setOnClickListener((View v) -> {
            if (Helper.isConnected(getApplicationContext())) {
                checkValidation();
            } else {
                Toast.makeText(getApplicationContext(), R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            }
        });

        setData();
        setPagination();
    }

    public void checkValidation() {
        commentText = Helper.GetText(write_comment);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(commentText))
            isDataValid = Helper.DataNotValid(write_comment);

        if (isDataValid) {
            networkCallForAddVideoComment();//networkCall.NetworkAPICall(API.API_ADD_VIDEO_COMMENT, true);

        }
    }

    private void setPagination() {
        commentRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                    if (isScrolling && (visibleItemCount + firstVisibleItem == totalItemCount)) {
                        isScrolling = false;
                        networkCallForGetVideoComment(false);
                    }
                }
            }
        });
    }

    private void setData() {
        nameTV.setText(comment.getName());
        commentTV.setText(comment.getComment());
        likeComment.setText(comment.getLikes());
        Glide.with(this)
                .load(comment.getProfile_picture())
                .apply(new RequestOptions().placeholder(R.mipmap.default_pic).error(R.mipmap.default_pic))
                .into(imageIV);

        String date = (String) DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getTime()));
        String[] dates = date.split("\\s+");
        if (dates.length > 1) {
            if (dates[1].equalsIgnoreCase(getString(R.string.minutes_string))) {
                dates[1] = dates[1].substring(0, 1);
            } else {
                dates[1] = dates[1].substring(0, 1);
            }
            if (DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getTime())).equals(getString(R.string.string_minutes_ago))) {
                dateTV.setText(getString(R.string.just_now_string));
            } else {
                String newDate = dates[0] + " " + dates[1];
                dateTV.setText(newDate);
            }
        } else {
            dateTV.setText(dates[0]);
        }
    }

    public void networkCallForGetVideoComment(final boolean pagination) {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mprogress != null)
            mprogress.show();
        Call<JsonObject> response;
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Log.e(TAG, "video_id - " + data.getId() + ", parent_id - " + parentId + ", last_comment_id - " + lastCommentId);
        if (pagination) {
            response = apiInterface.getSingleVideoComment(SharedPreference.getInstance().getLoggedInUser().getId(), data.getId(), lastCommentId);
        } else {
            response = apiInterface.getSingleVideoCommmentList(SharedPreference.getInstance().getLoggedInUser().getId(), data.getId(), parentId, lastCommentId);
        }
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mprogress != null)
                    mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(TAG, " networkCallForGetVideoComment onResponse: " + jsonResponse);
                        if (pagination) {
                            Log.e(TAG, " getSingleVideoComment onResponse: " + jsonResponse);
                        } else {
                            Log.e(TAG, " getSingleVideoCommmentList onResponse: " + jsonResponse);
                        }


                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            JSONArray jsonArray = jsonResponse.optJSONArray(Const.DATA);
                            if (jsonArray != null && jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Comment comment = new Gson().fromJson(jsonArray.optJSONObject(i).toString(), Comment.class);
                                    commentList.add(comment);
                                }
                                lastCommentId = commentList.get(commentList.size() - 1).getId();
                                Log.e(TAG, "onResponse: size - " + commentList.size());
                                Log.e(TAG, "onResponse: last_comment_id - " + lastCommentId);
                                initCommentAdapter();
                            }
                        } else {
                            Toast.makeText(CommentActivity.this, getString(R.string.no_comment_found), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(CommentActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mprogress != null)
                    mprogress.dismiss();
                Helper.showExceptionMsg(CommentActivity.this, t);
            }
        });
    }

    private void initCommentAdapter() {
        commentAdapter = new VideoCommentAdapter(commentList, CommentActivity.this, data);
        commentRV.setAdapter(commentAdapter);
    }

    private void networkCallForAddVideoComment() {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mprogress.show();
        Call<JsonObject> response;
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);

        response = apiInterface.addVideoCommentList(SharedPreference.getInstance().getLoggedInUser().getId(),
                videoId, parentId,
                StringEscapeUtils.escapeJava(write_comment.getText().toString()));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(TAG, " networkCallForAddVideoComment onResponse: " + jsonResponse);
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            SharedPreference.getInstance().putBoolean(Const.IS_FROM_COMMENT_ACTIVITY, true);
                            write_comment.setText("");
                            lastCommentId = "";
                            commentList.clear();
                            networkCallForGetVideoComment(false);
                            commentUpdate();

                            VideoFragmentViewPager.IS_COMMENT_UPDATE = true;
                        } else {
                            RetrofitResponse.getApiData(CommentActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showExceptionMsg(CommentActivity.this, t);

            }
        });
    }

    public void commentUpdate() {
        SharedPreference sharedPreference = SharedPreference.getInstance();
        sharedPreference.setCommentUpdate(data);
    }

    public void callCommentView() {
        if (!data.getComments().equals("0")) {
            networkCallForGetVideoComment(false);
        } else {
            noComment.setVisibility(View.VISIBLE);
            commentRV.setVisibility(View.GONE);
        }
    }

}
