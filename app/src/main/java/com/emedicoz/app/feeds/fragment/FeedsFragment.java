package com.emedicoz.app.feeds.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.feeds.activity.NewProfileActivity;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.feeds.adapter.FeedRVAdapter;
import com.emedicoz.app.modelo.OwnerInfo;
import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.response.PostResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedsFragment extends Fragment implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {
    public Progress mProgress;
    public User user;
    public FeedRVAdapter feedRVAdapter;
    public boolean isRefresh;
    public String lastPostId;
    public String tagId = "";
    public int previousTotalItemCount;
    public int firstVisibleItem = 0;
    public int visibleItemCount;
    public int totalItemCount;
    public String apiType;
    public RecyclerView feedRV;
    TextView errorTV;
    TextView followingTV;
    TextView facultyPostTV;
    TextView allPostTV;
    ImageView profilePicIV, profilePicIVText;
    LinearLayout upperPanelFeed, tilesLL;
    RelativeLayout imageRL;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<PostResponse> feedArrayList;
    Activity activity;
    LinearLayoutManager LM;
    Tags tg = null;
    int isAlreadyConnected = 0;
    String errorMessageForFeeds;
    MasterFeedsHitResponse masterHitData;
    String feedPreference = Constants.SharedPref.ALL;
    String moderator_selected_stream;
    // for dynamic logic of feed positions
    int Threshold = 20;
    ArrayList<Integer> numPosition;
    ArrayList<String> feedType;
    String searchedQuery;
    Button saveBtn;
    EditText emailET, passwordET, cnPasswordEt;
    String email, password;
    boolean clickedNav = false;
    private boolean loading = true;
    // for searching we need search parameters....
    // if search text is not empty then we show only feeds not anything else like meet the expert and other things.
    private int visibleThreshold = 5;
    private Dialog dialog;

    final String FEED_URL = "data_model/fanwall/fan_wall/get_fan_wall_for_user";
    Menu menu;

    public FeedsFragment() {
    }

    public static FeedsFragment newInstance() {
        return new FeedsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        clickedNav = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feeds, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        clickedNav = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feedArrayList = new ArrayList<>();
        Threshold = 30;
        numPosition = new ArrayList<>();

        //todo need to add the data in the position
        numPosition.add(1);
        numPosition.add(9);
        numPosition.add(17);
        numPosition.add(22);
        numPosition.add(27);

        feedType = new ArrayList<>();

        feedType.add(Const.POST_TYPE_MEET_THE_EXPERT);
        feedType.add(Const.POST_TYPE_PEOPLEYMK);
        feedType.add(Const.POST_TYPE_BANNER);
        feedType.add(Const.POST_TYPE_SUGGESTED_VIDEOS);
        feedType.add(Const.POST_TYPE_SUGGESTED_COURSES);


        masterHitData = SharedPreference.getInstance().getMasterHitResponse();
  /*
        ((BaseABNavActivity) activity).titleRL.setOnClickListener(view1 -> {
          if (clicknav) {
                PopupMenu popupMenu = new PopupMenu(activity, ((BaseABNavActivity) activity).toolbarsubtitleTV, Gravity.LEFT);
                for (String s : activity.getResources().getStringArray(R.array.feedTypeArray)) {
                    popupMenu.getMenu().add(s);
                }
                popupMenu.setOnMenuItemClickListener(FeedsFragment.this);
                popupMenu.show();
            }

        });
*/
        lastPostId = "";
//        if (activity instanceof BaseABNavActivity)
//            swipeRefreshLayout = ((BaseABNavActivity) activity).swipeRefreshLayout;
//        else if (activity instanceof BaseABNoNavActivity)
//            swipeRefreshLayout = ((BaseABNoNavActivity) activity).swipeRefreshLayout;
//        swipeRefreshLayout.setRefreshing(false);
        if (!SharedPreference.getInstance().getString(Const.SUBTITLE).equals("")) {
            try {
                String str = SharedPreference.getInstance().getString(Const.SUBTITLE);
                Tags tg = new Gson().fromJson(new JSONObject(str).toString(), Tags.class);
                tagId = tg.getId();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        errorTV = view.findViewById(R.id.errorTV);

        feedRV = view.findViewById(R.id.feedRV);
        followingTV = view.findViewById(R.id.followingTV);
        facultyPostTV = view.findViewById(R.id.facultyPostTV);
        allPostTV = view.findViewById(R.id.allPostTV);
        upperPanelFeed = view.findViewById(R.id.upperPanelFeed);
        tilesLL = view.findViewById(R.id.feedTile);
        tilesLL.setVisibility(View.VISIBLE);
        imageRL = view.findViewById(R.id.imageRL);

        profilePicIV = view.findViewById(R.id.profilepicIV);
        profilePicIVText = view.findViewById(R.id.profilepicIVText);

        LM = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        feedRV.setLayoutManager(LM);

        initFeedAdapter();

        feedRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = LM.getChildCount();
                totalItemCount = LM.getItemCount();
                firstVisibleItem = LM.findFirstVisibleItemPosition();
                if (dy > 0) {
                    //BaseABNavActivity.postFAB.setVisibility(View.GONE);
//                    if (((BaseABNavActivity) activity).searchView.isIconified())
//                        BaseABNavActivity.bottomPanelRL.setVisibility(View.VISIBLE);
//                    else
//                        BaseABNavActivity.bottomPanelRL.setVisibility(View.GONE);
                } else if (dy < 0) {
//                    if (((BaseABNavActivity) activity).searchView.isIconified())
//                        BaseABNavActivity.bottomPanelRL.setVisibility(View.VISIBLE);
//                    else
//                        BaseABNavActivity.bottomPanelRL.setVisibility(View.GONE);
                }
                if (totalItemCount > 10) { // if the list is more than 10 then only pagination will work
                    if (loading && totalItemCount > previousTotalItemCount) {
                        loading = false;
                        previousTotalItemCount = totalItemCount;
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {

                        int i = 0;
                        while (i < totalItemCount) {

                            if (!feedArrayList.isEmpty() && feedArrayList.size() > totalItemCount - 1 - i) {
                                if (feedArrayList.get(totalItemCount - 1 - i).getId().equals("00")) {
                                    i++;
                                } else {
                                    lastPostId = feedArrayList.get(totalItemCount - 1 - i).getId();
                                    i = totalItemCount;
                                }
                            }
                        }
                        if (isAlreadyConnected == 0) {
                            refreshFeedList(false);// from pagination
                            isAlreadyConnected = 1;
                        }
                        loading = true;
                    }
                }
            }
        });

        // checking if there is no post referred by someone else
        if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.POSTID_REFFERED))) {
            Intent intent = new Intent(activity, PostActivity.class); // Comment Fragment // to show the referred post to this current user
            intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
            intent.putExtra(Const.POST_ID, SharedPreference.getInstance().getString(Const.POSTID_REFFERED));
            activity.startActivity(intent);
        }/* else {
            if (((BaseABNavActivity) activity).feedStatus)
                refreshFeedList(true);
        }*/
        followingTV.setOnClickListener(this);
        facultyPostTV.setOnClickListener(this);
        allPostTV.setOnClickListener(this);
        upperPanelFeed.setOnClickListener(this);

        upperPanelFeed.setVisibility(View.VISIBLE);
        tilesLL.setVisibility(View.VISIBLE);

        if (SharedPreference.getInstance().getLoggedInUser() != null) {
            setUpImage(SharedPreference.getInstance().getLoggedInUser());
        }
    }

    protected void initFeedAdapter() {
        if (TextUtils.isEmpty(lastPostId)) {
            if (feedArrayList.size() > 1 && masterHitData != null) {
                errorTV.setVisibility(View.GONE);
                feedRV.setVisibility(View.VISIBLE);
                Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_FEEDS, feedArrayList);
                feedRVAdapter = new FeedRVAdapter(activity, feedArrayList, activity);
                feedRV.getRecycledViewPool().setMaxRecycledViews(2, 0);
                feedRV.getRecycledViewPool().setMaxRecycledViews(1, 0);
                feedRV.setAdapter(feedRVAdapter);
            } else if (Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_FEEDS) != null &&
                    masterHitData != null && true || !Helper.isConnected(activity)) {
                errorTV.setVisibility(View.GONE);
                feedRV.setVisibility(View.VISIBLE);
                upperPanelFeed.setVisibility(View.VISIBLE);
                tilesLL.setVisibility(View.VISIBLE);
                if (feedPreference.equalsIgnoreCase(activity.getResources().getString(R.string.all_feed))) {
                    changeTabBackground(Const.ALL_FEED);
                } else if (feedPreference.equalsIgnoreCase(activity.getResources().getString(R.string.expert_feed))) {
                    changeTabBackground(Const.FACULTY_FEED);
                } else if (feedPreference.equalsIgnoreCase(activity.getResources().getString(R.string.with_follow_feed))) {
                    changeTabBackground(Const.FOLLOW_FEED);
                }
                feedArrayList = (ArrayList<PostResponse>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_FEEDS);
                feedRVAdapter = new FeedRVAdapter(activity, feedArrayList, activity);
                feedRV.setAdapter(feedRVAdapter);
            } else {
                if (!GenericUtils.isEmpty(errorMessageForFeeds) && errorMessageForFeeds.contains(
                        getString(R.string.something_went_wrong_string)))
                    Helper.showErrorLayoutForNav(apiType, activity, 1, 2);
            }
        } else {
            feedRVAdapter.notifyDataSetChanged();
        }

    }

    public void networkCallForFeeds() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FEEDFRAG", "networkCallForFeeds: ");
        searchedQuery = SharedPreference.getInstance().getString(Constants.SharedPref.SEARCHED_QUERY);
        feedPreference = (TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.FEED_PREFERENCE)) ? activity.getResources().getString(R.string.all_feed) : SharedPreference.getInstance().getString(Const.FEED_PREFERENCE));
        moderator_selected_stream = SharedPreference.getInstance().getString(Const.MODERATOR_SELECTED_STREAM);

        String url = FEED_URL;

        if (feedPreference.equalsIgnoreCase(activity.getResources().getString(R.string.all_feed)) || !TextUtils.isEmpty(searchedQuery))
            url = FEED_URL + Const.GET_ONLY_FEED + moderator_selected_stream;

            // to check whether the user has selected to see the expert feeds only.
        else if (feedPreference.equalsIgnoreCase(activity.getResources().getString(R.string.expert_feed)))
            url = FEED_URL + Const.IS_EXPERT;

            // to check whether user has selected to see the with follow expert feeds.
        else if (feedPreference.equalsIgnoreCase(activity.getResources().getString(R.string.with_follow_feed)))
            url = FEED_URL + Const.IS_FOLLOWER_POST;

        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getFeedForUser(url, SharedPreference.getInstance().getLoggedInUser().getId(),
                lastPostId, tagId, searchedQuery);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONArray dataArray;
                Gson gson = new Gson();

                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        isAlreadyConnected = 0;

                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            Log.e("feed:getFeed", "onResponse: ");
                            upperPanelFeed.setVisibility(View.VISIBLE);
                            tilesLL.setVisibility(View.VISIBLE);
                            if (feedPreference.equalsIgnoreCase(activity.getResources().getString(R.string.all_feed))) {
                                changeTabBackground(Const.ALL_FEED);
                            } else if (feedPreference.equalsIgnoreCase(activity.getResources().getString(R.string.expert_feed))) {
                                changeTabBackground(Const.FACULTY_FEED);
                            } else if (feedPreference.equalsIgnoreCase(activity.getResources().getString(R.string.with_follow_feed))) {
                                changeTabBackground(Const.FOLLOW_FEED);
                            }
                            dataArray = GenericUtils.getJsonArray(jsonResponse);
                            if (dataArray.length() > 0) {

                                visibleThreshold = dataArray.length() - 10;
                                int i = 0;
                                while (i < dataArray.length()) {
                                    feedArrayList.add(gson.fromJson(dataArray.optJSONObject(i).toString(), PostResponse.class));
                                    i++;
                                }
                            }

                            // only the feeds will be shown is the search is on.
                            // this segment will not work if user is searching.
                            if (TextUtils.isEmpty(searchedQuery)) {
                                if (Threshold > feedArrayList.size()) {
                                    int i = feedArrayList.size() / Threshold;
                                    while (i >= 0) {
                                        for (int j = 0; j < numPosition.size(); j++) {
                                            if (numPosition.get(j) < feedArrayList.size() && feedType.size() > j && !feedType.get(j).isEmpty()) {
                                                switch (feedType.get(j)) {
                                                    case Const.POST_TYPE_PEOPLEYMK:
                                                        feedArrayList.add(numPosition.get(j), getPeopleYouMayKnowType());
                                                        break;
                                                    case Const.POST_TYPE_BANNER:
                                                        feedArrayList.add(numPosition.get(j), getBannerType());
                                                        break;
                                                    case Const.POST_TYPE_SUGGESTED_VIDEOS:
                                                        feedArrayList.add(numPosition.get(j), getSuggestedVideoType());
                                                        break;
                                                    case Const.POST_TYPE_MEET_THE_EXPERT:
                                                        feedArrayList.add(numPosition.get(j), getMeetTheExpertType());
                                                        break;
                                                    case Const.POST_TYPE_SUGGESTED_COURSES:
                                                        feedArrayList.add(numPosition.get(j), getSuggestedCourseType());
                                                        break;
                                                    default:
                                                }
                                                numPosition.set(j, numPosition.get(j) + Threshold);
                                            }
                                        }
                                        i--;
                                    }
                                } else {
                                    for (int j = 0; j < numPosition.size(); j++) {
                                        if (numPosition.get(j) < feedArrayList.size() && feedType.size() > j && !feedType.get(j).isEmpty()) {
                                            switch (feedType.get(j)) {
                                                case Const.POST_TYPE_PEOPLEYMK:
                                                    feedArrayList.add(numPosition.get(j), getPeopleYouMayKnowType());
                                                    break;
                                                case Const.POST_TYPE_BANNER:
                                                    feedArrayList.add(numPosition.get(j), getBannerType());
                                                    break;
                                                case Const.POST_TYPE_SUGGESTED_VIDEOS:
                                                    feedArrayList.add(numPosition.get(j), getSuggestedVideoType());
                                                    break;
                                                case Const.POST_TYPE_MEET_THE_EXPERT:
                                                    feedArrayList.add(numPosition.get(j), getMeetTheExpertType());
                                                    break;
                                                case Const.POST_TYPE_SUGGESTED_COURSES:
                                                    feedArrayList.add(numPosition.get(j), getSuggestedCourseType());
                                                    break;
                                                default:
                                            }
                                            numPosition.set(j, numPosition.get(j) + Threshold);
                                        }
                                    }
                                }
                            }
                            Constants.feedStatus = false;
                            //((BaseABNavActivity) activity).feedStatus = false;
                            initFeedAdapter();
                        } else {
                            if (GenericUtils.isListEmpty(feedArrayList)) {
//                                upperPanelFeed.setVisibility(View.GONE);
//                                tilesLL.setVisibility(View.GONE);
                                errorTV.setVisibility(View.VISIBLE);
//                                Helper.showErrorLayoutForNav(apiType, activity, 1, 0);
                            }

                            JSONObject data;
                            String popupMessage;
                            data = GenericUtils.getJsonObject(jsonResponse);
                            popupMessage = data.getString("popup_msg");
                            RetrofitResponse.handleAuthCode(activity, jsonResponse.optString(Const.AUTH_CODE), popupMessage);

//                            if (swipeRefreshLayout.isRefreshing() && isAlreadyConnected == 0) {
//                                swipeRefreshLayout.setRefreshing(false);
//                                isRefresh = false;
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    if (swipeRefreshLayout.isRefreshing() && isAlreadyConnected == 0) {
//                        swipeRefreshLayout.setRefreshing(false);
//                        isRefresh = false;
//                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });


    }

    public void networkCallForMasterHit() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getMasterFeedForUser2(SharedPreference.getInstance().getLoggedInUser().getId(), tagId);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e("FeedsFragment ", "networkCallForMasterHit onResponse:" + jsonResponse);
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            masterHitData = gson.fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterFeedsHitResponse.class);
                        /*if (masterFeedsHitResponse.getUser_detail().getCountry()==null || masterFeedsHitResponse.getUser_detail().getState()==null || masterFeedsHitResponse.getUser_detail().getCity()==null || masterFeedsHitResponse.getUser_detail().getCollege()==null){
                            Helper.userDetailMissingPopup(activity,masterFeedsHitResponse.getPop_msg(), Const.REGISTRATION, Const.PROFILE);
                        }*/
                            if (masterHitData.getValidate_dams_user().equals("1")) {
                                user = SharedPreference.getInstance().getLoggedInUser();
                                checkAuth();
                            }
                            SharedPreference.getInstance().setMasterHitData(masterHitData);
                            if (activity != null) {
                                ((HomeActivity) activity).getNavData();
                               // ((BaseABNavActivity) activity).getNavData();
                                Constants.courseStatus = false;
                            }

                            networkCallForLiveStream();
                        } else {

                            networkCallForLiveStream();//NetworkAPICall(API.API_GET_LIVE_STREAM, false);
                            if (swipeRefreshLayout.isRefreshing() && isAlreadyConnected == 0) {
                                swipeRefreshLayout.setRefreshing(false);
                                isRefresh = false;
                            }

                            JSONObject data;
                            String popupMessage;
                            data = GenericUtils.getJsonObject(jsonResponse);
                            popupMessage = data.getString("popup_msg");
                            RetrofitResponse.handleAuthCode(activity, jsonResponse.optString(Const.AUTH_CODE), popupMessage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });

    }

    public void networkCallForLiveStream() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FEEDFRAG", "networkcallForLiveStream: ");
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getLiveStreamForUser(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {

                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            Log.e("FEEDSFRAGMNET", "networkCallForLive: ");
                            feedArrayList = new ArrayList<>();
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);

                            PostResponse response1 = new PostResponse();
                            OwnerInfo owner = new OwnerInfo();
                            response1.setPost_type(Const.POST_TYPE_LIVE_STREAM);
                            response1.setHlslink(data.optString(Const.HLS));
                            response1.setChat_node(data.optString("chat_node"));
                            response1.setChat_platform(data.optString("chat_platform"));
                            response1.setId("00");
                            response1.setThumbnail(data.optString("thumbnail"));
                            response1.setIs_vod(data.optString("is_vod"));
                            owner.setId(data.optString(com.emedicoz.app.utilso.Constants.Extras.ID));
                            owner.setName(data.optString(com.emedicoz.app.utilso.Constants.Extras.NAME));
                            owner.setProfile_picture(data.optString(Const.PROFILE_PICTURE));
                            response1.setPost_owner_info(owner);
                            feedArrayList.add(response1);
                            numPosition.set(0, 2);
                            getFirstFeedData();

                            networkCallForFeeds();
                            //NetworkAPICall(API.API_GET_FEEDS_FOR_USER, false);
                        } else {

                                /*if (jsonResponse.optString(Const.AUTH_CODE)!=null){
                                    if (jsonResponse.optString(Const.AUTH_CODE).equals(Const.EXPIRY_AUTH_CODE)) {
                                        Toast.makeText(activity, "You are already logged in with some other devices, So you are logged out from this device.", Toast.LENGTH_SHORT).show();
                                        Helper.SignOutUser(activity);
                                    }
                                }*/

                            numPosition.set(0, 1);
                            if (TextUtils.isEmpty(lastPostId)) {
                                feedArrayList = new ArrayList<>();
                            }
                            getFirstFeedData();
                            networkCallForFeeds();

                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            //NetworkAPICall(API.API_GET_FEEDS_FOR_USER, false);

                        }
                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     }
                                 }
                             }

                             @Override
                             public void onFailure(Call<JsonObject> call, Throwable t) {
                                 Helper.showExceptionMsg(activity, t);
                             }
                         }
        );
    }

    public void networkCallForUserData() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FEEDFRAG", "networkcallForUserData: ");
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getDataForUser("data_model/user/Registration/get_active_user/" +
                SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.body() != null) {

                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Log.e("FEEDFRAGMENT ", "userDAta");
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);

                            SharedPreference.getInstance().setLoggedInUser(gson.fromJson(data.toString(), User.class));
                            SharedPreference.getInstance().putInt(Const.VERSION_CODE, Helper.getVersionCode(activity));
                            refreshFeedList(true);/// from user successcallback
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                       /* if (jsonResponse.optString(Const.AUTH_CODE)!=null){
                            if (jsonResponse.optString(Const.AUTH_CODE).equals(Const.EXPIRY_AUTH_CODE)) {
                                Toast.makeText(activity, "You are already logged in with some other devices, So you are logged out from this device.", Toast.LENGTH_SHORT).show();
                                Helper.SignOutUser(activity);
                            }
                        }*/
                            if (TextUtils.isEmpty(lastPostId)) {
                                errorMessageForFeeds = jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE);
                                initFeedAdapter();
                            }
                            isAlreadyConnected = 0;

                            if (swipeRefreshLayout.isRefreshing() && isAlreadyConnected == 0) {
                                swipeRefreshLayout.setRefreshing(false);
                                isRefresh = false;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });

    }

    public void networkCallForUpdateUserData() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FEEDFRAG", "networkcallForUpdateUserData: ");
        mProgress.show();
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.updateDataForUser(SharedPreference.getInstance().getLoggedInUser().getId(),
                email, password);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                Gson gson = new Gson();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            Log.e("networkcallForUser: ", "updateUserDAta");
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            User userprofile = gson.fromJson(data.toString(), User.class);
                            SharedPreference.getInstance().ClearLoggedInUser();
                            SharedPreference.getInstance().setLoggedInUser(userprofile);
                            dialog.cancel();

                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                       /* if (jsonResponse.optString(Const.AUTH_CODE)!=null){
                            if (jsonResponse.optString(Const.AUTH_CODE).equals(Const.EXPIRY_AUTH_CODE)) {
                        +        Toast.makeText(activity, "You are already logged in with some other devices, So you are logged out from this device.", Toast.LENGTH_SHORT).show();
                                Helper.SignOutUser(activity);
                            }
                        }*/

                            if (swipeRefreshLayout.isRefreshing() && isAlreadyConnected == 0) {
                                swipeRefreshLayout.setRefreshing(false);
                                isRefresh = false;
                            }
                            Toast.makeText(activity, jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showExceptionMsg(activity, t);
            }
        });

    }

    public void retryApi(String apiname) {
        switch (apiname) {
            case API.API_GET_FEEDS_FOR_USER:
                networkCallForFeeds();
                break;
            case API.API_GET_LIVE_STREAM:
                networkCallForLiveStream();
                break;
            case API.API_GET_MASTER_HIT:
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    networkCallForValidateDAMSUser();
                }
                networkCallForMasterHit();
                break;
            case API.API_GET_USER:
                networkCallForUserData();
                break;
            case API.API_UPDATE_INFO:
                networkCallForUpdateUserData();
                break;
            default:
        }
    }


    private void networkCallForValidateDAMSUser() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FEEDFRAG", "networkcallForValidateDAMSUser: ");
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.validateDAMSUser(SharedPreference.getInstance().getLoggedInUser().getDams_tokken());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            Log.e("FEEDFRAGMENT: ", "validateDams");
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            Helper.SignOutUser(activity);
                        /*JSONObject data=null;
                        String popupMessage = "";
                        try {
                            data = GenericUtils.getJsonObject(jsonResponse);
                            popupMessage = data.getString("popup_msg");
                        } catch (Exception e){
                            e.printStackTrace();
                        }*/
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    public PostResponse getMeetTheExpertType() {
        PostResponse response = new PostResponse();
        response.setId("00");
        response.setPost_type(Const.POST_TYPE_MEET_THE_EXPERT);
        return response;
    }

    public PostResponse getSuggestedVideoType() {
        PostResponse response = new PostResponse();
        response.setId("00");
        response.setPost_type(Const.POST_TYPE_SUGGESTED_VIDEOS);
        return response;
    }

    public PostResponse getSuggestedCourseType() {
        PostResponse response = new PostResponse();
        response.setId("00");
        response.setPost_type(Const.POST_TYPE_SUGGESTED_COURSES);
        return response;
    }

    public PostResponse getPeopleYouMayKnowType() {
        PostResponse response = new PostResponse();
        response.setId("00");
        response.setPost_type(Const.POST_TYPE_PEOPLEYMK);
        return response;
    }

    public PostResponse getBannerType() {
        PostResponse response = new PostResponse();
        response.setId("00");
        response.setPost_type(Const.POST_TYPE_BANNER);
        return response;
    }

    public void refreshFeedList(boolean show) {

//        if (!swipeRefreshLayout.isRefreshing()) {
//            swipeRefreshLayout.setRefreshing(show);
//            isRefresh = show;
//        }

//        if (show)
//            mprogress.show();

        // this 'IF' is only to check if user is using the latest version first time,
        // then we get user profile again from server
        int preversion = SharedPreference.getInstance().getInt(Const.VERSION_CODE);

        if (preversion > 0 && (preversion < Helper.getVersionCode(activity))) {
            networkCallForUserData();//NetworkAPICall(API.API_GET_USER, false);
        } else {
            if (TextUtils.isEmpty(lastPostId)) {
                //if (((BaseABNavActivity) Objects.requireNonNull(getActivity())).masterHitStatus)
                if (Constants.masterHitStatus) {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        networkCallForValidateDAMSUser();
                    }
                    networkCallForMasterHit();//NetworkAPICall(API.API_GET_MASTER_HIT, false);
                } else
                    networkCallForLiveStream();//NetworkAPICall(API.API_GET_LIVE_STREAM, false);
            } else networkCallForFeeds();//NetworkAPICall(API.API_GET_FEEDS_FOR_USER, false);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof HomeActivity){
            menu = ((HomeActivity) activity).getMyMenu();
            if (menu!=null){
                menu.findItem(R.id.app_bar_search).setVisible(false);
            }
            ((HomeActivity) activity).toolbarHeader.setVisibility(View.GONE);
            ((HomeActivity) activity).floatingActionButton.setVisibility(View.GONE);

        }
