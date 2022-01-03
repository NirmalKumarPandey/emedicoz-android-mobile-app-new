package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.AllLiveCourses;
import com.emedicoz.app.modelo.courses.CoursesData;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;

import java.util.ArrayList;

public class LCPracticeRV1Adapter extends RecyclerView.Adapter<LCPracticeRV1Adapter.PracticeListViewHolder> {

    Context context;
    ArrayList<CoursesData> coursesDataArrayList;

    public LCPracticeRV1Adapter(Context context, ArrayList<CoursesData> coursesDataArrayList) {
        this.context = context;
        this.coursesDataArrayList = coursesDataArrayList;
    }

    @Override
    public PracticeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ibt_single_item_practice, null);
        return new PracticeListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PracticeListViewHolder holder, int position) {
        holder.setData(coursesDataArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return GenericUtils.isListEmpty(coursesDataArrayList) ? 0 : coursesDataArrayList.size();
    }

    public class PracticeListViewHolder extends RecyclerView.ViewHolder {

        TextView practiceNameTV, viewAllTV;
        RecyclerView recyclerView;
        LinearLayout ibtSingleItemLL;
        RelativeLayout ibtSingleItemRL;


        public PracticeListViewHolder(View itemView) {
            super(itemView);
            practiceNameTV = itemView.findViewById(R.id.practiceNameTV);
            viewAllTV = itemView.findViewById(R.id.viewAllTV);
            recyclerView = itemView.findViewById(R.id.ibt_practice_single_RV);
            ibtSingleItemLL = itemView.findViewById(R.id.ibt_single_item_LL);
            ibtSingleItemRL = itemView.findViewById(R.id.ibt_single_item_RL);

        }

        public void setData(final CoursesData coursesData) {
            if (coursesData != null) {
                practiceNameTV.setText(Helper.CapitalizeText(coursesData.getCategory_info().getName()));
                viewAllTV.setOnClickListener(view -> {
                    Intent intent = new Intent(context, AllLiveCourses.class);
                    intent.putExtra("CATEGORY_INFO_ID", coursesData.getCategory_info().getId());
                    intent.putExtra("courseList", coursesData.getCourse_list());
                    context.startActivity(intent);
                });
                if (!GenericUtils.isListEmpty(coursesData.getCourse_list())) {
                    if (coursesData.getCourse_list().size() <= 4)
                        viewAllTV.setVisibility(View.GONE);
                    else
                        viewAllTV.setVisibility(View.VISIBLE);

                    LCPracticeSingleRVAdapter adapter = new LCPracticeSingleRVAdapter(context, coursesData);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
                    recyclerView.setAdapter(adapter);


                } else {
                    ibtSingleItemLL.setVisibility(View.GONE);
                    ibtSingleItemRL.setVisibility(View.GONE);
                }
            }
        }
    }
}
