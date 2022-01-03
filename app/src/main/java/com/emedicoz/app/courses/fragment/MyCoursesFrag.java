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
import androidx.viewpager2.widget.ViewPager2;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.adapter.MyCourseFragmentAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.LandingPageApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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

public class MyCoursesFrag extends Fragment{
    private static final String TAG = "MyCoursesFragment";
    Activity activity;
    private String fragType;
    private String courseType;
    private ArrayList<Course> courseArrayList;
    private ArrayList<String> list;
    private MyCourseFragmentAdapter adapter;
    TabLayout mainTab;
    ViewPager2 viewPager;
    TextView noData;
    private Progress mProgress;


    public static MyCoursesFrag newInstance(String fragType) {
        MyCoursesFrag fragment = new MyCoursesFrag();
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
        if (fragType.equalsIgnoreCase(Const.MYCOURSES)) {
            courseType = Constants.Type.COURSE_TYPE_MY_COURSE;
        } else if (fragType.equalsIgnoreCase(Const.MYTEST)) {
            courseType = Constants.Type.COURSE_TYPE_MY_TEST;
        } else {
            courseType = Constants.Type.COURSE_TYPE_OTHER;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_mycourse, container, false);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        list = new ArrayList<>();
        initView(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view) {
        try {
            mainTab = view.findViewById(R.id.mainTabLayout);
            viewPager = view.findViewById(R.id.pager);
            noData = view.findViewById(R.id.txv_nodata);

            getMyCourseData(); // myNetworkCall.NetworkAPICall(API.API_GET_MY_COURSE_DATA, true);
        }catch (Exception e){
            noData.setVisibility(View.VISIBLE);
        }
    }


    private void getMyCourseData() {

        mProgress.show();
        LandingPageApiInterface apiInterface = ApiClient.createService(LandingPageApiInterface.class);
        Call<JsonObject> response = apiInterface.getMyCourseData(SharedPreference.getInstance().getLoggedInUser().getId(), courseType);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.body() != null) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    JSONArray dataArray;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                            dataArray = GenericUtils.getJsonObject(jsonResponse).optJSONArray("course_list");
                            noData.setVisibility(View.GONE);
                            courseArrayList = new ArrayList<>();
                            adapter = new MyCourseFragmentAdapter(MyCoursesFrag.this);
                            list.add(getString(R.string.all));

                            for (int i = 0; i < Objects.requireNonNull(dataArray).length(); i++) {
                                Course coursesData;
                                coursesData = gson.fromJson(dataArray.get(i).toString(), Course.class);
                                courseArrayList.add(coursesData);
                            }
                            {
                                NewMyCourseFrag newMyCourseFragment = new NewMyCourseFrag();
                                Bundle args = new Bundle();
                                args.putString(Constants.Extras.NAME, getString(R.string.all));
                                args.putSerializable("Array_List", courseArrayList);
                                newMyCourseFragment.setArguments(args);
                                adapter.addFragment(newMyCourseFragment, getString(R.string.all));
                            }
                            courseArrayList = new ArrayList<>();
                            for (int i = 0; i < Objects.requireNonNull(dataArray).length(); i++) {
                                Course coursesData;
                                coursesData = gson.fromJson(dataArray.get(i).toString(), Course.class);

                                courseArrayList.add(coursesData);

                                if (!list.contains(coursesData.getSubject_title())) {
                                    list.add(coursesData.getSubject_title());
                                    NewMyCourseFrag newMyCourseFragment =  new NewMyCourseFrag();
                                    Bundle args = new Bundle();
                                    args.putString(Constants.Extras.NAME, coursesData.getSubject_title());
                                    args.putSerializable("Array_List", courseArrayList);
                                    newMyCourseFragment.setArguments(args);
                                    adapter.addFragment(newMyCourseFragment,coursesData.getSubject_title());
                                }
                            }
                            Log.e("LISTTABS", list.toString());
//                            setCourseList(courseArrayList);
//                            adapter.addFragment(list);
                            viewPager.setAdapter(adapter);
                            new TabLayoutMediator(mainTab, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                                @Override
                                public void onConfigureTab(TabLayout.Tab tab, int position) {
                                    // position of the current tab and that tab
                                    tab.setText(adapter.getItemTitle(position));
                                }
                            }).attach();


                        }else{
//                            String message = "No Data Found";
                            noData.setVisibility(View.VISIBLE);
//                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        }

                        mProgress.dismiss();
                    } catch (JSONException e) {
                        mProgress.dismiss();
                        noData.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                }else{
                    noData.setVisibility(View.VISIBLE);
//                    Toast.makeText(getContext(), "No Data Found ", Toast.LENGTH_LONG).show();
                }
                mProgress.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                mProgress.dismiss();
//                Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                noData.setVisibility(View.VISIBLE);
            }
        });
    }



}
