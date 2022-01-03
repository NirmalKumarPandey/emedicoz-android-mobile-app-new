package com.emedicoz.app.testmodule.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoTestFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    Progress mProgress;
    LinearLayout mcqOptionsLL;
    TextView percentageTextTV;
    LinearLayout parentLL;
    NestedScrollView nestedSV;
    List<View> LinearLayoutList = new ArrayList<>();
    List<View> parentList = new ArrayList<>();
    List<View> tvList = new ArrayList<>();
    ImageView imageTick, imgExplanation, imgQuestion;
    int position;
    LinearLayout explanationLL;
    BasicInfo basicInfo;
    int selectedAnswerPosition;
    ImageView play;
    TextView explanationTV;
    TextView text;
    TextView tvReportError;
    private TextView tvQuestion, tvUid, tvEmail;
    private RelativeLayout markForReview, unmarkForReview;
    private QuestionBank questionBank;

    private WebView tableView;
    private View testReferenceRootLayout;
    private TextView referenceText;
    private ImageView referenceIcon;

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectedAnswerPosition = (int) view.getTag();
            for (int j = 0; j < LinearLayoutList.size(); j++) {
                if (selectedAnswerPosition == j) {
                    if (LinearLayoutList.get(j).isSelected())
                        LinearLayoutList.get(j).setSelected(false);
                    else
                        LinearLayoutList.get(j).setSelected(true);
                } else {
                    LinearLayoutList.get(j).setSelected(false);
                }
            }
            questionBank.setIsanswer(true, selectedAnswerPosition + 1);
            Log.e("onClick: ", String.valueOf(questionBank.getAnswerPosttion()));
        }
    };
    private boolean status;
    private ImageView imgBookmark, imgGuess, imgShare;
    private RelativeLayout checkBoxGuess;
    private Drawable empty3;
    private String guess;

    public static VideoTestFragment newInstance(QuestionBank questionBank) {
        Bundle args = new Bundle();
        args.putSerializable("QUESTION_BANK", questionBank);
        VideoTestFragment fragment = new VideoTestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
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
    }

    private void initView(View view) {
        mcqOptionsLL = view.findViewById(R.id.mcqoptions);
        tvQuestion = view.findViewById(R.id.tv_question);
        imgBookmark = view.findViewById(R.id.img_bookmark);
        imgShare = view.findViewById(R.id.img_share);
        checkBoxGuess = view.findViewById(R.id.checkBox);
        markForReview = view.findViewById(R.id.mark_for_review);
        unmarkForReview = view.findViewById(R.id.unmark_for_review);
        imgGuess = view.findViewById(R.id.imgGuess);
        tvReportError = view.findViewById(R.id.tv_report_error);
        imgExplanation = view.findViewById(R.id.imgExplanation);
        imgQuestion = view.findViewById(R.id.imgQuestion);

        tableView = view.findViewById(R.id.table_view);
        referenceIcon = view.findViewById(R.id.img_ref);
        referenceText = view.findViewById(R.id.txv_ref_title);
        testReferenceRootLayout = view.findViewById(R.id.test_ref_root_layout);

        tvUid = view.findViewById(R.id.tv_uid);
        tvEmail = view.findViewById(R.id.tv_email);
        explanationLL = view.findViewById(R.id.explanationLL);
        explanationTV = view.findViewById(R.id.explanationTV);
        nestedSV = view.findViewById(R.id.nestedSV);

        basicInfo = ((VideoTestBaseActivity) activity).testseriesBase.getData().getBasicInfo();
        if (basicInfo.getDisplay_guess() != null && !basicInfo.getDisplay_guess().equalsIgnoreCase("") &&
                basicInfo.getDisplay_guess().equals("0")) {
            checkBoxGuess.setVisibility(View.GONE);
        } else {
            checkBoxGuess.setVisibility(View.VISIBLE);
        }

        markForReview.setVisibility(View.VISIBLE);

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
        imgShare.setVisibility(View.GONE);
        checkBoxGuess.setOnClickListener(this);
        markForReview.setOnClickListener(this);
        unmarkForReview.setOnClickListener(this);
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
                    mcqOptionsLL.addView(initMCQOptionView("A", questionBank.getOption1(),
                            j - 1));
                    break;
                case 2:
                    if (questionBank.getOption2().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("B", questionBank.getOption2(),
                            j - 1));
                    break;
                case 3:
                    if (questionBank.getOption3().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("C", questionBank.getOption3(),
                            j - 1));
                    break;
                case 4:
                    if (questionBank.getOption4().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("D", questionBank.getOption4(),
                            j - 1));
                    break;
                case 5:
                    if (questionBank.getOption5().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("E", questionBank.getOption5(),
                            j - 1));
                    break;
                case 6:
                    if (questionBank.getOption6().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("F", questionBank.getOption6(),
                            j - 1));
                    break;
                case 7:
                    if (questionBank.getOption7().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("G", questionBank.getOption7(),
                            j - 1));
                    break;
                case 8:
                    if (questionBank.getOption8().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("H", questionBank.getOption8(),
                            j - 1));
                    break;
                case 9:
                    if (questionBank.getOption9().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("I", questionBank.getOption9(),
                            j - 1));
                    break;
                case 10:
                    if (questionBank.getOption10().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("J", questionBank.getOption10(),
                            j - 1));
                    break;
                case 11:
                    if (questionBank.getOption11().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("K", questionBank.getOption11(),
                            j - 1));
                    break;
                case 12:
                    if (questionBank.getOption12().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("L", questionBank.getOption12(),
                            j - 1));
                    break;
                case 13:
                    if (questionBank.getOption13().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("M", questionBank.getOption13(),
                            j - 1));
                    break;
                case 14:
                    if (questionBank.getOption4().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("N", questionBank.getOption14(),
                            j - 1));
                    break;
                case 15:
                    if (questionBank.getOption15().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("O", questionBank.getOption15(),
                            j - 1));
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

    public LinearLayout initMCQOptionView(String text1, String text2, int tag) {
        LinearLayout v = (LinearLayout) View.inflate(activity, R.layout.layout_option_test_view, null);
        TextView tv = v.findViewById(R.id.optionIconTV);
        text = v.findViewById(R.id.optionTextTV);
        ImageView imgOption = v.findViewById(R.id.imgOption);
        percentageTextTV = v.findViewById(R.id.percentageTextTV);
        parentLL = v.findViewById(R.id.viewLL);
        imageTick = v.findViewById(R.id.imageTick);
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

        Log.e("initMCQOptionView: ", String.valueOf(questionBank.getAnswerPosttion()));
        if (questionBank.isanswer() && questionBank.getAnswerPosttion() != 0)
            if (questionBank.getAnswerPosttion() - 1 == tag)
                v.setSelected(true);

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
                    networkCallForRemoveBookmark(questionBank.getId());
                } else {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmarked));
                    netoworkCallForAddBookmark(questionBank.getId());
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
                unmarkForReview.setVisibility(View.VISIBLE);

                break;
            case R.id.unmark_for_review:
                Toast.makeText(activity, "Unmarked for review.", Toast.LENGTH_SHORT).show();
                questionBank.setMarkForReview(false);
                ((VideoTestBaseActivity) Objects.requireNonNull(getActivity())).notifyNumberAdapter();
                markForReview.setVisibility(View.VISIBLE);
                unmarkForReview.setVisibility(View.GONE);

                break;
            case R.id.tv_report_error:
                showPopupErrorTest();
                break;

            default:
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
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                            quizBasicInfoDialog.dismiss();
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        } else {
                            quizBasicInfoDialog.dismiss();
                            if (jsonResponse.optString(Constants.Extras.MESSAGE).equalsIgnoreCase(activity.getResources().getString(R.string.internet_error_message)))
                                Helper.showErrorLayoutForNav(API.API_SUBMIT_QUERY, activity, 1, 1);

                            if (jsonResponse.optString(Constants.Extras.MESSAGE).contains(activity.getResources().getString(R.string.something_went_wrong)))
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
        questionBank.setIsanswer(false, 0);
        guess = "0";
        for (int k = 0; k < LinearLayoutList.size(); k++) {
            LinearLayoutList.get(k).setSelected(false);
        }
    }

    private void netoworkCallForAddBookmark(String id) {

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

    private void loadPlayerWithVideoURL(String url) {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                ExoPlayerFragment.newInstance(AES.decrypt(url))).commit();

    }

    private void networkCallForRemoveBookmark(String id) {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
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

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void initializePlayer(String videoUrl) {
        loadPlayerWithVideoURL(videoUrl);
    }

}
