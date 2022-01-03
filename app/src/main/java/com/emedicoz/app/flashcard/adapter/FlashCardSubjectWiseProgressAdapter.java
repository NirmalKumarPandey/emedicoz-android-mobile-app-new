package com.emedicoz.app.flashcard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.model.myprogress.SubjectWiseType;
import com.emedicoz.app.flashcard.model.myprogress.Subjectwise;

import java.util.List;

public class FlashCardSubjectWiseProgressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<SubjectWiseType> subjectWiseTypeArrayList;
    int subjectTitle = 0;


    public FlashCardSubjectWiseProgressAdapter(List<SubjectWiseType> subjectWiseTypeArrayList, Context context) {
        this.context = context;
        this.subjectWiseTypeArrayList = subjectWiseTypeArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == subjectTitle) {
            view = LayoutInflater.from(context).inflate(R.layout.subjectwise_title, viewGroup, false);
            return new SubjectWistTitle(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.subjectwise_item, viewGroup, false);
            return new SubjectWiseHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof SubjectWistTitle) {
            ((SubjectWistTitle) viewHolder).tvTitle.setText(subjectWiseTypeArrayList.get(i).getTitle());
        } else {
            Subjectwise subjectwise = subjectWiseTypeArrayList.get(i).getSubjectwise();
            ((SubjectWiseHolder) viewHolder).tvTitle.setText(subjectwise.getTitle());
            ((SubjectWiseHolder) viewHolder).readCardTV1.setText(subjectwise.getProgress().get(6).getReadCard().toString());
            ((SubjectWiseHolder) viewHolder).readCardTV2.setText(subjectwise.getProgress().get(5).getReadCard().toString());
            ((SubjectWiseHolder) viewHolder).readCardTV3.setText(subjectwise.getProgress().get(4).getReadCard().toString());
            ((SubjectWiseHolder) viewHolder).readCardTV4.setText(subjectwise.getProgress().get(3).getReadCard().toString());
            ((SubjectWiseHolder) viewHolder).readCardTV5.setText(subjectwise.getProgress().get(2).getReadCard().toString());
            ((SubjectWiseHolder) viewHolder).readCardTV6.setText(subjectwise.getProgress().get(1).getReadCard().toString());
            ((SubjectWiseHolder) viewHolder).readCardTV7.setText(subjectwise.getProgress().get(6).getReadCard().toString());
        }
    }

    @Override
    public int getItemCount() {
        return subjectWiseTypeArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (subjectWiseTypeArrayList.get(position).getType() == subjectTitle)
            return 0;
        else
            return 1;
    }

    static class SubjectWiseHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView readCardTV1;
        TextView readCardTV2;
        TextView readCardTV3;
        TextView readCardTV4;
        TextView readCardTV5;
        TextView readCardTV6;
        TextView readCardTV7;

        public SubjectWiseHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            readCardTV1 = itemView.findViewById(R.id.tv_1);
            readCardTV2 = itemView.findViewById(R.id.tv_2);
            readCardTV3 = itemView.findViewById(R.id.tv_3);
            readCardTV4 = itemView.findViewById(R.id.tv_4);
            readCardTV5 = itemView.findViewById(R.id.tv_5);
            readCardTV6 = itemView.findViewById(R.id.tv_6);
            readCardTV7 = itemView.findViewById(R.id.tv_7);
        }
    }

    static class SubjectWistTitle extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public SubjectWistTitle(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

}
