package com.emedicoz.app.testmodule.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.emedicoz.app.R;
import com.emedicoz.app.testmodule.model.TrueFalse;

import java.util.ArrayList;

public class TrueFalseBookmarkAdapter extends RecyclerView.Adapter<TrueFalseBookmarkAdapter.ViewHolder> {

    Activity activity;
    ArrayList<String> answerList;
    ArrayList<String> userAnswerList;
    ArrayList<TrueFalse> trueFalseArrayList;

    int pagerPosition;

    /*String str="";*/
    public TrueFalseBookmarkAdapter(Activity activity, ArrayList<TrueFalse> trueFalseArrayList, int pagerPosition, ArrayList<String> answerList) {
        this.activity = activity;
        this.trueFalseArrayList = trueFalseArrayList;
        this.pagerPosition = pagerPosition;
        this.answerList = answerList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_option_radio_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.textOptionRadio.setText(trueFalseArrayList.get(position).getOptionSerial() + ". " + trueFalseArrayList.get(position).getOptionText());

        if (answerList.get(position).equals("0")) {
            holder.falseRB.setChecked(true);
            holder.falseRB.setBackground(activity.getResources().getDrawable(R.drawable.bg_mcq_right_answer));

        } else {
            holder.trueRB.setChecked(true);
            holder.trueRB.setBackground(activity.getResources().getDrawable(R.drawable.bg_mcq_right_answer));

        }

    }

    @Override
    public int getItemCount() {
        return trueFalseArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textOptionRadio;
        RadioGroup myRadioGroup;
        RadioButton trueRB, falseRB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textOptionRadio = itemView.findViewById(R.id.textOptionRadio);
            myRadioGroup = itemView.findViewById(R.id.myRadioGroup);
            trueRB = itemView.findViewById(R.id.trueRB);
            falseRB = itemView.findViewById(R.id.falseRB);
        }
    }
}
