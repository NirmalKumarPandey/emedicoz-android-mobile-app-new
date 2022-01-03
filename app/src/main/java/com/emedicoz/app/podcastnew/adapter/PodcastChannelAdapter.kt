package com.emedicoz.app.podcastnew.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emedicoz.app.HomeActivity
import com.emedicoz.app.PodcastNewKotlin
import com.emedicoz.app.R
import com.emedicoz.app.databinding.SingleItemPodcastChannelBinding
import com.emedicoz.app.podcast.Author
import com.emedicoz.app.podcastnew.PodcastFragment
import com.emedicoz.app.podcastnew.activity.PodcastByAuthor
import com.emedicoz.app.podcastnew.activity.PodcastMainActivity
import com.emedicoz.app.utilso.Const

class PodcastChannelAdapter(var context: Context, var authorList: List<Author>) :
    RecyclerView.Adapter<PodcastChannelAdapter.PodcastChannelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastChannelViewHolder {
        val binding = SingleItemPodcastChannelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PodcastChannelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PodcastChannelViewHolder, position: Int) {
        holder.singleItemPodcastChannelBinding.creatorName.text ="Podcast By : "+ authorList[position].name
        Glide.with(context).load(authorList[position].image)
            .apply(
                RequestOptions()
                    .placeholder(R.mipmap.medicos_icon).error(R.mipmap.medicos_icon)
            )
            .into(holder.singleItemPodcastChannelBinding.creatorIV)
        holder.singleItemPodcastChannelBinding.podcastCountTV.text =
            authorList[position].podcast_count

        holder.singleItemPodcastChannelBinding.mainChannelCV.setOnClickListener {
            if (context is PodcastMainActivity) {
                val fragment = (context as PodcastMainActivity).getCurrentFragment()
                if (fragment is PodcastNewKotlin) {
                    val childFragment = fragment.getCurrentFragment()
                    if (childFragment is PodcastFragment) {
                        if (childFragment.podcastType == Const.CHANNEL) {
                            context.startActivity(
                                Intent(context, PodcastByAuthor::class.java)
                                    .putExtra("id", authorList[position].id)
                            )
                        }
                    }
                }
            }

//            if (context is HomeActivity) {
//                val fragment = (context as HomeActivity).getCurrentFragment()
//                if (fragment is PodcastNewKotlin) {
//                    val childFragment = fragment.getCurrentFragment()
//                    if (childFragment is PodcastFragment) {
//                        if (childFragment.podcastType == Const.CHANNEL) {
//                            context.startActivity(
//                                Intent(context, PodcastByAuthor::class.java)
//                                    .putExtra("id", authorList[position].id)
//                            )
//                        }
//                    }
//                }
//            }
        }
    }

    override fun getItemCount(): Int {
        return authorList.size
    }

    class PodcastChannelViewHolder(var singleItemPodcastChannelBinding: SingleItemPodcastChannelBinding) :
        RecyclerView.ViewHolder(singleItemPodcastChannelBinding.root)
}