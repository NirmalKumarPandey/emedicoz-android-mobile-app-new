package com.emedicoz.app.testmodule.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.activity.VideoTestBaseActivity;
import com.emedicoz.app.testmodule.activity.ViewSolutionWithTabNew;
import com.emedicoz.app.testmodule.callback.NumberPadOnClick;
import com.emedicoz.app.testmodule.fragment.ViewSolutionWithTabFragment;
import com.emedicoz.app.testmodule.model.QuestionBank;
import com.emedicoz.app.testmodule.model.ViewSolutionResult;
import com.emedicoz.app.utilso.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyRecyclerAdapterTwo extends RecyclerView.Adapter<MyRecyclerAdapterTwo.ViewHolder> {

    private static final String TAG = "MyRecyclerAdapterTwo";
    public List<QuestionBank> questionBankList;
    public int selectedPosition;
    String type;
    ViewSolutionWithTabFragment fragment;
    ArrayList<ViewSolutionResult> viewSolutionResults;
    private List<ViewModel> items;
    private int itemLayout;
    private Activity activity;
    private NumberPadOnClick numberPadOnClick;

    public MyRecyclerAdapterTwo(List<QuestionBank> questionBankList, Activity activity, List<ViewModel> items, int itemLayout, NumberPadOnClick numberPadOnClick, String type) {
        this.items = items;
        this.itemLayout = itemLayout;
        this.activity = activity;
        this.numberPadOnClick = numberPadOnClick;
        this.questionBankList = questionBankList;
        this.type = type;
    }

    public MyRecyclerAdapterTwo(ArrayList<ViewSolutionResult> viewSolutionResults, Activity activity, int itemLayout, NumberPadOnClick numberPadOnClick, ViewSolutionWithTabFragment fragment, String type) {
        this.itemLayout = itemLayout;
        this.activity = activity;
        this.numberPadOnClick = numberPadOnClick;
        this.viewSolutionResults = viewSolutionResults;
        this.fragment = fragment;
        this.type = type;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: ");
        holder.imageIVText.setVisibility(View.VISIBLE);
        holder.text.setText(String.valueOf(position + 1));
        final int finalPosition = position;

        if (activity instanceof TestBaseActivity || activity instanceof VideoTestBaseActivity) {

            if (questionBankList.get(position).getQuestion() != null) {
                String[] text = questionBankList.get(position).getQuestion().split("<img");

                if (type.equalsIgnoreCase("1")) {
                    holder.questionTV.setVisibility(View.VISIBLE);
                    holder.questionTV.setText(HtmlCompat.fromHtml(text[0], HtmlCompat.FROM_HTML_MODE_LEGACY));
                } else {
                    holder.questionTV.setVisibility(View.GONE);
                }
            }
            holder.parentLLTestpad.setOnClickListener(v -> {

                if (activity instanceof TestBaseActivity) {
                    ((TestBaseActivity) activity).changeTextOnNextAndPrevButton();
                    ((TestBaseActivity) activity).setFIBData();
                } else if (activity instanceof VideoTestBaseActivity)
                    ((VideoTestBaseActivity) activity).checkNextButton();

                numberPadOnClick.sendOnclickInd(finalPosition);
            });

            if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_CHOICE)) {
                if (selectedPosition == finalPosition) {
                    holder.imageIVText.setImageResource(R.drawable.bg_selected_question_rect);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                } else if (questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("-1")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_skipped);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));

                } else if (questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_unanswered);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
                } else if (!questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("-1") && !questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_answered);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
                }
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SINGLE_CHOICE) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.SEQUENTIAL_ARRANGEMENTS) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.REASON_ASSERTION) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.EXTENDED_MATCHING) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_COMPLETION)) {
                if (selectedPosition == finalPosition) {
                    holder.imageIVText.setImageResource(R.drawable.bg_selected_question_rect);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                } else if (questionBankList.get(finalPosition).getAnswerPosttion() == -1) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_skipped);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));

                } else if (questionBankList.get(finalPosition).getAnswerPosttion() == 0) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_unanswered);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
                } else if (questionBankList.get(finalPosition).getAnswerPosttion() != -1 && questionBankList.get(finalPosition).getAnswerPosttion() != 0) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_answered);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
                }
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MULTIPLE_TRUE_FALSE) || questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.TRUE_FALSE)) {
                if (selectedPosition == finalPosition) {
                    holder.imageIVText.setImageResource(R.drawable.bg_selected_question_rect);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                } else if (questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("-1")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_skipped);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));

                } else if (questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_unanswered);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
                } else if (!questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("-1") && !questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_answered);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
                }
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.MATCH_THE_FOLLOWING)) {
                if (selectedPosition == finalPosition) {
                    holder.imageIVText.setImageResource(R.drawable.bg_selected_question_rect);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                } else if (questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("-1")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_skipped);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));

                } else if (questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_unanswered);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
                } else if (!questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("-1") && !questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_answered);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
                }
            } else if (questionBankList.get(position).getQuestionType().equalsIgnoreCase(Constants.QuestionType.FILL_IN_THE_BLANKS)) {
                Log.e(TAG, "finalPos:-" + finalPosition + "valueOnPos:-" + questionBankList.get(finalPosition).getMultipleAnswerPosition());
                if (selectedPosition == finalPosition) {
                    holder.imageIVText.setImageResource(R.drawable.bg_selected_question_rect);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                } else if (questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("-1") || questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_skipped);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));

                } else if (questionBankList.get(finalPosition).getMultipleAnswerPosition().equalsIgnoreCase("0")) {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_unanswered);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
                } else {
                    holder.imageIVText.setImageResource(R.drawable.rectangle_answered);
                    holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
                }
            }
            if (questionBankList.get(finalPosition).isMarkForReview()) {
                holder.imageIVText.setImageResource(R.drawable.rectangle_marked);
                holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
            }
        } else {

            if (viewSolutionResults.get(position).getQuestion() != null) {
                String[] text = viewSolutionResults.get(position).getQuestion().split("<img");

                if (type.equalsIgnoreCase("1")) {
                    holder.questionTV.setVisibility(View.VISIBLE);
                    holder.questionTV.setText(HtmlCompat.fromHtml(text[0], HtmlCompat.FROM_HTML_MODE_LEGACY));
                } else {
                    holder.questionTV.setVisibility(View.GONE);
                }
            }

            holder.imageIVText.setOnClickListener(v -> numberPadOnClick.sendOnclickInd(finalPosition));

            String[] userAnswer = viewSolutionResults.get(position).getUserAnswer().split(",");
            ArrayList<String> userAnswerList = new ArrayList<>(Arrays.asList(userAnswer));
            Collections.sort(userAnswerList);
            StringBuilder strUserAnswer = new StringBuilder();
            for (int j = 0; j < userAnswerList.size(); j++) {
                strUserAnswer.append(userAnswerList.get(j)).append(",");
            }
            if (strUserAnswer.toString().endsWith(",")) {
                strUserAnswer = new StringBuilder(strUserAnswer.substring(0, strUserAnswer.length() - 1));
            }


            if (viewSolutionResults.get(position).Is_selected()) {
                holder.imageIVText.setImageResource(R.drawable.bg_selected_question_rect);
                holder.text.setTextColor(ContextCompat.getColor(activity, R.color.black));
            } else if (viewSolutionResults.get(position).getUserAnswer().equals("")) {
                holder.imageIVText.setImageResource(R.drawable.rectangle_skipped);
                holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
            } else if (strUserAnswer.toString().equalsIgnoreCase(viewSolutionResults.get(position).getAnswer())) {
                holder.imageIVText.setImageResource(R.drawable.rectangle_answered);
                holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
            } else {
                holder.imageIVText.setImageResource(R.drawable.rectangle_unanswered);
                holder.text.setTextColor(ContextCompat.getColor(activity, R.color.white));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (activity instanceof TestBaseActivity || activity instanceof VideoTestBaseActivity)
            return questionBankList.size();
        else
            return viewSolutionResults.size();
    }

    public void setSelectePosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        if (activity instanceof ViewSolutionWithTabNew) {
            for (int i = 0; i < viewSolutionResults.size(); i++) {
                if (i == selectedPosition) {
                    viewSolutionResults.get(selectedPosition).set_selected(true);
                } else {
                    viewSolutionResults.get(i).set_selected(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getselectePosition() {
        return selectedPosition;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageIVText;
        public TextView text, questionTV;
        LinearLayout parentLLTestpad;
        private RelativeLayout rlselected;

        public ViewHolder(View itemView) {
            super(itemView);
            imageIVText = itemView.findViewById(R.id.imageIVText);
            text = itemView.findViewById(R.id.myImageViewText);
            rlselected = itemView.findViewById(R.id.rl_selected);
            questionTV = itemView.findViewById(R.id.questionTV);
            parentLLTestpad = itemView.findViewById(R.id.parentLLTestpad);
        }
    }
}
