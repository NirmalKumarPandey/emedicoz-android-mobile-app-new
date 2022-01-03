package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.courses.activity.TestQuizActionActivity;
import com.emedicoz.app.modelo.courses.FAQ;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.retrofit.Constants;
import com.emedicoz.app.utilso.Const;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by appsquadz on 25/9/17.
 */

public class CommonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    Bitmap bitmap;
    LayoutInflater layoutInflater;
    String type;
    List<FAQ> faqArrayList;
    List<ResultTestSeries> resultTestSeriesArrayList;

    public CommonListAdapter(Context activity, String type, List<FAQ> faqArrayList) {
        this.context = activity;
        this.type = type;
        this.faqArrayList = faqArrayList;
        this.layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CommonListAdapter(Context activity, List<ResultTestSeries> resultTestSeriesArrayList) {
        this.context = activity;
        this.resultTestSeriesArrayList = resultTestSeriesArrayList;
        this.layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (type != null && type.equals(Const.FAQ))
            return 0;
        else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_faq, parent, false);
            return new ViewHolder(view);
        } else if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_leaderboard, parent, false);
            return new LeaderboardHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sholder, final int position) {

        if (type != null && type.equals(Const.FAQ)) {
            ViewHolder holder1 = (ViewHolder) sholder;
            holder1.setSingleFAQData(faqArrayList.get(position));
        } else {
            final LeaderboardHolder holder = (LeaderboardHolder) sholder;
            holder.nameTV.setText(resultTestSeriesArrayList.get(position).getTestSeriesName());
            String setType = resultTestSeriesArrayList.get(position).getSetType();
            if (setType != null &&
                    !setType.equalsIgnoreCase("")) {
                if (setType.equalsIgnoreCase("0")) {
                    holder.typeTV.setText("Test");
                } else if (setType.equalsIgnoreCase("1")) {
                    holder.typeTV.setText("Quiz");
                } else {
                    holder.typeTV.setText("Daily Quiz");
                }
            }

            Glide.with(context)
                    .asBitmap()
                    .load(resultTestSeriesArrayList.get(position).getImage())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap result, Transition<? super Bitmap> transition) {
                            holder.imageIV.setImageBitmap(result);
                        }
                    });

            holder.timeTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(resultTestSeriesArrayList.get(position).getCreationTime())));
            holder.parentLL.setOnClickListener((View view) -> {
                Log.e("RESULT_DATE", resultTestSeriesArrayList.get(position).getTestResultDate());
                long tsLong = System.currentTimeMillis();
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(tsLong);
                String date = DateFormat.format("dd-MM-yyyy", cal).toString();
                Log.e("DATE_1", date);
                if (resultTestSeriesArrayList.get(position).getSetType().equalsIgnoreCase("0") || resultTestSeriesArrayList.get(position).getSetType().equalsIgnoreCase("2")) {
                    if (!resultTestSeriesArrayList.get(position).getTestResultDate().equalsIgnoreCase("")) {
                        if (tsLong < (Long.parseLong(resultTestSeriesArrayList.get(position).getTestResultDate()) * 1000)) {
                            goToResultAwaitScreen(position);
                        } else {
                            goToTestSeriesResultScreen(position);
                        }
                    } else {
                        goToTestSeriesResultScreen(position);
                    }
                } else {
                    if (!resultTestSeriesArrayList.get(position).getTestResultDate().equalsIgnoreCase("")) {
                        if (tsLong < (Long.parseLong(resultTestSeriesArrayList.get(position).getTestResultDate()) * 1000)) {
                            goToResultAwaitScreen(position);
                        } else {
                            goToQbankResultScreen(position);
                        }
                    } else {
                        goToQbankResultScreen(position);
                    }
                }
            });
        }
    }

    private void goToResultAwaitScreen(int position) {
        Intent resultScreen = new Intent(context, QuizActivity.class);
        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_AWAIT);
        resultScreen.putExtra("date", resultTestSeriesArrayList.get(position).getTestResultDate());
        context.startActivity(resultScreen);
    }

    private void goToTestSeriesResultScreen(int position) {

        Intent resultScreen = new Intent(context, QuizActivity.class);
        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
        Constants.FROM_NAV = "true";
        resultScreen.putExtra(com.emedicoz.app.utilso.Constants.ResultExtras.TEST_SEGMENT_ID, resultTestSeriesArrayList.get(position).getId());
        resultScreen.putExtra(com.emedicoz.app.utilso.Constants.Extras.NAME, resultTestSeriesArrayList.get(position).getTestSeriesName());
        resultScreen.putExtra(Const.TEST_SERIES_ID, resultTestSeriesArrayList.get(position).getTestSeriesId());
        context.startActivity(resultScreen);
    }

    private void goToQbankResultScreen(int position) {
        // Old code for qbank result {with graph}
      /*  Intent resultScreen = new Intent(context, QuizActivity.class);
        resultScreen.putExtra(Const.FRAG_TYPE, Const.QBANK_RESULT);
        resultScreen.putExtra(Const.TOTAL_QUESTIONS, "");
        resultScreen.putExtra(com.emedicoz.app.utils.Constants.ResultExtras.TEST_SEGMENT_ID, resultTestSeriesArrayList.get(position).getId());
        context.startActivity(resultScreen);*/
        String testType = resultTestSeriesArrayList.get(position).getTestType();
        if (testType != null && testType.equalsIgnoreCase("video")) {
            goToTestActivityForComplete(resultTestSeriesArrayList.get(position), com.emedicoz.app.utilso.Constants.ResultExtras.VIDEO); //video based complete
        } else {
            goToTestActivityForComplete(resultTestSeriesArrayList.get(position), com.emedicoz.app.utilso.Constants.ResultExtras.TEXT_PHOTO); // complete
        }
    }

    private void goToTestActivityForComplete(ResultTestSeries course, String quizType) {
        Intent quizView = new Intent(context, TestQuizActionActivity.class);
        quizView.putExtra(Const.STATUS, false);
        quizView.putExtra(com.emedicoz.app.utilso.Constants.Extras.QUIZ_TYPE, quizType);
        quizView.putExtra(com.emedicoz.app.utilso.Constants.Extras.TITLE_NAME, course.getTestSeriesName());
        quizView.putExtra(com.emedicoz.app.utilso.Constants.Extras.QUES_NUM, course.getTotalQuestions());
        quizView.putExtra(com.emedicoz.app.utilso.Constants.Extras.RATING, course.getAvgRating());
        quizView.putExtra(com.emedicoz.app.utilso.Constants.Extras.SCREEN_TYPE, com.emedicoz.app.utilso.Constants.ResultExtras.COMPLETE);
        quizView.putExtra(com.emedicoz.app.utilso.Constants.ResultExtras.TEST_SEGMENT_ID, course.getId());
        quizView.putExtra(Const.TEST_SERIES_ID, course.getTestSeriesId());

        context.startActivity(quizView);
    }

    @Override
    public int getItemCount() {
        if (getItemViewType(0) == 0) {
            return faqArrayList.size();
        } else return resultTestSeriesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView questiontextTV;
        LinearLayout parentLL;
        private ImageView dropDownIV;
        private View dividerId;
        private TextView answertextTV;
        private LinearLayout mainLL;

        public ViewHolder(View itemView) {
            super(itemView);
            questiontextTV = itemView.findViewById(R.id.questiontextTV);
            dropDownIV = itemView.findViewById(R.id.dropDownIV);
            answertextTV = itemView.findViewById(R.id.answertextTV);
            mainLL = itemView.findViewById(R.id.lowerViewItem);
            parentLL = itemView.findViewById(R.id.parentLL);
            dividerId = itemView.findViewById(R.id.dividerV);
        }

        public void setSingleFAQData(FAQ singlefaqdata) {

            parentLL.setOnClickListener((View view) -> {
                if (mainLL.getVisibility() == View.GONE) {
                    mainLL.setVisibility(View.VISIBLE);
                    dividerId.setVisibility(View.VISIBLE);
                    dropDownIV.setImageResource(R.mipmap.up_black);
                } else {
                    mainLL.setVisibility(View.GONE);
                    dividerId.setVisibility(View.GONE);
                    dropDownIV.setImageResource(R.mipmap.down_black);
                }
            });
            questiontextTV.setText(singlefaqdata.getQuestion());
            answertextTV.setText(singlefaqdata.getDescription());
        }
    }

    public class LeaderboardHolder extends RecyclerView.ViewHolder {
        TextView nameTV;
        TextView timeTV;
        TextView typeTV;
        RelativeLayout seeResultLL;
        ImageView imageIV;
        LinearLayout parentLL;

        public LeaderboardHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            seeResultLL = itemView.findViewById(R.id.seeResultLL);
            imageIV = itemView.findViewById(R.id.imageIV);
            timeTV = itemView.findViewById(R.id.timeTV);
            typeTV = itemView.findViewById(R.id.typeTV);
            parentLL = itemView.findViewById(R.id.parentLL);
        }
    }


}
