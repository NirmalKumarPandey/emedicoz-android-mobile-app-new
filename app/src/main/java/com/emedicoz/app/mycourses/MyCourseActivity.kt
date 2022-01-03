package com.emedicoz.app.mycourses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.emedicoz.app.R
import kotlinx.android.synthetic.main.header_layout.*

class MyCourseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)

        ibBack.setOnClickListener(View.OnClickListener { finish() })
        tvHeaderName.text = "My Courses"

        val fragment = MyCourseTabFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_frame, fragment)
        transaction.commit()
    }
}