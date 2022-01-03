package com.emedicoz.app.testmodule.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.QuizApiInterface;
import com.emedicoz.app.testmodule.activity.ViewSolutionWithTabNew;
import com.emedicoz.app.testmodule.adapter.MatchTheFollowingAdapter;
import com.emedicoz.app.testmodule.adapter.MatchTheFollowingAdapterTop;
import com.emedicoz.app.testmodule.adapter.TrueFalseViewSolutionAdapter;
import com.emedicoz.app.testmodule.model.Social;
import com.emedicoz.app.testmodule.model.TrueFalse;
import com.emedicoz.app.testmodule.model.ViewSolutionData;
import com.emedicoz.app.testmodule.model.ViewSolutionResult;
import com.emedicoz.app.utilso.AES;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
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


public class ViewSolutionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ViewSolutionFragment";
    Activity activity;
    TrueFalseViewSolutionAdapter trueFalseAdapter;
    LinearLayout mcqOptionsLL;
    MatchTheFollowingAdapter matchTheFollowingAdapter;
    boolean status1;
    boolean status2;
    ArrayList<TrueFalse> trueFalseList = new ArrayList<>();
    LinearLayout parentLL, llMatchingQuestion;
    ArrayList<Social> items1 = new ArrayList<>();
    ArrayList<Social> items2 = new ArrayList<>();
    RecyclerView rvMatchingQuestion1;
    RecyclerView rvMatchingQuestion2;
    ViewSolutionData viewSolutionData;
    ArrayList<ViewSolutionResult> resultArrayList;
    NestedScrollView nestedSV;
    List<View> linearLayoutList;
    List<View> parentList;
    List<View> tvList;
    TextView text;
    ImageView imgBookmark;
    ImageView imgQuestion;
    ImageView imgExplanation;
    int position;
    ArrayList<String> answerList = new ArrayList<>();
    ArrayList<String> userAnswersList = new ArrayList<>();
    LinearLayout explanationLL;
    RecyclerView trueFalseRV;
    ViewSolutionResult result;
    TextView explanationTV;
    TextView userEmailTV;
    TextView tvUid;
    RelativeLayout videoRL;
    String tabName;

    private TextView tvQuestion;
    private boolean status;
    private Drawable empty;
    Drawable empty3;
    private WebView tableView;
    private View testReferenceRootLayout;
    private TextView referenceText;
    private ImageView referenceIcon;

    public static ViewSolutionFragment newInstance(int position, String tabName) {
        Bundle args = new Bundle();
        args.putInt(Const.POSITION, position);
        args.putString(Constants.Extras.TAB_NAME, tabName);
        ViewSolutionFragment fragment = new ViewSolutionFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_solution, null);
        activity = getActivity();
        getBundleData();
        initView(view);
        return view;
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null) {

            position = bundle.getInt(Const.POSITION, 0);
            tabName = bundle.getString(Constants.Extras.TAB_NAME);
            if (activity instanceof ViewSolutionWithTabNew)
                viewSolutionData = ((ViewSolutionWithTabNew) activity).viewSolutionData;

            if (viewSolutionData != null && activity instanceof ViewSolutionWithTabNew) {
                switch (Objects.requireNonNull(tabName)) {
                    case Constants.TestStatus.CORRECT:
                        resultArrayList = ((ViewSolutionWithTabNew) activity).viewSolutionResultCorrect;
                        break;
                    case Constants.TestStatus.INCORRECT:
                        resultArrayList = ((ViewSolutionWithTabNew) activity).viewSolutionResultIncorrect;
                        break;
                    case Constants.TestStatus.SKIPPED:
                        resultArrayList = ((ViewSolutionWithTabNew) activity).viewSolutionResultSkipped;
                        break;
                    case Constants.TestStatus.BOOKMARKED:
                        resultArrayList = ((ViewSolutionWithTabNew) activity).viewSolutionResultBookmarked;
                        break;
                    case Constants.TestStatus.ALL:
                    default:
                        resultArrayList = viewSolutionData.getResult();
                        break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (result != null) {
            if (result.getQuestion().contains("<table")) {
                final Document doc = Jsoup.parse(result.getQuestion());
                String tableCode = doc.select("table").toString();
                tvQuestion.setText(GenericUtils.removeTagFromHtml(doc, "table"));

                tableView.setVisibility(View.VISIBLE);
                tableView.loadDataWithBaseURL(null, tableCode, "text/html", "utf-8", null);

            } else if (result.getQuestion().contains("<img")) {
                imgQuestion.setVisibility(View.VISIBLE);
                videoRL.setVisibility(View.GONE);
                final Document doc = Jsoup.parse(result.getQuestion());
                tvQuestion.setText(doc.body().text());
                String imageUrl = doc.select("img").attr("src");
                Glide.with(activity).load(imageUrl).into(imgQuestion);
                imgQuestion.setOnClickListener(v -> Helper.fullScreenImageDialog(activity, imageUrl));
            } else if (!GenericUtils.isEmpty(result.getVideo_url())) {
                imgQuestion.setVisibility(View.GONE);
                videoRL.setVisibility(View.VISIBLE);

                initializePlayer(AES.decrypt(result.getVideo_url()));
                tvQuestion.setText(HtmlCompat.fromHtml(result.getQuestion(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else if (result.getQuestion().contains("<video")) {
                imgQuestion.setVisibility(View.GONE);
                videoRL.setVisibility(View.VISIBLE);
                final Document doc = Jsoup.parse(result.getQuestion());
                tvQuestion.setText(doc.body().text());
                String videoUrl = "";
                videoUrl = doc.select("video").attr("src");
                initializePlayer(videoUrl);

            } else {
                videoRL.setVisibility(View.GONE);
                imgQuestion.setVisibility(View.GONE);
                String html = result.getQuestion();// used by WebView
                tvQuestion.setText(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY));
            }
        }

    }

    private void initView(View view) {
        rvMatchingQuestion1 = view.findViewById(R.id.rvmatchinquestion1);
        llMatchingQuestion = view.findViewById(R.id.LLmatchinquestion);
        rvMatchingQuestion2 = view.findViewById(R.id.rvmatchinquestion2);
        rvMatchingQuestion1.setLayoutManager(new LinearLayoutManager(activity));
        rvMatchingQuestion2.setLayoutManager(new LinearLayoutManager(activity));
        rvMatchingQuestion2.setHasFixedSize(true);
        mcqOptionsLL = view.findViewById(R.id.mcqoptions);
        videoRL = view.findViewById(R.id.videoRL);
        tvQuestion = view.findViewById(R.id.tv_question);
        trueFalseRV = view.findViewById(R.id.trueFalseRV);
        explanationLL = view.findViewById(R.id.explanationLL);
        explanationTV = view.findViewById(R.id.explanationTV);
        tvUid = view.findViewById(R.id.tv_uid);
        imgExplanation = view.findViewById(R.id.imgExplanation);
        imgQuestion = view.findViewById(R.id.imgQuestion);

        tableView = view.findViewById(R.id.table_view);
        referenceIcon = view.findViewById(R.id.img_ref);
        referenceText = view.findViewById(R.id.txv_ref_title);
        testReferenceRootLayout = view.findViewById(R.id.test_ref_root_layout);

        nestedSV = view.findViewById(R.id.nestedSV);
        userEmailTV = view.findViewById(R.id.userEmailTV);
        imgBookmark = view.findViewById(R.id.img_bookmark);
        linearLayoutList = new ArrayList<>();
        parentList = new ArrayList<>();
        tvList = new ArrayList<>();

        if (resultArrayList != null && !resultArrayList.isEmpty())
            result = resultArrayList.get(position);


        if (result != null) {
            if (!GenericUtils.isEmpty(result.getDescription())) {
                explanationLL.setVisibility(View.VISIBLE);
                if (result.getDescription().contains("<p>") || result.getDescription().contains("<h>") || result.getDescription().contains("<b>")) {
                    if (result.getDescription().contains("<table")) {
                        final Document doc = Jsoup.parse(result.getDescription());
                        String tableCode = doc.select("table").toString();
                        explanationTV.setText(GenericUtils.removeTagFromHtml(doc, "table"));

                        tableView.setVisibility(View.VISIBLE);
                        tableView.loadDataWithBaseURL(null, tableCode, "text/html", "utf-8", null);

                    } else if (result.getDescription().contains("<img")) {
                        imgExplanation.setVisibility(View.VISIBLE);
                        final Document doc = Jsoup.parse(result.getDescription());
                        explanationTV.setText(doc.body().text());
                        String imageUrl = doc.select("img").attr("src");
                        Glide.with(activity).load(imageUrl).into(imgExplanation);
                        imgExplanation.setOnClickListener(v -> Helper.fullScreenImageDialog(activity, imageUrl));
                    } else {
                        imgExplanation.setVisibility(View.GONE);
                        String html = result.getDescription();// used by WebView
                        explanationTV.setText(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    }
                } else {
                    explanationTV.setText(result.getDescription());
                }
            } else {
                explanationLL.setVisibility(View.GONE);
            }

            if (!GenericUtils.isEmpty(result.getQuestion_reference())) {
                referenceText.setText(result.getQuestion_reference());
                Glide.with(activity).load(result.getQuestion_reference_icon()).into(referenceIcon)
                        .onLoadFailed(getResources().getDrawable(R.mipmap.daily_quiz_logo));
                testReferenceRootLayout.setVisibility(View.VISIBLE);
            } else
                testReferenceRootLayout.setVisibility(View.GONE);
            tvUid.setText(String.format("eMedicoz QUID %s %s", result.getId(), result.getQuestionType()));

            if (result.getIs_bookmark() != null) {
                if (result.getIs_bookmark().equals("1")) {
                    imgBookmark.setImageResource(R.mipmap.bookmarked);

                } else {
                    imgBookmark.setImageResource(R.mipmap.bookmark);

                }
            }
            switch (result.getQuestionType()) {
                case Constants.QuestionType.SINGLE_CHOICE:
                case Constants.QuestionType.MULTIPLE_CHOICE:
                case Constants.QuestionType.REASON_ASSERTION:
                case Constants.QuestionType.EXTENDED_MATCHING:
                case Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS:
                case Constants.QuestionType.MULTIPLE_COMPLETION:
                    trueFalseRV.setVisibility(View.GONE);
                    mcqOptionsLL.setVisibility(View.VISIBLE);
                    addSolutionOption();
                    displaySolution();
                    break;
                case Constants.QuestionType.MULTIPLE_TRUE_FALSE:
                case Constants.QuestionType.TRUE_FALSE:
                    trueFalseRV.setVisibility(View.VISIBLE);
                    mcqOptionsLL.setVisibility(View.GONE);
                    addTrueFalseLayout();
                    break;
                case Constants.QuestionType.MATCH_THE_FOLLOWING:
                    trueFalseRV.setVisibility(View.GONE);
                    mcqOptionsLL.setVisibility(View.GONE);
                    llMatchingQuestion.setVisibility(View.VISIBLE);
                    setMatchingLayoutOption();
                    break;
                default:
                    break;
            }
        }
        if (viewSolutionData != null)
            userEmailTV.setText(viewSolutionData.getUserInfo().getEmail());

        imgBookmark.setOnClickListener(this);
    }


    private void loadPlayerWithVideoURL(String url) {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.exoplayer_container_layout,
                ExoPlayerFragment.newInstance(AES.decrypt(url))).commit();

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
                    if (result.getOption1().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("A", result.getOption1(), j - 1));

                    break;
                case 2:
                    if (result.getOption2().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("B", result.getOption2(), j - 1));
                    break;
                case 3:
                    if (result.getOption3().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("C", result.getOption3(), j - 1));

                    break;
                case 4:
                    if (result.getOption4().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("D", result.getOption4(), j - 1));
                    break;
                case 5:
                    if (result.getOption5().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("E", result.getOption5(), j - 1));
                    break;
                case 6:
                    if (result.getOption6().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("F", result.getOption6(), j - 1));
                    break;
                case 7:
                    if (result.getOption7().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items2.add(new Social("G", result.getOption7(), j - 1));
                    break;
                case 8:
                    if (result.getOption8().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("P", result.getOption8(), j - 1));
                    break;
                case 9:
                    if (result.getOption9().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("Q", result.getOption9(), j - 1));
                    break;
                case 10:
                    if (result.getOption10().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("R", result.getOption10(), j - 1));
                    break;

                case 11:
                    if (result.getOption11().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("S", result.getOption11(), j - 1));
                    break;

                case 12:
                    if (result.getOption12().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("T", result.getOption12(), j - 1));
                    break;

                case 13:
                    if (result.getOption13().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("U", result.getOption13(), j - 1));
                    break;

                case 14:
                    if (result.getOption14().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("V", result.getOption14(), j - 1));
                    break;

                case 15:
                    if (result.getOption15().isEmpty()) {
                        status1 = true;
                        break;
                    }
                    items1.add(new Social("W", result.getOption15(), j - 1));
                    break;

            }
        }


        Log.e(TAG, "setMatchingLayoutOption: " + result.getAnswer());
        String[] answers = result.getAnswer().split(",");
        String[] userAnswers = result.getUserAnswer().split(",");

        Collections.addAll(answerList, answers);

        for (int i = 0; i < answerList.size(); i++) {
            userAnswersList.add("-");
        }


        if (!result.getUserAnswer().equals("")) {
            for (int i = 0; i < answerList.size(); i++) {
                for (String userAnswer : userAnswers) {
                    if (String.valueOf(answerList.get(i).charAt(0)).equals(String.valueOf(userAnswer.charAt(0)))) {
                        userAnswersList.set(i, userAnswer);
                    }
                }
            }
        }
        rvMatchingQuestion2.setVisibility(View.VISIBLE);
        MatchTheFollowingAdapterTop matchTheFollowingAdapterTop = new MatchTheFollowingAdapterTop(activity, items1);
        rvMatchingQuestion2.setAdapter(matchTheFollowingAdapterTop);
        matchTheFollowingAdapter = new MatchTheFollowingAdapter(activity, items2, items1, false, position, answerList, userAnswersList);
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
                    if (result.getOption1().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("A", result.getOption1()));

                    break;
                case 2:
                    if (result.getOption2().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("B", result.getOption2()));
                    break;
                case 3:
                    if (result.getOption3().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("C", result.getOption3()));

                    break;
                case 4:
                    if (result.getOption4().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("D", result.getOption4()));
                    break;
                case 5:
                    if (result.getOption5().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("E", result.getOption5()));
                    break;
                case 6:
                    if (result.getOption6().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("F", result.getOption6()));
                    break;
                case 7:
                    if (result.getOption7().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("G", result.getOption7()));
                    break;
                case 8:
                    if (result.getOption8().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("H", result.getOption8()));
                    break;
                case 9:
                    if (result.getOption9().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("I", result.getOption9()));
                    break;
                case 10:
                    if (result.getOption10().isEmpty()) {
                        status2 = true;
                        break;
                    }
                    trueFalseList.add(new TrueFalse("J", result.getOption10()));
                    break;

            }
        }

        String[] answers = result.getAnswer().split(",");
        String[] userAnswers = result.getUserAnswer().split(",");
        Collections.addAll(answerList, answers);
        if (!result.getUserAnswer().equals("")) {
            Collections.addAll(userAnswersList, userAnswers);
        }
        trueFalseAdapter = new TrueFalseViewSolutionAdapter(activity, trueFalseList, position, answerList, userAnswersList);
        trueFalseRV.setLayoutManager(new LinearLayoutManager(activity));
        trueFalseRV.setAdapter(trueFalseAdapter);
        status2 = false;
    }

    private void displaySolution() {
        try {
            if (result.getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_CHOICE)) {
                String[] answers = result.getAnswer().split(",");
                ArrayList<String> answerList = new ArrayList<>();
                Collections.addAll(answerList, answers);
                if (!result.getUserAnswer().equals("")) {

                    String[] userAnswers = result.getUserAnswer().split(",");
                    for (String answer : userAnswers) {
                        if (answerList.contains(answer)) {
                            linearLayoutList.get(Integer.parseInt(answer) - 1).setSelected(true);
                            answerList.remove(answer);
                        } else {
                            if (!answerList.isEmpty()) {
                                for (int j = 0; j < answerList.size(); j++) {
                                    linearLayoutList.get(Integer.parseInt(answerList.get(j)) - 1).setSelected(true);
                                }
                            }
                            parentList.get(Integer.parseInt(answer) - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_wrong_answer));
                            tvList.get(Integer.parseInt(answer) - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_wrong));
                            ((TextView) (tvList.get(Integer.parseInt(answer) - 1))).setTextColor(ContextCompat.getColor(activity, R.color.white));

                        }

                    }
                } else {
                    for (String answer : answers) {
                        linearLayoutList.get(Integer.parseInt(answer) - 1).setSelected(true);
                    }
                }

            } else if (result.getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE) || result.getQuestionType().equalsIgnoreCase(Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS) || result.getQuestionType().equalsIgnoreCase(Constants.QuestionType.REASON_ASSERTION) || result.getQuestionType().equalsIgnoreCase(Constants.QuestionType.EXTENDED_MATCHING) || result.getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_COMPLETION)) {
                if (result.getUserAnswer().equalsIgnoreCase("")) {
                    linearLayoutList.get(Integer.parseInt(result.getAnswer()) - 1).setSelected(true);
                } else {
                    if (result.getUserAnswer().equalsIgnoreCase(result.getAnswer())) {
                        parentList.get(Integer.parseInt(result.getUserAnswer()) - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_selected));
                        tvList.get(Integer.parseInt(result.getUserAnswer()) - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_right));
                    } else {
                        parentList.get(Integer.parseInt(result.getAnswer()) - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_selected));
                        tvList.get(Integer.parseInt(result.getAnswer()) - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_right));
                        ((TextView) (tvList.get(Integer.parseInt(result.getAnswer()) - 1))).setTextColor(ContextCompat.getColor(activity, R.color.black));
                        parentList.get(Integer.parseInt(result.getUserAnswer()) - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_mcq_wrong_answer));
                        tvList.get(Integer.parseInt(result.getUserAnswer()) - 1).setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_wrong));
                    }
                    ((TextView) (tvList.get(Integer.parseInt(result.getUserAnswer()) - 1))).setTextColor(ContextCompat.getColor(activity, R.color.black));
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void addSolutionOption() {
        for (int j = 1; j <= 15; j++) {
            if (status)
                break;
            switch (j) {
                case 1:
                    if (result.getOption1().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("A", result.getOption1(),
                            j - 1));
                    break;
                case 2:
                    if (result.getOption2().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("B", result.getOption2(),
                            j - 1));
                    break;
                case 3:
                    if (result.getOption3().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("C", result.getOption3(),
                            j - 1));
                    break;
                case 4:
                    if (result.getOption4().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("D", result.getOption4(),
                            j - 1));
                    break;
                case 5:
                    if (result.getOption5().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("E", result.getOption5(),
                            j - 1));
                    break;

                case 6:
                    if (result.getOption6() == null || result.getOption6().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("F", result.getOption6(),
                            j - 1));
                    break;

                case 7:
                    if (result.getOption7() == null || result.getOption7().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("G", result.getOption7(),
                            j - 1));
                    break;

                case 8:
                    if (result.getOption8() == null || result.getOption8().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("H", result.getOption8(),
                            j - 1));
                    break;

                case 9:
                    if (result.getOption9() == null || result.getOption9().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("I", result.getOption9(),
                            j - 1));
                    break;

                case 10:
                    if (result.getOption10() == null || result.getOption10().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("J", result.getOption10(),
                            j - 1));
                    break;

                case 11:
                    if (result.getOption11() == null || result.getOption11().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("K", result.getOption11(),
                            j - 1));
                    break;

                case 12:
                    if (result.getOption12() == null || result.getOption12().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("L", result.getOption12(),
                            j - 1));
                    break;

                case 13:
                    if (result.getOption13() == null || result.getOption13().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("M", result.getOption13(),
                            j - 1));
                    break;

                case 14:
                    if (result.getOption14() == null || result.getOption14().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("N", result.getOption14(),
                            j - 1));
                    break;

                case 15:
                    if (result.getOption15() == null || result.getOption15().isEmpty()) {
                        status = true;
                        break;
                    }
                    mcqOptionsLL.addView(initMCQOptionView("O", result.getOption15(),
                            j - 1));
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
        parentLL = v.findViewById(R.id.viewLL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
        linearLayoutList.add(v);
        parentList.add(parentLL);
        tvList.add(tv);
        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_bookmark) {
            if (result.getIs_bookmark().equals("1")) {
                imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmark));
                networkCallForRemoveBookmark(result.getId());
            } else {
                imgBookmark.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.bookmarked));
                networkCallForAddBookmark(result.getId());
            }
        }
    }

    private void networkCallForAddBookmark(String id) {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
            return;
        }
        QuizApiInterface apiInterface = ApiClient.createService(QuizApiInterface.class);
        Call<JsonObject> response = null;

        String userId = SharedPreference.getInstance().getLoggedInUser().getId();
        if (viewSolutionData != null && viewSolutionData.getSet_type() != null) {
            switch (viewSolutionData.getSet_type()) {
                case "0":
                    response = apiInterface.bookmarkTestSeries(userId, id, Constants.Type.BOOKMARK_TYPE_TEST);
                    break;
                case "1":
                    response = apiInterface.bookmarkTestSeries(userId, id, Constants.Type.BOOKMARK_TYPE_QUIZ);
                    break;
                case "2":
                    response = apiInterface.bookmarkTestSeries(userId, id, Constants.Type.BOOKMARK_TYPE_DQUIZ);
                    break;
            }
        } else {
            response = apiInterface.bookmarkTestSeries(userId, id, Constants.Type.BOOKMARK_TYPE_TEST);
        }

        Objects.requireNonNull(response).enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        Log.e(TAG, " netoworkCallforAddbookmark onResponse: " + jsonResponse);
                        if (Objects.requireNonNull(jsonResponse).optBoolean(Const.STATUS)) {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                            result.setIs_bookmark("1");
                            if (activity instanceof ViewSolutionWithTabNew) {
                                if (tabName.equals(Constants.TestStatus.BOOKMARKED)) {
                                    ((ViewSolutionWithTabNew) activity).getData(1);
                                } else {
                                    ((ViewSolutionWithTabNew) activity).getData(2);
                                }
                            }
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        }
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
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());

                        Log.e(TAG, " networkcallforremovebookmark onResponse: " + jsonResponse);
                        if (Objects.requireNonNull(jsonResponse).optBoolean("status")) {
                            result.setIs_bookmark("0");
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();

                            if (activity instanceof ViewSolutionWithTabNew) {
                                if (tabName.equals(Constants.TestStatus.BOOKMARKED)) {
                                    ((ViewSolutionWithTabNew) activity).getData(1);
                                } else {
                                    ((ViewSolutionWithTabNew) activity).getData(2);
                                }
                            }
                        } else {
                            Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                        }
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

    private void initializePlayer(String videoUrl) {
        loadPlayerWithVideoURL(videoUrl);
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}