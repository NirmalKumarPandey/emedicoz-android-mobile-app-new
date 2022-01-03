package com.emedicoz.app.templateAdapters

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
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
import com.emedicoz.app.databinding.SingleItemUpcomingClassBinding
import com.emedicoz.app.modelo.UpcomingCourseData
import com.emedicoz.app.modelo.liveclass.LiveClassVideoList
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import java.io.Serializable

class OngoingAdapter(
    private val context: Context,
    private val upcomingClassList: ArrayList<UpcomingCourseData>
) : RecyclerView.Adapter<OngoingAdapter.OngoingClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingClassViewHolder {
        val binding: SingleItemUpcomingClassBinding = SingleItemUpcomingClassBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return OngoingClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OngoingClassViewHolder, position: Int) {
        // displaying course name
        holder.singleItemUpcomingClassBinding.courseName.text =
            upcomingClassList[position].courseName

        // displaying video title
        holder.singleItemUpcomingClassBinding.videoName.text =
            upcomingClassList[position].videoTitle
        Log.e("TAG", "onBindViewHolder: ${upcomingClassList[position].thumbnailUrl}")

        // displaying image using Glide
        Glide.with(context).load(upcomingClassList[position].thumbnailUrl).apply(
            RequestOptions()
                .placeholder(R.drawable.landscape_placeholder)
                .error(R.drawable.landscape_placeholder)
        ).into(holder.singleItemUpcomingClassBinding.imageView)

        // condition for displaying live blink icon
        if (upcomingClassList[position].isLive.equals("1")) {
            holder.singleItemUpcomingClassBinding.liveIV.visibility = View.VISIBLE
            Glide.with(context).asGif().load(R.drawable.live_gif)
                .into(holder.singleItemUpcomingClassBinding.liveIV)
        } else {
            holder.singleItemUpcomingClassBinding.liveIV.visibility = View.GONE
        }

        holder.singleItemUpcomingClassBinding.parentClassRL.setOnClickListener {
            if (upcomingClassList[position].isPurchased) {
                goToLiveCourseActivity(position)
            } else {
                if (upcomingClassList[position].mrp.equals("0")) {
                    goToLiveCourseActivity(position)
                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                        if (upcomingClassList[position].forDams == "0")
                            goToLiveCourseActivity(position)
                        else
                            testQuizCourseDialog()
                    } else {
                        if (upcomingClassList[position].nonDams == "0")
                            goToLiveCourseActivity(position)
                        else
                            testQuizCourseDialog()
                    }
                }
            }
        }
    }

    private fun goToLiveCourseActivity(position: Int) {
        val intent = Intent(
            context,
            LiveCourseActivity::class.java
        )
        intent.putExtra(Const.VIDEO_LINK, upcomingClassList[position].fileType)
        intent.putExtra(
            Const.VIDEO,
            getLiveClassVideoList(upcomingClassList[position]) as Serializable?
        )
        context.startActivity(intent)
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

    override fun getItemCount(): Int {
        return upcomingClassList.size
    }

    inner class OngoingClassViewHolder(var singleItemUpcomingClassBinding: SingleItemUpcomingClassBinding) :
        RecyclerView.ViewHolder(singleItemUpcomingClassBinding.root)

    private fun testQuizCourseDialog() {
        val v = Helper.newCustomDialog(
            context, true, context.getString(R.string.alert),
            context.getString(R.string.purchase_this_course)
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