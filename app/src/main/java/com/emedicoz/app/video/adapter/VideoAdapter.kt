package com.emedicoz.app.video.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.modelo.Video
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.Constants.UPDATE_LIST
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.emedicoz.app.video.activity.VideoDetail
import com.emedicoz.app.video.ui.services.VideoApi
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import kotlin.collections.ArrayList

class VideoAdapter(private val context: Context, private var modelList: ArrayList<Video>) :
    RecyclerView.Adapter<VideoAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val viewItem = LayoutInflater.from(parent.context).inflate(R.layout.video_adapter, parent, false)
        return CustomViewHolder(viewItem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val mList = modelList[position]
        val customViewHolder = holder as CustomViewHolder
        with(customViewHolder) {
            Glide.with(context).load(mList.thumbnail_url).apply(
                RequestOptions()
                    .placeholder(R.drawable.landscape_placeholder)
                    .error(R.drawable.landscape_placeholder)
            )
                .into(customViewHolder.videoImage!!)

            customViewHolder.title?.text = mList.video_title
            customViewHolder.description?.text = mList.author_name

            if (!TextUtils.isEmpty(mList.views)) {
                if (mList.views == "0" || mList.views == "1") {
                    customViewHolder.tvViews?.text = mList.views + Const.VIEWS
                } else {
                    customViewHolder.tvViews?.text = mList.views + " Views"
                }
            }

            if (mList.is_bookmarked == null || mList.is_bookmarked == "0") {
                customViewHolder.ibBookMark?.visibility = View.VISIBLE
                customViewHolder.ibBookMark?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bookmark_blankk))
            } else {
                customViewHolder.ibBookMarkFill?.visibility = View.VISIBLE
                customViewHolder.ibBookMarkFill?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.white_bookmark_icon))
            }

            if (!TextUtils.isEmpty(mList.creation_time)) {
                customViewHolder.date?.text =
                    if (DateUtils.getRelativeTimeSpanString(mList.creation_time.toLong()) == context.getString(R.string.string_minutes_ago)) Const.JUST_NOW else DateUtils.getRelativeTimeSpanString(
                        mList.creation_time.toLong()
                    )
            }
            setView(modelList[position])
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    inner class CustomViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val videoImage = itemView?.findViewById<ImageView>(R.id.video_image)
        val title = itemView?.findViewById<TextView>(R.id.title)
        val description = itemView?.findViewById<TextView>(R.id.description)
        val tvViews = itemView?.findViewById<TextView>(R.id.tvViews)
        val date = itemView?.findViewById<TextView>(R.id.date)
        val ibBookMark = itemView?.findViewById<ImageButton>(R.id.ibBookMark)
        val ibBookMarkFill = itemView?.findViewById<ImageButton>(R.id.ibBookMarkFill)
        private val videoitemRL = itemView?.findViewById<CardView>(R.id.videoitemRL)

        fun setView(videoData: Video) {
            ibBookMark?.setOnClickListener {
                ibBookMark.visibility = View.GONE
                ibBookMarkFill?.visibility = View.VISIBLE
                ibBookMarkFill?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.white_bookmark_icon))
                networkCallToBookmark(videoData.id)
            }

            ibBookMarkFill?.setOnClickListener {
                ibBookMarkFill.visibility = View.GONE
                ibBookMark?.visibility = View.VISIBLE
                ibBookMark?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bookmark_blankk))
                networkCallToRemoveBookmark(videoData.id)
            }

            videoitemRL?.setOnClickListener {
                val intent = Intent(context, VideoDetail::class.java).apply {
                    putExtra(Const.DATA, videoData as Serializable)
                    putExtra("is_bookmarked", videoData.is_bookmarked)
                    putExtra(Constants.Extras.TYPE, Const.VIDEO)
                }
                context.startActivity(intent)
            }
        }

        private fun networkCallToBookmark(videoId: String) {
            if (!Helper.isConnected(context)) {
                Toast.makeText(context, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
                return
            }

            val apiInterface = ApiClient.createService(VideoApi::class.java)
            val response = apiInterface.updateBookMarkStatus(SharedPreference.getInstance().loggedInUser.id, videoId, "1")
            response.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.body() != null) {
                        val jsonObject = response.body()
                        try {
                            UPDATE_LIST = "true"
                            val jsonResponse = JSONObject(jsonObject.toString())
                        } catch (e: JsonIOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(context, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Helper.showExceptionMsg(context, t)
                }
            })
        }

        private fun networkCallToRemoveBookmark(videoId: String) {
            if (!Helper.isConnected(context)) {
                Toast.makeText(context, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
                return
            }
            val apiInterface = ApiClient.createService(VideoApi::class.java)
            val response = apiInterface.updateBookMarkStatus(SharedPreference.getInstance().loggedInUser.id, videoId, "0")
            response.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.body() != null) {
                        val jsonObject = response.body()
                        try {
                            UPDATE_LIST = "true"
                            val jsonResponse = JSONObject(jsonObject.toString())
                        } catch (e: JsonIOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(context, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Helper.showExceptionMsg(context, t)
                }
            })
        }
    }
}