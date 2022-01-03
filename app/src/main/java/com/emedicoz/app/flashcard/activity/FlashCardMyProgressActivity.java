package com.emedicoz.app.flashcard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.adapter.FlashCardProgressAdapter;
import com.emedicoz.app.flashcard.adapter.FlashCardSubjectWiseProgressAdapter;
import com.emedicoz.app.flashcard.model.myprogress.GraphProgress;
import com.emedicoz.app.flashcard.model.myprogress.SubjectWiseType;
import com.emedicoz.app.flashcard.model.myprogress.Subjectwise;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;

public class FlashCardMyProgressActivity extends AppCompatActivity implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    ImageView ivBack;
    ImageView ivDrawer;
    CircleImageView ivProfile;
    TextView tvCurrentDate;
    TextView tvUserName;
    TextView tvTitle;
    TextView tvAvg;
    TextView tvCardStudied;
    TextView tvTotalCardCount;
    TextView tvLongestStreak;
    TextView tvTotalCard;
    TextView tvCurrentStreak;
    TextView tvEasy;
    TextView tvMedium;
    TextView tvHard;
    TextView tvStudiedCardCountAndTime;
    TextView tvDate1;
    TextView tvDate2;
    TextView tvDate3;
    TextView tvDate4;
    TextView tvDate5;
    TextView tvDate6;
    TextView tvDate7;
    TextView tvProgressForecast;
    TextView tvDay1;
    TextView tvDay2;
    TextView tvDay3;
    TextView tvDay4;
    TextView tvDay5;
    TextView tvDay6;
    TextView tvDay7;
    RecyclerView rvProgressGraph;
    RecyclerView rvSubjectWiseProgress;
    TextView tvWeekDate;
    Button btnSeeSubjectWiseCard;
    Button btnSeeSubjectWiseProgress;
    LinearLayout subjectWiseLL;
    MyNetworkCall myNetworkCall;
    Button btnPrevious;
    Button btnNext;
    private String TAG = "FlashCardMyProgressActi";
    DateTime now;
    DateTime weekFirstDate;
    DateTime weekLastDate;
    DateTime actualDate;
    ArrayList<Subjectwise> subjectWiseArrayList = new ArrayList<>();
    ArrayList<SubjectWiseType> subjectWiseTypeArrayList = new ArrayList<>();
    Set<String> stringHashSet;
    Set<String> dateStringSet;
    String type = "";
    List<String> dateList = new ArrayList<>();
    CircularProgressIndicator circularProgress;
    boolean isSubjectWise = false;
    FlashCardProgressAdapter flashCardProgressAdapter;
    ArrayList<GraphProgress> graphProgressArrayList = new ArrayList<>();
    GraphProgress graphProgress;
    ArrayList<Integer> integerArrayList = new ArrayList<>();
    FlashCardSubjectWiseProgressAdapter flashCardSubjectWiseProgressAdapter;
    LinearLayout progressRootLL;
    TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_flash_card_my_progress);

        now = DateTime.now(DateTimeZone.forID("Asia/Kolkata"));  // get current date for calculate weekly progress
        weekFirstDate = now.minusDays(6);
        weekLastDate = now;

        actualDate = now;

        Log.e(TAG, "onCreate: plusDays : " + now.plusDays(1).toLocalDate());
        Log.e(TAG, "onCreate: plusWeeks : " + now.plusWeeks(1).toLocalDate());
        Log.e(TAG, "onCreate: minusDays : " + now.minusDays(1).toLocalDate());
        Log.e(TAG, "onCreate: minusDays : 6: " + now.minusDays(6).toLocalDate());
        Log.e(TAG, "onCreate: minusWeeks :" + now.minusWeeks(1).toLocalDate());

        initViews();
        bindControls();
    }

    private void initViews() {
        myNetworkCall = new MyNetworkCall(this, this);

        progressRootLL = findViewById(R.id.ll_progress_root);
        tvError = findViewById(R.id.tv_error);
        circularProgress = findViewById(R.id.circular_progress);
        rvSubjectWiseProgress = findViewById(R.id.rv_subjectwise_progress);
        rvProgressGraph = findViewById(R.id.rv_progress_graph);
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        ivDrawer = findViewById(R.id.iv_drawer);
        tvWeekDate = findViewById(R.id.tv_week_date);
        btnSeeSubjectWiseCard = findViewById(R.id.btn_see_subjectwise_card);
        btnSeeSubjectWiseProgress = findViewById(R.id.btn_see_subjectwise_progress);
        subjectWiseLL = findViewById(R.id.ll_subjectwise);
        tvStudiedCardCountAndTime = findViewById(R.id.tv_studied_card_count_and_time);
        ivProfile = findViewById(R.id.iv_profile);
        tvUserName = findViewById(R.id.tv_user_name);
        tvCurrentDate = findViewById(R.id.tv_current_date);

        tvDate1 = findViewById(R.id.tv_date_1);
        tvDate2 = findViewById(R.id.tv_date_2);
        tvDate3 = findViewById(R.id.tv_date_3);
        tvDate4 = findViewById(R.id.tv_date_4);
        tvDate5 = findViewById(R.id.tv_date_5);
        tvDate6 = findViewById(R.id.tv_date_6);
        tvDate7 = findViewById(R.id.tv_date_7);

        tvDay1 = findViewById(R.id.tv_day1);
        tvDay2 = findViewById(R.id.tv_day2);
        tvDay3 = findViewById(R.id.tv_day3);
        tvDay4 = findViewById(R.id.tv_day4);
        tvDay5 = findViewById(R.id.tv_day5);
        tvDay6 = findViewById(R.id.tv_day6);
        tvDay7 = findViewById(R.id.tv_day7);

        tvEasy = findViewById(R.id.tv_easy);
        tvProgressForecast = findViewById(R.id.tv_progress_forcast);
        tvMedium = findViewById(R.id.tv_medium);
        tvHard = findViewById(R.id.tv_hard);
        tvAvg = findViewById(R.id.tv_avg);
        tvCardStudied = findViewById(R.id.tv_card_studied);
        tvLongestStreak = findViewById(R.id.tv_longest_streak);
        tvTotalCardCount = findViewById(R.id.tv_total_card_count);
        tvTotalCard = findViewById(R.id.tv_total_card);
        tvCurrentStreak = findViewById(R.id.tv_current_streak);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
    }

    private void bindControls() {
        tvTitle.setText(R.string.my_progress);
        ivBack.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSeeSubjectWiseCard.setOnClickListener(this);
        btnSeeSubjectWiseProgress.setOnClickListener(this);
        ivDrawer.setVisibility(View.GONE);

        rvProgressGraph.setLayoutManager(new LinearLayoutManager(this));
        rvSubjectWiseProgress.setLayoutManager(new LinearLayoutManager(this));

        flashCardProgressAdapter = new FlashCardProgressAdapter(this, graphProgressArrayList,integerArrayList);
        rvProgressGraph.setAdapter(flashCardProgressAdapter);

        flashCardSubjectWiseProgressAdapter = new FlashCardSubjectWiseProgressAdapter(subjectWiseTypeArrayList, this);
        rvSubjectWiseProgress.setAdapter(flashCardSubjectWiseProgressAdapter);

        rvProgressGraph.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        rvSubjectWiseProgress.addItemDecoration(new EqualSpacingItemDecoration(10, EqualSpacingItemDecoration.VERTICAL));
        ViewCompat.setNestedScrollingEnabled(rvProgressGraph, false);
        ViewCompat.setNestedScrollingEnabled(rvSubjectWiseProgress, false);

        tvUserName.setText(SharedPreference.getInstance().getLoggedInUser().getName());

        if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getProfile_picture())) {
            Glide.with(this).load(SharedPreference.getInstance().getLoggedInUser().getProfile_picture()).into(ivProfile);
        } else {
            ivProfile.setImageResource(R.mipmap.gray_user);
        }

        tvCurrentDate.setText(parseDateToddMMyyyy(now.toLocalDate().toString(), "dd/MM/yyyy"));

        hitApi();
    }

    private void hitSubjectForecast() {
        myNetworkCall.NetworkAPICall(API.API_GET_SUBJECT_FORCAST, true);
    }

    private void hitProgressForecast() {
        myNetworkCall.NetworkAPICall(API.API_GET_PROGRESS_FORCAST, true);
    }

    private void hitApi() {
        myNetworkCall.NetworkAPICall(API.API_GET_PROGRESS, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                type = Const.PREVIOUS;

                weekLastDate = getPreviousWeekFirstDate(weekFirstDate);
                weekFirstDate = getPreviousWeekLastDate(weekFirstDate);
                actualDate = weekLastDate;

                Log.e(TAG, "onClick:weekFirstDate " + weekFirstDate.toLocalDate());
                Log.e(TAG, "onClick:weekLastDate " + weekLastDate.toLocalDate());

                if (isSubjectWise) {
                    hitSubjectForecast();
                } else {
                    hitProgressForecast();
                }
                break;
            case R.id.btn_next:
                type = Const.NEXT;

                weekFirstDate = getWeekFirstDate(actualDate);
                weekLastDate = getWeekLastDate(actualDate);
                actualDate = weekLastDate;

                Log.e(TAG, "onClick:weekFirstDate " + weekFirstDate.toLocalDate());
                Log.e(TAG, "onClick:weekLastDate " + weekLastDate.toLocalDate());

                if (isSubjectWise) {
                    hitSubjectForecast();
                } else {
                    hitProgressForecast();
                }
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_see_subjectwise_card:
                startActivity(new Intent(this, SubjectWiseCardActivity.class));
                break;
            case R.id.btn_see_subjectwise_progress:
                isSubjectWise = !isSubjectWise;
                if (rvProgressGraph.getVisibility() == View.VISIBLE && subjectWiseLL.getVisibility() == View.GONE) {
                    rvProgressGraph.setVisibility(View.GONE);
                    subjectWiseLL.setVisibility(View.VISIBLE);
                    hitSubjectForecast();
                } else {
                    rvProgressGraph.setVisibility(View.VISIBLE);
                    subjectWiseLL.setVisibility(View.GONE);
                    hitProgressForecast();
                }

                break;
            default:
                break;
        }
    }


    private DateTime getWeekFirstDate(DateTime dateTime) {
        return dateTime.plusDays(1);
    }

    private DateTime getWeekLastDate(DateTime dateTime) {
        return dateTime.plusWeeks(1);
    }

    private DateTime getPreviousWeekFirstDate(DateTime dateTime) {
        return dateTime.minusDays(1);
    }

    private DateTime getPreviousWeekLastDate(DateTime dateTime) {
        return dateTime.minusWeeks(1);
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getId() : "");
        switch (apiType) {
            case API.API_GET_PROGRESS_FORCAST:
            case API.API_GET_SUBJECT_FORCAST:
                params.put(Constants.Extras.DATE, actualDate.toLocalDate());
                break;
        }
        Log.e(TAG, "getAPI: " + params);
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        Gson gson = new Gson();
        progressRootLL.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        switch (apiType) {
            case API.API_GET_PROGRESS_FORCAST:
                JSONObject jsonObject1 = jsonObject.optJSONObject(Const.DATA);
                setDateWiseProgress(Objects.requireNonNull(jsonObject1), gson);

                tvWeekDate.setText(String.format("%s - %s", parseDateToddMMyyyy(weekFirstDate.toLocalDate().toString(), "dd MMM yyyy"), parseDateToddMMyyyy(weekLastDate.toLocalDate().toString(), "dd MMM yyyy")));
                break;
            case API.API_GET_SUBJECT_FORCAST:

                tvWeekDate.setText(String.format("%s - %s", parseDateToddMMyyyy(weekFirstDate.toLocalDate().toString(), "dd MMM yyyy"), parseDateToddMMyyyy(weekLastDate.toLocalDate().toString(), "dd MMM yyyy")));

                Log.e(TAG, "SuccessCallBack:weekFirstDate " + weekFirstDate.toLocalDate());
                Log.e(TAG, "SuccessCallBack:weekLastDate " + weekLastDate.toLocalDate());

                JSONArray jsonArray = jsonObject.optJSONArray(Const.DATA);
                if (jsonArray != null && jsonArray.length() != 0) {
                    stringHashSet = Collections.synchronizedSet(new LinkedHashSet<String>());
                    dateStringSet = Collections.synchronizedSet(new LinkedHashSet<String>());


                    Subjectwise subjectwise;
                    subjectWiseArrayList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        subjectwise = gson.fromJson(jsonArray.optJSONObject(i).toString(), Subjectwise.class);
                        subjectWiseArrayList.add(subjectwise);
                    }

                    for (int i = 0; i < subjectWiseArrayList.size(); i++) {
                        stringHashSet.add(subjectWiseArrayList.get(i).getDeck());
                        for (int j = 0; j < subjectWiseArrayList.get(i).getProgress().size(); j++) {
                            dateStringSet.add(subjectWiseArrayList.get(i).getProgress().get(j).getDate());
                        }
                    }

                    subjectWiseTypeArrayList.clear();
                    for (String string : stringHashSet) {
                        subjectWiseTypeArrayList.add(new SubjectWiseType(0, string));
                        for (int i = 0; i < subjectWiseArrayList.size(); i++) {
                            if (string.equals(subjectWiseArrayList.get(i).getDeck())) {
                                subjectWiseTypeArrayList.add(new SubjectWiseType(1, new Subjectwise(subjectWiseArrayList.get(i).getId(), subjectWiseArrayList.get(i).getTitle(),
                                        subjectWiseArrayList.get(i).getProgress())));
                            }
                        }
                    }

                    setDate();
                    flashCardSubjectWiseProgressAdapter.notifyDataSetChanged();
                }
                break;
            case API.API_GET_PROGRESS:
                tvWeekDate.setText(String.format("%s - %s", parseDateToddMMyyyy(weekFirstDate.toLocalDate().toString(), "dd MMM yyyy"), parseDateToddMMyyyy(weekLastDate.toLocalDate().toString(), "dd MMM yyyy")));

                JSONObject jsonData = jsonObject.optJSONObject(Const.DATA);
                JSONObject jsonToday = Objects.requireNonNull(jsonData).optJSONObject("today");

                tvAvg.setText(jsonData.optString("avg").length() > 5 ? "Daily Average : " + jsonData.optString("avg").substring(0, 5) : "Daily Average : " + jsonData.optString("avg"));
                tvCardStudied.setText(String.format("Cards Studied : %s", jsonData.optString("card_studied")));

                tvLongestStreak.setText(Integer.parseInt(jsonData.optString("long_interval")) > 1 ? "Longest Streak : " + jsonData.optString("long_interval") + " Days" : "Longest Streak : " + jsonData.optString("long_interval") + " Day");
                tvTotalCard.setText(String.format("Total Cards : %s", jsonData.optString("total_card")));

                tvCurrentStreak.setText(Integer.parseInt(jsonData.optString("current_interval")) > 1 ? "Current Streak : " + jsonData.optString("current_interval") + " Days" : "Current Streak : " + jsonData.optString("current_interval") + " Day");

                tvEasy.setText(jsonToday.optString("easy"));
                tvMedium.setText(jsonToday.optString("medium"));
                tvHard.setText(jsonToday.optString("hard"));

                tvTotalCardCount.setText(String.format("Total cards today : %s", jsonData.optJSONObject("today").optString("added")));

                circularProgress.setMaxProgress(Double.parseDouble(jsonData.optJSONObject("today").optString("added")));
                circularProgress.setCurrentProgress(Double.parseDouble(jsonData.optJSONObject("today").optString("study")));

                //  setTotalCardAndTime(jsonData.optString("card_studied"), jsonData.optInt("time_taken"));
                setTotalCardAndTime(jsonToday.optString("study"), jsonData.optInt("time_taken"));

                setDateWiseProgress(jsonData, gson);
                break;
        }

        if (type.equals(Const.PREVIOUS)) {
            if (compareTime("yyyy-MM-dd", now.toString(), weekFirstDate.toString())) {
                tvProgressForecast.setText(getString(R.string.forecast));
                btnSeeSubjectWiseProgress.setText(getString(R.string.see_sujectwise_forecast));
            } else {
                tvProgressForecast.setText(getString(R.string.progress));
                btnSeeSubjectWiseProgress.setText(getString(R.string.see_sujectwise_progress));
            }
        } else {
            if (compareTime("yyyy-MM-dd", now.toString(), weekLastDate.toString())) {
                tvProgressForecast.setText(getString(R.string.forecast));
                btnSeeSubjectWiseProgress.setText(getString(R.string.see_sujectwise_forecast));
            } else {
                tvProgressForecast.setText(getString(R.string.progress));
                btnSeeSubjectWiseProgress.setText(getString(R.string.see_sujectwise_progress));
            }
        }

    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        if (API.API_GET_PROGRESS.equals(apiType)) {
            progressRootLL.setVisibility(View.GONE);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(jsonString);
        }
        if (!TextUtils.isEmpty(Helper.GetText(tvWeekDate))) {
            String[] elements = Helper.GetText(tvWeekDate).split("-");

            Log.e(TAG, "ErrorCallBack: " + elements[0]);
            Log.e(TAG, "ErrorCallBack: " + elements[1]);
            Log.e(TAG, "ErrorCallBack: " + parseDateyyyyMMdd(elements[0], "dd MMM yyyy", "yyyy-MM-dd"));

            weekFirstDate = DateTime.parse(parseDateyyyyMMdd(elements[0], "dd MMM yyyy", "yyyy-MM-dd"));
            weekLastDate = DateTime.parse(parseDateyyyyMMdd(elements[1], "dd MMM yyyy", "yyyy-MM-dd"));
            actualDate = weekLastDate;
        }
    }

    private boolean compareTime(String format, String startDate, String endDate) {
        String pattern = format;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(startDate);
            Date date2 = sdf.parse(endDate);
            System.out.println(date1);
            System.out.println(date2);
            return Objects.requireNonNull(date1).before(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void setDateWiseProgress(JSONObject jsonData, Gson gson) {
        JSONArray jsonArray1 = jsonData.optJSONArray(Const.PROGRESS);

        graphProgressArrayList.clear();
        for (int i = Objects.requireNonNull(jsonArray1).length() - 1; i >= 0; i--) {
            graphProgress = gson.fromJson(jsonArray1.optJSONObject(i).toString(), GraphProgress.class);
            graphProgressArrayList.add(graphProgress);
        }

        integerArrayList.clear();
        for (GraphProgress graphProgress : graphProgressArrayList) {
            integerArrayList.add(Integer.parseInt(graphProgress.getRead_card()));
        }
        Collections.sort(integerArrayList);
        Log.e(TAG, "setDateWiseProgress: " + (integerArrayList.get(integerArrayList.size() - 1)));
        flashCardProgressAdapter.notifyDataSetChanged();
    }

    public void setTotalCardAndTime(String totalReadCard, int totalTime) {
        String minute = Helper.combinationFormatter(totalTime);
        tvStudiedCardCountAndTime.setText(Html.fromHtml("Studied <font color='#2f90d0'>" + totalReadCard + "</font> Cards in <font color='#2f90d0'>" + minute + "</font> Today"));
    }

    private void setDate() {
        dateList = new ArrayList<String>(dateStringSet);

        tvDay1.setText(Html.fromHtml(parseDateToEE(dateList.get(6)).equalsIgnoreCase("sun") ? "<font color='#2f90d0'>" + parseDateToEE(dateList.get(6)).charAt(0) + "</font>" : "<font color='#000000'>" + parseDateToEE(dateList.get(6)).charAt(0) + "</font>"));
        tvDay2.setText(Html.fromHtml(parseDateToEE(dateList.get(5)).equalsIgnoreCase("sun") ? "<font color='#2f90d0'>" + parseDateToEE(dateList.get(5)).charAt(0) + "</font>" : "<font color='#000000'>" + parseDateToEE(dateList.get(5)).charAt(0) + "</font>"));
        tvDay3.setText(Html.fromHtml(parseDateToEE(dateList.get(4)).equalsIgnoreCase("sun") ? "<font color='#2f90d0'>" + parseDateToEE(dateList.get(4)).charAt(0) + "</font>" : "<font color='#000000'>" + parseDateToEE(dateList.get(4)).charAt(0) + "</font>"));
        tvDay4.setText(Html.fromHtml(parseDateToEE(dateList.get(3)).equalsIgnoreCase("sun") ? "<font color='#2f90d0'>" + parseDateToEE(dateList.get(3)).charAt(0) + "</font>" : "<font color='#000000'>" + parseDateToEE(dateList.get(3)).charAt(0) + "</font>"));
        tvDay5.setText(Html.fromHtml(parseDateToEE(dateList.get(2)).equalsIgnoreCase("sun") ? "<font color='#2f90d0'>" + parseDateToEE(dateList.get(2)).charAt(0) + "</font>" : "<font color='#000000'>" + parseDateToEE(dateList.get(2)).charAt(0) + "</font>"));
        tvDay6.setText(Html.fromHtml(parseDateToEE(dateList.get(1)).equalsIgnoreCase("sun") ? "<font color='#2f90d0'>" + parseDateToEE(dateList.get(1)).charAt(0) + "</font>" : "<font color='#000000'>" + parseDateToEE(dateList.get(1)).charAt(0) + "</font>"));
        tvDay7.setText(Html.fromHtml(parseDateToEE(dateList.get(0)).equalsIgnoreCase("sun") ? "<font color='#2f90d0'>" + parseDateToEE(dateList.get(0)).charAt(0) + "</font>" : "<font color='#000000'>" + parseDateToEE(dateList.get(0)).charAt(0) + "</font>"));


        tvDate1.setText(Html.fromHtml(Helper.parseDateToddMM(dateList.get(6))));
        tvDate2.setText(Html.fromHtml(Helper.parseDateToddMM(dateList.get(5))));
        tvDate3.setText(Html.fromHtml(Helper.parseDateToddMM(dateList.get(4))));
        tvDate4.setText(Html.fromHtml(Helper.parseDateToddMM(dateList.get(3))));
        tvDate5.setText(Html.fromHtml(Helper.parseDateToddMM(dateList.get(2))));
        tvDate6.setText(Html.fromHtml(Helper.parseDateToddMM(dateList.get(1))));
        tvDate7.setText(Html.fromHtml(Helper.parseDateToddMM(dateList.get(0))));
    }

    public static String parseDateToddMMyyyy(String time, String dateFormat) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = dateFormat;
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(Objects.requireNonNull(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateyyyyMMdd(String time, String inputDateFormat, String outputDateFormat) {
        String inputPattern = inputDateFormat;
        String outputPattern = outputDateFormat;
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(Objects.requireNonNull(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToEE(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "EE";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(Objects.requireNonNull(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(str);
    }
}