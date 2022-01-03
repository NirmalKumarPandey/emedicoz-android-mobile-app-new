package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.R;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.modelo.courses.CoursesData;
import com.emedicoz.app.utilso.Const;

import java.util.List;

/**
 * Created by appsquadz on 28/9/17.
 */

public class CategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<CoursesData> coursesArrayList;
    View.OnClickListener itemclickListener = (View v) -> {
        CoursesData coursesData = (CoursesData) v.getTag();

        Intent courseList = new Intent(context, CourseActivity.class);//FRAG_TYPE, Const.SEEALL_COURSE AllCoursesAdapter
        courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_COURSE);
        courseList.putExtra(Const.COURSE_CATEGORY, coursesData.getCategory_info());
        context.startActivity(courseList);
    };

    public CategoryListAdapter(Context context, List<CoursesData> coursesDataArrayList) {
        this.context = context;
        this.coursesArrayList = coursesDataArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_categories_courses, parent, false);
        return new CourseHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final CourseHolder holder1 = (CourseHolder) holder;
        holder1.courseTV.setText(coursesArrayList.get(position).getCategory_info().getName());
        Glide.with(context).load(coursesArrayList.get(position).getCategory_info().getImage().replace(
                "course_file_meta", "course_file_meta_100")).into(holder1.imageIV);
        holder1.parentCV.setTag(coursesArrayList.get(position));
        holder1.parentCV.setOnClickListener(itemclickListener);

    }

    @Override
    public int getItemCount() {
        return coursesArrayList.size();
    }

    public static class CourseHolder extends RecyclerView.ViewHolder {
        TextView courseTV;
        ImageView imageIV;
        LinearLayout parentLL;
        CardView parentCV;

        public CourseHolder(View itemView) {
            super(itemView);
            courseTV = itemView.findViewById(R.id.titleTV);
            imageIV = itemView.findViewById(R.id.videoImage);
            parentLL = itemView.findViewById(R.id.parentLL);
            parentCV = itemView.findViewById(R.id.parentCV);
        }
    }

}
