package com.emedicoz.app.podcastnew

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emedicoz.app.databinding.PodcastrowBinding
import com.emedicoz.app.podcast.Podcast
import com.emedicoz.app.ui.podcast.ViewHolder.PodcastRecycleViewHolder
import com.emedicoz.app.ui.views.RoundedImageView
import java.util.*

class PodcastRecycleViewAdapter(mContext: Context, list: List<Podcast>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val name: String? = null
    private val profile = 0
    private val email: String? = null

    private val list: List<Podcast> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

     var binding=  PodcastrowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PodcastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder!!.itemViewType) {
            0 -> {
                val rowViewHolder = holder as PodcastRecycleViewHolder?
                configure_header(rowViewHolder, position)
            }
        }
    }

    private fun configure_header(rowViewHolder: PodcastRecycleViewHolder?, position: Int) {
        rowViewHolder!!.episodetitle.text = list[position].title
        rowViewHolder.publisher.text = list[position].createdBy
    }

    private fun load_profile_bitmap(imageView2: RoundedImageView, url: String) {}
    override fun getItemCount(): Int {
        return list.size
    }

    // With the following method we check what type of view is being passed
    override fun getItemViewType(position: Int): Int {
        var p = 1
        p = if (position == 0) {
            0
        } else {
            1
        }
        p = 0
        return p
    }

    companion object {
        // Declaring Variable to Understand which View is being worked on
        private const val TYPE_HEADER = 0

        // IF the view under inflation and population is header or Item
        private const val TYPE_ITEM = 1
    }



    inner class PodcastViewHolder(val mPodcastrowBinding: PodcastrowBinding)
        : RecyclerView.ViewHolder(mPodcastrowBinding.root)



}
