package com.emedicoz.app.testmodule.adapter;

import android.app.Activity;
import androidx.lifecycle.ViewModel;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emedicoz.app.R;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.callback.NumberPadOnClick;
import com.emedicoz.app.testmodule.fragment.ViewSolutionWithTabFragment;
import com.emedicoz.app.testmodule.model.QuestionBank;
import com.emedicoz.app.testmodule.model.QuestionDump;
import com.emedicoz.app.testmodule.model.ViewSolutionResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    public List<QuestionBank> questionBankList;
    public List<QuestionDump> questionDumpList;
    String type;
    ArrayList<ViewSolutionResult> viewSolutionResults;
    ViewSolutionWithTabFragment fragment;
    private int itemLayout;
    private Activity activity;
    private NumberPadOnClick numberPadOnClick;
    private int selectedPosition;

    public MyRecyclerAdapter(List<QuestionDump> questionDumpList, List<QuestionBank> questionBankList, Activity activity, List<ViewModel> items, int itemLayout, NumberPadOnClick numberPadOnClick) {
        this.itemLayout = itemLayout;
        this.activity = activity;
        this.numberPadOnClick = numberPadOnClick;
        this.questionBankList = questionBankList;
        this.questionDumpList = questionDumpList;
    }

    public MyRecyclerAdapter(ArrayList<ViewSolutionResult> viewSolutionResults, Activity activity, int itemLayout, NumberPadOnClick numberPadOnClick) {
        this.itemLayout = itemLayout;
        this.activity = activity;
        this.numberPadOnClick = numberPadOnClick;
        this.viewSolutionResults = viewSolutionResults;

    }

    public MyRecyclerAdapter(ArrayList<ViewSolutionResult> viewSolutionResults, Activity activity, int itemLayout, NumberPadOnClick numberPadOnClick, ViewSolutionWithTabFragment fragment) {
        this.itemLayout = itemLayout;
        this.activity = activity;
        this.numberPadOnClick = numberPadOnClick;
        this.viewSolutionResults = viewSolutionResults;
        this.fragment = fragment;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageIVText.setVisibility(View.VISIBLE);
        holder.text.setText(String.valueOf(position + 1));
        final int finalPosition = position;

        if (activity instanceof TestBaseActivity) {
            holder.imageIVText.setOnClickListener(v -> {

                    numberPadOnClick.sendOnclickInd(finalPosition);
                    ((TestBaseActivity) activity).changeTextOnNextAndPrevButton();
            });

            if (selectedPosition == finalPosition) {
                holder.imageIVText.setImageResource(R.drawable.bg_selected_question);
                holder.text.setTextColor(ContextCompat.getColor(activity,R.color.blue));
            } else if (questionBankList.get(finalPosition).getAnswerPosttion() == -1) {
                holder.imageIVText.setImageResource(R.drawable.circle_skip);
                holder.text.setTextColor(ContextCompat.getColor(activity,R.color.white));

            } else if (questionBankList.get(finalPosition).getAnswerPosttion() == 0) {
                holder.imageIVText.setImageResource(R.drawable.circle_unanswered);
                holder.text.setTextColor(ContextCompat.getColor(activity,R.color.white));
            } else if (questionBankList.get(finalPosition).getAnswerPosttion() != -1 && questionBankList.get(finalPosition).getAnswerPosttion() != -0) {
                holder.imageIVText.setImageResource(R.drawable.circle_answered);
                holder.text.setTextColor(ContextCompat.getColor(activity,R.color.white));
            }

            if (questionBankList.get(finalPosition).isMarkForReview()) {
                holder.rlselected.setVisibility(View.VISIBLE);
            } else {
                holder.rlselected.setVisibility(View.GONE);
            }
        } else {
            holder.imageIVText.setOnClickListener(v -> numberPadOnClick.sendOnclickInd(finalPosition));

            String[] userAnswer = viewSolutionResults.get(position).getUserAnswer().split(",");
            ArrayList<String> userAnswerList = new ArrayList<>(Arrays.asList(userAnswer));
            Collections.sort(userAnswerList);
            String strUserAnswer = "";
            for (int j = 0; j < userAnswerList.size(); j++) {
                strUserAnswer = strUserAnswer + userAnswerList.get(j) + ",";
            }
            if (strUserAnswer.endsWith(",")) {
                strUserAnswer = strUserAnswer.substring(0, strUserAnswer.length() - 1);
            }

            if (selectedPosition == finalPosition) {
                holder.imageIVText.setImageResource(R.drawable.bg_selected_question);
                holder.text.setTextColor(ContextCompat.getColor(activity,R.color.black));
            } else if (viewSolutionResults.get(position).getUserAnswer().equals("")) {
                holder.imageIVText.setImageResource(R.drawable.circle_skip);
                holder.text.setTextColor(ContextCompat.getColor(activity,R.color.white));
            } else if (strUserAnswer.equalsIgnoreCase(viewSolutionResults.get(position).getAnswer())) {
                holder.imageIVText.setImageResource(R.drawable.circle_answered);
                holder.text.setTextColor(ContextCompat.getColor(activity,R.color.white));
            } else {
                holder.imageIVText.setImageResource(R.drawable.circle_unanswered);
                holder.text.setTextColor(ContextCompat.getColor(activity,R.color.white));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (activity instanceof TestBaseActivity)
            return questionBankList.size();
        else
            return viewSolutionResults.size();
    }

    public void setSelectePosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public int getselectePosition() {
        return selectedPosition;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageIVText;
        public TextView text, questionTV;
        private RelativeLayout rlselected;

        public ViewHolder(View itemView) {
            super(itemView);
            imageIVText = itemView.findViewById(R.id.imageIVText);
            text = itemView.findViewById(R.id.myImageViewText);
            rlselected = itemView.findViewById(R.id.rl_selected);
            questionTV = itemView.findViewById(R.id.questionTV);
        }
    }
}
