package com.emedicoz.app.feeds.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.R;
import com.emedicoz.app.bookmark.ChildBookMarkFrag;
import com.emedicoz.app.bookmark.adapter.TestAdapternew;
import com.emedicoz.app.bookmark.model.TestModel;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.feeds.adapter.FeedRVAdapter;
import com.emedicoz.app.feeds.adapter.ViewPagerAdapter;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.response.PostResponse;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.utilso.network.CheckConnection;
import com.emedicoz.app.video.adapter.videoAdapter1;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class SavedNotesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyNetworkCall.MyNetworkCallBack {

    public boolean isRefresh;
    public String lastPostId = "";
    public int previousTotalItemCount;
    public int firstVisibleItem;
    public int visibleItemCount;
    public int totalItemCount;
    public FeedRVAdapter feedRVAdapter;
    ArrayList<Video> arrayList = new ArrayList<>();
    RecyclerView feedRV;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<PostResponse> feedArrayList;
    Activity activity;
    TestAdapternew adapter;
    TextView errorTV;
    LinearLayoutManager linearLayoutManager;
    Progress mProgress;
    TabLayout mainTab;
    List<String> listOfTabs = new ArrayList<>();
    View view;
    String tagId;
    String nameOfTab;
    String lisType;
    List<TestModel> itemList2 = new ArrayList<>();
    ArrayList<String> strings = new ArrayList<>();
    private boolean loading = true;
    private int visibleThreshold = 2;
    private ViewPager viewPager;
    String testSeriesId;
    String qTypeDqb = "";
    LinearLayout viewPagerLL;
    MyNetworkCall myNetworkCall;
    private static final String TAG = "SavedNotesFragment";
    LinearLayout upperPanelFeed, tilesLL;

    public SavedNotesFragment() {
        // Required empty public constructor
    }

    public static SavedNotesFragment newInstance() {
        return new SavedNotesFragment();
    }

    public static SavedNotesFragment newInstance(String qTypeDqb) {
        SavedNotesFragment fragment = new SavedNotesFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Extras.Q_TYPE_DQB, qTypeDqb);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        activity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (!(activity instanceof CourseActivity)) {
            if (getArguments() != null) {
                if (getArguments().getString(Constants.Extras.TAG_ID) != null)
                    tagId = getArguments().getString(Constants.Extras.TAG_ID);
                nameOfTab = getArguments().getString(Constants.Extras.NAME_OF_TAB);
                testSeriesId = getArguments().getString(Const.TESTSERIES_ID);
                if (getArguments().getString(Constants.Extras.Q_TYPE_DQB) != null)
                    qTypeDqb = getArguments().getString(Constants.Extras.Q_TYPE_DQB);
                else
                    qTypeDqb = "";
                view = inflater.inflate(R.layout.fragment_feeds, container, false);
            } else {
                view = inflater.inflate(R.layout.layout_newbookmarks, container, false);
                initView(view);
            }
        } else {
            if (getArguments() != null) {
                if (getArguments().getString(Constants.Extras.Q_TYPE_DQB) != null)
                    qTypeDqb = getArguments().getString(Constants.Extras.Q_TYPE_DQB);
                else
                    qTypeDqb = "";
            }
            view = inflater.inflate(R.layout.layout_newbookmarks, container, false);
            initView(view);
        }
        Log.e(TAG, Constants.Extras.Q_TYPE_DQB + "=" + qTypeDqb);
        return view;
    }

    private void initView(View view) {

        FrameLayout containerFl = view.findViewById(R.id.fl_container);
        viewPagerLL = view.findViewById(R.id.ll_view_pager);
        mainTab = view.findViewById(R.id.mainTabLayout);
        viewPager = view.findViewById(R.id.pager);

        upperPanelFeed = view.findViewById(R.id.upperPanelFeed);
        tilesLL = view.findViewById(R.id.feedTile);
        if (upperPanelFeed != null && tilesLL != null) {
            upperPanelFeed.setVisibility(View.GONE);
            tilesLL.setVisibility(View.GONE);
        }
        listOfTabs.clear();
        strings.clear();

        listOfTabs.add("QUIZ");
        listOfTabs.add("VIDEO");
        listOfTabs.add("FEEDS");
        listOfTabs.add("PODCAST");
        listOfTabs.add("WISHLIST");

        strings.add("QBANK");
        strings.add("VIDEO");
        strings.add("FEEDS");
        strings.add("PODCAST");
        strings.add("WISHLIST");

        MasterFeedsHitResponse masterResponse = SharedPreference.getInstance().getMasterHitResponse();
        if (masterResponse != null) {
            if (masterResponse.getDisplay_test_bookmark() != null && masterResponse.getDisplay_test_bookmark().equals("1")) {
                strings.add("TEST");
                listOfTabs.add("TEST");
            }

            if (masterResponse.getDisplay_dquiz_bookmark() != null && masterResponse.getDisplay_dquiz_bookmark().equals("1")) {
                strings.add("DQ");
                listOfTabs.add(Constants.TestType.DAILY_CHALLENGE);
            }
        }

        if (qTypeDqb.equals(Constants.Type.DQB) || qTypeDqb.equals(Constants.Type.DAILY_CHALLENGE)) {
            viewPagerLL.setVisibility(View.GONE);
            containerFl.setVisibility(View.VISIBLE);

            ChildBookMarkFrag fragment = new ChildBookMarkFrag();
            Bundle args = new Bundle();

            if (qTypeDqb.equals(Constants.Type.DQB)) {
                args.putString(Constants.Extras.TAG_NAME, Helper.toUpperCase(Constants.TestType.QUIZ));
                args.putString(Constants.Extras.BOOKMARK_NAME, Helper.toUpperCase(Constants.TestType.QUIZ));

            } else if (qTypeDqb.equals(Constants.Type.DAILY_CHALLENGE)) {
                args.putString(Constants.Extras.TAG_NAME, Helper.toUpperCase(Constants.TestType.DAILY_CHALLENGE));
                args.putString(Constants.Extras.BOOKMARK_NAME, Helper.toUpperCase(Constants.TestType.DAILY_CHALLENGE));
            }
            args.putString(Constants.Extras.Q_TYPE_DQB, qTypeDqb);
            fragment.setArguments(args);

            addFragment(fragment);
        } else {
            viewPagerLL.setVisibility(View.VISIBLE);
            containerFl.setVisibility(View.GONE);
            makeTabs();
        }
    }

    public void makeTabs() {
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), listOfTabs, qTypeDqb, strings));
        mainTab.setupWithViewPager(viewPager);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myNetworkCall = new MyNetworkCall(this, activity);
        if (tagId != null) {
            feedArrayList = new ArrayList<>();
            lastPostId = "";
            errorTV = view.findViewById(R.id.errorTV);

            feedRV = view.findViewById(R.id.feedRV);
            linearLayoutManager = new LinearLayoutManager(activity);
            feedRV.setLayoutManager(linearLayoutManager);

            feedRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                    if (totalItemCount >= 10) {
                        if (loading && totalItemCount > previousTotalItemCount) {
                            loading = false;
                            previousTotalItemCount = totalItemCount;
                        }
                        if (!loading && (totalItemCount - visibleItemCount)
                                <= (firstVisibleItem + visibleThreshold)) { // 0<=4 0<=2
                            int i = 0;
                            while (i < totalItemCount) {
                                if (lisType.equalsIgnoreCase(Const.FEEDS)) {
                                    if (feedArrayList.get(totalItemCount - 1 - i).getId().equals("00")) {
                                        i++;
                                    } else {
                                        lastPostId = feedArrayList.get(totalItemCount - 1 - i).getId();
                                        i = totalItemCount;
                                    }

                                } else if (lisType.equalsIgnoreCase(Const.VIDEO)) {
                                    if (arrayList.get(totalItemCount - 1 - i).getId().equals("00")) {
                                        i++;
                                    } else {
                                        lastPostId = arrayList.get(totalItemCount - 1 - i).getId();
                                        i = totalItemCount;
                                    }

                                } else {
                                    if (itemList2.get(totalItemCount - 1 - i).getId().equals("00")) {
                                        i++;
                                    } else {
                                        lastPostId = itemList2.get(totalItemCount - 1 - i).getBid();
                                        i = totalItemCount;
                                    }
                                }
                            }
                            refreshFeedList2();

                            loading = true;
                        }
                    }
                }
            });

        }
    }

    protected void initFeedAdapter() {
        if (lastPostId.equals("")) {
            if (!feedArrayList.isEmpty()) {
                errorTV.setVisibility(View.GONE);
                feedRV.setVisibility(View.VISIBLE);
                Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_SAVEDNOTES, feedArrayList);
                feedRVAdapter = new FeedRVAdapter(getContext(), feedArrayList, activity);
                feedRV.setAdapter(feedRVAdapter);

            } else if (Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_SAVEDNOTES) != null
                    /*&& (!((BaseABNavActivity)activity).NotesStatus*/ || !Helper.isConnected(activity)) {
                errorTV.setVisibility(View.GONE);
                feedRV.setVisibility(View.VISIBLE);
                feedArrayList = (ArrayList<PostResponse>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_SAVEDNOTES);
            } else {
                errorTV.setVisibility(View.VISIBLE);
                feedRV.setVisibility(View.GONE);
                errorTV.setText(R.string.saved_nothing);

            }
        } else {
            feedRVAdapter.notifyDataSetChanged();
        }
    }

    public void refreshFeedList() {
        makeTabs();
        //swipeRefreshLayout = ((BaseABNavActivity) Objects.requireNonNull(getActivity())).swipeRefreshLayout;

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void refreshFeedList2() {
        if (nameOfTab.equalsIgnoreCase("video")) {
            lisType = "video";
            networkCallForVideo();
        } else if (nameOfTab.equalsIgnoreCase("feeds")) {
            lisType = "feeds";
            networkCallForFeedsBookmarked();
        } else {
            lisType = Constants.TestType.TEST;
            networkCallForTest("refreshFeedList2");
        }
    }

    @Override
    public void onRefresh() {
        Log.e("swipe refresh", "on refresh dash board");
        if (CheckConnection.isConnection(activity)) {
            swipeRefreshLayout.setRefreshing(true);
            isRefresh = true;
            lastPostId = "";
            firstVisibleItem = 0;
            previousTotalItemCount = 0;
            visibleItemCount = 0;
            if (tagId != null) {
                refreshFeedList2();
            } else {
                refreshFeedList();
            }
        } else {
            isRefresh = false;
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "12345onResume: tag_id - " + tagId);
        Log.e("TAG", "12345onResume: listype - " + lisType);
        // reset these variables
        SharedPreference.getInstance().putString(Constants.SharedPref.SEARCHED_QUERY, "");
        SharedPreference.getInstance().putString(Constants.SharedPref.COURSE_SEARCHED_QUERY, "");
        if (Constants.TestType.TEST.equals(lisType)) {
            networkCallForTest("onResume");
        }
        if (!GenericUtils.isEmpty(nameOfTab))
            refreshFeedList2();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("TAG", "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("TAG", "onStop: ");
    }

    public void addFragment(Fragment fragment) {
        try {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_container, fragment)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void networkCallForTest(String str) {
        Log.e(TAG, "networkcallfortest:str = " + str);
        myNetworkCall.NetworkAPICall(API.API_BOOKMARK_LIST, true);
    }

    private void networkCallForVideo() {
        myNetworkCall.NetworkAPICall(API.API_BOOKMARK_LIST, true);
    }

    public void networkCallForFeedsBookmarked() {
        myNetworkCall.NetworkAPICall(API.API_BOOKMARK_LIST, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        if (apiType.equals(API.API_BOOKMARK_LIST)) {
            params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
            params.put(Constants.Extras.TYPE, nameOfTab);
            params.put(Constants.Extras.STREAM, SharedPreference.getInstance().getString(Constants.Extras.STREAM_ID));
            params.put(Const.LAST_POST_ID, lastPostId);
            params.put(Constants.Extras.TAG_ID, tagId);
            params.put(Const.SEARCH_TEXT, SharedPreference.getInstance().getString(Constants.SharedPref.SEARCHED_QUERY));
            if (nameOfTab.equalsIgnoreCase(Const.VIDEO) || nameOfTab.equalsIgnoreCase(Const.FEEDS)) {
                params.put(Const.TESTSERIES_ID, "");
                params.put(Constants.Extras.Q_TYPE_DQB, "");
            } else if (nameOfTab.equalsIgnoreCase("DQUIZ")) {
                params.put(Constants.Extras.Q_TYPE_DQB, Constants.Type.DAILY_CHALLENGE);
            } else {
                params.put(Const.TESTSERIES_ID, !TextUtils.isEmpty(testSeriesId) ? testSeriesId : "");
                params.put(Constants.Extras.Q_TYPE_DQB, qTypeDqb);
            }
        }
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        if (apiType.equals(API.API_BOOKMARK_LIST)) {
            if (nameOfTab.equalsIgnoreCase(Const.VIDEO)) {
                JSONArray jsonArray = GenericUtils.getJsonArray(jsonObject);

                if (TextUtils.isEmpty(lastPostId)) {
                    arrayList = new ArrayList<>();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject data = jsonArray.optJSONObject(i);
                    Video video = new Gson().fromJson(data.toString(), Video.class);

                    arrayList.add(video);
                }
                feedRV.setAdapter(new videoAdapter1(arrayList, activity));

            } else if (nameOfTab.equalsIgnoreCase(Const.FEEDS)) {
                JSONArray dataArray = GenericUtils.getJsonArray(jsonObject);
                if (lastPostId.equals("")) {
                    feedArrayList = new ArrayList<>();
                }
                if (dataArray.length() > 0) {
                    int i = 0;
                    while (i < dataArray.length()) {
                        JSONObject singleDataRow = dataArray.optJSONObject(i);
                        PostResponse response1 = new Gson().fromJson(singleDataRow.toString(), PostResponse.class);
                        feedArrayList.add(response1);
                        i++;
                    }
                }
                initFeedAdapter();
            } else {
                if (jsonObject.optJSONArray(Const.DATA) != null) {
                    JSONArray jsonArray = GenericUtils.getJsonArray(jsonObject);

                    if (jsonArray.length() > 0) {
                        feedRV.setVisibility(View.VISIBLE);
                        errorTV.setVisibility(View.GONE);
                        if (TextUtils.isEmpty(lastPostId)) {
                            itemList2 = new ArrayList<>();
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.optJSONObject(i);
                            TestModel test = new Gson().fromJson(data.toString(), TestModel.class);
                            itemList2.add(test);
                        }
                        Log.e(TAG, "networkcallfortest:itemlist2 size - " + itemList2.size());
                        if (TextUtils.isEmpty(lastPostId)) {
                            if (nameOfTab.equalsIgnoreCase("DQUIZ"))
                                qTypeDqb = Constants.Type.DAILY_CHALLENGE;
                            adapter = new TestAdapternew(activity, itemList2, tagId, testSeriesId, qTypeDqb, nameOfTab);
                            feedRV.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        feedRV.setVisibility(View.GONE);
                        errorTV.setVisibility(View.VISIBLE);

                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        errorTV.setLayoutParams(layoutParams);

                        errorTV.setText(R.string.saved_nothing);
                    }
                }
            }
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
//        GenericUtils.showToast(activity, jsonString);
    }
}
