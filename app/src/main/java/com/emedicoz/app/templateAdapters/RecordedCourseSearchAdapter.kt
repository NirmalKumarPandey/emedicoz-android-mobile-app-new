package com.emedicoz.app.templateAdapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.RecordCoursesSearchListItemBinding
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.Const.COURSE
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RecordedCourseSearchAdapter(private val trendingCourseList: ArrayList<Course>, private val context: Context, private val isWishlist: Boolean) : RecyclerView.Adapter<RecordedCourseSearchAdapter.RecordCoursesSearchItemViewHolder>() {
    private lateinit var progress: Progress

    override fun onBindViewHolder(holder: RecordCoursesSearchItemViewHolder, position: Int) {
        val currentCourseItem: Course = trendingCourseList[position]

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
                   /* val intent = Intent(context, CourseActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                    intent.putExtra(Const.COURSES, item)
                    context.startActivity(intent)*/
                } else {
                    val intent = Intent(context, CourseDetailActivity::class.java)
                    intent.putExtra(COURSE, item)
                    intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES)
                    context.startActivity(intent)
                }
            }

            Glide.with(context)
                    .load(currentCourseItem.cover_image)
                    .into(holder.binding.courseImage)
            if (!GenericUtils.isEmpty(currentCourseItem.category_tag)) {
                holder.binding.tvCourseCategory.text = currentCourseItem.category_tag
                holder.binding.tvCourseCategory.visibility = View.VISIBLE
            } else{
                holder.binding.tvCourseCategory.visibility = View.GONE
                if (!GenericUtils.isEmpty(currentCourseItem.course_tag)) {
                    holder.binding.tvCourseCategory.text = currentCourseItem.course_tag
                    holder.binding.tvCourseCategory.visibility = View.VISIBLE
                }
            }
            setSpannable(holder.binding.coursePrice, currentCourseItem)

            if (!currentCourseItem.calMrp.equals("free", true)) {
                holder.binding.tvCourseFeeType.text = context.getString(R.string.paid)
                holder.binding.tvCourseFeeType.setBackgroundResource(R.drawable.background_red)
                holder.binding.coursePrice.visibility = View.VISIBLE
            } else {
                holder.binding.tvCourseFeeType.setBackgroundResource(R.drawable.background_green)
                holder.binding.tvCourseFeeType.text = context.getString(R.string.free)
                holder.binding.coursePrice.visibility = View.GONE
            }

            if (currentCourseItem.isFreeTrial) {
                holder.binding.tvCourseFeeType.setBackgroundResource(R.drawable.background_green)
                holder.binding.tvCourseFeeType.text = context.getString(R.string.free_trial)
            }

            holder.binding.courseRating.rating = currentCourseItem.rating.toFloat()
            holder.binding.tvCourseName.text = currentCourseItem.title
            holder.binding.tvCourseRating.text = currentCourseItem.rating
            holder.binding.courseEnrolled.text = context.getString(R.string.enrolled_text, currentCourseItem.learner)


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
                    removeFromWishList(item, isWishlist)
                    holder.binding.imgWishlist.setImageResource(R.drawable.wishlist_unselected)

                }
            }
            Helper.setEnrolAndWishListBackground(context, currentCourseItem, holder.binding.imgWishlist, holder.binding.enrollBtn)


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

    private fun removeFromWishList(item: Course, isWishlist: Boolean?) {
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
                            if (!isWishlist!!)
                                item.isIs_wishlist = false
                            else
                                trendingCourseList.remove(item)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordCoursesSearchItemViewHolder {
        val binding = RecordCoursesSearchListItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordCoursesSearchItemViewHolder(binding)
    }


    inner class RecordCoursesSearchItemViewHolder(val binding: RecordCoursesSearchListItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = trendingCourseList.size
}