package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.SubjectWiseResultSubject;

import java.util.ArrayList;

public class SubjectWiseResultAdapter extends RecyclerView.Adapter<SubjectWiseResultAdapter.SubjectWiseResultViewHolder> {

    Activity activity;
    ArrayList<SubjectWiseResultSubject> subjectWiseResults;

    public SubjectWiseResultAdapter(Activity activity, ArrayList<SubjectWiseResultSubject> subjectWiseResults) {
        this.activity = activity;
        this.subjectWiseResults = subjectWiseResults;
    }

    @NonNull
    @Override
    public SubjectWiseResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_row_subject_wise_result, viewGroup, false);
        return new SubjectWiseResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubjectWiseResultViewHolder holder, int position) {

        SubjectWiseResultSubject subject = subjectWiseResults.get(position);
        if (subjectWiseResults.get(position).getName() != null)
            holder.subject.setText(subjectWiseResults.get(position).getName());
        if (subjectWiseResults.get(position).getPercentge() != null)
            holder.percentage.setText(subjectWiseResults.get(position).getPercentge() + " %");

        if (subjectWiseResults.get(position).getTime() != null) {
            holder.time.setText(subjectWiseResults.get(position).getTime());
            holder.time.setVisibility(View.VISIBLE);
        } else {
            holder.time.setVisibility(View.GONE);
        }
        holder.right.setText(subject.getRight());
        holder.wrong.setText(subject.getWrong());
        holder.guess.setText(subject.getGuess());
        holder.topperPercentage.setText(subject.getTopperPercentge() + " %");
        holder.topperTime.setText(subject.getTopperTime());
        holder.score.setText(subject.getScore());
        holder.skipped.setText(subject.getSkipped());

        holder.cardSub.setOnClickListener(view -> {
            if (holder.detailLL.getVisibility() == View.GONE) {
                holder.detailLL.setVisibility(View.VISIBLE);
                holder.dropdown.setImageResource(R.mipmap.result_show_more_up);
            } else {
                holder.detailLL.setVisibility(View.GONE);
                holder.dropdown.setImageResource(R.mipmap.result_show_more_down);
            }
        });

    }

    @Override
    public int getItemCount() {
        return subjectWiseResults.size();
    }

    public class SubjectWiseResultViewHolder extends RecyclerView.ViewHolder {

        TextView subject, percentage, time, right, wrong, score, guess, skipped, topperTime, topperPercentage;

        ImageView dropdown;
        LinearLayout detailLL;
        CardView cardSub;

        public SubjectWiseResultViewHolder(@NonNull View itemView) {
            super(itemView);

            subject = itemView.findViewById(R.id.viewSubject);
            percentage = itemView.findViewById(R.id.viewPercentage);
            time = itemView.findViewById(R.id.viewTime);
            right = itemView.findViewById(R.id.rightTV);
            wrong = itemView.findViewById(R.id.wrongTV);
            score = itemView.findViewById(R.id.scoreTV);
            guess = itemView.findViewById(R.id.guessTV);
            skipped = itemView.findViewById(R.id.skippedTV);
            topperTime = itemView.findViewById(R.id.topperTimeTV);
            topperPercentage = itemView.findViewById(R.id.topperPercentageTV);
            dropdown = itemView.findViewById(R.id.dropdown);
            detailLL = itemView.findViewById(R.id.detailLL);
            cardSub = itemView.findViewById(R.id.cardSub);
        }
    }
}
