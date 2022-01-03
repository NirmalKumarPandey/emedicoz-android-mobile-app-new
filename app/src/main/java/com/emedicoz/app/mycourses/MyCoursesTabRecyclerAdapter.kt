package com.emedicoz.app.mycourses

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.MyCoursesRecordedTabItemBinding
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.utilso.*
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCoursesTabRecyclerAdapter(private val coursesTabList: ArrayList<Course>, private val context: Activity)
    : RecyclerView.Adapter<MyCoursesTabRecyclerAdapter.TabRecyclerViewHolder>() {
    private lateinit var progress: Progress


    override fun onBindViewHolder(holder: MyCoursesTabRecyclerAdapter.TabRecyclerViewHolder, position: Int) {
        val currentCourseItem: Course = coursesTabList[position]

        holder.binding.apply {
            tvCourseName.text = currentCourseItem.title
            CourseExpiry.text = "Expiry on: ${currentCourseItem.end_date}"

            detailCourse.tag = currentCourseItem
            detailCourse.setOnClickListener {
                var item = it.tag as Course

                if (Helper.isTestOrQbankCourse(item)) {
                    val intent = Intent(context, CourseActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                    intent.putExtra(Const.COURSES, item)
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, CourseDetailActivity::class.java)
                    intent.putExtra(Const.COURSE, item)
                    intent.putExtra(Const.FRAG_TYPE, Const.MYCOURSES)
                    context.startActivity(intent)
                }
            }

            Glide.with(context)
                    .load(currentCourseItem.cover_image)
                    .apply(RequestOptions.placeholderOf(R.mipmap.courses_blue).error(R.mipmap.courses_blue))
                    .into(courseImage)

            renewBtn.tag = currentCourseItem
            if (currentCourseItem.isIs_renew == "1") {
                renewBtn.visibility = View.VISIBLE
            } else {
                renewBtn.visibility = View.INVISIBLE
            }

            renewBtn.tag = currentCourseItem
            renewBtn.setOnClickListener {

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
                } else {
                    networkCallForFreeCourseTransaction(item)
                }

            }
            val progressVal = currentCourseItem.course_completion_percentage.toInt()

            if (GenericUtils.isEmpty(currentCourseItem.end_date) || currentCourseItem.end_date == null || currentCourseItem.end_date == "0000-00-00") {
                CourseExpiry.visibility = View.GONE
            } else {
                CourseExpiry.visibility = View.VISIBLE
            }

//            if(progressVal == 0){
//                seekBar.visibility = View.GONE
//                courseComplete.visibility = View.GONE
//            }else{
//                seekBar.visibility = View.VISIBLE
//                courseComplete.visibility = View.VISIBLE
//            }

            courseComplete.text = "$progressVal% Completed"

            seekBar.progress = progressVal

            seekBar.tag = currentCourseItem
            root.tag = currentCourseItem
            root.setOnClickListener {
                var item = it.tag as Course
                if (Helper.isTestOrQbankCourse(item)) {
                    val intent = Intent(context, CourseActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                    intent.putExtra(Const.COURSES, item)
                    context.startActivity(intent)
                } else {
                    val intent1 = Intent(context, CourseDetailActivity::class.java)
                    intent1.putExtra(Const.COURSE, item)
                    intent1.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                    context.startActivity(intent1)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabRecyclerViewHolder {
        val binding = MyCoursesRecordedTabItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return TabRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return coursesTabList.size
    }

    inner class TabRecyclerViewHolder(val binding: MyCoursesRecordedTabItemBinding)
        : RecyclerView.ViewHolder(binding.root)

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
