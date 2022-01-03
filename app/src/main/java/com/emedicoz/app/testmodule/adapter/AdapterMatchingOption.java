package com.emedicoz.app.testmodule.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emedicoz.app.R;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.model.Social;

import java.util.ArrayList;

public class AdapterMatchingOption extends RecyclerView.Adapter<AdapterMatchingOption.ViewHolder> {
    Activity activity;
    int row_index = -1;
    boolean clicked = false;
    int leftItemPosition, leftItemSelectedPosition, selectedPos;
    ArrayList<String> leftItemList = new ArrayList<>();
    ArrayList<String> rightItemList = new ArrayList<>();
    String answer = "";
    int pagerPosition;
    private ArrayList<Social> items;
    private ArrayList<Social> leftItems;

    public AdapterMatchingOption(Activity activity, ArrayList<Social> items, ArrayList<Social> leftItems, int leftItemPosition, int pagerPosition) {
        this.activity = activity;
        this.items = items;
        this.leftItems = leftItems;
        this.leftItemPosition = leftItemPosition;
        this.pagerPosition = pagerPosition;
        Log.e("AdapterMatchingOption: ", "From Constructor");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Log.e("onCreateViewHolder: ", "onCreateViewHolder is called : " + pagerPosition);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.e("POSITION", String.valueOf(position));
        Log.e("onBindViewHolder: ", "onBind is called " + pagerPosition);
        holder.text.setText(items.get(position).getOption());

        if (((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer != null && !((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.isEmpty()) {
            for (int i = 0; i < ((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.size(); i++) {
                leftItemList.add(String.valueOf(((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.get(i).charAt(0)));
                rightItemList.add(String.valueOf(((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.get(i).charAt(1)));
            }
        }

        if (((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer != null && !((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.isEmpty()) {
            for (int i = 0; i < leftItemList.size(); i++) {
                if (leftItemList.get(i).equalsIgnoreCase(leftItems.get(leftItemPosition).getOption())) {
                    leftItemSelectedPosition = leftItemPosition;
                    selectedPos = i;
                }
            }

            if (leftItemPosition == leftItemSelectedPosition) {
                if (rightItemList.get(selectedPos).equals(items.get(position).getOption())) {
                    holder.llAdapter1.setSelected(true);
                    clicked = true;
                    row_index = position;
                }
            }
        }

        holder.llAdapter1.setOnClickListener(v -> {
                if (!clicked) {
                    holder.llAdapter1.setSelected(true);
                    answer = answer + leftItems.get(leftItemPosition).getOption() + items.get(position).getOption();
                    ((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.add(answer);
                    clicked = true;
                } else {
                    answer = "";
                    answer = answer + leftItems.get(leftItemPosition).getOption() + items.get(position).getOption();
                    for (int i = 0; i < ((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.size(); i++) {
                        if (String.valueOf(((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.get(i).charAt(0)).equalsIgnoreCase(leftItems.get(leftItemPosition).getOption())) {
                            ((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.set(i, answer);
                        }
                    }
                    row_index = position;
                    notifyDataSetChanged();
                }

                ((TestBaseActivity) activity).questionBankList.get(pagerPosition).setAnswers(((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer);

                String answerPosition = "";
                for (int i = 0; i < ((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.size(); i++) {
                    answerPosition = answerPosition + ((TestBaseActivity) activity).questionBankList.get(pagerPosition).mtfAnswer.get(i) + ",";
                }
                ((TestBaseActivity) activity).questionBankList.get(pagerPosition).setIsMultipleAnswer(true, answerPosition);
        });

        if (row_index == position) {
            holder.llAdapter1.setSelected(true);
        } else {
            holder.llAdapter1.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public LinearLayout llAdapter1;


        public ViewHolder(View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.optionIconTV);
            llAdapter1 = itemView.findViewById(R.id.llAdapter1);

        }
    }
}
