package com.emedicoz.app.video.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.response.MasterFeedsHitResponse;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.video.adapter.videoAdapter1;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin1 on 27/10/17.
 */

public class VideoFragment extends Fragment {

    Activity activity;
    public ViewPager tabanimViewpager;
    public ArrayList<Fragment> mFragmentList = new ArrayList<>();
    public int firstVisibleItem, visibleItemCount, totalItemCount;
    public int previousTotalItemCount;
    String lastPostId = "";
    String tagId = "0";
    String search = "";
    public int currentPosition = 0;
    public Pageadapter pageadapter;
    public View mView;
    public Progress mProgress;

    ArrayList<Video> arrayList = new ArrayList<>();
    Tags tg = null;
    String sortBy;
    String errorMessage;
    ArrayList<Tags> tagsArrayListMain;
    private LinearLayout viewPagerLayout;
    private RelativeLayout recyclerViewLayout;
    private TextView errorMessageTV;
    private RecyclerView videoRV;
    private TabLayout tabs;
    private videoAdapter1 videoAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<String> mFragmentTitleList = new ArrayList<>();
    private ArrayList<Tags> tagsArrayList;
    private boolean isScrolling = false;
    public boolean isSearching = false;
    private Menu menu;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) activity).toolbarHeader.setVisibility(View.GONE);
        initView(view);
        mView = view;
    }

    public void initView(View v) {
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);

        viewPagerLayout = v.findViewById(R.id.toplayout);
        recyclerViewLayout = v.findViewById(R.id.rl1);

        errorMessageTV = v.findViewById(R.id.errorTV);
        videoRV = v.findViewById(R.id.videoRV);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        videoRV.setLayoutManager(layoutManager);

        tabs = v.findViewById(R.id.tabs);
        tabanimViewpager = v.findViewById(R.id.tabanim_viewpager);

        // getting offline data if it is pre-saved
        tagsArrayListMain = getTagsForUser();
        tagsArrayList = new ArrayList<>();

        Tags tag = new Tags();
        tag.setId("0");
        tag.setText(getString(R.string.all));

        addFragment(tag);
        tagsArrayList.add(tag);

        setTagOrHitApi();


        pageadapter = new Pageadapter(getChildFragmentManager(), tagsArrayList.size());
        tabanimViewpager.setAdapter(pageadapter);
        tabanimViewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabs.setupWithViewPager(tabanimViewpager);

        try {
            String str = SharedPreference.getInstance().getString(Const.SUBTITLE);
            if (!TextUtils.isEmpty(str)) {
                tg = new Gson().fromJson(new JSONObject(str).toString(), Tags.class);

            }
            if (tg != null) {
                for (Tags tags : tagsArrayList) {
                    if (tags.getText().equals(tg.getText())) {
                        tabanimViewpager.setCurrentItem(tagsArrayList.indexOf(tags));
                        break;
                    }
                }

            } else {
                tabanimViewpager.setCurrentItem(0);
            }

            tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    tabanimViewpager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        videoRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                Log.e("TAG", "onScrolled:dy = " + dy);
                if (dy < 0) {
                    manageBottomAndFabVisibility();
                } else if (dy > 0) {
//                    BaseABNavActivity.postFAB.setVisibility(View.VISIBLE);
//                    if (((BaseABNavActivity) activity).searchView.isIconified()) {
//                        ((BaseABNavActivity) activity).setPostFABLayoutParams(BaseABNavActivity.bottomPanelRL.getMeasuredHeight() + 30);
//                        BaseABNavActivity.bottomPanelRL.setVisibility(View.VISIBLE);
//                    } else {
//                        ((BaseABNavActivity) activity).setPostFABLayoutParams(30);
//                        BaseABNavActivity.bottomPanelRL.setVisibility(View.GONE);
//                    }
                    if (isScrolling && (visibleItemCount + firstVisibleItem == totalItemCount)) {
                        if (!arrayList.isEmpty()) {
                            isScrolling = false;
                            lastPostId = arrayList.get(arrayList.size() - 1).getId();
                            refreshVideoList(false);
                        }
                    }
                }
            }
        });
    }

    public void refreshVideoList(boolean show) {
        networkcallForSingleCatVideoData();//
        // NetworkAPICall(API.API_GET_SINGLE_CAT_VIDEO_DATA, show);
    }

    public void retryApis(String apiname) {
        switch (apiname) {
            case API.API_GET_MASTER_HIT:
                networkCallForMasterHit();
                break;

            case API.API_GET_SINGLE_CAT_VIDEO_DATA:
                networkcallForSingleCatVideoData();
                break;

            default:
                break;
        }

    }

    public void networkcallForSingleCatVideoData() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final Progress mprogress = new Progress(activity);
        mprogress.setCancelable(false);
        mprogress.show();
        SinglecatVideoDataApiInterface apiInterface = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apiInterface.getVideoListData(SharedPreference.getInstance().getLoggedInUser().getId(), tagId, lastPostId, "", ""
                , search);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (!((BaseABNavActivity) activity).searchView.isIconified()) {
//                    ((BaseABNavActivity) activity).searchView.clearFocus();
//                }
                if (getActivity() != null && getActivity().isFinishing() && isAdded()){
                    if (response.body() != null) {
                        mprogress.dismiss();
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            Log.e("TAG", " networkcallForSingleCatVideoData onResponse: " + jsonResponse);
                            if (jsonResponse.optBoolean(Const.STATUS)) {
                                JSONArray jsonArray = GenericUtils.getJsonArray(jsonResponse);

                                if (TextUtils.isEmpty(lastPostId)) {
                                    arrayList = new ArrayList<>();
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.optJSONObject(i);
                                    Video video = new Gson().fromJson(data.toString(), Video.class);
                                    arrayList.add(video);
                                }
                                Helper.getStorageInstance(getActivity()).addRecordStore("0", arrayList);
                                initVideoAdapter();
                            } else {
                                initVideoAdapter();

                                if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(getResources().getString(R.string.internet_error_message)))
                                    Helper.showErrorLayoutForNav(API.API_GET_SINGLE_CAT_VIDEO_DATA, activity, 1, 1);

                                if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(getString(R.string.something_went_wrong_string))) {
                                    Helper.showErrorLayoutForNav(API.API_GET_SINGLE_CAT_VIDEO_DATA, activity, 1, 2);
                                }
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showExceptionMsg(activity, t);

            }
        });
    }


    public void networkCallForMasterHit() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        SinglecatVideoDataApiInterface apis = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apis.getMasterFeedForUser(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            MasterFeedsHitResponse response1 = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterFeedsHitResponse.class);
                            SharedPreference.getInstance().setMasterHitData(response1);

                            tagsArrayListMain = getTagsForUser();
                            for (Tags tags : tagsArrayListMain) {
                                tagsArrayList.add(tags);
                                addFragment(tags);
                            }
                            if (getActivity() != null)
                                //((BaseABNavActivity) getActivity()).masterHitStatus = false;
                                ((HomeActivity) getActivity()).setMasterHitStatus(false);

                        } else {
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(getResources().getString(R.string.internet_error_message)))
                                Helper.showErrorLayoutForNav(API.API_GET_MASTER_HIT, activity, 1, 1);

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equals(getString(R.string.something_went_wrong_string))) {
                                Helper.showErrorLayoutForNav(API.API_GET_MASTER_HIT, activity, 1, 2);
                            }

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

    protected void initVideoAdapter() {
        if (arrayList != null && !arrayList.isEmpty()) {
            videoRV.setVisibility(View.VISIBLE);
            errorMessageTV.setVisibility(View.GONE);
            if (TextUtils.isEmpty(lastPostId)) {
                videoAdapter = new videoAdapter1(arrayList, activity, 0, null);
                videoRV.setAdapter(videoAdapter);
            } else {
                videoAdapter.notifyDataSetChanged();
            }
        } else {
            videoRV.setVisibility(View.GONE);
            errorMessageTV.setVisibility(View.VISIBLE);
            errorMessageTV.setText((TextUtils.isEmpty(errorMessage) ? getString(R.string.no_videos_found) : errorMessage));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof HomeActivity) {
            menu = ((HomeActivity) activity).getMyMenu();
            if (menu!=null){
                menu.findItem(R.id.app_bar_search).setVisible(true);
            }
            ((HomeActivity) activity).floatingActionButton.setVisibility(View.VISIBLE);
            ((HomeActivity) activity).floatingActionButton.setImageResource(R.drawable.ic_outline_filter_alt_24);
        }

        if (VideoFragmentViewPager.IS_LIKE_UPDATE) {
            if (recyclerViewLayout.getVisibility() == View.VISIBLE) {
                videoAdapter.itemChangedAtVideoId(SharedPreference.getInstance().getLikeUpdate());

            } else {
                if (((VideoFragmentViewPager) mFragmentList.get(currentPosition)).adapter != null)
                    ((VideoFragmentViewPager) mFragmentList.get(currentPosition)).adapter.itemChangedAtVideoId(SharedPreference.getInstance().getLikeUpdate());
                ((VideoFragmentViewPager) mFragmentList.get(currentPosition)).changeSliderData(SharedPreference.getInstance().getLikeUpdate());
            }
            VideoFragmentViewPager.IS_LIKE_UPDATE = false;
        }

        if (VideoFragmentViewPager.IS_VIEW_UPDATE) {
            if (recyclerViewLayout.getVisibility() == View.VISIBLE) {
                videoAdapter.itemChangedAtVideoId(SharedPreference.getInstance().getViewUpdate());

            } else {
                if (((VideoFragmentViewPager) mFragmentList.get(currentPosition)).adapter != null)
                    ((VideoFragmentViewPager) mFragmentList.get(currentPosition)).adapter.itemChangedAtVideoId(SharedPreference.getInstance().getViewUpdate());
                ((VideoFragmentViewPager) mFragmentList.get(currentPosition)).changeSliderData(SharedPreference.getInstance().getViewUpdate());
            }
            VideoFragmentViewPager.IS_VIEW_UPDATE = false;
        }

        if (VideoFragmentViewPager.IS_COMMENT_UPDATE) {
            if (recyclerViewLayout.getVisibility() == View.VISIBLE) {
                videoAdapter.itemChangedAtVideoId(SharedPreference.getInstance().getCommentUpdate());
            } else {
                if (((VideoFragmentViewPager) mFragmentList.get(currentPosition)).adapter != null)
                    ((VideoFragmentViewPager) mFragmentList.get(currentPosition)).adapter.itemChangedAtVideoId(SharedPreference.getInstance().getCommentUpdate());
                ((VideoFragmentViewPager) mFragmentList.get(currentPosition)).changeSliderData(SharedPreference.getInstance().getCommentUpdate());
            }
            VideoFragmentViewPager.IS_COMMENT_UPDATE = false;
        }
    }


    public void getErrorCallback(String apiType, int type) {
        if (type == 1)
            Helper.showErrorLayoutForNav(apiType, activity, 1, 1);

        if (type == 2) {
            Helper.showErrorLayoutForNav(apiType, activity, 1, 2);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (((HomeActivity) Objects.requireNonNull(getActivity())).popupWindow != null)
//            ((BaseABNavActivity) getActivity()).popupWindow.dismiss();

    }

    class Pageadapter extends FragmentStatePagerAdapter {
        int count;

        public Pageadapter(FragmentManager fm, int count) {
            super(fm);
            this.count = count;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            try {
                return mFragmentTitleList.get(position);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public int getCount() {
            return count;
        }
    }

    private void setTagOrHitApi() {
        if (tagsArrayListMain != null && !tagsArrayListMain.isEmpty()) {
            for (Tags tags : tagsArrayListMain) {
                tagsArrayList.add(tags);
                addFragment(tags);
            }
        } else if (((HomeActivity) requireActivity()).getMasterHitStatus()) {
            networkCallForMasterHit(); // NetworkAPICall(API.API_GET_MASTER_HIT, false);
        }
    }

    private void manageBottomAndFabVisibility() {

//        if (((BaseABNavActivity) activity).searchView.isIconified()) {
//            BaseABNavActivity.bottomPanelRL.setVisibility(View.VISIBLE);
//        } else {
//            BaseABNavActivity.bottomPanelRL.setVisibility(View.GONE);
//        }
//
//        BaseABNavActivity.postFAB.setVisibility(View.GONE);
    }

    public ArrayList<Tags> getTagsForUser() {

        ArrayList<Tags> tagsArrayList = new ArrayList<>();
        User user = SharedPreference.getInstance().getLoggedInUser();
        String moderatorSelectedStream = SharedPreference.getInstance().getString(Const.MODERATOR_SELECTED_STREAM);
        MasterFeedsHitResponse masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();

        if (masterFeedsHitResponse != null && !GenericUtils.isListEmpty(masterFeedsHitResponse.getAll_tags())) {
            for (Tags tag : masterFeedsHitResponse.getAll_tags()) {
                if (!TextUtils.isEmpty(user.getIs_moderate()) && user.getIs_moderate().equalsIgnoreCase("1")) {// this means user is moderator
                    if (TextUtils.isEmpty(moderatorSelectedStream) || moderatorSelectedStream.equals("1")) {
                        if (tag.getMaster_id().equalsIgnoreCase("1")) {
                            tagsArrayList.add(tag);
                        }
                    } else if (!TextUtils.isEmpty(moderatorSelectedStream)) {
                        if (tag.getMaster_id().equalsIgnoreCase(moderatorSelectedStream)) {
                            tagsArrayList.add(tag);
                        }
                    }
                } else {
                    if (user.getUser_registration_info() != null && !TextUtils.isEmpty(user.getUser_registration_info().getMaster_id()) && !TextUtils.isEmpty(tag.getMaster_id())
                            && tag.getMaster_id().equalsIgnoreCase(user.getUser_registration_info().getMaster_id())) {
                        tagsArrayList.add(tag);
                    }
                }
            }
        }
        return tagsArrayList;
    }

    public void openFilterMenu() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, R.layout.single_row_spinner_item);
        arrayAdapter.add("Sort By Date");
        arrayAdapter.add("Sort By Views");
        arrayAdapter.add("Sort By Likes");

        builderSingle.setAdapter(arrayAdapter, ((DialogInterface dialog, int which) -> {
            String strName = arrayAdapter.getItem(which);

            switch (Objects.requireNonNull(strName)) {
                case "Sort By Views":
                    sortBy = "views";
                    break;
                case "Sort By Likes":
                    sortBy = "likes";
                    break;
                case "Sort By Date":
                    sortBy = "time";
                    break;
                default:
                    sortBy = "";
                    break;

            }
            dialog.dismiss();
            setFilteronFragment();
        }));
        builderSingle.show();
    }

    public void setFilteronFragment() {
        ((VideoFragmentViewPager) mFragmentList.get(currentPosition)).filteringSearch(sortBy);
    }

    public void addFragment(Tags tag) {
        Fragment fragment = VideoFragmentViewPager.newInstance(tag);
        ((VideoFragmentViewPager) fragment).activity = activity;

        mFragmentList.add(fragment);
        mFragmentTitleList.add(tag.getText());
    }

    public void searchVideoList(int type) {

        if (type == 0) {
            isSearching = true;
            arrayList = new ArrayList<>();
            sortBy = "";
            lastPostId = "";
            firstVisibleItem = 0;
            previousTotalItemCount = 0;
            visibleItemCount = 0;
            search = SharedPreference.getInstance().getString(Constants.SharedPref.SEARCHED_QUERY);

            recyclerViewLayout.setVisibility(View.VISIBLE);
            viewPagerLayout.setVisibility(View.GONE);

            refreshVideoList(true);
        } else {
            isSearching = false;
            recyclerViewLayout.setVisibility(View.GONE);
            viewPagerLayout.setVisibility(View.VISIBLE);
        }
    }

}
