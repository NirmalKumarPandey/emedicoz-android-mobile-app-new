package com.emedicoz.app.testmodule.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.SubmitQueryApiInterface;
import com.emedicoz.app.testmodule.activity.VideoTestBaseActivity;
import com.emedicoz.app.testmodule.model.BasicInfo;
import com.emedicoz.app.testmodule.model.QuestionBank;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.video.exoplayer2.ExoPlayerFragment;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoQBFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    Progress mProgress;
    LinearLayout mcqOptionsLL;
    TextView percentageTextTV;
    TextView percentageTextTV2;
    TextView percentageTV;
    LinearLayout parentLL;
    NestedScrollView nestedSV;
    List<View> linearLayoutList = new ArrayList<>();
    List<View> parentList = new ArrayList<>();
    List<View> tvList = new ArrayList<>();
    List<View> tickList = new ArrayList<>();
    List<View> percentageTvList = new ArrayList<>();
    List<View> percentageLLList = new ArrayList<>();
    List<View> percentageIVList = new ArrayList<>();
    List<String> attemptList = new ArrayList<>();
    CircularProgressIndicator circularProgressIndicator;
    LinearLayout percentageLL;
    LinearLayout percentageBottomLL;
    ImageView percentageIV;
    ImageView imageTick;
    ImageView imgExplanation;
    ImageView imgQuestion;
    LinearLayout explanationLL;
    BasicInfo basicInfo;
    int selectedAnswerPosition;
    TextView explanationTV;
    TextView text;
    int i = 0;
    private TextView tvQuestion;
    TextView tvUid;
    TextView tvEmail;
    private TextView tvReportError;
    private RelativeLayout markForReview;
    RelativeLayout unMarkForReview;
    private QuestionBank questionBank;

    private WebView tableView;
    private View testReferenceRootLayout;
    private TextView referenceText;
    private ImageView referenceIcon;

    public View.OnClickListener onClickListener = v -> {
        selectedAnswerPosition = (int) v.getTag();
        int selectedOption = selectedAnswerPosition + 1;
        explanationLL.setVisibility(View.VISIBLE);
        if (questionBank.getDescription().contains("<img")) {
            imgExplanation.setVisibility(View.VISIBLE);
            final Document doc = Jsoup.parse(questionBank.getDescription());
            explanationTV.setText(doc.body().text());
            String imageUrl = doc.select("img").attr("src");
            Glide.with(activity).load(imageUrl).into(imgExplanation);
            imgExplanation.setOnClickListener(view1 -> Helper.fullScreenImageDialog(activity, imageUrl));
        } else {
            imgExplanation.setVisibility(View.GONE);
            if (questionBank.getDescription().contains("<p>") || questionBank.getDescription().contains("<h>") || questionBank.getDescription().contains("<b>")) {
                explanationTV.setText(HtmlCompat.fromHtml(questionBank.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                explanationTV.setText(questionBank.getDescription());
            }
        }

        questionBank.setIsanswer(true, selectedOption);
        if (questionBank.getAnswer().contains(",")) {
            String[] answers = questionBank.getAnswer().split(",");
            for (; i < answers.length; i++) {
                if (String.valueOf(selectedOption).equals(answers[i])) {
                    linearLayoutList.get(selectedOption - 1).setSelected(true);
                    break;
                } else {
                    parentList.get(selectedOption - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_wrong_answer));
                    tvList.get(selectedOption - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_wrong));
                    ((TextView) tvList.get(selectedOption - 1)).setTextColor(ContextCompat.getColor(activity, R.color.black));
                }
            }

        } else {
            if (String.valueOf(selectedOption).equals(questionBank.getAnswer())) {
                linearLayoutList.get(selectedOption - 1).setSelected(true);
                setCorrectPercentage(Constants.QuestionType.SINGLE_CHOICE, ((LinearLayout) percentageLLList.get(selectedOption - 1)), ((TextView) percentageTvList.get(selectedOption - 1)), attemptList.get(selectedOption - 1), "You Marked ");
            } else {
                if (linearLayoutList.size() > (Integer.parseInt(questionBank.getAnswer()) - 1)) {
                    linearLayoutList.get(Integer.parseInt(questionBank.getAnswer()) - 1).setSelected(true);
                    setCorrectPercentage(Constants.QuestionType.SINGLE_CHOICE, ((LinearLayout) percentageLLList.get(Integer.parseInt(questionBank.getAnswer()) - 1)), ((TextView) percentageTvList.get(Integer.parseInt(questionBank.getAnswer()) - 1)), attemptList.get(Integer.parseInt(questionBank.getAnswer()) - 1), "You Missed ");
                }
                parentList.get(selectedOption - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_wrong_answer));
                tvList.get(selectedOption - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_wrong));
                ((TextView) tvList.get(selectedOption - 1)).setTextColor(ContextCompat.getColor(activity, R.color.black));
                percentageLLList.get(selectedOption - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_percentage_incorrect));
                percentageLLList.get(selectedOption - 1).setVisibility(View.VISIBLE);
                ((TextView) percentageTvList.get(selectedOption - 1)).setTextColor(ContextCompat.getColor(activity, R.color.dark_red_new));
                percentageIVList.get(selectedOption - 1).setVisibility(View.GONE);
                ((TextView) percentageTvList.get(selectedOption - 1)).setText("You Marked " + attemptList.get(selectedOption - 1) + " %");

            }

            questionBank.setAnswered(true);
            for (int i = 0; i < linearLayoutList.size(); i++) {
                if (questionBank.isAnswered()) {
                    linearLayoutList.get(i).setClickable(false);
                } else {
                    linearLayoutList.get(i).setClickable(true);
                }
            }
        }
    };
    private boolean status;
    private ImageView imgBookmark, imgShare;
    ImageView imgGuess;
    private RelativeLayout checkBoxGuess;
    private String guess;

    public static VideoQBFragment newInstance(QuestionBank questionBank) {
        Bundle args = new Bundle();
        args.putSerializable(Const.QUESTION_BANK, questionBank);
        VideoQBFragment fragment = new VideoQBFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_test, null);
        activity = getActivity();

        getBundleData();
        initView(view);
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);

        return view;
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            questionBank = (QuestionBank) bundle.getSerializable(Const.QUESTION_BANK);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String questionNew = questionBank.getQuestion().replace("\"", "#");
        Log.e("initView: ", questionNew);
        if (questionBank.getVideo_url() != null && !questionBank.getVideo_url().equals("")) {
            initializePlayer(questionBank.getVideo_url());

            tvQuestion.setText(Helper.getFileName(HtmlCompat.fromHtml(questionBank.getQuestion(), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()));
        } else {
            if (questionBank.getQuestion().contains("<video")) {
                imgQuestion.setVisibility(View.GONE);
                final Document doc = Jsoup.parse(questionBank.getQuestion());
                tvQuestion.setText(doc.body().text());
                String videoUrl = "";
                videoUrl = doc.select("video").attr("src");
                initializePlayer(videoUrl);

            }

        }
        Log.e("onResume: ", "called");
    }

    private void initView(View view) {
        mcqOptionsLL = view.findViewById(R.id.mcqoptions);
        tvQuestion = view.findViewById(R.id.tv_question);
        imgBookmark = view.findViewById(R.id.img_bookmark);
        imgShare = view.findViewById(R.id.img_share);
        checkBoxGuess = view.findViewById(R.id.checkBox);
        markForReview = view.findViewById(R.id.mark_for_review);
        unMarkForReview = view.findViewById(R.id.unmark_for_review);
        tvReportError = view.findViewById(R.id.tv_report_error);
        imgExplanation = view.findViewById(R.id.imgExplanation);
        imgQuestion = view.findViewById(R.id.imgQuestion);
        tvUid = view.findViewById(R.id.tv_uid);

        tableView = view.findViewById(R.id.table_view);
        referenceIcon = view.findViewById(R.id.img_ref);
        referenceText = view.findViewById(R.id.txv_ref_title);
        testReferenceRootLayout = view.findViewById(R.id.test_ref_root_layout);
        percentageTextTV2 = view.findViewById(R.id.percentageTextTV);
        percentageTV = view.findViewById(R.id.percentageTV);
        circularProgressIndicator = view.findViewById(R.id.circularProgress);
        percentageBottomLL = view.findViewById(R.id.percentageBottomLL);

        imgGuess = view.findViewById(R.id.imgGuess);
        tvEmail = view.findViewById(R.id.tv_email);
        explanationLL = view.findViewById(R.id.explanationLL);
        explanationTV = view.findViewById(R.id.explanationTV);
        nestedSV = view.findViewById(R.id.nestedSV);
        markForReview.setVisibility(View.GONE);

        if (((VideoTestBaseActivity) activity).testseriesBase != null) {
            basicInfo = ((VideoTestBaseActivity) activity).testseriesBase.getData().getBasicInfo();
            if (basicInfo.getDisplay_guess() != null && !basicInfo.getDisplay_guess().equalsIgnoreCase("")) {
                if (basicInfo.getDisplay_guess().equals("0")) {
                    checkBoxGuess.setVisibility(View.GONE);
                } else {
                    checkBoxGuess.setVisibility(View.VISIBLE);
                }
            } else {
                checkBoxGuess.setVisibility(View.VISIBLE);
            }
        }
        if (questionBank.getDescription().contains("<table")) {
            final Document doc = Jsoup.parse(questionBank.getDescription());
            String tableCode = doc.select("table").toString();
            tableView.setVisibility(View.VISIBLE);
            tableView.loadDataWithBaseURL(null, tableCode, "text/html", "utf-8", null);

        } else if (questionBank.getDescription().contains("<img")) {
            imgExplanation.setVisibility(View.VISIBLE);
            final Document doc = Jsoup.parse(questionBank.getDescription());
            explanationTV.setText(doc.body().text());
            String imageUrl = doc.select("img").attr("src");
            Glide.with(activity).load(imageUrl).into(imgExplanation);
            imgExplanation.setOnClickListener(v -> Helper.fullScreenImageDialog(activity, imageUrl));
        } else {
            imgExplanation.setVisibility(View.GONE);
            if (questionBank.getDescription().contains("<p>") || questionBank.getDescription().contains("<h>") || questionBank.getDescription().contains("<b>")) {
                explanationTV.setText(HtmlCompat.fromHtml(questionBank.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                explanationTV.setText(questionBank.getDescription());
            }
        }

        if (!GenericUtils.isEmpty(questionBank.getQuestion_reference())) {
            referenceText.setText(questionBank.getQuestion_reference());
            Glide.with(activity).load(questionBank.getQuestion_reference_icon()).into(referenceIcon)
                    .onLoadFailed(getResources().getDrawable(R.mipmap.daily_quiz_logo));
            testReferenceRootLayout.setVisibility(View.VISIBLE);
        } else
            testReferenceRootLayout.setVisibility(View.GONE);
        tvUid.setText(String.format("eMedicoz QUID %s %s", questionBank.getId(), questionBank.getQuestionType()));

        addQuestionOption();
        imgBookmark.setOnClickListener(this);
//        imgShare.setOnClickListener(this);
        imgShare.setVisibility(View.GONE);
        checkBoxGuess.setOnClickListener(this);
        markForReview.setOnClickListener(this);
        unMarkForReview.setOnClickListener(this);
        tvReportError.setOnClickListener(this);

        if (questionBank.getIsBookmarked().equals("1")) {
            imgBookmark.setImageResource(R.mipmap.bookmarked);
        } else {
            imgBookmark.setImageResource(R.mipmap.bookmark);

        }

        if (questionBank.getIsguess().equals("1")) {
            checkBoxGuess.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_unmark_review));
            imgGuess.setImageResource(R.mipmap.guessing_answer_active);
        } else {
            checkBoxGuess.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_unselected));
            imgGuess.setImageResource(R.mipmap.guessing_answer);
        }
        tvEmail.setVisibility(View.GONE);

    }

    private void initializePlayer(String videoUrl) {
        loadPlayerWithVideoURL(videoUrl);
    }

    private void addQuestionOption() {
        for (int j = 1; j <= 15; j++) {
            if (status)
                break;
            switch (j) {
                case 1:
                    if (questionBank.getOption1().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("A", questionBank.getOption1(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption1Attempt()))),
                            j - 1));
                    break;
                case 2:
                    if (questionBank.getOption2().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("B", questionBank.getOption2(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption2Attempt()))),
                            j - 1));
                    break;
                case 3:
                    if (questionBank.getOption3().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("C", questionBank.getOption3(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption3Attempt()))),
                            j - 1));
                    break;
                case 4:
                    if (questionBank.getOption4().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("D", questionBank.getOption4(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption4Attempt()))),
                            j - 1));
                    break;
                case 5:
                    if (questionBank.getOption5().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("E", questionBank.getOption5(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption5Attempt()))),
                            j - 1));
                    break;
                case 6:
                    if (questionBank.getOption6().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("F", questionBank.getOption6(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption6Attempt()))),
                            j - 1));
                    break;
                case 7:
                    if (questionBank.getOption7().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("G", questionBank.getOption7(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption7Attempt()))),
                            j - 1));
                    break;
                case 8:
                    if (questionBank.getOption8().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("H", questionBank.getOption8(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption8Attempt()))),
                            j - 1));
                    break;
                case 9:
                    if (questionBank.getOption9().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("I", questionBank.getOption9(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption9Attempt()))),
                            j - 1));
                    break;
                case 10:
                    if (questionBank.getOption10().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("J", questionBank.getOption10(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption10Attempt()))),
                            j - 1));
                    break;
                case 11:
                    if (questionBank.getOption11().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("K", questionBank.getOption11(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption11Attempt()))),
                            j - 1));
                    break;
                case 12:
                    if (questionBank.getOption12().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("L", questionBank.getOption12(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption12Attempt()))),
                            j - 1));
                    break;
                case 13:
                    if (questionBank.getOption13().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("M", questionBank.getOption13(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption13Attempt()))),
                            j - 1));
                    break;
                case 14:
                    if (questionBank.getOption4().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("N", questionBank.getOption14(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption14Attempt()))),
                            j - 1));
                    break;
                case 15:
                    if (questionBank.getOption15().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("O", questionBank.getOption15(), String.valueOf(Math.round(Float.parseFloat(questionBank.getOption15Attempt()))),
                            j - 1));
                    break;
                default:
            }
        }
        status = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public LinearLayout initMCQOptionView(String text1, String text2, String attempt, int tag) {
        LinearLayout v = (LinearLayout) View.inflate(activity, R.layout.layout_option_test_view, null);
        TextView tv = v.findViewById(R.id.optionIconTV);
        text = v.findViewById(R.id.optionTextTV);
        ImageView imgOption = v.findViewById(R.id.imgOption);
        percentageTextTV = v.findViewById(R.id.percentageTextTV);
        percentageLL = v.findViewById(R.id.percentageLL);
        percentageIV = v.findViewById(R.id.percentageIV);
        parentLL = v.findViewById(R.id.viewLL);
        imageTick = v.findViewById(R.id.imageTick);
        percentageIV.setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 0);

        v.setLayoutParams(lp);
        tv.setText(text1);
        tv.setGravity(Gravity.CENTER);

        if (text2.contains("<img")) {
            imgOption.setVisibility(View.VISIBLE);
            final Document doc = Jsoup.parse(text2);
            text.setText(doc.body().text());
            String imageUrl = doc.select("img").attr("src");
            Glide.with(activity).load(imageUrl).into(imgOption);
            imgOption.setOnClickListener(v1 -> Helper.fullScreenImageDialog(activity, imageUrl));
        } else {
            imgOption.setVisibility(View.GONE);
            if (text2.contains("<p>") || text2.contains("<h>") || text2.contains("<b>")) {
                text.setText(HtmlCompat.fromHtml(text2, HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                text.setText(text2);
            }
        }

        v.setTag(tag);
        if (questionBank.getAnswer().contains(",")) {


        } else {
            if (questionBank.isanswer() && (questionBank.getAnswerPosttion() != -1 || questionBank.getAnswerPosttion() != 0)) {
                if (questionBank.getAnswerPosttion() == tag + 1) {
                    if (String.valueOf(questionBank.getAnswerPosttion()).equals(questionBank.getAnswer())) {
                        v.setSelected(true);
                        setCorrectPercentage(Constants.QuestionType.SINGLE_CHOICE, percentageLL, percentageTextTV, attempt, "You Marked ");
                    } else {
                        parentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_wrong_answer));
                        tv.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_wrong));
                        tv.setTextColor(ContextCompat.getColor(activity, R.color.black));
                        setIncorrectPercentage(percentageLL, percentageTextTV, percentageIV, attempt);
                    }
                } else {
                    if (tag + 1 == Integer.parseInt(questionBank.getAnswer())) {
                        v.setSelected(true);
                        setCorrectPercentage(Constants.QuestionType.SINGLE_CHOICE, percentageLL, percentageTextTV, attempt, "You Missed ");
                    }
                }
                questionBank.setAnswered(true);
                explanationLL.setVisibility(View.VISIBLE);

            }
        }
        linearLayoutList.add(v);
        parentList.add(parentLL);
        tvList.add(tv);
        tickList.add(imageTick);
        attemptList.add(attempt);
        percentageTvList.add(percentageTextTV);
        percentageLLList.add(percentageLL);
        percentageIVList.add(percentageIV);
        v.setOnClickListener(onClickListener);
        if (questionBank.isAnswered()) {
            v.setClickable(false);
        } else {
            v.setClickable(true);
        }
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bookmark:
                if (questionBank.getIsBookmarked().equals("1")) {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmark));
                    networkCallForRemoveBookmark(questionBank.getId());
                } else {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmarked));
                    networkCallForAddBookmark(questionBank.getId());
                }

                break;

            case R.id.img_share:
                if (questionBank != null) {
                    Helper.openShareDialog(activity, questionBank);
                }
                break;
            case R.id.checkBox:
                if (questionBank.getIsguess().equals("0")) {
                    Log.e("TAG", "Checked");
                    guess = "1";
                    questionBank.setIsguess(guess);
                    checkBoxGuess.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_unmark_review));
                    imgGuess.setImageResource(R.mipmap.guessing_answer_active);
                } else {
                    guess = "0";
                    questionBank.setIsguess(guess);
                    checkBoxGuess.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_unselected));
                    imgGuess.setImageResource(R.mipmap.guessing_answer);
                }
                break;
            case R.id.mark_for_review:
                Toast.makeText(activity, "Marked for review.", Toast.LENGTH_SHORT).show();
                questionBank.setMarkForReview(true);
                ((VideoTestBaseActivity) Objects.requireNonNull(getActivity())).notifyNumberAdapter();
                markForReview.setVisibility(View.GONE);
                unMarkForReview.setVisibility(View.VISIBLE);

                break;
            case R.id.unmark_for_review:
                Toast.makeText(activity, "Unmarked for review.", Toast.LENGTH_SHORT).show();
                questionBank.setMarkForReview(false);
                ((VideoTestBaseActivity) Objects.requireNonNull(getActivity())).notifyNumberAdapter();
                markForReview.setVisibility(View.VISIBLE);
                unMarkForReview.setVisibility(View.GONE);

                break;
            case R.id.tv_report_error:

                showPopupErrorTest();
                break;
        }
    }

    private void showPopupErrorTest() {

        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = li.inflate(R.layout.dialog_report_error, null, false);
        final Dialog quizBasicInfoDialog = new Dialog(activity);
        quizBasicInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        quizBasicInfoDialog.setCancelable(true);
        quizBasicInfoDialog.setCanceledOnTouchOutside(false);
        quizBasicInfoDialog.setContentView(v);
        quizBasicInfoDialog.show();
        Button btnSubmit;
        final EditText feedbackET;
        final RadioGroup radioGroup;
        final RadioButton[] radioButton = new RadioButton[1];
        feedbackET = v.findViewById(R.id.feedbackET);
        radioGroup = v.findViewById(R.id.radioError);
        btnSubmit = v.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v1 -> {
            boolean isDataValid = true;
            int selectedId = radioGroup.getCheckedRadioButtonId();
            radioButton[0] = v.findViewById(selectedId);
            if (TextUtils.isEmpty(feedbackET.getText().toString().trim()))
                isDataValid = Helper.DataNotValid(feedbackET);
            if (isDataValid) {
                networkCallForSubmitQuery(radioButton[0].getText().toString().trim(), feedbackET.getText().toString().trim(), quizBasicInfoDialog);
            }
        });
    }

    public void networkCallForSubmitQuery(String error, String feedback, final Dialog quizBasicInfoDialog) {
        mProgress.show();
        SubmitQueryApiInterface apiInterface = ApiClient.createService(SubmitQueryApiInterface.class);
        Call<JsonObject> response = apiInterface.getresponseofsubmitquery(SharedPreference.getInstance().getLoggedInUser().getId(), tvUid.getText().toString().trim(),
                feedback, error, "");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("response", "response" + response.body().toString());
                if (response.body() != null) {
                    mProgress.dismiss();
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            quizBasicInfoDialog.dismiss();
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } else {
                            quizBasicInfoDialog.dismiss();
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
                                Helper.showErrorLayoutForNav(API.API_SUBMIT_QUERY, activity, 1, 1);

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).contains(activity.getString(R.string.something_went_wrong)))
                                Helper.showErrorLayoutForNav(API.API_SUBMIT_QUERY, activity, 1, 2);

                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
                quizBasicInfoDialog.dismiss();
                Helper.showErrorLayoutForNav(API.API_SUBMIT_QUERY, activity, 1, 2);

            }
        });
    }

    private void networkCallForAddBookmark(String id) {

        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response = apiInterface.bookmarkTestSeries(SharedPreference.getInstance().getLoggedInUser().getId(), id, "2");
        response.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                            questionBank.setIsBookmarked("1");
                        }
                        Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void networkCallForRemoveBookmark(String id) {
        ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
        Call<JsonObject> response = apiInterface.removeBookmark(SharedPreference.getInstance().getLoggedInUser().getId(), id);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                            questionBank.setIsBookmarked("0");
                        }
                        Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Toast.makeText(activity, throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadPlayerWithVideoURL(String url) {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                ExoPlayerFragment.newInstance(AES.decrypt(url))).commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setIncorrectPercentage(LinearLayout percentageLL, TextView percentageTextTV, ImageView percentageIV, String attempt) {
        percentageLL.setVisibility(View.VISIBLE);
        percentageTextTV.setTextColor(ContextCompat.getColor(activity, R.color.dark_red_new));
        // percentageIV.setVisibility(View.GONE);
        percentageLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_percentage_incorrect));
        percentageTextTV.setText("You Marked " + attempt + " %");
    }

    private void setCorrectPercentage(String type, LinearLayout percentageLL, TextView percentageTextTV, String attempt, String text) {
        //  if (type.equalsIgnoreCase("SC")) {
        percentageLL.setVisibility(View.VISIBLE);
        percentageBottomLL.setVisibility(View.VISIBLE);
        //}
        percentageTextTV.setTextColor(ContextCompat.getColor(activity, R.color.dark_green_new));
        percentageLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_percentage_correct));
        percentageTextTV.setText(text + attempt + " %");
        circularProgressIndicator.setCurrentProgress(Double.parseDouble(attempt));
        percentageTV.setText(attempt + "%");
        percentageTextTV2.setText(attempt + "% Got it right");
    }
}
