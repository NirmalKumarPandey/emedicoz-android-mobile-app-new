package com.emedicoz.app.courses.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.courses.adapter.CurriculumFileRecyclerAdapter;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.Curriculam;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.quiz.QuizModel;
import com.emedicoz.app.modelo.courses.quiz.Quiz_Basic_Info;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Curriculum extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static ImageView bannerimageIV, deleteIV;


    static Activity activity;
    public ArrayList<String> curriculumStatus = new ArrayList<>();
    boolean refreshStatus = false;
    SwipeRefreshLayout swipeCurriculum;
    String fragmentType;
    NestedScrollView scroll;
    CardView courseRatingCV;
    ArrayList<Curriculam> curriculamArrayList;
    SingleCourseData course;
    String frag_type = "", des = "";
    LinearLayout curriculumListLL, curriculumMainLL, mainLayout;
    TextView bannernameTV, coursenameTV, descriptionnameTV, courseDescriptionTV, curriculumTextTV, errorTV;
    Button seeAllB, faqB;
    int Course_Description_Length = 100;
    CurriculumFileRecyclerAdapter curriculumFileRecyclerAdapter;
    ClickableSpan readmoreclick, readlessclick;
    Progress mprogress;
    NonScrollRecyclerView curriculumExpListRV;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            boolean message = intent.getBooleanExtra(Constants.Extras.MESSAGE, false);
            if (message) {
                if (deleteIV.getVisibility() == View.GONE) deleteIV.setVisibility(View.VISIBLE);
            }
            Log.d("receiver", "Got message: " + message);
        }
    };

    public Curriculum() {
        // Required empty public constructor
    }

    public static Fragment newInstance(SingleCourseData singleCourseData) {
        Curriculum curr = new Curriculum();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.COURSE_DESC, singleCourseData);
        curr.setArguments(bundle);
        return curr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        fragmentType = "2";
        SharedPreference.getInstance().putString("FRAGMENT_TYPE", fragmentType);
        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
                new IntentFilter("dowloaded"));
        curriculamArrayList = new ArrayList<>();
        if (getArguments() != null) {
            frag_type = getArguments().getString(Const.FRAG_TYPE);
            course = (SingleCourseData) getArguments().getSerializable(Const.COURSE_DESC);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_curriculum, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);

        networkCallForListCurriculum();//NetworkAPICall(API.API_GET_FILE_LIST_CURRICULUM, true);
    }

    private void initViews(View view) {
        swipeCurriculum = view.findViewById(R.id.swipeSingleCurriculum);
        swipeCurriculum.setOnRefreshListener(this);
        swipeCurriculum.setColorSchemeResources(R.color.blue, R.color.colorAccent, R.color.colorPrimaryDark);
        scroll = view.findViewById(R.id.scroll);
        bannernameTV = view.findViewById(R.id.bannernameTV);
        coursenameTV = view.findViewById(R.id.nameTV);
        courseDescriptionTV = view.findViewById(R.id.descriptionTV);
        bannerimageIV = view.findViewById(R.id.bannerimageIV);
        curriculumListLL = view.findViewById(R.id.curriculumListLL);
        curriculumMainLL = view.findViewById(R.id.curriculumLay);
        curriculumTextTV = view.findViewById(R.id.curriculumTextTV);
        courseRatingCV = view.findViewById(R.id.courseRatingCV);
        descriptionnameTV = view.findViewById(R.id.courseDescriptionTV);
        seeAllB = view.findViewById(R.id.seeAllCurriculumBtn);
        faqB = view.findViewById(R.id.faqbtn);
        mainLayout = view.findViewById(R.id.mainLayout);
        errorTV = view.findViewById(R.id.errorTV);
        deleteIV = view.findViewById(R.id.deleteIV);
        curriculumExpListRV = view.findViewById(R.id.curriculumExpListLL);
        curriculumExpListRV.setVisibility(View.VISIBLE);

        if (courseRatingCV.getVisibility() == View.VISIBLE) {
            courseRatingCV.setVisibility(View.GONE);
            seeAllB.setVisibility(View.GONE);
            faqB.setVisibility(View.GONE);
        }
        deleteIV.setOnClickListener(this);
        ViewCompat.setNestedScrollingEnabled(scroll, false);
    }

    private void setAllDatatoView(ArrayList<Curriculam> curriculam) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mprogress.dismiss();
            }
        }, 5000);

        if (mainLayout.getVisibility() == View.GONE) mainLayout.setVisibility(View.VISIBLE);

        bannernameTV.setText(Helper.CapitalizeText(course.getTitle().trim()));
        coursenameTV.setText(Helper.CapitalizeText(course.getTitle().trim()));

        des = course.getDescription();
        if (des.length() > Course_Description_Length) {
            des = des.substring(0, Course_Description_Length) + "...";
            courseDescriptionTV.setText(des + HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>",HtmlCompat.FROM_HTML_MODE_LEGACY));
            readmoreclick = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    courseDescriptionTV.setText(String.format("%s %s", course.getDescription(), HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read Less</u></font>",HtmlCompat.FROM_HTML_MODE_LEGACY)));
                    Helper.makeLinks(courseDescriptionTV, "Read Less", readlessclick);
                }
            };

            readlessclick = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    courseDescriptionTV.setText(String.format("%s %s", des, HtmlCompat.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>",HtmlCompat.FROM_HTML_MODE_LEGACY)));
                    Helper.makeLinks(courseDescriptionTV, "Read More", readmoreclick);
                }
            };
            Helper.makeLinks(courseDescriptionTV, "Read More", readmoreclick);
        } else {
            courseDescriptionTV.setText(des);
        }

        if (activity != null) {
            Glide.with(activity)
                    .asBitmap()
                    .load(course.getDesc_header_image())
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.courses_blue))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                            bannerimageIV.setImageBitmap(result);
                        }
                    });
        } else {
            bannerimageIV.setImageResource(R.mipmap.courses_blue);
        }

        curriculumExpListRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        curriculumFileRecyclerAdapter = new CurriculumFileRecyclerAdapter(course, activity, curriculam);
        curriculumExpListRV.setAdapter(curriculumFileRecyclerAdapter);


    }

    private void showPopUp(QuizModel quiz) {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.popup_basicinfo_quiz, null, false);
        final Dialog quizBasicInfoDialog = new Dialog(activity);
        quizBasicInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        quizBasicInfoDialog.setCanceledOnTouchOutside(true);
        quizBasicInfoDialog.setContentView(v);
        quizBasicInfoDialog.show();

        TextView quizTitle, quizQuesNumTv, quizTimeTV, quizCorrectValue, quizWrongValue, quizTotalMarks;
        Button startQuiz;
        Quiz_Basic_Info basic_info = quiz.getBasic_info();

        quizTitle = v.findViewById(R.id.quizTitleTV);
        quizCorrectValue = v.findViewById(R.id.marksCorrectValueTV);
        quizWrongValue = v.findViewById(R.id.marksWrongValueTV);
        quizTotalMarks = v.findViewById(R.id.marksTextValueTV);
        quizQuesNumTv = v.findViewById(R.id.numQuesValueTV);
        quizTimeTV = v.findViewById(R.id.quizTimeValueTV);
        startQuiz = v.findViewById(R.id.startQuizBtn);

        quizTitle.setText(basic_info.getTest_series_name());
        quizCorrectValue.setText(basic_info.getMarks_per_question());
        quizWrongValue.setText(basic_info.getNegative_marking());
        quizQuesNumTv.setText(basic_info.getTotal_questions());
        quizTotalMarks.setText(basic_info.getTotal_marks());
        quizTimeTV.setText(basic_info.getTime_in_mins());

        startQuiz.setTag(quiz);
        startQuiz.setOnClickListener(this);

    }

    public void retryApis(String apiType) {
        switch (apiType) {
            case API.API_GET_FILE_LIST_CURRICULUM:
                networkCallForListCurriculum();
                break;
            case API.API_GET_COMPLETE_INFO_TEST_SERIES:
                networkCallForCompleteTestSeries();
                break;

            default:
                break;
        }
    }

    private void networkCallForListCurriculum() {
        mprogress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getFileListCurriculum(SharedPreference.getInstance().getLoggedInUser().getId()
                , course.getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            if (swipeCurriculum.isRefreshing())
                                swipeCurriculum.setRefreshing(false);
                            JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                            if (!curriculamArrayList.isEmpty()) curriculamArrayList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject dataObj = data.optJSONObject(i);
                                Curriculam curriculam = gson.fromJson(dataObj.toString(), Curriculam.class);
                                curriculamArrayList.add(curriculam);
                            }

                            if (!curriculumStatus.contains(course.getId())) {
                                curriculumStatus.add(course.getId());
                            }
                            setAllDatatoView(curriculamArrayList);

                        } else {
                            errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_FILE_LIST_CURRICULUM);
                            RetrofitResponse.getApiData(activity, API.API_GET_FILE_LIST_CURRICULUM);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_FILE_LIST_CURRICULUM, activity, 1, 1);
            }
        });
    }

    private void networkCallForCompleteTestSeries() {
        mprogress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getCompleteInfTestSeries(SharedPreference.getInstance().getLoggedInUser().getId(),
                "2");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();
                    mprogress.dismiss();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                        JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                        QuizModel quiz = gson.fromJson(data.toString(), QuizModel.class);
                        showPopUp(quiz);
                    } else {
                        errorCallBack(jsonResponse.optString(Constants.Extras.MESSAGE), API.API_GET_COMPLETE_INFO_TEST_SERIES);
                        RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mprogress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_COMPLETE_INFO_TEST_SERIES, activity, 1, 1);
            }
        });
    }

    public void errorCallBack(String jsonString, String apiType) {
        switch (apiType) {
            case API.API_GET_FILE_LIST_CURRICULUM:
                if (swipeCurriculum.isRefreshing()) {
                    swipeCurriculum.setRefreshing(false);
                    refreshStatus = false;
                }

                if (Helper.getStorageInstance(getActivity()).getRecordObject(String.valueOf(course.getId() + "_curriculum")) != null) {
                    curriculamArrayList = (ArrayList<Curriculam>) Helper.getStorageInstance(getActivity()).getRecordObject(String.valueOf(course.getId() + "_curriculum"));
                    setAllDatatoView(curriculamArrayList);

                } else {

                    if (jsonString.equalsIgnoreCase(getResources().getString(R.string.internet_error_message)))
                        Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);

                    if (jsonString.contains(getString(R.string.something_went_wrong_string)))
                        Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
                }
                break;
            case API.API_GET_COMPLETE_INFO_TEST_SERIES:
                if (jsonString.equalsIgnoreCase(getResources().getString(R.string.internet_error_message)))
                    Helper.showErrorLayoutForNoNav(apiType, activity, 1, 1);

                if (jsonString.contains(getString(R.string.something_went_wrong_string)))
                    Helper.showErrorLayoutForNoNav(apiType, activity, 1, 2);
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Curriculum resume", "is on");
        if (eMedicozDownloadManager.getOfflineDataStatusByCourse(course.getId(), activity))
            deleteIV.setVisibility(View.VISIBLE);
        else
            deleteIV.setVisibility(View.GONE);

        if (curriculamArrayList != null && curriculamArrayList.size() > 0 && SharedPreference.getInstance().getBoolean(Const.IS_STATE_CHANGE)) {
            SharedPreference.getInstance().putBoolean(Const.IS_STATE_CHANGE, false);
            try {
                curriculamArrayList.get(SharedPreference.getInstance().getInt(Const.CHAPTER_POS)).getFile_meta().get(SharedPreference.getInstance().getInt(Const.LAST_POS))
                        .setIs_paused(SharedPreference.getInstance().getString(Const.IS_COMPLETE));

                curriculumFileRecyclerAdapter.notifyDataSetChanged();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startQuizBtn:
                QuizModel quiz = (QuizModel) v.getTag();
                Intent quizActivity = new Intent(activity, QuizActivity.class);
                quizActivity.putExtra(Const.FRAG_TYPE, Const.TESTSERIES);
                quizActivity.putExtra(Const.TESTSERIES, quiz);
                activity.startActivity(quizActivity);

                break;

            case R.id.deleteIV:
                AlertDialog.Builder alertBuild = new AlertDialog.Builder(activity);
                alertBuild
                        .setTitle(activity.getString(R.string.app_name))
                        .setMessage(activity.getString(R.string.deleteCourse))
                        .setCancelable(true)
                        .setPositiveButton("ok", (dialog1, which) -> {
                                dialog1.dismiss();
                                if (eMedicozDownloadManager.removeOfflineDataByCourse(course.getId(), activity)) {
                                    networkCallForListCurriculum();//NetworkAPICall(API.API_GET_FILE_LIST_CURRICULUM, true);
                                    if (deleteIV.getVisibility() == View.VISIBLE)
                                        deleteIV.setVisibility(View.GONE);
                                }
                        })
                        .setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss());
                AlertDialog dialog = alertBuild.create();
                dialog.show();
                break;
        }
    }

    @Override
    public void onRefresh() {
        refreshStatus = true;
        networkCallForListCurriculum();//NetworkAPICall(API.API_GET_FILE_LIST_CURRICULUM, true);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
