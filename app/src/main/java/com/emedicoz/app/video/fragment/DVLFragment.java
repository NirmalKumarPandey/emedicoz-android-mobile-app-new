package com.emedicoz.app.video.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavHostController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.Tags;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.modelo.Video;
import com.emedicoz.app.modelo.dvl.DVLTopic;
import com.emedicoz.app.modelo.dvl.Desk;
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
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DVLFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "DVLFragment";
    public static boolean free = false;
    public static boolean IS_BUY_CLICKED = false;
    ViewPager tabAnimViewPager;
    ArrayList<Fragment> mFragmentList = new ArrayList<>();
    public int currentPosition = 0;
    Pageadapter pageAdapter;
    public boolean isSearching = false;
    LinearLayout tabContainer;
    TextView premiumVideos;
    TextView freeVideos;
    ArrayList<Desk> deskArrayList;
    Activity activity;
    videoAdapter1 videoAdapter;
    LinearLayout gotoLiveVideo;
    String search = "";
    String lastPostId = "";
    String tagId = "0";
    ArrayList<Video> arrayList = new ArrayList<>();
    ArrayList<Video> sliderArrayList = new ArrayList<>();
    ArrayList<Tags> tagsArrayListMain;
    RelativeLayout mainLL;
    LinearLayout viewPagerLL;
    ImageView imageView;
    FrameLayout flipperFL;
    HashMap<Integer, ArrayList<DVLTopic>> hashMap;
    private TextView textViewDVL;
    private RecyclerView videoRV;
    private ArrayList<String> mFragmentTitleList = new ArrayList<>();
    private ArrayList<Tags> tagsArrayList;
    private MasterFeedsHitResponse masterHitResponse;

    public static DVLFragment newInstance() {
        return new DVLFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dams_video_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deskArrayList = new ArrayList<>();
        arrayList = new ArrayList<>();
        sliderArrayList = new ArrayList<>();
        hashMap = new HashMap<>();
        free = false;
        checkFilterIcon();
        initView(view);
    }

    private void initView(View view) {
        ((BaseABNavActivity) activity).searchView.setVisibility(View.GONE);
        tabContainer = view.findViewById(R.id.tab_container);
        premiumVideos = view.findViewById(R.id.premiumVideos);
        freeVideos = view.findViewById(R.id.freeVideos);
        viewPagerLL = view.findViewById(R.id.viewPagerLL);
        mainLL = view.findViewById(R.id.mainLL);
        gotoLiveVideo = view.findViewById(R.id.gotoLiveVideo);
        gotoLiveVideo.setOnClickListener(this);
        textViewDVL = view.findViewById(R.id.textViewDVL);
        flipperFL = view.findViewById(R.id.flipperFL);
        imageView = view.findViewById(R.id.video_image);

        videoRV = view.findViewById(R.id.videoRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        videoRV.setLayoutManager(layoutManager);

        TabLayout tabs = view.findViewById(R.id.tabs);
        tabAnimViewPager = view.findViewById(R.id.tabanim_viewpager);

        // getting offline data if it is pre-saved
        tagsArrayListMain = getTagsForUser();
        tagsArrayList = new ArrayList<>();

        Tags tag = new Tags();
        tag.setId("0");
        tag.setText(activity.getResources().getString(R.string.all));

        addFragment(tag);
        tagsArrayList.add(tag);

        setTagOrHitApi();

        pageAdapter = new Pageadapter(getChildFragmentManager(), tagsArrayList.size());
        tabAnimViewPager.setAdapter(pageAdapter);
        tabAnimViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // can be used when required
            }
        });
        tabs.setupWithViewPager(tabAnimViewPager);
        masterHitResponse = SharedPreference.getInstance().getMasterHitResponse();
