package com.emedicoz.app.cart.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.emedicoz.app.R
import com.emedicoz.app.cart.MyCartFragment
import com.emedicoz.app.dailychallenge.DailyChallengeDashboard
import kotlinx.android.synthetic.main.header_layout.*

class MyCartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)

        ibBack.setOnClickListener(View.OnClickListener { finish() })
        tvHeaderName.text = "My Cart"

        val fragment = MyCartFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_frame, fragment)
        transaction.commit()
    }
}