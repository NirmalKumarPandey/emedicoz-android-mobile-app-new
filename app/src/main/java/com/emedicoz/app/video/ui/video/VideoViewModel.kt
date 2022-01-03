package com.emedicoz.app.video.ui.video

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emedicoz.app.modelo.Video
import com.emedicoz.app.video.ui.models.TagResponse
import com.emedicoz.app.video.ui.models.VideoResponse
import kotlinx.coroutines.*

class VideoViewModel constructor(private val videoRepository: VideoRepository) : ViewModel() {
    private var job: Job? = null
    val errorMessage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val videoResponse: MutableLiveData<VideoResponse> by lazy { MutableLiveData<VideoResponse>() }
    val tagResponse: MutableLiveData<List<TagResponse.Data.AllTags>> by lazy { MutableLiveData<List<TagResponse.Data.AllTags>>() }
    val bannerResponse: MutableLiveData<List<TagResponse.Data.VideoBanner>> by lazy { MutableLiveData<List<TagResponse.Data.VideoBanner>>() }

    /**
     * Exception handler function
     */
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", throwable.localizedMessage.toString())
    }

    /**
     * Hit get all video api
     * @param userId, subCat, lastVideoId, sortBy, pageSegment, searchContent
     */

    fun getAllVideo(userId: String, subCat: String, lastVideoId: String, pageSegment: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = videoRepository.getAllVideo(userId, subCat, lastVideoId, "", pageSegment, "")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    if (response.body()?.status == true) {
                        videoResponse.postValue(response.body())
                    } else {
                        onError(response.body()!!.message)
                    }
                } else {
                    onError("Something went wrong!")
                }
            }
        }
    }

    /**
     * Hit tag api
     * @param userId
     */
    fun getTags(userId: String, masterId: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = videoRepository.getTag(userId, masterId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    if (response.body()?.status == true) {
                        with(tagResponse) {
                            postValue(response.body()?.data?.all_tags)
                        }
                        with(bannerResponse) {
                            postValue(response?.body()?.data?.banner_list)
                        }
                    } else {
                        onError("Error : ${response.body()?.message}")
                    }
                } else {
                    onError("Something went wrong!")
                }
            }
        }
    }

    /**
     * Show error msg function
     */
    private fun onError(message: String) {
        errorMessage.postValue(message)
    }

    /**
     * Cancel the job
     */
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}