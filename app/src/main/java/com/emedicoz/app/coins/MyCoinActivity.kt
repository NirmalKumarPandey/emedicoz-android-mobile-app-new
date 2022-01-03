package com.emedicoz.app.coins

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.ActivityCoinBinding
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController


class MyCoinActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityCoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    fun initViews() {
//        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_redeem_offers)
//        val adapter = RedeemAdapter(this)
//        recyclerView.adapter = adapter

//        binding.lytViewAll.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
//            R.id.lytViewAll -> {
//                intent = Intent(this, MyCoinVActivity::class.java)
//                startActivity(intent)
//            }
        }
    }
}