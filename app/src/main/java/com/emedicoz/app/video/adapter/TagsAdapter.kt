package com.emedicoz.app.video.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.R
import com.emedicoz.app.databinding.CustomTabBinding
import com.emedicoz.app.video.ui.models.TagResponse

class TagsAdapter(
    val ctx: Context, val tagsList: List<TagResponse.Data.AllTags>,
    var onItemClick: ((TagResponse.Data.AllTags) -> Unit)
) : RecyclerView.Adapter<TagsAdapter.TagViewHolder>() {
    var selectedItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemBinding = CustomTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        with(holder) {
            with(tagsList[position]) {
                with(itemBinding) {

                    if (selectedItem == position) {
                        tabImageView.visibility = View.GONE
                        cardViewImage.visibility = View.VISIBLE
                        tabTextView.text = text
                        Glide.with(ctx).load(ctx.resources.getDrawable(R.drawable.ic_all)).into(tabImageViewOne)
                    } else {
                        cardViewImage.visibility = View.GONE
                        tabImageView.visibility = View.VISIBLE
                        tabTextView.text = text
                        Glide.with(ctx).load(logo_image).into(tabImageView)
                    }

                    if (selectedItem == position) {
                        if (position == 0) {
                            tabImageView.visibility = View.GONE
                            cardViewImage.visibility = View.VISIBLE
                            Glide.with(ctx).load(ctx.resources.getDrawable(R.drawable.ic_all)).into(tabImageViewOne)
                        } else {
                            tabImageView.visibility = View.GONE
                            cardViewImage.visibility = View.VISIBLE
                            Glide.with(ctx).load(logo_image).into(tabImageViewOne)
                        }
                        tabTextView.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                    } else {
                        if (position == 0) {
                            cardViewImage.visibility = View.GONE
                            tabImageView.visibility = View.VISIBLE
                            Glide.with(ctx).load(ctx.resources.getDrawable(R.drawable.ic_all)).into(tabImageView)
                        } else {
                            cardViewImage.visibility = View.GONE
                            tabImageView.visibility = View.VISIBLE
                            Glide.with(ctx).load(logo_image).into(tabImageView)
                        }
                        tabTextView.setTextColor(ContextCompat.getColor(ctx, R.color.transparent_black))
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return tagsList.size
    }

    inner class TagViewHolder(val itemBinding: CustomTabBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        init {
            itemBinding.tagLayout.setOnClickListener {
                onItemClick.invoke(tagsList[adapterPosition])
                selectedItem = adapterPosition
                notifyDataSetChanged()
            }
        }
    }
}