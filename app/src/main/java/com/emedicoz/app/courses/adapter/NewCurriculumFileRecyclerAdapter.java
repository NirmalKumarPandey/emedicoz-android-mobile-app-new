package com.emedicoz.app.courses.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class NewCurriculumFileRecyclerAdapter extends RecyclerView.Adapter<NewCurriculumFileRecyclerAdapter.NewCurriculumFileViewHolder> {

    SingleCourseData singleCourseData;
    SingleChildViewCurriculum singleChildViewCurriculum;
    ArrayList<Curriculam> curriculamArrayList;
    private Activity context;
    private Curriculam curriculum;
    private String topic_id = "";
    private ArrayList<File_Meta_Type> fileMetaTypeArrayList;
    private NewCurriculumRecyclerAdapter curriculumRecyclerAdapter;

    public NewCurriculumFileRecyclerAdapter(SingleCourseData course, Activity activity, ArrayList<Curriculam> curriculam) {
        this.singleCourseData = course;
        this.context = activity;
        this.curriculamArrayList = curriculam;
    }

    @NonNull
    @Override
    public NewCurriculumFileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_row_curriculum, viewGroup, false);
        return new NewCurriculumFileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewCurriculumFileViewHolder holder, int position) {

        if (singleCourseData.getIs_locked().equals("1")) {   // to check that course content should be locked or not
            if (position <= Integer.parseInt(CourseLockedManager.getCourseLockStatus(singleCourseData.getId(), String.valueOf(position), context))) {
                holder.lockedLL.setVisibility(View.GONE);
                holder.titleTV.setText(Helper.CapitalizeText(curriculamArrayList.get(position).getTitle()));
                curriculumRecyclerAdapter = new NewCurriculumRecyclerAdapter(singleCourseData, NewCurriculumFileRecyclerAdapter.this, curriculamArrayList, context, position, false);
                holder.expListView.setAdapter(curriculumRecyclerAdapter);
            } else {
                holder.lockedLL.setVisibility(View.VISIBLE);
                holder.titleTV.setText(Helper.CapitalizeText(curriculamArrayList.get(position).getTitle()));
                curriculumRecyclerAdapter = new NewCurriculumRecyclerAdapter(singleCourseData, NewCurriculumFileRecyclerAdapter.this, curriculamArrayList, context, position, true);
                holder.expListView.setAdapter(curriculumRecyclerAdapter);
            }
        } else {  // to check that course content should be locked or not , if it is not locked then it must be open to all
            holder.lockedLL.setVisibility(View.GONE);
            holder.titleTV.setText(Helper.CapitalizeText(curriculamArrayList.get(position).getTitle()));
            curriculumRecyclerAdapter = new NewCurriculumRecyclerAdapter(singleCourseData, NewCurriculumFileRecyclerAdapter.this, curriculamArrayList, context, position, false);
            holder.expListView.setAdapter(curriculumRecyclerAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return curriculamArrayList.size();
    }

    public class NewCurriculumFileViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        NonScrollRecyclerView expListView;
        LinearLayout mainCurriculumLL, lockedLL;

        public NewCurriculumFileViewHolder(final View view) {
            super(view);
            expListView = view.findViewById(R.id.curriculumExpListLL);
            titleTV = view.findViewById(R.id.curriculumTextTV);
            mainCurriculumLL = view.findViewById(R.id.mainCurriculumLL);
            lockedLL = view.findViewById(R.id.lockedLL);
            expListView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }
    }
}
