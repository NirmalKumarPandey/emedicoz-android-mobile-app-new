package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.courses.adapter.ViewPagerAdapter;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.dialog.QbankAppRating;
import com.emedicoz.app.dialog.StudyTestSeriesDialog;
import com.emedicoz.app.modelo.courses.Basic;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.TestQuizPojo;
import com.emedicoz.app.modelo.courses.TestSeries;
import com.emedicoz.app.rating.GetQbankRating;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTestFragment extends Fragment {

    private static final String TAG = "NewTestFragment";

    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList<TestSeries> activeTestList;
    String isPurchased = "1";
    LinearLayout noDataLL;
    ImageView noDataImage;
    TextView noDataText;
    ArrayList<String> subjectId;
    ArrayList<TestSeries> list1 = new ArrayList<>();
    ArrayList<TestSeries> list2 = new ArrayList<>();
    ArrayList<TestSeries> list3 = new ArrayList<>();
    ArrayList<TestSeries> list4 = new ArrayList<>();
    ViewPagerAdapter adapter;
    Activity activity;
    String courseId = "";
    private TestQuizPojo testQuizPojo;
    private TextView noDataTV;
    private StudyTestSeriesDialog studyTestSeriesDialog;
    String test_status;


    public static NewTestFragment newInstance(String courseId) {
        NewTestFragment fragment = new NewTestFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.COURSE_ID, courseId);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            courseId = getArguments().getString(Const.COURSE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        studyTestSeriesDialog = new StudyTestSeriesDialog();
        test_status = SharedPreference.getInstance().getStudyTestStatus(Const.STUDY_TEST_STATUS, "");
        initViews(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (activity instanceof BaseABNavActivity)
            ((BaseABNavActivity) activity).toolbar.setBackgroundResource(R.color.dark_test);
        checkForNetworkCall();
        getTestRating();
    }

    public void checkForNetworkCall(){
        if (!isAdded()) {
            new Handler().postDelayed(this::networkCall, 500);
        } else
            networkCall();
    }

    private void networkCall() {
        Log.e(TAG, "networkCall: ");
        final Progress progress = new Progress(activity);
        progress.show();
        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.getStudyTestData(SharedPreference.getInstance().getLoggedInUser().getId(), courseId,SharedPreference.getInstance().getString(Constants.SharedPref.SEARCHED_QUERY));
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            progress.dismiss();
                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                            testQuizPojo = gson.fromJson(data.toString(), TestQuizPojo.class);
                            noDataLL.setVisibility(View.GONE);
                            extractData();
                            extractSubjectData();
                            setupViewPager(activeTestList, list1, list2, list3, list4);
                            tabLayout.setupWithViewPager(viewPager);
                            if (activity instanceof HomeActivity){
                                Fragment fragment = ((HomeActivity) activity).getCurrentFragment();
                                if (fragment instanceof StudyFragment && testQuizPojo != null) {
                                    ((StudyFragment) fragment).setTestSeriesData(testQuizPojo);
                                }
                            }
//                            if (activity instanceof BaseABNavActivity) {
//                                Fragment fragment = ((BaseABNavActivity) activity).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//                                if (fragment instanceof StudyFragment && testQuizPojo != null) {
//                                    ((StudyFragment) fragment).setTestSeriesData(testQuizPojo);
//                                }
//                            }
                        } else {
                            progress.dismiss();
                            tabLayout.setVisibility(View.GONE);
                            noDataTV.setVisibility(View.GONE);
                            noDataLL.setVisibility(View.VISIBLE);
                            noDataText.setText(R.string.no_test_found);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    progress.dismiss();
                    tabLayout.setVisibility(View.GONE);
                    noDataLL.setVisibility(View.VISIBLE);
                    noDataText.setText(R.string.no_test_found);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss();
                Helper.showExceptionMsg(activity, t);
            }
        });
    }

    private void extractData() {
        clearArrayList();
        for (int i = 0; i < testQuizPojo.getTest_series().length; i++) {
            TestSeries testSeries = testQuizPojo.getTest_series()[i];
            activeTestList.add(testSeries);
            for (int j = 0; j < testQuizPojo.getMasters().length; j++) {
                String title = testQuizPojo.getMasters()[j].getTest_type_title();
                if (testSeries.getTopic_name().equalsIgnoreCase(title)) {
                    if (j == 0)
                        list1.add(testSeries);

                    else if (j == 1)
                        list2.add(testSeries);

                    else if (j == 2)
                        list3.add(testSeries);

                    else if (j == 3)
                        list4.add(testSeries);
                }
            }
        }
    }

    private void clearArrayList() {
        if (!activeTestList.isEmpty()) {
            activeTestList.clear();
        }
        if (!list1.isEmpty()) {
            list1.clear();
        }
        if (!list2.isEmpty()) {
            list2.clear();
        }
        if (!list3.isEmpty()) {
            list3.clear();
        }
        if (!list4.isEmpty()) {
            list4.clear();
        }
    }

    private void extractSubjectData() {
        if (!GenericUtils.isListEmpty(subjectId)) {
            subjectId.clear();
        }
        for (int i = 0; i < testQuizPojo.getTest_series().length; i++) {
            if (!subjectId.contains(testQuizPojo.getTest_series()[i].getSubject_id())) {
                subjectId.add(testQuizPojo.getTest_series()[i].getSubject_id());
            }
        }
    }

    public SingleCourseData getData(Basic basic) {
        SingleCourseData singleCourseData = new SingleCourseData();
        singleCourseData.setCourse_type(basic.getCourse_type());
        singleCourseData.setFor_dams(basic.getFor_dams());
        singleCourseData.setNon_dams(basic.getNon_dams());
        singleCourseData.setMrp(basic.getMrp());
        singleCourseData.setId(basic.getId());
        singleCourseData.setCover_image(basic.getCover_image());
        singleCourseData.setTitle(basic.getTitle());
        singleCourseData.setLearner(basic.getLearner());
        singleCourseData.setRating(basic.getRating());
        singleCourseData.setGst_include(basic.getGst_include());
        singleCourseData.setIs_purchased(testQuizPojo.getIs_purchased());
        if (!GenericUtils.isEmpty(basic.getGst()))
            singleCourseData.setGst(basic.getGst());
        else
            singleCourseData.setGst("18");
        if (!GenericUtils.isEmpty(basic.getPoints_conversion_rate()))
            singleCourseData.setPoints_conversion_rate(basic.getPoints_conversion_rate());
        else
            singleCourseData.setPoints_conversion_rate("100");
        return singleCourseData;
    }

    private void setupViewPager(ArrayList<TestSeries> activeTestList, ArrayList<TestSeries> list1, ArrayList<TestSeries> list2, ArrayList<TestSeries> list3, ArrayList<TestSeries> list4) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        if (!activeTestList.isEmpty()) {
            noDataTV.setVisibility(View.GONE);
            noDataLL.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(activeTestList, courseId, getString(R.string.all_test), isPurchased, getData(testQuizPojo.getBasic_info())), getString(R.string.all_test));

            for (int i = 0; i < testQuizPojo.getMasters().length; i++) {

                if (i == 0) {
                    adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(list1, courseId, getString(R.string.others), isPurchased, getData(testQuizPojo.getBasic_info())), testQuizPojo.getMasters()[i].getTest_type_title());

                } else if (i == 1) {
                    adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(list2, courseId, getString(R.string.others), isPurchased, getData(testQuizPojo.getBasic_info())), testQuizPojo.getMasters()[i].getTest_type_title());

                } else if (i == 2) {
                    adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(list3, courseId, getString(R.string.others), isPurchased, getData(testQuizPojo.getBasic_info())), testQuizPojo.getMasters()[i].getTest_type_title());

                } else if (i == 3) {
                    adapter.addFragment(NewChildTestQuizCourseFrag.getInstance(list4, courseId, getString(R.string.others), isPurchased, getData(testQuizPojo.getBasic_info())), testQuizPojo.getMasters()[i].getTest_type_title());

                }
            }
        } else {
            tabLayout.setVisibility(View.GONE);
            noDataTV.setVisibility(View.GONE);
            noDataLL.setVisibility(View.VISIBLE);
            noDataText.setText(R.string.no_test_found);
        }
        viewPager.setAdapter(adapter);
    }

    private void initViews(View view) {
        activeTestList = new ArrayList<>();
        subjectId = new ArrayList<>();

        noDataTV = view.findViewById(R.id.noDataTV);
        RelativeLayout toolbar = view.findViewById(R.id.relativeToolbar);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewpager);
        noDataText = view.findViewById(R.id.noDataText);
        noDataImage = view.findViewById(R.id.noDataImage);
        noDataImage.setImageResource(R.mipmap.test_blank);
        noDataLL = view.findViewById(R.id.noDataLL);
        toolbar.setVisibility(View.GONE);
    }


    //check test series drating exit
    private void getTestRating(){
        if (!Helper.isConnected(getContext())) {
            Toast.makeText(getContext(), R.string.internet_error_message, Toast.LENGTH_SHORT).show();
        }
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        apiInterface.getQbankRating(SharedPreference.getInstance().getLoggedInUser().getId(), "apprating").enqueue(new Callback<GetQbankRating>() {
            @Override
            public void onResponse(Call<GetQbankRating> call, Response<GetQbankRating> response) {
                GetQbankRating getQbankRating = response.body();
                if (getQbankRating.getStatus().getStatuscode().equals("200")) {

                    if (getQbankRating.getData() != null) {
                        if (SharedPreference.getInstance().getLoggedInUser().getId().equals(getQbankRating.getData().getUser_id())) {
                            //qbankAppRating.dismiss();
                        }
                    } else {
                    }
                }
            }

            @Override
            public void onFailure(Call<GetQbankRating> call, Throwable t) {
                if (test_status.equals("true")){
                    studyTestSeriesDialog.show(getChildFragmentManager(), "");
                }else {
                }
            }
        });
    }
}
