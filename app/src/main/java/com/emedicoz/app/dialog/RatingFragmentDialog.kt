package com.emedicoz.app.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.emedicoz.app.R
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.utilso.Helper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RatingFragmentDialog: BottomSheetDialogFragment() {

    private lateinit var imageView7: ImageView
    private lateinit var tv_submit: TextView
    private lateinit var ratingRB: RatingBar
    private lateinit var imageView6: ImageView
    private lateinit var textView8: TextView
    var rating = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_rate_us_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_submit = view.findViewById(R.id.tv_submit)
        ratingRB = view.findViewById(R.id.ratingRB)
        imageView7 = view.findViewById(R.id.imageView7)
        imageView6 = view.findViewById(R.id.imageView6)
        imageView6.setImageResource(R.drawable.play_store_icon)
        textView8 = view.findViewById(R.id.textView8)

        textView8.text = "Would you like to rate on Play Store ?"
        tv_submit.text = "rate on playstore"
        ratingRB.visibility = View.INVISIBLE


        imageView7.setOnClickListener(View.OnClickListener { dismiss() })

        tv_submit.setOnClickListener(View.OnClickListener {
            Helper.goToPlayStore(context)
            dialog!!.dismiss()
        })
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }
}