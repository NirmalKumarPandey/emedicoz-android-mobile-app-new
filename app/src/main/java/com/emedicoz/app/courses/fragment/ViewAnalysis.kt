package com.emedicoz.app.courses.fragment

import com.emedicoz.app.utilso.MyNetworkCall.MyNetworkCallBack
import android.app.Activity
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries
import com.emedicoz.app.modelo.SubjectWiseResultPart
import com.emedicoz.app.modelo.SubjectWiseResultSubject
import com.emedicoz.app.modelo.SubjectWiseResultData
import com.emedicoz.app.courses.adapter.SubjectWiseResultAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.emedicoz.app.courses.activity.QuizActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.emedicoz.app.utilso.network.API
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.emedicoz.app.R
import com.google.gson.JsonObject
import com.emedicoz.app.databinding.FragmentViewAnalysisBinding
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface
import com.emedicoz.app.utilso.*
import org.json.JSONException
import org.json.JSONObject
import com.google.gson.Gson
import retrofit2.Call
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ViewAnalysis : Fragment(), View.OnClickListener, MyNetworkCallBack {
    private lateinit var binding: FragmentViewAnalysisBinding
    var activity: Activity? = null
    var testSegmentId: String? = null
    var resultTestSeries: ResultTestSeries? = null
    var partList: ArrayList<String>? = null
    var partAList: ArrayList<SubjectWiseResultPart>? = null
    var partBList: ArrayList<SubjectWiseResultPart>? = null
    var partCList: ArrayList<SubjectWiseResultPart>? = null
    var partASubjectList: ArrayList<SubjectWiseResultSubject>? = null
    var partBSubjectList: ArrayList<SubjectWiseResultSubject>? = null
    var partCSubjectList: ArrayList<SubjectWiseResultSubject>? = null
    var subjectData: SubjectWiseResultData? = null
    var subjectWiseResults: ArrayList<SubjectWiseResultSubject>? = null
    var adapter: SubjectWiseResultAdapter? = null
    private lateinit var myNetworkCall: MyNetworkCall
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()
        if (arguments != null) {
            testSegmentId = arguments!!.getString(Constants.ResultExtras.TEST_SEGMENT_ID)
            resultTestSeries =
                arguments!!.getSerializable(Const.RESULT_TEST_SERIES) as ResultTestSeries?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as QuizActivity?)!!.checkGraphVisibility()
        initView()
    }

    private fun initView() {
        myNetworkCall = MyNetworkCall(this, activity)
        binding.apply {
            subjectRV.layoutManager = LinearLayoutManager(activity)
            subjectRV.isNestedScrollingEnabled = false
        }
        subjectWiseResults = ArrayList()
        partList = ArrayList()
        partAList = ArrayList()
        partBList = ArrayList()
        partCList = ArrayList()
        partASubjectList = ArrayList()
        partBSubjectList = ArrayList()
        partCSubjectList = ArrayList()
        bindControls()
    }

    private fun bindControls() {
        binding.apply {
            allTV.setOnClickListener(this@ViewAnalysis)
            partATV.setOnClickListener(this@ViewAnalysis)
            partBTV.setOnClickListener(this@ViewAnalysis)
            partCTV.setOnClickListener(this@ViewAnalysis)
            myScoreTV.text = resultTestSeries!!.marks
            averageScoreTV.text = resultTestSeries!!.averageMarks.avg_marks
            toppersScoreTV.text = resultTestSeries!!.topperScore
            totalScoreTVOne.text = Const.OUT_OF + resultTestSeries!!.testSeriesMarks
            totalScoreTVTwo.text = Const.OUT_OF + resultTestSeries!!.testSeriesMarks
            totalScoreTVThree.text = Const.OUT_OF + resultTestSeries!!.testSeriesMarks
        }
        (activity as QuizActivity?)!!.graphTV.setOnClickListener {
            if (Helper.isConnected(activity)) {
                val intent = Intent(activity, QuizActivity::class.java)
                intent.putExtra(Const.FRAG_TYPE, "GRAPH")
                intent.putExtra(Const.RESULT_SCREEN, resultTestSeries)
                intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId)
                activity!!.startActivity(intent)
            } else {
                Toast.makeText(
                    activity,
                    activity!!.resources.getString(R.string.please_check_your_internet_connectivity),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        netoworkCallSubjectResult(API.API_GET_SUBJECT_ANALYSIS)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.allTV -> {
                adapter = SubjectWiseResultAdapter(activity, subjectWiseResults)
                setActiveView(0)
            }
            R.id.partATV -> {
                adapter = SubjectWiseResultAdapter(activity, partASubjectList)
                setActiveView(1)
            }
            R.id.partBTV -> {
                adapter = SubjectWiseResultAdapter(activity, partBSubjectList)
                setActiveView(2)
            }
            R.id.partCTV -> {
                adapter = SubjectWiseResultAdapter(activity, partCSubjectList)
                setActiveView(3)
            }
            else -> {
            }
        }
        binding.subjectRV.adapter = adapter
    }

    private fun setActiveView(type: Int) {
        binding.apply {
            allTV.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
            allTV.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
            partATV.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
            partATV.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
            partBTV.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
            partBTV.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
            partCTV.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.transparent))
            partCTV.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        }
        if (type == 0) {
            binding.apply {
                allTV.background = ContextCompat.getDrawable(activity!!, R.drawable.bg_btn)
                allTV.setTextColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.left_panel_header_text_color
                    )
                )
            }
        } else if (type == 1) {
            binding.apply {
                partATV.background = ContextCompat.getDrawable(activity!!, R.drawable.bg_btn)
                partATV.setTextColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.left_panel_header_text_color
                    )
                )
            }
        } else if (type == 2) {
            binding.apply {
                partBTV.background = ContextCompat.getDrawable(activity!!, R.drawable.bg_btn)
                partBTV.setTextColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.left_panel_header_text_color
                    )
                )
            }
        } else if (type == 3) {
            binding.apply {
                partCTV.background = ContextCompat.getDrawable(activity!!, R.drawable.bg_btn)
                partCTV.setTextColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.left_panel_header_text_color
                    )
                )
            }
        }
    }

    fun netoworkCallSubjectResult(api: String?) {
        myNetworkCall.NetworkAPICall(api, true)
    }

    override fun getAPI(apiType: String, service: FlashCardApiInterface): Call<JsonObject> {
        val params: MutableMap<String, Any?> = HashMap()
        params[Const.USER_ID] = SharedPreference.getInstance().loggedInUser.id
        params["result_id"] = testSegmentId
        Log.e(TAG, "getAPI: $params")
        return service.postData(apiType, params)
    }

    @Throws(JSONException::class)
    override fun successCallBack(jsonObject: JSONObject, apiType: String) {
        Helper.showErrorLayoutForNoNav(API.API_GET_SUBJECT_ANALYSIS, activity, 0, 0)
        val data = GenericUtils.getJsonObject(jsonObject)
        subjectData = Gson().fromJson(data.toString(), SubjectWiseResultData::class.java)
        if (!GenericUtils.isListEmpty(subjectData!!.getPart())) {
            binding.cardPart.visibility = View.VISIBLE
        } else {
            binding.cardPart.visibility = View.GONE
        }
        subjectWiseResults!!.addAll(subjectData!!.getSubject())
        setAdapter()
        extractParts()
        setPartData()
        setPartWiseSubject()
    }

    override fun errorCallBack(jsonString: String, apiType: String) {
        if (jsonString.equals(
                activity!!.resources.getString(R.string.internet_error_message),
                ignoreCase = true
            )
        ) {
            Helper.showErrorLayoutForNoNav(API.API_GET_SUBJECT_ANALYSIS, activity, 1, 1)
        } else {
            Helper.showErrorLayoutForNoNav(API.API_GET_SUBJECT_ANALYSIS, activity, 1, 2)
        }
    }

    private fun setAdapter() {
        if (!subjectWiseResults!!.isEmpty()) {
            adapter = SubjectWiseResultAdapter(activity, subjectWiseResults)
            binding.subjectRV.adapter = adapter
        }
    }

    private fun extractParts() {
        for (i in subjectData!!.part.indices) {
            if (!partList!!.contains(subjectData!!.part[i].partName)) {
                partList!!.add(subjectData!!.part[i].partName)
                partList!!.sort()
            }
        }
    }

    private fun setPartData() {
        for (i in subjectData!!.part.indices) {
            val partName = subjectData!!.part[i].partName
            if (partName.equals(partList!![0], ignoreCase = true)) {
                partAList!!.add(subjectData!!.part[i])
            } else if (partName.equals(partList!![1], ignoreCase = true)) {
                partBList!!.add(subjectData!!.part[i])
            } else if (partName.equals(partList!![2], ignoreCase = true)) {
                partCList!!.add(subjectData!!.part[i])
            }
        }
    }

    private fun setPartWiseSubject() {
        setPartASubject()
        setPartBSubject()
        setPartCSubject()
    }

    private fun setPartASubject() {
        for (i in partAList!!.indices) {
            for (j in subjectData!!.subject.indices) {
                if (partAList!![i].sectionId == subjectData!!.subject[j].subjectId) {
                    partASubjectList!!.add(subjectData!!.subject[j])
                }
            }
        }
    }

    private fun setPartBSubject() {
        for (i in partBList!!.indices) {
            for (j in subjectData!!.subject.indices) {
                if (partBList!![i].sectionId == subjectData!!.subject[j].subjectId) {
                    partBSubjectList!!.add(subjectData!!.subject[j])
                }
            }
        }
    }

    private fun setPartCSubject() {
        for (i in partCList!!.indices) {
            for (j in subjectData!!.subject.indices) {
                if (partCList!![i].sectionId == subjectData!!.subject[j].subjectId) {
                    partCSubjectList!!.add(subjectData!!.subject[j])
                }
            }
        }
    }

    companion object {
        private const val TAG = "ViewAnalysis"

        @JvmStatic
        fun newInstance(status: String?, resultTestSeries: ResultTestSeries?): ViewAnalysis {
            val fragment = ViewAnalysis()
            val args = Bundle()
            args.putSerializable(Const.RESULT_TEST_SERIES, resultTestSeries)
            args.putString(Constants.ResultExtras.TEST_SEGMENT_ID, status)
            fragment.arguments = args
            return fragment
        }
    }
}