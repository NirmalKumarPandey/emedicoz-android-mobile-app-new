package com.emedicoz.app.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.emedicoz.app.R
import com.emedicoz.app.rating.Rating
import com.emedicoz.app.retrofit.ApiClient
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Helper
import com.emedicoz.app.utilso.SharedPreference
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QbankFeedBackDialog: BottomSheetDialogFragment() {
    private lateinit var imageView7: ImageView
    private lateinit var tv_submit: TextView
    private lateinit var imageView6: ImageView
    private lateinit var textView8: TextView
    private lateinit var editFeedback: EditText

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
        //System.out.println("rating3444------------------------"+rating)
        tv_submit.setOnClickListener(View.OnClickListener {
            if (rating != null) {

            }
        })
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

}