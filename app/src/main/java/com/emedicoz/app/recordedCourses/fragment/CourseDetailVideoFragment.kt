package com.emedicoz.app.recordedCourses.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity
import com.emedicoz.app.courseDetail.adapter.CourseNotesAdapter
import com.emedicoz.app.courseDetail.adapter.CourseVideoAdapter
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.databinding.FragmentRecordedDetailVideoBinding
import com.emedicoz.app.modelo.liveclass.courses.DescriptionData
import com.emedicoz.app.recordedCourses.model.CurriculumData
import com.emedicoz.app.recordedCourses.model.CurriculumDataObject
import com.emedicoz.app.recordedCourses.model.SubCurriculumData
import com.emedicoz.app.recordedCourses.model.detaildatanotes.DataObject
import com.emedicoz.app.recordedCourses.model.detaildatavideo.TopicVideoDatum
import com.emedicoz.app.recordedCourses.model.detaildatavideo.VideoItemData
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.retrofit.RetrofitResponse
import com.emedicoz.app.retrofit.apiinterfaces.LiveCourseInterface
import com.emedicoz.app.utilso.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CourseDetailVideoFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(fragType: String, courseId: String): CourseDetailVideoFragment {
            val fragment = CourseDetailVideoFragment()
            val args = Bundle()
            args.putString(Const.FRAG_TYPE, fragType)
            args.putString(Const.COURSE_ID, courseId)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(fragType: String, descriptionData: DescriptionData): CourseDetailVideoFragment {
            val fragment = CourseDetailVideoFragment()
            val args = Bundle()
            args.putString(Const.FRAG_TYPE, fragType)
            args.putSerializable(Const.DESCRIPTION, descriptionData)
            args.putString(Const.COURSE_ID, descriptionData.basic.id)
            fragment.arguments = args
            return fragment
        }
    }

    private var courseId: String? = ""
    private var fragType: String? = ""
    private lateinit var progress: Progress
    private lateinit var descriptionData: DescriptionData

    // Variables Declaration
    private lateinit var rdBinding: FragmentRecordedDetailVideoBinding

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = rdBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rdBinding = FragmentRecordedDetailVideoBinding.inflate(inflater, container, false)
        progress = Progress(activity)
        progress.setCancelable(false)

        val view = binding.root
        if (arguments != null) {
            fragType = arguments!!.getString(Const.FRAG_TYPE)
            courseId = arguments!!.getString(Const.COURSE_ID)
            if (arguments!!.getSerializable(Const.DESCRIPTION) != null)
                descriptionData = arguments!!.getSerializable(Const.DESCRIPTION) as DescriptionData
        }
        if (fragType.equals(Const.VIDEOS))
            getVideoDataOfCourseDetail()
        else
            getTestEpubPdfDataOfCourseDetail()

        return view
    }

    override fun onResume() {
        super.onResume()
        if (fragType.equals(Const.NOTES_TEST) && eMedicozApp.getInstance().refreshTestScreen) {
            getTestEpubPdfDataOfCourseDetail()
            eMedicozApp.getInstance().refreshTestScreen = false
        }
    }

    private fun renderVideoData(jsonResponse: JSONObject) {

        val gson = Gson()
        val jsonVideoList: JSONArray
        val videoList: ArrayList<TopicVideoDatum> = ArrayList()
        jsonVideoList = GenericUtils.getJsonObject(jsonResponse).optJSONArray("topic_video_data")
        val courseTotalData = GenericUtils.getJsonObject(jsonResponse).optJSONObject("course_total_data")
        val totalModule = courseTotalData.optInt("total_module")
        val totalTopic = courseTotalData.optInt("total_topic")
        val totalVideo = courseTotalData.optInt("total_video")


        var totalVideosCount = 0
        for (i in 0 until jsonVideoList.length()) {

            val jsonVideoObject = jsonVideoList.get(i)
            val videoObject = gson.fromJson(Objects.requireNonNull(jsonVideoObject).toString(), TopicVideoDatum::class.java)
            var totalcount: Long = 0


            if (isCourseAccessible()) {
                try {
                    for (a in 0 until videoObject.list.size) {

                        val list = videoObject.list[a].videoList
                        var videoList = ArrayList<VideoItemData>()

                        for (b in 0 until list.size) {

                            if (list[b].fileUrl != null && !GenericUtils.isEmpty(list[b].fileUrl)) {
                                videoList.add(list[b])
                                totalVideosCount++
                                totalcount++
                            } else {
                                if (list[b].liveUrl != null && !GenericUtils.isEmpty(list[b].liveUrl)) {
                                    videoList.add(list[b])
                                    totalVideosCount++
                                    totalcount++
                                }
                            }

                        }

                        videoObject.list[a].videoList = videoList

                    }

                    videoObject.totalVideos = totalcount

                    videoList.add(videoObject)

                    binding.curriculumHeaderDetails.text = "$totalModule Modules, $totalTopic Topics, $totalVideosCount Videos "

                } catch (e: Exception) {

                }

            } else {
                videoList.add(videoObject)
                binding.curriculumHeaderDetails.text = "$totalModule Modules, $totalTopic Topics, $totalVideo Videos "

            }
        }



        binding.curriculumExpandList.apply {
            setAdapter(CourseVideoAdapter(context, videoList, courseId.toString()))
            setListViewHeight(this, 0)
            expandGroup(0, true)
//            setListViewHeight(this)
//            setOnGroupClickListener { parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long ->
//                setListViewHeight(parent!!, groupPosition)
//                false
//            }
        }
    }

    private fun isCourseAccessible(): Boolean {

        return if (descriptionData.isPurchased == "1" || (activity as CourseDetailActivity).isFreeTrial ||
                descriptionData.basic.mrp.isEmpty() || descriptionData.basic.mrp == "0") {
            true
        } else if (!TextUtils.isEmpty(SharedPreference.getInstance().loggedInUser.dams_tokken)) {
            descriptionData.basic.forDams == "0"
        } else {
            descriptionData.basic.nonDams == "0"
        }

    }

    private fun getVideoDataOfCourseDetail() {
        // asynchronous operation to get video list for course detail screen.
        progress.show()
        val apiInterface = ApiClient.createService(LiveCourseInterface::class.java)
        val response = apiInterface.getVideoDataOfCourseDetail(SharedPreference.getInstance().loggedInUser.id, courseId)
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

                            renderVideoData(jsonResponse)
                        } else {
                            Helper.showErrorLayoutForNav("getVideoData", activity, 1, 0)
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav("getVideoData", activity, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav("getVideoData", activity, 1, 1)
            }
        })
    }

    private fun getTestEpubPdfDataOfCourseDetail() {
        // asynchronous operation to get notes, test, pdf n epub list for course detail screen.
        progress.show()
        val apiInterface = ApiClient.createService(LiveCourseInterface::class.java)
        val response = apiInterface.getTestEpubPdfDataOfCourseDetail(SharedPreference.getInstance().loggedInUser.id, courseId)
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

                            renderNotesTestData(jsonResponse)

                        } else {
                            Helper.showErrorLayoutForNav("getTestEpubPdfDataOfCourseDetail", activity, 1, 0)
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE))
                        }
                    } catch (e: JSONException) {
                        Helper.showErrorLayoutForNav("getTestEpubPdfDataOfCourseDetail", activity, 1, 0)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                progress.dismiss()
                Helper.showErrorLayoutForNav("getTestEpubPdfDataOfCourseDetail", activity, 1, 1)
            }
        })
    }

    private fun renderNotesTestData(jsonResponse: JSONObject) {

        val gson = Gson()
        val jsonNotesList: JSONArray
        val notesList: ArrayList<DataObject> = ArrayList()
        jsonNotesList = jsonResponse.optJSONArray("data")

        for (i in 0 until jsonNotesList.length()) {

            val jsonNotesObject = jsonNotesList.get(i)
            val notesObject = gson.fromJson(Objects.requireNonNull(jsonNotesObject).toString(), DataObject::class.java)
            notesList.add(notesObject)
        }

        binding.curriculumExpandList.apply {
            try {
                setAdapter(CourseNotesAdapter(context, notesList, courseId.toString()))
                expandGroup(0, true)
                setListViewHeight(this, 0)
//                setListViewHeight(this)
//                setOnGroupClickListener { parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long ->
//                    setListViewHeight(parent!!, groupPosition)
//                    false
//                }
            } finally {

            }
        }
    }

    private fun setListViewHeight(listView: ExpandableListView) {
        val listAdapter = listView.expandableListAdapter
        var totalHeight = 0
        for (i in 0 until listAdapter.groupCount) {
            val groupView = listAdapter.getGroupView(i, true, null, listView)
            groupView.measure(0, View.MeasureSpec.UNSPECIFIED)
            totalHeight += groupView.measuredHeight
//            break // height gets too long if we calculate for each group item
            if (listView.isGroupExpanded(i)) {
                for (j in 0 until listAdapter.getChildrenCount(i)) {
                    val listItem = listAdapter.getChildView(i, j, false, null, listView)
                    listItem.measure(0, View.MeasureSpec.UNSPECIFIED)
                    totalHeight += listItem.measuredHeight
                }
            }
        }
        val params = listView.layoutParams
        params.height = (totalHeight
                + listView.dividerHeight * (listAdapter.groupCount - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }

    private fun setListViewHeight(listView: ExpandableListView, group: Int) {
        val listAdapter = listView.expandableListAdapter
        var totalHeight = 0
        val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.width,
                View.MeasureSpec.EXACTLY)
        for (i in 0 until listAdapter.groupCount) {
            val groupItem = listAdapter.getGroupView(i, false, null, listView)
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += groupItem.measuredHeight
            if (i == group /*((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))*/) {
                for (j in 0 until listAdapter.getChildrenCount(i)) {
                    val listItem = listAdapter.getChildView(i, j, false, null,
                            listView)
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                    totalHeight += listItem.measuredHeight
                }
            }
        }
        totalHeight += 100
        val params = listView.layoutParams
        var height = (totalHeight
                + listView.dividerHeight * (listAdapter.groupCount - 1))
        if (height < 10) height = 200
        params.height = height
        listView.layoutParams = params
        listView.requestLayout()
    }

    private fun loadCurriculum(): ArrayList<CurriculumData> {
        var list: ArrayList<CurriculumData> = ArrayList()
        for (i in 1..3) {

            val subList: ArrayList<SubCurriculumData> = ArrayList()
            val sublistChapters: ArrayList<CurriculumDataObject> = ArrayList()
            sublistChapters.add(CurriculumDataObject("Chapter Video 1"))
            sublistChapters.add(CurriculumDataObject("Chapter Video 2"))

            subList.add(SubCurriculumData("Introduction $i", sublistChapters))
            subList.add(SubCurriculumData("Chapter 1 $i", sublistChapters))


            val curriculumData = CurriculumData("Module $i", subList)


            list.add(curriculumData)
        }
        return list
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        rdBinding = null
    }

}