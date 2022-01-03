package com.emedicoz.app.courses.adapter

import android.app.Activity
import com.emedicoz.app.modelo.courses.CourseCategory
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.courses.adapter.AllCatAdapter.AllCatViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.content.Intent
import android.view.View
import com.emedicoz.app.courses.activity.CourseActivity
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.SingleItemAllCategoryBinding
import com.emedicoz.app.utilso.Const

class AllCatAdapter(var activity: Activity, var tileDataArrayList: List<CourseCategory>) :
    RecyclerView.Adapter<AllCatViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): AllCatViewHolder {
        val binding = SingleItemAllCategoryBinding.inflate(LayoutInflater.from(activity),viewGroup,false)
        return AllCatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllCatViewHolder, position: Int) {
        setData(holder,position)
    }

    private fun setData(holder: AllCatAdapter.AllCatViewHolder, position: Int) {
        holder.singleItemAllCategoryBinding.categoryName.text = tileDataArrayList[position].name
        holder.singleItemAllCategoryBinding.parentLL.setOnClickListener {
            val courseList = Intent(
                activity,
                CourseActivity::class.java
            ) //FRAG_TYPE, Const.SEEALL_COURSE AllCoursesAdapter
            courseList.putExtra(Const.FRAG_TYPE, Const.SEEALL_COURSE)
            courseList.putExtra(Const.COURSE_CATEGORY, tileDataArrayList[position])
            activity.startActivity(courseList)
        }
    }

    override fun getItemCount(): Int {
        return tileDataArrayList.size
    }

    inner class AllCatViewHolder(val singleItemAllCategoryBinding: SingleItemAllCategoryBinding) : RecyclerView.ViewHolder(singleItemAllCategoryBinding.root)
}