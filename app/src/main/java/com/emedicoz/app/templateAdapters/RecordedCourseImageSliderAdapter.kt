package com.emedicoz.app.templateAdapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.databinding.RecordCoursesListItemImageSliderBinding
import com.emedicoz.app.modelo.CourseBannerData
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView


class RecordedCourseImageSliderAdapter(private val recordedImageList: ArrayList<ArrayList<CourseBannerData>>, private val context: Context)
    : RecyclerView.Adapter<RecordedCourseImageSliderAdapter.RecordCoursesListItemImageViewHolder>() {

    override fun onBindViewHolder(holder: RecordCoursesListItemImageViewHolder, position: Int) {
        try {
            val imageList: ArrayList<CourseBannerData> = recordedImageList[position]
            if(imageList.size > 1){
                holder.binding.recordedImageSlider.visibility = View.VISIBLE
                holder.binding.imageView.visibility = View.GONE
                val recordedCourseImageBannerAdapter = RecordedCourseImageBannerAdapter(imageList, context)
                holder.binding.recordedImageSlider.apply {
                    setSliderAdapter(recordedCourseImageBannerAdapter)
                    setIndicatorAnimation(IndicatorAnimationType.WORM)
                    setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
                    indicatorSelectedColor = Color.WHITE
                    isAutoCycle = true
                    scrollTimeInSec = 3
                }.startAutoCycle()
            }else if(imageList.size == 1){
                val currentImageBannerList: CourseBannerData = imageList[0]

                if (currentImageBannerList.media_type == "video") {

                }else {
                    holder.binding.recordedImageSlider.visibility = View.GONE
                    holder.binding.imageView.visibility = View.VISIBLE


                    Glide.with(context)
                            .load(currentImageBannerList.imageLink)
                            .into(holder.binding.imageView)

                    holder.binding.imageView.setOnClickListener {
                        /* if (currentImageBannerList.webImageLink != null) {
                             Helper.GoToWebViewActivity(context, currentImageBannerList.webLink)
                         } else {*/
                        if (currentImageBannerList.id == Constants.Extras.QUESTION_BANK_COURSE_ID) {
                            val intent = Intent(context, CourseActivity::class.java)
                            intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                            intent.putExtra(Const.PARENT_ID, currentImageBannerList.id)
                            context.startActivity(intent)
                        } else {
                            val intent = Intent(context, CourseDetailActivity::class.java)
                            intent.putExtra(Const.COURSE, getCourseData(currentImageBannerList))
/*                                val intent = Intent(context, CourseActivity::class.java)
                                intent.putExtra(Const.PARENT_ID, currentImageBannerList.id)*/
                            if (currentImageBannerList.is_live == "1") {
                                intent.putExtra(Const.FRAG_TYPE, Const.LIVE_CLASSES)
                            } else {
                                intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                            }
                            context.startActivity(intent)
                        }
                        //    }
                    }
                }

            }
        } catch (e: Exception) {
        }
    }

    private fun getCourseData(currentImageBannerList: CourseBannerData): Course {
        val course = Course()
        course.id = currentImageBannerList.courseId
        course.title = currentImageBannerList.bannerTitle
        course.is_combo = currentImageBannerList.isCombo
        course.is_live = currentImageBannerList.is_live
        course.mrp = currentImageBannerList.mrp
        course.for_dams = currentImageBannerList.for_dams
        course.non_dams = currentImageBannerList.non_dams
        return course
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordCoursesListItemImageViewHolder {
        val binding = RecordCoursesListItemImageSliderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordCoursesListItemImageViewHolder(binding)
    }

    override fun getItemCount() = recordedImageList.size

    inner class RecordCoursesListItemImageViewHolder(val binding: RecordCoursesListItemImageSliderBinding)
        : RecyclerView.ViewHolder(binding.root)


    // Demo Data
    /*private fun loadImageBannerList(): ArrayList<RecordedCoursesImageItem> {
        var recordedImageSliderList: ArrayList<RecordedCoursesImageItem> = ArrayList()
        val sliderItem = RecordedCoursesImageItem()
        sliderItem.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        recordedImageSliderList.add(sliderItem)
        sliderItem.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260")
        recordedImageSliderList.add(sliderItem)
        sliderItem.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
        recordedImageSliderList.add(sliderItem)


        return recordedImageSliderList

    }*/


}