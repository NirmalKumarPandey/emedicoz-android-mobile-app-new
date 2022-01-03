package com.emedicoz.app.reward.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.emedicoz.app.R
import com.emedicoz.app.coins.CheckOutActivity


class EarnPointFragment : Fragment() {

    private lateinit var tvEnroll: TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_earn_point, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvEnroll = view.findViewById(R.id.tvEnroll)

        tvEnroll.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, CheckOutActivity::class.java)
            startActivity(intent)
        })
    }
}