package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.NonScrollRecyclerView;
import com.emedicoz.app.customviews.SingleChildViewCurriculum;
import com.emedicoz.app.modelo.courses.CourseLockedManager;
import com.emedicoz.app.modelo.courses.Curriculam;
import com.emedicoz.app.modelo.courses.File_Meta_Type;
import com.emedicoz.app.modelo.courses.SingleCourseData;
import com.emedicoz.app.utilso.Helper;

import java.util.ArrayList;

/**
 * Created by app on 30/1/18.
 */

public class CurriculumFileRecyclerAdapter extends RecyclerView.Adapter<CurriculumFileRecyclerAdapter.ViewHolder1> {

    SingleCourseData singleCourseData;
    SingleChildViewCurriculum singleChildViewCurriculum;
    ArrayList<Curriculam> curriculamArrayList;
    private Activity context;
    private Curriculam curriculum;
    private ArrayList<File_Meta_Type> fileMetaTypeArrayList;
    private CurriculumRecyclerAdapter curriculumRecyclerAdapter;

    public CurriculumFileRecyclerAdapter(SingleCourseData singleCourseData, Curriculam curriculam, Activity context, ArrayList<File_Meta_Type> fileMetaTypeArrayList) {
        this.singleCourseData = singleCourseData;
        this.context = context;
        this.fileMetaTypeArrayList = fileMetaTypeArrayList;
        this.curriculum = curriculam;
    }

    public CurriculumFileRecyclerAdapter(SingleCourseData course, Activity activity, ArrayList<Curriculam> curriculam) {
        this.singleCourseData = course;
        this.context = activity;
        this.curriculamArrayList = curriculam;
    }

    @Override
    public ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_curriculum, parent, false);
        return new ViewHolder1(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder1 holder, int position) {

        if (singleCourseData.getIs_locked().equals("1")) {   // to check that course content should be locked or not
            if (position <= Integer.parseInt(CourseLockedManager.getCourseLockStatus(singleCourseData.getId(), String.valueOf(position), context))) {
                holder.lockedLL.setVisibility(View.GONE);
                holder.titleTV.setText(Helper.CapitalizeText(curriculamArrayList.get(position).getTitle()));
                curriculumRecyclerAdapter = new CurriculumRecyclerAdapter(singleCourseData, CurriculumFileRecyclerAdapter.this, curriculamArrayList, context, position, false);
                holder.expListView.setAdapter(curriculumRecyclerAdapter);
            } else {
                holder.lockedLL.setVisibility(View.VISIBLE);
                holder.titleTV.setText(Helper.CapitalizeText(curriculamArrayList.get(position).getTitle()));
                curriculumRecyclerAdapter = new CurriculumRecyclerAdapter(singleCourseData, CurriculumFileRecyclerAdapter.this, curriculamArrayList, context, position, true);
                holder.expListView.setAdapter(curriculumRecyclerAdapter);
            }
        } else {  // to check that course content should be locked or not , if it is not locked then it must be open to all
            holder.lockedLL.setVisibility(View.GONE);
            holder.titleTV.setText(Helper.CapitalizeText(curriculamArrayList.get(position).getTitle()));
            curriculumRecyclerAdapter = new CurriculumRecyclerAdapter(singleCourseData, CurriculumFileRecyclerAdapter.this, curriculamArrayList, context, position, false);
            holder.expListView.setAdapter(curriculumRecyclerAdapter);
        }

    }

    @Override
    public int getItemCount() {
        return curriculamArrayList.size();
    }

    // SingleChildView for single feed
    public class ViewHolder1 extends RecyclerView.ViewHolder {
        TextView titleTV;
        NonScrollRecyclerView expListView;
        LinearLayout mainCurriculumLL, lockedLL;

        public ViewHolder1(final View view) {
            super(view);
            expListView = view.findViewById(R.id.curriculumExpListLL);
            titleTV = view.findViewById(R.id.curriculumTextTV);
            mainCurriculumLL = view.findViewById(R.id.mainCurriculumLL);
            lockedLL = view.findViewById(R.id.lockedLL);
            expListView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }
    }
}
