package com.emedicoz.app.templateAdapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.databinding.RecordedDetailScreenImageItemBinding
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.JsonObject
import com.smarteist.autoimageslider.SliderViewAdapter
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RecordedDetailScreenImageBannerAdapter(private var courseList: MutableList<Course>, private val context: Context)
    : SliderViewAdapter<RecordedDetailScreenImageBannerAdapter.RecordedDetailScreenImageBannerViewHolder>() {


    override fun onBindViewHolder(holder: RecordedDetailScreenImageBannerViewHolder, position: Int) {
        val currentCourseItem: Course = courseList[position]


        try {
            holder.binding.detailCourse.tag = currentCourseItem
            holder.binding.detailCourse.setOnClickListener {
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
                            (context as HomeActivity).navController.navigate(R.id.studyFragment )
                        }
                    } else {
                        (context as HomeActivity).navController.navigate(R.id.studyFragment )
                    }
                    /*val intent = Intent(context, CourseActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                    intent.putExtra(Const.COURSES, item)
                    context.startActivity(intent)*/
                } else {
                    val intent = Intent(context, CourseDetailActivity::class.java)
                    intent.putExtra(Const.COURSE, item)
                    intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                    context.startActivity(intent)
                }
            }


            Glide.with(context)
                    .load(currentCourseItem.cover_image)
                    .apply(RequestOptions.placeholderOf(R.mipmap.courses_blue).error(R.mipmap.courses_blue))
                    .into(holder.binding.courseImage)

            setSpannable(holder.binding.coursePrice, currentCourseItem)

            if (!currentCourseItem.calMrp.equals("free", true)) {
                holder.binding.coursePrice.visibility = View.VISIBLE
            } else {
                holder.binding.coursePrice.visibility = View.GONE
            }

            holder.binding.courseRating.rating = currentCourseItem.rating.toFloat()
            holder.binding.tvCourseName.text = currentCourseItem.title
            holder.binding.tvCourseRating.text = currentCourseItem.rating
            holder.binding.courseEnrolled.text = "${currentCourseItem.learner} Enrolled"


            holder.binding.courseImage.setOnClickListener {

            }
            if (currentCourseItem.isIs_wishlist)
                holder.binding.imgWishlist.setImageResource(R.drawable.wishlist_selected)
            else
                holder.binding.imgWishlist.setImageResource(R.drawable.wishlist_unselected)

            holder.binding.imgWishlist.tag = currentCourseItem
            holder.binding.imgWishlist.setOnClickListener {
                val item = it.tag as Course
                if (!item.isIs_wishlist) {
                    addToWishList(item)
                    holder.binding.imgWishlist.setImageResource(R.drawable.wishlist_selected)
                } else {
                    removeFromWishList(item)
                    holder.binding.imgWishlist.setImageResource(R.drawable.wishlist_unselected)

                }
            }

            holder.binding.totalHours.text = "0 Total Hours"
            holder.binding.totalHours.visibility = View.GONE

            val sdf = SimpleDateFormat("MM|yyyy")
            val resultDate = Date(currentCourseItem.last_updated.toLong())
            holder.binding.lastUpdated.text = "Updated on ${sdf.format(resultDate)}"

            if (currentCourseItem.isIs_purchased) {
                holder.binding.imgWishlist.visibility = View.GONE
            } else {
                holder.binding.imgWishlist.visibility = View.VISIBLE
            }


        } catch (e: Exception) {

        }

    }

    private fun setSpannable(priceTV: TextView, courseItem: Course) {
        if (courseItem.isDiscounted) {
            priceTV.setText(courseItem.calMrp, TextView.BufferType.SPANNABLE)
            val spannable = priceTV.text as Spannable
            spannable.setSpan(StrikethroughSpan(), 2, Helper.calculatePriceBasedOnCurrency(courseItem.mrp).length + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            priceTV.text = courseItem.calMrp
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup): RecordedDetailScreenImageBannerViewHolder {
        val binding = RecordedDetailScreenImageItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordedDetailScreenImageBannerViewHolder(binding)
    }

    override fun getCount() = courseList.size

    inner class RecordedDetailScreenImageBannerViewHolder(val binding: RecordedDetailScreenImageItemBinding)
        : SliderViewAdapter.ViewHolder(binding.root)


    private fun addToWishList(item: Course) {
        // asynchronous operation to add course to wishlist.
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.addCourseToWishlist(SharedPreference.getInstance().loggedInUser.id, item.id)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            item.isIs_wishlist = true

                            notifyDataSetChanged()
                            Toast.makeText(context, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                        } else {
                            Helper.showErrorLayoutForNav("addToWishList", context as Activity?, 1, 0)
                            RetrofitResponse.getApiData(context, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav("addToWishList", context as Activity?, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Helper.showErrorLayoutForNav("removeFromWishList", context as Activity?, 1, 1)

            }
        })

    }

    private fun removeFromWishList(item: Course) {
        // asynchronous operation to remove course to wishlist.
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.removeCourseToWishlist(SharedPreference.getInstance().loggedInUser.id, item.id)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            item.isIs_wishlist = false
                            notifyDataSetChanged()
                            Toast.makeText(context, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                        } else {
                            Helper.showErrorLayoutForNav("removeFromWishList", context as Activity?, 1, 0)
                            RetrofitResponse.getApiData(context, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav("removeFromWishList", context as Activity?, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Helper.showErrorLayoutForNav("removeFromWishList", context as Activity?, 1, 1)

            }
        })

    }
}