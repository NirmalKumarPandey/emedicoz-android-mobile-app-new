package com.emedicoz.app.pastpaperexplanation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.pastpaperexplanation.model.PPECategoryData;
import com.emedicoz.app.pastpaperexplanation.model.PPEData;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;

import java.util.ArrayList;

public class ExamParentAdapter extends RecyclerView.Adapter<ExamParentAdapter.MyViewHolderParent> {

        Context context;
        ArrayList<PPECategoryData> ppeCategoryDataArrayList;
        PPEData ppeData;

        public ExamParentAdapter(Context context, ArrayList<PPECategoryData> ppeCategoryDataArrayList, PPEData ppeData) {
            this.context = context;
            this.ppeCategoryDataArrayList = ppeCategoryDataArrayList;
            this.ppeData = ppeData;
        }

        @NonNull
        @Override
        public MyViewHolderParent onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_exam_parent_adapter, parent, false);
            return new MyViewHolderParent(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolderParent holder, int position) {
            holder.tvTitle.setText(ppeCategoryDataArrayList.get(position).getSubject());

            holder.examChildRecyclerview.setAdapter(new ExamChildAdapter(context, ppeCategoryDataArrayList.get(position).getData(), ppeData));
            if (ppeCategoryDataArrayList.get(position).isSelected()) {
                holder.ivArrow.setRotation(90);
                holder.examChildRecyclerview.setVisibility(View.VISIBLE);
            } else {
                holder.examChildRecyclerview.setVisibility(View.GONE);
                holder.ivArrow.setRotation(270);
            }
        }

        @Override
        public int getItemCount() {
            return ppeCategoryDataArrayList.size();
        }

        class MyViewHolderParent extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tvTitle;
            ImageView ivArrow;
            RecyclerView examChildRecyclerview;

            public MyViewHolderParent(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tvTitle);
                ivArrow = itemView.findViewById(R.id.ivArrow);
                examChildRecyclerview = itemView.findViewById(R.id.examChildRecyclerview);
                examChildRecyclerview.setLayoutManager(new LinearLayoutManager(context));
                examChildRecyclerview.addItemDecoration(new EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL));

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position >= 0) {
                    for (int i = 0; i < ppeCategoryDataArrayList.size(); i++) {
                        if (position == i) {
                            if (ppeCategoryDataArrayList.get(position).isSelected()) {
                                ppeCategoryDataArrayList.get(position).setSelected(false);
                            } else {
                                ppeCategoryDataArrayList.get(position).setSelected(true);
                            }
                        } else {
                            ppeCategoryDataArrayList.get(i).setSelected(false);
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        }
    }