package com.emedicoz.app.coins

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R

class RedeemOfferAdapter(private val context: Context): RecyclerView.Adapter<RedeemOfferAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.redeem_item, parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       // TODO("Not yet implemented")
    }
    override fun getItemCount(): Int {
        return 2

    }
    class ViewHolder(itemlayoutView: View) : RecyclerView.ViewHolder(itemlayoutView){

    }
}