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
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.model.BasicInfo;
import com.emedicoz.app.testmodule.model.QuestionBank;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class NewQuestionBankFragment extends Fragment implements View.OnClickListener {
    Activity activity;
    Progress mProgress;
    LinearLayout mcqOptionsLL;
    TextView percentageTextTV;
    TextView percentageTextTV2;
    TextView percentageTV;
    CircularProgressIndicator circularProgressIndicator;
    LinearLayout percentageLL;
    LinearLayout percentageBottomLL;
    ImageView percentageIV;
    LinearLayout parentLL;
    NestedScrollView nestedSV;
    List<View> LinearLayoutList;
    List<View> parentList;
    List<View> tvList;
    List<View> tickList;
    List<View> percentageTvList;
    List<View> percentageLLList;
    List<View> percentageIVList;
    List<String> attemptList;
    ArrayList<Integer> selectedValue;
    RelativeLayout markForReview;
    ImageView imageTick, imgExplanation, imgQuestion;
    int position;
    LinearLayout explanationLL;
    BasicInfo basicInfo;
    int selectedAnswerPosition;
    TextView explanationTV;
    ImageView imgGuess;
    String multipleAnswerPosition;
    TextView text;
    int clicked = 0;
    private TestBaseActivity testBaseActivity;
    private boolean status;
    private ImageView imgBookmark, imgShare;
    private RelativeLayout checkBoxGuess;

    private WebView tableView;
    private View testReferenceRootLayout;
    private TextView referenceText;
    private ImageView referenceIcon;

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //   percentageTextTV.setVisibility(View.VISIBLE);
            selectedAnswerPosition = (int) view.getTag();
            int selectedOption = selectedAnswerPosition + 1;

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
            testBaseActivity.questionBankList.get(position).setIsanswer(true, selectedOption);
            if (testBaseActivity.questionBankList.get(position).getAnswer().contains(",")) {

                String[] answers = testBaseActivity.questionBankList.get(position).getAnswer().split(",");
                ArrayList<String> answerList = new ArrayList<>(Arrays.asList(answers));
                if (answerList.contains(String.valueOf(selectedOption))) {

                    int index = answerList.indexOf(String.valueOf(selectedOption));
                    LinearLayoutList.get(Integer.parseInt(answerList.get(index)) - 1).setSelected(true);
                //    setCorrectPercentage(Constants.QuestionType.MULTIPLE_CHOICE, ((LinearLayout) percentageLLList.get(Integer.parseInt(answerList.get(index)) - 1)), ((TextView) percentageTvList.get(Integer.parseInt(answerList.get(index)) - 1)), attemptList.get(Integer.parseInt(answerList.get(index)) - 1), "You Marked ");
                    multipleAnswerPosition = multipleAnswerPosition + selectedAnswerPosition + ",";
                    testBaseActivity.questionBankList.get(position).setAnswered(true);
                    clicked++;

                } else {
                    if (!questionBank.getDescription().equals(""))
                        explanationLL.setVisibility(View.VISIBLE);
                    else
                        explanationLL.setVisibility(View.GONE);
                    parentList.get(selectedOption - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_wrong_answer));
                    tvList.get(selectedOption - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_wrong));
                    ((TextView) tvList.get(selectedOption - 1)).setTextColor(ContextCompat.getColor(activity, R.color.black));
                //    setIncorrectPercentage(((LinearLayout) percentageLLList.get(selectedOption - 1)), ((TextView) percentageTvList.get(selectedOption - 1)), ((ImageView) percentageIVList.get(selectedOption - 1)), attemptList.get(selectedOption - 1));
                    for (int i = 0; i < answerList.size(); i++) {
                        LinearLayoutList.get(Integer.parseInt(answerList.get(i)) - 1).setSelected(true);
                  //      setCorrectPercentage(Constants.QuestionType.MULTIPLE_CHOICE, ((LinearLayout) percentageLLList.get(Integer.parseInt(answerList.get(i)) - 1)), ((TextView) percentageTvList.get(Integer.parseInt(answerList.get(i)) - 1)), attemptList.get(Integer.parseInt(answerList.get(i)) - 1), "You Missed ");
                    }
                    multipleAnswerPosition = multipleAnswerPosition + selectedAnswerPosition + ",";

                    testBaseActivity.questionBankList.get(position).setAnswered(true);
                    for (int i = 0; i < LinearLayoutList.size(); i++) {
                        if (testBaseActivity.questionBankList.get(position).isAnswered()) {
                            LinearLayoutList.get(i).setClickable(false);
                        } else {
                            LinearLayoutList.get(i).setClickable(true);
                        }
                    }
                }

                if (clicked == answerList.size()) {
                    if (!questionBank.getDescription().equals(""))
                        explanationLL.setVisibility(View.VISIBLE);
                    else
                        explanationLL.setVisibility(View.GONE);

/*                    for (int i = 0; i < answerList.size(); i++)
                        percentageLLList.get(Integer.parseInt(answerList.get(i))).setVisibility(View.VISIBLE);*/
                }

                selectedValue.add(selectedAnswerPosition);
                ((TestBaseActivity) activity).questionBankList.get(position).setSelectedValue(selectedValue);
                ((TestBaseActivity) activity).questionBankList.get(position).setIsMultipleAnswer(true, multipleAnswerPosition);

                Log.e("MAP: ", multipleAnswerPosition);
            } else {
                if (!questionBank.getDescription().equals(""))
                    explanationLL.setVisibility(View.VISIBLE);
                else
                    explanationLL.setVisibility(View.GONE);
                if (String.valueOf(selectedOption).equals(testBaseActivity.questionBankList.get(position).getAnswer())) {
                    LinearLayoutList.get(selectedOption - 1).setSelected(true);
                    setCorrectPercentage(Constants.QuestionType.SINGLE_CHOICE, ((LinearLayout) percentageLLList.get(selectedOption - 1)), ((TextView) percentageTvList.get(selectedOption - 1)), attemptList.get(selectedOption - 1), "You Marked ");
                } else {
                    if (LinearLayoutList.size() > (Integer.parseInt(testBaseActivity.questionBankList.get(position).getAnswer()) - 1)) {
                        LinearLayoutList.get(Integer.parseInt(testBaseActivity.questionBankList.get(position).getAnswer()) - 1).setSelected(true);
                        setCorrectPercentage(Constants.QuestionType.SINGLE_CHOICE, ((LinearLayout) percentageLLList.get(Integer.parseInt(testBaseActivity.questionBankList.get(position).getAnswer()) - 1)), ((TextView) percentageTvList.get(Integer.parseInt(testBaseActivity.questionBankList.get(position).getAnswer()) - 1)), attemptList.get(Integer.parseInt(testBaseActivity.questionBankList.get(position).getAnswer()) - 1), "You Missed ");
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

                testBaseActivity.questionBankList.get(position).setAnswered(true);
                for (int i = 0; i < LinearLayoutList.size(); i++) {
                    if (testBaseActivity.questionBankList.get(position).isAnswered()) {
                        LinearLayoutList.get(i).setClickable(false);
                    } else {
                        LinearLayoutList.get(i).setClickable(true);
                    }
                }
            }
        }
    };
    private TextView tvQuestion, tvUid, tvEmail;
    private QuestionBank questionBank;
    private String guess;

    public static NewQuestionBankFragment newInstance(int position, BasicInfo basicInfo) {
        Bundle args = new Bundle();
        args.putInt(Const.POSITION, position);
        args.putSerializable(Constants.Extras.BASIC_INFO, basicInfo);
        NewQuestionBankFragment fragment = new NewQuestionBankFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TestBaseActivity)
            testBaseActivity = (TestBaseActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_series, null);
        activity = getActivity();

        getBundleData();
        initView(view);
        mProgress = new Progress(activity);

        return view;
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            basicInfo = (BasicInfo) bundle.getSerializable(Constants.Extras.BASIC_INFO);
            position = bundle.getInt(Const.POSITION, 0);
            Log.e("NewQuestionBankFragment", "getBundleData: " + position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        NetworkAPICall(API.API_GET_LANDING_PAGE_DATA_EXAM, true);
    }

    private void initView(View view) {
        mcqOptionsLL = view.findViewById(R.id.mcqoptions);
        tvQuestion = view.findViewById(R.id.tv_question);
        imgBookmark = view.findViewById(R.id.img_bookmark);
        imgShare = view.findViewById(R.id.img_share);
        checkBoxGuess = view.findViewById(R.id.checkBox);
        imgGuess = view.findViewById(R.id.imgGuess);
        markForReview = view.findViewById(R.id.mark_for_review);
        TextView tvReportError = view.findViewById(R.id.tv_report_error);
        imgExplanation = view.findViewById(R.id.imgExplanation);
        imgQuestion = view.findViewById(R.id.imgQuestion);
        tvUid = view.findViewById(R.id.tv_uid);
        tvEmail = view.findViewById(R.id.tv_email);
        explanationLL = view.findViewById(R.id.explanationLL);
        explanationTV = view.findViewById(R.id.explanationTV);
        nestedSV = view.findViewById(R.id.nestedSV);

        tableView = view.findViewById(R.id.table_view);
        referenceIcon = view.findViewById(R.id.img_ref);
        referenceText = view.findViewById(R.id.txv_ref_title);
        testReferenceRootLayout = view.findViewById(R.id.test_ref_root_layout);
        percentageTextTV2 = view.findViewById(R.id.percentageTextTV);
        percentageTV = view.findViewById(R.id.percentageTV);
        circularProgressIndicator = view.findViewById(R.id.circularProgress);
        percentageBottomLL = view.findViewById(R.id.percentageBottomLL);

        LinearLayoutList = new ArrayList<>();
        parentList = new ArrayList<>();
        tvList = new ArrayList<>();
        tickList = new ArrayList<>();
        percentageTvList = new ArrayList<>();
        percentageIVList = new ArrayList<>();
        percentageLLList = new ArrayList<>();
        attemptList = new ArrayList<>();
        multipleAnswerPosition = "";
        selectedValue = new ArrayList<>();
        clicked = 0;


        if (basicInfo.getDisplay_guess() != null && !basicInfo.getDisplay_guess().equalsIgnoreCase("")
                && basicInfo.getDisplay_guess().equals("0")) {
            checkBoxGuess.setVisibility(View.GONE);
        } else {
            checkBoxGuess.setVisibility(View.VISIBLE);
        }

        if (SharedPreference.getInstance().getString(Constants.Extras.TEST_TYPE).equalsIgnoreCase(Constants.TestType.Q_BANK)) {
            markForReview.setVisibility(View.GONE);
        } else {
            markForReview.setVisibility(View.VISIBLE);
        }
        if (testBaseActivity.questionBankList != null && !testBaseActivity.questionBankList.isEmpty()) {
            questionBank = testBaseActivity.questionBankList.get(position);
            if (questionBank.getDescription().contains("<table")) {
                final Document doc = Jsoup.parse(questionBank.getDescription());
                String tableCode = doc.select("table").toString();
                explanationTV.setText(GenericUtils.removeTagFromHtml(doc, "table"));

                tableView.setVisibility(View.VISIBLE);
                tableView.loadDataWithBaseURL(null, tableCode, "text/html", "utf-8", null);

            } else if (questionBank.getDescription().contains("<img")) {
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
            String htmlAsString = testBaseActivity.questionBankList.get(position).getQuestion();// used by WebView
            tvUid.setText(String.format("eMedicoz QUID %s %s", questionBank.getId(), questionBank.getQuestionType()));

            if (questionBank.getQuestion().contains("<table")) {
                final Document doc = Jsoup.parse(questionBank.getQuestion());
                String tableCode = doc.select("table").toString();

                tvQuestion.setText(GenericUtils.removeTagFromHtml(doc, "table"));

                tableView.setVisibility(View.VISIBLE);
                tableView.loadDataWithBaseURL(null, tableCode, "text/html", "utf-8", null);

            } else if (questionBank.getQuestion().contains("<img")) {
                imgQuestion.setVisibility(View.VISIBLE);
                final Document doc = Jsoup.parse(questionBank.getQuestion());
                tvQuestion.setText(doc.body().text());
                String imageUrl = doc.select("img").attr("src");
                Glide.with(activity).load(imageUrl).into(imgQuestion);
                imgQuestion.setOnClickListener(view12 -> Helper.fullScreenImageDialog(activity, imageUrl));
            } else {
                tvQuestion.setText(HtmlCompat.fromHtml(htmlAsString, HtmlCompat.FROM_HTML_MODE_LEGACY));
            }

            if (!GenericUtils.isEmpty(questionBank.getQuestion_reference())) {
                referenceText.setText(questionBank.getQuestion_reference());
                Glide.with(activity).load(questionBank.getQuestion_reference_icon()).into(referenceIcon)
                        .onLoadFailed(getResources().getDrawable(R.mipmap.daily_quiz_logo));
                testReferenceRootLayout.setVisibility(View.VISIBLE);
            } else
                testReferenceRootLayout.setVisibility(View.GONE);

            addQuestionOption();
            imgBookmark.setOnClickListener(this);
//            imgShare.setOnClickListener(this);
            imgShare.setVisibility(View.GONE);
            checkBoxGuess.setOnClickListener(this);
            markForReview.setOnClickListener(this);
            tvReportError.setOnClickListener(this);

            if (testBaseActivity.questionBankList.get(position).getIsBookmarked().equals("1")) {
                imgBookmark.setImageResource(R.mipmap.bookmarked);

            } else {
                imgBookmark.setImageResource(R.mipmap.bookmark);

            }

            if (testBaseActivity.questionBankList.get(position).getIsguess().equals("1")) {
                checkBoxGuess.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_unmark_review));
                imgGuess.setImageResource(R.mipmap.guessing_answer_active);
            } else {
                checkBoxGuess.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_unselected));
                imgGuess.setImageResource(R.mipmap.guessing_answer);
            }
            tvEmail.setText(testBaseActivity.data.getUserInfo().getEmail());
        } else {
            Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
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
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
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
            imgOption.setOnClickListener(view -> Helper.fullScreenImageDialog(activity, imageUrl));
        } else {
            imgOption.setVisibility(View.GONE);
            if (text2.contains("<p>") || text2.contains("<h>") || text2.contains("<b>")) {
                text.setText(HtmlCompat.fromHtml(text2, HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                text.setText(text2);
            }
        }

        v.setTag(tag);
        if (testBaseActivity.questionBankList.get(position).getAnswer().contains(",")) {
            if (!testBaseActivity.questionBankList.get(position).getMultipleAnswerPosition().equals("-1") && !testBaseActivity.questionBankList.get(position).getMultipleAnswerPosition().equals("0")) {

                String[] answers = testBaseActivity.questionBankList.get(position).getAnswer().split(",");
                ArrayList<String> answerList = new ArrayList<>(Arrays.asList(answers));
                String clickedAnswer = testBaseActivity.questionBankList.get(position).getMultipleAnswerPosition();
                if (clickedAnswer.endsWith(",")) {
                    clickedAnswer = clickedAnswer.substring(0, clickedAnswer.length() - 1);
                }

                String[] myAnswers = clickedAnswer.split(",");
                ArrayList<String> clickedAnswers = new ArrayList<>(Arrays.asList(myAnswers));

                if (clickedAnswers.contains(String.valueOf(tag)) && answerList.contains(String.valueOf(tag + 1))) {
                    v.setSelected(true);
                //    setCorrectPercentage(Constants.QuestionType.MULTIPLE_CHOICE, percentageLL, percentageTextTV, attempt, "You Marked ");
                } else if (clickedAnswers.contains(String.valueOf(tag)) && !answerList.contains(String.valueOf(tag + 1))) {
                    parentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_wrong_answer));
                    tv.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_wrong));
                    tv.setTextColor(ContextCompat.getColor(activity, R.color.black));
                 //   setIncorrectPercentage(percentageLL, percentageTextTV, percentageIV, attempt);
                } else if (!clickedAnswers.contains(String.valueOf(tag)) && answerList.contains(String.valueOf(tag + 1))) {
                    v.setSelected(true);
                 //   setCorrectPercentage(Constants.QuestionType.MULTIPLE_CHOICE, percentageLL, percentageTextTV, attempt, "You Missed ");
                }

                testBaseActivity.questionBankList.get(position).setAnswered(true);
                if (!questionBank.getDescription().equals(""))
                    explanationLL.setVisibility(View.VISIBLE);
                else
                    explanationLL.setVisibility(View.GONE);

                Log.e("initMCQOptionView: ", clickedAnswer);
            }
        } else {
            if (testBaseActivity.questionBankList.get(position).isanswer() && (testBaseActivity.questionBankList.get(position).getAnswerPosttion() != -1 || testBaseActivity.questionBankList.get(position).getAnswerPosttion() != 0)) {
                if (testBaseActivity.questionBankList.get(position).getAnswerPosttion() == tag + 1) {
                    if (String.valueOf(testBaseActivity.questionBankList.get(position).getAnswerPosttion()).equals(testBaseActivity.questionBankList.get(position).getAnswer())) {
                        v.setSelected(true);
                        setCorrectPercentage(Constants.QuestionType.SINGLE_CHOICE, percentageLL, percentageTextTV, attempt, "You Marked ");
                    } else {
                        parentLL.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_wrong_answer));
                        tv.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_wrong));
                        tv.setTextColor(ContextCompat.getColor(activity, R.color.black));
                        setIncorrectPercentage(percentageLL, percentageTextTV, percentageIV, attempt);
                    }
                } else {
                    if (tag + 1 == Integer.parseInt(testBaseActivity.questionBankList.get(position).getAnswer())) {
                        v.setSelected(true);
                        setCorrectPercentage(Constants.QuestionType.SINGLE_CHOICE, percentageLL, percentageTextTV, attempt, "You Missed ");
                    }
                }
                ((TestBaseActivity) activity).questionBankList.get(position).setAnswered(true);
                if (!questionBank.getDescription().equals(""))
                    explanationLL.setVisibility(View.VISIBLE);
                else
                    explanationLL.setVisibility(View.GONE);
            }
        }
        LinearLayoutList.add(v);
        parentList.add(parentLL);
        tvList.add(tv);
        tickList.add(imageTick);
        attemptList.add(attempt);
        percentageTvList.add(percentageTextTV);
        percentageLLList.add(percentageLL);
        percentageIVList.add(percentageIV);
        v.setOnClickListener(onClickListener);
        if (((TestBaseActivity) activity).questionBankList.get(position).isAnswered()) {
            v.setClickable(false);
        } else {
            v.setClickable(true);
        }
        return v;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bookmark:
                if (questionBank != null) {
                    if (questionBank.getIsBookmarked().equals("1")) {
                        imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmark));
                        networkCallForRemoveBookmark(questionBank.getId());
                    } else {
                        imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmarked));
                        networkCallForAddBookmark(questionBank.getId());
                    }
                }

                break;

            case R.id.img_share:
                if (questionBank != null) {
                    Helper.openShareDialog(activity, questionBank);
                }

                break;
            case R.id.checkBox:
                if (testBaseActivity.questionBankList.get(position).getIsguess().equals("0")) {
                    System.out.println("Checked");
                    guess = "1";
                    testBaseActivity.questionBankList.get(position).setIsguess(guess);
                    checkBoxGuess.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_unmark_review));
                    imgGuess.setImageResource(R.mipmap.guessing_answer_active);
                } else {
                    guess = "0";
                    testBaseActivity.questionBankList.get(position).setIsguess(guess);
                    checkBoxGuess.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_unselected));
                    imgGuess.setImageResource(R.mipmap.guessing_answer);
                }
                break;
            case R.id.mark_for_review:
                testBaseActivity.questionBankList.get(position).setMarkForReview(true);
                testBaseActivity.notifyNumberApater();
                break;
            case R.id.tv_report_error:

                showPopupErrorTest();
                break;
            default:
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
            if (isDataValid && radioButton[0] != null) {
                networkCallForSubmitQuery(radioButton[0].getText().toString().trim(), feedbackET.getText().toString().trim(), quizBasicInfoDialog);
            }
        });
    }

    public void networkCallForSubmitQuery(String error, String feedback, final Dialog quizBasicInfoDialog) {
        Progress mprogress = new Progress(activity);
        mprogress.setCancelable(false);
        mprogress.show();
        SubmitQueryApiInterface apiInterface = ApiClient.createService(SubmitQueryApiInterface.class);
        Call<JsonObject> response = apiInterface.getresponseofsubmitquery(SharedPreference.getInstance().getLoggedInUser().getId(), tvUid.getText().toString().trim(),
                feedback, error, "");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.body() != null) {
                    mprogress.dismiss();
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

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).contains(getString(R.string.something_went_wrong_string)))
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
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                mprogress.dismiss();
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
}