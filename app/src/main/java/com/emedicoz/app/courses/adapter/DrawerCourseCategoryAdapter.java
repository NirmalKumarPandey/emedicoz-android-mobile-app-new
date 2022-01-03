package com.emedicoz.app.courses.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.modelo.courses.CourseCategory;
import com.emedicoz.app.modelo.courses.CoursesData;
import com.emedicoz.app.templateAdapters.RecordedCourseSearchAdapter;

import java.util.ArrayList;

public class DrawerCourseCategoryAdapter extends RecyclerView.Adapter<DrawerCourseCategoryAdapter.CategoryViewHolder> {

    ArrayList<CoursesData> categoryArrayList;
    Context context;

    public DrawerCourseCategoryAdapter(ArrayList<CoursesData> categoryArrayList, Context context) {
        this.categoryArrayList = categoryArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public DrawerCourseCategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.drawer_course_category_layout, viewGroup, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i) {
        CourseCategory courseCategory = categoryArrayList.get(i).getCategory_info();
        ArrayList<Course> courseList = categoryArrayList.get(i).getCourse_list();
        if (courseCategory == null || courseList == null) return;

        categoryViewHolder.tv_course_category_title.setText(courseCategory.getName());
        if (courseCategory.isSelected()) {
            categoryViewHolder.iv_drop_down.setRotation(-90f);
            categoryViewHolder.recycler_view_course_list.setVisibility(View.VISIBLE);
        } else {
            categoryViewHolder.iv_drop_down.setRotation(90f);
            categoryViewHolder.recycler_view_course_list.setVisibility(View.GONE);
        }
        if (courseCategory.getApp_view_type().equals("1")) {
            categoryViewHolder.recycler_view_course_list.setLayoutManager(new LinearLayoutManager(context));
        } else if (courseCategory.getApp_view_type().equals("2")) {
            categoryViewHolder.recycler_view_course_list.setLayoutManager(new LinearLayoutManager(context, LinearLayout.HORIZONTAL, false));
        }

//        categoryViewHolder.recycler_view_course_list.setAdapter(new DrawerCourseCategoryListAdapter(context, courseCategory.getApp_view_type(), courseList));
        RecordedCourseSearchAdapter adapter = new RecordedCourseSearchAdapter(courseList, context, false);
        categoryViewHolder.recycler_view_course_list.setLayoutManager(new LinearLayoutManager(context));
        categoryViewHolder.recycler_view_course_list.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public void filterList(ArrayList<CoursesData> newCoursesDataArrayList) {
        categoryArrayList = newCoursesDataArrayList;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_course_category_title;
        ImageView iv_drop_down;
        RecyclerView recycler_view_course_list;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_course_category_title = itemView.findViewById(R.id.tv_course_category_title);
            iv_drop_down = itemView.findViewById(R.id.iv_drop_down);
            recycler_view_course_list = itemView.findViewById(R.id.recycler_view_course_list);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position >= 0) {
                for (int i = 0; i < categoryArrayList.size(); i++) {
                    CourseCategory catInfo = categoryArrayList.get(position).getCategory_info();
                    if (position == i && catInfo != null) {
                        if (catInfo.isSelected()) {
                            catInfo.setSelected(false);
                        } else {
                            catInfo.setSelected(true);
                        }
                    } else if (categoryArrayList.get(i).getCategory_info() != null) {
                        categoryArrayList.get(i).getCategory_info().setSelected(false);
                    }
                }
                notifyDataSetChanged();
            }
        }
    }
}
