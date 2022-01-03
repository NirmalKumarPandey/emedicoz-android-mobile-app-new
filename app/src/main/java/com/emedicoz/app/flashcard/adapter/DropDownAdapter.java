package com.emedicoz.app.flashcard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.CustomItemSelectedListener;
import com.emedicoz.app.flashcard.fragment.FlashCardReviewFragment;
import com.emedicoz.app.flashcard.model.Topic;

import java.util.ArrayList;
import java.util.List;

public class DropDownAdapter extends RecyclerView.Adapter<DropDownAdapter.DropDownHolder> {

    private Context context;
    private List<Topic> topicList;
    private CustomItemSelectedListener customItemSelectedListener;
    Fragment fragment;

    public DropDownAdapter(Context context, List<Topic> topicList, CustomItemSelectedListener customItemSelectedListener) {
        this.context = context;
        this.topicList = topicList;
        this.customItemSelectedListener = customItemSelectedListener;
    }

    public DropDownAdapter(Context context, String type, ArrayList<Topic> topicList, Fragment fragment) {
        this.context = context;
        this.topicList = topicList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public DropDownHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinadapt, viewGroup, false);
        return new DropDownHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DropDownHolder dropDownHolder, final int i) {
        final Topic topic = topicList.get(i);
        dropDownHolder.weekOfDay.setText(topic.getTitle());
        dropDownHolder.itemView.setOnClickListener(view -> {

            if (customItemSelectedListener != null) {
                customItemSelectedListener.onItemSelected(topicList.get(i).getTitle(), topicList.get(i).getId());
            } else if (fragment instanceof FlashCardReviewFragment) {
                ((FlashCardReviewFragment) fragment).setDataForCard(topicList.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    static class DropDownHolder extends RecyclerView.ViewHolder {

        TextView weekOfDay;

        public DropDownHolder(@NonNull View itemView) {
            super(itemView);

            weekOfDay = itemView.findViewById(R.id.weekofday);
        }
    }

}
