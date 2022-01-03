package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.legacy.widget.Space;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.api.ApiInterface;
import com.emedicoz.app.courseDetail.activity.TestInstructionDetailsActivity;
import com.emedicoz.app.courses.activity.NewTestQuizActivity;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.TestSeriesResultData;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.TestSeries;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.activity.VideoTestBaseActivity;
import com.emedicoz.app.testmodule.activity.ViewSolutionWithTabNew;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mridul on 28/9/17.
 */

public class NewTestQuizCourseAdapter extends RecyclerView.Adapter<NewTestQuizCourseAdapter.CourseHolder> {
    private static String testSeriesId;
    String isPurchased;
    SingleCourseData singleCourseData;
    String resultDate = "";
    ArrayList<TestSeriesResultData> testSeriesResultDataArrayList;
    private Context activity;
    private List<TestSeries> topics;
    private TestSeries currentTestSeriesData;
    private String id;
    double percentage;


    public NewTestQuizCourseAdapter(Context activity, List<TestSeries> topics, String id, String isPurchased, SingleCourseData singleCourseData) {
        this.activity = activity;
        this.topics = topics;
        this.id = id;
        this.isPurchased = isPurchased;
        this.singleCourseData = singleCourseData;
        testSeriesResultDataArrayList = new ArrayList<>();
    }

