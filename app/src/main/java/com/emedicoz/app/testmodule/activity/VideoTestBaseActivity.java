package com.emedicoz.app.testmodule.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.courses.activity.TestQuizActionActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.testmodule.adapter.MyRecyclerAdapter;
import com.emedicoz.app.testmodule.adapter.MyRecyclerAdapterTwo;
import com.emedicoz.app.testmodule.callback.FragmentChangeListener;
import com.emedicoz.app.testmodule.callback.NumberPadOnClick;
import com.emedicoz.app.testmodule.fragment.VideoQBFragment;
import com.emedicoz.app.testmodule.fragment.VideoTestFragment;
import com.emedicoz.app.testmodule.model.BasicInfo;
import com.emedicoz.app.testmodule.model.Data;
import com.emedicoz.app.testmodule.model.Part;
import com.emedicoz.app.testmodule.model.QuestionBank;
import com.emedicoz.app.testmodule.model.QuestionDump;
import com.emedicoz.app.testmodule.model.TestseriesBase;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.LinearLayoutManagerWithSmoothScroller;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoTestBaseActivity extends AppCompatActivity implements FragmentChangeListener, View.OnClickListener, MyNetworkCall.MyNetworkCallBack, NumberPadOnClick {

    public String testSeriesId = "";
    public String testSName;
    public Fragment fragment;
    public ArrayList<Part> partList = new ArrayList<>();
    public TestseriesBase testseriesBase;
    public ArrayList<HashMap<String, String>> answerList;
    RelativeLayout btnNext, btnPrev, btnFinish;
    ArrayList<QuestionBank> questionBankList = new ArrayList<>();
    ArrayList<QuestionDump> questionDumpList = new ArrayList<>();
    int i = 0;
    TextView testSeriesName;
    PopupWindow popupWindow;
    PopupWindow popUp;
    List<String> questionPart = new ArrayList<>();
    String PartId;
    int prevSeconds;
    int unAttemptedCount;
    int attemptCount;
    int skippedQuestion;
    int markForReviewCount;
    Button btnSubmit;
    MyNetworkCall networkCall;
    TextView tvAnswerCount;
    TextView tvUnAnswerCount;
    TextView tvMarkForReviewCount;
    TextView gridView;
    TextView listView;
    TextView textSpinner;
    TextView tvQuestionNumber;
    TextView btnClear;
    Data data;
    ImageView imgTestMenu;
    ImageView imgPause;
    ImageView imgTestBack;
    DrawerLayout drawerLayout;
    String state = "1", lastView = "0";
    boolean daily;
    BasicInfo basicInfo;
    String testVideoQuizAction;
    String quizQues;
    String quizQuesRating;
    private FragmentManager fragmentManager;
    private List<ViewModel> items;
    private RecyclerView rlQuestionPad, rvNumberPad;
    private MyRecyclerAdapter rvNumberPadAdapter;
    private MyRecyclerAdapterTwo rvQuestionAdapter;
    private LinearLayout llDrawerRight, llMarkForReviewCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_video_test);
        Log.e("onCreate: ", "VideoTestBaseActivity");
        networkCall = new MyNetworkCall(this, this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            testSeriesId = getIntent().getExtras().getString(Const.TEST_SERIES_ID);
            testSName = getIntent().getExtras().getString(Constants.Extras.NAME);
            testVideoQuizAction = getIntent().getExtras().getString(Constants.Extras.TEST_VIDEO_QUIZ_ACTION);
            quizQues = getIntent().getExtras().getString(Constants.Extras.QUES_NUM);
            quizQuesRating = getIntent().getExtras().getString(Constants.Extras.RATING);
        }
        btnNext = findViewById(R.id.btn_next);
        btnPrev = findViewById(R.id.btn_prev);
        btnFinish = findViewById(R.id.btn_finish);
        btnClear = findViewById(R.id.btn_clear);
        rlQuestionPad = findViewById(R.id.rl_questionpad);
        tvQuestionNumber = findViewById(R.id.tv_questionnumber);
        testSeriesName = findViewById(R.id.testSeriesName);
        textSpinner = findViewById(R.id.text_spinner);
        rvNumberPad = findViewById(R.id.rvnumberpad);
        imgPause = findViewById(R.id.img_pause);
        imgTestMenu = findViewById(R.id.img_testmenu);
        imgTestBack = findViewById(R.id.img_testback);
        drawerLayout = findViewById(R.id.drawerLayout);
        llMarkForReviewCount = findViewById(R.id.llMarkForReviewCount);
        llDrawerRight = findViewById(R.id.llDrawerRight);
        btnSubmit = findViewById(R.id.btn_submit);
        tvAnswerCount = findViewById(R.id.tv_answer_count);
        tvUnAnswerCount = findViewById(R.id.tv_unanswer_count);
        tvMarkForReviewCount = findViewById(R.id.tv_markforReview_count);
        gridView = findViewById(R.id.gridView);
        listView = findViewById(R.id.listView);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        imgTestMenu.setOnClickListener(this);
        imgTestBack.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        listView.setOnClickListener(this);
        gridView.setOnClickListener(this);
        imgPause.setOnClickListener(this);
        textSpinner.setVisibility(View.GONE);
        if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
            llMarkForReviewCount.setVisibility(View.GONE);
            btnClear.setVisibility(View.GONE);
        } else {
            llMarkForReviewCount.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
        }
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        networkCall.NetworkAPICall(API.API_GET_COMPLETE_INFO_TEST_SERIES, true);
        countTotalAnswer();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragment.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (i < questionBankList.size() - 1) {
                    i++;
                    if (testseriesBase != null)
                        testseriesBase.setLastanswerPosition(i);
                    if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
                        replaceFragment(VideoTestFragment.newInstance(questionBankList.get(i)));
                    } else {
                        replaceFragment(VideoQBFragment.newInstance(questionBankList.get(i)));
                    }
                    tvQuestionNumber.setText(String.format("%d/%d Questions", i + 1, questionBankList.size()));
                    if (questionBankList.get(i).getAnswerPosttion() == -1)
                        questionBankList.get(i).setIsanswer(false, 0);
                    rvQuestionAdapter.setSelectePosition(i);

                }
                checkNextButton();
                break;
            case R.id.btn_prev:
                if (i > 0) {
                    i--;
                    if (testseriesBase != null)
                        testseriesBase.setLastanswerPosition(i);
                    if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
                        replaceFragment(VideoTestFragment.newInstance(questionBankList.get(i)));
                    } else {
                        replaceFragment(VideoQBFragment.newInstance(questionBankList.get(i)));
                    }
                    tvQuestionNumber.setText(String.format("%d/%d Questions", i + 1, questionBankList.size()));
                    if (questionBankList.get(i).getAnswerPosttion() == -1)
                        questionBankList.get(i).setIsanswer(false, 0);
                    rvQuestionAdapter.setSelectePosition(i);
                }
                checkNextButton();
                break;

            case R.id.btn_clear:
                fragmentManager = getSupportFragmentManager();
                fragment = fragmentManager.findFragmentById(R.id.container);
                if (fragment instanceof VideoTestFragment) {
                    ((VideoTestFragment) fragment).refreshPage();
                }
                break;

            case R.id.img_testmenu:
                countTotalAnswer();
                drawerLayout.openDrawer(llDrawerRight);
                break;

            case R.id.img_testback:
                onBackPressed();
                break;

            case R.id.img_pause:
                showPopupPauseTest();
                break;

            case R.id.btn_submit:
            case R.id.btn_finish:
                if (!isFinishing())
                    SharedPreference.getInstance().saveDailyQuizStatus(Const.DAILY_QUIZ_TEST, "true");
                    new Handler().postDelayed(() -> showPopupSubmitTest(), 1000);
                break;

            case R.id.gridView:
                SharedPreference.getInstance().putString("VIEW", "0");
                GridLayoutManager manager = new GridLayoutManager(VideoTestBaseActivity.this, 6, GridLayoutManager.VERTICAL, false);
                rlQuestionPad.setLayoutManager(manager);
                gridView.setTextColor(ContextCompat.getColor(this, R.color.black));
                gridView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_grid_list));
                listView.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
                listView.setTextColor(ContextCompat.getColor(this, R.color.white));
                rvQuestionAdapter = new MyRecyclerAdapterTwo(questionBankList, VideoTestBaseActivity.this, items, R.layout.single_row_testpad_no, VideoTestBaseActivity.this, "0");
                rlQuestionPad.setAdapter(rvQuestionAdapter);
                break;
            case R.id.listView:
                SharedPreference.getInstance().putString("VIEW", "1");
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                rlQuestionPad.setLayoutManager(linearLayoutManager);
                listView.setTextColor(ContextCompat.getColor(this, R.color.black));
                listView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_grid_list));
                gridView.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
                gridView.setTextColor(ContextCompat.getColor(this, R.color.white));
                rvQuestionAdapter = new MyRecyclerAdapterTwo(questionBankList, VideoTestBaseActivity.this, items, R.layout.single_row_testpad_no, VideoTestBaseActivity.this, "1");
                rlQuestionPad.setAdapter(rvQuestionAdapter);
                break;
            default:
                break;
        }
    }

    public void checkNextButton() {
        if (i == questionBankList.size() - 1) {
            btnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        switch (apiType) {
            case API.API_GET_COMPLETE_INFO_TEST_SERIES:
                params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
                params.put(Const.TESTSERIES_ID, testSeriesId);
                return service.postData(apiType, params);
        }
        return null;
    }

    @Override
    public void successCallBack(JSONObject jsonString, String apiType) throws JSONException {
        switch (apiType) {
            case API.API_GET_COMPLETE_INFO_TEST_SERIES:
            case API.API_GET_COMPLETE_INFO_CUSTOM_TEST_SERIES:
                if (jsonString.optString("status").equals(Const.TRUE)) {

                    /*new code  start*/
                    Gson gson = new Gson();
                    try {
                        testseriesBase = gson.fromJson(jsonString.toString(), TestseriesBase.class);
                        data = testseriesBase.getData();
                        basicInfo = testseriesBase.getData().getBasicInfo();
                        testSeriesName.setText(basicInfo.getTestSeriesName());
                        setTestData();

                    } catch (Exception e) {
                        Log.e("SuccessCallBack Catch: ", Objects.requireNonNull(e.getMessage()));
                    }

                } else {
                    this.errorCallBack(jsonString.optString(Constants.Extras.MESSAGE), apiType);
                }
                break;

            default:
                break;
        }
    }

    private void setTestData() {
        if (basicInfo.getDisplay_bubble() != null && !basicInfo.getDisplay_bubble().equalsIgnoreCase("")) {

            if (basicInfo.getDisplay_bubble().equalsIgnoreCase("1")) {
                rvNumberPad.setVisibility(View.VISIBLE);
            } else {
                rvNumberPad.setVisibility(View.GONE);
            }
        } else {
            rvNumberPad.setVisibility(View.VISIBLE);
        }
        questionBankList = (ArrayList<QuestionBank>) data.getQuestionBank();
        questionDumpList = (ArrayList<QuestionDump>) data.getQuestionDump();
        PartId = questionBankList.get(0).getPart();
        if (questionDumpList != null) {
            for (int i = 0; i < questionBankList.size(); i++) {
                if (data.getActiveQues().equals(questionBankList.get(i).getId())) {
                    if (testseriesBase != null)
                        testseriesBase.setLastanswerPosition(i);
                }
                for (int j = 0; j < questionDumpList.size(); j++) {
                    int answerPosition = 0;
                    boolean isanswer = false;

                    if (questionDumpList.get(j).getQuestionId().equals(questionBankList.get(i).getId())) {

                        if (questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE)) {
                            if (questionDumpList.get(j).getAnswer().equals("")) {
                                answerPosition = -1;
                                isanswer = false;
                            } else if (questionDumpList.get(j).getAnswer().equals("0")) {
                                answerPosition = 0;
                                isanswer = false;
                            } else {
                                answerPosition = Integer.parseInt(questionDumpList.get(j).getAnswer());
                                Log.e("answer", "" + answerPosition);
                                isanswer = true;
                            }
                            questionBankList.get(i).setIsanswer(isanswer, answerPosition);
                        }
                        boolean markforreview;
                        markforreview = !questionDumpList.get(j).getReview().equals("0") && !questionDumpList.get(j).getReview().isEmpty();
                        questionBankList.get(i).setMarkForReview(markforreview);

                        if (!questionDumpList.get(j).getOnscreen().equals(""))
                            questionBankList.get(i).setTotalTimeSpent(Integer.parseInt(questionDumpList.get(j).getOnscreen()));
                        questionBankList.get(i).setIsguess(questionDumpList.get(j).getGuess());

                    }
                }

            }
        }

        partList = (ArrayList<Part>) data.getBasicInfo().getParts();

        if (testseriesBase.getLastanswerPosition() == -1 && testseriesBase.getLastanswerPosition() == 0) {
            tvQuestionNumber.setText(String.format("1/%d Questions", questionBankList.size()));
        } else {
            tvQuestionNumber.setText(String.format("%d/%d Questions", testseriesBase.getLastanswerPosition() + 1, questionBankList.size()));
        }
        if (questionBankList != null && !questionBankList.isEmpty()) {
            if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
                replaceFragment(VideoTestFragment.newInstance(questionBankList.get(testseriesBase.getLastanswerPosition())));
            } else {
                replaceFragment(VideoQBFragment.newInstance(questionBankList.get(testseriesBase.getLastanswerPosition())));
            }
            i = testseriesBase.getLastanswerPosition();
        }

        rvQuestionAdapter = new MyRecyclerAdapterTwo(questionBankList, VideoTestBaseActivity.this, items, R.layout.single_row_testpad_no, VideoTestBaseActivity.this, "0");
        rvNumberPadAdapter = new MyRecyclerAdapter(questionDumpList, questionBankList, VideoTestBaseActivity.this, items, R.layout.single_row_testpad_no, VideoTestBaseActivity.this);

        rlQuestionPad.setAdapter(rvQuestionAdapter);
        GridLayoutManager manager = new GridLayoutManager(VideoTestBaseActivity.this, 6, GridLayoutManager.VERTICAL, false);
        rlQuestionPad.setLayoutManager(manager);
        rlQuestionPad.setItemAnimator(new DefaultItemAnimator());

        rvNumberPad.setAdapter(rvNumberPadAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManagerWithSmoothScroller(VideoTestBaseActivity.this, LinearLayoutManager.HORIZONTAL, false);

        rvNumberPad.setLayoutManager(linearLayoutManager);
        rvNumberPad.setItemAnimator(new DefaultItemAnimator());
        for (int i = 0; i < partList.size(); i++) {
            partList.get(i).setIndex(getIndexOf(questionBankList, partList.get(i).getId()));
            Log.e("setTestData", "onPageScrollStateChanged" + getIndexOf(questionBankList, partList.get(i).getId()));
            questionPart.add(partList.get(i).getPartName());
        }

        popUp = popupWindowPart();

        setPart();

    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {

    }


    private void networkCallSubmitTestSeries(HashMap<String, String> finalResponse) {
        final Progress mProgress = new Progress(this);
        mProgress.setCancelable(false);
        mProgress.show();
        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response = apiInterface.submitAiimsTestSeries(finalResponse);
        response.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e("response", "response" + jsonResponse.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            if (state.equals("0")) {
                                String testSegmentId;
                                String testResultDate;
                                String testSeriesName;
                                JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                                testSegmentId = data.getString(Constants.Extras.ID);
                                testResultDate = data.getString(Constants.ResultExtras.TEST_RESULT_DATE);
                                testSeriesName = data.getString(Constants.ResultExtras.TEST_SERIES_NAME);

                                if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
                                    if (Constants.Extras.TEST_VIDEO_QUIZ_ACTION.equalsIgnoreCase(testVideoQuizAction)) {
                                        ResultTestSeries resultTestSeries = new ResultTestSeries();
                                        resultTestSeries.setTestResultDate(testResultDate);
                                        resultTestSeries.setTestSeriesName(testSeriesName);

                                        Intent resultScreen = new Intent(VideoTestBaseActivity.this, TestQuizActionActivity.class);
                                        resultScreen.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                                        resultScreen.putExtra(Constants.Extras.QUIZ_TYPE, "VIDEO");
                                        resultScreen.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.COMPLETE);
                                        resultScreen.putExtra(Constants.Extras.QUES_NUM, quizQues);
                                        resultScreen.putExtra(Constants.Extras.RATING, quizQuesRating);
                                        resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                                        resultScreen.putExtra(Constants.Extras.TITLE_NAME, resultTestSeries.getTestSeriesName());
                                        startActivity(resultScreen);
                                        finish();

                                    } else {
                                        ResultTestSeries resultTestSeries = new ResultTestSeries();
                                        resultTestSeries.setTestResultDate(testResultDate);
                                        resultTestSeries.setTestSeriesName(testSeriesName);

                                        Intent resultScreen = new Intent(VideoTestBaseActivity.this, QuizActivity.class);
                                        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_BASIC);
                                        resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                                        resultScreen.putExtra(Constants.Extras.TYPE, Const.QUIZ_RESULT);
                                        resultScreen.putExtra(Const.RESULT_SCREEN, resultTestSeries);
                                        startActivity(resultScreen);
                                    }
                                } else {
                                    Intent resultScreen = new Intent(VideoTestBaseActivity.this, QuizActivity.class);

                                    if (!GenericUtils.isEmpty(testResultDate)) {
                                        long tsLong = System.currentTimeMillis();
                                        if (tsLong < (Long.parseLong(testResultDate) * 1000)) {
                                            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_AWAIT);
                                            resultScreen.putExtra(Constants.Extras.DATE, testResultDate);
                                        } else {
                                            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                                            resultScreen.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                                            resultScreen.putExtra(Constants.Extras.NAME, testSName);
                                            resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                                        }
                                    } else {
                                        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                                        resultScreen.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                                        resultScreen.putExtra(Constants.Extras.NAME, testSName);
                                        resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                                    }
                                    startActivity(resultScreen);
                                }

                            }
                        } else {
                            Toast.makeText(VideoTestBaseActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Log.e("OnFailure", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(VideoTestBaseActivity.this, "Please check your internet connection....", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public HashMap<String, String> makeAnswerArray(int position) {
        String answerPosition = "";
        if (questionBankList.get(position).getAnswer() != null) {
            if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE)) {
                if (questionBankList.get(position).getAnswerPosttion() == -1) {
                    answerPosition = "";
                    //  unanswered = "";
                } else if (questionBankList.get(position).getAnswerPosttion() == 0) {
                    answerPosition = "";
                    //  unanswered = "0";
                } else {
                    answerPosition = String.valueOf(questionBankList.get(position).getAnswerPosttion());
                    //  unanswered = "1";
                }
            }
        } else {
            if (questionBankList.get(position).getAnswerPosttion() == -1) {
                answerPosition = "";
                //  unanswered = "";
            } else if (questionBankList.get(position).getAnswerPosttion() == 0) {
                answerPosition = "";
                //  unanswered = "0";
            } else {
                answerPosition = String.valueOf(questionBankList.get(position).getAnswerPosttion());
                //  unanswered = "1";
            }
        }
        HashMap<String, String> answer = new HashMap<String, String>();
        answer.put(Const.ORDER, String.valueOf(position + 1));
        answer.put(Const.QUESTIONID, questionBankList.get(position).getId());
        answer.put(Const.ANSWER, answerPosition);
        //answer.put("unanswered", unanswered);
        answer.put(Const.ONSCREEN, "0");
        answer.put(Const.GUESS, questionBankList.get(position).getIsguess());
        answer.put(Const.PART, questionBankList.get(position).getPart());
        answer.put(Const.SECTIONID, questionBankList.get(position).getSectionId());
        answer.put(Const.ISCORRECT, questionBankList.get(position).isIsanswerRight() ? "1" : "0");
        answer.put(Const.ISMARKFORREVIEW, questionBankList.get(position).isMarkForReview() ? "1" : "0");
        return answer;
    }

    private void finishTestSeries() {
        Log.e("finishTestSeries: ", "FTS is called");
        lastView = questionBankList.get(i).getId();
        answerList = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < questionBankList.size(); i++) {
            answerList.add(makeAnswerArray(i));
        }

        String jsonStr = new Gson().toJson(answerList);
        HashMap<String, String> finalResponse = new HashMap<>();
        finalResponse.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        finalResponse.put(Const.TESTSERIES_ID, testSeriesId);
        finalResponse.put(Const.STATE, state);
        finalResponse.put(Const.LAST_VIEW, lastView);
        if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
            if (daily) {
                if (SharedPreference.getInstance().getMasterHitResponse().getQbank_timer().equalsIgnoreCase("1")) {
                    if (!data.getBasicInfo().getTimeInMins().equalsIgnoreCase(""))
                        finalResponse.put(Const.TIME_SPENT, getTimeSpentInMinutes());
                    else
                        finalResponse.put(Const.TIME_SPENT, getTimeSpentOnQuestionCount());
                } else {
                    finalResponse.put(Const.TIME_SPENT, "0");
                }
            } else {
                if (!data.getBasicInfo().getTimeInMins().equalsIgnoreCase(""))
                    finalResponse.put(Const.TIME_SPENT, getTimeSpentInMinutes());
                else
                    finalResponse.put(Const.TIME_SPENT, getTimeSpentOnQuestionCount());
            }
        } else if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
            if (SharedPreference.getInstance().getMasterHitResponse().getQbank_timer().equalsIgnoreCase("1")) {
                if (!data.getBasicInfo().getTimeInMins().equalsIgnoreCase(""))
                    finalResponse.put(Const.TIME_SPENT, getTimeSpentInMinutes());
                else
                    finalResponse.put(Const.TIME_SPENT, getTimeSpentOnQuestionCount());
            } else {
                finalResponse.put(Const.TIME_SPENT, "0");
            }
        }
        finalResponse.put(Const.QUESTION_DUMP, jsonStr);

        networkCallSubmitTestSeries(finalResponse);
    }


    private String getTimeSpentOnQuestionCount() {
        return String.valueOf(((data.getQuestionBank().size() * 60) * 1000 - data.getBasicInfo().getTimeRemaining()) / 1000);
    }

    private String getTimeSpentInMinutes() {
        return String.valueOf(((Integer.parseInt(data.getBasicInfo().getTimeInMins()) * 60) * 1000 - data.getBasicInfo().getTimeRemaining()) / 1000);
    }

    private void showPopupPauseTest() {
        View v = Helper.newCustomDialog(this, false, getString(R.string.app_name), getString(R.string.do_you_really_want_to_pause_this_test));

        Button btnCancel, btnSubmit;

        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        btnCancel.setOnClickListener(v1 -> Helper.dismissDialog());

        btnSubmit.setOnClickListener(v1 -> {
            Helper.dismissDialog();
            if (testseriesBase != null)
                testseriesBase.setLastanswerPosition(i);
            state = "1";
            finishTestSeries();
        });
    }

    @Override
    public void onBackPressed() {
        Log.e("onBackPressed: ", "onBackPressed of VideoTestBaseActivity");
        showPopupPauseTest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eMedicozApp.getInstance().playerState = null;
    }

    @Override
    public void sendOnclickInd(int index) {

        i = index;
        if (testseriesBase != null)
            testseriesBase.setLastanswerPosition(index);
        if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
            replaceFragment(VideoTestFragment.newInstance(questionBankList.get(index)));
        } else {
            replaceFragment(VideoQBFragment.newInstance(questionBankList.get(index)));
        }
        tvQuestionNumber.setText(String.format("%d/%d Questions", index + 1, questionBankList.size()));
        if (questionBankList.get(index).getAnswerPosttion() == -1)
            questionBankList.get(index).setIsanswer(false, 0);
        rvQuestionAdapter.setSelectePosition(index);

    }

    public void notifyNumberAdapter() {
        rvQuestionAdapter.notifyDataSetChanged();
        rvNumberPadAdapter.notifyDataSetChanged();
    }

    private void showPopupSubmitTest() {
        try {
            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v = li.inflate(R.layout.dialogbox_test_submit, null, false);
            final Dialog quizBasicInfoDialog = new Dialog(this);
            quizBasicInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            quizBasicInfoDialog.setCanceledOnTouchOutside(false);
            quizBasicInfoDialog.setContentView(v);
            quizBasicInfoDialog.show();
            TextView answeredTV, unansweredTV, markedTV, marked1, submitTV1, submitTV2;
            LinearLayout notDisplayedLayout;
            Button btnCancel, btnSubmit;
            View markedView1, markedView2;

            btnCancel = v.findViewById(R.id.btn_cancel);
            answeredTV = v.findViewById(R.id.answeredTV);
            unansweredTV = v.findViewById(R.id.unansweredTV);
            markedTV = v.findViewById(R.id.markedTV);
            marked1 = v.findViewById(R.id.marked1);
            markedView1 = v.findViewById(R.id.markedView1);
            markedView2 = v.findViewById(R.id.markedView2);
            submitTV1 = v.findViewById(R.id.submitTest1);
            submitTV2 = v.findViewById(R.id.tv_submit_test);
            notDisplayedLayout = v.findViewById(R.id.notDisplayed);

            if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
                submitTV1.setText(R.string.submit_qbank);
                submitTV2.setText(R.string.do_you_want_to_submit_qbank);
                markedTV.setVisibility(View.GONE);
                marked1.setVisibility(View.GONE);
                markedView1.setVisibility(View.GONE);
                markedView2.setVisibility(View.GONE);
                notDisplayedLayout.setVisibility(View.GONE);
            } else {
                submitTV1.setText(R.string.submit_test);
                submitTV2.setText(R.string.do_you_want_to_submit_test);
                markedTV.setVisibility(View.VISIBLE);
                marked1.setVisibility(View.VISIBLE);
                markedView1.setVisibility(View.VISIBLE);
                markedView2.setVisibility(View.VISIBLE);
            }

            int unAttemptCount = questionBankList.size() - attemptCount;
            answeredTV.setText(String.valueOf(attemptCount));
            unansweredTV.setText(String.valueOf(unAttemptCount));
            markedTV.setText(String.valueOf(markForReviewCount));
            btnSubmit = v.findViewById(R.id.btn_submit);
            btnCancel.setOnClickListener(v1 -> quizBasicInfoDialog.dismiss());

            btnSubmit.setOnClickListener(v1 -> {
                quizBasicInfoDialog.dismiss();
                state = "0";
                finishTestSeries();
            });
        } catch (WindowManager.BadTokenException bte) {
            bte.printStackTrace();
        }
    }

    public void countTotalAnswer() {
        unAttemptedCount = 0;
        attemptCount = 0;
        skippedQuestion = 0;
        markForReviewCount = 0;
        for (int i = 0; i < questionBankList.size(); i++) {

            if (questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE)) {

                if (questionBankList.get(i).isMarkForReview()) {
                    ++markForReviewCount;
                }
                if (questionBankList.get(i).getAnswerPosttion() == -1) {
                    ++skippedQuestion;
                } else if (questionBankList.get(i).getAnswerPosttion() == 0) {
                    ++unAttemptedCount;
                } else if (questionBankList.get(i).getAnswerPosttion() != -1 && questionBankList.get(i).getAnswerPosttion() != -0) {
                    ++attemptCount;
                }
            }
        }
        tvAnswerCount.setText(String.valueOf(attemptCount));
        tvUnAnswerCount.setText(String.valueOf(unAttemptedCount));
        tvMarkForReviewCount.setText(String.valueOf(markForReviewCount));
    }

    public int getIndexOf(List<QuestionBank> list, String name) {
        int pos = 0;

        for (QuestionBank myObj : list) {
            if (name.equalsIgnoreCase(myObj.getPart()))
                return pos;
            pos++;
        }

        return -1;
    }

    private PopupWindow popupWindowPart() {

        // initialize a pop up window type
        popupWindow = new PopupWindow(this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_popup, null);
        if (partList != null && !partList.isEmpty()) {
            TextView partATV, partBTV, partCTV;

            partATV = view.findViewById(R.id.partATV);
            partBTV = view.findViewById(R.id.partBTV);
            partCTV = view.findViewById(R.id.partCTV);

            partATV.setText(partList.get(0).getPartName());
            partBTV.setText(partList.get(1).getPartName());
            partCTV.setText(partList.get(2).getPartName());

            partATV.setOnClickListener(v -> {
                if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
                    replaceFragment(VideoTestFragment.newInstance(questionBankList.get(partList.get(2).getIndexOf())));

                } else {
                    replaceFragment(VideoQBFragment.newInstance(questionBankList.get(partList.get(2).getIndexOf())));

                }
                textSpinner.setText(questionPart.get(0));
                popupWindow.dismiss();
            });
            partBTV.setOnClickListener(v -> {
                if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
                    replaceFragment(VideoTestFragment.newInstance(questionBankList.get(partList.get(2).getIndexOf())));

                } else {
                    replaceFragment(VideoQBFragment.newInstance(questionBankList.get(partList.get(2).getIndexOf())));

                }
                textSpinner.setText(questionPart.get(1));
                popupWindow.dismiss();
            });
            partCTV.setOnClickListener(v -> {
                if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
                    replaceFragment(VideoTestFragment.newInstance(questionBankList.get(partList.get(2).getIndexOf())));

                } else {
                    replaceFragment(VideoQBFragment.newInstance(questionBankList.get(partList.get(2).getIndexOf())));

                }
                textSpinner.setText(questionPart.get(2));
                popupWindow.dismiss();
            });
        }
        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background_rectangle));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the list view as pop up window content
        popupWindow.setContentView(view);

        return popupWindow;
    }

    private void setPart() {
        for (int i = 0; i < partList.size(); i++) {
            if (!(i == partList.size() - 1)) {
                if (testseriesBase.getLastanswerPosition() >= partList.get(i).getIndexOf() && testseriesBase.getLastanswerPosition() <= partList.get(i + 1).getIndexOf() - 1) {
                    textSpinner.setText(questionPart.get(i));
                    break;
                }
            } else {
                textSpinner.setText(questionPart.get(i));
            }
        }

    }
}
