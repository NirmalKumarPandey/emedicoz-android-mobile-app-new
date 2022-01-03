package com.emedicoz.app.templateAdapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.databinding.RecordCoursesListItemCategoryBinding
import com.emedicoz.app.modelo.courses.CourseCategory
import com.emedicoz.app.utilso.Const

class RecordedCourseCategoriesAdapter(private val courseCategory: ArrayList<CourseCategory>, private val context: Context)
    : RecyclerView.Adapter<RecordedCourseCategoriesAdapter.RecordCoursesListItemCategoriesViewHolder>() {


    override fun onBindViewHolder(holder: RecordCoursesListItemCategoriesViewHolder, position: Int) {
        val category: CourseCategory = courseCategory[position]



        try {
            Glide.with(context)
                    .load(category.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.cat_image).error(R.drawable.cat_image))
                    .into(holder.binding.courseImage)

            holder.binding.tvCourseName.text = category.name
            holder.binding.root.tag = category
            holder.binding.root.setOnClickListener {

                redirectToSeeAll(it.tag as CourseCategory)
            }
        } catch (e: Exception) {

        }

    }


    private fun redirectToSeeAll(category: CourseCategory) {

        val courseList = Intent(context, CourseActivity::class.java)
        courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_COURSE)
        courseList.putExtra(Const.COURSE_CATEGORY, category)
        context.startActivity(courseList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordCoursesListItemCategoriesViewHolder {
        val binding = RecordCoursesListItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordCoursesListItemCategoriesViewHolder(binding)
    }
    inner class RecordCoursesListItemCategoriesViewHolder(val binding: RecordCoursesListItemCategoryBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = courseCategory.size
}