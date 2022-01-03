package com.emedicoz.app.coins
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R
import com.emedicoz.app.databinding.ActivityCheckoutBinding
import kotlinx.android.synthetic.main.coin_header.*
import kotlinx.android.synthetic.main.earn_point_layout_view.*

class CheckOutActivity : AppCompatActivity() {
    lateinit var binding: ActivityCheckoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }
    fun initViews(){
       val recyclerViewCheckout = findViewById<RecyclerView>(R.id.recycler_view_checkout)
        val  adapter = CheckOutAdapter(this)
        recyclerViewCheckout.adapter = adapter

        txtCoin.text = "Checkout"
        tvRewardsPoints.visibility = View.GONE
        imgBackPressed.setOnClickListener(View.OnClickListener { finish() })

    }
}