    @Override
    public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_child_test_quiz_course, parent, false);
        return new CourseHolder(view);
    }

    @Override
    public void onBindViewHolder(final CourseHolder holder, final int position) {
        holder.setData(topics.get(position), position);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public class CourseHolder extends RecyclerView.ViewHolder {
        TextView courseTV;
        TextView desTV;
        TextView validityTv;
        TextView testStartDate;
        TextView testEndDate;
        CardView parentLL;
        Button statusTV;
        ImageView lockedIV;
        LinearLayout dateLL;
        TextView newTV;
        ProgressBar progressPercentage;
        TextView tv_progress_circle;

        public CourseHolder(View itemView) {
            super(itemView);

            courseTV = itemView.findViewById(R.id.nameTV);
            desTV = itemView.findViewById(R.id.desTV);
            statusTV = itemView.findViewById(R.id.statusTV);
            parentLL = itemView.findViewById(R.id.parentLL);
            validityTv = itemView.findViewById(R.id.validityTv);
            lockedIV = itemView.findViewById(R.id.lockedIV);
            dateLL = itemView.findViewById(R.id.dateLL);
            testStartDate = itemView.findViewById(R.id.testStartDate);
            testEndDate = itemView.findViewById(R.id.testEndDate);
            newTV = itemView.findViewById(R.id.newTV);
            progressPercentage = itemView.findViewById(R.id.progressPercentage);
            tv_progress_circle = itemView.findViewById(R.id.tv_progress_circle);
        }

        private void setData(final TestSeries course, final int position) {
//            course.setDisplay_review_answer("0");
            dateLL.setVisibility(View.VISIBLE);
            currentTestSeriesData = course;
            courseTV.setText(course.getTest_series_name());
            percentage = Double.parseDouble(course.getPercentage());
            //System.out.println("percentage1-------------------------------"+percentage);
            if (percentage == 0.0){
                progressPercentage.setVisibility(View.GONE);
                tv_progress_circle.setVisibility(View.GONE);

            }else if (percentage < 59.9){
                progressPercentage.setVisibility(View.VISIBLE);
                tv_progress_circle.setVisibility(View.VISIBLE);
                progressPercentage.setProgress((int) percentage);
                progressPercentage.setProgressDrawable(activity.getResources().getDrawable(R.drawable.app_progress));
                //progressPercentage.getIndeterminateDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
                tv_progress_circle.setText(String.valueOf(percentage)+"%");

            }else if (percentage > 59.9){
                progressPercentage.setProgress((int) percentage);
                progressPercentage.setProgressDrawable(activity.getResources().getDrawable(R.drawable.green_progress));
                //progressPercentage.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                tv_progress_circle.setText(String.valueOf(percentage)+"%");
                progressPercentage.setVisibility(View.VISIBLE);
                tv_progress_circle.setVisibility(View.VISIBLE);
            }
            String status = "";


            if (course.getIs_paused().equalsIgnoreCase("0")) {

                if (!TextUtils.isEmpty(course.getDisplay_review_answer()) && course.getDisplay_review_answer().equals("1")) {
                    status = activity.getString(R.string.review_solution);
                    statusTV.setBackground(activity.getResources().getDrawable(R.drawable.bg_btn));
                } else if (!GenericUtils.isEmpty(course.getDisplay_reattempt()) && course.getDisplay_reattempt().equalsIgnoreCase("1")) {
                    status = activity.getString(R.string.reattempt);
                    percentage = Double.parseDouble(course.getPercentage());
                    //System.out.println("percentage-------------------------------"+percentage);
                    statusTV.setBackground(activity.getResources().getDrawable(R.drawable.bg_btn));

                    if (percentage == 0.0){
                        progressPercentage.setVisibility(View.GONE);
                        tv_progress_circle.setVisibility(View.GONE);
                    }else if (percentage < 59.9){
                        progressPercentage.setVisibility(View.VISIBLE);
                        tv_progress_circle.setVisibility(View.VISIBLE);
                        progressPercentage.setProgress((int) percentage);
                        progressPercentage.setProgressDrawable(activity.getResources().getDrawable(R.drawable.app_progress));

                        //progressPercentage.setProgressTintList(ColorStateList.valueOf(Color.RED));
//                        progressPercentage.getProgressDrawable().setColorFilter(
//                                Color.RED, PorterDuff.Mode.MULTIPLY);
                        //progressPercentage.getIndeterminateDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
                        tv_progress_circle.setText(String.valueOf(percentage)+"%");

                    }else if (percentage > 59.9){
                        progressPercentage.setProgress((int) percentage);
                        progressPercentage.setProgressDrawable(activity.getResources().getDrawable(R.drawable.green_progress));
                        //progressPercentage.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                        tv_progress_circle.setText(String.valueOf(percentage)+"%");
                        progressPercentage.setVisibility(View.VISIBLE);
                        tv_progress_circle.setVisibility(View.VISIBLE);
                    }
                } else {
                    status = activity.getString(R.string.result_string);
                    statusTV.setBackground(activity.getResources().getDrawable(R.drawable.completed_btn_bg));
                }

            } else if (course.getIs_paused().equalsIgnoreCase("1")) {
                status = activity.getString(R.string.resume);
                //System.out.println("percentage-------------------------------"+percentage);
            } else {
                status = activity.getString(R.string.start);
            }
            statusTV.setText(status);
            if (!TextUtils.isEmpty(course.getTest_end_date()) && !course.getTest_end_date().equals("0")) {
                validityTv.setVisibility(View.GONE);
                validityTv.setText("Valid TillQBANK_TEST_SERIES_ID: " + course.getTest_end_date());
            } else
                validityTv.setVisibility(View.GONE);

            if (!GenericUtils.isEmpty(course.getTotal_questions())) {
                desTV.setText(course.getTotal_questions() + " MCQs" + " | " + course.getTime_in_mins() + " mins");
            }

            if (!GenericUtils.isEmpty(course.getIs_new()) && course.getIs_new().equals("1"))
                newTV.setVisibility(View.VISIBLE);
            else
                newTV.setVisibility(View.GONE);

            if (singleCourseData.getIs_purchased().equalsIgnoreCase("0")) {
                if (singleCourseData.getMrp().equals("0")) {
                    statusTV.setVisibility(View.VISIBLE);
                    lockedIV.setVisibility(View.GONE);
                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                        if (singleCourseData.getFor_dams().equals("0")) {
                            statusTV.setVisibility(View.VISIBLE);
                            lockedIV.setVisibility(View.GONE);
                        } else {
                            setVisibility(course);
                        }
                    } else {
                        if (singleCourseData.getNon_dams().equals("0")) {
                            statusTV.setVisibility(View.VISIBLE);
                            lockedIV.setVisibility(View.GONE);
                        } else {
                            setVisibility(course);
                        }
                    }
                }
            } else {
                statusTV.setVisibility(View.VISIBLE);
                lockedIV.setVisibility(View.GONE);
            }

            long startMillis = getMilliFromDate(course.getTest_start_date());
            long endMillis = getMilliFromDate(course.getTest_end_date());

            long currentTimeStamp = System.currentTimeMillis();
            if (currentTimeStamp < startMillis || currentTimeStamp > endMillis) {
                lockedIV.setVisibility(View.VISIBLE);
                statusTV.setVisibility(View.GONE);
            } else {
                statusTV.setVisibility(View.VISIBLE);
                lockedIV.setVisibility(View.GONE);
            }

            Long startDate = getMilliFromDate(course.getTest_start_date());
            Date d = new Date(startDate);
            DateFormat f = new SimpleDateFormat("dd-MM-yyyy hh.mm aa");
            String dateString = f.format(d);

            testStartDate.setText(HtmlCompat.fromHtml("<font color='#CC0000'>Start date: </font>", HtmlCompat.FROM_HTML_MODE_LEGACY) + dateString);

            final Long endDate = getMilliFromDate(course.getTest_end_date());
            Date dEnd = new Date(endDate);
            DateFormat fEnd = new SimpleDateFormat("dd-MM-yyyy hh.mm aa");
            String dateStringEnd = fEnd.format(dEnd);

            testEndDate.setText(HtmlCompat.fromHtml("<font color='#CC0000'>End date: </font>", HtmlCompat.FROM_HTML_MODE_LEGACY) + dateStringEnd);

            if (!GenericUtils.isEmpty(course.getTest_result_date()))
                resultDate = course.getTest_result_date();

            statusTV.setOnClickListener((View view) -> {
                if (Helper.isConnected(activity)) {
                    SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
                    testSeriesId = course.getTest_series_id();
                    SharedPreference.getInstance().saveStudyTestSeriesId(Const.STUDY_TEST_SERIES_ID, testSeriesId);
                    if (singleCourseData.getIs_purchased().equals("1")) {
                        managePurchasedCourseTest(course, position, endDate);
                    } else {
                        if (singleCourseData.getMrp().equals("0"))
                                managePurchasedCourseTest(course, position, endDate);

                        else {
                            if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                                if (singleCourseData.getFor_dams().equals("0"))
                                    managePurchasedCourseTest(course, position, endDate);
                                else
                                    testQuizCourseDialog();
                            } else {
                                if (singleCourseData.getNon_dams().equals("0"))
                                    managePurchasedCourseTest(course, position, endDate);
                                else
                                    testQuizCourseDialog();
                            }
                        }
                    }
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.please_check_your_internet_connectivity), Toast.LENGTH_SHORT).show();
                }
            });

            lockedIV.setOnClickListener((View view) -> {
                if (Helper.isConnected(activity)) {
                    testQuizCourseDialog();
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.please_check_your_internet_connectivity), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setVisibility(TestSeries course) {
            if (course.getIs_locked().equalsIgnoreCase("0")) {
                lockedIV.setVisibility(View.VISIBLE);
                statusTV.setVisibility(View.GONE);
            } else {
                statusTV.setVisibility(View.VISIBLE);
                lockedIV.setVisibility(View.GONE);
            }
        }

        private void managePurchasedCourseTest(TestSeries course, int position, Long endDate) {
            if (!course.getIs_paused().equalsIgnoreCase("0")) {

                long millis = getMilliFromDate(course.getTest_start_date());
                String correctDateFormat = Helper.getFormatedDate(millis);

                Log.e("Current time in AM/PM: ", correctDateFormat);

                if (System.currentTimeMillis() < millis) {
                    Helper.newCustomDialog(activity,
                            activity.getString(R.string.app_name),
                            activity.getString(R.string.this_test_will_be_available_on) + " " + correctDateFormat,
                            false,
                            activity.getString(R.string.close),
                            ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_red));
                } else {
                    if (endDate < System.currentTimeMillis()) {
                        Helper.newCustomDialog(activity,
                                activity.getString(R.string.app_name),
                                activity.getString(R.string.test_date_expired),
                                false,
                                activity.getString(R.string.close),
                                ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_red));
                    } else {
                        if (course.getVideo_based().equalsIgnoreCase("1")) {
                            Intent quizView = new Intent(activity, VideoTestBaseActivity.class);
                            quizView.putExtra(Const.STATUS, false);
                            quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                            SharedPreference.getInstance().saveTestSerieId(Const.QBANK_TEST_SERIES_ID, testSeriesId);
                            activity.startActivity(quizView);
                        } else {
                            if (course.getIs_paused().equalsIgnoreCase("1")){
                                Intent quizView = new Intent(activity, TestBaseActivity.class);
                                quizView.putExtra(Const.STATUS, false);
                                quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                                SharedPreference.getInstance().saveTestSerieId(Const.QBANK_TEST_SERIES_ID, testSeriesId);
                                activity.startActivity(quizView);
                            } else {
                                Intent quizView = new Intent(activity, TestBaseActivity.class);
                                quizView.putExtra(Const.STATUS, false);
                                quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                                SharedPreference.getInstance().saveTestSerieId(Const.QBANK_TEST_SERIES_ID, testSeriesId);
                                activity.startActivity(quizView);
//                                Intent quizView = new Intent(activity, TestInstructionDetailsActivity.class);
//                                quizView.putExtra(Const.STATUS, false);
//                                quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
//                                SharedPreference.getInstance().saveTestSerieId(Const.QBANK_TEST_SERIES_ID, testSeriesId);
//                                activity.startActivity(quizView);
                            }
                        }
                        if (activity instanceof NewTestQuizActivity) {
                            ((NewTestQuizActivity) activity).lastPos = position;

                        }
                    }
                }
            } else {
                Long tsLong = System.currentTimeMillis();
                if (!TextUtils.isEmpty(course.getDisplay_review_answer()) && course.getDisplay_review_answer().equals("1")) {
                    Intent intent = new Intent(activity, ViewSolutionWithTabNew.class);
                    intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, course.getIs_user_attemp());
                    intent.putExtra(Constants.Extras.NAME, course.getTest_series_name());
                    activity.startActivity(intent);

                } else if (!GenericUtils.isEmpty(course.getDisplay_reattempt())) {
                    if (course.getDisplay_reattempt().equalsIgnoreCase("1")) {
                        networkCallForTestSeriesResult(course);
                    } else {
                        if (!resultDate.equalsIgnoreCase("") && tsLong < (Long.parseLong(resultDate) * 1000)) {
                            Intent resultScreen = new Intent(activity, QuizActivity.class);
                            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_AWAIT);
                            resultScreen.putExtra(Constants.Extras.DATE, resultDate);

                            activity.startActivity(resultScreen);
                        } else {
                            String testSegmentId = course.getIs_user_attemp();
                            Intent resultScreen = new Intent(activity, QuizActivity.class);
                            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                            resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                            activity.startActivity(resultScreen);
                        }
                    }
                } else {
                    if (!resultDate.equalsIgnoreCase("") && tsLong < (Long.parseLong(resultDate) * 1000)) {
                        Intent resultScreen = new Intent(activity, QuizActivity.class);
                        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_AWAIT);
                        resultScreen.putExtra(Constants.Extras.DATE, resultDate);
                        activity.startActivity(resultScreen);
                    } else {
                        String testSegmentId = course.getIs_user_attemp();
                        Intent resultScreen = new Intent(activity, QuizActivity.class);
                        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                        resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                        activity.startActivity(resultScreen);
                    }
                }
            }
        }

        public long getMilliFromDate(String dateFormat) {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                date = formatter.parse(dateFormat);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.e("Today is ", String.valueOf(date));
            return Objects.requireNonNull(date).getTime();
        }

        private void networkCallForTestSeriesResult(final TestSeries course) {
            if (!Helper.isConnected(activity)) {
                Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            final Progress mProgress;
            mProgress = new Progress(activity);
            mProgress.setCancelable(false);
            mProgress.show();
            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class);
            Call<JsonObject> response = apiInterface.getCompleteInfTestSeriesResult(SharedPreference.getInstance().getLoggedInUser().getId(),
                    testSeriesId);
            response.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mProgress.dismiss();
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        JSONObject jsonResponse;

                        try {
                            jsonResponse = new JSONObject(jsonObject.toString());
                            Gson gson = new Gson();

                            if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                                JSONArray data = GenericUtils.getJsonArray(jsonResponse);
                                if (!testSeriesResultDataArrayList.isEmpty()) {
                                    testSeriesResultDataArrayList.clear();
                                }
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject dataObj = data.optJSONObject(i);
                                    TestSeriesResultData testSeriesResult = gson.fromJson(dataObj.toString(), TestSeriesResultData.class);
                                    testSeriesResultDataArrayList.add(testSeriesResult);
                                }
                                showPopupResult(testSeriesResultDataArrayList, course);

                            } else {
                                Toast.makeText(activity, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                                RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(activity, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mProgress.dismiss();

                }
            });
        }

        private void showPopupResult(ArrayList<TestSeriesResultData> resultData,
                                     final TestSeries course) {
            LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v = li.inflate(R.layout.dialog_test_reattempt, null, false);
            final Dialog quizBasicInfoDialog = new Dialog(activity);
            quizBasicInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            quizBasicInfoDialog.setCanceledOnTouchOutside(false);
            quizBasicInfoDialog.setContentView(v);
            quizBasicInfoDialog.show();

            RecyclerView recyclerView;
            Button continueReattempt;
            TextView dialogTestName;

            recyclerView = v.findViewById(R.id.reattemptDialogRV);
            continueReattempt = v.findViewById(R.id.continueReattempt);
            dialogTestName = v.findViewById(R.id.dialogTestName);
            if (resultData != null && resultData.get(0).getTest_series_name() != null) {
                dialogTestName.setText(resultData.get(0).getTest_series_name());
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(new ReattemptDialogAdapter((Activity) activity, resultData, activity, quizBasicInfoDialog));
            continueReattempt.setOnClickListener(view1 -> {
                if (course.getVideo_based().equalsIgnoreCase("1")) {
                    Intent quizView = new Intent(activity, VideoTestBaseActivity.class);
                    quizView.putExtra(Const.STATUS, false);
                    quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                    quizView.putExtra(Constants.Extras.NAME, course.getTest_series_name());
                    SharedPreference.getInstance().saveTestSerieId(Const.QBANK_TEST_SERIES_ID, testSeriesId);
                    activity.startActivity(quizView);
                } else {
                    Intent quizView = new Intent(activity, TestBaseActivity.class);
                    quizView.putExtra(Const.STATUS, false);
                    quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                    SharedPreference.getInstance().saveTestSerieId(Const.QBANK_TEST_SERIES_ID, testSeriesId);
                    quizView.putExtra(Constants.Extras.NAME, course.getTest_series_name());
                    activity.startActivity(quizView);
                }
                quizBasicInfoDialog.dismiss();
            });
        }

        private void testQuizCourseDialog() {
            View v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name), activity.getString(R.string.you_have_to_buy_this_course_to_attempt_test));
            Space space;
            Button btnCancel;
            Button btnSubmit;

            space = v.findViewById(R.id.space);
            btnCancel = v.findViewById(R.id.btn_cancel);
            btnSubmit = v.findViewById(R.id.btn_submit);

            space.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);

            btnSubmit.setText(activity.getString(R.string.enroll));

            btnSubmit.setOnClickListener(view1 -> {
                Helper.dismissDialog();
                Helper.goToCourseInvoiceScreen(activity, singleCourseData);
            });
        }
    }
}
