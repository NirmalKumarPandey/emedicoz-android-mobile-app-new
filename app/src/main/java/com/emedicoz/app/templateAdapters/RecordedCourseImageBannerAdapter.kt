package com.emedicoz.app.templateAdapters

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer.OnCompletionListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.emedicoz.app.R
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.databinding.RecordCoursesListItemImageBannerBinding
import com.emedicoz.app.modelo.CourseBannerData
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import com.smarteist.autoimageslider.SliderViewAdapter


class RecordedCourseImageBannerAdapter(private var imageBannerList: MutableList<CourseBannerData>, private val context: Context)
    : SliderViewAdapter<RecordedCourseImageBannerAdapter.RecordedCourseImageBannerViewHolder>() {

    override fun onBindViewHolder(holder: RecordedCourseImageBannerViewHolder, position: Int) {
        val currentImageBannerList: CourseBannerData = imageBannerList[position]
        try {

            if (currentImageBannerList.media_type == "video") {
                holder.binding.videoView.visibility = View.VISIBLE
                holder.binding.ivAutoImageSlider.visibility = View.GONE
                holder.binding.videoView.setVideoPath(currentImageBannerList.imageLink)
                holder.binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                holder.binding.videoView.visibility = View.VISIBLE
                holder.binding.playPauseBtn.visibility = View.VISIBLE
            } else {
                holder.binding.playPauseBtn.visibility = View.GONE
                holder.binding.videoView.visibility = View.GONE
                holder.binding.ivAutoImageSlider.visibility = View.VISIBLE
                Glide.with(context)
                        .load(currentImageBannerList.imageLink)
                        .into(holder.binding.ivAutoImageSlider)
            }

            holder.binding.ivAutoImageSlider.setOnClickListener {
                if (currentImageBannerList.media_type == "video") {
                } else {
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
/*                    if (currentImageBannerList.webImageLink != null) {
                        Helper.GoToWebViewActivity(context, currentImageBannerList.webLink)
                    } else {
                        if (currentImageBannerList.id == Constants.Extras.QUESTION_BANK_COURSE_ID) {
                            val intent = Intent(context, CourseActivity::class.java)
                            intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                            intent.putExtra(Const.PARENT_ID, currentImageBannerList.id)
                            context.startActivity(intent)
                        } else {
                            val intent = Intent(context, CourseActivity::class.java)
                            intent.putExtra(Const.PARENT_ID, currentImageBannerList.id)
                            if (currentImageBannerList.is_live == "1") {
                                intent.putExtra(Const.FRAG_TYPE, Const.LIVE_CLASSES)
                            } else {
                                intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                            }

                            context.startActivity(intent)
                        }
                    }*/
                }
            }


            holder.binding.videoView.setOnClickListener {
                if (holder.binding.playPauseBtn.visibility == View.VISIBLE) {
                    holder.binding.playPauseBtn.visibility = View.GONE
                } else {
                    holder.binding.playPauseBtn.visibility = View.VISIBLE
                }
                if (holder.binding.videoView.isPlaying) {
                    holder.binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
                    holder.binding.videoView.pause()
                } else {
                    holder.binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    holder.binding.videoView.start()
                    holder.binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
                    holder.binding.playPauseBtn.visibility = View.GONE
                }
            }

            holder.binding.playPauseBtn.setOnClickListener {
                if (holder.binding.videoView.isPlaying) {
                    holder.binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
                    holder.binding.videoView.pause()
                } else {
                    holder.binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    holder.binding.videoView.start()
                    holder.binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
                    holder.binding.playPauseBtn.visibility = View.GONE
                }
            }


            holder.binding.videoView.setOnCompletionListener(OnCompletionListener {
                // TODO Auto-generated method stub
                holder.binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                holder.binding.playPauseBtn.visibility = View.VISIBLE
            })


        } catch (e: Exception) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecordedCourseImageBannerViewHolder {
        val binding = RecordCoursesListItemImageBannerBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordedCourseImageBannerViewHolder(binding)
    }

    inner class RecordedCourseImageBannerViewHolder(val binding: RecordCoursesListItemImageBannerBinding)
        : SliderViewAdapter.ViewHolder(binding.root)

    override fun getCount() = imageBannerList.size

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
}

