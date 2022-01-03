package com.emedicoz.app.flashcard.adapter;

import android.animation.LayoutTransition;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.fragment.FlashCardReviewFragment;
import com.emedicoz.app.flashcard.model.MainDeck;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;

import java.util.List;

public class ReviewParentAdapter extends RecyclerView.Adapter<ReviewParentAdapter.ReviewParentHolder> {

    Context context;
    List<MainDeck> mainDeckArrayList;
    FlashCardReviewFragment flashCardReviewFragment;

    public ReviewParentAdapter(Context context, List<MainDeck> mainDeckArrayList, FlashCardReviewFragment flashCardReviewFragment) {
        this.context = context;
        this.mainDeckArrayList = mainDeckArrayList;
        this.flashCardReviewFragment = flashCardReviewFragment;
    }

    @NonNull
    @Override
    public ReviewParentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.flashcard_review_parent_adapter_item, viewGroup, false);
        return new ReviewParentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewParentHolder reviewHolder, int i) {
        MainDeck mainDeck = mainDeckArrayList.get(i);
        reviewHolder.tvTitle.setText(mainDeck.getTitle());
        reviewHolder.tvStudiedCount.setText(String.format("%s/%s %s", mainDeck.getReadCards(), mainDeck.getTotalCard(), context.getString(R.string.studied)));
        reviewHolder.tvCountLeft.setText(String.format("%d left", Integer.parseInt(mainDeck.getTotalCard()) - Integer.parseInt(mainDeck.getReadCards())));

        if (mainDeck.getLeft_cards().equals("0") || mainDeck.getLeft_cards().equals("1")) {
            reviewHolder.tvRecviewCount.setText(String.format("%s Review Today", mainDeck.getReviewed_today()));
        } else {
            reviewHolder.tvRecviewCount.setText(String.format("%s Reviews Today", mainDeck.getReviewed_today()));
        }
        if (Integer.parseInt(mainDeck.getReviewed_today()) > 0) {
            reviewHolder.btnStartReview.setEnabled(true);
        } else {
            reviewHolder.btnStartReview.setEnabled(false);
        }

        if (!TextUtils.isEmpty(mainDeck.getImage())) {
            Glide.with(context).load(mainDeck.getImage()).into(reviewHolder.topicIV);
        } else {
            reviewHolder.topicIV.setImageDrawable(Helper.GetDrawable(mainDeck.getTitle(), context, ColorGenerator.MATERIAL.getRandomColor()));
        }

        if (mainDeck.isSelected()) {
            reviewHolder.rvReviewChild.setVisibility(View.VISIBLE);

            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            ((ViewGroup) reviewHolder.rvReviewChild.getParent()).setLayoutTransition(layoutTransition);

        } else {
            reviewHolder.rvReviewChild.setVisibility(View.GONE);
        }

        reviewHolder.rvReviewChild.setAdapter(new ReviewChildAdapter(context, mainDeck.getSubdeck(), flashCardReviewFragment));
    }

    @Override
    public int getItemCount() {
        return null != mainDeckArrayList ? mainDeckArrayList.size() : 0;
    }

    class ReviewParentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout titleLL;
        TextView tvTitle;
        TextView tvStudiedCount;
        TextView tvCountLeft;
        TextView tvRecviewCount;
        ImageView topicIV;
        RecyclerView rvReviewChild;
        RelativeLayout rlArrow;
        Button btnStartReview;

        public ReviewParentHolder(@NonNull View itemView) {
            super(itemView);

            btnStartReview = itemView.findViewById(R.id.btn_start_review);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvStudiedCount = itemView.findViewById(R.id.tv_studied_count);
            topicIV = itemView.findViewById(R.id.topicIV);
            rlArrow = itemView.findViewById(R.id.rl_arrow);
            titleLL = itemView.findViewById(R.id.ll_title);
            tvCountLeft = itemView.findViewById(R.id.tv_count_left);
            tvRecviewCount = itemView.findViewById(R.id.tv_recview_count);

            rlArrow.setOnClickListener(this);
            titleLL.setOnClickListener(this);
            btnStartReview.setOnClickListener(this);

            rvReviewChild = itemView.findViewById(R.id.rv_review_child);
            rvReviewChild.setLayoutManager(new LinearLayoutManager(context));
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position >= 0) {
                switch (view.getId()) {
                    case R.id.btn_start_review:
                        flashCardReviewFragment.startReview(Const.DECK_ID, mainDeckArrayList.get(position).getDId());
                        break;
                    case R.id.rl_arrow:
                    case R.id.ll_title:
                        for (int i = 0; i < mainDeckArrayList.size(); i++) {
                            if (position == i) {
                                if (mainDeckArrayList.get(position).isSelected()) {
                                    mainDeckArrayList.get(position).setSelected(false);
                                } else {
                                    mainDeckArrayList.get(position).setSelected(true);
                                }
                            } else {
                                mainDeckArrayList.get(i).setSelected(false);
                            }
                        }
                        notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
