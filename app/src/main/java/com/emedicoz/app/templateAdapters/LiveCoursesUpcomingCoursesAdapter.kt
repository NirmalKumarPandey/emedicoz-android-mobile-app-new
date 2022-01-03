package com.emedicoz.app.templateAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.LiveCoursesUpcomingClassesBinding
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LiveCourseInterface
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class LiveCoursesUpcomingCoursesAdapter(private val upcomingCourseList: ArrayList<Course>, private val context: Context)
    : RecyclerView.Adapter<LiveCoursesUpcomingCoursesAdapter.LiveCoursesItemUpcomingCoursesViewHolder>() {

    private var lstHolders: ArrayList<LiveCoursesItemUpcomingCoursesViewHolder> = ArrayList()

    private lateinit var progress: Progress
    private val mHandler: Handler = Handler()
    private val updateRemainingTimeRunnable = Runnable {
        synchronized(lstHolders) {
            for (holder in lstHolders) {
                holder.updateTimeRemaining()
            }
        }
    }

    override fun onBindViewHolder(holder: LiveCoursesItemUpcomingCoursesViewHolder, position: Int) {
        val currentCourseItem: Course = upcomingCourseList[position]
        try {
            holder.binding.apply {

                detailCourse.tag = currentCourseItem
                detailCourse.setOnClickListener {
                    var item = it.tag as Course
                    if (Helper.isTestOrQbankCourse(item)) {
                        if (item != null) {
                            if (item.course_type == "2") {
                                val intent = Intent(context, HomeActivity::class.java)
                                intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.TESTS)
                                context.startActivity(intent)
//                                val args = Bundle()
//                                args.putString(Const.FRAG_TYPE, Constants.StudyType.TESTS)
//                                (context as HomeActivity).navController.navigate(R.id.studyFragment,args )
                            } else if (item.course_type == "3") {
                                val intent = Intent(context, HomeActivity::class.java)
                                intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.QBANKS)
                                context.startActivity(intent)
//                                val args = Bundle()
//                                args.putString(Const.FRAG_TYPE, Constants.StudyType.QBANKS)
//                                (context as HomeActivity).navController.navigate(R.id.studyFragment,args )
                            } else {
                                val intent = Intent(context, HomeActivity::class.java)
                                intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.QBANKS)
                                context.startActivity(intent)
                                //(context as HomeActivity).navController.navigate(R.id.studyFragment )
                            }
                        } else {
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.QBANKS)
                            context.startActivity(intent)
                            //(context as HomeActivity).navController.navigate(R.id.studyFragment )
                        }
                       /* val intent = Intent(context, CourseActivity::class.java)
                        intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                        intent.putExtra(Const.COURSES, item)
                        context.startActivity(intent)*/
                    } else {
                        val intent = Intent(context, CourseDetailActivity::class.java)
                        intent.putExtra(Const.COURSE, currentCourseItem)
                        intent.putExtra(Const.FRAG_TYPE, Const.LIVE_CLASSES)
                        context.startActivity(intent)
                    }
                }

                tvCourseName.text = currentCourseItem.title
                courseRating.rating = currentCourseItem.rating.toFloat()
                tvCourseRating.text = currentCourseItem.rating

                className.text = "Next Class: ${currentCourseItem.video_title}"

                val resultDate = Helper.getTime(currentCourseItem.live_on.toLong())
                time.text = "Time: $resultDate"

                val resultDate1 = Helper.getFormattedDate(currentCourseItem.live_on.toLong())
                date.text = "Date: $resultDate1"

//                currentCourseItem.is_remind_opted = false
                val date = Date(currentCourseItem.current_timestamp.toLong())
                val liveDate = Date(currentCourseItem.live_on.toLong() * 1000)
                val difference = (liveDate.time - date.time)
                val isOpted = currentCourseItem.is_remind_opted // that means Remind me is not opted Yet

                if (!isOpted) {
                    remindText.visibility = View.GONE
                    if (difference <= 172800000) {  // if hours is less than 48 hours
                        remindMe.visibility = View.VISIBLE
                    } else {
                        remindMe.visibility = View.GONE
                    }
                } else {
                    remindMe.visibility = View.GONE
                    remindText.visibility = View.VISIBLE

                    synchronized(lstHolders) {
                        lstHolders.add(holder)
                    }
                    holder.setData(currentCourseItem)

                }



                remindMe.tag = currentCourseItem
                remindMe.setOnClickListener {
                    val item = it.tag as Course
                    customDialog(context.getString(R.string.app_name), context.getString(R.string.remind_me), item)
                }


            }

        } catch (e: Exception) {

        }
    }


    private fun customDialog(title: String, message: String, item: Course) {
        val v = Helper.newCustomDialog(context, false, title, message)
        val btnCancel: Button
        val btnSubmit: Button
        btnCancel = v.findViewById(R.id.btn_cancel)
        btnSubmit = v.findViewById(R.id.btn_submit)
        btnCancel.text = context.getString(R.string.no)
        btnSubmit.text = context.getString(R.string.yes)
        btnCancel.setOnClickListener { view: View? -> Helper.dismissDialog() }
        btnSubmit.setOnClickListener { view: View? ->
            Helper.dismissDialog()
            networkCallForRemindMe(item)
        }
    }


    private fun networkCallForRemindMe(item: Course) {
        val apiInterface = ApiClient.createService(LiveCourseInterface::class.java)
        val response = apiInterface.remindMeForLiveClass(SharedPreference.getInstance().loggedInUser.id, item.id, item.video_id, item.live_on)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            val dataObject = jsonResponse.optJSONObject(Const.DATA)
                            item.is_remind_opted = true
                            item.current_timestamp = dataObject.optString("creation_time")
                            notifyDataSetChanged()
                            Toast.makeText(context, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                        } else {
                            RetrofitResponse.getApiData(context, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
            }
        })

    }

    init {
        startUpdateTimer()
    }

    private fun startUpdateTimer() {
        val tmr = Timer()
        tmr.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post(updateRemainingTimeRunnable)
            }
        }, 1000, 1000)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveCoursesItemUpcomingCoursesViewHolder {
        val binding = LiveCoursesUpcomingClassesBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return LiveCoursesItemUpcomingCoursesViewHolder(binding)
    }


    inner class LiveCoursesItemUpcomingCoursesViewHolder(val binding: LiveCoursesUpcomingClassesBinding)
        : RecyclerView.ViewHolder(binding.root) {

        lateinit var currentCourseItem: Course

        fun setData(item: Course) {
            currentCourseItem = item
            updateTimeRemaining()
        }

        fun updateTimeRemaining() {
            val date = Date(currentCourseItem.current_timestamp.toLong())
            val liveDate = Date(currentCourseItem.live_on.toLong() * 1000)
            val timeDiff = (liveDate.time - date.time)
            if (timeDiff > 0) {
                val seconds = (timeDiff / 1000).toInt() % 60
                val minutes = (timeDiff / (1000 * 60) % 60).toInt()
                val hours = (timeDiff / (1000 * 60 * 60) % 24).toInt()
                val days = (timeDiff / (60 * 60 * 24 * 1000)).toInt()
                if (days == 0) {
                    binding.remindText.text = " $hours h $minutes m $seconds s"
                    if (hours == 0)
                        binding.remindText.text = " $minutes m $seconds s"
                } else
                    binding.remindText.text = " $days d $hours h $minutes m $seconds s"
            } else {
                binding.remindText.text = ""
            }
            currentCourseItem.current_timestamp = (currentCourseItem.current_timestamp.toLong() + 1000).toString()
        }
    }

    override fun getItemCount() = upcomingCourseList.size


}