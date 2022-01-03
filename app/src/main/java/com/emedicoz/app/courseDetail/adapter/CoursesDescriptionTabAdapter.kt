package com.emedicoz.app.courseDetail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.databinding.RecordedDescriptionReviewItemsBinding
import com.emedicoz.app.modelo.liveclass.courses.DescCourseReview
import com.emedicoz.app.utilso.Helper


class CoursesDescriptionTabAdapter(private val DescriptionReviewList: List<DescCourseReview>, private var context: Context)
    : RecyclerView.Adapter<CoursesDescriptionTabAdapter.CourseReviewViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseReviewViewHolder {
        val binding = RecordedDescriptionReviewItemsBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseReviewViewHolder, position: Int) {
        val currentDescriptionItem: DescCourseReview = DescriptionReviewList[position]

        try {

            holder.binding.apply {
                Glide.with(context)
                        .load(currentDescriptionItem.profilePicture)
                        .apply(RequestOptions.placeholderOf(R.drawable.profile_image).error(R.drawable.profile_image))
                        .into(reviewProfileImg1)
                reviewer1.text = currentDescriptionItem.name
                reviewer1Rating.rating = currentDescriptionItem.rating.toFloat()
                reviewer1Comments.text = currentDescriptionItem.text
                reviewDate1.text = Helper.getFormatedDate(currentDescriptionItem.creationTime.toLong())

            }


        } catch (e: Exception) {
        }

    }

    override fun getItemCount(): Int {
        return if (DescriptionReviewList.size > 2) 2 else DescriptionReviewList.size
    }

    inner class CourseReviewViewHolder(val binding: RecordedDescriptionReviewItemsBinding)
        : RecyclerView.ViewHolder(binding.root)

}










