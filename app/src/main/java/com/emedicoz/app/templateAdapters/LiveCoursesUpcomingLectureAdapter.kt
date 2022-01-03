package com.emedicoz.app.templateAdapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.LiveCoursesUpcomingLecturesBinding
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import java.util.*


class LiveCoursesUpcomingLectureAdapter(private val upcomingLectureList: ArrayList<Course>, private val context: Context)
    : RecyclerView.Adapter<LiveCoursesUpcomingLectureAdapter.LiveCoursesItemUpcomingLecturesViewHolder>() {

    private lateinit var progress: Progress

    override fun onBindViewHolder(holder: LiveCoursesItemUpcomingLecturesViewHolder, position: Int) {
        val currentCourseItem: Course = upcomingLectureList[position]
        try {


            holder.binding.apply {

                detailCourse.tag = currentCourseItem
                detailCourse.setOnClickListener {
                    var item = it.tag as Course
                    if (Helper.isTestOrQbankCourse(item)) {
                        if (item != null) {
                            if (item.course_type == "2") {
                                val args = Bundle()
                                args.putString(Const.FRAG_TYPE, Constants.StudyType.TESTS)
                                (context as HomeActivity).navController.navigate(R.id.studyFragment,args )
                            } else if (item.course_type == "3") {
                                val args = Bundle()
                                args.putString(Const.FRAG_TYPE, Constants.StudyType.QBANKS)
                                (context as HomeActivity).navController.navigate(R.id.studyFragment,args )
                            } else {
                                (context as HomeActivity).navController.navigate(R.id.studyFragment)
                            }
                        } else {
                            (context as HomeActivity).navController.navigate(R.id.studyFragment )
                        }
                     /*   val intent = Intent(context, CourseActivity::class.java)
                        intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                        intent.putExtra(Const.COURSES, item)
                        context.startActivity(intent)*/
                    } else {
                        val intent = Intent(context, CourseDetailActivity::class.java)
                        intent.putExtra(Const.COURSE, item)
                        intent.putExtra(Const.FRAG_TYPE, Const.LIVE_CLASSES)
                        context.startActivity(intent)
                    }
                }

                tvCourseName.text = currentCourseItem.video_title
                courseEnrolled.text = "${currentCourseItem.learner} Enrolled"
                if (currentCourseItem.learner == null) {
                    courseEnrolled.text = "0 Enrolled"
                }
                Glide.with(context)
                        .load(currentCourseItem.cover_image)
                        .apply(RequestOptions.placeholderOf(R.mipmap.courses_blue).error(R.mipmap.courses_blue))
                        .into(holder.binding.courseImage)

                val resultDate = Helper.getTime(currentCourseItem.live_on.toLong())
                time.text = "Time: $resultDate"


                minutes.text = "${currentCourseItem.duration} min"


            }

        } catch (e: Exception) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveCoursesItemUpcomingLecturesViewHolder {
        val binding = LiveCoursesUpcomingLecturesBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return LiveCoursesItemUpcomingLecturesViewHolder(binding)
    }

    inner class LiveCoursesItemUpcomingLecturesViewHolder(val binding: LiveCoursesUpcomingLecturesBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = upcomingLectureList.size



}