//        masterHitResponse.setShow_premium_videos("1");
        if (masterHitResponse == null)
            masterHitResponse = new MasterFeedsHitResponse();
        if (masterHitResponse.getShow_premium_videos() != null
                && masterHitResponse.getShow_premium_videos().equalsIgnoreCase("0")) {
            premiumVideos.setVisibility(View.GONE);
            tabContainer.setVisibility(View.GONE);
        } else {
            tabContainer.setVisibility(View.VISIBLE);
            premiumVideos.setVisibility(View.VISIBLE);
            premiumVideos.setText(!GenericUtils.isEmpty(masterHitResponse.getPremium_videos_title()) ? masterHitResponse.getPremium_videos_title()
                    : getString(R.string.premium_videos));
        }
        if (masterHitResponse.getShow_live_course() != null && masterHitResponse.getShow_live_course().equalsIgnoreCase("0")) {
            gotoLiveVideo.setVisibility(View.GONE);
        } else {
            gotoLiveVideo.setVisibility(View.VISIBLE);
        }

        bindControls();
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

    private void bindControls() {
        premiumVideos.setOnClickListener(this);
        freeVideos.setOnClickListener(this);
        if (masterHitResponse.getShow_premium_videos() != null
                && masterHitResponse.getShow_premium_videos().equalsIgnoreCase("0")) {
            loadFreeVideosFragment();
        } else {
            loadPremiumFragment();
        }
    }

    private void loadPremiumFragment() {

        ((BaseABNavActivity) activity).searchView.setVisibility(View.GONE);
        premiumVideos.setBackground(ContextCompat.getDrawable(activity, R.drawable.drawable_dvl_premium));
        premiumVideos.setTextColor(Color.parseColor("#fec80e"));
        freeVideos.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
        freeVideos.setTextColor(ContextCompat.getColor(activity, R.color.black));
        viewPagerLL.setVisibility(View.GONE);
        textViewDVL.setText(!GenericUtils.isEmpty(masterHitResponse.getPremium_videos_title()) ? masterHitResponse.getPremium_videos_title()
                : getString(R.string.premium_videos));
        mainLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.drawable_gradient_dvl));
        free = false;
        checkFilterIcon();
        replaceFragment(PremiumVideosFragment.newInstance());
    }

    private void loadFreeVideosFragment() {

        ((BaseABNavActivity) activity).searchView.setVisibility(View.VISIBLE);
        freeVideos.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_btn));
        freeVideos.setTextColor(ContextCompat.getColor(activity, R.color.white));
        premiumVideos.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
        premiumVideos.setTextColor(ContextCompat.getColor(activity, R.color.black));

        textViewDVL.setText(activity.getResources().getString(R.string.free_videos));
        mainLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.drawable_blue_dvl));
        free = true;
        checkFilterIcon();
        replaceFragment(VideoFragment.newInstance());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.premiumVideos:
                loadPremiumFragment();
                break;
            case R.id.gotoLiveVideo:
                ((BaseABNavActivity) Objects.requireNonNull(getActivity())).liveClassesLL.performClick();
                break;
            case R.id.freeVideos:
                loadFreeVideosFragment();
                break;
            default:
                break;
        }
    }

    public void checkFilterIcon() {
        if (free) {
            BaseABNavActivity.postFAB.setVisibility(View.VISIBLE);
            BaseABNavActivity.postFAB.setImageResource(R.mipmap.filter);
        } else
            BaseABNavActivity.postFAB.setVisibility(View.GONE);
    }

    public void networkCallForSingleCatVideoData() {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        final Progress mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        mProgress.show();
        SinglecatVideoDataApiInterface apiInterface = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apiInterface.getVideoListData(SharedPreference.getInstance().getLoggedInUser().getId(), tagId, lastPostId, "", ""
                , search);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!((BaseABNavActivity) activity).searchView.isIconified()) {
                    ((BaseABNavActivity) activity).searchView.clearFocus();
                }
                if (response.body() != null) {
                    mProgress.dismiss();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(TAG, " networkcallForSingleCatVideoData onResponse: " + jsonResponse);
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            JSONArray jsonArray = GenericUtils.getJsonArray(jsonResponse);

                            if (TextUtils.isEmpty(lastPostId)) {
                                arrayList = new ArrayList<>();
                                sliderArrayList = new ArrayList<>();
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
//                            Helper.showErrorLayoutForNav(API.API_GET_SINGLE_CAT_VIDEO_DATA, activity, 1, 2);
//                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));

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

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showExceptionMsg(activity, t);

            }
        });
    }

    private void initVideoAdapter() {
        if (!GenericUtils.isListEmpty(arrayList)) {
            if (TextUtils.isEmpty(lastPostId)) {
                videoAdapter = new videoAdapter1(arrayList, activity, 0, null);
                videoRV.setAdapter(videoAdapter);
            } else {
                videoAdapter.notifyDataSetChanged();
            }
        }
    }

    public ArrayList<Tags> getTagsForUser() {

        ArrayList<Tags> tagsList = new ArrayList<>();
        User user = SharedPreference.getInstance().getLoggedInUser();
        String moderatorSelectedStream = SharedPreference.getInstance().getString(Const.MODERATOR_SELECTED_STREAM);
        MasterFeedsHitResponse masterFeedsHitResponse = SharedPreference.getInstance().getMasterHitResponse();

        if (masterFeedsHitResponse != null && !GenericUtils.isListEmpty(masterFeedsHitResponse.getAll_tags())) {
            for (Tags tag : masterFeedsHitResponse.getAll_tags()) {
                if (!TextUtils.isEmpty(user.getIs_moderate()) && user.getIs_moderate().equalsIgnoreCase("1")) {// this means user is moderator
                    if (TextUtils.isEmpty(moderatorSelectedStream) || moderatorSelectedStream.equals("1")) {
                        if (tag.getMaster_id().equalsIgnoreCase("1")) {
                            tagsList.add(tag);
                        }
                    } else if (!TextUtils.isEmpty(moderatorSelectedStream) && tag.getMaster_id().equalsIgnoreCase(moderatorSelectedStream)) {
                        tagsList.add(tag);
                    }
                } else {
                    if (user.getUser_registration_info() != null && !TextUtils.isEmpty(user.getUser_registration_info().getMaster_id()) && !TextUtils.isEmpty(tag.getMaster_id())
                            && tag.getMaster_id().equalsIgnoreCase(user.getUser_registration_info().getMaster_id())) {
                        tagsList.add(tag);
                    }
                }
            }
        }
        return tagsList;
    }

    public void openFilterMenu() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fl_container);
        if (fragment instanceof VideoFragment) {
            Log.e(TAG, "getFragment: Free Videos Fragment");
            ((VideoFragment) fragment).openFilterMenu();
        }
    }


    public void addFragment(Tags tag) {
        Fragment fragment = VideoFragmentViewPager.newInstance(tag);
        ((VideoFragmentViewPager) fragment).activity = activity;

        mFragmentList.add(fragment);
        mFragmentTitleList.add(tag.getText());
    }

    public void searchVideoList(int type) {
        Fragment fragment = ((HomeActivity) getContext()).getCurrentFragment();
        //Fragment fragment = getChildFragmentManager().findFragmentById(R.id.nav_host_fragment);
        //Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fl_container);
        if (fragment instanceof VideoFragment) {
            Log.e(TAG, "getFragment: Free Videos Fragment");
            ((VideoFragment) fragment).searchVideoList(type);
        }
    }

    public void retryApis(String apiname) {
        switch (apiname) {
            case API.API_GET_MASTER_HIT:
                networkCallForMasterHit();
                break;

            case API.API_GET_SINGLE_CAT_VIDEO_DATA:
                networkCallForSingleCatVideoData();
                break;

            default:
                break;
        }
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
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(TAG, " networkCallForMasterHit onResponse: " + jsonResponse);
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            MasterFeedsHitResponse response1 = new Gson().fromJson(GenericUtils.getJsonObject(jsonResponse).toString(), MasterFeedsHitResponse.class);
                            SharedPreference.getInstance().setMasterHitData(response1);

                            tagsArrayListMain = getTagsForUser();
                            for (Tags tags : tagsArrayListMain) {
                                tagsArrayList.add(tags);
                                addFragment(tags);
                            }
                            ((BaseABNavActivity) activity).masterHitStatus = false;

                        } else {
                            Helper.showErrorLayoutForNav(API.API_GET_MASTER_HIT, activity, 1, 1);
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

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: onResume is called");
    }


    @Override
    public void onStop() {
        super.onStop();
        if (((BaseABNavActivity) Objects.requireNonNull(getActivity())).popupWindow != null)
            ((BaseABNavActivity) getActivity()).popupWindow.dismiss();

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

    public void replaceFragment(Fragment fragment) {
        try {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

}
