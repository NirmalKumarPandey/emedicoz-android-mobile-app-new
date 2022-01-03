package com.emedicoz.app.newsandarticle.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emedicoz.app.network.NetworkRepository
import com.emedicoz.app.network.model.AddressBook
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.newsandarticle.models.NewsListResponse
import com.emedicoz.app.ui.podcast.ProgressListner
import com.emedicoz.app.ui.podcast.StoryName

class NewsAndArticleViewModel: ViewModel() {


    val newstitle = MutableLiveData<String>()

    public val latestNews = MutableLiveData<List<StoryName>>()
    public val newsList = MutableLiveData<NewsListResponse>()

    fun loadNewsList(mContext: Context?, mProgressListner: ProgressUpdateListner, user_id : String ,type:String) {

        NetworkRepository().call_latest_News_List(mContext,mProgressListner , this , user_id,type )
    }

    fun loadTestData( mContext: Context?, mProgressListner: ProgressUpdateListner?) {
      //  PodcastRepository.getInstance().loadTestData(mContext, this, mProgressListner)
        // mIndex.setValue(index);
    }

    //for bookmark
    fun loadBookmarkList(mContext: Context?, mProgressListner: ProgressUpdateListner, user_id : String) {

        NetworkRepository().getBookmarkList(mContext,mProgressListner , this , user_id)
    }
    //for bookmark
    fun loadTopDetailList(mContext: Context?, mProgressListner: ProgressUpdateListner, user_id : String,type:String,type_id:String) {

        NetworkRepository().getTopicDetailList(mContext,mProgressListner , this , user_id,type,type_id)
    }


}