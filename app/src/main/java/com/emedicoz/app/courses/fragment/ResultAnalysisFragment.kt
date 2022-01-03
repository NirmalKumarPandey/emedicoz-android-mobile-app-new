package com.emedicoz.app.courses.fragment

import com.emedicoz.app.utilso.MyNetworkCall.MyNetworkCallBack
import android.app.Activity
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.emedicoz.app.testmodule.activity.TestBaseActivity
import com.emedicoz.app.courses.activity.QuizActivity
import com.emedicoz.app.testmodule.activity.ViewSolutionWithTabNew
import com.emedicoz.app.courses.activity.VideoSolution
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.emedicoz.app.R
import com.emedicoz.app.utilso.network.API
import com.google.gson.JsonObject
import com.emedicoz.app.databinding.AnalysisScreenBinding
import org.json.JSONException
import org.json.JSONObject
import com.google.gson.Gson
import com.emedicoz.app.modelo.courses.quiz.QuizModel
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface
import com.emedicoz.app.utilso.*
import retrofit2.Call
import java.util.HashMap

/**
 * A simple [Fragment] subclass.
 */
class ResultAnalysisFragment : Fragment(), View.OnClickListener, MyNetworkCallBack {
    private lateinit var binding: AnalysisScreenBinding
    var activity: Activity? = null
    var mprogress: Progress? = null
    var testSegmentId: String? = null
    var resultTestSeries: ResultTestSeries? = null
    private var testSeriesId: String? = null
    private var testSName: String? = null
    private lateinit var myNetworkCall: MyNetworkCall
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()
        if (arguments != null) {
            testSegmentId = arguments!!.getString(Constants.ResultExtras.TEST_SEGMENT_ID)
            testSeriesId = arguments!!.getString(Const.TEST_SERIES_ID)
            testSName = arguments!!.getString(Constants.Extras.NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AnalysisScreenBinding.inflate(inflater, container, false)
        return binding.root
        //  return inflater.inflate(R.layout.analysis_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        myNetworkCall = MyNetworkCall(this, activity)
        /*if (!(testSName == null || testSName!!.contains("DAILY QUIZ"))) {
            binding.viewReattemptTV.setVisibility(View.VISIBLE)
        }*/
        binding.viewReattemptTV.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(activity, TestBaseActivity::class.java)
            intent.putExtra(Const.STATUS, false)
            intent.putExtra(Const.TEST_SERIES_ID, testSeriesId)
            startActivity(intent)
            activity!!.finish()
        })
        binding.circularProgress.setMaxProgress(100.0)
        bindControls()
    }

    private fun bindControls() {
        binding.apply {
            viewAnalysisTV.setOnClickListener(this@ResultAnalysisFragment)
            viewLeaderboardTV.setOnClickListener(this@ResultAnalysisFragment)
            viewSolutionTV.setOnClickListener(this@ResultAnalysisFragment)
            videoSolutionTV.setOnClickListener(this@ResultAnalysisFragment)
        }
        networkCallForQuizResult()
    }

    override fun onClick(view: View) {
        var intent: Intent? = null
        if (Helper.isConnected(activity)) {
            when (view.id) {
                R.id.viewAnalysisTV -> {
                    if (resultTestSeries == null) return
                    val resultTestSeriesOne = ResultTestSeries()
                    resultTestSeriesOne.marks = resultTestSeries!!.marks
                    resultTestSeriesOne.averageMarks = resultTestSeries!!.averageMarks
                    resultTestSeriesOne.topperScore = resultTestSeries!!.topTenList[0].marks
                    resultTestSeriesOne.testSeriesMarks = resultTestSeries!!.testSeriesMarks
                    intent = Intent(activity, QuizActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.VIEW_ANALYSIS)
                    intent.putExtra(Const.RESULT_SCREEN, resultTestSeriesOne)
                    intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId)
                }
                R.id.viewLeaderboardTV -> {
                    if (resultTestSeries == null) return
                    val resultTestSeriesTwo = ResultTestSeries()
                    resultTestSeriesTwo.testSeriesId = resultTestSeries!!.testSeriesId
                    resultTestSeriesTwo.testSeriesName = resultTestSeries!!.testSeriesName
                    resultTestSeriesTwo.totalQuestions = resultTestSeries!!.totalQuestions
                    resultTestSeriesTwo.setType = resultTestSeries!!.setType
                    resultTestSeriesTwo.testSeriesMarks = resultTestSeries!!.testSeriesMarks
                    resultTestSeriesTwo.totalTestSeriesTime = resultTestSeries!!.totalTestSeriesTime
                    intent = Intent(activity, QuizActivity::class.java)
                    intent.putExtra(Const.FRAG_TYPE, Const.LEADERBOARD)
                    intent.putExtra(Const.RESULT_SCREEN, resultTestSeriesTwo)
                    intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId)
                }
                R.id.viewSolutionTV -> if (resultTestSeries != null) {
                    intent = Intent(activity, ViewSolutionWithTabNew::class.java)
                    intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId)
                    intent.putExtra(Constants.Extras.NAME, resultTestSeries!!.testSeriesName)
                    if (resultTestSeries!!.markingScheme != null) {
                        intent.putExtra(Const.MARKING_SCHEME, resultTestSeries!!.markingScheme)
                    }
                }
                R.id.videoSolutionTV -> {
                    intent = Intent(activity, VideoSolution::class.java)
                    intent.putExtra(Constants.ResultExtras.TEST_SEGMENT_ID, testSegmentId)
                }
                else -> {
                }
            }
            if (intent != null) activity!!.startActivity(intent)
        } else {
            Toast.makeText(
                activity,
                "Please check your internet connection and try again..",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        var intent: Intent? = intent
        if (intent == null) {
            intent = Intent()
        }
        super.startActivityForResult(intent, requestCode)
    }

    fun networkCallForQuizResult() {
        Log.e(
            "STATUS",
            testSegmentId + " USER_ID " + SharedPreference.getInstance().loggedInUser.id
        )
        myNetworkCall.NetworkAPICall(API.API_GET_USER_LEADER_BOARD_RESULT_V3, true)
    }

    private fun setViewData() {
        (activity as QuizActivity?)!!.setToolbarTitle(resultTestSeries!!.testSeriesName)
        if (resultTestSeries != null && !GenericUtils.isEmpty(resultTestSeries!!.setType)) {
            binding.timeTakenRL.visibility = View.VISIBLE
        } else {
            binding.timeTakenRL.visibility = View.GONE
        }
        binding.apply {
            testSeriesName.text = resultTestSeries!!.testSeriesName
            correctTV.text = resultTestSeries!!.correctCount
            incorrectTV.text = resultTestSeries!!.incorrectCount
            skippedTV.text = resultTestSeries!!.nonAttempt
            if (resultTestSeries!!.userRank == "") {
                rankTV.text = "N/A"
            } else {
                rankTV.text = resultTestSeries!!.userRank
                eMedicozApp.getInstance().rank = resultTestSeries!!.userRank
            }

            myScoreTV.text = resultTestSeries!!.marks
            percentileTV.text = resultTestSeries!!.percentile
            timeTakenTV.text = resultTestSeries!!.timeSpent
            guessTV.text = resultTestSeries!!.guessCount
            candidateTV.text = resultTestSeries!!.totalUserAttempt
            totalQues.text = resultTestSeries!!.totalQuestions
            marksTV.text = resultTestSeries!!.testSeriesMarks
            totalTimeTV.text = String.format("%s mins", resultTestSeries!!.totalTestSeriesTime)
            myRankTV.text =
                if (!TextUtils.isEmpty(resultTestSeries!!.userRank)) resultTestSeries!!.userRank else "N/A"
            myScore.text = resultTestSeries!!.marks
            totalScore.text = resultTestSeries!!.testSeriesMarks
            if (!TextUtils.isEmpty(resultTestSeries!!.percentage)) {
                circularProgress.setCurrentProgress(
                    Math.round(resultTestSeries!!.percentage.toFloat()).toDouble()
                )
                percentageTV.text = Helper.getPercentage(resultTestSeries!!.percentage)
            } else {
                circularProgress.setCurrentProgress(0.0)
                percentageTV.text = "0 %"
            }
            val attempted =
                resultTestSeries!!.correctCount.toInt() + resultTestSeries!!.incorrectCount.toInt()
            attemptedTV.text = attempted.toString()
        }
    }

    override fun getAPI(apiType: String, service: FlashCardApiInterface): Call<JsonObject> {
        val params: MutableMap<String, Any?> = HashMap()
        params[Const.USER_ID] = SharedPreference.getInstance().loggedInUser.id
        params["test_segment_id"] = testSegmentId
        Log.e(TAG, "getAPI: $params")
        return service.postData(apiType, params)
    }

    @Throws(JSONException::class)
    override fun successCallBack(jsonObject: JSONObject, apiType: String) {
        Helper.showErrorLayoutForNoNav(API.API_GET_USER_LEADER_BOARD_RESULT_V3, activity, 0, 0)
        val data = GenericUtils.getJsonObject(jsonObject)
        resultTestSeries = Gson().fromJson(data.toString(), ResultTestSeries::class.java)
        if (resultTestSeries != null && resultTestSeries!!.displayVSolution == "1") {
            binding.videoSolutionTV.visibility = View.VISIBLE
        } else {
            binding.videoSolutionTV.visibility = View.GONE
        }
        if (resultTestSeries != null && resultTestSeries!!.displayReattempt == "1") {
            binding.viewReattemptTV.visibility = View.VISIBLE
        } else {
            binding.viewReattemptTV.visibility = View.GONE
        }
        setViewData()
    }

    override fun errorCallBack(jsonString: String, apiType: String) {
        if (jsonString.equals(
                activity!!.resources.getString(R.string.internet_error_message),
                ignoreCase = true
            )
        ) {
            Helper.showErrorLayoutForNoNav(API.API_GET_USER_LEADER_BOARD_RESULT_V3, activity, 1, 1)
        } else {
            Helper.showErrorLayoutForNoNav(API.API_GET_USER_LEADER_BOARD_RESULT_V3, activity, 1, 2)
        }
    }

    companion object {
        private const val TAG = "ResultAnalysisFragment"
        @JvmStatic
        fun newInstance(
            fragType: String?,
            testSeriesId: String?,
            resultTestSeries: ResultTestSeries?,
            quiz: QuizModel?,
            testSeriesName: String?
        ): ResultAnalysisFragment {
            val quizFragment = ResultAnalysisFragment()
            val bundle = Bundle()
            bundle.putSerializable(Const.TESTSERIES, resultTestSeries)
            bundle.putSerializable(Const.TEST_SERIES_ID, testSeriesId)
            bundle.putSerializable(Const.FRAG_TYPE, fragType)
            bundle.putSerializable(Const.RESULT_SCREEN, quiz)
            bundle.putString(Constants.Extras.NAME, testSeriesName)
            quizFragment.arguments = bundle
            return quizFragment
        }

        @JvmStatic
        fun newInstance(
            fragType: String?,
            testSeriesId: String?,
            status: String?,
            testSeriesName: String?
        ): ResultAnalysisFragment {
            val quizFragment = ResultAnalysisFragment()
            val bundle = Bundle()
            bundle.putSerializable(Const.FRAG_TYPE, fragType)
            bundle.putSerializable(Constants.ResultExtras.TEST_SEGMENT_ID, status)
            bundle.putSerializable(Const.TEST_SERIES_ID, testSeriesId)
            bundle.putString(Constants.Extras.NAME, testSeriesName)
            quizFragment.arguments = bundle
            return quizFragment
        }
    }
}