package com.emedicoz.app.podcastnew.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.emedicoz.app.PodcastNewKotlin
import com.emedicoz.app.R

class PodcastMainActivity : AppCompatActivity(){

    private lateinit var ibBack: ImageButton
    private lateinit var tvHeaderName: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast_main)
        initView()

        val fragment = PodcastNewKotlin()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_frame, fragment)
        transaction.commit()

    }
    private fun initView() {
        ibBack = findViewById(R.id.ibBack)
        tvHeaderName = findViewById(R.id.tvHeaderName)
        tvHeaderName.text = "Podcast"

        ibBack.setOnClickListener(View.OnClickListener { finish() })
    }


    fun getCurrentFragment():Fragment {
        return supportFragmentManager.findFragmentById(R.id.container_frame)!!
    }

















}



