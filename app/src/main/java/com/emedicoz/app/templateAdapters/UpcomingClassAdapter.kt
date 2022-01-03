package com.emedicoz.app.templateAdapters

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.legacy.widget.Space
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.courses.activity.LiveCourseActivity
import com.emedicoz.app.databinding.SingleItemUpcomingClassBinding
import com.emedicoz.app.modelo.UpcomingCourseData
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import java.io.Serializable
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UpcomingClassAdapter(
    private val context: Context,
    private val upcomingClassList: ArrayList<UpcomingCourseData>
) : RecyclerView.Adapter<UpcomingClassAdapter.UpcomingClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingClassViewHolder {
        val binding: SingleItemUpcomingClassBinding = SingleItemUpcomingClassBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return UpcomingClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingClassViewHolder, position: Int) {
        holder.singleItemUpcomingClassBinding.courseName.text =
            upcomingClassList[position].courseName
        holder.singleItemUpcomingClassBinding.videoName.text =
            upcomingClassList[position].videoTitle
        Log.e("TAG", "onBindViewHolder: ${upcomingClassList[position].thumbnailUrl}")
        Glide.with(context).load(upcomingClassList[position].thumbnailUrl).apply(
            RequestOptions()
                .placeholder(R.drawable.landscape_placeholder)
                .error(R.drawable.landscape_placeholder)
        ).into(holder.singleItemUpcomingClassBinding.imageView)

        if (!upcomingClassList[position].liveOn.equals("")) {
            holder.singleItemUpcomingClassBinding.upcomingDateTV.visibility = View.VISIBLE
            val timestamp = upcomingClassList[position].liveOn.toLong() * 1000
            val date = Date(timestamp)
            val dateString: String = SimpleDateFormat("dd/MM/yyyy hh:mma").format(date)
            Log.e("TAG", "onBindViewHolder: $dateString")

            holder.singleItemUpcomingClassBinding.upcomingDateTV.text =
                "Upcoming\n${dateString.split(" ")[1]}\n${dateString.split(" ")[0]}"

        }

        holder.singleItemUpcomingClassBinding.parentClassRL.setOnClickListener {
            if (upcomingClassList[position].isPurchased) {
                checkDate(position)
            } else {
                if (upcomingClassList[position].mrp.equals("0")) {
                    checkDate(position)
                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                        if (upcomingClassList[position].forDams == "0")
                            checkDate(position)
                        else
                            testQuizCourseDialog(
                                context.getString(R.string.alert),
                                context.getString(R.string.purchase_this_course)
                            )
                    } else {
                        if (upcomingClassList[position].nonDams == "0")
                            checkDate(position)
                        else
                            testQuizCourseDialog(
                                context.getString(R.string.alert),
                                context.getString(R.string.purchase_this_course)
                            )
                    }
                }
            }
        }
    }

    private fun checkDate(position: Int) {
        val date1 = Date(
            GenericUtils.getParsableString(upcomingClassList[position].liveOn)
                .toLong() * 1000
        )
        val simpleDateFormat = SimpleDateFormat("E, LLL dd, yyyy KK:mm aaa")
        val dateTime = simpleDateFormat.format(date1).toString()
        Log.e("DATE_TIME", dateTime)

        testQuizCourseDialog(
            context.getString(R.string.app_name),
            "This video will be live on $dateTime"
        )
    }

    override fun getItemCount(): Int {
        return upcomingClassList.size
    }

    inner class UpcomingClassViewHolder(var singleItemUpcomingClassBinding: SingleItemUpcomingClassBinding) :
        RecyclerView.ViewHolder(singleItemUpcomingClassBinding.root)

    private fun testQuizCourseDialog(title: String, message: String) {
        val v = Helper.newCustomDialog(
            context, true, title,
            message
        )

        val space: Space = v.findViewById(R.id.space)
        val btnCancel: Button = v.findViewById(R.id.btn_cancel)
        val btnSubmit: Button = v.findViewById(R.id.btn_submit)
        space.visibility = View.GONE
        btnCancel.visibility = View.GONE
        btnSubmit.setOnClickListener {
            Helper.dismissDialog()
        }
    }

}