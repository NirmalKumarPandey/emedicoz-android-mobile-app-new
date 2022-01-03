package com.emedicoz.app.podcastnew

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.podcast.Author
import com.emedicoz.app.podcast.Podcast
import com.emedicoz.app.ui.podcast.NewNameDetailList
import com.emedicoz.app.ui.podcast.StoryName


class PodcastViewModel : ViewModel() {
    private val mIndex = MutableLiveData<Int>()
    val text = Transformations.map(
        mIndex
    ) { input -> "Hello world from section: $input" }

    fun setIndex(index: Int) {
        mIndex.value = index
    }

    fun loadData(index: Int, mContext: Context?, mProgressListner: ProgressUpdateListner?) {
        PodcastRepository.getInstance().loadDataOnViewHolder(mContext, this, mProgressListner!!)
        // mIndex.setValue(index);
    }

    fun loadPoadcastData(
        mContext: Context?,
        mProgressListner: ProgressUpdateListner?,
        userID: String?,
        selectedAuthorId: String?,
        filterType: String,
        page:Int
    ) {
        PodcastRepository.getInstance().loadPoadCastDataOnViewHolder(
            mContext,
            this,
            mProgressListner!!,
            userID,
            selectedAuthorId,
            filterType,
            page
        )
    }

    fun loadBookmarkedPoadcastData(
        mContext: Context?,
        mProgressListner: ProgressUpdateListner?,
        userID: String?,
        selectedAuthorId: String?,
        filterType: String,
        pageNumber:Int
    ) {
        PodcastRepository.getInstance().loadPodcastBookmarkedData(
            mContext,
            this,
            mProgressListner!!,
            userID,
            selectedAuthorId,
            filterType,
            pageNumber
        )
    }

    fun loadPoadcastAuthorList(
        mContext: Context?,
        userID: String?,
        selectedAuthorId: String?,
        mProgressListner: ProgressUpdateListner?,
        filterType: String
    ) {
        PodcastRepository.getInstance()
            .getPodcastAuthorList(mContext, this, userID, selectedAuthorId, mProgressListner!!,filterType)
    }

    fun networkCallForBookmark(mContext: Activity, podcastId: String) {
        PodcastRepository.getInstance()
            .networkCallForBookmark(mContext, podcastId)
    }

    fun loadAuthorPodcastById(
        mContext: Context?,
        mProgressListner: ProgressUpdateListner?,
        userID: String?
    ) {
        PodcastRepository.getInstance()
            .loadAuthorPodcastById(mContext, this,mProgressListner!!,userID)
    }

    val messageContainerA = MutableLiveData<String>()
    private val newName = MutableLiveData<NewNameDetailList>()
    val storyList = MutableLiveData<List<StoryName>>()

    /* Live API Of PodCast*/
    private val podcastList = MutableLiveData<List<Podcast>>()
    private val bookmarkedPodcastList = MutableLiveData<List<Podcast>>()
    private val podcastByUserId = MutableLiveData<List<Podcast>>()
    val authorList = MutableLiveData<List<Author>>()

    fun getpodcastList(): MutableLiveData<List<Podcast>> {
        return podcastList
    }

    fun getBookmarkedPodcastList(): MutableLiveData<List<Podcast>> {
        return bookmarkedPodcastList
    }

    fun getPodcastByUserId(): MutableLiveData<List<Podcast>> {
        return podcastByUserId
    }
}