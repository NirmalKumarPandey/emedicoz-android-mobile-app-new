package com.emedicoz.app.flashcard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.model.myprogress.SsubjectWiseCard.Subdeck;
import com.emedicoz.app.flashcard.model.myprogress.SubjectWiseCard;

import java.util.List;

public class SubjectCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    int subjectTitle = 0;
    List<SubjectWiseCard> subjectWiseCardArrayList;

    public SubjectCardAdapter(Context context, List<SubjectWiseCard> subjectWiseCardArrayList) {
        this.context = context;
        this.subjectWiseCardArrayList = subjectWiseCardArrayList;
    }

    @Override
    public int getItemCount() {
        return subjectWiseCardArrayList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == subjectTitle) {
            view = LayoutInflater.from(context).inflate(R.layout.subjectwise_title, viewGroup, false);
            return new TitleViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.subjectwisecard_item, viewGroup, false);
            return new CardViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof TitleViewHolder) {
            ((TitleViewHolder) viewHolder).tvTitle.setText(subjectWiseCardArrayList.get(i).getDeck());
        } else {
            Subdeck subdeck = subjectWiseCardArrayList.get(i).getSubdeck();
            ((CardViewHolder) viewHolder).tvTitle.setText(subdeck.getTitle());
            ((CardViewHolder) viewHolder).totalCountTV.setText(subdeck.getTotal().toString());
            ((CardViewHolder) viewHolder).readCountTV.setText(subdeck.getRead().toString());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (subjectWiseCardArrayList.get(position).getView_type() == subjectTitle)
            return 0;
        else
            return 1;
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView totalCountTV;
        TextView readCountTV;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            totalCountTV = itemView.findViewById(R.id.tv_1);
            readCountTV = itemView.findViewById(R.id.tv_2);
        }
    }
}
