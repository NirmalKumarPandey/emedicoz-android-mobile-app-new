package com.emedicoz.app.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.emedicoz.app.R
import com.emedicoz.app.rating.Rating
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedbackDialog: BottomSheetDialogFragment() {

    private lateinit var imageView7: ImageView
    private lateinit var tv_submit: TextView
    private lateinit var imageView6: ImageView
    private lateinit var textView8: TextView
    private lateinit var editFeedback: EditText

    companion object{

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.feedback_dialog, container, false)

    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_submit = view.findViewById(R.id.tv_submit)
        imageView7 = view.findViewById(R.id.imageView7)
        imageView6 = view.findViewById(R.id.imageView6)
        imageView6.setImageResource(R.drawable.ic_rating_2)
        textView8 = view.findViewById(R.id.textView8)
        editFeedback = view.findViewById(R.id.editFeedback)
        imageView7.setOnClickListener(View.OnClickListener { dismiss() })

        val rating = arguments?.getFloat("rating")
        System.out.println("rating3444------------------------"+rating)
        tv_submit.setOnClickListener(View.OnClickListener {
            if (rating != null) {
                if (editFeedback.text.isEmpty()){
                    Toast.makeText(context, "Please enter feedback", Toast.LENGTH_LONG).show()
                }
                ratingApi(SharedPreference.getInstance().loggedInUser.id, rating, editFeedback.text.toString(), "apprating")
                //dialog!!.dismiss()
            }
        })
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    //function create rating api call to send to our server
    private fun ratingApi(user_id: String, rating: Float, feedback: String, apprating: String){
        if (!Helper.isConnected(context)) {
            Toast.makeText(context, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        val apis = ApiClient.createService(com.emedicoz.app.api.ApiInterface::class.java)
        val response = apis.addRating(user_id, rating, feedback, apprating)
        response?.enqueue(object : Callback<Rating?> {
            override fun onResponse(call: Call<Rating?>?, response: Response<Rating?>?) {
                val rating = response!!.body()
                if (rating?.status?.statuscode == 200){
                    //SharedPreference.getInstance().feedStatus(Const.IS_FEED_STATUS, rating?.status?.status)
                    Toast.makeText(context, rating?.message.message, Toast.LENGTH_LONG).show()
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
}

