package com.emedicoz.app.cart

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.courses.activity.CourseActivity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.MyCartListItemBinding
import com.emedicoz.app.installment.model.Installment
import com.emedicoz.app.modelo.courses.Course
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Const.COURSE
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*

class MyCartListAdapter(private var courseList: ArrayList<Course>, private var context: Context, private var gst: String, private val myCartInterface: MyCartInterface)
    : RecyclerView.Adapter<MyCartListAdapter.MyCartListViewHolder>() {
    private lateinit var progress: Progress
    private var gson = Gson()
    private val df2 = DecimalFormat("#.##")


    override fun onBindViewHolder(holder: MyCartListViewHolder, position: Int) {
        val currentCourseItem: Course = courseList[position]
        progress = Progress(context)
        progress.setCancelable(false)

        try {

            Glide.with(context)
                    .load(currentCourseItem.cover_image)
                    .apply(RequestOptions.placeholderOf(R.mipmap.courses_blue).error(R.mipmap.courses_blue))
                    .into(holder.binding.imageIV)


            holder.binding.ratingRB.rating = currentCourseItem.rating.toFloat()
            holder.binding.coursenameTV.text = currentCourseItem.title
            holder.binding.ratingTV.text = currentCourseItem.rating
            holder.binding.txtLearnerValue.text =  currentCourseItem.learner

            if(courseList.get(position).is_subscription.equals("0")){

                if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)){
                    holder.binding.courseMrp.text = String.format("%s %s", Helper.getCurrencySymbol(), currentCourseItem.for_dams)
                }
                else {
                    holder.binding.courseMrp.text = String.format("%s %s", Helper.getCurrencySymbol(), currentCourseItem.non_dams)
                }

            }

            holder.binding.delete.tag = currentCourseItem
            holder.binding.delete.setOnClickListener {
                openDeleteDialog(context, it.tag as Course)
                // removeFromCart(it.tag as Course)
            }

            holder.binding.coursenameTV.setOnClickListener {
                holder.binding.coursenameTV.tag = currentCourseItem
                var item = it.tag as Course
                if (Helper.isTestOrQbankCourse(item)) {
                    val intent = Intent(context, CourseActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                    intent.putExtra(Const.COURSES, item)
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, CourseDetailActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, "CART")
                    intent.putExtra(COURSE, item)
                    context.startActivity(intent)
                }
            }

            holder.binding.imageIV.setOnClickListener {
                holder.binding.imageIV.tag = currentCourseItem
                var item = it.tag as Course
                if (Helper.isTestOrQbankCourse(item)) {
                    val intent = Intent(context, CourseActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.QBANK_COURSE)
                    intent.putExtra(Const.COURSES, item)
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, CourseDetailActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, "CART")
                    intent.putExtra(COURSE, item)
                    context.startActivity(intent)
                }
            }

            if(courseList.get(position).is_subscription.equals("1") && courseList.get(position).subscription_data != null){

                holder.binding.subscriptionDetails.visibility = View.VISIBLE
                var installmentData = gson.fromJson(gson.toJson(courseList.get(position).subscription_data), Installment::class.java)
                holder.binding.tvName.text = installmentData.name


                var coursePrice = installmentData.amount_description.payment.get(0)
                var month: Int = Integer.parseInt(installmentData.name.split(" ").get(0))

                holder.binding.courseMrp.text = String.format("%s %s", Helper.getCurrencySymbol(), coursePrice)

                if (TextUtils.isEmpty(gst) || gst == "0") {
                    holder.binding.totalAmount.text = String.format("%s %s", Helper.getCurrencySymbol(), coursePrice)
                    holder.binding.perMonthAmount.text = String.format("%s %s", Helper.getCurrencySymbol(), df2.format(coursePrice.toFloat() / month))
                } else {
                    holder.binding.totalAmount.text = String.format("%s %s", Helper.getCurrencySymbol(), coursePrice.toInt() + coursePrice.toInt() * gst.toInt() / 100)
                    holder.binding.perMonthAmount.text = String.format("%s %s", Helper.getCurrencySymbol(), df2.format((coursePrice.toFloat() + coursePrice.toFloat() * gst.toFloat() / 100) / month))
                }

            }
            else {
                holder.binding.subscriptionDetails.visibility = View.GONE
            }


        } catch (e: Exception) {

        }
    }


    private fun removeFromCart(item: Course) {
        // asynchronous operation to remove course from cart.
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.removeCourseFromCart(SharedPreference.getInstance().loggedInUser.id, item.id)
        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            courseList.remove(item)
                            notifyDataSetChanged()
                            Toast.makeText(context, jsonResponse.optString(Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show()
                            myCartInterface.onClickDelete()
                        } else {
                            Helper.showErrorLayoutForNav("removeFromCart", context as Activity?, 1, 0)
                            RetrofitResponse.getApiData(context as Activity?, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav("removeFromCart", context as Activity?, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav("removeFromCart", context as Activity?, 1, 1)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartListViewHolder {
        val binding = MyCartListItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return MyCartListViewHolder(binding)
    }


    inner class MyCartListViewHolder(val binding: MyCartListItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = courseList.size

    fun onNotifyDataSetChange(courseList: ArrayList<Course>, context: Context) {

        this.courseList = courseList
        this.context = context
        notifyDataSetChanged()
    }

    fun openDeleteDialog(ctx: Context, item: Course) {

        val alertBuild = AlertDialog.Builder(ctx)
        alertBuild
                .setTitle(ctx.getString(R.string.cart_delete_title))
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, whichButton -> removeFromCart(item) }
                .setNegativeButton("No") { dialog, whichButton ->
                    dialog.dismiss()
                }
        val dialog = alertBuild.create()
        try {
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val alertTitle = ctx.resources.getIdentifier("alertTitle", Constants.Extras.ID, "android")
        (dialog.findViewById<View>(alertTitle) as TextView).gravity = Gravity.CENTER
        (dialog.findViewById<View>(alertTitle) as TextView).gravity = Gravity.CENTER
    }
}