package com.emedicoz.app.podcastnew.callback

import com.emedicoz.app.podcast.Podcast
import com.emedicoz.app.ui.podcast.ViewHolder.PodcastRecycleViewHolder

interface OnPodcastBookmarkClick {
    fun onPodcastBookmarkClick(podcast:Podcast,position:Int,podcastRecycleViewHolder: PodcastRecycleViewHolder)
}