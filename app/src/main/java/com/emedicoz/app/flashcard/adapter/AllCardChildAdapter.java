package com.emedicoz.app.flashcard.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.CircularTextView;
import com.emedicoz.app.flashcard.activity.ViewFlashCardActivity;
import com.emedicoz.app.flashcard.model.FlashCards;

import java.util.List;

public class AllCardChildAdapter extends RecyclerView.Adapter<AllCardChildAdapter.ChildViewHolder> {

    Context context;
    List<FlashCards> flashCardsArrayList;
    private final String TAG = "AllCardChildAdapter";

    public AllCardChildAdapter(Context context, List<FlashCards> flashCardsArrayList) {
        this.context = context;
        this.flashCardsArrayList = flashCardsArrayList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_card_child_adapter_item, viewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder childViewHolder, int i) {
        Log.e(TAG, "onBindViewHolder: "+flashCardsArrayList.size() );
        if (flashCardsArrayList.get(i).isNewPosition()) {
            childViewHolder.ctvNumber.setSolidColor(ContextCompat.getColor(context,R.color.green_new));
            childViewHolder.ctvNumber.setStrokeColor(ContextCompat.getColor(context,R.color.green));
            childViewHolder.ctvNumber.setStrokeWidth(3);
        } else if (flashCardsArrayList.get(i).isComplete()) {
            childViewHolder.ctvNumber.setSolidColor(ContextCompat.getColor(context,R.color.green_new));
        } else if (flashCardsArrayList.get(i).isAlreadyComplete()) {
            childViewHolder.ctvNumber.setSolidColor(ContextCompat.getColor(context,R.color.light_green));
            childViewHolder.ctvNumber.setTextColor(ContextCompat.getColor(context,R.color.black_overlay));
        } else if (flashCardsArrayList.get(i).isSelected()) {
            childViewHolder.ctvNumber.setSolidColor(ContextCompat.getColor(context,R.color.white));
            childViewHolder.ctvNumber.setTextColor(ContextCompat.getColor(context,R.color.black));
            childViewHolder.ctvNumber.setStrokeColor(ContextCompat.getColor(context,R.color.blue));
            childViewHolder.ctvNumber.setStrokeWidth(1);
        } else {
            childViewHolder.ctvNumber.setSolidColor(ContextCompat.getColor(context,R.color.colorPrimaryNight));
        }

        childViewHolder.ctvNumber.setText("" + (i + 1));
    }

    @Override
    public int getItemCount() {
        return flashCardsArrayList.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircularTextView ctvNumber;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

            ctvNumber = itemView.findViewById(R.id.ctv_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.e(TAG, "onClick: " + position);
            if (position >= 0) {
                ((ViewFlashCardActivity) context).moveToPosition(flashCardsArrayList.get(getAdapterPosition()).getId());
//                for (int i = 0; i < flashCardsArrayList.size(); i++) {
//                    if (position == i) {
//                        if (flashCardsArrayList.get(i).isSelected()) {
//                            flashCardsArrayList.get(i).setSelected(false);
//                        } else {
//                            flashCardsArrayList.get(i).setSelected(true);
//                        }
//                    } else {
//                        flashCardsArrayList.get(i).setSelected(false);
//                    }
//                }
//
//                notifyDataSetChanged();
            }
        }
    }

    public void notifiedAdapter() {

    }
}
