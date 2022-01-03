package com.emedicoz.app.templateAdapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.RecordedScreenCourseAsPerSearchViewBinding
import com.emedicoz.app.modelo.courses.Course
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class RecordedCourseAsPerSearchAdapter(private val courseAsPerSearchList: ArrayList<ArrayList<Course>>, private val context: Context)
    : RecyclerView.Adapter<RecordedCourseAsPerSearchAdapter.RecordedCourseAsPerSearchViewHolder>() {


    override fun onBindViewHolder(holder: RecordedCourseAsPerSearchViewHolder, position: Int) {
        try {

            val recordedCourseAsPerSearchItemAdapter = RecordedCourseAsPerSearchItemAdapter(courseAsPerSearchList[position], context)
            holder.binding.recordedCourseSlider.apply {
                setSliderAdapter(recordedCourseAsPerSearchItemAdapter)
                setIndicatorAnimation(IndicatorAnimationType.WORM)
                setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
                indicatorSelectedColor =  ContextCompat.getColor(context, R.color.skyBlue);
                indicatorUnselectedColor = Color.GRAY
                isAutoCycle = true
                scrollTimeInSec = 3
            }.startAutoCycle()

        } catch (e: Exception) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordedCourseAsPerSearchViewHolder {
        val binding = RecordedScreenCourseAsPerSearchViewBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordedCourseAsPerSearchViewHolder(binding)
    }

    inner class RecordedCourseAsPerSearchViewHolder(val binding: RecordedScreenCourseAsPerSearchViewBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = courseAsPerSearchList.size

    private fun loadRecordedCourse(): ArrayList<Course> {
        var courseList: ArrayList<Course> = ArrayList()
        for (i in 1..3) {
            val course: Course = Course()
            courseList.add(course)
        }
        return courseList
    }
}