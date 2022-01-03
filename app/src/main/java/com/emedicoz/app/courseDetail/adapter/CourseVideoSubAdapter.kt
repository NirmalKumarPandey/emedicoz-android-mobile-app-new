package com.emedicoz.app.courseDetail.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.emedicoz.app.Model.offlineData
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.CourseDetailRowItemTopicBinding
import com.emedicoz.app.databinding.CourseDetailRowItemVideoBinding
import com.emedicoz.app.modelo.liveclass.courses.DescriptionResponse
import com.emedicoz.app.recordedCourses.model.detaildatavideo.ListDetailData
import com.emedicoz.app.recordedCourses.model.detaildatavideo.VideoItemData
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.apiinterfaces.LiveCourseInterface
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager
import com.google.gson.JsonObject
import com.tonyodev.fetch.Fetch
import com.tonyodev.fetch.request.RequestInfo
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CourseVideoSubAdapter(
    private val context: Context,
    private val moduleId: String,
    private val subItems: ArrayList<ListDetailData>,
    private val courseId: String
) : BaseExpandableListAdapter(), eMedicozDownloadManager.SaveOfflineVideoListener {

    private var topicId: String = ""
    private lateinit var savedOfflineListener: eMedicozDownloadManager.SaveOfflineVideoListener
    private var downloadId: Long = 0
    private val descriptionResponse: DescriptionResponse =
        (context as com.emedicoz.app.courseDetail.activity.CourseDetailActivity).descriptionResponse
    private val isFreeTrial: Boolean =
        (context as com.emedicoz.app.courseDetail.activity.CourseDetailActivity).isFreeTrial

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val binding = CourseDetailRowItemTopicBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val view = binding.root
        binding.topicIV.text = subItems[groupPosition].title
        topicId = subItems[groupPosition].id
        if (isExpanded) {
            binding.imageExpanded.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        } else {
            binding.imageExpanded.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
        }

        val childArray: ArrayList<VideoItemData> = subItems[groupPosition].videoList

        if (childArray == null || childArray.size <= 0) {
            binding.imageExpanded.visibility = View.GONE
        }

        return view
    }


    override fun getChildrenCount(i: Int): Int {
        return subItems[i].videoList.size
    }

    override fun getGroup(i: Int): ListDetailData? {
        return subItems[i]
    }

    override fun getGroupCount() = subItems.size

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getChild(i: Int, i1: Int): java.util.ArrayList<VideoItemData> {
        return subItems[i].videoList
    }

    override fun getGroupId(i: Int): Long {
        return 0
    }

    override fun getChildId(i: Int, i1: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val binding = CourseDetailRowItemVideoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val view = binding.root
        val videoData = subItems[groupPosition].videoList[childPosition]
        videoData.module_id = moduleId
        videoData.topic_id = topicId
        videoData.course_id = courseId

        savedOfflineListener = this

        setData(binding, videoData)
        return view
    }


    fun setData(binding: CourseDetailRowItemVideoBinding, videoItemData: VideoItemData) {
        binding.videoTitle.text = videoItemData.videoTitle
        if (videoItemData.liveOn != "0" && videoItemData.isLive != "1") {
            binding.tvLiveon.visibility = View.VISIBLE
            binding.tvLiveonDate.visibility = View.VISIBLE
        } else if (videoItemData.videoType == "0" && videoItemData.isVod == "1") {
            binding.tvLiveon.visibility = View.GONE
            binding.tvLiveonDate.visibility = View.VISIBLE
        }

        if (videoItemData.is_viewed == "1")
            binding.videoViewedImage.visibility = View.VISIBLE
        else
            binding.videoViewedImage.visibility = View.GONE

        if (descriptionResponse.data.isPurchased == "0" && !isFreeTrial && videoItemData.isPreviewVideo)
            binding.txtVideoPreview.visibility = View.VISIBLE
        else
            binding.txtVideoPreview.visibility = View.GONE

        if (videoItemData.isLive != null /*&& !liveClassVideoList.getIsLive().equals("1")*/
            && videoItemData.liveOn != ""
        ) {
            binding.tvLiveon.visibility = View.VISIBLE
            binding.tvLiveonDate.text =
                Helper.getFormattedDateTime(videoItemData.liveOn.toLong() * 1000)
        } else if (videoItemData.duration != null && videoItemData.duration.isNotEmpty()) {
            binding.videoDuration.visibility = View.VISIBLE
            binding.tvLiveon.visibility = View.GONE
            binding.videoDuration.text = "Video Length ${videoItemData.duration}"
        } else {
            binding.tvLiveon.visibility = View.GONE
            binding.videoDuration.visibility = View.GONE
        }

        if (SharedPreference.getInstance().getBoolean(Const.SINGLE_STUDY)) {
            if (videoItemData.isVod == "1") {
                if (descriptionResponse.data.isPurchased == "1" || (!isFreeTrial && !videoItemData.isPreviewVideo)) {
                    binding.downloadIV.visibility = View.VISIBLE
                    checkData(binding, videoItemData)
                }
                /*
            } else if (liveClassVideoList.getIsVod().equals("0") && liveClassVideoList.getFileUrl().contains(".m3u8")) {
                ((ImageView) convertView.findViewById(R.id.downloadIV)).setVisibility(View.VISIBLE);
                checkData(convertView, liveClassVideoList);*/
            } else {
                binding.deleteIV.visibility = View.GONE
                binding.messageTV.visibility = View.GONE
                binding.downloadIV.visibility = View.GONE
                binding.downloadProgessBar.visibility = View.GONE
            }
            binding.locIV.visibility = View.GONE
        } else {
            if (descriptionResponse.data.isPurchased == "0" && !isFreeTrial) {
                if (descriptionResponse.data.basic.mrp == "0") {
                    setViewOfVideoItem(binding, videoItemData)
                } else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                        if (descriptionResponse.data.basic.forDams == "0") {
                            setViewOfVideoItem(binding, videoItemData)
                        } else {
                            binding.locIV.visibility = View.VISIBLE
                            binding.downloadIV.visibility = View.GONE
                        }
                    } else {
                        if (descriptionResponse.data.basic.nonDams == "0") {
                            setViewOfVideoItem(binding, videoItemData)
                        } else {
                            binding.locIV.visibility = View.VISIBLE
                            binding.downloadIV.visibility = View.GONE
                        }
                    }
                }
            } else {
                if (videoItemData.videoType == "0" && videoItemData.isVod == "1") {
//                                llDownload.setVisibility(View.VISIBLE);
                    if (descriptionResponse.data.isPurchased == "1" || (!isFreeTrial && !videoItemData.isPreviewVideo))
                        binding.downloadIV.visibility = View.VISIBLE
                } else {
//                                llDownload.setVisibility(View.GONE);
                    binding.deleteIV.visibility = View.GONE
                    binding.messageTV.visibility = View.GONE
                    binding.downloadIV.visibility = View.GONE
                    binding.downloadProgessBar.visibility = View.GONE
                }
                binding.locIV.visibility = View.GONE
            }
        }
        var correctDateFormat = ""
        if (!GenericUtils.isEmpty(videoItemData.liveOn)) {
            val d = Date(GenericUtils.getParsableString(videoItemData.liveOn).toLong() * 1000)
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
        }
        Glide.with(context).asGif().load(R.drawable.live_gif).into(binding.liveIV)
        if (videoItemData.isLive != null && videoItemData.isLive == "1") {
            binding.liveIV.visibility = View.VISIBLE
            binding.downloadIV.visibility = View.GONE
        } else
            binding.liveIV.visibility = View.GONE
        bindControls(descriptionResponse, binding, videoItemData, correctDateFormat)
    }

    private fun setViewOfVideoItem(
        binding: CourseDetailRowItemVideoBinding,
        videoItemData: VideoItemData
    ) {
        if (descriptionResponse.data.isPurchased == "1" || (!isFreeTrial && !videoItemData.isPreviewVideo))
            checkData(binding, videoItemData)
        if (videoItemData.liveUrl.equals("", ignoreCase = true) && videoItemData.isLive.equals(
                "0",
                ignoreCase = true
            )
        ) {
            binding.locIV.visibility = View.GONE
            binding.downloadIV.visibility = View.GONE
        } else {
            if (videoItemData.fileUrl.isNotEmpty()) {
                binding.locIV.visibility = View.GONE
                if (descriptionResponse.data.isPurchased == "1" || (!isFreeTrial && !videoItemData.isPreviewVideo))
                    binding.downloadIV.visibility = View.VISIBLE
            } else {
                binding.locIV.visibility = View.GONE
                binding.downloadIV.visibility = View.GONE
            }
        }
    }

    private fun bindControls(
        descriptionResponse: DescriptionResponse,
        binding: CourseDetailRowItemVideoBinding,
        videoItemData: VideoItemData,
        correctDateFormat: String
    ) {
        binding.txtVideoPreview.setOnClickListener {
            binding.videoPlayLayout.performClick()
        }
        binding.videoPlayLayout.tag = videoItemData
        binding.videoPlayLayout.setOnClickListener {
            val item = it.tag as VideoItemData

            if (item.isPreviewVideo) {
                if (!GenericUtils.isEmpty(item.isDrm)) {
                    if (item.isDrm == "0")
                        goToPlayerActivity(item, correctDateFormat)
                    else
                        networkCallForCreateVideoLink(item, correctDateFormat)
                } else {
                    goToPlayerActivity(item, correctDateFormat)
                }
                return@setOnClickListener
            }

            if (descriptionResponse.data.isPurchased == "1" || isFreeTrial) {
                if (!GenericUtils.isEmpty(item.isDrm)) {
                    if (item.isDrm == "0")
                        goToPlayerActivity(item, correctDateFormat)
                    else
                        networkCallForCreateVideoLink(item, correctDateFormat)
                } else {
                   goToPlayerActivity(item, correctDateFormat)
                }
            } else {
                if (descriptionResponse.data.basic.mrp == "0")
                    if (!GenericUtils.isEmpty(item.isDrm)) {
                        if (item.isDrm == "0")
                            goToPlayerActivity(item, correctDateFormat)
                        else
                            networkCallForCreateVideoLink(item, correctDateFormat)
                    } else {
                        goToPlayerActivity(item, correctDateFormat)
                    }
                else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                        if (descriptionResponse.data.basic.forDams == "0")
                            if (!GenericUtils.isEmpty(item.isDrm)) {
                                if (item.isDrm == "0")
                                    goToPlayerActivity(item, correctDateFormat)
                                else
                                    networkCallForCreateVideoLink(item, correctDateFormat)
                            } else {
                                goToPlayerActivity(item, correctDateFormat)
                            }
                        else
                            Toast.makeText(context, R.string.buy_to_watch, Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        if (descriptionResponse.data.basic.nonDams == "0")
                            if (!GenericUtils.isEmpty(item.isDrm)) {
                                if (item.isDrm == "0")
                                    goToPlayerActivity(item, correctDateFormat)
                                else
                                    networkCallForCreateVideoLink(item, correctDateFormat)
                            } else {
                                goToPlayerActivity(item, correctDateFormat)
                            }
                        else
                            Toast.makeText(context, R.string.buy_to_watch, Toast.LENGTH_SHORT)
                                .show()
                    }
                }
            }
        }
        binding.downloadIV.tag = videoItemData
        binding.downloadIV.setOnClickListener {
            val item = it.tag as VideoItemData

            if (descriptionResponse.data.isPurchased == "1")
                startDownload(item.fileUrl, binding, item)
            else {
                if (descriptionResponse.data.basic.mrp == "0")
                    startDownload(item.fileUrl, binding, item)
                else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                        if (descriptionResponse.data.basic.forDams == "0")
                            startDownload(item.fileUrl, binding, item)
                        else
                            Toast.makeText(
                                context,
                                R.string.buy_to_download_video,
                                Toast.LENGTH_SHORT
                            ).show()
                    } else {
                        if (descriptionResponse.data.basic.nonDams == "0")
                            startDownload(item.fileUrl, binding, item)
                        else
                            Toast.makeText(
                                context,
                                R.string.buy_to_download_video,
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
            }
        }
        binding.deleteIV.tag = videoItemData
        binding.deleteIV.setOnClickListener {
            val item = it.tag as VideoItemData
            getDownloadCancelDialog(
                binding,
                item,
                context,
                Const.DELETE_DOWNLOAD,
                Const.CONFIRM_DELETE_DOWNLOAD
            )
        }
    }

    private fun goToPlayerActivity(videoItemData: VideoItemData, correctDateFormat: String) {
        val offline = eMedicozDownloadManager.getOfflineDataIds(
            videoItemData.id,
            Const.VIDEOS,
            context as Activity,
            videoItemData.id
        )
        if (offline == null) {
            if (Helper.isConnected(context)) {
                if (videoItemData.isVod == "0") {
                    if (!GenericUtils.isEmpty(videoItemData.liveOn) && videoItemData.liveOn.toLong() * 1000
                        <= System.currentTimeMillis()
                    ) {
                        if (videoItemData.isLive.equals("1", ignoreCase = true)) {
//                            val intent = Intent(context, LiveCourseActivity::class.java)
//                            intent.putExtra(Const.VIDEO_LINK, videoItemData.liveUrl)
//                            intent.putExtra(Const.VIDEO, videoItemData)
//                            context.startActivity(intent)

                            val url = if (!GenericUtils.isEmpty(videoItemData.isDrm)) {
                                if (videoItemData.isDrm == "0") {
                                    AES.decrypt(videoItemData.fileUrl)
                                } else {
                                    videoItemData.drmUrl
                                }
                            } else {
                                AES.decrypt(videoItemData.fileUrl)
                            }
                            Helper.GoToLiveClassesWithChat(
                                context,
                                url,
                                Const.VIDEO_LIVE,
                                videoItemData.chatPlatform,
                                videoItemData.id,
                                true,
                                videoItemData.chatNode
                            )

                        } else {
                            var url = ""
                            url = if (!GenericUtils.isEmpty(videoItemData.isDrm)) {
                                if (videoItemData.isDrm == "0") {
                                    if (GenericUtils.isEmpty(videoItemData.liveUrl))
                                        AES.decrypt(videoItemData.fileUrl)
                                    else
                                        videoItemData.liveUrl
                                } else {
                                    videoItemData.drmUrl
                                }
                            } else {
                                if (GenericUtils.isEmpty(videoItemData.liveUrl))
                                    AES.decrypt(videoItemData.fileUrl)
                                else
                                    videoItemData.liveUrl
                            }
                            gotoPreviewVideoPlayer(
                                context,
                                videoItemData,
                                url
                            )
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "This video will be live on: $correctDateFormat",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    var url = ""
                    url = if (!GenericUtils.isEmpty(videoItemData.isDrm)) {
                        if (videoItemData.isDrm == "0") {
                            if (GenericUtils.isEmpty(videoItemData.liveUrl))
                                AES.decrypt(videoItemData.fileUrl)
                            else
                                videoItemData.liveUrl
                        } else {
                            videoItemData.drmUrl
                        }
                    } else {
                        if (GenericUtils.isEmpty(videoItemData.liveUrl))
                            AES.decrypt(videoItemData.fileUrl)
                        else
                            videoItemData.liveUrl
                    }
                    gotoPreviewVideoPlayer(context, videoItemData, url)
                }
            } else Toast.makeText(context, R.string.internet_error_message, Toast.LENGTH_SHORT)
                .show()
        } else
            singleViewCLick(videoItemData, offline)
    }

    private fun networkCallForCreateVideoLink(
        videoItemData: VideoItemData,
        correctDateFormat: String
    ) {

        val resourceId = if (!GenericUtils.isEmpty(videoItemData.resourceId)) {
            videoItemData.resourceId
        } else {
            ""
        }
        val progress: Progress = Progress(context)
        progress.show()
        val apiInterface = ApiClient.createService(LiveCourseInterface::class.java)
        val response = apiInterface.onRequestCreateVideoLink(
            SharedPreference.getInstance().loggedInUser.id,
            videoItemData.id,
            resourceId
        )

        response.enqueue(object : Callback<JsonObject?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject

                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        val data = jsonResponse.optJSONObject(Const.DATA)
                        Log.e(
                            "TAG",
                            "onResponse: ${data.optJSONObject("link").optString("file_url")}"
                        )
                        videoItemData.drmUrl = data.optJSONObject("link").optString("file_url")
                        videoItemData.drmToken = data.optJSONObject("link").optString("token")
                        goToPlayerActivity(videoItemData, correctDateFormat)
                        progress.dismiss()
                    } catch (e: Exception) {
                        progress.dismiss()
                        e.printStackTrace()
                        Helper.showErrorLayoutForNav(
                            "networkCallForRecordedCourse",
                            context as Activity?,
                            1,
                            0
                        )
                    }
                } else {
                    progress.dismiss()
                    Helper.showErrorLayoutForNav(
                        "networkCallForRecordedCourse",
                        context as Activity?,
                        1,
                        1
                    )
                }

            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav(
                    "networkCallForRecordedCourse",
                    context as Activity?,
                    1,
                    2
                )
            }
        })


    }


    private fun gotoPreviewVideoPlayer(
        context: Activity,
        videoItemData: VideoItemData,
        fileUrl: String?
    ) {

        Helper.GoToVODPlayActivity(
            context,
            fileUrl,
            Const.VIDEO_STREAM,
            videoItemData,
            Const.COURSE_VIDEO_TYPE
        )
//        Helper.GoToLiveClassesWithChat(context, videoItemData.fileUrl, Const.VIDEO_LIVE, "", videoItemData.id)
    }

    private fun getDownloadCancelDialog(
        binding: CourseDetailRowItemVideoBinding,
        videoItemData: VideoItemData,
        context1: Context,
        title: String?,
        message: String?
    ) {
        val v = Helper.newCustomDialog(context1, true, title, message)
        val btnCancel: Button
        val btnSubmit: Button
        btnCancel = v.findViewById(R.id.btn_cancel)
        btnSubmit = v.findViewById(R.id.btn_submit)
        btnCancel.text = context1.getString(R.string.no)
        btnSubmit.text = context1.getString(R.string.yes)
        btnCancel.setOnClickListener { Helper.dismissDialog() }
        btnSubmit.setOnClickListener {
            Helper.dismissDialog()
            eMedicozDownloadManager.removeOfflineData(
                videoItemData.id, Const.VIDEOS,
                context1 as Activity, videoItemData.id
            )
            binding.downloadIV.visibility = View.VISIBLE
            binding.downloadIV.setImageResource(R.drawable.ic_baseline_download_icon)
            binding.downloadProgessBar.visibility = View.GONE
            binding.downloadProgessBar.progress = 0
            binding.messageTV.visibility = View.GONE
            binding.deleteIV.visibility = View.GONE
        }
    }

    fun singleViewCLick(videoItemData: VideoItemData, offlineData: offlineData?) {
        var offlineData = offlineData
        if (offlineData == null) {
            offlineData = eMedicozDownloadManager.getOfflineDataIds(
                videoItemData.id,
                Const.VIDEOS, context as Activity, videoItemData.id
            )
        }
        if (offlineData != null && offlineData.requestInfo == null) {
            offlineData.requestInfo =
                eMedicozDownloadManager.getFetchInstance()[offlineData.downloadid]
        }
        if (offlineData != null && offlineData.requestInfo != null &&
            offlineData.requestInfo.status == Fetch.STATUS_DONE && offlineData.link != null
        ) {
            if ((context.filesDir.toString() + "/" + offlineData.link).contains(".mp4")) {
                Helper.GoToVideoActivity(
                    context as Activity,
                    context.getFilesDir().toString() + "/" + offlineData.link,
                    Const.VIDEO_STREAM,
                    videoItemData.id,
                    Const.COURSE_VIDEO_TYPE
                )
            } else {
                Helper.DecryptAndGoToVideoActivity(
                    context as Activity,
                    context.getFilesDir().toString() + "/" + offlineData.link,
                    Const.VIDEO_STREAM,
                    videoItemData.id,
                    Const.COURSE_VIDEO_TYPE
                )
            }
        }
    }

    private fun checkData(binding: CourseDetailRowItemVideoBinding, videoItemData: VideoItemData) {
        val offlineData = eMedicozDownloadManager.getOfflineDataIds(
            videoItemData.id,
            Const.VIDEOS, context as Activity, videoItemData.id
        )
        binding.downloadIV.setImageResource(R.drawable.ic_baseline_download_icon)
        binding.downloadProgessBar.visibility = View.GONE
        binding.deleteIV.visibility = View.GONE
        binding.messageTV.visibility = View.GONE
        binding.downloadIV.visibility = View.GONE
        if (offlineData != null) {
            if (offlineData.requestInfo == null) offlineData.requestInfo =
                eMedicozDownloadManager.getFetchInstance()[offlineData.downloadid]
            if (offlineData.requestInfo != null) {
                binding.downloadProgessBar.visibility =
                    if (offlineData.requestInfo.progress < 100) View.VISIBLE else View.GONE

                //4 conditions to check at the time of intialising the video

                //0. downloading in queue
                if (offlineData.requestInfo.status == Fetch.STATUS_QUEUED) {
                    binding.downloadIV.visibility = View.GONE
                    binding.deleteIV.visibility = View.VISIBLE
                    binding.messageTV.setText(R.string.download_queued)
                    downloadId = offlineData.downloadid
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(
                        binding.root, offlineData.downloadid, videoItemData,
                        savedOfflineListener, Const.VIDEOS
                    )

                } else if (offlineData.requestInfo.status == Fetch.STATUS_DOWNLOADING
                    && offlineData.requestInfo.progress < 100
                ) {
                    binding.downloadIV.visibility = View.GONE
                    binding.deleteIV.visibility = View.VISIBLE
                    binding.downloadProgessBar.visibility = View.VISIBLE
                    binding.downloadProgessBar.progress = offlineData.requestInfo.progress
                    binding.messageTV.setText(R.string.download_queued)
                    downloadId = offlineData.downloadid
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(
                        binding.root, offlineData.downloadid, videoItemData,
                        savedOfflineListener, Const.VIDEOS
                    )

                } else if (offlineData.requestInfo.status == Fetch.STATUS_DONE && offlineData.requestInfo.progress == 100) {
                    binding.messageTV.setText(R.string.downloaded_offline)
                    binding.downloadProgessBar.visibility = View.GONE
                    binding.downloadIV.visibility = View.VISIBLE
                    binding.downloadIV.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                    binding.deleteIV.visibility = View.VISIBLE

                } else if (offlineData.requestInfo.status == Fetch.STATUS_PAUSED && offlineData.requestInfo.progress < 100) {
                    binding.downloadProgessBar.progress = offlineData.requestInfo.progress
                    binding.deleteIV.visibility = View.VISIBLE
                    binding.downloadIV.visibility = View.VISIBLE
                    binding.downloadIV.setImageResource(R.mipmap.download_pause)
                    binding.messageTV.setText(R.string.download_pasued)

                } else if (offlineData.requestInfo.status == Fetch.STATUS_ERROR && offlineData.requestInfo.progress < 100) {
                    binding.messageTV.setText(R.string.error_in_downloading)
                    binding.deleteIV.visibility = View.VISIBLE
                    binding.downloadProgessBar.progress = offlineData.requestInfo.progress
                    binding.downloadIV.visibility = View.VISIBLE
                    binding.downloadIV.setImageResource(R.mipmap.download_reload)
                }
                binding.messageTV.visibility =
                    if (offlineData.requestInfo != null) View.VISIBLE else View.GONE
            } else {
                binding.downloadProgessBar.visibility = View.GONE
                binding.deleteIV.visibility = View.GONE
                binding.messageTV.visibility = View.GONE
                binding.downloadIV.visibility = View.VISIBLE
            }
        } else {
            binding.downloadProgessBar.visibility = View.GONE
            binding.deleteIV.visibility = View.GONE
            binding.messageTV.visibility = View.GONE
            binding.downloadIV.visibility = View.VISIBLE
        }
    }

    private fun startDownload(
        fileUrl: String,
        binding: CourseDetailRowItemVideoBinding,
        videoItemData: VideoItemData
    ) {
        val offline = eMedicozDownloadManager.getOfflineDataIds(
            videoItemData.id,
            Const.VIDEOS,
            context as Activity,
            videoItemData.id
        )
        if (offline != null && offline.requestInfo == null) offline.requestInfo =
            eMedicozDownloadManager.getFetchInstance()[offline.downloadid]
        if (offline != null && offline.requestInfo != null) {
            //when video is downloading
            if (offline.requestInfo.status == Fetch.STATUS_DOWNLOADING || offline.requestInfo.status == Fetch.STATUS_QUEUED) {
                Toast.makeText(context, R.string.file_download_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } //when video is paused

            else if (offline.requestInfo.status == Fetch.STATUS_PAUSED) {
                binding.messageTV.visibility = View.VISIBLE
                binding.downloadProgessBar.visibility = View.VISIBLE
                binding.downloadIV.visibility = View.GONE
                binding.deleteIV.visibility = View.VISIBLE
                binding.messageTV.setText(R.string.download_pending)
                downloadId = offline.downloadid
                eMedicozDownloadManager.getFetchInstance().resume(offline.requestInfo.id)
                eMedicozDownloadManager.getInstance().downloadProgressUpdate(
                    binding.root, offline.downloadid, videoItemData,
                    savedOfflineListener, Const.VIDEOS
                )
            } //when some error occurred

            else if (offline.requestInfo.status == Fetch.STATUS_ERROR) {
                binding.messageTV.visibility = View.VISIBLE
                binding.downloadProgessBar.visibility = View.VISIBLE
                binding.downloadIV.visibility = View.GONE
                binding.deleteIV.visibility = View.VISIBLE
                binding.messageTV.setText(R.string.download_pending)
                downloadId = offline.downloadid
                eMedicozDownloadManager.getFetchInstance().retry(offline.downloadid)
                eMedicozDownloadManager.getInstance().downloadProgressUpdate(
                    binding.root, offline.downloadid, videoItemData,
                    savedOfflineListener, Const.VIDEOS
                )

            } else if (offline.requestInfo.status == Fetch.STATUS_DONE) {
                binding.messageTV.setText(R.string.downloaded_offline)
                binding.downloadProgessBar.visibility = View.GONE
                binding.downloadIV.visibility = View.VISIBLE
                binding.downloadIV.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                binding.deleteIV.visibility = View.VISIBLE
                singleViewCLick(videoItemData, offline)
            }
        } //for new download
        else if (offline == null || offline.requestInfo == null) {
            if (Helper.isConnected(context)) {
                eMedicozDownloadManager.getInstance().showQualitySelectionPopup(
                    context,
                    videoItemData.id,
                    fileUrl,
                    Helper.getFileName(fileUrl, videoItemData.videoTitle, Const.VIDEOS),
                    Const.VIDEOS,
                    videoItemData.id
                ) { downloadid: Long ->
                    binding.messageTV.visibility = View.VISIBLE
                    binding.downloadProgessBar.visibility = View.VISIBLE
                    binding.downloadIV.visibility = View.GONE
                    binding.deleteIV.visibility = View.VISIBLE
                    binding.messageTV.setText(R.string.download_queued)

                    if (downloadid == Constants.MIGRATED_DOWNLOAD_ID.toLong()) {
                        binding.downloadIV.visibility = View.VISIBLE
                        binding.deleteIV.visibility = View.VISIBLE
                        binding.messageTV.visibility = View.VISIBLE
                        binding.downloadIV.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                        binding.messageTV.setText(R.string.downloaded_offline)
                        if (binding.downloadProgessBar.visibility != View.GONE)
                            binding.downloadProgessBar.visibility = View.GONE
                    } else if (downloadid != 0L) {
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(
                            binding.root, downloadid, videoItemData,
                            savedOfflineListener, Const.VIDEOS
                        )
                    } else {
                        binding.messageTV.visibility = View.INVISIBLE
                        binding.downloadProgessBar.visibility = View.GONE
                        binding.deleteIV.visibility = View.GONE
                        binding.downloadIV.visibility = View.VISIBLE
                        binding.downloadIV.setImageResource(R.drawable.ic_baseline_download_icon)
                    }
                }
            } else Toast.makeText(context, R.string.internet_error_message, Toast.LENGTH_SHORT)
                .show()
        } else if (offline != null && offline.requestInfo == null) {
            offline.requestInfo = eMedicozDownloadManager.getFetchInstance()[offline.downloadid]
            if (offline.requestInfo == null) {
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
            } else {
                binding.downloadIV.performClick()
            }
        }
    }

    override fun updateUIForDownloadedVideo(
        convertView: View?,
        data: VideoItemData?,
        requestInfo: RequestInfo?,
        id: Long
    ) {

        convertView!!.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
        convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
        convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
        (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
        convertView.findViewById<View>(R.id.messageTV).visibility = View.VISIBLE
        (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.downloaded_offline)
        eMedicozDownloadManager.addOfflineDataIds(
            data?.id, data?.fileUrl, context as Activity,
            false, true, Const.VIDEOS, requestInfo!!.id, data?.id
        )

    }

    override fun updateProgressUI(convertView: View?, value: Int?, status: Int, id: Long) {

        convertView!!.findViewById<View>(R.id.messageTV).visibility = View.VISIBLE
        convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.VISIBLE

        if (status == Fetch.STATUS_QUEUED) {
            convertView.findViewById<View>(R.id.downloadIV).visibility = View.GONE
            convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
            (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.download_queued)

        } else if (status == Fetch.STATUS_REMOVED) {
            (convertView.findViewById<View>(R.id.downloadProgessBar) as ProgressBar).progress = 0
            convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
            convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
            (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.drawable.ic_baseline_download_icon)
            convertView.findViewById<View>(R.id.deleteIV).visibility = View.GONE
            convertView.findViewById<View>(R.id.messageTV).visibility = View.INVISIBLE

        } else if (status == Fetch.STATUS_ERROR) {
            (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.mipmap.download_reload)
            convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
            convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
            (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.error_in_downloading)

        } else if (status == Fetch.STATUS_DOWNLOADING) {
            convertView.findViewById<View>(R.id.downloadIV).visibility = View.GONE
            convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
            if (value!! > 0) {
                (convertView.findViewById<View>(R.id.messageTV) as TextView).text =
                    "Downloading...$value%"
            } else (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.download_pending)
        }

        (convertView.findViewById<View>(R.id.downloadProgessBar) as ProgressBar).progress = value!!
    }

}

