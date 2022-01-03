package com.emedicoz.app.dailychallenge.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import com.emedicoz.app.R
import com.emedicoz.app.customviews.Progress
import com.emedicoz.app.dailychallenge.DailyChallengeDashboard
import com.emedicoz.app.modelo.PostFile
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.eMedicozApp
import kotlinx.android.synthetic.main.header_layout.*

class DailyChallengeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_challenge)

        ibBack.setOnClickListener(View.OnClickListener { finish() })
        tvHeaderName.text = "Daily Challenge"
        val fragment = DailyChallengeDashboard()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_frame, fragment)
        transaction.commit()
    }
}