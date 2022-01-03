package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.QuizActivity;
import com.emedicoz.app.modelo.TestSeriesResultData;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReattemptDialogAdapter extends RecyclerView.Adapter<ReattemptDialogAdapter.ReattemptDialogViewHolder> {
    Activity activity;
    ArrayList<TestSeriesResultData> testSeriesResultDataArrayList;
    Context context;
    int position;
    Dialog quizBasicInfoDialog;

    public ReattemptDialogAdapter(Activity activity, ArrayList<TestSeriesResultData> testSeriesResultDataArrayList, Context context, Dialog quizBasicInfoDialog) {
        this.activity = activity;
        this.context = context;
        this.testSeriesResultDataArrayList = testSeriesResultDataArrayList;
        this.quizBasicInfoDialog = quizBasicInfoDialog;
    }

    @NonNull
    @Override
    public ReattemptDialogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_dialog_reattempt, viewGroup, false);
        return new ReattemptDialogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReattemptDialogViewHolder holder, final int position) {
        this.position = position;
        holder.reattemptDialogSN.setText(String.valueOf(position + 1));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (testSeriesResultDataArrayList.get(position).getCreation_time() != null) {
            String dateString = formatter.format(new Date(Long.parseLong(testSeriesResultDataArrayList.get(position).getCreation_time())));

            String[] dateAndTime = dateString.split("\\s+");
            if (dateAndTime[0] != null)
                holder.reattemptDialogDate.setText(dateAndTime[0]);
            if (dateAndTime[1] != null)
                holder.reattemptDialogTime.setText(dateAndTime[1]);
        }

        if (testSeriesResultDataArrayList.get(position).getState() != null && !testSeriesResultDataArrayList.get(position).getState().equals("")) {
            if (testSeriesResultDataArrayList.get(position).getState().equalsIgnoreCase("0")) {
                holder.reattemptDialogDetail.setText(activity.getResources().getString(R.string.result));
            } else {
                holder.reattemptDialogDetail.setText(activity.getResources().getString(R.string.resume));
            }
        }

        holder.reattemptDialogDetail.setOnClickListener(v -> {
            TestSeriesResultData data = testSeriesResultDataArrayList.get(position);
            if (!GenericUtils.isEmpty(data.getState())) {
                if (data.getState().equalsIgnoreCase("1")) {
                    if (!GenericUtils.isEmpty(data.getTest_series_id())) {
                        Intent quizView = new Intent(activity, TestBaseActivity.class);
                        quizView.putExtra(Const.STATUS, false);
                        quizView.putExtra(Const.TEST_SERIES_ID, data.getTest_series_id());
                        quizView.putExtra(Constants.Extras.NAME, data.getTest_series_name());
                        activity.startActivity(quizView);
                    }
                } else {
                    if (data.getTest_result_date() != null && !data.getTest_result_date().equalsIgnoreCase("")) {
                        Long currentTS = System.currentTimeMillis();
                        if (currentTS < (Long.parseLong(data.getTest_result_date()) * 1000)) {
                            Intent resultScreen = new Intent(activity, QuizActivity.class);
                            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_AWAIT);
                            resultScreen.putExtra(Constants.Extras.DATE, data.getTest_result_date());
                            activity.startActivity(resultScreen);
                        } else {
                            Intent intent = new Intent(activity, QuizActivity.class);
                            intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, data.getId());
                            intent.putExtra(Const.TEST_SERIES_ID, data.getTest_series_id());
                            intent.putExtra(Constants.Extras.NAME, data.getTest_series_name());
                            intent.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                            activity.startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(activity, QuizActivity.class);
                        intent.putExtra(Const.STATUS, data.getId());
                        intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, data.getId());
                        intent.putExtra(Const.TEST_SERIES_ID, data.getTest_series_id());
                        intent.putExtra(Constants.Extras.NAME, data.getTest_series_name());
                        intent.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN);
                        activity.startActivity(intent);
                    }
                }
                quizBasicInfoDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return testSeriesResultDataArrayList.size();
    }

    public class ReattemptDialogViewHolder extends RecyclerView.ViewHolder {

        TextView reattemptDialogDate, reattemptDialogTime, reattemptDialogSN;
        TextView reattemptDialogDetail;

        public ReattemptDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            reattemptDialogDate = itemView.findViewById(R.id.reattemptDialogDate);
            reattemptDialogSN = itemView.findViewById(R.id.reattemptDialogSN);
            reattemptDialogTime = itemView.findViewById(R.id.reattemptDialogTime);
            reattemptDialogDetail = itemView.findViewById(R.id.reattemptDialogResultDetails);
        }
    }
}
