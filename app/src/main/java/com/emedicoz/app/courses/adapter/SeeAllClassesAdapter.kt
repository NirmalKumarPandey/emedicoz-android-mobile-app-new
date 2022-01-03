package com.emedicoz.app.courses.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.legacy.widget.Space
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.courses.activity.LiveCourseActivity
import com.emedicoz.app.databinding.RecordCoursesListItemTrendingBinding
import com.emedicoz.app.databinding.SingleItemSeeAllClassesBinding
import com.emedicoz.app.modelo.UpcomingCourseData
import com.emedicoz.app.modelo.liveclass.LiveClassVideoList
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

class SeeAllClassesAdapter(
    var context: Context,
    var upcomingCourseList: ArrayList<UpcomingCourseData>,
    var classType: String
) : RecyclerView.Adapter<SeeAllClassesAdapter.SeeAllClassesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeeAllClassesViewHolder {
        val binding = SingleItemSeeAllClassesBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return SeeAllClassesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeeAllClassesViewHolder, position: Int) {
        holder.singleItemSeeAllClassesBinding.ibtSingleSubVdTvTitle.text = upcomingCourseList[position].videoTitle
        holder.singleItemSeeAllClassesBinding.ibtSingleSubVdTvDes.text = upcomingCourseList[position].courseName
        Glide.with(context).load(upcomingCourseList[position].thumbnailUrl)
            .apply(RequestOptions().placeholder(R.drawable.landscape_placeholder).error(R.drawable.landscape_placeholder))
            .into(holder.singleItemSeeAllClassesBinding.ibtSingleSubVdIv)
        var correctDateFormat: String = ""
        if (classType == Const.UPCOMING) {
            holder.singleItemSeeAllClassesBinding.liveIV.visibility = View.GONE
            holder.singleItemSeeAllClassesBinding.liveOnLL.visibility = View.VISIBLE
            val d = Date(GenericUtils.getParsableString(upcomingCourseList[position].liveOn).toLong() * 1000)
            val f: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm aa")
            val dateString = f.format(d)
            var date: Date? = null
            try {
                date = f.parse(f.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val amPM = dateString.split("\\s+".toRegex()).toTypedArray()

            val fullDate = date.toString().split("\\s+".toRegex()).toTypedArray()
            correctDateFormat = GenericUtils.getFormattedDate(fullDate, amPM)
            holder.singleItemSeeAllClassesBinding.tvLiveonDate.text = correctDateFormat
        } else {
            if (upcomingCourseList[position].isLive.equals("1")) {
                holder.singleItemSeeAllClassesBinding.liveIV.visibility = View.VISIBLE
                Glide.with(context).asGif().load(R.drawable.live_gif)
                    .into(holder.singleItemSeeAllClassesBinding.liveIV)
            } else {
                holder.singleItemSeeAllClassesBinding.liveIV.visibility = View.GONE
            }
            holder.singleItemSeeAllClassesBinding.liveOnLL.visibility = View.GONE
        }
        holder.singleItemSeeAllClassesBinding.ibtSingleSubVdRL.setOnClickListener {
            if (upcomingCourseList[position].isPurchased) {
                if (classType == Const.UPCOMING) {
                    testQuizCourseDialog(
                        context.getString(R.string.app_name),
                        "This video will be live on $correctDateFormat"
                    )
                } else {
                    goToLiveCourseActivity(position)
                }
            } else {
                if (upcomingCourseList[position].mrp.equals("0")) {
                    goToLiveCourseActivity(position)
                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                        if (upcomingCourseList[position].forDams == "0")
                            goToLiveCourseActivity(position)
                        else
                            testQuizCourseDialog(
                                context.getString(R.string.alert),
                                context.getString(R.string.purchase_this_course)
                            )
                    } else {
                        if (upcomingCourseList[position].nonDams == "0")
                            goToLiveCourseActivity(position)
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

    override fun getItemCount(): Int {
        return upcomingCourseList.size
    }

    private fun goToLiveCourseActivity(position: Int) {
        val intent = Intent(
            context,
            LiveCourseActivity::class.java
        )
        intent.putExtra(Const.VIDEO_LINK, upcomingCourseList[position].fileType)
        intent.putExtra(
            Const.VIDEO,
            getLiveClassVideoList(upcomingCourseList[position]) as Serializable?
        )
        context.startActivity(intent)
    }

    inner class SeeAllClassesViewHolder(var singleItemSeeAllClassesBinding: SingleItemSeeAllClassesBinding) :

        RecyclerView.ViewHolder(singleItemSeeAllClassesBinding.root)


    private fun testQuizCourseDialog(title: String, message: String) {
        val v = Helper.newCustomDialog(context, true, title, message)

        val space: Space = v.findViewById(R.id.space)
        val btnCancel: Button = v.findViewById(R.id.btn_cancel)
        val btnSubmit: Button = v.findViewById(R.id.btn_submit)
        space.visibility = View.GONE
        btnCancel.visibility = View.GONE
        btnSubmit.setOnClickListener {
            Helper.dismissDialog()
        }
    }

    private fun getLiveClassVideoList(ongoingCourseData: UpcomingCourseData): LiveClassVideoList {
        val liveClassVideoList = LiveClassVideoList()
        liveClassVideoList.id = ongoingCourseData.id
        liveClassVideoList.videoTitle = ongoingCourseData.videoTitle
        liveClassVideoList.chatNode = ongoingCourseData.chatNode
        liveClassVideoList.chatPlatform = ongoingCourseData.chatPlatform
        liveClassVideoList.description = ongoingCourseData.description
        liveClassVideoList.encUrl = ongoingCourseData.encUrl
        liveClassVideoList.fileType = ongoingCourseData.fileType
        liveClassVideoList.fileUrl = ongoingCourseData.fileUrl
        liveClassVideoList.isLive = ongoingCourseData.isLive
        liveClassVideoList.isLocked = ongoingCourseData.isLocked
        liveClassVideoList.isVod = ongoingCourseData.isVod
        liveClassVideoList.liveOn = ongoingCourseData.liveOn
        liveClassVideoList.liveStatus = ongoingCourseData.liveStatus
        liveClassVideoList.liveUrl = ongoingCourseData.fileUrl
        liveClassVideoList.token = ongoingCourseData.token
        liveClassVideoList.videoType = ongoingCourseData.videoType
        return liveClassVideoList
    }
}