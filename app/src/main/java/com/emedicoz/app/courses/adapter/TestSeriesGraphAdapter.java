package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.customviews.CircularTextView;
import com.emedicoz.app.modelo.courses.TestSeries;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;

import java.text.DecimalFormat;
import java.util.List;

public class TestSeriesGraphAdapter extends RecyclerView.Adapter<TestSeriesGraphAdapter.TestSeriesGraphViewHolder> {

    Activity activity;
    List<TestSeries> testSeriesList;
    private static final String TAG = "TestSeriesGraphAdapter";

    public TestSeriesGraphAdapter(Activity activity, List<TestSeries> testSeriesList) {
        this.activity = activity;
        this.testSeriesList = testSeriesList;
    }

    @NonNull
    @Override
    public TestSeriesGraphViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.test_series_graph_item, viewGroup, false);
        return new TestSeriesGraphViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestSeriesGraphViewHolder holder, int i) {
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        if (i == 0) {
            holder.percentageLL.setVisibility(View.VISIBLE);
        } else {
            holder.percentageLL.setVisibility(View.GONE);
        }
        holder.tv1.setSolidColor(ContextCompat.getColor(activity, R.color.white));
        holder.testName.setText(testSeriesList.get(i).getTest_series_name());
        holder.progressGraph.setMax(100);
        holder.progressGraph.setProgress((int) Double.parseDouble(testSeriesList.get(i).getPercentage()));
        holder.percentage.setText(decimalFormat.format(Double.parseDouble(testSeriesList.get(i).getPercentage())) + "%");

        Log.e(TAG, "onBindViewHolder: " + testSeriesList.get(i).getPercentage());

        holder.testName.setOnClickListener(v -> {
            Log.e(TAG, "onClick: " + testSeriesList.get(i).getIs_user_attemp());
            if (!TextUtils.isEmpty(testSeriesList.get(i).getIs_user_attemp())) {
                String testSegmentId = testSeriesList.get(i).getIs_user_attemp(); //segment_id

                Intent resultScreen = new Intent(activity, QuizActivity.class);
                resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                resultScreen.putExtra(Constants.Extras.NAME, testSeriesList.get(i).getTest_series_name());
                resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId);
                activity.startActivity(resultScreen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return testSeriesList.size();
    }

    class TestSeriesGraphViewHolder extends RecyclerView.ViewHolder {

        TextView percentage;
        ProgressBar progressGraph;
        CircularTextView tv1;
        TextView testName;
        LinearLayout percentageLL;

        public TestSeriesGraphViewHolder(@NonNull View itemView) {
            super(itemView);
            testName = itemView.findViewById(R.id.testName);
            percentage = itemView.findViewById(R.id.percentage);
            progressGraph = itemView.findViewById(R.id.progressGraph);
            percentageLL = itemView.findViewById(R.id.ll_percentage);
            tv1 = itemView.findViewById(R.id.tv1);
        }
    }
}
