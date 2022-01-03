package com.emedicoz.app.dailychallenge.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.dailychallenge.model.DCArchiveData;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.SharedPreference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCArchivesAdapter extends RecyclerView.Adapter<DCArchivesAdapter.DCArchiveViewHolder> {
    Activity activity;
    List<DCArchiveData> itemList;

    public DCArchivesAdapter(Activity activity, List<DCArchiveData> itemList) {
        this.activity = activity;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public DCArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_archive_layout, viewGroup, false);
        return new DCArchiveViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DCArchiveViewHolder dcArchiveViewHolder, int i) {
        String name = itemList.get(i).getTestSeriesName();
        if (!GenericUtils.isEmpty(itemList.get(i).getDqTitle()))
            dcArchiveViewHolder.testSeriesTV.setText(itemList.get(i).getDqTitle());
        else if (name.contains("(") && name.contains(")"))
            dcArchiveViewHolder.testSeriesTV.setText(name.substring(name.indexOf("(") + 1,
                    name.indexOf(")")).replace("-", " ")
                    /*String.format("DQ%s", itemList.get(i).getTestSeriesId())*/);
        else
            dcArchiveViewHolder.testSeriesTV.setText(name);

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yy");
        String dateString = formatter.format(new Date(Long.parseLong(itemList.get(i).getCreationTime()) * 1000));
        dcArchiveViewHolder.testDateTV.setText(dateString);

        dcArchiveViewHolder.rootLayout.setOnClickListener(v -> {
            if (itemList.get(i).getUserRank().isEmpty()) {
                //Toast.makeText(activity, "djhfjdh", Toast.LENGTH_SHORT).show();
                Intent quizView = new Intent(activity, TestBaseActivity.class);
                quizView.putExtra(Const.TEST_SERIES_ID, itemList.get(i).getTestSeriesId());
                quizView.putExtra(Const.STATUS, false);
                quizView.putExtra(Constants.Extras.DAILY, true);
                SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.DAILY_CHALLENGE);
                SharedPreference.getInstance().testDailyQuizSerieId(Const.DIALY_QUIZ_TEST_SERIES_ID, itemList.get(i).getTestSeriesId());
                quizView.putExtra("OPEN_FROM", Constants.TestType.DAILY_CHALLENGE);
                activity.startActivity(quizView);

                if (i == 0)
                    try {
                        new Handler().post(() -> activity.onBackPressed());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            } else {

                Intent resultScreen = new Intent(activity, QuizActivity.class);
                resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, itemList.get(i).getTestSegmentId());
                activity.startActivity(resultScreen);
            }
        });
        if (itemList.get(i).getUserRank().isEmpty()) {
            dcArchiveViewHolder.startTestIV.setVisibility(View.VISIBLE);
            dcArchiveViewHolder.resultTestIV.setVisibility(View.GONE);
            dcArchiveViewHolder.startBtn.setVisibility(View.VISIBLE);
        } else {
            dcArchiveViewHolder.resultTestIV.setVisibility(View.VISIBLE);
            dcArchiveViewHolder.startTestIV.setVisibility(View.GONE);
            dcArchiveViewHolder.startBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public static class DCArchiveViewHolder extends RecyclerView.ViewHolder {
        ImageView resultTestIV, startTestIV;
        TextView testSeriesTV, testDateTV, startBtn;
        LinearLayout rootLayout;

        public DCArchiveViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.root_layout_archieve);

            resultTestIV = itemView.findViewById(R.id.resultTestIV);
            startTestIV = itemView.findViewById(R.id.startTestIV);
            testSeriesTV = itemView.findViewById(R.id.testSeriesTV);
            testDateTV = itemView.findViewById(R.id.testDateTV);
            startBtn = itemView.findViewById(R.id.startBtn);

        }
    }
}
