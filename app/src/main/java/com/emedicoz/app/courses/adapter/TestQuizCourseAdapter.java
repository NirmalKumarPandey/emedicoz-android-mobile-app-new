package com.emedicoz.app.courses.adapter;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.legacy.widget.Space;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.courses.activity.TestQuizActionActivity;
import com.emedicoz.app.courses.activity.TestQuizActivity;
import com.emedicoz.app.modelo.TestSeriesResultData;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.modelo.courses.TestSeries;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mridul on 28/9/17.
 */

public class TestQuizCourseAdapter extends RecyclerView.Adapter<TestQuizCourseAdapter.CourseHolder> {
    String totalQuestion = "";
    String isPurchased;
    SingleCourseData singleCourseData;
    ArrayList<TestSeriesResultData> testSeriesResultDataArrayList;
    private Context context;
    private List<TestSeries> topics;

    public TestQuizCourseAdapter(Context context, List<TestSeries> topics, String isPurchased, SingleCourseData singleCourseData) {
        this.context = context;
        this.topics = topics;
        this.isPurchased = isPurchased;
        this.singleCourseData = singleCourseData;
        testSeriesResultDataArrayList = new ArrayList<>();
    }

    @Override
    public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_qbank, parent, false);
        return new CourseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CourseHolder holder, final int position) {
        if (position == topics.size() - 1) {
            holder.view.setVisibility(View.GONE);
        } else {
            holder.view.setVisibility(View.VISIBLE);
        }
        holder.setData(topics.get(position));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public class CourseHolder extends RecyclerView.ViewHolder {
        TextView courseTV;
        TextView reviewTV;
        TextView questionsTV;
        ImageView statusTV;
        LinearLayout qBankList;
        View view;
        private TextView newTV;

        public CourseHolder(View itemView) {
            super(itemView);

            courseTV = itemView.findViewById(R.id.nameTV);
            reviewTV = itemView.findViewById(R.id.review);
            questionsTV = itemView.findViewById(R.id.questions);
            view = itemView.findViewById(R.id.view);
            statusTV = itemView.findViewById(R.id.statusTV);
            qBankList = itemView.findViewById(R.id.qBankLayout);
            newTV = itemView.findViewById(R.id.newTV);
        }

        private void setData(final TestSeries course) {
            courseTV.setText(course.getTest_series_name());

            questionsTV.setText(String.format("Question: %s", course.getTotal_questions()));
            if (course.getAvg_rating() != null)
                reviewTV.setText(String.format("Rating: %s", course.getAvg_rating()));
            else
                reviewTV.setText(context.getResources().getString(R.string.rating));
            totalQuestion = course.getTotal_questions();

            if (!GenericUtils.isEmpty(course.getIs_new()) && course.getIs_new().equals("1"))
                newTV.setVisibility(View.VISIBLE);
            else
                newTV.setVisibility(View.GONE);

            /**
             * isPurchased key is used to check that the course is purchased or not..
             * if isPurchased is 0 then the course is not purchased
             * if isPurchased is 1 then course is purchased
             */
            if (isPurchased.equalsIgnoreCase("0")) {
                /**
                 * is_locked key is used to check that the course is locked or not..
                 * if is_locked is 0 then the course is not locked
                 * if is_locked is 1 then course is locked
                 */
                if (course.getIs_locked().equalsIgnoreCase("0")) {
                    /**
                     * is_paused key is used to check the status of the test(start,resume and completed)..
                     * if is_paused is 0 it means the test status is completed
                     * if is_paused is 1 it means the test status is paused
                     * if is_paused is blank(empty) it means test is not yet started
                     */
                    if (course.getIs_paused().equalsIgnoreCase("0")) {
                        statusTV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_correct));
                    } else if (course.getIs_paused().equalsIgnoreCase("1")) {
                        statusTV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_button));
                    } else {
                        statusTV.setVisibility(View.GONE);
                        statusTV.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.start));

                    }
                } else {
                    statusTV.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.lock));
                }
            } else {
                if (course.getIs_paused().equalsIgnoreCase("0")) {
                    statusTV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_correct));

                } else if (course.getIs_paused().equalsIgnoreCase("1")) {
                    statusTV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_button));
                } else {
                    statusTV.setVisibility(View.GONE);
                    statusTV.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.start));
                }
            }

            qBankList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreference.getInstance().saveTestSerieId(Const.QBANK_TEST_SERIES_ID, course.getTest_series_id());
                    onQbankListClick(course);
                }
            });


        }

        private void onQbankListClick(TestSeries course) {
            if (Helper.isConnected(context)) {
                SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK);
                if (!isPurchased.equalsIgnoreCase("0"))
                    goToTestStartScreen(course);

                else if (singleCourseData.getMrp().equals("0"))
                    goToTestStartScreen(course);
                else if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getDams_tokken())) {
                    if (singleCourseData.getFor_dams().equals("0"))
                        goToTestStartScreen(course);
                    else
                        showBuyPopup();
                } else {
                    if (singleCourseData.getNon_dams().equals("0"))
                        goToTestStartScreen(course);
                    else
                        showBuyPopup();
                }
            } else {
                Toast.makeText(context, R.string.please_check_your_internet_connectivity, Toast.LENGTH_SHORT).show();
            }
        }

        private void goToTestStartScreen(TestSeries course) {
            String testSeriesId = course.getTest_series_id();
            Log.e("TSI", testSeriesId);
            if (course.getIs_paused().equalsIgnoreCase("")) {
                if (course.getVideo_based().equalsIgnoreCase("1")) {
                    goToTestActivityForStart(course, Constants.ResultExtras.VIDEO); // video based start
                } else {
                    goToTestActivityForStart(course, Constants.ResultExtras.TEXT_PHOTO); // start
                }

            } else if (course.getIs_paused().equalsIgnoreCase("0")) {
                if (!GenericUtils.isEmpty(course.getDisplay_reattempt())) {
                    if (course.getDisplay_reattempt().equalsIgnoreCase("1") && course.getVideo_based().equalsIgnoreCase("1")) {
                        goToTestActivityForComplete(course, Constants.ResultExtras.VIDEO); //video based complete
                    } else {
                        goToTestActivityForComplete(course, Constants.ResultExtras.TEXT_PHOTO); // complete
                    }
                } else {
                    ResultTestSeries resultTestSeries = new ResultTestSeries();
                    resultTestSeries.setTestSeriesName(course.getTest_series_name());
                    //Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                    Intent resultScreen = new Intent(context, QuizActivity.class);
                    resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_BASIC);
                    resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, course.getIs_user_attemp());
                    resultScreen.putExtra(Const.RESULT_SCREEN, resultTestSeries);
                    resultScreen.putExtra(Constants.Extras.SUBJECT_NAME, ((TestQuizActivity) context).titlefrag);
                    context.startActivity(resultScreen);
                }
            } else {
                if (course.getVideo_based().equalsIgnoreCase("1")) {
                    goToTestActivityForPause(course, Constants.ResultExtras.VIDEO); // video based pause
                } else {
                    goToTestActivityForPause(course, Constants.ResultExtras.TEXT_PHOTO); // pause
                }
            }
        }

        private void goToTestActivityForStart(TestSeries course, String quizType) {
            Intent quizView = new Intent(context, TestQuizActionActivity.class);
            quizView.putExtra(Const.STATUS, false);
            quizView.putExtra(Const.TEST_SERIES_ID, course.getTest_series_id());
            quizView.putExtra(Constants.Extras.QUIZ_TYPE, quizType);
            quizView.putExtra(Constants.Extras.TITLE_NAME, course.getTest_series_name());
            quizView.putExtra(Constants.Extras.QUES_NUM, course.getTotal_questions());
            quizView.putExtra(Constants.Extras.RATING, course.getAvg_rating());
            quizView.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.START);
            context.startActivity(quizView);
        }

        private void goToTestActivityForPause(TestSeries course, String quizType) {
            Intent quizView = new Intent(context, TestQuizActionActivity.class);
            quizView.putExtra(Const.STATUS, false);
            quizView.putExtra(Constants.Extras.QUIZ_TYPE, quizType);
            quizView.putExtra(Constants.Extras.TITLE_NAME, course.getTest_series_name());
            quizView.putExtra(Constants.Extras.QUES_NUM, course.getTotal_questions());
            quizView.putExtra(Constants.Extras.RATING, course.getAvg_rating());
            quizView.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.PAUSE);
            quizView.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, course.getIs_user_attemp());
            quizView.putExtra(Const.TEST_SERIES_ID, course.getTest_series_id());
            context.startActivity(quizView);
        }

        private void goToTestActivityForComplete(TestSeries course, String quizType) {
            Intent quizView = new Intent(context, TestQuizActionActivity.class);
            quizView.putExtra(Const.STATUS, false);
            quizView.putExtra(Constants.Extras.QUIZ_TYPE, quizType);
            quizView.putExtra(Constants.Extras.TITLE_NAME, course.getTest_series_name());
            quizView.putExtra(Constants.Extras.QUES_NUM, course.getTotal_questions());
            quizView.putExtra(Constants.Extras.RATING, course.getAvg_rating());
            quizView.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.COMPLETE);
            quizView.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, course.getIs_user_attemp());
            quizView.putExtra(Const.TEST_SERIES_ID, course.getTest_series_id());
            context.startActivity(quizView);
        }

        private void showBuyPopup() {
            View v = Helper.newCustomDialog(context, true, context.getString(R.string.app_name), context.getString(R.string.you_have_to_buy_this_qbank_to_attempt));

            Space space;
            Button btnCancel;
            Button btnSubmit;

            space = v.findViewById(R.id.space);
            btnCancel = v.findViewById(R.id.btn_cancel);
            btnSubmit = v.findViewById(R.id.btn_submit);

            space.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnSubmit.setText(context.getString(R.string.enroll));

            btnSubmit.setOnClickListener((View view) -> {
                Helper.dismissDialog();
                Helper.goToCourseInvoiceScreen(context, singleCourseData);
               // Helper.goToCartScreen(context, singleCourseData);
            });
        }
    }
}
