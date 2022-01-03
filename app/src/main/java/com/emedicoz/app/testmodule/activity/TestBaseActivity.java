package com.emedicoz.app.testmodule.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.legacy.widget.Space;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.emedicoz.app.testmodule.adapter.TestViewPagerAdapter;
import com.emedicoz.app.testmodule.callback.NumberPadOnClick;
import com.emedicoz.app.testmodule.db.TestReaderContract;
import com.emedicoz.app.testmodule.db.TestSeriesDbHelper;
import com.emedicoz.app.testmodule.fragment.NewQuestionBankFragment;
import com.emedicoz.app.testmodule.fragment.TestSeriesFragmentNew;
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
import com.emedicoz.app.utilso.network.API;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestBaseActivity extends AppCompatActivity implements View.OnClickListener, NumberPadOnClick, MyNetworkCall.MyNetworkCallBack {
    private static final String TAG = "TestBaseActivity";

    public String testSeriesId;
    public TestViewPagerAdapter pagerAdapter;
    public int currentPage;
    public ArrayList<Part> partList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> answerList;
    public Data data;
    public List<QuestionBank> questionBankList;
    public List<QuestionDump> questionDumpList;
    public Boolean custom = false;
    public Boolean daily = false;
    public String testSName;
    public String testQuizAction;
    String quizQues;
    String quizQuesRating;
    String totalQuestion;
    String unanswered = "";
    boolean status = false;
    long count = 0;
    String subjectName = "";
    ConstraintLayout rootCL;
    TextView testSeriesName;
    PopupWindow popupWindow;
    TestseriesBase testSeriesBase;
    List<String> questionPart = new ArrayList<>();
    BasicInfo basicInfo;
    Progress mProgress;
    String state = "";
    String lastView;
    long millisInFuture;
    MyNetworkCall networkCall;
    int prevSeconds;
    int attemptCount;
    int attempLimit;
    int unAttemptedCount;
    int skippedQuestion;
    int markForReviewCount;
    String PartId;
    public String mPartId;
    String partId;
    PopupWindow popUp;
    TextView gridView;
    TextView listView;
    TextView nextTV;
    private TestSeriesDbHelper testSeriesDbHelper;
    private LinearLayout llDrawerRight;
    private LinearLayout llMarkForReviewCount;
    private DrawerLayout drawerLayout;
    private ImageView imgTestBack;
    private ImageView imgTestMenu;
    private ImageView imgPause;
    private RecyclerView rlQuestionPad;
    private RecyclerView rvNumberPad;
    private ViewPager viewPagerTest;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private List<ViewModel> items;
    private Button btnSubmit;
    private RelativeLayout btnNext;
    private RelativeLayout btnPrev;
    private RelativeLayout btnFinish;
    private TextView btnClear;
    private TextView tvTime;
    private TextView textSpinner;
    private TextView tvQuestionNumber;
    private TextView tvAnswerCount;
    private TextView tvUnAnswerCount;
    private TextView tvMarkForReviewCount;
    private MyRecyclerAdapter rvNumberPadAdapter;
    private MyRecyclerAdapterTwo rvQuestionAdapter;
    private CountDownTimer countDownTimer = null;
    String testSegmentId;
    private String openFrom;
    private boolean noTestData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_test_base);
        testSeriesDbHelper = new TestSeriesDbHelper(this);
        networkCall = new MyNetworkCall(this, this);
        SharedPreference.getInstance().putString("VIEW", "0");
        setReference();

        if (getIntent() != null && getIntent().getExtras() != null) {
            testSeriesId = getIntent().getExtras().getString(Const.TEST_SERIES_ID);
            testQuizAction = getIntent().getExtras().getString(Constants.Extras.TEST_QUIZ_ACTION);
            testSName = getIntent().getExtras().getString(Constants.Extras.NAME);
            quizQues = getIntent().getExtras().getString(Constants.Extras.QUES_NUM);
            quizQuesRating = getIntent().getExtras().getString(Constants.Extras.RATING);
            openFrom = getIntent().getExtras().getString(Constants.Extras.OPEN_FROM);
            totalQuestion = getIntent().getExtras().getString(Constants.Extras.TOTAL_QUESTIONS);
            subjectName = getIntent().getExtras().getString(Constants.Extras.SUBJECT_NAME);
            custom = getIntent().getExtras().getBoolean(Constants.Extras.CUSTOM, false);
            daily = getIntent().getExtras().getBoolean(Constants.Extras.DAILY, false);
            testSegmentId = getIntent().getExtras().getString(Constants.ResultExtras.TEST_SEGMENT_ID);
        }

        Log.e("onCreate: ", testSeriesId);
        if (daily) {
            imgPause.setVisibility(View.GONE);
        } else {
            imgPause.setVisibility(View.VISIBLE);
        }

        if (custom) {
            networkCall.NetworkAPICall(API.API_GET_COMPLETE_INFO_CUSTOM_TEST_SERIES, true);
        } else {

            // to retrieve data from local db, if local db data is empty then get data from server otherwise set this local db data
            Cursor rs = testSeriesDbHelper.getTestSeriesByid(Integer.parseInt(testSeriesId));
            if (rs != null && rs.moveToFirst()) {
                rs.moveToFirst();
                String response = rs.getString(rs.getColumnIndex(TestReaderContract.TestDataTable.TEST_DATA));
                Log.e("LOCAL", Const.RESPONSE + response);
                Gson gson = new Gson();
                testSeriesBase = gson.fromJson(response, TestseriesBase.class);
                Log.e("BASE_DATA ", String.valueOf(testSeriesBase.getData().getQuestionBank().size()));

                data = testSeriesBase.getData();
                if (GenericUtils.isListEmpty(data.getQuestionBank()))
                    networkCall.NetworkAPICall(API.API_GET_COMPLETE_INFO_TEST_SERIES, true);
                else {
                    basicInfo = testSeriesBase.getData().getBasicInfo();
                    testSeriesName.setText(basicInfo.getTestSeriesName());
                    setTestData();
                    setTimerVisibility();
                }
            } else {
                networkCall.NetworkAPICall(API.API_GET_COMPLETE_INFO_TEST_SERIES, true);
            }

        }
        mProgress = new Progress(this);
        mProgress.setCancelable(false);

        countTotalAnswer();

    }

    public void countTotalAnswer() {
        /**
         *  -1 is used for skipped, 0 used for unattempted
         */
        // This function is used for counting number of questions attempted,unattempted,mark for review and skipped
        unAttemptedCount = 0;
        attemptCount = 0;
        skippedQuestion = 0;
        markForReviewCount = 0;
        for (int i = 0; i < questionBankList.size(); i++) {
            String qType = questionBankList.get(i).getQuestionType();

            if (qType.equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE)
                    || qType.equalsIgnoreCase(Constants.QuestionType.REASON_ASSERTION)
                    || qType.equalsIgnoreCase(Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS)
                    || qType.equalsIgnoreCase(Constants.QuestionType.EXTENDED_MATCHING)
                    || qType.equalsIgnoreCase(Constants.QuestionType.MULTIPLE_COMPLETION)) {

                if (questionBankList.get(i).isMarkForReview()) {
                    ++markForReviewCount;
                }
                if (questionBankList.get(i).getAnswerPosttion() == -1) {
                    ++skippedQuestion;
                } else if (questionBankList.get(i).getAnswerPosttion() == 0) {
                    ++unAttemptedCount;
                } else if (questionBankList.get(i).getAnswerPosttion() != -1 &&
                        questionBankList.get(i).getAnswerPosttion() != -0) {
                    ++attemptCount;
                }

            } else if (qType.equalsIgnoreCase(Constants.QuestionType.MULTIPLE_CHOICE)
                    || qType.equalsIgnoreCase(Constants.QuestionType.MULTIPLE_TRUE_FALSE)
                    || qType.equalsIgnoreCase(Constants.QuestionType.MATCH_THE_FOLLOWING)
                    || qType.equalsIgnoreCase(Constants.QuestionType.TRUE_FALSE)
                    || qType.equalsIgnoreCase(Constants.QuestionType.FILL_IN_THE_BLANKS)) {
                if (questionBankList.get(i).isMarkForReview()) {
                    ++markForReviewCount;
                }
                if (questionBankList.get(i).getMultipleAnswerPosition().equalsIgnoreCase("-1")) {
                    ++skippedQuestion;
                } else if (questionBankList.get(i).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                    ++unAttemptedCount;
                } else if (!questionBankList.get(i).getMultipleAnswerPosition().equalsIgnoreCase("-1") &&
                        !questionBankList.get(i).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                    ++attemptCount;
                }
            }
        }
        tvAnswerCount.setText(String.valueOf(attemptCount));
        tvUnAnswerCount.setText(String.valueOf(unAttemptedCount));
        tvMarkForReviewCount.setText(String.valueOf(markForReviewCount));
    }

    private void setTestData() {
        BasicInfo basicInfo = testSeriesBase.getData().getBasicInfo();
        if (!GenericUtils.isEmpty(basicInfo.getDisplay_bubble())) {
            // condition is for show and hide top question number bubbles
            if (basicInfo.getDisplay_bubble().equalsIgnoreCase("1")) {
                rvNumberPad.setVisibility(View.VISIBLE);
            } else {
                rvNumberPad.setVisibility(View.GONE);
            }
        } else {
            rvNumberPad.setVisibility(View.VISIBLE);
        }
        questionDumpList = data.getQuestionDump();
        questionBankList = data.getQuestionBank();
        if (!GenericUtils.isListEmpty(questionBankList)) {
            questionBankList.get(0).setIsanswer(false, 0);
            partId = questionBankList.get(0).getPart();
        }

        //************************* This code snippet is used for setting the previously saved data when user resumes the test series***************************

        if (questionDumpList != null) {
            for (int i = 0; i < questionBankList.size(); i++) {
                if (data.getActiveQues().equals(questionBankList.get(i).getId()) && testSeriesBase != null) {
                    testSeriesBase.setLastanswerPosition(i);
                    Log.e(TAG, "setTestData: "+testSeriesBase.getData().getActiveQues() +", POsition:"+i);
                }
                for (int j = 0; j < questionDumpList.size(); j++) {
                    int answerPosition;
                    boolean isAnswer;
                    StringBuilder multipleAnswerPosition;
                    boolean isMultipleAnswer;

                    if (questionDumpList.get(j).getQuestionId().equals(questionBankList.get(i).getId())) {

                        if (questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE) || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS) || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.REASON_ASSERTION) || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.EXTENDED_MATCHING) || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_COMPLETION)) {
                            /**
                             *  setting answerPosition and isAnswer for SingleChoice,SequentialArrangement,ReasonAssertion,ExtendedMatching and MultipleCompletion type question
                             *  isAnswer is used for checking that answer of the question is given(true) or not given(false)
                             *  answerPosition is used to find out the option selected for particular question
                             */
                            if (questionDumpList.get(j).getAnswer().equals("")) {
                                answerPosition = -1;
                                isAnswer = false;
                            } else if (questionDumpList.get(j).getAnswer().equals("0")) {
                                answerPosition = 0;
                                isAnswer = false;
                            } else {
                                answerPosition = Integer.parseInt(questionDumpList.get(j).getAnswer());
                                Log.e(Const.ANSWER, "" + answerPosition);
                                isAnswer = true;
                            }
                            questionBankList.get(i).setIsanswer(isAnswer, answerPosition);
                        } else if (questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_CHOICE)) {
                            /**
                             *  setting multipleAnswerPosition and isMultipleAnswer for MultipleChoice type question
                             *  isMultipleAnswer is used for checking that answer of the question is given(true) or not given(false)
                             *  multipleAnswerPosition is used to find out the options selected for particular question
                             */
                            ArrayList<Integer> selectedValue = new ArrayList<>();

                            if (questionDumpList.get(j).getAnswer().equals("")) {
                                multipleAnswerPosition = new StringBuilder("-1");
                                isMultipleAnswer = false;
                            } else if (questionDumpList.get(j).getAnswer().equals("0")) {
                                multipleAnswerPosition = new StringBuilder("0");
                                isMultipleAnswer = false;
                            } else {
                                multipleAnswerPosition = new StringBuilder();
                                String[] answers = questionDumpList.get(j).getAnswer().split(",");
                                for (String answer : answers) {
                                    selectedValue.add(Integer.parseInt(answer) - 1);
                                    multipleAnswerPosition.append((Integer.parseInt(answer) - 1)).append(",");
                                }
                                questionBankList.get(i).setAnswered(true);
                                questionBankList.get(i).setSelectedValue(selectedValue);
                                isMultipleAnswer = true;
                            }
                            questionBankList.get(i).setIsMultipleAnswer(isMultipleAnswer, multipleAnswerPosition.toString());
                        } else if (questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_TRUE_FALSE)
                                || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.TRUE_FALSE)) {
                            /**
                             *  setting multipleAnswerPosition and isMultipleAnswer for TrueFalse and MultipleTrueFalse type question
                             *  isMultipleAnswer is used for checking that answer of the question is given(true) or not given(false)
                             *  multipleAnswerPosition is used to find out the options selected for particular question
                             */
                            ArrayList<String> selectedValue = new ArrayList<>();
                            if (questionDumpList.get(j).getAnswer().equals("")) {
                                multipleAnswerPosition = new StringBuilder("-1");
                                isMultipleAnswer = false;
                            } else if (questionDumpList.get(j).getAnswer().equals("0")) {
                                multipleAnswerPosition = new StringBuilder("0");
                                isMultipleAnswer = false;
                            } else {
                                multipleAnswerPosition = new StringBuilder();
                                String[] answers = questionDumpList.get(j).getAnswer().split(",");
                                for (String answer : answers) {
                                    selectedValue.add(answer);
                                    multipleAnswerPosition.append(answer).append(",");
                                }
                                questionBankList.get(i).setTfAnswerArrayList(selectedValue);
                                isMultipleAnswer = true;
                            }
                            questionBankList.get(i).setAnswered(true);
                            questionBankList.get(i).setIsMultipleAnswer(isMultipleAnswer, multipleAnswerPosition.toString());
                        } else if (questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MATCH_THE_FOLLOWING)) {
                            /**
                             *  setting multipleAnswerPosition and isMultipleAnswer for MatchTheFollowing
                             *  isMultipleAnswer is used for checking that answer of the question is given(true) or not given(false)
                             *  multipleAnswerPosition is used to find out the options selected for particular question
                             */
                            if (questionDumpList.get(j).getAnswer().equals("")) {
                                multipleAnswerPosition = new StringBuilder("-1");
                                isMultipleAnswer = false;
                            } else if (questionDumpList.get(j).getAnswer().equals("0")) {
                                multipleAnswerPosition = new StringBuilder("0");
                                isMultipleAnswer = false;
                            } else {
                                multipleAnswerPosition = new StringBuilder();
                                String[] answers = questionDumpList.get(j).getAnswer().split(",");
                                for (String answer : answers) {
                                    questionBankList.get(i).mtfAnswer.add(answer);
                                    multipleAnswerPosition.append(answer).append(",");
                                }
                                questionBankList.get(i).setAnswers(questionBankList.get(i).mtfAnswer);
                                isMultipleAnswer = true;
                            }
                            questionBankList.get(i).setAnswered(true);
                            questionBankList.get(i).setIsMultipleAnswer(isMultipleAnswer, multipleAnswerPosition.toString());
                        } else if (questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.FILL_IN_THE_BLANKS)) {
                            if (questionDumpList.get(j).getAnswer().equals("")) {
                                multipleAnswerPosition = new StringBuilder("-1");
                                isMultipleAnswer = false;
                            } else if (questionDumpList.get(j).getAnswer().equals("0")) {
                                multipleAnswerPosition = new StringBuilder("0");
                                isMultipleAnswer = false;
                            } else {
                                multipleAnswerPosition = new StringBuilder(questionDumpList.get(j).getAnswer());
                                isMultipleAnswer = true;
                                questionBankList.get(i).setAnswerFIB(questionDumpList.get(j).getAnswer());
                            }
                            questionBankList.get(i).setAnswered(true);
                            questionBankList.get(i).setIsMultipleAnswer(isMultipleAnswer, multipleAnswerPosition.toString());
                        }

                        boolean markForReview;
                        markForReview = !questionDumpList.get(j).getReview().equals("0") && !questionDumpList.get(j).getReview().isEmpty();

                        questionBankList.get(i).setMarkForReview(markForReview);

                        if (!questionDumpList.get(j).getOnscreen().equals("")) {
                            questionBankList.get(i).setTotalTimeSpent(Integer.parseInt(questionDumpList.get(j).getOnscreen()));
                        }
                        questionBankList.get(i).setIsguess(questionDumpList.get(j).getGuess());
                    }
                }
            }
        }

        for (int i = 0; i < basicInfo.getParts().size(); i++) {
            basicInfo.getParts().get(i).setAttemptLimit(0);
        }

        for (int i = 0; i < basicInfo.getSection().size(); i++) {
            for (int j = 0; j < basicInfo.getParts().size(); j++) {
                if (basicInfo.getSection().get(i).getSectionTitle().equals(basicInfo.getParts().get(j).getId())) {
                    Log.e(TAG, "setTestData: attempt_limit = " + (basicInfo.getParts().get(j).getAttemptLimit() + basicInfo.getSection().get(i).getAttemptLimit()));
                    basicInfo.getParts().get(j).setAttemptLimit((basicInfo.getParts().get(j).getAttemptLimit() + basicInfo.getSection().get(i).getAttemptLimit()));
                }
            }
        }

        partList = (ArrayList<Part>) data.getBasicInfo().getParts();

        for (int i = 0; i < questionBankList.size(); i++) {
            addFragment(i);
        }

        int answerIndex;
        if (testSeriesBase.getLastanswerPosition() == -1 || testSeriesBase.getLastanswerPosition() == 0) {
            answerIndex = 1;
        } else {
            answerIndex = testSeriesBase.getLastanswerPosition();
        }
        tvQuestionNumber.setText(String.format("Question %d/%d", answerIndex, questionBankList.size()));
        viewPagerTest.setOffscreenPageLimit(1);
        pagerAdapter = new TestViewPagerAdapter(getSupportFragmentManager(), TestBaseActivity.this, mFragmentList, questionBankList.size());
        viewPagerTest.setAdapter(pagerAdapter);

        rvQuestionAdapter = new MyRecyclerAdapterTwo(questionBankList, TestBaseActivity.this, items, R.layout.single_row_testpad_no, TestBaseActivity.this, "0");
        rvNumberPadAdapter = new MyRecyclerAdapter(questionDumpList, questionBankList, TestBaseActivity.this, items, R.layout.single_row_testpad_no, TestBaseActivity.this);

        rlQuestionPad.setAdapter(rvQuestionAdapter);
        GridLayoutManager manager = new GridLayoutManager(TestBaseActivity.this, 6, GridLayoutManager.VERTICAL, false);
        rlQuestionPad.setLayoutManager(manager);
        rlQuestionPad.setItemAnimator(new DefaultItemAnimator());

        rvNumberPad.setAdapter(rvNumberPadAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManagerWithSmoothScroller(TestBaseActivity.this, LinearLayoutManager.HORIZONTAL, false);

        rvNumberPad.setLayoutManager(linearLayoutManager);
        rvNumberPad.setItemAnimator(new DefaultItemAnimator());

        viewPagerTest.setCurrentItem(testSeriesBase.getLastanswerPosition());
        if (testSeriesBase.getData().getBasicInfo().getParts() == null || testSeriesBase.getData().getBasicInfo().getParts().isEmpty()) {
            textSpinner.setVisibility(View.GONE);
        } else {
            textSpinner.setVisibility(View.VISIBLE);
        }


        for (int i = 0; i < partList.size(); i++) {
            partList.get(i).setIndex(getIndexOf(questionBankList, partList.get(i).getId()));
            Log.e("setTestData", "onPageScrollStateChanged" + getIndexOf(questionBankList, partList.get(i).getId()));
            questionPart.add(partList.get(i).getPartName());
        }

        popUp = popupWindowPart();

        setPart();

    }

    private void setPart() {
        boolean textIsSet = false;
        for (int i = 0; i < partList.size() - 1; i++) {
            if (testSeriesBase.getLastanswerPosition() < partList.get(i + 1).getIndexOf()) {
                textSpinner.setText(questionPart.get(i));
                textIsSet = true;
                break;
            }
        }
        if (!textIsSet && partList.size() > 0) {
            textSpinner.setText(questionPart.get(partList.size() - 1));
        }
    }

    public int getIndexOf(List<QuestionBank> list, String name) {
        int pos = 0;

        for (QuestionBank myObj : list) {
            if (name.equalsIgnoreCase(myObj.getPart())) {
                return pos;
            }
            pos++;
        }

        return -1;
    }

    private void setReference() {
        llDrawerRight = findViewById(R.id.llDrawerRight);
        drawerLayout = findViewById(R.id.drawerLayout);
        rootCL = findViewById(R.id.rootCL);
        imgTestBack = findViewById(R.id.img_testback);
        imgTestMenu = findViewById(R.id.img_testmenu);
        rlQuestionPad = findViewById(R.id.rl_questionpad);
        rvNumberPad = findViewById(R.id.rvnumberpad);
        viewPagerTest = findViewById(R.id.view_pager_test);

        btnNext = findViewById(R.id.btn_next);
        btnPrev = findViewById(R.id.btn_prev);
        btnClear = findViewById(R.id.btn_clear);
        tvTime = findViewById(R.id.tv_time);
        btnFinish = findViewById(R.id.btn_finish);
        llMarkForReviewCount = findViewById(R.id.llMarkForReviewCount);

        nextTV = findViewById(R.id.nextTV);
        textSpinner = findViewById(R.id.text_spinner);
        tvQuestionNumber = findViewById(R.id.tv_questionnumber);
        tvAnswerCount = findViewById(R.id.tv_answer_count);
        tvUnAnswerCount = findViewById(R.id.tv_unanswer_count);
        testSeriesName = findViewById(R.id.testSeriesName);
        tvMarkForReviewCount = findViewById(R.id.tv_markforReview_count);
        btnSubmit = findViewById(R.id.btn_submit);
        imgPause = findViewById(R.id.img_pause);
        gridView = findViewById(R.id.gridView);
        listView = findViewById(R.id.listView);

        if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
            btnClear.setVisibility(View.GONE);
            tvMarkForReviewCount.setVisibility(View.GONE);
            llMarkForReviewCount.setVisibility(View.GONE);
        } else if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.DAILY_CHALLENGE)) {
            llMarkForReviewCount.setVisibility(View.GONE);
            tvMarkForReviewCount.setVisibility(View.GONE);
            btnClear.setVisibility(View.VISIBLE);
        } else {
            llMarkForReviewCount.setVisibility(View.VISIBLE);
            tvMarkForReviewCount.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
        }
        textSpinner.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        imgPause.setOnClickListener(this);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        questionBankList = new ArrayList<>();
        imgTestBack.setOnClickListener(this);
        imgTestMenu.setOnClickListener(this);


        viewPagerTest.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("onPageScrolled: ", String.valueOf(position));
                setPart();
                changeTextOnNextAndPrevButton();
            }


            @Override
            public void onPageSelected(final int position) {
                Log.e("onPageSelected: ", String.valueOf(position));
                currentPage = position;
                rvNumberPad.scrollToPosition(currentPage);
                if (rvNumberPad.getLayoutManager() != null) {
                    rvNumberPad.getLayoutManager().smoothScrollToPosition(rvNumberPad, new RecyclerView.State(), currentPage);
                }
                if (rvNumberPadAdapter != null) {
                    rvNumberPadAdapter.setSelectePosition(position);
                }

                if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.REASON_ASSERTION) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.EXTENDED_MATCHING) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_COMPLETION)) {
                    if (questionBankList.get(position).getAnswerPosttion() == -1) {
                        questionBankList.get(position).setIsanswer(false, 0);
                    }
                } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_CHOICE)) {
                    if (questionBankList.get(position).getMultipleAnswerPosition().equalsIgnoreCase("-1")) {
                        questionBankList.get(position).setIsMultipleAnswer(false, "0");
                    }
                } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_TRUE_FALSE) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.TRUE_FALSE)) {
                    if (questionBankList.get(position).getMultipleAnswerPosition().equalsIgnoreCase("-1")) {
                        questionBankList.get(position).setIsMultipleAnswer(false, "0");
                    }
                } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MATCH_THE_FOLLOWING)) {
                    if (questionBankList.get(position).getMultipleAnswerPosition().equalsIgnoreCase("-1")) {
                        questionBankList.get(position).setIsMultipleAnswer(false, "0");
                    }
                } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.FILL_IN_THE_BLANKS)) {
                    if (questionBankList.get(position).getMultipleAnswerPosition().equalsIgnoreCase("-1")) {
                        questionBankList.get(position).setIsMultipleAnswer(false, "0");
                    }
                }
                if (data != null) {
                    tvQuestionNumber.setText(String.format("Question %d/%d", position + 1, questionBankList.size()));
                }
                if (testSeriesBase != null) {
                    testSeriesBase.setLastanswerPosition(currentPage);
                }

                new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
                    if (rvQuestionAdapter != null) {
                        rvQuestionAdapter.setSelectePosition(position);
                    }
                    rlQuestionPad.scrollToPosition(position);
                }, 2000);

                saveDataToDB();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        gridView.setOnClickListener(this);
        listView.setOnClickListener(this);
    }

    private void saveDataToDB() {
        // save test series data to local database
        try {
            questionDumpList = new ArrayList<>();
            if (questionBankList != null && !questionBankList.isEmpty()) {
                for (int i = 0; i < questionBankList.size(); i++) {
                    questionDumpList.add(makeQuestionDumpList(i));
                }
            }
            if (data != null && testSeriesBase != null) {
                data.setQuestionBank(questionBankList);
                data.setQuestionDump(questionDumpList);
                data.setBasicInfo(basicInfo);
                testSeriesBase.setData(data);
                Gson gson = new Gson();
                testSeriesDbHelper.updateTestSeries(Integer.valueOf(testSeriesId), state, gson.toJson(testSeriesBase));
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void addFragment(int i) {
        // calling test
        if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
            mFragmentList.add(NewQuestionBankFragment.newInstance(i, basicInfo));
        } else {
            mFragmentList.add(TestSeriesFragmentNew.newInstance(i, basicInfo, openFrom));
        }
    }

    /**
     * show popup window method return PopupWindow
     */
    private PopupWindow popupWindowPart() {

        // initialize a pop up window type
        popupWindow = new PopupWindow(this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_popup, null);
        if (!GenericUtils.isListEmpty(partList)) {
            TextView partATV, partBTV, partCTV;
            LinearLayout partALL;
            LinearLayout partBLL;
            LinearLayout partCLL;
            partATV = view.findViewById(R.id.partATV);
            partBTV = view.findViewById(R.id.partBTV);
            partCTV = view.findViewById(R.id.partCTV);
            partALL = view.findViewById(R.id.partALL);
            partBLL = view.findViewById(R.id.partBLL);
            partCLL = view.findViewById(R.id.partCLL);

            partALL.setVisibility(View.VISIBLE);
            partATV.setText(partList.get(0).getPartName());
            if (partList.size() > 1) {
                partBTV.setText(partList.get(1).getPartName());
                partBLL.setVisibility(View.VISIBLE);
            }
            if (partList.size() > 2) {
                partCTV.setText(partList.get(2).getPartName());
                partCLL.setVisibility(View.VISIBLE);
            }
            partATV.setOnClickListener((View v) -> {
                rvNumberPad.scrollToPosition(partList.get(0).getIndexOf());
                viewPagerTest.setCurrentItem(partList.get(0).getIndexOf());
                textSpinner.setText(questionPart.get(0));
                popupWindow.dismiss();
            });
            partBTV.setOnClickListener((View v) -> {
                rvNumberPad.scrollToPosition(partList.get(1).getIndexOf());
                viewPagerTest.setCurrentItem(partList.get(1).getIndexOf());
                if (questionPart.size() > 1) {
                    textSpinner.setText(questionPart.get(1));
                }
                popupWindow.dismiss();
            });
            partCTV.setOnClickListener((View v) -> {
                rvNumberPad.scrollToPosition(partList.get(2).getIndexOf());
                viewPagerTest.setCurrentItem(partList.get(2).getIndexOf());
                if (questionPart.size() > 2) {
                    textSpinner.setText(questionPart.get(2));
                }
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

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TestBaseActivity", "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("TestBaseActivity", "onStop");
        cancelQbankTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("TestBaseActivity", "onPause");
        state = "0";
        if (!questionBankList.isEmpty()) {
            questionBankList.get(currentPage).setPause(true);
        }

        saveDataToDB();
    }

    private void setTimer() {
        status = true;
        if (!GenericUtils.isListEmpty(questionBankList) && questionBankList.size() > currentPage) {
            if (questionBankList.get(currentPage).isPause()) {
                millisInFuture = data.getBasicInfo().getTimeRemaining();
            } else {
                if (!data.getBasicInfo().getTimeInMins().equalsIgnoreCase("")) {
                    millisInFuture = (Integer.parseInt(data.getBasicInfo().getTimeInMins()) * 60) * 1000 - Integer.parseInt(data.getTimeSpent().equals("") ? "0" : data.getTimeSpent()) * 1000;
                } else {
                    millisInFuture = (data.getQuestionBank().size() * 60) * 1000 - Integer.parseInt(data.getTimeSpent().equals("") ? "0" : data.getTimeSpent()) * 1000;
                }
            }

            countDownTimer = new CountDownTimer(millisInFuture, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    count = millisUntilFinished / 1000;
                    tvTime.setText(formatSeconds((int) count));
                    data.getBasicInfo().setTimeRemaining(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    if (!isFinishing()) {
                        status = false;
                        tvTime.setText("00:00:00");
                        questionBankList.get(currentPage).setPause(false);
                        state = "0";
                        View v = Helper.newCustomDialog(TestBaseActivity.this, false, getString(R.string.time_out), getString(R.string.your_test_time_is_over_click_ok_for_submit));

                        Space space;
                        Button btnCancel;
                        Button btnSubmit;

                        space = v.findViewById(R.id.space);
                        btnCancel = v.findViewById(R.id.btn_cancel);
                        btnSubmit = v.findViewById(R.id.btn_submit);

                        btnCancel.setVisibility(View.GONE);
                        space.setVisibility(View.GONE);

                        btnSubmit.setText(R.string.ok);
                        btnSubmit.setOnClickListener(v1 -> {
                            Helper.dismissDialog();
                            finishTestSeries();
                        });
                    }
                }
            };
            countDownTimer.start();
        } else {
            finish();
            Toast.makeText(this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("TestBaseActivity", "onDestroy");
//        if (isForceFinish)
//        {
//            state="1";
//            finishTestSeries();
//        }
        super.onDestroy();
    }

    private void finishTestSeries() {
        if (questionBankList != null && !questionBankList.isEmpty()) {
            lastView = questionBankList.get(currentPage).getId();
            answerList = new ArrayList<>();
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
                        if (!data.getBasicInfo().getTimeInMins().equalsIgnoreCase("")) {
                            finalResponse.put(Const.TIME_SPENT, getTimeSpentInMinutes());
                        } else {
                            finalResponse.put(Const.TIME_SPENT, getTimeSpentOnQuestionCount());
                        }
                    } else {
                        finalResponse.put(Const.TIME_SPENT, getTimeSpentOnQuestionCount());
                    }
                } else {

                    if (!data.getBasicInfo().getTimeInMins().equalsIgnoreCase("")) {
                        finalResponse.put(Const.TIME_SPENT, getTimeSpentInMinutes());
                    } else {
                        finalResponse.put(Const.TIME_SPENT, getTimeSpentOnQuestionCount());
                    }
                }
            } else if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
                if (SharedPreference.getInstance().getMasterHitResponse().getQbank_timer().equalsIgnoreCase("1")) {
                    if (!data.getBasicInfo().getTimeInMins().equalsIgnoreCase("")) {
                        finalResponse.put(Const.TIME_SPENT, getTimeSpentInMinutes());
                    } else {
                        finalResponse.put(Const.TIME_SPENT, getTimeSpentOnQuestionCount());
                    }
                } else {
                    finalResponse.put(Const.TIME_SPENT, getTimeSpentOnQuestionCount());
                }
            } else if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.DAILY_CHALLENGE)) {
                if (Const.DAILY_QUIZ_TEST.equals("")){
                    SharedPreference.getInstance().saveDailyQuizStatus(Const.DAILY_QUIZ_TEST, "true");
                }else if (Const.DAILY_QUIZ_TEST.equals("false")){
                    SharedPreference.getInstance().saveDailyQuizStatus(Const.DAILY_QUIZ_TEST, "false");
                }


                //Toast.makeText(this, "dfjhkdhgkjfhkjg", Toast.LENGTH_SHORT).show();

                if (!data.getBasicInfo().getTimeInMins().equalsIgnoreCase("")) {
                    finalResponse.put(Const.TIME_SPENT, getTimeSpentInMinutes());
                } else {
                    finalResponse.put(Const.TIME_SPENT, getTimeSpentOnQuestionCount());
                }
            }
            finalResponse.put(Const.QUESTION_DUMP, jsonStr);
            Log.e("finishTestSeries", "finishTestSeries" + finalResponse.toString());
            if (custom) {
                networkCallSubmitCustomTestSeries(finalResponse);
            } else {
                networkCallSubmitTestSeries(finalResponse);
            }
        } else {
            finish();
            Toast.makeText(this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    private String getTimeSpentOnQuestionCount() {
        return String.valueOf(((data.getQuestionBank().size() * 60) * 1000 - data.getBasicInfo().getTimeRemaining()) / 1000);
    }

    private String getTimeSpentInMinutes() {
        return String.valueOf(((Integer.parseInt(data.getBasicInfo().getTimeInMins()) * 60) * 1000 - data.getBasicInfo().getTimeRemaining()) / 1000);
    }

    public String formatSeconds(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        if (seconds != prevSeconds) {
            questionBankList.get(currentPage).setTotalTimeSpent(questionBankList.get(currentPage).getTotalTimeSpent() + 1);
        }
        prevSeconds = seconds;
        String formattedTime = "";
        if (hours < 10) {
            formattedTime += "0";
        }
        formattedTime += hours + ":";
        if (minutes < 10) {
            formattedTime += "0";
        }
        formattedTime += minutes + ":";

        if (seconds < 10) {
            formattedTime += "0";
        }
        formattedTime += seconds;

        return formattedTime;
    }

    @Override
    public void onBackPressed() {
        if (noTestData) {
            finish();
            return;
        }
        if (daily) {
            ShowPopUpSubmitTestSeries();   // state = "0";    finishTestSeries();   // showPopupSubmitTest();
        } else {
           //ShowPopUpPauseTestSeries();
             showPopupPauseTest();
        }
    }
    @Override
    public void onClick(View view) {
        final Progress progress = new Progress(this);
        progress.setCancelable(false);
        switch (view.getId()) {
            case R.id.btn_next:
                Log.e(TAG, "onClick: currentItem = " + viewPagerTest.getCurrentItem());
                setFIBData();
                if (testSeriesBase != null) {
                    testSeriesBase.setLastanswerPosition(currentPage);
                }
                viewPagerTest.setCurrentItem(viewPagerTest.getCurrentItem() + 1, true);
                break;
            case R.id.btn_prev:
                setFIBData();
                if (testSeriesBase != null) {
                    testSeriesBase.setLastanswerPosition(currentPage);
                }
                viewPagerTest.setCurrentItem(viewPagerTest.getCurrentItem() - 1, true);
                break;
            case R.id.btn_submit:
                progress.show();
                if (!daily) {
                    if (!isFinishing()) {
                        new Handler().postDelayed(() -> {
                            progress.dismiss();
                            showPopupSubmitTest();
                        }, 1000);
                    }
                } else {
                    state = "0";
                    finishTestSeries();
                }
                break;
            case R.id.btn_clear:
                if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)
                        || SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.DAILY_CHALLENGE)) {
                    ((TestSeriesFragmentNew) mFragmentList.get(currentPage)).refreshPage();
                }
                notifyNumberApater();
                break;
            case R.id.img_testmenu:
                countTotalAnswer();
                drawerLayout.openDrawer(llDrawerRight);
                break;
            case R.id.text_spinner:
                if (popUp != null) {
                    popUp.showAsDropDown(view, 0, 0);
                }
                break;
            case R.id.img_testback:
            case R.id.img_pause:
//                showPopupPauseTest();
                onBackPressed();
                break;
            case R.id.gridView:
                SharedPreference.getInstance().putString("VIEW", "0");
                GridLayoutManager manager = new GridLayoutManager(TestBaseActivity.this, 6, GridLayoutManager.VERTICAL, false);
                rlQuestionPad.setLayoutManager(manager);
                gridView.setTextColor(ContextCompat.getColor(this, R.color.black));
                gridView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_grid_list));
                listView.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
                listView.setTextColor(ContextCompat.getColor(this, R.color.white));
                rvQuestionAdapter = new MyRecyclerAdapterTwo(questionBankList, TestBaseActivity.this, items, R.layout.single_row_testpad_no, TestBaseActivity.this, "0");
                rlQuestionPad.setAdapter(rvQuestionAdapter);
                rvQuestionAdapter.selectedPosition = currentPage;
                rlQuestionPad.scrollToPosition(currentPage);
                Log.e(TAG, String.format("onClick: %d", currentPage));
                break;
            case R.id.listView:
                SharedPreference.getInstance().putString("VIEW", "1");
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                rlQuestionPad.setLayoutManager(linearLayoutManager);
                listView.setTextColor(ContextCompat.getColor(this, R.color.black));
                listView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_grid_list));
                gridView.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
                gridView.setTextColor(ContextCompat.getColor(this, R.color.white));
                rvQuestionAdapter = new MyRecyclerAdapterTwo(questionBankList, TestBaseActivity.this, items, R.layout.single_row_testpad_no, TestBaseActivity.this, "1");
                rlQuestionPad.setAdapter(rvQuestionAdapter);
                rvQuestionAdapter.selectedPosition = currentPage;
                rlQuestionPad.scrollToPosition(currentPage);
                Log.e(TAG, "onClick: " + currentPage);
                break;

            case R.id.btn_finish:
                //SharedPreference.getInstance().saveDailyQuizStatus(Const.DAILY_QUIZ_TEST, "true");

                countTotalAnswer();
                progress.show();
                if (!daily) {
                    if (!isFinishing()) {
                        new Handler().postDelayed(() -> {
                            progress.dismiss();
                            showPopupSubmitTest();
                        }, 1000);
                    }
                } else {
                    state = "0";
                    finishTestSeries();

                    //Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    public void setFIBData() {
        if (questionBankList.size() <= currentPage) {
            return;
        }

        if (questionBankList.get(currentPage).getQuestionType().equalsIgnoreCase(Constants.QuestionType.FILL_IN_THE_BLANKS)) {
            Fragment fragment = pagerAdapter.getRegisteredFragment(currentPage);
            if (fragment instanceof TestSeriesFragmentNew) {
                Log.e(TAG, "onClick: " + fragment);
                ((TestSeriesFragmentNew) fragment).setFibAnswers();
                boolean isAnswer = ((TestSeriesFragmentNew) fragment).setIsFibAnswer();
                if (Boolean.TRUE.equals(isAnswer)) {
                    questionBankList.get(currentPage).setIsMultipleAnswer(true, ((TestSeriesFragmentNew) fragment).getFibAnswer());
                } else {
                    questionBankList.get(currentPage).setIsMultipleAnswer(false, "0");
                }

                Log.e(TAG, "ANSWER: " + ((TestSeriesFragmentNew) fragment).getFibAnswer() + "isAnswer:-" + isAnswer);
            }
        }
    }

    public boolean attempLimit() {
        if (!testSeriesBase.getData().getBasicInfo().getSection().isEmpty() && !testSeriesBase.getData().getBasicInfo().getParts().isEmpty()) {
            mPartId = "";
            attempLimit = 0;
            for (int i = 0; i < testSeriesBase.getData().getBasicInfo().getParts().size(); i++) {
                if (testSeriesBase.getData().getBasicInfo().getParts().get(i).getPartName().equals(textSpinner.getText().toString())) {
                    attempLimit = testSeriesBase.getData().getBasicInfo().getParts().get(i).getAttemptLimit();
                    mPartId = testSeriesBase.getData().getBasicInfo().getParts().get(i).getId();
                    break;
                }
            }

            Log.e(TAG, "attempLimit: mPartId = " + mPartId + ", attempLimit = " + attempLimit + ", getAttemptedCount(mPartId) = " + getAttemptedCount(mPartId));

            if (attempLimit > 0) {
                // under devlopment return true , original value is false - nimesh
                return attempLimit > getAttemptedCount(mPartId);
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    public int getAttemptedCount(String mPartId) {
        int attemptedCount = 0;

        for (int i = 0; i < questionBankList.size(); i++) {
            if (questionBankList.get(i).getPart().equals(mPartId)) {
                if (questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE) || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.REASON_ASSERTION) || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS) || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.EXTENDED_MATCHING) || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_COMPLETION)) {
                    if (questionBankList.get(i).getAnswerPosttion() != -1 && questionBankList.get(i).getAnswerPosttion() != -0) {
                        ++attemptedCount;
                    }
                } else if (questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_CHOICE)) {
                    if (!questionBankList.get(i).getMultipleAnswerPosition().equalsIgnoreCase("-1") && !questionBankList.get(i).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                        ++attemptedCount;
                    }
                } else if (questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_TRUE_FALSE) || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MATCH_THE_FOLLOWING) || questionBankList.get(i).getQuestionType().equalsIgnoreCase(Constants.QuestionType.TRUE_FALSE)) {
                    if (!questionBankList.get(i).getMultipleAnswerPosition().equalsIgnoreCase("-1") && !questionBankList.get(i).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                        ++attemptedCount;
                    }

                }
            }
        }
        Log.e(TAG, "getAttemptedCount: attemptedCount = " + attemptedCount);
        return attemptedCount;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void changeTextOnNextAndPrevButton() {
        int currentPosition = viewPagerTest.getCurrentItem() + 1;
        if (currentPosition == questionBankList.size()) {
            btnFinish.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
        } else {
            btnFinish.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
        }

        if (currentPosition == 1) {
            btnPrev.setBackground(ContextCompat.getDrawable(this, R.drawable.background_bg_prev));
        } else {
            btnPrev.setBackground(ContextCompat.getDrawable(this, R.drawable.background_bg_next));
        }
    }

    public QuestionDump makeQuestionDumpList(int position) {
        StringBuilder answerPosition = new StringBuilder();

        if (questionBankList.get(position).getAnswer() != null) {
            if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_CHOICE)) {
                if (questionBankList.get(position).getSelectedValue() == null || questionBankList.get(position).getSelectedValue().isEmpty()) {
                    answerPosition = new StringBuilder();
                    unanswered = "";
                } else {
                    for (int j = 0; j < questionBankList.get(position).getSelectedValue().size(); j++) {
                        answerPosition.append(questionBankList.get(position).getSelectedValue().get(j) + 1).append(",");
                    }
                    if (answerPosition.toString().endsWith(",")) {
                        answerPosition = new StringBuilder(answerPosition.substring(0, answerPosition.length() - 1));
                    }
                    unanswered = "1";
                }
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE) ||
                    questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS) ||
                    questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.REASON_ASSERTION) ||
                    questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.EXTENDED_MATCHING) ||
                    questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_COMPLETION)) {
                if (questionBankList.get(position).getAnswerPosttion() == -1) {
                    answerPosition = new StringBuilder();
                    unanswered = "";
                } else if (questionBankList.get(position).getAnswerPosttion() == 0) {
                    answerPosition = new StringBuilder();
                    unanswered = "0";
                } else {
                    answerPosition = new StringBuilder(String.valueOf(questionBankList.get(position).getAnswerPosttion()));
                    unanswered = "1";
                }
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_TRUE_FALSE) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.TRUE_FALSE)) {
                if (questionBankList.get(position).getTfAnswerArrayList() == null || questionBankList.get(position).getTfAnswerArrayList().isEmpty()) {
                    answerPosition = new StringBuilder();
                } else {
                    for (int j = 0; j < questionBankList.get(position).getTfAnswerArrayList().size(); j++) {
                        answerPosition.append(questionBankList.get(position).getTfAnswerArrayList().get(j)).append(",");
                    }
                    if (answerPosition.toString().endsWith(",")) {
                        answerPosition = new StringBuilder(answerPosition.substring(0, answerPosition.length() - 1));
                    }
                }
                unanswered = "1";
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MATCH_THE_FOLLOWING)) {
                if (questionBankList.get(position).getAnswers() == null || questionBankList.get(position).getAnswers().isEmpty()) {
                    answerPosition = new StringBuilder();
                } else {
                    ArrayList<String> sortedList;
                    sortedList = questionBankList.get(position).getAnswers();
                    Collections.sort(sortedList);
                    for (int j = 0; j < questionBankList.get(position).getAnswers().size(); j++) {
                        answerPosition.append(sortedList.get(j)).append(",");
                    }
                    if (answerPosition.toString().endsWith(",")) {
                        answerPosition = new StringBuilder(answerPosition.substring(0, answerPosition.length() - 1));
                    }
                }
                unanswered = "1";
            }
        } else {
            if (questionBankList.get(position).getAnswerPosttion() == -1) {
                answerPosition = new StringBuilder();
                unanswered = "";
            } else if (questionBankList.get(position).getAnswerPosttion() == 0) {
                answerPosition = new StringBuilder();
                unanswered = "0";
            } else {
                answerPosition = new StringBuilder(String.valueOf(questionBankList.get(position).getAnswerPosttion()));
                unanswered = "1";
            }
        }

        QuestionDump questionDump = new QuestionDump();
        questionDump.setQuestionId(questionBankList.get(position).getId());
        if (questionBankList.get(position).isMarkForReview()) {
            questionDump.setReview("1");
        } else {
            questionDump.setReview("0");
        }
        questionDump.setAnswer(answerPosition.toString());
        questionDump.setSectionId(questionBankList.get(position).getSectionId());
        questionDump.setGuess(questionBankList.get(position).getIsguess());
        questionDump.setPart(questionBankList.get(position).getPart());
        questionDump.setOnscreen(String.valueOf(questionBankList.get(position).getTotalTimeSpent()));
        return questionDump;
    }

    public HashMap<String, String> makeAnswerArray(int position) {
        StringBuilder answerPosition = new StringBuilder();

        if (questionBankList.get(position).getAnswer() != null) {
            if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_CHOICE)) {
                if (questionBankList.get(position).getSelectedValue() == null || questionBankList.get(position).getSelectedValue().isEmpty()) {
                    answerPosition = new StringBuilder();
                    unanswered = "";
                } else {
                    for (int j = 0; j < questionBankList.get(position).getSelectedValue().size(); j++) {
                        answerPosition.append(questionBankList.get(position).getSelectedValue().get(j) + 1).append(",");
                    }
                    if (answerPosition.toString().endsWith(",")) {
                        answerPosition = new StringBuilder(answerPosition.substring(0, answerPosition.length() - 1));
                    }
                    unanswered = "1";
                }
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.REASON_ASSERTION) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.EXTENDED_MATCHING) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_COMPLETION)) {
                if (questionBankList.get(position).getAnswerPosttion() == -1) {
                    answerPosition = new StringBuilder();
                    unanswered = "";
                } else if (questionBankList.get(position).getAnswerPosttion() == 0) {
                    answerPosition = new StringBuilder();
                    unanswered = "0";
                } else {
                    answerPosition = new StringBuilder(String.valueOf(questionBankList.get(position).getAnswerPosttion()));
                    unanswered = "1";
                }
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_TRUE_FALSE) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.TRUE_FALSE)) {
                if (questionBankList.get(position).getTfAnswerArrayList() == null || questionBankList.get(position).getTfAnswerArrayList().isEmpty()) {
                    answerPosition = new StringBuilder();
                } else {
                    for (int j = 0; j < questionBankList.get(position).getTfAnswerArrayList().size(); j++) {
                        answerPosition.append(questionBankList.get(position).getTfAnswerArrayList().get(j)).append(",");
                    }
                    if (answerPosition.toString().endsWith(",")) {
                        answerPosition = new StringBuilder(answerPosition.substring(0, answerPosition.length() - 1));
                    }
                }
                unanswered = "1";
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MATCH_THE_FOLLOWING)) {
                if (questionBankList.get(position).getAnswers() == null || questionBankList.get(position).getAnswers().isEmpty()) {
                    answerPosition = new StringBuilder();
                } else {
                    ArrayList<String> sortedList;
                    sortedList = questionBankList.get(position).getAnswers();
                    Collections.sort(sortedList);
                    for (int j = 0; j < questionBankList.get(position).getAnswers().size(); j++) {
                        answerPosition.append(sortedList.get(j)).append(",");
                    }
                    if (answerPosition.toString().endsWith(",")) {
                        answerPosition = new StringBuilder(answerPosition.substring(0, answerPosition.length() - 1));
                    }
                }
                unanswered = "1";
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.FILL_IN_THE_BLANKS)) {
                if (!GenericUtils.isEmpty(questionBankList.get(position).getAnswerFIB())) {
                    answerPosition = new StringBuilder(questionBankList.get(position).getAnswerFIB());
                    unanswered = "1";
                } else {
                    answerPosition = new StringBuilder();
                    unanswered = "";
                }
            }
        } else {
            if (questionBankList.get(position).getAnswerPosttion() == -1) {
                answerPosition = new StringBuilder();
                unanswered = "";
            } else if (questionBankList.get(position).getAnswerPosttion() == 0) {
                answerPosition = new StringBuilder();
                unanswered = "0";
            } else {
                answerPosition = new StringBuilder(String.valueOf(questionBankList.get(position).getAnswerPosttion()));
                unanswered = "1";
            }
        }

        HashMap<String, String> answer = new HashMap<>();
        answer.put(Const.ORDER, String.valueOf(position + 1));
        answer.put(Const.QUESTIONID, questionBankList.get(position).getId());
        answer.put(Const.ANSWER, answerPosition.toString());
        answer.put(Const.UNANSWERED, unanswered);

        answer.put(Const.ONSCREEN, String.valueOf(questionBankList.get(position).getTotalTimeSpent()));
        answer.put(Const.GUESS, questionBankList.get(position).getIsguess());
        answer.put(Const.PART, questionBankList.get(position).getPart());
        answer.put(Const.SECTIONID, questionBankList.get(position).getSectionId());
        answer.put(Const.ISCORRECT, questionBankList.get(position).isIsanswerRight() ? "1" : "0");

        answer.put(Const.ISMARKFORREVIEW, questionBankList.get(position).isMarkForReview() ? "1" : "0");
        return answer;
    }

    /**
     * @param index
     */
    @Override
    public void sendOnclickInd(int index) {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        Log.e("sendOnclickInd: ", String.valueOf(index));
        viewPagerTest.setCurrentItem(index);
        setPart();
    }

    private void networkCallSubmitTestSeries(HashMap<String, String> finalResponse) {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        SharedPreference.getInstance().putBoolean(Const.PICTURE, true);

        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response = apiInterface.submitAiimsTestSeries(finalResponse);
        response.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(Const.RESPONSE, Const.RESPONSE + jsonResponse.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            testSeriesDbHelper.deleteTestSeries(testSeriesId);
                            if (state.equals("0")) {
                                SharedPreference.getInstance().putBoolean(Const.IS_STATE_CHANGE, true);
                                SharedPreference.getInstance().putString(Const.IS_COMPLETE, "0");
                                String testSegmentId;
                                String testResultDate;
                                String testSeriesName;
                                JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                                testSegmentId = data.getString(Constants.Extras.ID);
                                testResultDate = data.getString(Constants.ResultExtras.TEST_RESULT_DATE);
                                testSeriesName = data.getString(Constants.ResultExtras.TEST_SERIES_NAME);

                                if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
                                    ResultTestSeries resultTestSeries = new ResultTestSeries();
                                    resultTestSeries.setTestResultDate(testResultDate);
                                    resultTestSeries.setTestSeriesName(testSeriesName);

                                    if (Constants.Extras.TEST_QUIZ_ACTION.equalsIgnoreCase(testQuizAction)) {

                                        Intent resultScreen = new Intent(TestBaseActivity.this, TestQuizActionActivity.class);
                                        resultScreen.putExtra(Constants.Extras.SUBJECT_NAME, subjectName);
                                        resultScreen.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                                        resultScreen.putExtra(Constants.Extras.QUIZ_TYPE, Constants.ResultExtras.TEXT_PHOTO);
                                        resultScreen.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.COMPLETE);
                                        resultScreen.putExtra(Constants.Extras.QUES_NUM, quizQues);
                                        resultScreen.putExtra(Constants.Extras.RATING, quizQuesRating);
                                        resultScreen.putExtra(Constants.Extras.TITLE_NAME, resultTestSeries.getTestSeriesName());
                                        resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                                        startActivity(resultScreen);
                                        finish();
                                    } else {
                                        Intent resultScreen = new Intent(TestBaseActivity.this, QuizActivity.class);
                                        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_BASIC);
                                        resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                                        resultScreen.putExtra(Constants.Extras.TYPE, Const.QUIZ_RESULT);
                                        resultScreen.putExtra(Const.RESULT_SCREEN, resultTestSeries);
                                        resultScreen.putExtra(Constants.Extras.SUBJECT_NAME, subjectName);
                                        startActivity(resultScreen);
                                        finish();
                                    }

                                } else {
                                    checkResultData(testResultDate, testSegmentId);
                                }
                            } else {
                                SharedPreference.getInstance().putBoolean(Const.IS_STATE_CHANGE, true);
                                SharedPreference.getInstance().putString(Const.IS_COMPLETE, "1");
                            }
                            finish();
                        } else {
                            Toast.makeText(TestBaseActivity.this, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("onResponse: ", response.message());
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showExceptionMsg(TestBaseActivity.this, t);

            }
        });
    }

    private void checkResultData(String testResultDate, String testSegmentId) {
        Intent resultScreen = new Intent(TestBaseActivity.this, QuizActivity.class);
        if (!GenericUtils.isEmpty(testResultDate)) {
            long tsLong = System.currentTimeMillis();
            if (tsLong < (Long.parseLong(testResultDate) * 1000)) {
                resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_AWAIT);
                resultScreen.putExtra(Constants.Extras.DATE, testResultDate);
            } else {
                resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                resultScreen.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                resultScreen.putExtra(Constants.Extras.NAME, testSName);
            }
        } else {
            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
            resultScreen.putExtra(Const.TEST_SERIES_ID, testSeriesId);
            resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
            resultScreen.putExtra(Constants.Extras.NAME, testSName);
        }
        startActivity(resultScreen);
    }

    private void networkCallSubmitCustomTestSeries(HashMap<String, String> finalResponse) {
        if (!Helper.isConnected(this)) {
            Toast.makeText(this, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgress.show();
        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response = apiInterface.submitCustomTestSeries(finalResponse);
        response.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    Log.w("RES", "onResponse: " + response.body().toString());
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        Log.e(Const.RESPONSE, Const.RESPONSE + jsonResponse.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            SharedPreference.getInstance().putString("STATE", state);
                            SharedPreference.getInstance().putString(Constants.ResultExtras.CUSTOM_TYPE, "2");
                            if (state.equals("0")) {

                                String testSeriesId;
                                String correctCount, incorrectCount, nonAttempt, accuracy;
                                JSONObject data = GenericUtils.getJsonObject(jsonResponse);
                                correctCount = data.getString(Constants.ResultExtras.CORRECT_COUNT);
                                incorrectCount = data.getString(Constants.ResultExtras.INCORRECT_COUNT);
                                nonAttempt = data.getString(Constants.ResultExtras.NON_ATTEMPT);
                                accuracy = data.getString(Constants.ResultExtras.ACCURACY);
                                testSeriesId = data.getString(Constants.Extras.ID);

                                ResultTestSeries resultTestSeries = new ResultTestSeries();
                                resultTestSeries.setCorrectCount(correctCount);
                                resultTestSeries.setIncorrectCount(incorrectCount);
                                resultTestSeries.setNonAttempt(nonAttempt);
                                resultTestSeries.setAccuracy(accuracy);
                                resultTestSeries.setTestSeriesId(testSeriesId);
                                SharedPreference.getInstance().setResultTestSeries(resultTestSeries);
                            }
                        } else {
                            SharedPreference.getInstance().putString(Constants.ResultExtras.CUSTOM_TYPE, "2");
                            SharedPreference.getInstance().putString("STATE", state);
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
                Helper.showExceptionMsg(TestBaseActivity.this, t);
            }
        });
    }
    private void ShowPopUpSubmitTestSeries(){
        if (isFinishing()){
            return;
        }
        View v = Helper.newCustomDialog(this, false, getString(R.string.app_name), getString(R.string.do_you_really_want_to_submit_this_test));
        Button btnCancel;
        Button btnSubmit;
        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);
        btnCancel.setText(R.string.no);
        btnSubmit.setText(R.string.yes);
        btnCancel.setOnClickListener(v1 -> Helper.dismissDialog());
        btnSubmit.setOnClickListener(v1 -> {
            Helper.dismissDialog();
            cancelQbankTimer();
            if (testSeriesBase != null) {
                testSeriesBase.setLastanswerPosition(currentPage);
            }
            state = "0";
            finishTestSeries();
        });
    }
    private void ShowPopUpPauseTestSeries(){
        if (isFinishing()){
            return;
        }
/*        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.activity_pause_test);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
        RelativeLayout lytPaused = bottomSheetDialog.findViewById(R.id.lytPaused);
        RelativeLayout lytResume = bottomSheetDialog.findViewById(R.id.lytResume);
        RelativeLayout lytAbort = bottomSheetDialog.findViewById(R.id.lytAbort);
        TextView tvTimer = bottomSheetDialog.findViewById(R.id.tvTimer);
        lytResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                count = millisUntilFinished / 1000;
                tvTimer.setText("Time Remaining:"+formatSeconds((int) count));
                data.getBasicInfo().setTimeRemaining(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (!isFinishing()) {
                }
            }
        };
        countDownTimer.start();
         lytAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                AbortDialog();
            }
         });*/
    }
    private void AbortDialog() {
/*        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.activiity_abort_questions);
        CardView crdCancelAbort = bottomSheetDialog.findViewById(R.id.crdCancelAbort);
        CardView crdAbortSuccess = bottomSheetDialog.findViewById(R.id.crdAbortSuccess);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
        crdCancelAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        crdAbortSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.dismissDialog();
                cancelQbankTimer();
                if (testSeriesBase != null) {
                    testSeriesBase.setLastanswerPosition(currentPage);
                }
                state = "1";
                finishTestSeries();
                Toast.makeText(getApplicationContext(), "Abort Success", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });*/
    }
    private void showPopupPauseTest() {
        if (isFinishing()) {
            return;
        }
        View v = Helper.newCustomDialog(this, false, getString(R.string.app_name), getString(R.string.do_you_really_want_to_pause_this_test));

        Button btnCancel;
        Button btnSubmit;

        btnCancel = v.findViewById(R.id.btn_cancel);
        btnSubmit = v.findViewById(R.id.btn_submit);

        btnCancel.setText(R.string.no);
        btnSubmit.setText(R.string.yes);

        btnCancel.setOnClickListener(v1 -> Helper.dismissDialog());

        btnSubmit.setOnClickListener(v1 -> {
            Helper.dismissDialog();
            cancelQbankTimer();
            if (testSeriesBase != null) {
                testSeriesBase.setLastanswerPosition(currentPage);
            }
            state = "1";
            finishTestSeries();
        });
    }

    private void showPopupSubmitTest() {
        try {
            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v = li.inflate(R.layout.dialog_test_submit, null, false);
            final Dialog quizBasicInfoDialog = new Dialog(this, R.style.Theme_Dialog);
            quizBasicInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            quizBasicInfoDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            quizBasicInfoDialog.setCanceledOnTouchOutside(false);
            quizBasicInfoDialog.setContentView(v);
            quizBasicInfoDialog.show();
            TextView answeredTV, unansweredTV, markedTV, marked1, submitTV1, submitTV2;
            TextView timeRemainingTV;
            TextView totalQuestionTV;
            LinearLayout notDisplayedLayout;
            Button btnCancel, btnSubmit;
            View markedView1, markedView2;
            LinearLayout totalTimeLL;
            View totalTimeView;
            btnCancel = v.findViewById(R.id.btn_cancel);
            answeredTV = v.findViewById(R.id.answeredTV);
            unansweredTV = v.findViewById(R.id.unansweredTV);
            markedTV = v.findViewById(R.id.markedTV);
           /* marked1 = v.findViewById(R.id.marked1);
            markedView1 = v.findViewById(R.id.markedView1);
            markedView2 = v.findViewById(R.id.markedView2);*/
            submitTV1 = v.findViewById(R.id.submitTest1);
            submitTV2 = v.findViewById(R.id.tv_submit_test);
            totalQuestionTV = v.findViewById(R.id.totalQuestionTV);
            timeRemainingTV = v.findViewById(R.id.timeRemainingTV);
            totalTimeLL = v.findViewById(R.id.totalTimeLL);
            totalTimeView = v.findViewById(R.id.totalTimeView);
            //  notDisplayedLayout = v.findViewById(R.id.notDisplayed);
            if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
                SharedPreference.getInstance().saveQbankTestStatus(Const.QBANK_TEST_STATUS, "true");

                if (SharedPreference.getInstance().getMasterHitResponse().getQbank_timer().equalsIgnoreCase("1")) {
                    totalTimeLL.setVisibility(View.VISIBLE);
                    totalTimeView.setVisibility(View.VISIBLE);
                } else {
                    totalTimeLL.setVisibility(View.GONE);
                    totalTimeView.setVisibility(View.GONE);
                }
                submitTV1.setText(getResources().getString(R.string.submit_qbank));
                submitTV2.setText(getResources().getString(R.string.are_you_sure_you_want_to_submit_qbank));
                markedTV.setVisibility(View.GONE);
                /*marked1.setVisibility(View.GONE);
                markedView1.setVisibility(View.GONE);
                markedView2.setVisibility(View.GONE);
                notDisplayedLayout.setVisibility(View.GONE);*/
            } else {
                //  notDisplayedLayout.setVisibility(View.GONE); //iOS has asked to hide this stats for tests.
                SharedPreference.getInstance().saveStudyTestStatus(Const.STUDY_TEST_STATUS, "true");
                submitTV1.setText(getResources().getString(R.string.submit_test));
                submitTV2.setText(getResources().getString(R.string.are_you_sure_you_want_to_submit_test));
                markedTV.setVisibility(View.VISIBLE);
                /*marked1.setVisibility(View.VISIBLE);
                markedView1.setVisibility(View.VISIBLE);
                markedView2.setVisibility(View.VISIBLE);*/
            }
            int unAttemptCount = questionBankList.size() - attemptCount;
            answeredTV.setText(String.valueOf(attemptCount));
            unansweredTV.setText(String.valueOf(unAttemptCount));
            markedTV.setText(String.valueOf(markForReviewCount));
            totalQuestionTV.setText(String.valueOf(questionBankList.size()));
            timeRemainingTV.setText(formatSeconds((int) count));
            btnSubmit = v.findViewById(R.id.btn_submit);
            btnCancel.setOnClickListener(view1 -> quizBasicInfoDialog.dismiss());

            btnSubmit.setOnClickListener(view1 -> {
                quizBasicInfoDialog.dismiss();
                state = "0";
                finishTestSeries();

            });
        } catch (WindowManager.BadTokenException bte) {
            bte.printStackTrace();
        }
    }

    public void notifyNumberApater() {
        rvQuestionAdapter.notifyDataSetChanged();
        rvNumberPadAdapter.notifyDataSetChanged();
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        if (API.API_GET_COMPLETE_INFO_TEST_SERIES.equals(apiType)) {
            params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
            params.put(Const.TESTSERIES_ID, testSeriesId);
        } else if (API.API_GET_COMPLETE_INFO_CUSTOM_TEST_SERIES.equals(apiType)) {
            params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        }
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonString, String apiType) throws JSONException {
        if (API.API_GET_COMPLETE_INFO_TEST_SERIES.equals(apiType)
                || API.API_GET_COMPLETE_INFO_CUSTOM_TEST_SERIES.equals(apiType)) {
            if (jsonString.optString(Const.STATUS).equals(Const.TRUE)) {
                /*new code  start*/
                Log.e(TAG, "successCallBack: "+jsonString);
                Gson gson = new Gson();
                try {
                    testSeriesBase = gson.fromJson(jsonString.toString(), TestseriesBase.class);
                    testSeriesDbHelper.insertTestSeries(Integer.valueOf(testSeriesId), state, gson.toJson(testSeriesBase));
                    data = testSeriesBase.getData();
                    basicInfo = testSeriesBase.getData().getBasicInfo();
                    testSeriesName.setText(basicInfo.getTestSeriesName());
                    setTestData();
                    setTimerVisibility();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                /*new code  end*/
            } else {
                this.errorCallBack(jsonString.optString(Constants.Extras.MESSAGE), apiType);
            }
        }
    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        noTestData = apiType.equals(API.API_GET_COMPLETE_INFO_TEST_SERIES);
        GenericUtils.showToast(this, jsonString);
    }

    public void setTimerVisibility() {
        // if it's a Test type and in test if its Daily Quiz(Challenge) then we check condition of qbank_timer, if condition is true then timer is started
        // if condition is false then timer not started
        // if its test but not Daily Quiz then timer is started
        // Now if its a Qbank then we check condition of qbank_timer, if condition is true then timer is started
        // if condition is false then timer not started
        if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
            if (daily) {
                if (SharedPreference.getInstance().getMasterHitResponse().getQbank_timer().equalsIgnoreCase("1")) {
                    tvTime.setVisibility(View.VISIBLE);
                    setTimer();
                } else {
                    tvTime.setVisibility(View.GONE);
                }
            } else {
                tvTime.setVisibility(View.VISIBLE);
                setTimer();
            }
        } else if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
            if (SharedPreference.getInstance().getMasterHitResponse().getQbank_timer().equalsIgnoreCase("1")) {
                tvTime.setVisibility(View.VISIBLE);
                setTimer();
            } else {
                tvTime.setVisibility(View.GONE);
            }
        } else if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.DAILY_CHALLENGE)) {
            tvTime.setVisibility(View.VISIBLE);
            setTimer();
        }
    }

    private void cancelQbankTimer() {
        // if it's a Test type and in test if its Daily Quiz(Challenge) then we check condition of qbank_timer, if condition is true then cancel timer
        // Now if its a Qbank then we check condition of qbank_timer, if condition is true then cancel timer

        if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.TEST)) {
            if (Boolean.TRUE.equals(daily) && SharedPreference.getInstance().getMasterHitResponse() != null && SharedPreference.getInstance().getMasterHitResponse().getQbank_timer().equalsIgnoreCase("1")) {
                countDownTimer.cancel();
            }
        } else if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)
                && SharedPreference.getInstance().getMasterHitResponse() != null
                && SharedPreference.getInstance().getMasterHitResponse().getQbank_timer().equalsIgnoreCase("1")) {
            countDownTimer.cancel();
        }
    }
}
