package com.emedicoz.app.video.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.modelo.Videotable;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.video.adapter.videoAdapter1;
import com.emedicoz.app.video.model.LiveVideoResponseData;
import com.emedicoz.app.video.retrofit.VideoRetrofitApi;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class VideoFragmentViewPager extends Fragment {

    private static final String TAG = "VideoFragmentViewPager";
    public static boolean IS_LIKE_UPDATE = false;
    public static boolean IS_VIEW_UPDATE = false;
    public static boolean IS_COMMENT_UPDATE = false;
    public SwipeRefreshLayout swipeRefreshLayout;
    int firstVisibleItem;
    int visibleItemCount;
    int totalItemCount;
    int isAlreadyConnected = 0;
    public int previousTotalItemCount;
    public String lastPostId = "";
    public videoAdapter1 adapter;
    public Activity activity;
    VideoRetrofitApi mService;
    LiveVideoResponseData liveVideoResponseData;
    LinearLayoutManager layoutManager;
    TextView noVideo;
    ArrayList<Video> arrayList = new ArrayList<>();
    List<Video> sliderArrayList = new ArrayList<>();
    ArrayList<LiveVideoResponseData> liveVideoResponseDataArrayList;
    String jsonData = "";
    Tags tag;
    ArrayList<Video> liveVideoArrayList;
    com.emedicoz.app.imageslider.sliderAdapter sliderAdapter;
    int flagForSlider = 0;
    Video liveVideoRes = null;
    Progress mProgress;
    private RecyclerView recyclerView;
    private boolean loading = true;
    private int visibleThreshold = 3;
    private String sortBy = "";
    private int pageCount = 1;
    private int total_item_count = 0;
    private int lastViewedPosition;

    public static VideoFragmentViewPager newInstance(Tags tag) {
        VideoFragmentViewPager fragment = new VideoFragmentViewPager();
        Bundle args = new Bundle();
        args.putSerializable(Const.TAG_VIDEO, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        activity = getActivity();
        if (getArguments() != null)
            tag = (Tags) getArguments().getSerializable(Const.TAG_VIDEO);
        liveVideoResponseDataArrayList = new ArrayList<>();
        liveVideoArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.video_fragment_view_pager, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mService = Helper.getApi();
        liveVideoResponseData = new LiveVideoResponseData();
        initView(view);
        //jsonData = ((BaseABNavActivity) activity).getData(tag.getId()); //i comment
        jsonData = ((HomeActivity) activity).getData(tag.getId());
        Videotable videotable = new Gson().fromJson(jsonData, Videotable.class);
        //BaseABNavActivity.postFAB.setVisibility(View.VISIBLE); //i comment
        //BaseABNavActivity.bottomPanelRL.setVisibility(View.VISIBLE); //i comment
        if (videotable.message.equals(activity.getResources().getString(R.string.no_record_found))) {
            if (Helper.isConnected(activity)) {
                lastPostId = "";
                recyclerView.setVisibility(View.VISIBLE);
                noVideo.setVisibility(View.GONE);
            }
            refreshVideoList(true); // at the time of creating the fragment
        } else {
            lastPostId = "";
            arrayList.clear();

            for (int i = 0; i < videotable.videos.size(); i++) {
                Video video = videotable.videos.get(i);
                if (TextUtils.isEmpty(video.getId()) && video.getVideo_desc().equalsIgnoreCase(getString(R.string.slider_string))) {
                    flagForSlider = 1;
                }
                arrayList.add(video);
            }
            initVideoAdapter();
        }

        getDataOnRecyclerViewScroll();

    }

    private void getDataOnRecyclerViewScroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                lastViewedPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (dy > 0) {
                    ((HomeActivity) activity).floatingActionButton.setVisibility(View.GONE);
                    //BaseABNavActivity.postFAB.setVisibility(View.GONE); //i comment
                    //BaseABNavActivity.bottomPanelRL.setVisibility(View.VISIBLE); ////i comment
                } else if (dy < 0) {
                    ((HomeActivity) activity).floatingActionButton.setVisibility(View.VISIBLE);
                   // BaseABNavActivity.postFAB.setVisibility(View.VISIBLE); ////i comment
                    //BaseABNavActivity.bottomPanelRL.setVisibility(View.VISIBLE); //i comment
                }
                if (totalItemCount >= total_item_count) { // if the list is more than 25 then only pagination will work
                    visibleThreshold = total_item_count * 20 / 100;
                    if (loading && totalItemCount > previousTotalItemCount) {
                        loading = false;
                        previousTotalItemCount = totalItemCount;
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        if (!GenericUtils.isListEmpty(arrayList) && arrayList.size() > totalItemCount - 1) {
                            lastPostId = arrayList.get(totalItemCount - 1).getId();

                        }
                        if (sortBy.equals("views") || sortBy.equals("likes")) {
                            pageCount++;
                        }
                        if (isAlreadyConnected == 0) {
                            refreshVideoList(false); // at the time of pagination
                            isAlreadyConnected = 1;
                        }
                        loading = true;

                    }
                }
            }
        });
    }

    public void changeSliderData(Video videoResponse) {
        int i = 0;
        if (videoResponse != null) {
            while (i < sliderArrayList.size()) {
                if ((sliderArrayList.get(i).getId()).equals(videoResponse.getId())) {
                    sliderArrayList.set(i, videoResponse);

                    break;
                }
                i++;
            }
            if (sliderAdapter != null)
                sliderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        networkCallForSingleCatVideoData();
    }


    private void initView(View view) {
        arrayList.clear();
        recyclerView = view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        noVideo = view.findViewById(R.id.no_video);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.colorAccent, R.color.colorPrimaryDark);

        swipeRefreshLayout.setOnRefreshListener(this::pullToRefresh);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // TODO Auto-generated method stub
                try {
                    int firstPos = layoutManager.findFirstCompletelyVisibleItemPosition();

                    if (firstPos > 0 && !swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setEnabled(false);
                    } else {
                        swipeRefreshLayout.setEnabled(true);
                    }
                }catch (IndexOutOfBoundsException index){

                }
            }
        });
    }

    public void pullToRefresh() {
        arrayList.clear();
        sliderArrayList.clear();
        sortBy = "";
        lastPostId = "";
        previousTotalItemCount = 0;
        pageCount = 0;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        refreshVideoList(true); // for pull to refresh
    }

    public void networkCallForSingleCatVideoData() {
        if (tag == null) return;
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        SinglecatVideoDataApiInterface apiInterface = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apiInterface.getVideoListData(SharedPreference.getInstance().getLoggedInUser().getId(),
                tag.getId(), lastPostId, sortBy, String.valueOf(pageCount), "");
        Log.e(TAG, tag.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (isAdded()){
                    if (response.body() != null) {
                        if (!GenericUtils.isListEmpty(liveVideoResponseDataArrayList))
                            liveVideoResponseDataArrayList.clear();
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            isAlreadyConnected = 0;
                            Log.e(TAG, " networkcallForSingleCatVideoData onResponse: " + jsonResponse);
                            if (jsonResponse.optBoolean(Const.STATUS)) {
                                JSONArray jsonArray = GenericUtils.getJsonArray(jsonResponse);

                                if (TextUtils.isEmpty(lastPostId)) {
                                    arrayList.clear();
                                    total_item_count = jsonArray.length();
                                    //System.out.println("total_item_count-------------------------"+total_item_count);
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.optJSONObject(i);
                                    Video video = new Gson().fromJson(data.toString(), Video.class);
                                    if (video.getFeatured().equals("1") || video.getIs_new().equals("1")) {
                                        flagForSlider = 1;
                                    }
                                    arrayList.add(video);
                                }

                                if (flagForSlider == 1 && TextUtils.isEmpty(lastPostId)) {
                                    Video vd = new Video();
                                    vd.setVideo_desc("Slider");
                                    arrayList.add(0, vd);
                                }
                                Constants.videoStatus = false;
                                //((BaseABNavActivity) activity).videoStatus = false; //i comment
                                initVideoAdapter();
                            } else {
                                if (isAdded()) {
                                    initVideoAdapter();

//                                if (GenericUtils.isListEmpty(arrayList))
//                                    Helper.showErrorLayoutForNav(API.API_GET_SINGLE_CAT_VIDEO_DATA, activity, 1, 2);

                                    if (TextUtils.isEmpty(lastPostId)) {
                                        noVideo.setText(activity.getResources().getString(R.string.no_videos_found));
                                        recyclerView.setVisibility(View.GONE);
                                        noVideo.setVisibility(View.VISIBLE);
                                    } else {
                                        initVideoAdapter();
                                    }
                                }
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //((BaseABNavActivity) activity).setData(arrayList, tag.getId());
                        ((HomeActivity) activity).setData(arrayList, tag.getId());

                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });


    }

    public void networkCallForLiveStream() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        ApiInterface apis = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apis.getLiveStreamForUser(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (activity.isFinishing() && isAdded()) return;
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(TAG, " networkcallforLiveStream onResponse: " + jsonResponse);
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            sliderArrayList.clear();
                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                                if (GenericUtils.isJsonEmpty(data)) return;

                                liveVideoRes = new Video();
                                liveVideoRes.setURL(data.optString(Const.HLS));
                                liveVideoRes.setVideo_type(Const.VIDEO_LIVE);
                                liveVideoRes.setId("00");
                                liveVideoRes.setIs_new("0");
                                liveVideoRes.setFeatured("0");
                                liveVideoRes.setViews("0");
                                liveVideoRes.setCreation_time("0");
                                liveVideoRes.setId(data.optString(com.emedicoz.app.utilso.Constants.Extras.ID));
                                liveVideoRes.setVideo_title(data.optString(com.emedicoz.app.utilso.Constants.Extras.NAME));
                                liveVideoRes.setThumbnail_url(data.optString(Const.PROFILE_PICTURE));

                            }
                            networkCallForSingleCatVideoData();
                        } else {
                            networkCallForSingleCatVideoData();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());

            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void initVideoAdapter() {
//        if (adapter == null) {
        adapter = new videoAdapter1(arrayList, activity, flagForSlider, liveVideoRes);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(lastViewedPosition);
//        } else {
//            adapter.notifyDataSetChanged();
//        }

        if (GenericUtils.isListEmpty(arrayList)) {
            noVideo.setText(activity.getResources().getString(R.string.no_videos_found));
            recyclerView.setVisibility(View.GONE);
            noVideo.setVisibility(View.VISIBLE);

        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noVideo.setVisibility(View.GONE);
        }
    }


    public void filteringSearch(String sortBy) {
        pageCount = 0;
        lastPostId = "";
        this.sortBy = sortBy;
        previousTotalItemCount = 0;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        refreshVideoList(true);
        // to apply the filters
        //adapter.notifyDataSetChanged();
    }

    public void refreshVideoList(boolean show) {

            if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(show);
            }
            if (TextUtils.isEmpty(lastPostId)) {
                networkCallForLiveStream();//networkCall.NetworkAPICall(API.API_GET_LIVE_STREAM, false);
            } else {
                networkCallForSingleCatVideoData();//networkCall.NetworkAPICall(API.API_GET_SINGLE_CAT_VIDEO_DATA, false);
            }

    }

}
