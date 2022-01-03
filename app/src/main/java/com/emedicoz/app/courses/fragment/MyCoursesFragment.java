package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.MyCoursesPagerAdapter;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;

import static com.emedicoz.app.utilso.Helper.setCourseList;

public class MyCoursesFragment extends Fragment implements MyNetworkCall.MyNetworkCallBack {
    private static final String TAG = "MyCoursesFragment";
    Activity activity;
    String fragType;
    String courseType;
    ArrayList<Course> courseArrayList;
    ArrayList<String> list;
    TabLayout mainTab;
    ViewPager viewPager;
    MyNetworkCall myNetworkCall;
    TextView noData;

    public static MyCoursesFragment newInstance(String fragType) {
        MyCoursesFragment fragment = new MyCoursesFragment();
        Bundle args = new Bundle();
        args.putString(Const.FRAG_TYPE, fragType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: MyCoursesFragment");
        activity = getActivity();
        if (getArguments() != null) {
            fragType = getArguments().getString(Const.FRAG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_newbookmarks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = new ArrayList<>();
        initView(view);
    }

    private void initView(View view) {
        try {
            myNetworkCall = new MyNetworkCall(this, activity);
            mainTab = view.findViewById(R.id.mainTabLayout);
            viewPager = view.findViewById(R.id.pager);
            noData = view.findViewById(R.id.txv_nodata);
            mainTab.setTabMode(TabLayout.MODE_SCROLLABLE);

            myNetworkCall.NetworkAPICall(API.API_GET_MY_COURSE_DATA, true);
        }catch (Exception e){
            noData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        if (fragType.equalsIgnoreCase(Const.MYCOURSES)) {
            courseType = Constants.Type.COURSE_TYPE_MY_COURSE;
        } else if (fragType.equalsIgnoreCase(Const.MYTEST)) {
            courseType = Constants.Type.COURSE_TYPE_MY_TEST;
        } else {
            courseType = Constants.Type.COURSE_TYPE_OTHER;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        params.put(Const.COURSE_TYPE, courseType);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        if (this == null || !this.isVisible()) return;
        noData.setVisibility(View.GONE);

        Gson gson = new Gson();
        JSONObject data;
        data = jsonObject.getJSONObject(Const.DATA);
        JSONArray dataArray = data.getJSONArray(Const.COURSE_LIST);

        courseArrayList = new ArrayList<>();
        list.add(getString(R.string.all));
        for (int i = 0; i < Objects.requireNonNull(dataArray).length(); i++) {
            com.emedicoz.app.modelo.courses.Course coursesData;
            coursesData = gson.fromJson(dataArray.get(i).toString(), Course.class);

            courseArrayList.add(coursesData);

            if (!list.contains(coursesData.getSubject_title()))
                list.add(coursesData.getSubject_title());
        }
        Log.e("LISTTABS", list.toString());
        setCourseList(courseArrayList);
        MyCoursesPagerAdapter adapter = new MyCoursesPagerAdapter(getChildFragmentManager());
        adapter.addFragment(list);
        viewPager.setAdapter(adapter);
        mainTab.setupWithViewPager(viewPager);
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        noData.setVisibility(View.VISIBLE);
//        Helper.showErrorLayoutForNoNav(API.API_GET_MY_COURSE_DATA, activity, 1, 3);
    }
}
