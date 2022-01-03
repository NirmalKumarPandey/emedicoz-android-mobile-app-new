package com.emedicoz.app.feeds.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.emedicoz.app.R
import com.emedicoz.app.dailychallenge.DailyChallengeDashboard
import com.emedicoz.app.feeds.fragment.SavedNotesFragment
import kotlinx.android.synthetic.main.header_layout.*

class MyBookmarksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookmarks)

        ibBack.setOnClickListener(View.OnClickListener { finish() })
        tvHeaderName.text = "My Bookmarks"

        val fragment = SavedNotesFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_frame, fragment)
        transaction.commit()
    }
}