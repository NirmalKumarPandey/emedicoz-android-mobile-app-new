package com.emedicoz.app.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.emedicoz.app.R
import kotlinx.android.synthetic.main.header_layout.*

class NotificationSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        ibBack.setOnClickListener(View.OnClickListener { finish() })
        tvHeaderName.text = "Notification Settings"
    }
}