//        if (activity instanceof BaseABNavActivity) {
//
//            BaseABNavActivity.bottomPanelRL.setVisibility(View.VISIBLE);
//            ((BaseABNavActivity) activity).manageToolbar(Constants.ScreenName.FAN_WALL);
//        }
        if (SharedPreference.getInstance().getBoolean(Const.PICTURE)) {
            refreshFeedList(true);
            SharedPreference.getInstance().putBoolean(Const.PICTURE, false);
        }
        clickedNav = true;
        refreshFeedList(true);
    }

    // this is to add the let's talk section in the feeds Array List.
    public void getFirstFeedData() {
        /*if (feedArrayList.size() <= 1) {
            PostResponse response1 = new PostResponse();
            OwnerInfo owner = new OwnerInfo();
            owner.setId(SharedPreference.getInstance().getLoggedInUser().getId());
            owner.setName(SharedPreference.getInstance().getLoggedInUser().getName());
            owner.setProfile_picture(SharedPreference.getInstance().getLoggedInUser().getProfile_picture());
            response1.setPost_owner_info(owner);
            response1.setPost_type(Const.POST_TYPE_LETS_TALK);
            response1.setId("00");
            feedArrayList.add(0, response1);
        }*/
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        for (int i = 0; i < activity.getResources().getStringArray(R.array.feedTypeArray).length; i++) {
            if (item.getTitle().equals(activity.getResources().getStringArray(R.array.feedTypeArray)[i])) {

                if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.SUBTITLE))) {
                    try {
                        String str = SharedPreference.getInstance().getString(Const.SUBTITLE);
                        tg = new Gson().fromJson(new JSONObject(str).toString(), Tags.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (tg != null) {
                        ((BaseABNavActivity) activity).toolbarSubTitleTV
                                .setText(String.format("%s / %s", tg.getText(), item.getTitle()));
                    }
                } else {
                    ((BaseABNavActivity) activity).toolbarSubTitleTV.setVisibility(View.VISIBLE);
                    ((BaseABNavActivity) activity).toolbarSubTitleTV
                            .setText(String.format("%s", item.getTitle()));
                }

                Log.e("item", item.getTitle().toString() + i);
                feedPreference = item.getTitle().toString();

                SharedPreference.getInstance().putString(Const.FEED_PREFERENCE, feedPreference);

                isRefresh = true;
                lastPostId = "";
                firstVisibleItem = 0;
                previousTotalItemCount = 0;
                visibleItemCount = 0;
                refreshFeedList(true);//// on change of feedPreference
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void checkAuth() {
        /*if (!TextUtils.isEmpty(user.getDams_tokken()) && user.getIs_social().equals("0")
                && TextUtils.isEmpty(user.getEmail()) || TextUtils.isEmpty(user.getPassword())) {
            getDetailDialog();
        }*/
    }


    private void getDetailDialog() {
        dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_update_info);
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button

        saveBtn = dialog.findViewById(R.id.saveBtn);
        emailET = dialog.findViewById(R.id.emailET);
        passwordET = dialog.findViewById(R.id.passwordET);
        cnPasswordEt = dialog.findViewById(R.id.cnpasswordET);

        if (!TextUtils.isEmpty(user.getEmail()))
            emailET.setText(user.getEmail());

        // if button is clicked, close the custom dialog
        saveBtn.setOnClickListener(v -> checkDAMSLoginValidation());

        dialog.show();
    }

    private void checkDAMSLoginValidation() {
        email = Helper.GetText(emailET);
        password = Helper.GetText(passwordET);
        String cnPassword = Helper.GetText(cnPasswordEt);
        boolean isDataValid = true;

        if ((!Patterns.EMAIL_ADDRESS.matcher(email).matches()))
            isDataValid = Helper.DataNotValid(emailET, 1);

        else if (TextUtils.isEmpty(password))
            isDataValid = Helper.DataNotValid(passwordET);

        else if (TextUtils.isEmpty(cnPassword) || !password.equals(cnPassword)) {
            isDataValid = false;
            cnPasswordEt.setError("Password and confirmation password do not match. ");
            cnPasswordEt.requestFocus();
        }

        if (isDataValid) {
            networkCallForUpdateUserData();//NetworkAPICall(API.API_UPDATE_INFO, true);
        }
    }

    public void setUpImage(final User user) {

        imageRL.setOnClickListener(v -> {
            if (!(activity instanceof NewProfileActivity))
                Helper.GoToProfileActivity(activity, user.getId());
        });

        if (!TextUtils.isEmpty(user.getProfile_picture())) {
            profilePicIV.setVisibility(View.VISIBLE);
            profilePicIVText.setVisibility(View.GONE);
            Ion.with(profilePicIV.getContext())
                    .load(user.getProfile_picture())
                    .withBitmap()
                    .placeholder(R.mipmap.default_pic)
                    .error(R.mipmap.default_pic)
                    .asBitmap()
                    .setCallback((e, result) -> {
                        if (result != null)
                            profilePicIV.setImageBitmap(result);
                        else
                            profilePicIV.setImageResource(R.mipmap.default_pic);
                    });
        } else {
            Drawable dr = Helper.GetDrawable(user.getName(), activity, user.getId());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allPostTV:
                setPostFilter(activity.getResources().getString(R.string.all_feed));
                feedArrayList.clear();
                if (feedRVAdapter != null)
                    feedRVAdapter.notifyDataSetChanged();
                changeTabBackground(Const.ALL_FEED);
                //((BaseABNavActivity) activity).toolbarSubTitleTV.setText(activity.getResources().getString(R.string.all_feed));
                break;
            case R.id.facultyPostTV:
                feedArrayList.clear();
                if (feedRVAdapter != null)
                    feedRVAdapter.notifyDataSetChanged();
                setPostFilter(activity.getResources().getString(R.string.expert_feed));
                changeTabBackground(Const.FACULTY_FEED);
                //((BaseABNavActivity) activity).toolbarSubTitleTV.setText(activity.getResources().getString(R.string.expert_feed));
                break;
            case R.id.followingTV:
                feedArrayList.clear();
                if (feedRVAdapter != null)
                    feedRVAdapter.notifyDataSetChanged();
                setPostFilter(activity.getResources().getString(R.string.with_follow_feed));
                changeTabBackground(Const.FOLLOW_FEED);
                //((BaseABNavActivity) activity).toolbarSubTitleTV.setText(activity.getResources().getString(R.string.with_follow_feed));
                break;
            case R.id.upperPanelFeed:
                Helper.GoToPostActivity(activity, null, Const.POST_FRAG);
                break;
            default:
                break;
        }
    }

    private void setPostFilter(String postType) {
        SharedPreference.getInstance().putString(Const.FEED_PREFERENCE, postType);

        isRefresh = true;
        lastPostId = "";
        firstVisibleItem = 0;
        previousTotalItemCount = 0;
        visibleItemCount = 0;
        refreshFeedList(true);//// on change of feedPreference

    }

    public void changeTabBackground(String postType) {
        allPostTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.dvl_tab_background_unselected));
        facultyPostTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.dvl_tab_background_unselected));
        followingTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.dvl_tab_background_unselected));
        allPostTV.setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
        facultyPostTV.setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
        followingTV.setTextColor(ContextCompat.getColor(activity, R.color.left_panel_header_text_color));
        switch (postType) {
            case Const.ALL_FEED:
                allPostTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.dvl_tab_background_selected));
                allPostTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                break;
            case Const.FACULTY_FEED:
                facultyPostTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.dvl_tab_background_selected));
                facultyPostTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                break;
            case Const.FOLLOW_FEED:
                followingTV.setBackground(ContextCompat.getDrawable(activity, R.drawable.dvl_tab_background_selected));
                followingTV.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                break;
            default:
                break;
        }
    }
}
