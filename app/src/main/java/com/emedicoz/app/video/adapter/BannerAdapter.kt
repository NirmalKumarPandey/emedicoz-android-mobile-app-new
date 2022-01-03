package com.emedicoz.app.video.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.emedicoz.app.R
import com.emedicoz.app.video.ui.models.TagResponse
import com.smarteist.autoimageslider.SliderViewAdapter

class BannerAdapter(val ctx: Context, private val mSliderItems: ArrayList<TagResponse.Data.VideoBanner>) : SliderViewAdapter<BannerAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup): VH {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_banner, null)
        return VH(inflate)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        Glide.with(ctx).load(mSliderItems[position].image_link).into(viewHolder.imageView)
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    inner class VH(itemView: View) : ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.img_slider)

    }
}