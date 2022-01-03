package com.emedicoz.app.testmodule.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.testmodule.activity.TestBookmarkActivity;
import com.emedicoz.app.testmodule.adapter.MatchTheFollowingAdapterTop;
import com.emedicoz.app.testmodule.adapter.MatchTheFollowingBookmarkAdapter;
import com.emedicoz.app.testmodule.adapter.TrueFalseBookmarkAdapter;
import com.emedicoz.app.testmodule.model.QuestionBank;
import com.emedicoz.app.testmodule.model.Social;
import com.emedicoz.app.testmodule.model.TrueFalse;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.video.exoplayer2.ExoPlayerFragment;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestBookmarkFragment extends Fragment implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    LinearLayout mcqOptionsLL;
    List<View> linearLayoutList;
    ImageView imgBookmark;
    ImageView imgExplanation;
    TextView tvQuestion;
    TextView tvExplanation;
    Activity activity;
    Progress mProgress;
    MatchTheFollowingBookmarkAdapter matchTheFollowingAdapter;
    LinearLayout explanationLL;
    ArrayList<String> answerList = new ArrayList<>();
    int position;
    ArrayList<TrueFalse> trueFalseList = new ArrayList<>();
    LinearLayout matchingQuestionLL;
    ImageView imgQuestion;
    boolean status1;
    boolean status2;
    ArrayList<Social> items1 = new ArrayList<>();
    ArrayList<Social> items2 = new ArrayList<>();
    TrueFalseBookmarkAdapter trueFalseAdapter;
    NonScrollRecyclerView trueFalseRV;
    NonScrollRecyclerView rvMatchingQuestion1;
    NonScrollRecyclerView rvMatchingQuestion2;
    private QuestionBank questionBank;
    private boolean status;
    RelativeLayout playerContainer;
    MyNetworkCall myNetworkCall;

    private WebView tableView;
    private View testReferenceRootLayout;
    private TextView referenceText;
    private ImageView referenceIcon;

    public static TestBookmarkFragment newInstance(int position, QuestionBank questionBank) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putSerializable("DATA", questionBank);
        TestBookmarkFragment fragment = new TestBookmarkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TestBookmarkFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        TestBookmarkFragment fragment = new TestBookmarkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            position = getArguments().getInt("position");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_test_series_bookmark, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        myNetworkCall = new MyNetworkCall(this, activity);
        mProgress = new Progress(activity);
        mProgress.setCancelable(false);
        linearLayoutList = new ArrayList<>();
        rvMatchingQuestion1 = view.findViewById(R.id.rvmatchinquestion1);
        matchingQuestionLL = view.findViewById(R.id.LLmatchinquestion);
        rvMatchingQuestion2 = view.findViewById(R.id.rvmatchinquestion2);
        rvMatchingQuestion1.setLayoutManager(new LinearLayoutManager(activity));
        rvMatchingQuestion2.setLayoutManager(new LinearLayoutManager(activity));
        rvMatchingQuestion2.setHasFixedSize(true);
        mcqOptionsLL = view.findViewById(R.id.mcqoptions);
        imgExplanation = view.findViewById(R.id.imgExplanation);
        imgQuestion = view.findViewById(R.id.imgQuestion);
        tvQuestion = view.findViewById(R.id.tv_question);
        tvExplanation = view.findViewById(R.id.tv_explanation);
        imgBookmark = view.findViewById(R.id.img_bookmark);
        imgBookmark.setOnClickListener(this);
        trueFalseRV = view.findViewById(R.id.trueFalseRV);
        explanationLL = view.findViewById(R.id.explanationLL);
        playerContainer = view.findViewById(R.id.player_container);

        tableView = view.findViewById(R.id.table_view);
        referenceIcon = view.findViewById(R.id.img_ref);
        referenceText = view.findViewById(R.id.txv_ref_title);
        testReferenceRootLayout = view.findViewById(R.id.test_ref_root_layout);

        if (((TestBookmarkActivity) activity).questionBookmarks != null && ((TestBookmarkActivity) activity).questionBookmarks.size() > position) {
            questionBank = ((TestBookmarkActivity) activity).questionBookmarks.get(position);

            if (questionBank.getDescription() != null && !questionBank.getDescription().equalsIgnoreCase("")) {
                explanationLL.setVisibility(View.VISIBLE);
            } else {
                explanationLL.setVisibility(View.GONE);
            }

            if (questionBank.getDescription().contains("<p>") || questionBank.getDescription().contains("<h>") || questionBank.getDescription().contains("<b>")) {
                if (questionBank.getDescription().contains("<table")) {
                    final Document doc = Jsoup.parse(questionBank.getDescription());
                    tvExplanation.setText(GenericUtils.removeTagFromHtml(doc, "table"));
                    String tableCode = doc.select("table").toString();
                    tableView.setVisibility(View.VISIBLE);
                    tableView.loadDataWithBaseURL(null, tableCode, "text/html", "utf-8", null);

                } else if (questionBank.getDescription().contains("<img")) {
                    imgExplanation.setVisibility(View.VISIBLE);
                    final Document doc = Jsoup.parse(questionBank.getDescription());
                    tvExplanation.setText(doc.body().text());
                    String imageUrl = doc.select("img").attr("src");
                    Glide.with(activity).load(imageUrl).into(imgExplanation);
                    imgExplanation.setOnClickListener(v -> Helper.fullScreenImageDialog(activity, imageUrl));
                } else {
                    imgExplanation.setVisibility(View.GONE);
                    String html = questionBank.getDescription();// used by WebView
                    tvExplanation.setText(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY));
                }
            } else {
                tvExplanation.setText(questionBank.getDescription());
            }

            if (!GenericUtils.isEmpty(questionBank.getQuestion_reference())) {
                referenceText.setText(questionBank.getQuestion_reference());
                Glide.with(activity).load(questionBank.getQuestion_reference_icon()).into(referenceIcon)
                        .onLoadFailed(getResources().getDrawable(R.mipmap.daily_quiz_logo));
                testReferenceRootLayout.setVisibility(View.VISIBLE);
            } else
                testReferenceRootLayout.setVisibility(View.GONE);

            switch (questionBank.getQuestionType()) {
                case Constants.QuestionType.SINGLE_CHOICE:
                case Constants.QuestionType.MULTIPLE_CHOICE:
                case Constants.QuestionType.REASON_ASSERTION:
                case Constants.QuestionType.EXTENDED_MATCHING:
                case Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS:
                    trueFalseRV.setVisibility(View.GONE);
                    mcqOptionsLL.setVisibility(View.VISIBLE);
                    addQuestionOption();
                    break;
                case Constants.QuestionType.MULTIPLE_TRUE_FALSE:
                case Constants.QuestionType.TRUE_FALSE:
                    trueFalseRV.setVisibility(View.VISIBLE);
                    mcqOptionsLL.setVisibility(View.GONE);
                    addTrueFalseLayout();
                    break;
                case Constants.QuestionType.MATCH_THE_FOLLOWING:
                    matchingQuestionLL.setVisibility(View.VISIBLE);
                    trueFalseRV.setVisibility(View.GONE);
                    mcqOptionsLL.setVisibility(View.GONE);
                    setMatchingLayoutOption();
                    break;
            }
        }
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

            }
        }

        String[] answers = questionBank.getAnswer().split(",");

        Collections.addAll(answerList, answers);

        rvMatchingQuestion2.setVisibility(View.VISIBLE);
        rvMatchingQuestion1.setNestedScrollingEnabled(false);
        rvMatchingQuestion2.setNestedScrollingEnabled(false);
        MatchTheFollowingAdapterTop matchTheFollowingAdapterTop = new MatchTheFollowingAdapterTop(activity, items1);
        rvMatchingQuestion2.setAdapter(matchTheFollowingAdapterTop);
        matchTheFollowingAdapter = new MatchTheFollowingBookmarkAdapter(activity, items2, items1, false, position, answerList);
        rvMatchingQuestion1.setAdapter(matchTheFollowingAdapter);

    }

    private void addTrueFalseLayout() {
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

            }
        }

        String[] answers = questionBank.getAnswer().split(",");
        Collections.addAll(answerList, answers);

        trueFalseAdapter = new TrueFalseBookmarkAdapter(activity, trueFalseList, position, answerList);
        trueFalseRV.setLayoutManager(new LinearLayoutManager(activity));
        trueFalseRV.setAdapter(trueFalseAdapter);

        status2 = false;
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
            }
        }
        status = false;
    }

    public LinearLayout initMCQOptionView(String text1, String text2, int tag) {
        LinearLayout v = (LinearLayout) View.inflate(activity, R.layout.layout_option_test_view, null);
        TextView tv = v.findViewById(R.id.optionIconTV);
        TextView text = v.findViewById(R.id.optionTextTV);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 0);

        v.setLayoutParams(lp);
        tv.setText(text1);
        tv.setGravity(Gravity.CENTER);
        text.setText(HtmlCompat.fromHtml(text2, HtmlCompat.FROM_HTML_MODE_LEGACY));
        v.setTag(tag);
        linearLayoutList.add(v);
        setCorrectAnswer(linearLayoutList);
        return v;
    }

    private void setCorrectAnswer(List<View> linearLayoutList) {
        if (questionBank.getAnswer().contains(",")) {
            String[] answers = questionBank.getAnswer().split(",");
            for (String answer : answers) {
                if (linearLayoutList.size() >= Integer.parseInt(answer)) {
                    linearLayoutList.get(Integer.parseInt(answer) - 1).setSelected(true);
                }
            }
        } else {
            if (linearLayoutList.size() >= Integer.parseInt(questionBank.getAnswer())) {
                linearLayoutList.get(Integer.parseInt(questionBank.getAnswer()) - 1).setSelected(true);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (questionBank != null) {
            if (questionBank.getQuestion().contains("<table")) {
                final Document doc = Jsoup.parse(questionBank.getQuestion());
                String tableCode = doc.select("table").toString();
                tvQuestion.setText(String.format("Q-%d. %s", position + 1, doc.body().text()));

                tableView.setVisibility(View.VISIBLE);
                tableView.loadDataWithBaseURL(null, tableCode, "text/html", "utf-8", null);

            } else if (questionBank.getQuestion().contains("<img")) {
                imgQuestion.setVisibility(View.VISIBLE);
                final Document doc = Jsoup.parse(questionBank.getQuestion());
                tvQuestion.setText(String.format("Q-%d. %s", position + 1, doc.body().text()));
                String imageUrl = doc.select("img").attr("src");
                Glide.with(activity).load(imageUrl).into(imgQuestion);
                imgQuestion.setOnClickListener(v -> Helper.fullScreenImageDialog(activity, imageUrl));

            } else if (questionBank.getVideo_url() != null && !questionBank.getVideo_url().equals("")) {
                imgQuestion.setVisibility(View.GONE);
                playerContainer.setVisibility(View.VISIBLE);
                initializePlayer(questionBank.getVideo_url());
                tvQuestion.setText(Helper.getFileName(HtmlCompat.fromHtml("Q-" + (position + 1) + ". " + questionBank.getQuestion(), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()));

            } else if (questionBank.getQuestion().contains("<video")) {
                imgQuestion.setVisibility(View.GONE);
                playerContainer.setVisibility(View.VISIBLE);
                final Document doc = Jsoup.parse(questionBank.getQuestion());
                tvQuestion.setText("Q-" + (position + 1) + ". " + doc.body().text());
                String videoUrl = "";
                videoUrl = doc.select("video").attr("src");
                initializePlayer(videoUrl);

            } else {
                imgQuestion.setVisibility(View.GONE);
                tvQuestion.setText(Helper.getFileName(HtmlCompat.fromHtml("Q-" + (position + 1) + ". " + questionBank.getQuestion(), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()));
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_bookmark) {
            if (questionBank != null) {
                if (questionBank.getIs_bookmark().equals("1")) {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmark));
                    networkCallForRemoveBookmark(questionBank.getId(), imgBookmark);
                } else {
                    imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmarked));
                    networkCallForAddBookmark(questionBank.getId(), imgBookmark);
                }
            }

        }
    }

    private void initializePlayer(String videoUrl) {
        loadPlayerWithVideoURL(videoUrl);
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

    private void networkCallForRemoveBookmark(String id, final ImageView testBookmarkIV) {
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

                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            testBookmarkIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmark));
                            ((TestBookmarkActivity) activity).questionBookmarks.get(position).setIs_bookmark("0");
                        } else
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

    private void networkCallForAddBookmark(String id, final ImageView testBookmarkIV) {

        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response = apiInterface.bookmarkTestSeries(SharedPreference.getInstance().getLoggedInUser().getId(), id, "1");
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
                            testBookmarkIV.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmarked));
                            ((TestBookmarkActivity) activity).questionBookmarks.get(position).setIs_bookmark("1");
                        } else
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

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        return null;
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {

    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {

    }

}
