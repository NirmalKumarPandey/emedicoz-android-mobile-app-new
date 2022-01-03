package com.emedicoz.app.feeds.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.feeds.adapter.FeedRVAdapter;
import com.emedicoz.app.feeds.adapter.PeopleFollowRVAdapter;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.FollowResponse;
import com.emedicoz.app.response.PostResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.ProfileApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    public static boolean IS_PROFILE_UPDATED = false;
    public String last_post_id, last_followers_id, last_following_id;
    public FeedRVAdapter feedRVAdapter;
    public int previousTotalItemCount;
    public String id, type, apiType;
    public TextView followingTV;
    public PeopleFollowRVAdapter peopleFollowRVAdapter;
    String errorMessage;
    ArrayList<PostResponse> feedArrayList;
    ArrayList<FollowResponse> followingArrayList;
    ArrayList<FollowResponse> followersArrayList;
    TextView profileName;
    TextView postTV;
    TextView followersTV;
    TextView errorTV;
    TextView interestedcoursesTV;
    Button followBtn, expertBtn;
    ImageView profileImageIV;
    ImageView profileImageIVText;
    RecyclerView profileRV;
    LinearLayoutManager LM;
    Progress mprogress;
    LinearLayout allMainLL;
    LinearLayout horizontalLL;
    LinearLayout errorLayout;
    Button tryAgainBtn;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    CollapsingToolbarLayout collapsingToolbar;
    User user;
    int AdapterType = 3;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private RelativeLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    View.OnClickListener onFollowClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            followBtn.setEnabled(false);
            if (user.is_following()) {
                networkCallForUnFollow();//networkCall.NetworkAPICall(API.API_UNFOLLOW, true);
            } else {
                networkCallForFollow();//networkCall.NetworkAPICall(API.API_FOLLOW, true);
            }
        }
    };
    View.OnClickListener onExpertClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            expertBtn.setEnabled(false);
            if (!TextUtils.isEmpty(user.getIs_expert()) && user.getIs_expert().equals("1")) {
                networkCallForRemoveExpert();//networkCall.NetworkAPICall(API.API_REMOVE_EXPERT, true);
            } else if (!TextUtils.isEmpty(user.getIs_expert()) && user.getIs_expert().equals("0")) {
                networkCallForMakeExpert();//networkCall.NetworkAPICall(API.API_MAKE_AN_EXPERT, true);
            }
        }
    };
    private Toolbar mToolbar;
    private boolean loading = true;
    private int visibleThreshold = 5;

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mprogress = new Progress(ProfileActivity.this);
        mprogress.setCancelable(false);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_profile);
        feedArrayList = new ArrayList<PostResponse>();
        followingArrayList = new ArrayList<FollowResponse>();
        followersArrayList = new ArrayList<FollowResponse>();

        collapsingToolbar = findViewById(R.id.collapse_toolbar);
        allMainLL = findViewById(R.id.allMainLL);
        horizontalLL = findViewById(R.id.horizontalLL);
        errorLayout = findViewById(R.id.errorLL);
        tryAgainBtn = findViewById(R.id.tryAgainBtn);
        collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.blue));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));

        followBtn = findViewById(R.id.followBtn);
        expertBtn = findViewById(R.id.expertBtn);
        profileRV = findViewById(R.id.ProfileRV);

        LM = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        profileRV.setLayoutManager(LM);

        profileName = findViewById(R.id.profileName);
        followingTV = findViewById(R.id.followingTV);
        followersTV = findViewById(R.id.followersTV);
        postTV = findViewById(R.id.postTV);
        errorTV = findViewById(R.id.errorTV);
        interestedcoursesTV = findViewById(R.id.interestedcoursesTV);

        followingTV.setOnClickListener(this);
        followersTV.setOnClickListener(this);
        postTV.setOnClickListener(this);

        profileImageIV = findViewById(R.id.profileImage);
        profileImageIVText = findViewById(R.id.profileImageText);

        mToolbar = findViewById(R.id.main_toolbar);
        mTitle = findViewById(R.id.main_textview_title);

        mTitleContainer = findViewById(R.id.collapse_toolbar_RL);
        mAppBarLayout = findViewById(R.id.main_appbar);
        mAppBarLayout.addOnOffsetChangedListener(this);

        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryApiButton();
            }
        });
        if (getIntent() != null) {
            id = getIntent().getStringExtra(Constants.Extras.ID);
            type = getIntent().getStringExtra(Constants.Extras.TYPE);
            //      Log.d("UR_ID",id);
            if (!TextUtils.isEmpty(id))
                networkCallForGetUser();//networkCall.NetworkAPICall(API.API_GET_USER, true);
        }
        setSupportActionBar(mToolbar);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(type) && type.equals(Const.LAST)) {
                    Helper.GoToNextActivity(ProfileActivity.this);
                    ProfileActivity.this.finish();
                } else {
                    finish();
                }
            }
        });

        profileRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = LM.getChildCount();
                totalItemCount = LM.getItemCount();
                firstVisibleItem = LM.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotalItemCount) {
                        loading = false;
                        previousTotalItemCount = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    Log.i("Yaeye!", "end called");
                    // Do something
                    if (AdapterType == 3)
                        last_post_id = feedArrayList.get(totalItemCount - 1).getId();
                    else if (AdapterType == 2)
                        last_following_id = followingArrayList.get(totalItemCount - 1).getId();
                    else if (AdapterType == 1)
                        last_followers_id = followersArrayList.get(totalItemCount - 1).getId();

                    RefreshDataList(false, 1); // for pagination
                    loading = true;
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(type) && type.equals(Const.LAST)) {
            Helper.GoToNextActivity(ProfileActivity.this);
            ProfileActivity.this.finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FeedsActivity.isCommentRefreshed) {
            if (feedRVAdapter != null)
                feedRVAdapter.itemChangeDatePostId(SharedPreference.getInstance().getPost(), 0);
            FeedsActivity.isCommentRefreshed = false;
        }
        if (FeedsActivity.isNewPostAdded) {
            RefreshDataList(true, 0); // from onResume if new post is added
            last_post_id = "";
            FeedsActivity.isNewPostAdded = false;
        }
        if (FeedsActivity.isPostDeleted) {
            if (feedRVAdapter != null)
                feedRVAdapter.itemChangeDatePostId(SharedPreference.getInstance().getPost(), 1);
            last_post_id = "";
            FeedsActivity.isPostDeleted = false;
        }
        if (FeedsActivity.isPostUpdated) {
            if (feedRVAdapter != null)
                feedRVAdapter.itemChangeDatePostId(SharedPreference.getInstance().getPost(), 0);
            last_post_id = "";
            FeedsActivity.isPostUpdated = false;
        }
        if (IS_PROFILE_UPDATED) {
            user = SharedPreference.getInstance().getLoggedInUser();
            initView();
            IS_PROFILE_UPDATED = false;
        }
    }

    public void RefreshDataList(boolean show, int type) {
        if (AdapterType == 1) {
            if (type == 1 || (!user.getFollowers_count().equals("0") && followersArrayList.isEmpty()))
                networkCallForFollowerList();//networkCall.NetworkAPICall(API.API_FOLLOWERS_LIST, show);
            else initFollowersAdapter();
        } else if (AdapterType == 2) {
            if (type == 1 || (!user.getFollowing_count().equals("0") && followingArrayList.isEmpty()))
                networkCallForFollowingList();//networkCall.NetworkAPICall(API.API_FOLLOWING_LIST, show);
            else initfollowingAdapter();
        } else if (AdapterType == 3) {
            if (type == 1 || (user != null && !user.getPost_count().equals("0") && feedArrayList.isEmpty()))
                networkCallForFeeds();//networkCall.NetworkAPICall(API.API_GET_FEEDS_FOR_USER,show);
            else initFeedAdapter();
        }
    }

    public void initView() {
        if (horizontalLL.getVisibility() == View.GONE) {
            horizontalLL.setVisibility(View.VISIBLE);
        }
        if (AdapterType == 3)
            RefreshDataList(true, 0);//from InitView for feeds
        user.setName(Helper.CapitalizeText(user.getName()));

        if (!TextUtils.isEmpty(user.getProfile_picture())) {
            profileImageIV.setVisibility(View.VISIBLE);
            profileImageIVText.setVisibility(View.GONE);
 /*           Ion.with(ProfileActivity.this).load(user.getProfile_picture())
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (result != null)
                                profileImageIV.setImageBitmap(result);
                            else
                                profileImageIV.setImageResource(R.mipmap.default_pic);
                        }
                    });*/
        } else {
            Drawable dr = Helper.GetDrawable(user.getName(), this, user.getId());
            if (dr != null) {
                profileImageIV.setVisibility(View.GONE);
                profileImageIVText.setVisibility(View.VISIBLE);
                profileImageIVText.setImageDrawable(dr);
            } else {
                profileImageIV.setVisibility(View.VISIBLE);
                profileImageIVText.setVisibility(View.GONE);
                profileImageIV.setImageResource(R.mipmap.default_pic);
            }
        }
        collapsingToolbar.setTitle(user.getName());
        profileName.setText(user.getName());
//        if (user.getUser_registration_info() != null && !TextUtils.isEmpty(user.getUser_registration_info().getInterested_course_text())) {
//            interestedcoursesTV.setText(Html.fromHtml("<b><i>Courses Interested In: </b></i>" + user.getUser_registration_info().getInterested_course_text()));
//            interestedcoursesTV.setVisibility(View.VISIBLE);
//        } else interestedcoursesTV.setVisibility(View.GONE);

        if (user.getId().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
            followBtn.setVisibility(View.GONE);
        else {
            followBtn.setVisibility(View.VISIBLE);
            if (user.is_following()) {
                followBtn = changeBackgroundColor(followBtn, 1, 1);
            } else {
                followBtn = changeBackgroundColor(followBtn, 0, 1);
            }
        }

        //layout for making user as expert only by Moderator
        if (user.getId().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
            expertBtn.setVisibility(View.GONE);
        else if (!TextUtils.isEmpty(user.getIs_expert()) &&
                !TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getIs_moderate()) &&
                SharedPreference.getInstance().getLoggedInUser().getIs_moderate().equals("1")) {
            expertBtn.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(user.getIs_expert()) && user.getIs_expert().equals("1")) {
                expertBtn = changeBackgroundColor(expertBtn, 1, 2);
            } else {
                expertBtn = changeBackgroundColor(expertBtn, 0, 2);
            }
        } else expertBtn.setVisibility(View.GONE);


        expertBtn.setOnClickListener(onExpertClick);
        followBtn.setOnClickListener(onFollowClick);


        if (Integer.parseInt(user.getFollowers_count()) == 1)
            followersTV.setText(String.format("%s\nFollower", user.getFollowers_count()));
        else
            followersTV.setText(String.format("%s\nFollowers", user.getFollowers_count()));

        followingTV.setText(String.format("%s\nFollowing", user.getFollowing_count()));
        postTV.setText(String.format("%s\nPosts", user.getPost_count()));
    }

    public Button changeBackgroundColor(Button v, int type, int button) {
        if (type == 1) {
            if (button == 1)
                v.setText(R.string.following);
            else if (button == 2)
                v.setText("Remove as Expert");
            v.setTextColor(ContextCompat.getColor(this, R.color.white));
            v.setBackgroundResource(R.drawable.reg_round_blue_bg);
        } else {
            v.setBackgroundResource(R.drawable.reg_round_white_bg);
            if (button == 1)
                v.setText(R.string.follow);
            else if (button == 2)
                v.setText("Make an Expert");
            v.setTextColor(ContextCompat.getColor(this, R.color.blue));
        }
        return v;
    }

   /* public Button changeBackgroundColor(Button v, int type) {
        v.invalidate();
        if (type == 1) {
            v.setText(R.string.following);
            v.setBackgroundResource(R.drawable.reg_round_blue_bg);
            v.setTextColor(activity.getResources().getColor(R.color.white));
        } else {
            v.setBackgroundResource(R.drawable.reg_round_white_bg);
            v.setText(R.string.follow);
            v.setTextColor(activity.getResources().getColor(R.color.blue));
        }
        return v;
    }*/

    /*    public Button changeBackgroundColor(Button v, int type, int button) {
        v.setBackgroundResource(R.drawable.reg_round_blue_bg);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        if (type == 1) {
            if (button == 1)
                v.setText(R.string.following);
            else if (button == 2)
                v.setText("Remove as Expert");
            v.setTextColor(getResources().getColor(R.color.white));
            drawable.setStroke(2, getResources().getColor(R.color.white));
            drawable.setColor(getResources().getColor(R.color.blue));
        } else {
            drawable.setStroke(2, getResources().getColor(R.color.blue));
            drawable.setColor(getResources().getColor(R.color.white));
            if (button == 1)
                v.setText(R.string.follow);
            else if (button == 2)
                v.setText("Make an Expert");
            v.setTextColor(getResources().getColor(R.color.blue));
        }

        return v;
    }*/

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (id.equals(SharedPreference.getInstance().getLoggedInUser().getId()))
            getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editIM:
                Helper.GoToEditProfileActivity(ProfileActivity.this, Const.REGISTRATION, Const.PROFILE);
                break;
        }
        return true;
    }

    private void networkCallForFollow() {
        mprogress.show();
        ProfileApiInterface apiInterface = ApiClient.createService(ProfileApiInterface.class);
        Call<JsonObject> response = apiInterface.follow(user.getId(), SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            initViewFollowUnFollow(1);
                        } else {
                            if (TextUtils.isEmpty(last_post_id)) {
                                profileRV.setVisibility(View.GONE);
                                errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_FOLLOW);
                            }
                            checkEnable(API.API_FOLLOW);
                            RetrofitResponse.getApiData(ProfileActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(ProfileActivity.this, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                replaceErrorLayout(API.API_FOLLOW, 1, 1);
            }
        });
    }

    private void networkCallForUnFollow() {
        mprogress.show();
        ProfileApiInterface apiInterface = ApiClient.createService(ProfileApiInterface.class);
        Call<JsonObject> response = apiInterface.unFollow(user.getId(), SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            initViewFollowUnFollow(0);
                        } else {
                            if (TextUtils.isEmpty(last_post_id)) {
                                profileRV.setVisibility(View.GONE);
                                errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_UNFOLLOW);
                            }
                            checkEnable(API.API_UNFOLLOW);
                            RetrofitResponse.getApiData(ProfileActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                replaceErrorLayout(API.API_UNFOLLOW, 1, 1);
            }
        });
    }

    private void networkCallForMakeExpert() {
        mprogress.show();
        ProfileApiInterface apiInterface = ApiClient.createService(ProfileApiInterface.class);
        Call<JsonObject> response = apiInterface.makeAnExpert(SharedPreference.getInstance().getLoggedInUser().getId(),
                user.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            initViewExpert(1);
                        } else {
                            if (TextUtils.isEmpty(last_post_id)) {
                                profileRV.setVisibility(View.GONE);
                                errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_MAKE_AN_EXPERT);
                            }
                            checkEnable(API.API_MAKE_AN_EXPERT);
                            RetrofitResponse.getApiData(ProfileActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                replaceErrorLayout(API.API_MAKE_AN_EXPERT, 1, 1);
            }
        });
    }

    private void networkCallForRemoveExpert() {
        mprogress.show();
        ProfileApiInterface apiInterface = ApiClient.createService(ProfileApiInterface.class);
        Call<JsonObject> response = apiInterface.removeAnExpert(SharedPreference.getInstance().getLoggedInUser().getId(),
                user.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            initViewExpert(0);
                        } else {
                            if (TextUtils.isEmpty(last_post_id)) {
                                profileRV.setVisibility(View.GONE);
                                errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_REMOVE_EXPERT);
                            }
                            checkEnable(API.API_REMOVE_EXPERT);
                            RetrofitResponse.getApiData(ProfileActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                replaceErrorLayout(API.API_REMOVE_EXPERT, 1, 1);
            }
        });
    }

    private void networkCallForFeeds() {
        mprogress.show();
        ProfileApiInterface apiInterface = ApiClient.createService(ProfileApiInterface.class);
        Call<JsonObject> response = apiInterface.feedsForUser(SharedPreference.getInstance().getLoggedInUser().getId(),
                id, last_post_id);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    Gson gson = new Gson();
                    JSONArray dataarray;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            dataarray = GenericUtils.getJsonArray(jsonResponse);
                            if (TextUtils.isEmpty(last_post_id)) {
                                feedArrayList = new ArrayList<>();
                            }
                            if (dataarray.length() > 0) {
                                int i = 0;
                                while (i < dataarray.length()) {
                                    JSONObject singledatarow = dataarray.optJSONObject(i);
                                    PostResponse response1 = gson.fromJson(singledatarow.toString(), PostResponse.class);
                                    feedArrayList.add(response1);
                                    i++;
                                }
                            }
                            initFeedAdapter();
                        } else {
                            if (TextUtils.isEmpty(last_post_id)) {
                                profileRV.setVisibility(View.GONE);
                                errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_FEEDS_FOR_USER);
                            }
                            RetrofitResponse.getApiData(ProfileActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                replaceErrorLayout(API.API_GET_FEEDS_FOR_USER, 1, 1);
            }
        });
    }

    private void networkCallForFollowingList() {
        mprogress.show();
        ProfileApiInterface apiInterface = ApiClient.createService(ProfileApiInterface.class);
        Call<JsonObject> response = apiInterface.getFollowingList(SharedPreference.getInstance().getLoggedInUser().getId(),
                id, last_following_id);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    Gson gson = new Gson();
                    JSONArray dataarray;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            dataarray = GenericUtils.getJsonArray(jsonResponse);
                            if (TextUtils.isEmpty(last_following_id)) {
                                followingArrayList = new ArrayList<>();
                            }
                            if (dataarray.length() > 0) {
                                int i = 0;
                                while (i < dataarray.length()) {
                                    JSONObject singledatarow = dataarray.optJSONObject(i);
                                    FollowResponse response1 = gson.fromJson(singledatarow.toString(), FollowResponse.class);
                                    followingArrayList.add(response1);
                                    i++;
                                }
                            }
                            initfollowingAdapter();
                        } else {
                            if (TextUtils.isEmpty(last_post_id)) {
                                profileRV.setVisibility(View.GONE);
                                errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_FOLLOWING_LIST);
                            }
                            RetrofitResponse.getApiData(ProfileActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                replaceErrorLayout(API.API_FOLLOWING_LIST, 1, 1);
            }
        });
    }

    private void networkCallForFollowerList() {
        mprogress.show();
        ProfileApiInterface apiInterface = ApiClient.createService(ProfileApiInterface.class);
        Call<JsonObject> response = apiInterface.getFollowerList(SharedPreference.getInstance().getLoggedInUser().getId(), id,
                last_followers_id);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();
                    JSONArray dataarray;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            dataarray = GenericUtils.getJsonArray(jsonResponse);
                            if (TextUtils.isEmpty(last_followers_id)) {
                                followersArrayList = new ArrayList<>();
                            }
                            if (dataarray.length() > 0) {
                                int i = 0;
                                while (i < dataarray.length()) {
                                    JSONObject singledatarow = dataarray.optJSONObject(i);
                                    FollowResponse response1 = gson.fromJson(singledatarow.toString(), FollowResponse.class);
                                    followersArrayList.add(response1);
                                    i++;
                                }
                            }
                            initFollowersAdapter();
                        } else {
                            if (TextUtils.isEmpty(last_post_id)) {
                                profileRV.setVisibility(View.GONE);
                                errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_FOLLOWERS_LIST);
                            }
                            RetrofitResponse.getApiData(ProfileActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                replaceErrorLayout(API.API_FOLLOWERS_LIST, 1, 1);
            }
        });
    }

    private void networkCallForGetUser() {
        mprogress.show();
        ProfileApiInterface apiInterface = ApiClient.createService(ProfileApiInterface.class);
        Call<JsonObject> response = apiInterface.getUser("data_model/user/Registration/get_active_user/"
                + id + Const.IS_WATCHER + SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mprogress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            user = gson.fromJson(data.toString(), User.class);
                            SharedPreference.getInstance().setLoggedInUser(user);

                            initView();
                        } else {
                            if (TextUtils.isEmpty(last_post_id)) {
                                profileRV.setVisibility(View.GONE);
                                errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_USER);
                            }
                            RetrofitResponse.getApiData(ProfileActivity.this, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                replaceErrorLayout(API.API_GET_USER, 1, 1);

            }
        });
    }

    private void checkEnable(String apiType) {
        if (apiType.equals(API.API_FOLLOW) || apiType.equals(API.API_UNFOLLOW))
            followBtn.setEnabled(true);
        if (apiType.equals(API.API_MAKE_AN_EXPERT) || apiType.equals(API.API_REMOVE_EXPERT))
            expertBtn.setEnabled(true);
    }

    private void errorCallBack(String jsonString, String apiType) {
        Log.e("profile json", "Error " + jsonString);

        if (jsonString.equalsIgnoreCase(getResources().getString(R.string.internet_error_message)))
            replaceErrorLayout(apiType, 1, 1);

        errorMessage = jsonString;
        switch (apiType) {
            case API.API_GET_FEEDS_FOR_USER:
                initFeedAdapter();
                break;
            case API.API_FOLLOWERS_LIST:
                initFollowersAdapter();
                break;
            case API.API_FOLLOWING_LIST:
                initfollowingAdapter();
                break;
            default:
//                Toast.makeText(this, jsonString, Toast.LENGTH_SHORT).show();
                if (jsonString.contains(getString(R.string.something_went_wrong_string)))
                    replaceErrorLayout(apiType, 1, 2);
                break;
        }
    }

    protected void initFeedAdapter() {
        if (TextUtils.isEmpty(last_post_id)) {
            profileRV.invalidate();
            if (!feedArrayList.isEmpty()) {
                errorTV.setVisibility(View.GONE);
                profileRV.setVisibility(View.VISIBLE);
                feedRVAdapter = new FeedRVAdapter(ProfileActivity.this, feedArrayList, ProfileActivity.this);
                profileRV.setAdapter(feedRVAdapter);
            } else {
                errorTV.setText(R.string.no_feeds_found);
                errorTV.setVisibility(View.VISIBLE);
                profileRV.setVisibility(View.GONE);
            }
        } else {
            profileRV.setVisibility(View.VISIBLE);
            feedRVAdapter.notifyDataSetChanged();
        }
    }

    protected void initfollowingAdapter() {
        if (TextUtils.isEmpty(last_following_id)) {
            profileRV.invalidate();
            if (!followingArrayList.isEmpty()) {
                profileRV.setVisibility(View.VISIBLE);
                errorTV.setVisibility(View.GONE);

                //this is to refine the dams momup from non-dams user
                if (TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    for (int i = 0; i < followingArrayList.size(); i++) {
                        if (followingArrayList.get(i).getUser_id().equals(Const.DAMS_MOPUP_USER_ID)) {
                            followingArrayList.remove(i);
                            i = followingArrayList.size();
                        }
                    }
                }

                peopleFollowRVAdapter = new PeopleFollowRVAdapter(followingArrayList, ProfileActivity.this);
                profileRV.getRecycledViewPool().setMaxRecycledViews(0, 0);
                profileRV.setAdapter(peopleFollowRVAdapter);
            } else {
                errorTV.setText(R.string.no_following_found);
                errorTV.setVisibility(View.VISIBLE);
                profileRV.setVisibility(View.GONE);
            }
        } else {
            profileRV.setVisibility(View.VISIBLE);
            peopleFollowRVAdapter.notifyDataSetChanged();
        }
    }

    protected void initFollowersAdapter() {
        if (TextUtils.isEmpty(last_followers_id)) {
            profileRV.invalidate();
            if (!followersArrayList.isEmpty()) {
                errorTV.setVisibility(View.GONE);
                profileRV.setVisibility(View.VISIBLE);

                //this is to refine the dams momup from non-dams user
                if (TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    for (int i = 0; i < followersArrayList.size(); i++) {
                        if (followersArrayList.get(i).getUser_id().equals(Const.DAMS_MOPUP_USER_ID)) {
                            followersArrayList.remove(i);
                            i = followersArrayList.size();
                        }
                    }
                }

                peopleFollowRVAdapter = new PeopleFollowRVAdapter(followersArrayList, ProfileActivity.this);
                profileRV.setAdapter(peopleFollowRVAdapter);
            } else {
                errorTV.setText(R.string.no_followers_found);
                errorTV.setVisibility(View.VISIBLE);
                profileRV.setVisibility(View.GONE);
            }
        } else {
            profileRV.setVisibility(View.VISIBLE);
            peopleFollowRVAdapter.notifyDataSetChanged();
        }
    }

    public void initViewFollowUnFollow(int type) {
        followBtn.setEnabled(true);
        if (type == 1) {
            followBtn = changeBackgroundColor(followBtn, type, 1);
            user.setIs_following(true);
            user.setFollowers_count(setFollowers(type));
            followersTV.setText(String.format("%s\nFollowers", user.getFollowers_count()));
        } else {
            followBtn = changeBackgroundColor(followBtn, type, 1);
            user.setIs_following(false);
            user.setFollowers_count(setFollowers(type));
            followersTV.setText(String.format("%s\nFollowers", user.getFollowers_count()));
        }
    }

    public void initViewExpert(int type) {
        expertBtn.setEnabled(true);
        if (type == 1) {
            expertBtn = changeBackgroundColor(expertBtn, type, 2);
            user.setIs_expert("1");
//            user.setFollowers_count(setfollowers(type));
//            followersTV.setText(user.getFollowers_count() + "\nFollowers");
        } else {
            expertBtn = changeBackgroundColor(expertBtn, type, 2);
            user.setIs_expert("0");
//            user.setFollowers_count(setfollowers(type));
//            followersTV.setText(user.getFollowers_count() + "\nFollowers");
        }
    }

    public String setFollowers(int type) {
        String followers = user.getFollowers_count();
        if (type == 1)
            return String.valueOf(Integer.parseInt(followers) + 1);
        else
            return String.valueOf(Integer.parseInt(followers) - 1);
    }

    // apiType:  to save the API name on which the error occurred
    // isError:  Whether there is an error or success response
    // layoutType: Whether there is an internet issue or api Error like "Something went wrong"
    public void replaceErrorLayout(String apiType, int status, int layoutType) {
        this.apiType = apiType;
        if (layoutType == 1) {
            ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.no_internet);
            ((TextView) findViewById(R.id.errorMessageTV)).setText(getResources().getString(R.string.internet_error_message));
        } else if (layoutType == 2) {
            ((ImageView) findViewById(R.id.errorImageIV)).setImageResource(R.mipmap.something_went_wrong);
            ((TextView) findViewById(R.id.errorMessageTV)).setText(getResources().getString(R.string.exception_api_error_message));
        }

        mAppBarLayout.setVisibility((status == 1 ? View.GONE : View.VISIBLE));
        allMainLL.setVisibility((status == 1 ? View.GONE : View.VISIBLE));
        errorLayout.setVisibility((status == 1 ? View.VISIBLE : View.GONE));
    }

    public void retryApiButton() {
        switch (apiType) {
            case API.API_GET_USER:
                networkCallForGetUser();
                break;
            case API.API_GET_FEEDS_FOR_USER:
                networkCallForFeeds();
                break;
            case API.API_FOLLOW:
                networkCallForFollow();
                break;
            case API.API_UNFOLLOW:
                networkCallForUnFollow();
                break;
            case API.API_MAKE_AN_EXPERT:
                networkCallForMakeExpert();
                break;
            case API.API_REMOVE_EXPERT:
                networkCallForRemoveExpert();
                break;
            case API.API_FOLLOWING_LIST:
                networkCallForFollowingList();
                break;
            case API.API_FOLLOWERS_LIST:
                networkCallForFollowerList();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.followersTV:
                interestedcoursesTV.setVisibility(View.GONE);
                if (AdapterType != 1) {
                    postTV.setTextColor(ContextCompat.getColor(this, R.color.black));
                    followersTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                    followingTV.setTextColor(ContextCompat.getColor(this, R.color.black));
                    AdapterType = 1;
                    last_following_id = "";
                    last_post_id = "";
                    last_followers_id = "";
                    Log.e("onClick", "AdapterType " + AdapterType);
                    RefreshDataList(true, 0); //AdapterType = 3; followersTV onClick
                }
                AdapterType = 1;
                break;
            case R.id.followingTV:
                interestedcoursesTV.setVisibility(View.GONE);
                if (AdapterType != 2) {
                    postTV.setTextColor(ContextCompat.getColor(this, R.color.black));
                    followersTV.setTextColor(ContextCompat.getColor(this, R.color.black));
                    followingTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                    AdapterType = 2;
                    last_following_id = "";
                    last_post_id = "";
                    last_followers_id = "";
                    Log.e("onClick", "AdapterType " + AdapterType);
                    RefreshDataList(true, 0); //AdapterType = 2; followingTV onClick
                }

                AdapterType = 2;
                break;
            case R.id.postTV:
//                interestedcoursesTV.setVisibility(View.VISIBLE);
                last_post_id = "";
                if (AdapterType != 3) {
                    postTV.setTextColor(ContextCompat.getColor(this, R.color.blue));
                    followersTV.setTextColor(ContextCompat.getColor(this, R.color.black));
                    followingTV.setTextColor(ContextCompat.getColor(this, R.color.black));
                    AdapterType = 3;
                    last_following_id = "";
                    last_post_id = "";
                    last_followers_id = "";
                    Log.e("onClick", "AdapterType " + AdapterType);
                    RefreshDataList(true, 0);//AdapterType = 2; postTV onClick
                }
                AdapterType = 3;
                break;
        }
    }
}
