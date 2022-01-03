package com.emedicoz.app.dailychallenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.dailychallenge.model.DCScoreCardResponse;
import com.emedicoz.app.modelo.PostFile;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyChallengeDashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyChallengeDashboard extends Fragment implements View.OnClickListener {

    Activity activity;
    TextView txtScorecard;
    TextView txtArchives;
    TextView txtBookmarks;
    TextView finishedTextMsg;
    LinearLayout layoutRoot;
    LinearLayout parentHeaderRedLL;
    LinearLayout parentHeaderGreenLL;
    ConstraintLayout layoutHeader;
    RelativeLayout aboutTestRL;
    RelativeLayout startBtnRL;
    RelativeLayout rankRL;
    TextView questionNoTv;
    TextView durationTimeTv;
    TextView subjectNameTv;
    TextView rankTV;
    TextView viewReportTV;
    private int bgSelector;
    private String testSeriesId;
    private String testSegmentId;
    String noOfQuestion;
    String time;
    String rank;
    String subject;
    ConstraintLayout layoutHeaderGreen;
    Progress mProgress;


    public DailyChallengeDashboard() {
        // Required empty public constructor
    }

    // Rename and change types and number of parameters
    public static DailyChallengeDashboard newInstance(PostFile postFile) {
        DailyChallengeDashboard fragment = new DailyChallengeDashboard();
        Bundle args = new Bundle();
        if (postFile != null) {
            args.putString(Constants.ResultExtras.TEST_SEGMENT_ID, postFile.getTest_segment_id());
            args.putString(Const.TEST_SERIES_ID, postFile.getFile_info());
            args.putString(Constants.DailyChallengeExtras.NO_OF_QUESTION, postFile.getNoOfQuestion());
            args.putString(Constants.DailyChallengeExtras.TIME, postFile.getTime());
            args.putString(Constants.DailyChallengeExtras.SUBJECT, postFile.getSubject());
            args.putString(Constants.DailyChallengeExtras.RANK, postFile.getRank());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_challenge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            testSegmentId = getArguments().getString(Constants.ResultExtras.TEST_SEGMENT_ID);
            testSeriesId = getArguments().getString(Const.TEST_SERIES_ID);
            noOfQuestion = getArguments().getString(Constants.DailyChallengeExtras.NO_OF_QUESTION);
            time = getArguments().getString(Constants.DailyChallengeExtras.TIME);
            rank = getArguments().getString(Constants.DailyChallengeExtras.RANK);
            subject = getArguments().getString(Constants.DailyChallengeExtras.SUBJECT);
            eMedicozApp.getInstance().rank = rank;
        }
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rank != null) {
            if (eMedicozApp.getInstance().rank != null)
                rankTV.setText(eMedicozApp.getInstance().rank);
            else
                rankTV.setText(rank);
        }
    }

    private void initView(View view) {
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);

        txtScorecard = view.findViewById(R.id.txt_scorecard);
        txtArchives = view.findViewById(R.id.txt_archieves);
        txtBookmarks = view.findViewById(R.id.txt_bookmarks);
        finishedTextMsg = view.findViewById(R.id.finishedTextMsg);
        parentHeaderRedLL = view.findViewById(R.id.parent_header_red_LL);
        parentHeaderGreenLL = view.findViewById(R.id.parent_header_green_LL);
        layoutRoot = view.findViewById(R.id.layout_root);
        layoutHeader = view.findViewById(R.id.layout_header);
        aboutTestRL = view.findViewById(R.id.about_test_RL);
        startBtnRL = view.findViewById(R.id.start_btn_RL);
        startBtnRL.setOnClickListener(this);
        rankRL = view.findViewById(R.id.rankRL);
        questionNoTv = view.findViewById(R.id.question_no_tv);
        durationTimeTv = view.findViewById(R.id.duration_time_tv);
        subjectNameTv = view.findViewById(R.id.subject_name_tv);
        rankTV = view.findViewById(R.id.rankTV);
        viewReportTV = view.findViewById(R.id.viewReportTV);
        layoutHeaderGreen = view.findViewById(R.id.layout_header_green);
        aboutTestRL.setOnClickListener(this);
        viewReportTV.setOnClickListener(this);
        txtScorecard.setOnClickListener(this);
        txtArchives.setOnClickListener(this);
        txtBookmarks.setOnClickListener(this);
        if (testSeriesId != null) {
            setData();
            new Handler().post(() -> txtScorecard.performClick());
        } else
            getBasicInfoOfDailyChallenge();
    }

    private void renderView() {

        if (GenericUtils.isEmpty(testSegmentId)) {
            parentHeaderRedLL.setVisibility(View.VISIBLE);
            parentHeaderGreenLL.setVisibility(View.GONE);
            layoutHeader.setBackgroundResource(R.color.dc_header_color_red);
            layoutRoot.setBackgroundResource(R.drawable.bg_daily_challenge_header);
            bgSelector = R.drawable.bg_daily_challenge_tab;
        } else {
            parentHeaderRedLL.setVisibility(View.GONE);
            parentHeaderGreenLL.setVisibility(View.VISIBLE);
            layoutHeaderGreen.setBackgroundResource(R.color.dc_header_color);
            layoutRoot.setBackgroundResource(R.drawable.bg_daily_challenge_header_green);
            bgSelector = R.drawable.bg_daily_challenge_tab_green;
        }
    }

    private void setData() {
        renderView();
        if (GenericUtils.isEmpty(rank))
            rank = "N/A";
        rankTV.setText(rank);
        if (noOfQuestion != null)
            questionNoTv.setText(String.format("No of Questions: %s", noOfQuestion));
        if (GenericUtils.isEmpty(time))
            durationTimeTv.setVisibility(View.GONE);
        else {
            durationTimeTv.setVisibility(View.VISIBLE);
            durationTimeTv.setText(String.format("Time: %s mins", time));
        }
        if (subject != null)
            subjectNameTv.setText(String.format("Subject: %s", subject));

    }

    private void getBasicInfoOfDailyChallenge() {
        if (activity.isFinishing()) return;

        mProgress.show();
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.getScoreBoardOfDailyChallenge(SharedPreference.getInstance().getLoggedInUser().getId(),
                getFormattedDateForAPI());
        Log.e("getScoreBoard: ", Const.USER_ID + "-" + SharedPreference.getInstance().getLoggedInUser().getId() + " " + Constants.Extras.DATE + "-" + getFormattedDateForAPI());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                renderView();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            DCScoreCardResponse dcScoreCardResponse = gson.fromJson(jsonObject, DCScoreCardResponse.class);
                            List<ResultTestSeries> resultTestSeriesList = dcScoreCardResponse.getData();
                            if (!GenericUtils.isListEmpty(resultTestSeriesList)) {

                                ResultTestSeries resultTestSeries = resultTestSeriesList.get(0);

                                testSegmentId = resultTestSeries.getTestSegmentId();
                                testSeriesId = resultTestSeries.getTestSeriesId();
                                noOfQuestion = resultTestSeries.getTotalQuestions();
                                time = resultTestSeries.getTotalTestSeriesTime();
                                rank = resultTestSeries.getUserRank();
                                subject = resultTestSeries.getSubjectName();
                                eMedicozApp.getInstance().rank = rank;

                                setData();
                            } else {
                                parentHeaderRedLL.setVisibility(View.INVISIBLE);
//                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            parentHeaderRedLL.setVisibility(View.INVISIBLE);
//                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new Handler().post(() -> txtScorecard.performClick());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_SINGLE_COURSE_INFO_RAW, activity, 1, 1);
            }
        });
    }


    private String getFormattedDateForAPI() {
        return new SimpleDateFormat("yyyy-MM-dd").format(eMedicozApp.getInstance().getDcCalendar().getTime());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_scorecard:
                txtScorecard.setBackgroundResource(bgSelector);
                txtArchives.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
                txtBookmarks.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
                txtScorecard.setTextColor(ContextCompat.getColor(activity, R.color.white));
                txtArchives.setTextColor(ContextCompat.getColor(activity, R.color.black));
                txtBookmarks.setTextColor(ContextCompat.getColor(activity, R.color.black));
                // getScoreBoardOfDailyChallenge();

                replaceFragment(DailyChallengeScoreCardFragment.newInstance(testSegmentId, testSeriesId));
                break;

            case R.id.txt_archieves:
                txtArchives.setBackgroundResource(bgSelector);
                txtScorecard.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
                txtBookmarks.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
                txtArchives.setTextColor(ContextCompat.getColor(activity, R.color.white));
                txtBookmarks.setTextColor(ContextCompat.getColor(activity, R.color.black));
                txtScorecard.setTextColor(ContextCompat.getColor(activity, R.color.black));
                replaceFragment(DailyChallengeArchives.newInstance(testSegmentId, testSeriesId));
                break;

            case R.id.txt_bookmarks:
                txtBookmarks.setBackgroundResource(bgSelector);
                txtScorecard.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
                txtArchives.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
                txtBookmarks.setTextColor(ContextCompat.getColor(activity, R.color.white));
                txtScorecard.setTextColor(ContextCompat.getColor(activity, R.color.black));
                txtArchives.setTextColor(ContextCompat.getColor(activity, R.color.black));

                replaceFragment(DailyChallengeBookmark.newInstance(testSegmentId, testSeriesId));
                break;

            case R.id.viewReportTV:
            case R.id.start_btn_RL:
                if (!GenericUtils.isEmpty(testSeriesId)) {
                    if (testSegmentId.isEmpty()) {
                        if (Helper.isConnected(activity)) {
                            Intent quizView = new Intent(activity, TestBaseActivity.class);
                            quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                            quizView.putExtra(Const.STATUS, false);
                            quizView.putExtra(Constants.Extras.DAILY, true);
                            SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.DAILY_CHALLENGE);
                            quizView.putExtra(Constants.Extras.OPEN_FROM, Constants.TestType.DAILY_CHALLENGE);
                            activity.startActivity(quizView);
                            try {
                                new Handler().post(() -> requireFragmentManager().popBackStack());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(activity, activity.getResources().getString(R.string.please_check_your_internet_connectivity), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Intent resultScreen = new Intent(activity, QuizActivity.class);
                        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                        resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                        activity.startActivity(resultScreen);
                    }
                }
            default:
        }
    }
/*

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 11) {
            testSegmentId = "";
            rank = "";

            renderView();
            if (rank != null)
                rankTV.setText(rank);
        }
    }
*/

    public void addFragment(Fragment fragment) {
        try {
            getChildFragmentManager().beginTransaction().add(R.id.container_frame, fragment)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment fragment) {
        try {
            getChildFragmentManager().beginTransaction().replace(R.id.container_frame, fragment)
                    .addToBackStack(null).commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        eMedicozApp.getInstance().rank = null;
        super.onPause();
    }
}