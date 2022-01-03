package com.emedicoz.app.login.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.emedicoz.app.R
import com.emedicoz.app.login.activity.SliderActivity
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.Helper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class LogOutFragmentDialog : BottomSheetDialogFragment() {

    private lateinit var btn_cancel: Button
    private lateinit var btn_submit: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_out_dialog, container, false)
    }

    //view created and cast the view id
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_cancel = view.findViewById(R.id.btn_cancel)
        btn_submit = view.findViewById(R.id.btn_submit)

        btn_submit.setOnClickListener(View.OnClickListener {
            Helper.ClearUserData(context)
            val intent = Intent(context, SliderActivity::class.java)
            intent.putExtra(Constants.Extras.TYPE, "LOGIN")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        })

        btn_cancel.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }
}
