package com.emedicoz.app.newsandarticle.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emedicoz.app.network.NetworkRepository
import com.emedicoz.app.network.model.AddressBook
import com.emedicoz.app.network.model.ProgressUpdateListner
import com.emedicoz.app.newsandarticle.models.NewsListResponse
import com.emedicoz.app.newsandarticle.models.TopicListResponse
import com.emedicoz.app.ui.podcast.ProgressListner
import com.emedicoz.app.ui.podcast.StoryName

class TopicTagHomeViewModel: ViewModel() {


    val newstitle = MutableLiveData<String>()

    public val latestNews = MutableLiveData<List<StoryName>>()
   // public val topicList = MutableLiveData<TopicListResponse>()

    public val topTagList = MutableLiveData<TopicListResponse>()

    fun TopicTagList(mContext: Context?, mProgressListner: ProgressUpdateListner, user_id : String ,type:String) {

        NetworkRepository().call_topic_tag_List(mContext,mProgressListner , this , user_id,type )
    }

    fun loadTestData( mContext: Context?, mProgressListner: ProgressUpdateListner?) {
      //  PodcastRepository.getInstance().loadTestData(mContext, this, mProgressListner)
        // mIndex.setValue(index);
    }

    // search content

    fun searchTopicTagList(mContext: Context?, mProgressListner: ProgressUpdateListner, user_id : String ,type:String ,searchText:String) {

        NetworkRepository().call_search_topic_tag_List(mContext,mProgressListner , this , user_id,type,searchText )
    }

}