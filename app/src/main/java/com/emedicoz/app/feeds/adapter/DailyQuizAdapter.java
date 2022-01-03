package com.emedicoz.app.feeds.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.modelo.DailyQuizData;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.SharedPreference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DailyQuizAdapter extends RecyclerView.Adapter<DailyQuizAdapter.DailyQuizViewHolder>  {

    Activity activity;
    ArrayList<DailyQuizData> dataArrayList;
    String testSeriesId = "";

    public DailyQuizAdapter(Activity activity, ArrayList<DailyQuizData> dataArrayList) {
        this.activity = activity;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public DailyQuizViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_daily_quiz, viewGroup, false);
        return new DailyQuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DailyQuizViewHolder holder, final int position) {
        final DailyQuizData data = dataArrayList.get(position);
        holder.nameTV.setText(data.getTestSeriesName());
        if (data.getSegmentId().equals("0") || data.getSegmentId().equals("")) {
            holder.statusIV.setImageResource(R.mipmap.play_daily_quiz);
            holder.quizBgColor.setBackgroundColor(Color.parseColor("#2f90d0"));
        } else {
            holder.statusIV.setImageResource(R.mipmap.completed_daily_quiz);
            holder.quizBgColor.setBackgroundColor(Color.parseColor("#00a651"));
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(Long.parseLong(data.getTestStartDate()) * 1000));
        Log.e("onBindViewHolder: ", dateString);
        holder.dateTV.setText(dateString);
/*
        Date d = new Date(Long.parseLong(dataArrayList.get(position).getTestStartDate())*1000);
        //Date d = new Date(Long.parseLong("1576837800")*1000);
        DateFormat f = new SimpleDateFormat("dd:MM:yyyy hh.mm aa");
        String dateString = f.format(d);
        Date date = null;
        try {
            date = f.parse(f.format(d));
            System.out.println("DATE PRINT:"+date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/

/*        String[] amPM = dateString.split("\\s+");

        String[] fullDate = String.valueOf(date).split("\\s+");
        String correctDateFormat = fullDate[0]+", "+fullDate[1]+" "+fullDate[2]+", "+fullDate[5]+" at "+amPM[1]+" "+amPM[2];
        holder.timeTV.setText(correctDateFormat);*/
        holder.statusIV.setOnClickListener(v -> {

            if (data.getSegmentId().equals("0")) {
                SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST);
                testSeriesId = dataArrayList.get(position).getId();
                Intent quizView = new Intent(activity, TestBaseActivity.class);
                quizView.putExtra(Const.STATUS, false);
                quizView.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                quizView.putExtra("DAILY", true);
                activity.startActivity(quizView);
                //  networkCall.NetworkAPICall(API.API_GET_COMPLETE_INFO_TEST_SERIES, true);

            } else {
                ResultTestSeries resultTestSeries = new ResultTestSeries();
                resultTestSeries.setTestSeriesName(data.getTestSeriesName());
                resultTestSeries.setId(data.getSegmentId());

                Intent resultScreen = new Intent(activity, QuizActivity.class);
                resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, data.getSegmentId());
                activity.startActivity(resultScreen);
            }
        });
        //  holder.statusIV.setImageResource(R.mipmap.complete);


/*        holder.nameTV.setText(dataArrayList.get(position).getTest_series_name());

        holder.typeTV.setText("Daily Quiz");
//            Ion.with(context).load(resultTestSeriesArrayList.get(position).get)

        Ion.with(activity).load(dataArrayList.get(position).getImage())
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (e == null && result != null)
                            holder.imageIV.setImageBitmap(result);
                        else
                            holder.imageIV.setImageResource(R.mipmap.daily_quizz);
                    }
                });

        holder.timeTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(resultTestSeriesArrayList.get(position).getCreation_time())));
        holder.seeResultLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("RESULT_DATE",resultTestSeriesArrayList.get(position).getTest_result_date() );
                if (!resultTestSeriesArrayList.get(position).getTest_result_date().equalsIgnoreCase("")){
                    Long tsLong = System.currentTimeMillis();
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(tsLong);
                    String date = DateFormat.format("dd-MM-yyyy", cal).toString();
                    Log.e("DATE_1", date);
                    // String currentTS = tsLong.toString();
                    if (tsLong<(Long.parseLong(resultTestSeriesArrayList.get(position).getTest_result_date())*1000)){
                        Intent resultScreen = new Intent(activity, QuizActivity.class);
                        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_AWAIT);
                        resultScreen.putExtra("date", resultTestSeriesArrayList.get(position).getTest_result_date());
                        activity.startActivity(resultScreen);
                    }else {
                        Intent resultScreen = new Intent(activity, QuizActivity.class);
                        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                        Constants.FROM_NAV = "true";
                        resultScreen.putExtra(Const.STATUS, resultTestSeriesArrayList.get(position).getId());
                        resultScreen.putExtra(Const.NAME, resultTestSeriesArrayList.get(position).getTest_series_name());
                        activity.startActivity(resultScreen);
                    }
                }else {

                    Intent resultScreen = new Intent(activity, QuizActivity.class);
                    resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                    Constants.FROM_NAV = "true";
                    resultScreen.putExtra(Const.STATUS, resultTestSeriesArrayList.get(position).getId());
                    resultScreen.putExtra(Const.NAME, resultTestSeriesArrayList.get(position).getTest_series_name());
                    activity.startActivity(resultScreen);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    class DailyQuizViewHolder extends RecyclerView.ViewHolder {

        TextView nameTV, timeTV, typeTV, dateTV;
        RelativeLayout seeResultLL;
        ImageView statusIV;
        LinearLayout quizBgColor;

        public DailyQuizViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.dailyQuizName);
            dateTV = itemView.findViewById(R.id.dailyQuizDate);
            statusIV = itemView.findViewById(R.id.statusIV);
            quizBgColor = itemView.findViewById(R.id.dailyQuizBgColor);
/*            seeResultLL = (RelativeLayout) itemView.findViewById(R.id.seeResultLL);
            imageIV = (ImageView) itemView.findViewById(R.id.imageIV);
            timeTV = (TextView) itemView.findViewById(R.id.timeTV);
            typeTV = itemView.findViewById(R.id.typeTV);*/
        }
    }

}
