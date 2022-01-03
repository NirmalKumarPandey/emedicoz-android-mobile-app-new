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
import com.emedicoz.app.notifications.`interface`.CategoryItemClickListener
import com.emedicoz.app.notifications.model.Data

class NotificationCategoryAdapter(private val context: Context, private val data: List<Data>, private val categoryItemClick: CategoryItemClickListener): RecyclerView.Adapter<NotificationCategoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.notification_header_items, parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = data.get(position)
        Glide.with(context)
                .load(data.icon)
                .apply(RequestOptions.placeholderOf(R.drawable.offers).error(R.drawable.offers))
                .into(holder.categoryImage)
            holder.tvCategoryName.text = data.name

        holder.itemView.setOnClickListener(View.OnClickListener {
            categoryItemClick.onItemClickListener(data)

        })
    }
    override fun getItemCount(): Int {
        return data.size

    }
    class ViewHolder(itemlayoutView: View) : RecyclerView.ViewHolder(itemlayoutView){
        val categoryImage: ImageView = itemlayoutView.findViewById(R.id.categoryImage)
        val tvCategoryName: TextView = itemlayoutView.findViewById(R.id.tvCategoryName)
    }

}