package com.emedicoz.app.coins
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.emedicoz.app.databinding.EarnCoinLayoutBinding
import kotlinx.android.synthetic.main.coin_header.*
import kotlinx.android.synthetic.main.earn_point_layout_view.*

class EarnCoinActivity : AppCompatActivity() {
    private lateinit var binding: EarnCoinLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EarnCoinLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }
    fun initViews(){
        txtCoin.text = "Earn Point"
        tvRewardsPoints.visibility = View.GONE
        imgBackPressed.setOnClickListener(View.OnClickListener { finish() })

        cardEnrollNow.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, CheckOutActivity::class.java)
            startActivity(intent)
        })
    }
}