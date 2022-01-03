package com.emedicoz.app.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.R
import com.emedicoz.app.rating.Rating
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateRatingDialog : BottomSheetDialogFragment() {

    private lateinit var imageView7: ImageView
    private lateinit var tv_submit: TextView
    private lateinit var ratingRB: RatingBar
    private lateinit var imageView6: ImageView
    private lateinit var textView8: TextView
    var rating = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.updated_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_submit = view.findViewById(R.id.tv_submit)
        ratingRB = view.findViewById(R.id.ratingRB)
        imageView7 = view.findViewById(R.id.imageView7)
        imageView6 = view.findViewById(R.id.imageView6)
        textView8 = view.findViewById(R.id.textView8)

        imageView7.setOnClickListener(View.OnClickListener {
            SharedPreference.getInstance().feedStatus(Const.IS_FEED_STATUS, "")
            dismiss()
        })
        val getrating = arguments?.getString("rating")
        ratingRB.rating = getrating!!.toFloat()
        tv_submit.setOnClickListener(View.OnClickListener {
            if (rating == 0f) {
                GenericUtils.showToast(context, getString(R.string.rate_before_submit))
            }else if (rating < 4 && rating > 0){
                updateRating(SharedPreference.getInstance().loggedInUser.id, "apprating", rating)
            }else if (rating >= 4){
                dismiss()
                (activity as HomeActivity).navController.navigate(R.id.ratingFragmentDialog)
                updateRating(SharedPreference.getInstance().loggedInUser.id, "apprating", rating)
            }

        })

        ratingRB.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            this.rating = rating
            if (rating < 4) {
                System.out.println("onRatingChanged: $rating")

            } else if (rating >= 4) {
                System.out.println("onRatingChanged: $rating")

            }
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    private fun updateRating(user_id: String, apprating: String, rating: Float){
        if (!Helper.isConnected(context)) {
            Toast.makeText(context, R.string.internet_error_message, Toast.LENGTH_SHORT).show()
            return
        }
        val apis = ApiClient.createService(com.emedicoz.app.api.ApiInterface::class.java)
        val response = apis.updateRating(user_id, apprating, rating)
        response?.enqueue(object : Callback<Rating?> {
            override fun onResponse(call: Call<Rating?>?, response: Response<Rating?>?) {
                val rating = response!!.body()
                if (rating?.status?.statuscode == 200){
                        //dismiss()
                   // (activity as HomeActivity).navController.navigate(R.id.ratingFragmentDialog)

                }else if (rating?.status?.statuscode == 201){
                    //Toast.makeText(context, rating?.message.message, Toast.LENGTH_LONG).show()
                    //dismiss()
                }
            }

            override fun onFailure(call: Call<Rating?>?, t: Throwable?) {
                Helper.showExceptionMsg(context, t)
            }
        })
    }
}