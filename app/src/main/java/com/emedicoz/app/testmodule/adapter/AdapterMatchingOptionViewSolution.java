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
import com.emedicoz.app.testmodule.model.Social;

import java.util.ArrayList;

public class AdapterMatchingOptionViewSolution extends RecyclerView.Adapter<AdapterMatchingOptionViewSolution.ViewHolder> {
    Activity activity;
    int row_index = -1;
    boolean clicked = false;
    int leftItemPosition;
    ArrayList<String> answerList;
    ArrayList<String> userAnswerList;
    int pagerPosition;
    private ArrayList<Social> items;
    private ArrayList<Social> leftItems;

    public AdapterMatchingOptionViewSolution(Activity activity, ArrayList<Social> items, ArrayList<Social> leftItems, int leftItemPosition, int pagerPosition, ArrayList<String> answerList, ArrayList<String> userAnswerList) {
        this.activity = activity;
        this.items = items;
        this.leftItems = leftItems;
        this.leftItemPosition = leftItemPosition;
        this.pagerPosition = pagerPosition;
        this.answerList = answerList;
        this.userAnswerList = userAnswerList;
        Log.e("AdapterMatchingOption: ", "From Constructor");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Log.e("onCreateViewHolder: ", "onCreateViewHolder is called : " + String.valueOf(pagerPosition));
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.e("onBindViewHolder: ", "onBind is called " + String.valueOf(pagerPosition));
        holder.text.setText(items.get(position).getOption());

        if (userAnswerList.get(leftItemPosition).equals("-")) {
            if (String.valueOf(answerList.get(leftItemPosition).charAt(0)).equals(leftItems.get(leftItemPosition).getOption())) {
                if (String.valueOf(answerList.get(leftItemPosition).charAt(1)).equals(items.get(position).getOption())) {
                    holder.llAdapter1.setSelected(true);
                }
            }
        } else {
            if (String.valueOf(answerList.get(leftItemPosition).charAt(0)).equals(leftItems.get(leftItemPosition).getOption())) {
                if (String.valueOf(answerList.get(leftItemPosition).charAt(1)).equals(String.valueOf(userAnswerList.get(leftItemPosition).charAt(1)))) {
                    if (String.valueOf(answerList.get(leftItemPosition).charAt(1)).equals(items.get(position).getOption())) {
                        holder.llAdapter1.setSelected(true);
                    }
                } else {
                    if (String.valueOf(answerList.get(leftItemPosition).charAt(1)).equals(items.get(position).getOption())) {
                        holder.llAdapter1.setSelected(true);
                    } else if (String.valueOf(userAnswerList.get(leftItemPosition).charAt(1)).equals(items.get(position).getOption())) {
                        holder.llAdapter1.setBackground(activity.getResources().getDrawable(R.drawable.bg_mcq_wrong_answer));
                    }
                }
            }
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
