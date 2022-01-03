package com.emedicoz.app.templateAdapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.LiveCoursesItemMyCoursesBinding
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
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

class LiveCoursesMyCourseAdapter(private val myCourseList: ArrayList<Course>, private val context: Context)
    : RecyclerView.Adapter<LiveCoursesMyCourseAdapter.LiveCoursesItemMyCoursesViewHolder>() {

    private lateinit var progress: Progress


    override fun onBindViewHolder(holder: LiveCoursesItemMyCoursesViewHolder, position: Int) {
        val currentCourseItem: Course = myCourseList[position]
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
//                                (context as HomeActivity).navController.navigate(R.id.studyFragment )
                            }
                        } else {
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.putExtra(Const.FRAG_TYPE, Constants.StudyType.QBANKS)
                            context.startActivity(intent)
                            //(context as HomeActivity).navController.navigate(R.id.studyFragment )
                        }
                        /*val intent = Intent(context, CourseActivity::class.java)
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


                tvCourseName.text = currentCourseItem.title

                courseEnrolled.text = context.getString(R.string.enrolled_text, currentCourseItem.learner)
                if (currentCourseItem.learner == null) {
                    courseEnrolled.text = "0 Enrolled"
                }
                courseRating.rating = currentCourseItem.rating.toFloat()
                tvCourseRating.text = currentCourseItem.rating

                if (currentCourseItem.isIs_streaming) {
                    liveText.visibility = View.VISIBLE
                } else {
                    liveText.visibility = View.GONE
                }

                Helper.setEnrollBackground(context, currentCourseItem, holder.binding.enrollBtn)


                holder.binding.enrollBtn.tag = currentCourseItem
                holder.binding.enrollBtn.setOnClickListener {
                    val item = it.tag as Course

                    if (currentCourseItem.isIs_renew == "1" || !currentCourseItem.isIs_purchased && currentCourseItem.mrp != "0") {
                        if (item.mrp == "0") {
                            networkCallForFreeCourseTransaction(item)
                        } else {

                            if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                                if (currentCourseItem.for_dams != "0")
                                    Helper.goToCourseInvoiceScreen(context, Helper.getData(currentCourseItem))
                                else
                                    networkCallForFreeCourseTransaction(item)

                            } else {
                                if (currentCourseItem.non_dams != "0")
                                    Helper.goToCourseInvoiceScreen(context, Helper.getData(currentCourseItem))
                                else
                                    networkCallForFreeCourseTransaction(item)
                            }
                        }
                    } else if (currentCourseItem.isIs_purchased) {
                        return@setOnClickListener
                    }
                    else {
                        networkCallForFreeCourseTransaction(item)
                    }
                }

                imgShare.setOnClickListener {
                }
                imgShare.tag = currentCourseItem
                imgShare.setOnClickListener {
                    var item = it.tag as Course
                    Helper.openShareDialogLiveCourse(context, item)
                }

            }

        } catch (e: Exception) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveCoursesItemMyCoursesViewHolder {
        val binding = LiveCoursesItemMyCoursesBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return LiveCoursesItemMyCoursesViewHolder(binding)
    }

    inner class LiveCoursesItemMyCoursesViewHolder(val binding: LiveCoursesItemMyCoursesBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = myCourseList.size


    private fun networkCallForFreeCourseTransaction(item: Course) {

        progress = Progress(context)
        progress.setCancelable(false)
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.makeFreeCourseTransaction(SharedPreference.getInstance().loggedInUser.id,
                item.points_conversion_rate, "0", "", "", item.id, "0")
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            item.isIs_purchased = true
                            item.isIs_renew = "0"
                            notifyDataSetChanged()
                            Toast.makeText(context, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                        } else {
                            Helper.showErrorLayoutForNav("networkCallForFreeCourseTransaction", context as Activity?, 1, 0)
                            RetrofitResponse.getApiData(context, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav("networkCallForFreeCourseTransaction", context as Activity?, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav("networkCallForFreeCourseTransaction", context as Activity?, 1, 1)
            }
        })
    }


}