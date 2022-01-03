package com.emedicoz.app.coins

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R

class CheckOutAdapter (private  val context: Context) : RecyclerView.Adapter<CheckOutAdapter.ViewHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.checkout_item_view_layout, parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     //   TODO("Not yet implemented")
    }
    override fun getItemCount(): Int {
        return 2
    }
    class ViewHolder(itemLayoutView: View) :RecyclerView.ViewHolder(itemLayoutView){

    }
}