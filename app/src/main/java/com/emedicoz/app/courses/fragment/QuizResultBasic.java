package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.model.BasicInfo;
import com.emedicoz.app.testmodule.model.TestseriesBase;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class QuizResultBasic extends Fragment implements MyNetworkCall.MyNetworkCallBack {

    String testSegmentId = "";
    String testSeriesId = "";
    String totalQuestion = "";
    String type = "";
    String subjectName = "";
    Button startQuiz;
    ResultTestSeries resultTestSeries;
    TextView difficultyLevel;
    TextView createAndDeleteModule;
    TextView noOfQuestions;
    TextView selectedSubject;
    TextView completeDate;
    TextView bookmarkTV;
    LinearLayout topLL;
    TestseriesBase testseriesBase;
    RelativeLayout bookmarkRL;
    Activity activity;
    MyNetworkCall myNetworkCall;

    public static QuizResultBasic newInstance(String status, ResultTestSeries resultTestSeries, String type, String subjectName) {
        QuizResultBasic quizResultAwait = new QuizResultBasic();
        Bundle args = new Bundle();
        args.putString(Constants.ResultExtras.TEST_SEGMENT_ID, status);
        args.putString(Constants.Extras.TYPE, type);
        args.putString(Constants.Extras.SUBJECT_NAME, subjectName);
        args.putSerializable(Const.RESULT_SCREEN, resultTestSeries);
        quizResultAwait.setArguments(args);
        return quizResultAwait;
    }

    public static QuizResultBasic newInstance(String testSeriesId, ResultTestSeries resultTestSeries, TestseriesBase testseriesBase, String totalQuestion, String type, String subjectName) {
        QuizResultBasic quizResultAwait = new QuizResultBasic();
        Bundle args = new Bundle();
        args.putString(Const.TEST_SERIES_ID, testSeriesId);
        args.putString(Const.TOTAL_QUESTIONS, totalQuestion);
        args.putString(Constants.Extras.TYPE, type);
        args.putString(Constants.Extras.SUBJECT_NAME, subjectName);
        args.putSerializable(Const.RESULT_SCREEN, resultTestSeries);
        args.putSerializable(Const.TESTSERIESBASE, testseriesBase);
        quizResultAwait.setArguments(args);
        return quizResultAwait;
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
        return inflater.inflate(R.layout.start_qbank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            testSegmentId = getArguments().getString(Constants.ResultExtras.TEST_SEGMENT_ID);
            totalQuestion = getArguments().getString(Const.TOTAL_QUESTIONS);
            type = getArguments().getString(Constants.Extras.TYPE);
            testSeriesId = getArguments().getString(Const.TEST_SERIES_ID);
            testseriesBase = (TestseriesBase) getArguments().getSerializable(Const.TESTSERIESBASE);
            subjectName = getArguments().getString(Constants.Extras.SUBJECT_NAME);
        }
        myNetworkCall = new MyNetworkCall(this, activity);
        startQuiz = view.findViewById(R.id.startQuiz);
        difficultyLevel = view.findViewById(R.id.difficultyLevel);
        createAndDeleteModule = view.findViewById(R.id.createAndDeleteModule);
        noOfQuestions = view.findViewById(R.id.noOfQuestions);
        topLL = view.findViewById(R.id.topLL);
        bookmarkRL = view.findViewById(R.id.bookmarkRL);
        selectedSubject = view.findViewById(R.id.selectedSubject);
        completeDate = view.findViewById(R.id.completeDate);
        bookmarkTV = view.findViewById(R.id.bookmark);
        topLL.setVisibility(View.GONE);
        createAndDeleteModule.setVisibility(View.GONE);
        myNetworkCall.NetworkAPICall(API.API_GET_USER_LEADERBOARD_RESULT, true);

    }

    private void setData(final ResultTestSeries resultTestSeries) {
        if (testseriesBase == null) {
            difficultyLevel.setText(resultTestSeries.getTestSeriesName());
            if (resultTestSeries.getBookmarkCount() != null) {
                bookmarkTV.setText("Bookmarks (" + resultTestSeries.getBookmarkCount() + ")");
            }

            selectedSubject.setText(subjectName);
            int attempt = Integer.parseInt(resultTestSeries.getCorrectCount()) + Integer.parseInt(resultTestSeries.getIncorrectCount());
            int total = attempt + Integer.parseInt(resultTestSeries.getNonAttempt());
            startQuiz.setText("Review");
            if (!GenericUtils.isEmpty(resultTestSeries.getCreationTime())) {
                Date d = new Date(Long.parseLong(resultTestSeries.getCreationTime()));
                DateFormat f = new SimpleDateFormat("dd:MM:yyyy hh.mm aa");
                String dateString = f.format(d);
                Date date = null;
                try {
                    date = f.parse(f.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String[] amPM = dateString.split("\\s+");

                String[] fullDate = String.valueOf(date).split("\\s+");
                String correctDateFormat = fullDate[0] + ", " + fullDate[1] + " " + fullDate[2] + ", " + fullDate[5] + " at " + amPM[1] + " " + amPM[2];
                completeDate.setText(activity.getResources().getString(R.string.you_completed_qbank_on) + correctDateFormat);
            } else {
                completeDate.setVisibility(View.GONE);
            }
            noOfQuestions.setText(attempt + "/" + total + " Questions");

            startQuiz.setOnClickListener(v -> {
                Intent resultScreen = new Intent(activity, QuizActivity.class);
                resultScreen.putExtra(Const.FRAG_TYPE, Const.QBANK_RESULT);
                resultScreen.putExtra(Const.RESULT_SCREEN, resultTestSeries);
                resultScreen.putExtra(Const.TOTAL_QUESTIONS, totalQuestion);
                resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                activity.startActivity(resultScreen);
            });

            bookmarkRL.setOnClickListener(v -> goToBookmarkScreen(resultTestSeries.getSubject()));
        } else {
            completeDate.setVisibility(View.GONE);
            BasicInfo basicInfo = testseriesBase.getData().getBasicInfo();
            difficultyLevel.setText(basicInfo.getTestSeriesName());
            if (basicInfo.getBookmark_count() != null) {
                bookmarkTV.setText("Bookmarks (" + basicInfo.getBookmark_count() + ")");
            }
            if (basicInfo.getSubject_name() != null) {
                selectedSubject.setText(basicInfo.getSubject_name());
            } else {
                selectedSubject.setText(subjectName);
            }
            startQuiz.setText("Start Now");
            int attempt = 0;
            noOfQuestions.setText(attempt + "/" + totalQuestion + " Questions");

            startQuiz.setOnClickListener(v -> {
                Intent quizView = new Intent(activity, TestBaseActivity.class);
                quizView.putExtra(Const.STATUS, false);
                quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                quizView.putExtra(Const.TESTSERIES, testseriesBase);
                quizView.putExtra(Constants.Extras.SUBJECT_NAME, subjectName);
                quizView.putExtra(Const.TOTAL_QUESTIONS, totalQuestion);
                activity.startActivity(quizView);
                activity.finish();
            });

            bookmarkRL.setOnClickListener(v -> goToBookmarkScreen(testseriesBase.getData().getBasicInfo().getSubject()));
        }
    }

    private void goToBookmarkScreen(String subjectId) {
        Intent resultScreen = new Intent(activity, QuizActivity.class);
        resultScreen.putExtra(Const.FRAG_TYPE, Const.BOOKMARK_LIST);
        resultScreen.putExtra(Constants.Extras.SUBJECT_ID, subjectId);
        activity.startActivity(resultScreen);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        params.put(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        Gson gson = new Gson();
        JSONObject data = GenericUtils.getJsonObject(jsonObject);
        resultTestSeries = gson.fromJson(data.toString(), ResultTestSeries.class);
        if (resultTestSeries != null) {
            setData(resultTestSeries);
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Log.e("errorCallBack: ", jsonString);
    }
}
