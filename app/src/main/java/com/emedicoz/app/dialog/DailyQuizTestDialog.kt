package com.emedicoz.app.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.rating.Rating
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DailyQuizTestDialog: BottomSheetDialogFragment() {
    private lateinit var imageView7: ImageView
    private lateinit var tv_submit: TextView
    private lateinit var ratingRB: RatingBar
    private lateinit var imageView6: ImageView
    private lateinit var textView8: TextView
    var rating = 0f
    lateinit var feedbackDialog: QbankFeedBackDialog
    private lateinit var editFeedback: EditText
    lateinit var dialog1: FeedbackDialog
    var test_series_id = ""
    var daily_quiz_test = ""



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_rate_us_layout, container, false)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_submit = view.findViewById(R.id.tv_submit)
        imageView7 = view.findViewById(R.id.imageView7)
        imageView6 = view.findViewById(R.id.imageView6)
        imageView6.setImageResource(R.drawable.ic_rating_2)
        textView8 = view.findViewById(R.id.textView8)
        ratingRB = view.findViewById(R.id.ratingRB)
        editFeedback = view.findViewById(R.id.editFeedback)

        feedbackDialog = QbankFeedBackDialog()

        imageView7.setOnClickListener(View.OnClickListener {
            SharedPreference.getInstance().saveDailyQuizStatus(Const.DAILY_QUIZ_TEST, "false")
            dismiss()
        })

        //val rating = arguments?.getFloat("rating")
        //System.out.println("rating3444------------------------"+rating)
        rating = 0f
        //test_series_id = SharedPreference.getInstance().getTestSerieIdDailyQuiz(Const.DIALY_QUIZ_TEST_SERIES_ID, "")
        //daily_quiz_test = Constants.TestType.APP_QUIZ
        val feedbackText = editFeedback.text

        ratingRB.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            this.rating = rating
            if (rating < 4) {
                System.out.println("onRatingChanged: $rating")

            } else if (rating >= 4) {
                System.out.println("onRatingChanged: $rating")
            }
        }

        tv_submit.setOnClickListener(View.OnClickListener {
            if (rating == 0f) {
                GenericUtils.showToast(context, getString(R.string.rate_before_submit))
            } else if (rating < 4 && rating > 0) {
                //dialog!!.dismiss()
                val bundle = Bundle()
                bundle.putFloat("rating", rating)
                dialog1 = FeedbackDialog()
                dialog1.show(parentFragmentManager, "rating")
                dialog1.arguments = bundle
                dialog!!.dismiss()


                //(activity as HomeActivity).navController.navigate(R.id.feedbackDialog, bundle)
               // dialog!!.dismiss()
//                editFeedback.visibility = View.VISIBLE
//                ratingRB.visibility = View.GONE
//                System.out.println("value----------------------"+feedbackText)
//                if (feedbackText.isEmpty()){
//                    Toast.makeText(context, "Please enter feedback", Toast.LENGTH_LONG).show()
//                }else{
//                    saveRatingAll(SharedPreference.getInstance().loggedInUser.id, test_series_id, rating, editFeedback.text.toString(), daily_quiz_test)
//                }

            } else if (rating >= 4) {
//                editFeedback.visibility = View.GONE
//                ratingRB.visibility = View.VISIBLE
                ratingApi(SharedPreference.getInstance().loggedInUser.id, rating, "", "apprating")
                //saveRatingAll(SharedPreference.getInstance().loggedInUser.id, test_series_id, rating, "", daily_quiz_test)
            }
        })
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    private fun ratingApi(user_id: String, rating: Float, feedback: String, apprating: String){
        if (!Helper.isConnected(context)) {
            Toast.makeText(context, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        val apis = ApiClient.createService(com.emedicoz.app.api.ApiInterface::class.java)
        val response = apis.addRating(user_id, rating, feedback, apprating)
        response?.enqueue(object : Callback<Rating?>{
            override fun onResponse(call: Call<Rating?>?, response: Response<Rating?>?) {
                val rating = response!!.body()
                if (rating?.status?.statuscode == 200){
                    //SharedPreference.getInstance().feedStatus(Const.IS_FEED_STATUS, rating?.status?.status)
                    Toast.makeText(context, rating?.message.message, Toast.LENGTH_LONG).show()
                    (activity as HomeActivity).navController.navigate(R.id.ratingFragmentDialog)
                    dialog!!.dismiss()
                }else if (rating?.status?.statuscode == 201){
                    //SharedPreference.getInstance().feedStatus(Const.IS_FEED_STATUS, rating?.status?.status)
                    Toast.makeText(context, rating?.message.message, Toast.LENGTH_LONG).show()
                    dialog!!.dismiss()
                }
            }

            override fun onFailure(call: Call<Rating?>?, t: Throwable?) {
                Helper.showExceptionMsg(context, t)
            }
        })
    }
//    private fun saveRatingAll(user_id: String, test_series_id: String, rating: Float, feedback: String, test_type: String){
//        if (!Helper.isConnected(context)) {
//            Toast.makeText(context, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
//            return
//        }
//        val apis = ApiClient.createService(com.emedicoz.app.api.ApiInterface::class.java)
//        val response = apis.saveQbankRating(user_id, test_series_id, rating, feedback, test_type)
//        response.enqueue(object : Callback<Rating?> {
//            override fun onResponse(call: Call<Rating?>?, response: Response<Rating?>?) {
//                val rating = response!!.body()
//                if (rating?.status?.statuscode == 200){
//                    SharedPreference.getInstance().saveDailyQuizStatus(Const.DAILY_QUIZ_TEST, "")
//                    Toast.makeText(context, rating?.message.message, Toast.LENGTH_LONG).show()
//                    dialog!!.dismiss()
//                }else if (rating?.status?.statuscode == 201){
//                    Toast.makeText(context, rating?.message.message, Toast.LENGTH_LONG).show()
//                    SharedPreference.getInstance().saveDailyQuizStatus(Const.DAILY_QUIZ_TEST, "")
//                    dialog!!.dismiss()
//                }
//            }
//
//            override fun onFailure(call: Call<Rating?>?, t: Throwable?) {
//                Helper.showExceptionMsg(context, t)
//            }
//        })
//    }
}