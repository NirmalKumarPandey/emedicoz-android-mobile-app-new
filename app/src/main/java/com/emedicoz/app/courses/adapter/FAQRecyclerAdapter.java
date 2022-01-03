package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.liveclass.courses.DescriptionFAQ;

import java.util.List;

public class FAQRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    List<DescriptionFAQ> descriptionFAQList;

    public FAQRecyclerAdapter(Activity activity, List<DescriptionFAQ> faq) {
        this.activity = activity;
        this.descriptionFAQList = faq;
    }

    public void updateList(List<DescriptionFAQ> faq){
        this.descriptionFAQList = faq;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_description_faq_item, null);
        return new FAQQuestionHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof FAQQuestionHolder) {
            ((FAQQuestionHolder) viewHolder).faqQuestionPosition.setText(String.valueOf(position + 1));
            ((FAQQuestionHolder) viewHolder).faqQuestion.setText(descriptionFAQList.get(position).getQuestion());
            ((FAQQuestionHolder) viewHolder).answeredTV.setText(descriptionFAQList.get(position).getDescription());

            if (!descriptionFAQList.get(position).isExpanded()) {
                ((FAQQuestionHolder) viewHolder).image.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                ((FAQQuestionHolder) viewHolder).descriptionLayout.setVisibility(View.GONE);
            } else {
                ((FAQQuestionHolder) viewHolder).image.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                ((FAQQuestionHolder) viewHolder).descriptionLayout.setVisibility(View.VISIBLE);
            }

            ((FAQQuestionHolder) viewHolder).questionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (descriptionFAQList.get(position).isExpanded()) {
                        descriptionFAQList.get(position).setExpanded(false);
                    } else {
                        descriptionFAQList.get(position).setExpanded(true);
                    }
                    notifyDataSetChanged();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return descriptionFAQList != null ? descriptionFAQList.size() : 0;
    }

    public class FAQQuestionHolder extends RecyclerView.ViewHolder {
        TextView faqQuestion;
        TextView faqQuestionPosition;
        LinearLayout questionLayout;
        ImageView image;
        TextView answeredTV;
        LinearLayout descriptionLayout;

        public FAQQuestionHolder(@NonNull View itemView) {
            super(itemView);
            faqQuestion = itemView.findViewById(R.id.question);
            image = itemView.findViewById(R.id.image_expanded);
            answeredTV = itemView.findViewById(R.id.description);
            descriptionLayout = itemView.findViewById(R.id.description_layout);
            faqQuestionPosition = itemView.findViewById(R.id.question_position);
            questionLayout = itemView.findViewById(R.id.questionLayout);
        }

    }

}
