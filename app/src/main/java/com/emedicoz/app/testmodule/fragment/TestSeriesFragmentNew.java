package com.emedicoz.app.testmodule.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.SubmitQueryApiInterface;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.adapter.ImageViewPagerAdapter;
import com.emedicoz.app.testmodule.adapter.MatchTheFollowingAdapter;
import com.emedicoz.app.testmodule.adapter.MatchTheFollowingAdapterTop;
import com.emedicoz.app.testmodule.adapter.TrueFalseAdapter;
import com.emedicoz.app.testmodule.model.BasicInfo;
import com.emedicoz.app.testmodule.model.QuestionBank;
import com.emedicoz.app.testmodule.model.Social;
import com.emedicoz.app.testmodule.model.TrueFalse;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TestSeriesFragmentNew extends Fragment implements View.OnClickListener, TrueFalseAdapter.RadioClickedInterface {

    private static final String TAG = "TestSeriesFragmentNew";
    Activity activity;
    public static Boolean checkVideo = false;
    public ArrayList<Social> items1 = new ArrayList<>();
    public ArrayList<Social> items2 = new ArrayList<>();
    Progress mProgress;
    MatchTheFollowingAdapter matchTheFollowingAdapter;
    LinearLayout mcqOptionsLL;
    LinearLayout fibOptions;
    RelativeLayout fibOptionsRL;
    int count;
    TrueFalseAdapter trueFalseAdapter;
    RecyclerView rvMatchingQuestion1;
    RecyclerView rvMatchingQuestion2;
    RecyclerView trueFalseRV;
    TextView text;
    RelativeLayout unmMarkForReview;
    LinearLayout parentLL;
    LinearLayout llMatchingQuestion;
    NestedScrollView nestedSV;
    List<View> LinearLayoutList;
    List<View> parentList;
    List<View> tvList;
    List<View> radioGroupList;
    int position;
    ArrayList<Integer> selectedValue;
    LinearLayout explanationLL;
    int selectedAnswerposition;
    ArrayList<TrueFalse> trueFalseList = new ArrayList<>();
    TextView explanationTV;
    BasicInfo basicInfo;
    View view;
    TextView tvReportError;
    ImageView imgGuess;
    private boolean status1, status2;
    TextView tvQuestion;
    private TextView tvUid, tvEmail;
    private RelativeLayout markForReview, unmarkForReview;
    private QuestionBank questionBank;
    private boolean status;
    private ImageView imgBookmark, imgQuestion, imgShare;
    private RelativeLayout checkBoxGuess, viewPagerRL;
    private Drawable empty;
    private String guess;
    private String openFrom;
    EditText optionET;
    int selectedAnswerPosition;
    public ArrayList<EditText> editTextList;
    public ArrayList<String> fibAnswers;
    private WebView tableView;
    private View testReferenceRootLayout;
    private TextView referenceText;
    private ImageView referenceIcon;

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e("onClick: ", "CLICK_COUNT " + ((TestBaseActivity) activity).questionBankList.get(position).getCount());
            if (selectedValue == null) {
                selectedValue = new ArrayList<>();
            }
            selectedAnswerPosition = (int) view.getTag();
            if (((TestBaseActivity) activity).questionBankList.get(position).getAnswer().contains(",") ||
                    ((TestBaseActivity) activity).questionBankList.get(position).getQuestionType().equals(Constants.QuestionType.MULTIPLE_CHOICE)) {
                StringBuilder answerPosition = new StringBuilder();
                boolean isSelected = false;
                if (LinearLayoutList.get(selectedAnswerPosition).isSelected()) {

                    for (int i = 0; i < selectedValue.size(); i++) {
                        if (selectedAnswerPosition == selectedValue.get(i)) {
                            LinearLayoutList.get(selectedAnswerPosition).setSelected(false);
                            selectedValue.remove(i);
                            break;
                        }
                    }

                } else {
                    LinearLayoutList.get(selectedAnswerPosition).setSelected(true);
                    if (!selectedValue.contains(selectedAnswerPosition))
                        selectedValue.add(selectedAnswerPosition);
                }
                ((TestBaseActivity) activity).questionBankList.get(position).setSelectedValue(selectedValue);

                for (int j = 0; j < LinearLayoutList.size(); j++) {
                    if (LinearLayoutList.get(j).isSelected()) {
                        isSelected = true;
                    }
                }

                if (isSelected) {
                    for (int i = 0; i < ((TestBaseActivity) activity).questionBankList.get(position).getSelectedValue().size(); i++) {
                        answerPosition.append(((TestBaseActivity) activity).questionBankList.get(position).getSelectedValue().get(i)).append(",");
                    }
                    ((TestBaseActivity) activity).questionBankList.get(position).setIsMultipleAnswer(true, answerPosition.toString());
                }

                Log.e("IS_SELECTED", String.valueOf(isSelected));

            } else {
                Log.e(TAG, "onClick: selectedAnswerposition = " + selectedAnswerposition);
                if (((TestBaseActivity) activity).attempLimit()) {
                    setSingleChoice();
                } else {
                    if (((TestBaseActivity) activity).questionBankList.get(position).getAnswerPosttion() <= 0) {
                        View v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), "Your attempt limit is over.");
                        Button btnSubmit;

                        v.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        btnSubmit = v.findViewById(R.id.btn_submit);

                        btnSubmit.setText(activity.getString(R.string.ok));

                        btnSubmit.setOnClickListener((View vi) -> Helper.dismissDialog());
                    } else {
                        if (LinearLayoutList.get(selectedAnswerposition).isSelected()) {
                            LinearLayoutList.get(selectedAnswerposition).setSelected(false);
                            ((TestBaseActivity) activity).questionBankList.get(position).setIsanswer(false, 0);
                        } else {
                            setSingleChoice();
                        }
                    }
                }
            }
        }
    };

    private void setSingleChoice() {
        for (int j = 0; j < LinearLayoutList.size(); j++) {
            if (selectedAnswerPosition == j) {
                if (LinearLayoutList.get(j).isSelected()) {
                    ((TestBaseActivity) activity).questionBankList.get(position).setIsanswer(false, 0);
                    LinearLayoutList.get(j).setSelected(false);
                } else {
                    ((TestBaseActivity) activity).questionBankList.get(position).setIsanswer(true, selectedAnswerPosition + 1);
                    LinearLayoutList.get(j).setSelected(true);
                }
            } else {
                LinearLayoutList.get(j).setSelected(false);
            }
        }
    }

    public static TestSeriesFragmentNew newInstance(int position, BasicInfo basicInfo, String openFrom) {
        Bundle args = new Bundle();
        args.putInt(Const.POSITION, position);
        args.putSerializable(Constants.Extras.BASIC_INFO, basicInfo);
        args.putSerializable(Constants.Extras.OPEN_FROM, openFrom);
        TestSeriesFragmentNew fragment = new TestSeriesFragmentNew();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        selectedValue = new ArrayList<>();
        Log.e(TAG, "onCreate: onCreate() is called");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test_series, null);
        Log.e(TAG, "onCreateView: onCreateView() is called");
        activity = getActivity();
        editTextList = new ArrayList<>();
        fibAnswers = new ArrayList<>();
        checkVideo = false;
        getBundleData();
        initView(view);
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);

        return view;
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null) {

            basicInfo = (BasicInfo) bundle.getSerializable(Constants.Extras.BASIC_INFO);
            position = bundle.getInt(Const.POSITION, 0);
            openFrom = bundle.getString(Constants.Extras.OPEN_FROM);
            Log.e(TAG, "getBundleData: openFrom = " + openFrom);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: onResume() is called");
        //        NetworkAPICall(API.API_GET_LANDING_PAGE_DATA_EXAM, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: onPause() is called");
    }

    private void initView(View view) {
        mcqOptionsLL = view.findViewById(R.id.mcqoptions);
        fibOptions = view.findViewById(R.id.fibOptions);
        fibOptionsRL = view.findViewById(R.id.fibOptionsRL);
        rvMatchingQuestion1 = view.findViewById(R.id.rvmatchinquestion1);
        llMatchingQuestion = view.findViewById(R.id.LLmatchinquestion);
        rvMatchingQuestion1 = view.findViewById(R.id.rvmatchinquestion1);
        rvMatchingQuestion2 = view.findViewById(R.id.rvmatchinquestion2);
        trueFalseRV = view.findViewById(R.id.trueFalseRV);
        imgQuestion = view.findViewById(R.id.imgQuestion);
        viewPagerRL = view.findViewById(R.id.viewPagerRL);

        tableView = view.findViewById(R.id.table_view);
        referenceIcon = view.findViewById(R.id.img_ref);
        referenceText = view.findViewById(R.id.txv_ref_title);
        testReferenceRootLayout = view.findViewById(R.id.test_ref_root_layout);

        trueFalseRV.setLayoutManager(new LinearLayoutManager(activity));
        rvMatchingQuestion1.setLayoutManager(new LinearLayoutManager(activity));
        rvMatchingQuestion2.setLayoutManager(new LinearLayoutManager(activity));
        rvMatchingQuestion2.setHasFixedSize(true);
        tvQuestion = view.findViewById(R.id.tv_question);
        imgBookmark = view.findViewById(R.id.img_bookmark);
        imgShare = view.findViewById(R.id.img_share);
        checkBoxGuess = view.findViewById(R.id.checkBox);
        markForReview = view.findViewById(R.id.mark_for_review);
        unmMarkForReview = view.findViewById(R.id.unmark_for_review);
        tvReportError = view.findViewById(R.id.tv_report_error);
        imgGuess = view.findViewById(R.id.imgGuess);
        tvUid = view.findViewById(R.id.tv_uid);
        tvEmail = view.findViewById(R.id.tv_email);
        explanationLL = view.findViewById(R.id.explanationLL);
        explanationTV = view.findViewById(R.id.explanationTV);
        nestedSV = view.findViewById(R.id.nestedSV);
        LinearLayoutList = new ArrayList<>();
        parentList = new ArrayList<>();
        tvList = new ArrayList<>();
        radioGroupList = new ArrayList<>();

        if (!GenericUtils.isEmpty(basicInfo.getDisplay_guess())
                && basicInfo.getDisplay_guess().equals("0")) {
            checkBoxGuess.setVisibility(View.GONE);
        } else {
            checkBoxGuess.setVisibility(View.VISIBLE);
        }

        if (((TestBaseActivity) activity).daily) {
            markForReview.setVisibility(View.GONE);
        } else {
            if (!((TestBaseActivity) activity).questionBankList.isEmpty()) {
                if (((TestBaseActivity) activity).questionBankList.get(position).isMarkForReview()) {
                    unmMarkForReview.setVisibility(View.VISIBLE);
                    markForReview.setVisibility(View.GONE);
                } else {
                    unmMarkForReview.setVisibility(View.GONE);
                    markForReview.setVisibility(View.VISIBLE);
                }
            }
        }
        if (!GenericUtils.isEmpty(basicInfo.getDisplay_bookmark())) {
            if (basicInfo.getDisplay_bookmark().equals("0")) {
                imgBookmark.setVisibility(View.GONE);
            } else {
                imgBookmark.setVisibility(View.VISIBLE);
            }
        }

        if (((TestBaseActivity) Objects.requireNonNull(getActivity())).questionBankList != null && !((TestBaseActivity) getActivity()).questionBankList.isEmpty()) {
            questionBank = ((TestBaseActivity) getActivity()).questionBankList.get(position);

            if (questionBank.getQuestion().contains("<table")) {
                final Document doc = Jsoup.parse(questionBank.getQuestion());
                String tableCode = doc.select("table").toString();
                tvQuestion.setText(GenericUtils.removeTagFromHtml(doc, "table"));

                tableView.setVisibility(View.VISIBLE);
                tableView.loadDataWithBaseURL(null, tableCode, "text/html", "utf-8", null);

            } else if (questionBank.getQuestion().contains("<img")) {
                //  imgQuestion.setVisibility(View.VISIBLE);
              /*  final Document doc = Jsoup.parse(questionBank.getQuestion());
                tvQuestion.setText(doc.body().text());
                String imageUrl = doc.select("img").attr("src");
                Glide.with(activity).load(imageUrl).into(imgQuestion);
                imgQuestion.setOnClickListener(v -> Helper.fullScreenImageDialog(activity, imageUrl));
 */
                Document document = Jsoup.parse(questionBank.getQuestion());
                //Elements divs = document.select(document.select("img").attr("src"));
                //Elements divs = document.select("img ,src");
                Elements divs = document.select("img ,src");
                String[] imageUrl3 = new String[divs.size()];

                for (int j = 0; j < divs.size(); j++) {
                    imageUrl3[j] = divs.get(j).select("img").attr("src");
                }
                if (divs.size() <= 1) {
                    imgQuestion.setVisibility(View.VISIBLE);
                    final Document doc = Jsoup.parse(questionBank.getQuestion());
                    tvQuestion.setText(doc.body().text());
                    String imageUrl = doc.select("img").attr("src");
                    Glide.with(activity).load(imageUrl).into(imgQuestion);
                    imgQuestion.setOnClickListener(v -> Helper.fullScreenImageDialog(activity, imageUrl));

                } else {
                    viewPagerRL.setVisibility(View.VISIBLE);
                    ViewPager viewPager = view.findViewById(R.id.viewPagerMain);
                    viewPager.setVisibility(View.VISIBLE);
                    ImageViewPagerAdapter imageViewPagerAdapter = new ImageViewPagerAdapter(activity, imageUrl3, activity);
                    viewPager.setAdapter(imageViewPagerAdapter);
                    TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
                    tabLayout.setupWithViewPager(viewPager, true);
                }


            } else {
                imgQuestion.setVisibility(View.GONE);
                String htmlAsString = questionBank.getQuestion();// used by WebView
                tvQuestion.setText(HtmlCompat.fromHtml(htmlAsString, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // Test Data
                /*String newString = htmlAsString.replace("_____", "{}");
                newString = htmlAsString.replace("____", "{}");
                Log.e(TAG, "initView: " + newString);
                tvQuestion.setDataSource(Collections.<String[]>emptyList(), newString);*/

            }

            if (!GenericUtils.isEmpty(questionBank.getQuestion_reference())) {
                referenceText.setText(questionBank.getQuestion_reference());
                Glide.with(activity).load(questionBank.getQuestion_reference_icon()).into(referenceIcon)
                        .onLoadFailed(getResources().getDrawable(R.mipmap.daily_quiz_logo));
                testReferenceRootLayout.setVisibility(View.VISIBLE);
            } else
                testReferenceRootLayout.setVisibility(View.GONE);
            tvUid.setText(String.format("eMedicoz QUID %s %s", questionBank.getId(), questionBank.getQuestionType()));

            switch (questionBank.getQuestionType()) {
                case Constants.QuestionType.MATCH_THE_FOLLOWING:
                    Log.e(TAG, "initView: Match Following");
                    mcqOptionsLL.setVisibility(View.GONE);
                    llMatchingQuestion.setVisibility(View.VISIBLE);
                    trueFalseRV.setVisibility(View.GONE);
                    fibOptionsRL.setVisibility(View.GONE);
                    ((TestBaseActivity) activity).questionBankList.get(position).clickedCount = 0;
                    ((TestBaseActivity) activity).questionBankList.get(position).clickedCount2 = 0;
                    setMatchingLayoutOption();
                    break;
                case Constants.QuestionType.MULTIPLE_CHOICE:
                case Constants.QuestionType.SINGLE_CHOICE:
                case Constants.QuestionType.REASON_ASSERTION:
                case Constants.QuestionType.EXTENDED_MATCHING:
                case Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS:
                case Constants.QuestionType.MULTIPLE_COMPLETION:
                    mcqOptionsLL.setVisibility(View.VISIBLE);
                    llMatchingQuestion.setVisibility(View.GONE);
                    trueFalseRV.setVisibility(View.GONE);
                    fibOptionsRL.setVisibility(View.GONE);
                    addQuestionOption();
                    break;
                case Constants.QuestionType.MULTIPLE_TRUE_FALSE:
                case Constants.QuestionType.TRUE_FALSE:
                    mcqOptionsLL.setVisibility(View.GONE);
                    llMatchingQuestion.setVisibility(View.GONE);
                    trueFalseRV.setVisibility(View.VISIBLE);
                    fibOptionsRL.setVisibility(View.GONE);
                    addTrueFalseViewOption();
                    break;
                case Constants.QuestionType.FILL_IN_THE_BLANKS:
                    mcqOptionsLL.setVisibility(View.GONE);
                    llMatchingQuestion.setVisibility(View.GONE);
                    trueFalseRV.setVisibility(View.GONE);
                    fibOptionsRL.setVisibility(View.VISIBLE);
                    fibOptions.addView(initFIBView());
                    break;

            }
            imgBookmark.setOnClickListener(this);
            imgShare.setVisibility(View.GONE);
            checkBoxGuess.setOnClickListener(this);
            markForReview.setOnClickListener(this);
            unmMarkForReview.setOnClickListener(this);
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
            tvEmail.setText(((TestBaseActivity) getActivity()).data.getUserInfo().getEmail());
            count = ((TestBaseActivity) activity).questionBankList.get(position).getCount();
        }
    }

    public LinearLayout initFIBView() {
        LinearLayout v = (LinearLayout) View.inflate(activity, R.layout.option_fill_in_the_blanks, null);
        TextView tv = v.findViewById(R.id.textSerialNumber);
        optionET = (EditText) v.getChildAt(1);
        optionET.setText(!GenericUtils.isEmpty(questionBank.getAnswerFIB()) ? questionBank.getAnswerFIB() : "");
        tv.setText(count + 1 + ".");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 0);
        v.setLayoutParams(lp);
        editTextList.add(optionET);
        optionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ((TestBaseActivity) activity).setFIBData();
            }
        });
        Log.e(TAG, "initFIBView is called: " + editTextList.get(count).getText());

        return v;
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
                    mcqOptionsLL.addView(initMCQOptionView("A", questionBank.getOption1(), "11", false, j - 1));
                    break;
                case 2:
                    if (questionBank.getOption2().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("B", questionBank.getOption2(), "11", false, j - 1));

                    break;
                case 3:
                    if (questionBank.getOption3().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("C", questionBank.getOption3(),
                            "11", false, j - 1));
                    break;
                case 4:
                    if (questionBank.getOption4().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("D", questionBank.getOption4(),
                            "11", false, j - 1));
                    break;
                case 5:
                    if (questionBank.getOption5().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("E", questionBank.getOption5(), "11", false, j - 1));
                    break;
                case 6:
                    if (questionBank.getOption6().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("F", questionBank.getOption6(),
                            "11", false, j - 1));
                    break;
                case 7:
                    if (questionBank.getOption7().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("G", questionBank.getOption7(),
                            "11", false, j - 1));
                    break;
                case 8:
                    if (questionBank.getOption8().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("H", questionBank.getOption8(),
                            "11", false, j - 1));
                    break;
                case 9:
                    if (questionBank.getOption9().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("I", questionBank.getOption9(),
                            "11", false, j - 1));
                    break;
                case 10:
                    if (questionBank.getOption10().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("J", questionBank.getOption10(),
                            "11", false, j - 1));
                    break;
                case 11:
                    if (questionBank.getOption11().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("K", questionBank.getOption11(),
                            "11", false, j - 1));
                    break;
                case 12:
                    if (questionBank.getOption12().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("L", questionBank.getOption12(),
                            "11", false, j - 1));
                    break;
                case 13:
                    if (questionBank.getOption13().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("M", questionBank.getOption13(),
                            "11", false, j - 1));
                    break;
                case 14:
                    if (questionBank.getOption4().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("N", questionBank.getOption14(),
                            "11", false, j - 1));
                    break;
                case 15:
                    if (questionBank.getOption15().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("O", questionBank.getOption15(),
                            "11", false, j - 1));
                    break;
                default:
                    break;
            }
        }
        status = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public LinearLayout initMCQOptionView(String text1, String text2, final String pollValue, boolean pollVisiblity, int tag) {

        LinearLayout v = (LinearLayout) View.inflate(activity, R.layout.layout_option_test_view, null);
        TextView tv = v.findViewById(R.id.optionIconTV);
        text = v.findViewById(R.id.optionTextTV);
        ImageView imgOption = v.findViewById(R.id.imgOption);
        RadioButton radioRB = v.findViewById(R.id.radioRB);
        parentLL = v.findViewById(R.id.viewLL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 0);

        if (pollVisiblity) {
            radioRB.setVisibility(View.GONE);

        }
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
        if (((TestBaseActivity) activity).questionBankList.get(position).getQuestionType().equals(Constants.QuestionType.SINGLE_CHOICE) || ((TestBaseActivity) activity).questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS) || ((TestBaseActivity) activity).questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.REASON_ASSERTION) || ((TestBaseActivity) activity).questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.EXTENDED_MATCHING) || ((TestBaseActivity) activity).questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_COMPLETION)) {
            Log.e("initMCQOptionView: ", String.valueOf(((TestBaseActivity) Objects.requireNonNull(getActivity())).questionBankList.get(position).getAnswerPosttion()));
            if (questionBank.isanswer() && questionBank.getAnswerPosttion() != 0)
                if (questionBank.getAnswerPosttion() - 1 == tag)
                    v.setSelected(true);

        } else if (((TestBaseActivity) activity).questionBankList.get(position).getQuestionType().equals(Constants.QuestionType.MULTIPLE_CHOICE)) {
            if (((TestBaseActivity) activity).questionBankList.get(position).getSelectedValue() != null) {
                if (((TestBaseActivity) activity).questionBankList.get(position).getSelectedValue().contains(tag)) {
                    v.setSelected(true);
                    selectedValue = ((TestBaseActivity) activity).questionBankList.get(position).getSelectedValue();
                }
            }
        }

        LinearLayoutList.add(v);
        parentList.add(parentLL);
        tvList.add(tv);
        v.setOnClickListener(onClickListener);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bookmark:
                if (questionBank.getIsBookmarked().equals("1")) {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmark));
                    networkCallForRemoveBookmark(position, questionBank.getId());
                } else {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmarked));
                    networkCallForAddBookmark(position, questionBank.getId(), ((TestBaseActivity) getActivity()).testSeriesId);
                }
                break;
            case R.id.checkBox:
                if (questionBank.getIsguess().equals("0")) {
                    Log.e(TAG, "Checked");
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
                ((TestBaseActivity) getActivity()).notifyNumberApater();
                markForReview.setVisibility(View.GONE);
                unmMarkForReview.setVisibility(View.VISIBLE);

                break;
            case R.id.unmark_for_review:
                Toast.makeText(activity, "Unmarked for review.", Toast.LENGTH_SHORT).show();
                questionBank.setMarkForReview(false);
                ((TestBaseActivity) getActivity()).notifyNumberApater();
                markForReview.setVisibility(View.VISIBLE);
                unmMarkForReview.setVisibility(View.GONE);
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
            if (isDataValid && radioButton[0] != null) {
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
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (Objects.requireNonNull(jsonResponse).optString(Const.STATUS).equals(Const.TRUE)) {
                            quizBasicInfoDialog.dismiss();
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } else {
                            quizBasicInfoDialog.dismiss();
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
                                Helper.showErrorLayoutForNav(API.API_SUBMIT_QUERY, activity, 1, 1);

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).contains(getString(R.string.something_went_wrong)))
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
                mProgress.dismiss();
                quizBasicInfoDialog.dismiss();
                Helper.showErrorLayoutForNav(API.API_SUBMIT_QUERY, activity, 1, 2);

            }
        });
    }

    public void refreshPage() {
        if (questionBank != null) {
            Log.e("refereshPage: ", questionBank.getQuestionType());
            if (questionBank.getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE))
                questionBank.setIsanswer(false, 0);
            else if (questionBank.getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_CHOICE)) {
                questionBank.setIsMultipleAnswer(false, "0");
                questionBank.setSelectedValue(null);
                selectedValue = null;
                count = 0;
                ((TestBaseActivity) activity).questionBankList.get(position).setCount(count);
                SharedPreference.getInstance().putInt("CLICK_COUNT", count);
            } else if (questionBank.getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_TRUE_FALSE)) {
                questionBank.setIsMultipleAnswer(false, "0");
                questionBank.setTfAnswerArrayList(null);
                trueFalseAdapter = new TrueFalseAdapter(activity, trueFalseList, this, position, null);
                trueFalseRV.setAdapter(trueFalseAdapter);
                // trueFalseAdapter.notifyDataSetChanged();
            } else if (questionBank.getQuestionType().equalsIgnoreCase(Constants.QuestionType.MATCH_THE_FOLLOWING)) {
                questionBank.mtfAnswer.clear();

                matchTheFollowingAdapter = new MatchTheFollowingAdapter(activity, items2, items1, true, position);
                rvMatchingQuestion1.setAdapter(matchTheFollowingAdapter);
            } else if (questionBank.getQuestionType().equalsIgnoreCase(Constants.QuestionType.FILL_IN_THE_BLANKS)) {
                questionBank.setIsMultipleAnswer(false, "0");
                questionBank.setAnswerFIB("");
                optionET.setText("");
            }
        }
        imgGuess.setImageResource(R.mipmap.guessing_answer);
        checkBoxGuess.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_unselected));
        guess = "0";
        for (int k = 0; k < LinearLayoutList.size(); k++) {
            LinearLayoutList.get(k).setSelected(false);
        }
    }

    private void networkCallForAddBookmark(final int position, String id, String testseriesid) {
        if (Helper.isConnected(activity)) {

            QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
            Call<JsonObject> response = apiInterface.bookmarkTestSeries(SharedPreference.getInstance().getLoggedInUser().getId(), id,
                    getQType());
            response.enqueue(new Callback<JsonObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                                questionBank.setIsBookmarked("1");
                            }
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(activity, getString(R.string.internet_verification_msg), Toast.LENGTH_SHORT).show();
        }

    }

    private String getQType() {
        if (!GenericUtils.isEmpty(openFrom))
            return openFrom.equals(Helper.toUpperCase(Constants.TestType.DAILY_CHALLENGE)) ? "3" : openFrom;
        else
            return "1";
    }

    private void networkCallForRemoveBookmark(final int position, String id) {

        if (Helper.isConnected(activity)) {
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.removeBookmark(SharedPreference.getInstance().getLoggedInUser().getId(), id);
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                                questionBank.setIsBookmarked("0");
                            }
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    Toast.makeText(activity, throwable.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            Toast.makeText(activity, getString(R.string.internet_verification_msg), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRadioClicked(RadioGroup radioGroup, int checkedId) {

    }

    private void addTrueFalseViewOption() {
        if (trueFalseList != null)
            trueFalseList.clear();
        else
            trueFalseList = new ArrayList<>();

        for (int j = 1; j <= 10; j++) {
            if (status2)
                break;
            switch (j) {

                case 1:
                    if (questionBank.getOption1().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("A", questionBank.getOption1()));

                    break;
                case 2:
                    if (questionBank.getOption2().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("B", questionBank.getOption2()));
                    break;
                case 3:
                    if (questionBank.getOption3().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("C", questionBank.getOption3()));

                    break;
                case 4:
                    if (questionBank.getOption4().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("D", questionBank.getOption4()));
                    break;
                case 5:
                    if (questionBank.getOption5().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("E", questionBank.getOption5()));
                    break;
                case 6:
                    if (questionBank.getOption6().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("F", questionBank.getOption6()));
                    break;
                case 7:
                    if (questionBank.getOption7().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("G", questionBank.getOption7()));
                    break;
                case 8:
                    if (questionBank.getOption8().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("H", questionBank.getOption8()));
                    break;
                case 9:
                    if (questionBank.getOption9().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("I", questionBank.getOption9()));
                    break;
                case 10:
                    if (questionBank.getOption10().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("J", questionBank.getOption10()));
                    break;

                default:
                    break;
            }
        }
        trueFalseAdapter = new TrueFalseAdapter(activity, trueFalseList, this, position, ((TestBaseActivity) activity).questionBankList.get(position).getTfAnswerArrayList());
        trueFalseRV.setAdapter(trueFalseAdapter);

        status2 = false;
    }


    private void setMatchingLayoutOption() {

        if (items1 != null && !items1.isEmpty())
            items1.clear();
        else items1 = new ArrayList<>();
        if (items2 != null && !items2.isEmpty())
            items2.clear();
        else items2 = new ArrayList<>();
        for (int j = 1; j <= 15; j++) {
            switch (j) {

                case 1:
                    if (questionBank.getOption1().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("A", questionBank.getOption1(), j - 1));

                    break;
                case 2:
                    if (questionBank.getOption2().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("B", questionBank.getOption2(), j - 1));
                    break;
                case 3:
                    if (questionBank.getOption3().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("C", questionBank.getOption3(), j - 1));

                    break;
                case 4:
                    if (questionBank.getOption4().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("D", questionBank.getOption4(), j - 1));
                    break;
                case 5:
                    if (questionBank.getOption5().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("E", questionBank.getOption5(), j - 1));
                    break;
                case 6:
                    if (questionBank.getOption6().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("F", questionBank.getOption6(), j - 1));
                    break;
                case 7:
                    if (questionBank.getOption7().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("G", questionBank.getOption7(), j - 1));
                    break;
                case 8:
                    if (questionBank.getOption8().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("P", questionBank.getOption8(), j - 1));
                    break;
                case 9:
                    if (questionBank.getOption9().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("Q", questionBank.getOption9(), j - 1));
                    break;
                case 10:
                    if (questionBank.getOption10().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("R", questionBank.getOption10(), j - 1));
                    break;

                case 11:
                    if (questionBank.getOption11().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("S", questionBank.getOption11(), j - 1));
                    break;

                case 12:
                    if (questionBank.getOption12().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("T", questionBank.getOption12(), j - 1));
                    break;

                case 13:
                    if (questionBank.getOption13().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("U", questionBank.getOption13(), j - 1));
                    break;

                case 14:
                    if (questionBank.getOption14().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("V", questionBank.getOption14(), j - 1));
                    break;

                case 15:
                    if (questionBank.getOption15().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("W", questionBank.getOption15(), j - 1));
                    break;
                default:
                    break;
            }
        }

        rvMatchingQuestion2.setVisibility(View.VISIBLE);
        MatchTheFollowingAdapterTop matchTheFollowingAdapterTop = new MatchTheFollowingAdapterTop(activity, items1);
        rvMatchingQuestion2.setAdapter(matchTheFollowingAdapterTop);
        matchTheFollowingAdapter = new MatchTheFollowingAdapter(activity, items2, items1, true, position);
        rvMatchingQuestion1.setAdapter(matchTheFollowingAdapter);

    }

    public void setFibAnswers() {
/*        for (int i = 0; i < editTextList.size(); i++) {
            if (!GenericUtils.isEmpty(editTextList.get(i).getText().toString())) {
                fibAnswers.add(editTextList.get(i).getText().toString());
            } else {
                fibAnswers.add("-");
            }
        }
        questionBank.setFibAnswer(fibAnswers);*/
        questionBank.setAnswerFIB(optionET.getText().toString());
    }

    public boolean setIsFibAnswer() {
        boolean fibAnswer = false;
        if (!GenericUtils.isEmpty(questionBank.getAnswerFIB())) {
            fibAnswer = true;
        }
        return fibAnswer;
    }

    public String getFibAnswer() {
        return questionBank.getAnswerFIB();
    }

}
