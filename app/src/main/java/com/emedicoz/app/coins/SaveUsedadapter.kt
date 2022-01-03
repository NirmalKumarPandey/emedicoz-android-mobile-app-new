package com.emedicoz.app.coins

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.R

class SaveUsedadapter (private val context: Context) : RecyclerView.Adapter<SaveUsedadapter.Viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        return Viewholder(LayoutInflater.from(context).inflate(R.layout.use_save_item_list, parent, false)
        )
    }
    override fun onBindViewHolder(holder: Viewholder, position: Int) {
       // TODO("Not yet implemented")
    }
    override fun getItemCount(): Int {
        return 2

    }
    class Viewholder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView)
}