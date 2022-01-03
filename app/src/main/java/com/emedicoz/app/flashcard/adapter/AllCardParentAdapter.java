package com.emedicoz.app.flashcard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.model.AllFlashCard;
import com.emedicoz.app.utilso.GridSpacingItemDecoration;

import java.util.List;

public class AllCardParentAdapter extends RecyclerView.Adapter<AllCardParentAdapter.ParentViewHolder> {
    private static final String TAG = "AllCardParentAdapter";

    Context context;
    List<AllFlashCard> allFlashCardArrayList;

    public AllCardParentAdapter(Context context, List<AllFlashCard> allFlashCardArrayList) {
        this.context = context;
        this.allFlashCardArrayList = allFlashCardArrayList;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_card_parent_adapter_item, viewGroup, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder parentViewHolder, int i) {
        Log.e(TAG, "onBindViewHolder: " + allFlashCardArrayList.get(i).getFlashCardsArrayList().size());
        parentViewHolder.tvTitle.setText(allFlashCardArrayList.get(i).getTitle());
        parentViewHolder.rvAllCardChild.setAdapter(new AllCardChildAdapter(context, allFlashCardArrayList.get(i).getFlashCardsArrayList()));
    }

    @Override
    public int getItemCount() {
        return allFlashCardArrayList.size();
    }

    class ParentViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rvAllCardChild;
        TextView tvTitle;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            rvAllCardChild = itemView.findViewById(R.id.rv_all_card_child);
            rvAllCardChild.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
            rvAllCardChild.addItemDecoration(new GridSpacingItemDecoration(4, 25, true));
            ViewCompat.setNestedScrollingEnabled(rvAllCardChild, false);
            rvAllCardChild.setVisibility(View.VISIBLE);

        }
    }
}