package com.emedicoz.app.courseDetail.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.legacy.widget.Space
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.Model.offlineData
import com.emedicoz.app.R
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.courses.activity.QuizActivity
import com.emedicoz.app.courses.activity.TestQuizActionActivity
import com.emedicoz.app.courses.adapter.ReattemptDialogAdapter
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.CourseDetailRowItemPdfBinding
import com.emedicoz.app.databinding.CourseDetailRowItemTestBinding
import com.emedicoz.app.databinding.CourseDetailRowItemTopicBinding
import com.emedicoz.app.epubear.ePubActivity
import com.emedicoz.app.modelo.TestSeriesResultData
import com.emedicoz.app.modelo.courses.SingleCourseData
import com.emedicoz.app.modelo.courses.TestSeries
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries
import com.emedicoz.app.modelo.liveclass.courses.DescriptionResponse
import com.emedicoz.app.recordedCourses.model.detaildatanotes.NotesData
import com.emedicoz.app.recordedCourses.model.detaildatanotes.NotesList
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.testmodule.activity.TestBaseActivity
import com.emedicoz.app.utilso.*
import com.emedicoz.app.utilso.offlinedata.eMedicozDownloadManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tonyodev.fetch.Fetch
import com.tonyodev.fetch.request.RequestInfo
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CourseNotesSubAdapter(private val context: Context, private val subItems: ArrayList<NotesList>, private val courseId: String, private val moduleId: String)
    : BaseExpandableListAdapter(), eMedicozDownloadManager.SaveOfflineNotesListener {

    private var topicId: String = ""
    private val testSeriesResultList: ArrayList<TestSeriesResultData> = ArrayList()
    private val activity: Activity = context as Activity
    private lateinit var savedOfflineListener: eMedicozDownloadManager.SaveOfflineNotesListener
    private var downloadId: Long = 0
    private val descriptionResponse: DescriptionResponse = (context as com.emedicoz.app.courseDetail.activity.CourseDetailActivity).descriptionResponse
    private val isFreeTrial: Boolean = (context as com.emedicoz.app.courseDetail.activity.CourseDetailActivity).isFreeTrial

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
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
        val childArray: ArrayList<NotesData> = subItems[groupPosition].data

        if (childArray == null || childArray.size <= 0) {
            binding.imageExpanded.visibility = View.GONE
        }

        return view
    }

    override fun getGroupCount() = subItems.size

    override fun getChildrenCount(i: Int): Int {
        return subItems[i].data.size
    }

    override fun getGroup(i: Int): NotesList {
        return subItems[i]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getChild(i: Int, i1: Int): java.util.ArrayList<NotesData> {
        return subItems[i].data
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

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View? {
        var view = View(context)
        savedOfflineListener = this
        val childItem: NotesData = subItems[groupPosition].data[childPosition]
        childItem.module_id = moduleId
        childItem.topicId = topicId
        childItem.course_id = courseId
        if (childItem.type.equals(Constants.TestType.TEST, ignoreCase = true) ||
                childItem.type.equals(Constants.TestType.Q_BANK, ignoreCase = true)) {
            val binding = CourseDetailRowItemTestBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            view = binding.root

            setTestData(binding, childItem)
        } else if (childItem.type.equals("epub", ignoreCase = true) ||
                childItem.type.equals("pdf", ignoreCase = true)) {
            val binding = CourseDetailRowItemPdfBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            view = binding.root
            if (childItem.type.equals("epub", ignoreCase = true))
                setEpubData(binding, view, childItem)
            else if (childItem.type.equals("pdf", ignoreCase = true))
                setPdfData(binding, view, childItem)
        }

        setLockVisibility(view, childItem)

        return view
    }


    private fun setPdfData(binding: CourseDetailRowItemPdfBinding, convertView: View, pdfData: NotesData) {
        (convertView.findViewById<View>(R.id.fileTypeTV) as TextView).text = pdfData.title
        binding.itemTypeimageIV.setImageResource(R.mipmap.pdf)
        binding.quesCourseTest.text = pdfData.description
        if (!isFreeTrial)
            checkData(convertView, Const.PDF, pdfData)

        if (descriptionResponse.data.isPurchased == "1" || descriptionResponse.data.basic.mrp == "0" || isFreeTrial)
            convertView.findViewById<View>(R.id.showPdfRL).tag = pdfData
            convertView.findViewById<View>(R.id.showPdfRL).setOnClickListener {
                if (descriptionResponse.data.isPurchased == "1" || isFreeTrial) {
                    singleViewCLick(it.tag as NotesData, Const.PDF, null)
                } else {
                    if (descriptionResponse.data.basic.mrp == "0") singleViewCLick(it.tag as NotesData, Const.PDF, null)
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                            if (descriptionResponse.data.basic.forDams == "0")
                                singleViewCLick(it.tag as NotesData, Const.PDF, null)
                            else
                                testQuizCourseDialog()
                        } else {
                            if (descriptionResponse.data.basic.nonDams == "0")
                                singleViewCLick(it.tag as NotesData, Const.PDF, null)
                            else
                                testQuizCourseDialog()
                        }
                    }
                }
            }
       // }
        convertView.findViewById<View>(R.id.downloadIV).tag = pdfData
        convertView.findViewById<View>(R.id.downloadIV).setOnClickListener { view1: View ->
            val pdfData1 = view1.tag as NotesData
            if (descriptionResponse.data.isPurchased == "1") startDownload(convertView, pdfData1.fileUrl, Const.PDF, pdfData1) else {
                if (descriptionResponse.data.basic.mrp == "0") startDownload(convertView, pdfData1.fileUrl, Const.PDF, pdfData1) else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                        if (descriptionResponse.data.basic.forDams == "0") startDownload(convertView, pdfData1.fileUrl, Const.PDF, pdfData1) else Toast.makeText(activity, activity.resources.getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show()
                    } else {
                        if (descriptionResponse.data.basic.nonDams == "0") startDownload(convertView, pdfData1.fileUrl, Const.PDF, pdfData1) else Toast.makeText(activity, activity.resources.getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        convertView.findViewById<View>(R.id.deleteIV).tag = pdfData
        convertView.findViewById<View>(R.id.deleteIV).setOnClickListener { view1: View ->
            val pdfData1 = view1.tag as NotesData
            getDownloadCancelDialog(convertView, pdfData1, activity, Const.PDF, Const.DELETE_DOWNLOAD, Const.CONFIRM_DELETE_DOWNLOAD)
        }
    }

    private fun setEpubData(binding: CourseDetailRowItemPdfBinding, convertView: View, epubData: NotesData) {
        (convertView.findViewById<View>(R.id.fileTypeTV) as TextView).text = epubData.title
        binding.itemTypeimageIV.setImageResource(R.mipmap.epub_new_course)
        binding.quesCourseTest.text = "${epubData.pageCount} pages"
        if (!isFreeTrial)
            checkData(convertView, Const.EPUB, epubData)
        if (descriptionResponse.data.isPurchased == "1" || descriptionResponse.data.basic.mrp == "0" || isFreeTrial)
            convertView.findViewById<View>(R.id.showPdfRL).tag = epubData
            convertView.findViewById<View>(R.id.showPdfRL).setOnClickListener {
                if (descriptionResponse.data.isPurchased == "1" || isFreeTrial) {
                    singleViewCLick(it.tag as NotesData, Const.EPUB, null)
                } else {
                    if (descriptionResponse.data.basic.mrp == "0") singleViewCLick(it.tag as NotesData, Const.EPUB, null)
                    else {
                        if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                            if (descriptionResponse.data.basic.forDams == "0")
                                singleViewCLick(it.tag as NotesData, Const.EPUB, null)
                            else
                                testQuizCourseDialog()
                        } else {
                            if (descriptionResponse.data.basic.nonDams == "0")
                                singleViewCLick(it.tag as NotesData, Const.EPUB, null)
                            else
                                testQuizCourseDialog()
                        }
                    }
                }
            }
     //   }
        convertView.findViewById<View>(R.id.downloadIV).tag = epubData
        convertView.findViewById<View>(R.id.downloadIV).setOnClickListener { view1: View ->
            val epubData1 = view1.tag as NotesData
            if (descriptionResponse.data.isPurchased == "1") startDownload(convertView, epubData1.fileUrl, Const.EPUB, epubData1) else {
                if (descriptionResponse.data.basic.mrp == "0") startDownload(convertView, epubData1.fileUrl, Const.EPUB, epubData1) else {
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                        if (descriptionResponse.data.basic.forDams == "0") startDownload(convertView, epubData1.fileUrl, Const.EPUB, epubData1) else Toast.makeText(activity, activity.resources.getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show()
                    } else {
                        if (descriptionResponse.data.basic.nonDams == "0") startDownload(convertView, epubData1.fileUrl, Const.EPUB, epubData1) else Toast.makeText(activity, activity.resources.getString(R.string.buy_course_to_download), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        convertView.findViewById<View>(R.id.deleteIV).tag = epubData
        convertView.findViewById<View>(R.id.deleteIV).setOnClickListener { view1: View ->
            val epubData1 = view1.tag as NotesData
            getDownloadCancelDialog(convertView, epubData1, activity, Const.EPUB, Const.DELETE_DOWNLOAD, Const.CONFIRM_DELETE_DOWNLOAD)
        }
    }

    private fun startDownload(convertView: View, fileUrl: String, type: String, notesData: NotesData) {
        val offline = eMedicozDownloadManager.getOfflineDataIds(notesData.id, type, activity, notesData.id)
        if (offline != null && offline.requestInfo == null) offline.requestInfo = eMedicozDownloadManager.getFetchInstance()[offline.downloadid]
        if (offline != null && offline.requestInfo != null) {
            //when video is downloading
            if (offline.requestInfo.status == Fetch.STATUS_DOWNLOADING || offline.requestInfo.status == Fetch.STATUS_QUEUED) {
                Toast.makeText(activity, R.string.file_download_in_progress, Toast.LENGTH_SHORT).show()
            } //when video is paused
            else if (offline.requestInfo.status == Fetch.STATUS_PAUSED) {
                convertView.findViewById<View>(R.id.messageTV).visibility = View.VISIBLE
                convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.VISIBLE
                convertView.findViewById<View>(R.id.downloadIV).visibility = View.GONE
                convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
                (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.download_pending)
                downloadId = offline.downloadid
                eMedicozDownloadManager.getFetchInstance().resume(offline.requestInfo.id)
                eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offline.downloadid, notesData, savedOfflineListener, type)
            } //when some error occurred
            else if (offline.requestInfo.status == Fetch.STATUS_ERROR) {
                convertView.findViewById<View>(R.id.messageTV).visibility = View.VISIBLE
                convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.VISIBLE
                convertView.findViewById<View>(R.id.downloadIV).visibility = View.GONE
                convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
                (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.download_pending)
                downloadId = offline.downloadid
                eMedicozDownloadManager.getFetchInstance().retry(offline.downloadid)
                eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offline.downloadid, notesData, savedOfflineListener, type)
            } else if (offline.requestInfo.status == Fetch.STATUS_DONE) {
                (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.downloaded_offline)
                convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
                convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
                (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
                singleViewCLick(notesData, type, offline)
            }
        } //for new download
        else if (offline == null || offline.requestInfo == null) {
            try {
                eMedicozDownloadManager.getInstance().showQualitySelectionPopup(activity, notesData.id, fileUrl,
                        Helper.getFileName(fileUrl, notesData.title, type), type, notesData.id
                ) { downloadid: Long ->
                    convertView.findViewById<View>(R.id.messageTV).visibility = View.VISIBLE
                    convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.VISIBLE
                    convertView.findViewById<View>(R.id.downloadIV).visibility = View.GONE
                    convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
                    (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.download_queued)
                    if (downloadid == Constants.MIGRATED_DOWNLOAD_ID.toLong()) {
                        convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
                        convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
                        convertView.findViewById<View>(R.id.messageTV).visibility = View.VISIBLE
                        (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                        (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.downloaded_offline)
                        if (convertView.findViewById<View>(R.id.downloadProgessBar).visibility != View.GONE) convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
                    } else if (downloadid != 0L) {
                        eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, downloadid, notesData, savedOfflineListener, type)
                    } else {
                        convertView.findViewById<View>(R.id.messageTV).visibility = View.INVISIBLE
                        convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
                        convertView.findViewById<View>(R.id.deleteIV).visibility = View.GONE
                        convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
                        (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.drawable.ic_baseline_download_icon)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            offline.requestInfo = eMedicozDownloadManager.getFetchInstance()[offline.downloadid]
            if (offline.requestInfo == null) {
                Toast.makeText(activity, activity.resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            } else {
                convertView.findViewById<View>(R.id.downloadIV).performClick()
            }
        }
    }

    fun singleViewCLick(data: NotesData, type: String?, offlineData: offlineData?) {
        var offlineData = offlineData
        if (offlineData == null) {
            offlineData = eMedicozDownloadManager.getOfflineDataIds(data.id, type, activity, data.id)
        }
        if (offlineData != null && offlineData.requestInfo == null) {
            // Helper.GoToVideoActivity(context, fileMetaData.getLink(), Const.VIDEO_STREAM);
            offlineData.requestInfo = eMedicozDownloadManager.getFetchInstance()[offlineData.downloadid]
        }
        if (offlineData != null && offlineData.requestInfo != null && offlineData.requestInfo.status == Fetch.STATUS_DONE) {
            when (type) {
                "pdf" -> //                    displayPdf(offlineData.getRequestInfo().getFilePath());
                    Helper.openPdfActivityWithDetails(activity, offlineData.link,
                            activity.filesDir.toString() + "/" + offlineData.link, data)
                "epub" -> {
                    val intent = Intent(activity, ePubActivity::class.java)
                    intent.putExtra("filePath", activity.filesDir.toString() + "/" + offlineData.link)
                    intent.putExtra(Const.EPUB_DATA, data)
                    activity.startActivity(intent)
                }
            }
        } else if (type != null && type == "pdf") if (!GenericUtils.isEmpty(data.fileUrl)) Helper.GoToWebViewActivity(activity, AES.decrypt(data.fileUrl)) else Toast.makeText(activity, "url is empty", Toast.LENGTH_SHORT).show()
    }

    private fun checkData(convertView: View, type: String, notesData: NotesData) {
        val offlineData = eMedicozDownloadManager.getOfflineDataIds(notesData.id, type, activity, notesData.id)
        (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.drawable.ic_baseline_download_icon)
        convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
        convertView.findViewById<View>(R.id.deleteIV).visibility = View.GONE
        convertView.findViewById<View>(R.id.messageTV).visibility = View.GONE
        convertView.findViewById<View>(R.id.downloadIV).visibility = View.GONE
        if (descriptionResponse.data.isPurchased == "1") {
            setDownloadedData(convertView, notesData, offlineData)
        } else {
            if (descriptionResponse.data.basic.mrp == "0") {
                setDownloadedData(convertView, notesData, offlineData)
            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                    if (descriptionResponse.data.basic.forDams == "0") {
                        setDownloadedData(convertView, notesData, offlineData)
                    } else {
                        convertView.findViewById<View>(R.id.lockedIV).visibility = View.VISIBLE
                        convertView.findViewById<View>(R.id.downloadIV).visibility = View.GONE
                    }
                } else {
                    if (descriptionResponse.data.basic.nonDams == "0") {
                        setDownloadedData(convertView, notesData, offlineData)
                    } else {
                        convertView.findViewById<View>(R.id.lockedIV).visibility = View.VISIBLE
                        convertView.findViewById<View>(R.id.downloadIV).visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setDownloadedData(convertView: View, notesData: NotesData, offlineData: offlineData?) {
        convertView.findViewById<View>(R.id.lockedIV).visibility = View.GONE
        convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
        if (offlineData != null) {
            if (offlineData.requestInfo == null) offlineData.requestInfo = eMedicozDownloadManager.getFetchInstance()[offlineData.downloadid]
            if (offlineData.requestInfo != null) {
                convertView.findViewById<View>(R.id.downloadProgessBar).visibility = if (offlineData.requestInfo.progress < 100) View.VISIBLE else View.GONE

                //4 conditions to check at the time of intialising the video

                //0. downloading in queue
                if (offlineData.requestInfo.status == Fetch.STATUS_QUEUED) {
                    convertView.findViewById<View>(R.id.downloadIV).visibility = View.GONE
                    convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
                    (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.download_queued)
                    downloadId = offlineData.downloadid
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offlineData.downloadid, notesData, savedOfflineListener, offlineData.type)
                } else if (offlineData.requestInfo.status == Fetch.STATUS_DOWNLOADING
                        && offlineData.requestInfo.progress < 100) {
                    convertView.findViewById<View>(R.id.downloadIV).visibility = View.GONE
                    convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
                    (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.download_queued)
                    convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.VISIBLE
                    (convertView.findViewById<View>(R.id.downloadProgessBar) as ProgressBar).progress = offlineData.requestInfo.progress
                    downloadId = offlineData.downloadid
                    eMedicozDownloadManager.getInstance().downloadProgressUpdate(convertView, offlineData.downloadid, notesData, savedOfflineListener, offlineData.type)

                } else if (offlineData.requestInfo.status == Fetch.STATUS_DONE && offlineData.requestInfo.progress == 100) {
                    (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.downloaded_offline)
                    convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
                    convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
                    (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                    convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE

//                            sendViewMessage();
                } else if (offlineData.requestInfo.status == Fetch.STATUS_PAUSED && offlineData.requestInfo.progress < 100) {
                    (convertView.findViewById<View>(R.id.downloadProgessBar) as ProgressBar).progress = offlineData.requestInfo.progress
                    convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
                    convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
                    (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.mipmap.download_pause)
                    (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.download_pasued)
                } else if (offlineData.requestInfo.status == Fetch.STATUS_ERROR && offlineData.requestInfo.progress < 100) {
                    (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.error_in_downloading)
                    convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
                    (convertView.findViewById<View>(R.id.downloadProgessBar) as ProgressBar).progress = offlineData.requestInfo.progress
                    convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
                    (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.mipmap.download_reload)
                }
                convertView.findViewById<View>(R.id.messageTV).visibility = if (offlineData.requestInfo != null) View.VISIBLE else View.GONE
            } else {
                convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
                convertView.findViewById<View>(R.id.deleteIV).visibility = View.GONE
                convertView.findViewById<View>(R.id.messageTV).visibility = View.GONE
                convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
            }
        } else if (descriptionResponse.data.basic.forDams.equals("0", ignoreCase = true)) {
            convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
            convertView.findViewById<View>(R.id.lockedIV).visibility = View.GONE
        } else {
            convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
            convertView.findViewById<View>(R.id.deleteIV).visibility = View.GONE
            convertView.findViewById<View>(R.id.messageTV).visibility = View.GONE
            convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
        }
    }

    private fun getDownloadCancelDialog(convertView: View, data: NotesData, ctx: Activity, type: String?, title: String?, message: String?) {
        val v = Helper.newCustomDialog(ctx, true, title, message)
        val btnCancel: Button
        val btnSubmit: Button
        btnCancel = v.findViewById(R.id.btn_cancel)
        btnSubmit = v.findViewById(R.id.btn_submit)
        btnCancel.text = ctx.getString(R.string.no)
        btnSubmit.text = ctx.getString(R.string.yes)
        btnCancel.setOnClickListener { Helper.dismissDialog() }
        btnSubmit.setOnClickListener {
            Helper.dismissDialog()
            eMedicozDownloadManager.removeOfflineData(data.id, type, ctx, data.id)
            convertView.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
            (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.drawable.ic_baseline_download_icon)
            convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
            (convertView.findViewById<View>(R.id.downloadProgessBar) as ProgressBar).progress = 0
            convertView.findViewById<View>(R.id.messageTV).visibility = View.GONE
            convertView.findViewById<View>(R.id.deleteIV).visibility = View.GONE
        }
    }

    //region Render test item and contains function of result and redirection
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTestData(binding: CourseDetailRowItemTestBinding, data: NotesData) {
//        data.setDisplay_review_answer("0");
        binding.dateLL.visibility = View.VISIBLE
        binding.nameTV.text = data.testSeriesName
        var status = ""
        Log.e("setTestData: ", data.isPaused)
        if (data.isPaused != null) {
            if (data.isPaused.equals("1", ignoreCase = true)) {
                status = activity.getString(R.string.resume)
                binding.statusTV.background = activity.resources.getDrawable(R.drawable.bg_btn)

            } else if (data.isPaused.equals("0", ignoreCase = true)) {
                if (!TextUtils.isEmpty(data.display_review_answer) && data.display_review_answer == "1") {
                    status = activity.getString(R.string.review_solution)
                    binding.statusTV.background = activity.resources.getDrawable(R.drawable.bg_btn)
                } else if (!TextUtils.isEmpty(data.display_reattempt) && data.display_reattempt == "1") {
                    status = activity.getString(R.string.reattempt)
                    binding.statusTV.background = activity.resources.getDrawable(R.drawable.bg_btn)
                } else {
                    status = activity.getString(R.string.result)
                    binding.statusTV.background = activity.resources.getDrawable(R.drawable.completed_btn_bg)
                }
            } else {
                status = activity.getString(R.string.start)
                binding.statusTV.background = activity.resources.getDrawable(R.drawable.bg_btn)
            }
        }
        binding.statusTV.text = status
        if (!TextUtils.isEmpty(data.testEndDate) &&
                data.testEndDate != "0") {
            binding.validityTv.visibility = View.GONE
            binding.validityTv.text = String.format("Valid Till: %s", data.testEndDate)
        } else
            binding.validityTv.visibility = View.GONE
        if (!GenericUtils.isEmpty(data.totalQuestions)) {
            binding.desTV.text = String.format("%s MCQs | %s mins", data.totalQuestions, data.timeInMins)
        }

        setLockVisibility(binding.root, data)

        val startDate = getMilliFromDate(data.testStartDate)
        val d = Date(startDate)
        val f: DateFormat = SimpleDateFormat("dd-MM-yyyy hh.mm aa")
        val dateString = f.format(d)
        binding.testStartDate.text = HtmlCompat.fromHtml("<font color='#CC0000'>Start date: </font>", HtmlCompat.FROM_HTML_MODE_LEGACY).toString() + dateString

        val endDate = getMilliFromDate(data.testEndDate)
        val dEnd = Date(endDate)
        val fEnd: DateFormat = SimpleDateFormat("dd-MM-yyyy hh.mm aa")
        val dateStringEnd = fEnd.format(dEnd)
        binding.testEndDate.text = HtmlCompat.fromHtml("<font color='#CC0000'>End date: </font>", HtmlCompat.FROM_HTML_MODE_LEGACY).toString() + dateStringEnd

        binding.statusTV.setOnClickListener {
            if (Helper.isConnected(activity)) {
                if (data.type == Constants.TestType.TEST) {
                    SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST)
                    if (descriptionResponse.data.isPurchased == "1" || isFreeTrial) {
                        managePurchasedCourseTest(data)
                    } else {
                        if (descriptionResponse.data.basic.mrp == "0") managePurchasedCourseTest(data)
                        else {
                            if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                                if (descriptionResponse.data.basic.forDams == "0")
                                    managePurchasedCourseTest(data)
                                else
                                    testQuizCourseDialog()
                            } else {
                                if (descriptionResponse.data.basic.nonDams == "0")
                                    managePurchasedCourseTest(data)
                                else
                                    testQuizCourseDialog()
                            }
                        }
                    }
                } else {
                    SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK)
                    if (descriptionResponse.data.isPurchased == "1" || isFreeTrial)
                        goToTestStartScreen(getTestSeriesObject(data))
                    else if (descriptionResponse.data.basic.mrp == "0")
                        goToTestStartScreen(getTestSeriesObject(data))
                    else if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                        if (descriptionResponse.data.basic.forDams == "0")
                            goToTestStartScreen(getTestSeriesObject(data))
                        else
                            testQuizCourseDialog()
                    } else {
                        if (descriptionResponse.data.basic.nonDams == "0")
                            getTestSeriesObject(data)
                        else
                            testQuizCourseDialog()
                    }
                }
            } else {
                Toast.makeText(activity, R.string.please_check_your_internet_connectivity, Toast.LENGTH_SHORT).show()
            }
        }
        binding.lockedIV.tag = data
        binding.lockedIV.setOnClickListener { binding.statusTV.performClick() }
    }

    private fun getTestSeriesObject(data: NotesData): TestSeries {
        val testSeries = TestSeries()
        testSeries.is_paused = data.isPaused
        testSeries.video_based = data.type
        testSeries.display_reattempt = data.display_reattempt
        testSeries.test_series_id = data.id
        testSeries.test_series_name = data.testSeriesName
        testSeries.total_questions = data.totalQuestions
        testSeries.avg_rating = "0"

        return testSeries
    }
    private fun goToTestStartScreen(course: TestSeries) {
        val testSeriesId = course.test_series_id
        Log.e("TSI", testSeriesId)
        if (course.is_paused.equals("", ignoreCase = true)) {
            if (course.video_based.equals("1", ignoreCase = true)) {
                goToTestActivityForStart(course, Constants.ResultExtras.VIDEO) // video based start
            } else {
                goToTestActivityForStart(course, Constants.ResultExtras.TEXT_PHOTO) // start
            }
        } else if (course.is_paused.equals("0", ignoreCase = true)) {
            if (!GenericUtils.isEmpty(course.display_reattempt)) {
                if (course.display_reattempt.equals("1", ignoreCase = true) && course.video_based.equals("1", ignoreCase = true)) {
                    goToTestActivityForComplete(course, Constants.ResultExtras.VIDEO) //video based complete
                } else {
                    goToTestActivityForComplete(course, Constants.ResultExtras.TEXT_PHOTO) // complete
                }
            } else {
                val resultTestSeries = ResultTestSeries()
                resultTestSeries.testSeriesName = course.test_series_name
                val resultScreen = Intent(activity, QuizActivity::class.java)
                resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_BASIC)
                resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, course.is_user_attemp)
                resultScreen.putExtra(Const.RESULT_SCREEN, resultTestSeries)
                //                resultScreen.putExtra(Constants.Extras.SUBJECT_NAME, ((TestQuizActivity) activity).titlefrag);
                activity.startActivity(resultScreen)
            }
        } else {
            if (course.video_based.equals("1", ignoreCase = true)) {
                goToTestActivityForPause(course, Constants.ResultExtras.VIDEO) // video based pause
            } else {
                goToTestActivityForPause(course, Constants.ResultExtras.TEXT_PHOTO) // pause
            }
        }
    }

    private fun goToTestActivityForStart(course: TestSeries, quizType: String) {
        val quizView = Intent(activity, TestQuizActionActivity::class.java)
        quizView.putExtra(Const.STATUS, false)
        quizView.putExtra(Const.TEST_SERIES_ID, course.test_series_id)
        quizView.putExtra(Constants.Extras.QUIZ_TYPE, quizType)
        quizView.putExtra(Constants.Extras.TITLE_NAME, course.test_series_name)
        quizView.putExtra(Constants.Extras.QUES_NUM, course.total_questions)
        quizView.putExtra(Constants.Extras.RATING, course.avg_rating)
        quizView.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.START)

        activity.startActivity(quizView)
    }

    private fun goToTestActivityForPause(course: TestSeries, quizType: String) {
        val quizView = Intent(activity, TestQuizActionActivity::class.java)
        quizView.putExtra(Const.STATUS, false)
        quizView.putExtra(Constants.Extras.QUIZ_TYPE, quizType)
        quizView.putExtra(Constants.Extras.TITLE_NAME, course.test_series_name)
        quizView.putExtra(Constants.Extras.QUES_NUM, course.total_questions)
        quizView.putExtra(Constants.Extras.RATING, course.avg_rating)
        quizView.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.PAUSE)
        quizView.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, course.is_user_attemp)
        quizView.putExtra(Const.TEST_SERIES_ID, course.test_series_id)

        activity.startActivity(quizView)
    }

    private fun goToTestActivityForComplete(course: TestSeries, quizType: String) {
        val quizView = Intent(activity, TestQuizActionActivity::class.java)
        quizView.putExtra(Const.STATUS, false)
        quizView.putExtra(Constants.Extras.QUIZ_TYPE, quizType)
        quizView.putExtra(Constants.Extras.TITLE_NAME, course.test_series_name)
        quizView.putExtra(Constants.Extras.QUES_NUM, course.total_questions)
        quizView.putExtra(Constants.Extras.RATING, course.avg_rating)
        quizView.putExtra(Constants.Extras.SCREEN_TYPE, Constants.ResultExtras.COMPLETE)
        quizView.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, course.is_user_attemp)
        quizView.putExtra(Const.TEST_SERIES_ID, course.test_series_id)

        activity.startActivity(quizView)
    }

    private fun setLockVisibility(view: View, data: NotesData) {
        val statusTV = view.findViewById<View>(R.id.statusTV)
        val lockedIV = view.findViewById<View>(R.id.lockedIV)

        if (descriptionResponse.data.isPurchased == "1" || isFreeTrial) {
            statusTV?.visibility = View.VISIBLE
            lockedIV.visibility = View.GONE
        } else {
            if (descriptionResponse.data.basic.mrp == "0") {
                statusTV?.visibility = View.VISIBLE
                lockedIV.visibility = View.GONE
            } else {
                if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
                    if (descriptionResponse.data.basic.forDams == "0") {
                        statusTV?.visibility = View.VISIBLE
                        lockedIV.visibility = View.GONE
                    } else {
                        setVisibility(statusTV, lockedIV, data)
                    }
                } else {
                    if (descriptionResponse.data.basic.nonDams == "0") {
                        statusTV?.visibility = View.VISIBLE
                        lockedIV.visibility = View.GONE
                    } else {
                        setVisibility(statusTV, lockedIV, data)
                    }
                }
            }
        }
        if (data.type.equals("epub", ignoreCase = true) || data.type.equals("pdf", ignoreCase = true))
            return
        if (!isTestAccessible()) return

        val startMillis = getMilliFromDate(data.testStartDate)
        val endMillis = getMilliFromDate(data.testEndDate)
        Log.e("MILIS : ", startMillis.toString())
        val currentTimeStamp = System.currentTimeMillis()
        if (currentTimeStamp < startMillis) {
            lockedIV.visibility = View.VISIBLE
            statusTV?.visibility = View.GONE
            return
        } else {
            statusTV?.visibility = View.VISIBLE
            lockedIV.visibility = View.GONE
        }
        if (currentTimeStamp > endMillis) {
            if (data.isPaused.equals("0", ignoreCase = true)) {
                statusTV?.visibility = View.VISIBLE
                lockedIV.visibility = View.GONE
            } else {
                statusTV?.visibility = View.GONE
                lockedIV.visibility = View.VISIBLE
            }
        } else {
            statusTV?.visibility = View.VISIBLE
            lockedIV.visibility = View.GONE
        }
    }

    private fun isTestAccessible(): Boolean {

        return if (descriptionResponse.data.isPurchased == "1" || isFreeTrial ||
                descriptionResponse.data.basic.mrp.isEmpty() || descriptionResponse.data.basic.mrp == "0") {
            true
        } else if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
            descriptionResponse.data.basic.forDams == "0"
        } else {
            descriptionResponse.data.basic.nonDams == "0"
        }
    }

    private fun setVisibility(statusTV: View?, lockedIV: View, data: NotesData) {
        if (data.isLocked == null || data.isLocked.equals("0", ignoreCase = true)) {
            lockedIV.visibility = View.VISIBLE
            statusTV?.visibility = View.GONE
        } else {
            statusTV?.visibility = View.VISIBLE
            lockedIV.visibility = View.GONE
        }
    }

    private fun managePurchasedCourseTest(data: NotesData) {
        if (!GenericUtils.isEmpty(data.keyData) && data.keyData.equals(Constants.TestType.QUIZ, ignoreCase = true)) SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.Q_BANK) else SharedPreference.getInstance().putString(Constants.Extras.TEST_TYPE, Constants.TestType.TEST)
        if (data.isPaused != null) {
            if (data.isPaused.equals("", ignoreCase = true)) {
                val millis = getMilliFromDate(data.testStartDate)
                val correctDateFormat = Helper.getFormatedDate(millis)
                Log.e("Current time in AM/PM: ", correctDateFormat)
                if (System.currentTimeMillis() < millis) {
                    Helper.newCustomDialog(activity,
                            activity.getString(R.string.app_name),
                            activity.getString(R.string.this_test_will_be_available_on).toString() + " " + correctDateFormat,
                            false,
                            activity.getString(R.string.close),
                            ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_red))
                } else {
                    if (getMilliFromDate(data.testEndDate) < System.currentTimeMillis()) {
                        Helper.newCustomDialog(activity,
                                activity.getString(R.string.app_name),
                                activity.getString(R.string.test_date_expired),
                                false,
                                activity.getString(R.string.close),
                                ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_red))
                    } else {
                        /* if (course.getVideo_based().equalsIgnoreCase("1")) {
                        Intent quizView = new Intent(context, VideoTestBaseActivity.class);
                        quizView.putExtra(Const.STATUS, false);
                        quizView.putExtra(Const.TEST_SERIES_ID, data.getId());
                        context.startActivity(quizView);
                    } else {*/
                        eMedicozApp.getInstance().refreshTestScreen = true
                        val quizView = Intent(activity, TestBaseActivity::class.java)
                        quizView.putExtra(Const.STATUS, false)
                        quizView.putExtra(Const.TEST_SERIES_ID, data.id)
                        activity.startActivity(quizView)

                        /*if (context instanceof NewTestQuizActivity) {
                        ((NewTestQuizActivity) context).lastPos = position;
                    }*/
                    }
                }
            } else if (data.isPaused == "1") {
                if (getMilliFromDate(data.testEndDate) < System.currentTimeMillis()) {
                    Helper.newCustomDialog(activity,
                            activity.getString(R.string.app_name),
                            activity.getString(R.string.test_date_expired),
                            false,
                            activity.getString(R.string.close),
                            ContextCompat.getDrawable(activity, R.drawable.bg_round_corner_fill_red))
                } else {
                    eMedicozApp.getInstance().refreshTestScreen = true
                    val quizView = Intent(activity, TestBaseActivity::class.java)
                    quizView.putExtra(Const.STATUS, false)
                    quizView.putExtra(Const.TEST_SERIES_ID, data.id)
                    activity.startActivity(quizView)
                }
            } else {
                /*if (!TextUtils.isEmpty(data.display_review_answer) && data.display_review_answer == "1") {
                    val intent = Intent(activity, ViewSolutionWithTabNew::class.java)
                    intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, data.segId)
                    intent.putExtra(Constants.Extras.NAME, data.testSeriesName)
                    activity.startActivity(intent)
                } else*/
                    if (!TextUtils.isEmpty(data.display_reattempt) && data.display_reattempt == "1") {
                    /**
                     * This network call will be used with the check of reattempt
                     * if its 1 then this network call will be used
                     * but currently we are not getting any check that's why it's commented
                     */
                    networkCallForTestSeriesResult(data)
                } else {
                    if (!GenericUtils.isEmpty(data.testResultDate)) {
                        val tsLong = System.currentTimeMillis()
                        if (tsLong < data.testResultDate.toLong() * 1000) {
                            val resultScreen = Intent(activity, QuizActivity::class.java)
                            resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_AWAIT)
                            resultScreen.putExtra(Constants.Extras.DATE, data.testResultDate)
                            activity.startActivity(resultScreen)
                        } else {
                            goToResultScreen(data)
                        }
                    } else {
                        goToResultScreen(data)
                    }
                }
            }
        }
    }
    private fun goToResultScreen(data: NotesData) {
        val resultScreen = Intent(activity, QuizActivity::class.java)
        resultScreen.putExtra(Const.FRAG_TYPE, Const.RESULT_SCREEN)
        resultScreen.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, data.segId)
        activity.startActivity(resultScreen)
    }

    private fun networkCallForTestSeriesResult(notesData: NotesData) {
        if (!Helper.isConnected(activity)) {
            Toast.makeText(activity, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        val progress = Progress(activity)
        progress.setCancelable(false)
        progress.show()
        val apiInterface = ApiClient.createService(ApiInterface::class.java)
        val response = apiInterface.getCompleteInfTestSeriesResult(SharedPreference.getInstance().loggedInUser.id,
                notesData.id)
        response.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                progress.dismiss()
                if (response.body() != null) {
                    val jsonObject = response.body()
                    val jsonResponse: JSONObject
                    try {
                        jsonResponse = JSONObject(jsonObject.toString())
                        val gson = Gson()
                        if (jsonResponse.optString(Const.STATUS) == Const.TRUE) {
                            val data = GenericUtils.getJsonArray(jsonResponse)
                            testSeriesResultList.clear()
                            for (i in 0 until data.length()) {
                                val dataObj = data.optJSONObject(i)
                                val testSeriesResult = gson.fromJson(dataObj.toString(), TestSeriesResultData::class.java)
                                testSeriesResultList.add(testSeriesResult)
                            }
                            showPopupResult(testSeriesResultList, notesData)
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showExceptionMsg(activity, t)
            }
        })
    }

    private fun showPopupResult(resultData: java.util.ArrayList<TestSeriesResultData>?, course: NotesData) {
        val li = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = li.inflate(R.layout.dialog_test_reattempt, null, false)
        val quizBasicInfoDialog = Dialog(activity)

        quizBasicInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        quizBasicInfoDialog.setCanceledOnTouchOutside(false)
        quizBasicInfoDialog.setContentView(v)
        quizBasicInfoDialog.show()

        val recyclerView: RecyclerView
        val continueReattempt: Button
        val dialogTestName: TextView
        recyclerView = v.findViewById(R.id.reattemptDialogRV)
        continueReattempt = v.findViewById(R.id.continueReattempt)
        dialogTestName = v.findViewById(R.id.dialogTestName)
        if (resultData != null && resultData[0].test_series_name != null) {
            dialogTestName.text = resultData[0].test_series_name
        }
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = ReattemptDialogAdapter(activity, resultData, activity, quizBasicInfoDialog)
        continueReattempt.setOnClickListener {
            eMedicozApp.getInstance().refreshTestScreen = true
            val quizView = Intent(activity, TestBaseActivity::class.java)
            quizView.putExtra(Const.STATUS, false)
            quizView.putExtra(Const.TEST_SERIES_ID, course.id)
            quizView.putExtra(Constants.Extras.NAME, course.testSeriesName)
            activity.startActivity(quizView)
            quizBasicInfoDialog.dismiss()
        }
    }

    private fun getMilliFromDate(dateFormat: String?): Long {
        var date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        try {
            date = formatter.parse(dateFormat)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        Log.e("Today is ", date.toString())
        return Objects.requireNonNull(date).time
    }

    private fun testQuizCourseDialog() {
        val v = Helper.newCustomDialog(activity, true, activity.getString(R.string.app_name),
                activity.getString(R.string.you_have_to_buy_this_course_to_attempt_test))
        val space: Space
        val btnCancel: Button
        val btnSubmit: Button

        space = v.findViewById(R.id.space)
        btnCancel = v.findViewById(R.id.btn_cancel)
        btnSubmit = v.findViewById(R.id.btn_submit)
        space.visibility = View.GONE
        btnCancel.visibility = View.GONE
        btnSubmit.text = activity.getString(R.string.enroll)
        btnSubmit.setOnClickListener {
            Helper.dismissDialog()
            Helper.goToCourseInvoiceScreen(activity, getData(descriptionResponse))
        }
    }
    //endregion

    fun getData(descriptionResponse: DescriptionResponse): SingleCourseData? {
        val singleCourseData = SingleCourseData()

        singleCourseData.course_type = descriptionResponse.data.basic.courseType
        singleCourseData.for_dams = descriptionResponse.data.basic.forDams
        singleCourseData.non_dams = descriptionResponse.data.basic.nonDams
        singleCourseData.mrp = descriptionResponse.data.basic.mrp
        singleCourseData.id = descriptionResponse.data.basic.id
        singleCourseData.cover_image = descriptionResponse.data.basic.coverImage
        singleCourseData.title = descriptionResponse.data.basic.title
        singleCourseData.learner = descriptionResponse.data.basic.learner
        singleCourseData.rating = descriptionResponse.data.basic.rating
        singleCourseData.gst_include = descriptionResponse.data.basic.gstInclude
        if (!GenericUtils.isEmpty(descriptionResponse.data.basic.gst))
            singleCourseData.gst = descriptionResponse.data.basic.gst
        else
            singleCourseData.gst = "18"
        if (!GenericUtils.isEmpty(descriptionResponse.data.basic.pointsConversionRate))
            singleCourseData.points_conversion_rate = descriptionResponse.data.basic.pointsConversionRate
        else
            singleCourseData.points_conversion_rate = "100"

        return singleCourseData
    }

    override fun updateUIForDownloadedVideo(convertView: View?, data: NotesData?, requestInfo: RequestInfo?, id: Long) {

        convertView!!.findViewById<View>(R.id.downloadIV).visibility = View.VISIBLE
        convertView.findViewById<View>(R.id.deleteIV).visibility = View.VISIBLE
        convertView.findViewById<View>(R.id.downloadProgessBar).visibility = View.GONE
        (convertView.findViewById<View>(R.id.downloadIV) as ImageView).setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
        convertView.findViewById<View>(R.id.messageTV).visibility = View.VISIBLE
        (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.downloaded_offline)
        eMedicozDownloadManager.addOfflineDataIds(data?.id, data?.fileUrl, context as Activity,
                false, true, Const.VIDEOS, requestInfo!!.id, data?.id)

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
                (convertView.findViewById<View>(R.id.messageTV) as TextView).text = "Downloading...$value%"
            } else (convertView.findViewById<View>(R.id.messageTV) as TextView).setText(R.string.download_pending)
        }

        (convertView.findViewById<View>(R.id.downloadProgessBar) as ProgressBar).progress = value!!
    }


}