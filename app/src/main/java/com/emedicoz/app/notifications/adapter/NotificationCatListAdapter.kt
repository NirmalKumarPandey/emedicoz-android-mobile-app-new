package com.emedicoz.app.notifications.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.notifications.model.DataX

class NotificationCatListAdapter(private val context: Context, private val data: List<DataX>): RecyclerView.Adapter<NotificationCatListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.notification_items, parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = data.get(position)
        Glide.with(context)
            .load(data.extra)
            .apply(RequestOptions.placeholderOf(R.drawable.offers).error(R.drawable.offers))
            .into(holder.ivIcons)
        holder.tvMessage.text = data.text
        holder.tvTimeSpand.text = data.created_on

    }
    override fun getItemCount(): Int {
        return data.size

    }
    class ViewHolder(itemlayoutView: View) : RecyclerView.ViewHolder(itemlayoutView){
        val ivIcons: ImageView = itemlayoutView.findViewById(R.id.ivIcons)
        val tvMessage: TextView = itemlayoutView.findViewById(R.id.tvMessage)
        val tvTimeSpand: TextView = itemlayoutView.findViewById(R.id.tvTimeSpand)
    }

}