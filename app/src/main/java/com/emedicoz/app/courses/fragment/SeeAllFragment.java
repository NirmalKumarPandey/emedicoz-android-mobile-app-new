package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CourseCategory;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.templateAdapters.RecordedCourseTrendingAdapter;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;

public class SeeAllFragment extends Fragment implements MyNetworkCall.MyNetworkCallBack {

    Activity activity;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    String categoryId;
    ArrayList<Course> courseArrayList;
    String fragType;
    ArrayList<String> seeallStatus;
    CourseCategory courseCategory;
    private static final String TAG = "SeeAllFragment";
    MyNetworkCall myNetworkCall;
    private boolean isLiveCourse;

    public static SeeAllFragment newInstance(String fragType, String categoryId, CourseCategory courseCategory, boolean isLiveCourse) {
        SeeAllFragment fragment = new SeeAllFragment();
        Bundle args = new Bundle();
        args.putString(Const.FRAG_TYPE, fragType);
        args.putString(Const.CATEGORY_ID, categoryId);
        args.putBoolean(Const.LIVE_CLASSES, isLiveCourse);
        args.putSerializable(Const.COURSE_CATEGORY, courseCategory);
        fragment.setArguments(args);
        return fragment;
    }

    public static SeeAllFragment newInstance(String fragType, List<Course> courseArrayList, boolean isLiveCourse) {
        SeeAllFragment fragment = new SeeAllFragment();
        Bundle args = new Bundle();
        args.putString(Const.FRAG_TYPE, fragType);
        args.putBoolean(Const.LIVE_CLASSES, isLiveCourse);
        args.putSerializable(Const.COURSE_LIST, (Serializable) courseArrayList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            fragType = getArguments().getString(Const.FRAG_TYPE);
            isLiveCourse = getArguments().getBoolean(Const.LIVE_CLASSES);
            categoryId = getArguments().getString(Const.CATEGORY_ID);
            courseCategory = (CourseCategory) getArguments().getSerializable(Const.COURSE_CATEGORY);
            courseArrayList = (ArrayList<Course>) getArguments().getSerializable(Const.COURSE_LIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_e_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seeallStatus = new ArrayList<>();
        initView(view);
    }

    private void initView(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        recyclerView = view.findViewById(R.id.eBookRV);
        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (fragType.equals(Const.RECENTLY_VIEWED_COURSE) || fragType.equals(Const.MORE_COURSE)) {
            setAdapter();
        } else
            networkCallForAllCatData();
    }

    public void networkCallForAllCatData() { //getallcatdata
        if (GenericUtils.isEmpty(categoryId)) {
            categoryId = courseCategory.getId();
        }
        if (isLiveCourse)
            myNetworkCall.NetworkAPICall(API.API_GET_SEE_ALL_BY_CATEGORY_LIVE_CLASS, true);
        else
            myNetworkCall.NetworkAPICall(API.API_GET_SEE_ALL_BY_CATEGORY, true);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        params.put(Const.CAT_ID, categoryId);
        params.put("keyword", eMedicozApp.getInstance().searchedKeyword == null ? "" : eMedicozApp.getInstance().searchedKeyword);
        params.put("filter_type", eMedicozApp.getInstance().filterType == null ? "" : eMedicozApp.getInstance().filterType);
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONObject data = GenericUtils.getJsonObject(jsonObject);
        JSONArray dataArray = data.optJSONArray(Const.COURSE_LIST);
        courseArrayList = new ArrayList<>();
        for (int i = 0; i < Objects.requireNonNull(dataArray).length(); i++) {
            Course course = new Gson().fromJson(dataArray.opt(i).toString(), Course.class);
            if (course.getMrp().equals("0")) {
                course.setCalMrp("Free");
            } else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                course.setIs_dams(true);
                if (course.getFor_dams().equals("0")) {
                    course.setCalMrp("Free");
                } else {
                    if (course.getMrp().equals(course.getFor_dams())) {
                        course.setCalMrp(String.format("%s %s", Helper.getCurrencySymbol(),
                                Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                    } else {
                        course.setDiscounted(true);
                        course.setCalMrp(Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(course.getMrp()) + " " +
                                Helper.calculatePriceBasedOnCurrency(course.getFor_dams()));
                    }
                }
            } else {
                course.setIs_dams(false);
                if (course.getNon_dams().equals("0")) {
                    course.setCalMrp("Free");
                } else {
                    if (course.getMrp().equals(course.getNon_dams())) {
                        course.setCalMrp(String.format("%s %s", Helper.getCurrencySymbol(),
                                Helper.calculatePriceBasedOnCurrency(course.getMrp())));
                    } else {
                        course.setDiscounted(true);
                        course.setCalMrp(Helper.getCurrencySymbol() + " " + Helper.calculatePriceBasedOnCurrency(course.getMrp()) + " " +
                                Helper.calculatePriceBasedOnCurrency(course.getNon_dams()));
                    }
                }
            }

            if (course.getCalMrp().equalsIgnoreCase("free") || course.isIs_purchased())
                course.setIsFreeTrial(false);
            courseArrayList.add(course);
        }

        saveDataAndSetAdapter();
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        if (jsonString != null && jsonString.contains("not found"))
            Helper.showErrorLayoutForNoNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 0);
        else if (jsonString.equals(activity.getString(R.string.internet_error_message)))
            Helper.showErrorLayoutForNoNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 1);
        else
            Helper.showErrorLayoutForNoNav(API.API_GET_ALL_CATEGORY_DATA, activity, 1, 2);
    }

    private void saveDataAndSetAdapter() {
        if (!seeallStatus.contains(categoryId)) {
            seeallStatus.add(categoryId);
            Helper.getStorageInstance(activity).addRecordStore(Const.COURSESEEALL_IDS, seeallStatus);
        }

        Helper.getStorageInstance(activity).addRecordStore(categoryId, courseArrayList);

        if (((CourseActivity) activity).courseSeeAll) {
            setAdapter();
        }
        ((CourseActivity) activity).courseSeeAll = false;
    }

    private void setAdapter() {
//            CourseListAdapter adapter = new CourseListAdapter(activity, courseArrayList, fragType);
        RecordedCourseTrendingAdapter adapter = new RecordedCourseTrendingAdapter(courseArrayList, activity, false, false, Const.ALLCOURSES);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
