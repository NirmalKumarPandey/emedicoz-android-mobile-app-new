package com.emedicoz.app.video.ui.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VideoFactory constructor(private val videoRepository: VideoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(VideoViewModel::class.java)) {
            VideoViewModel(this.videoRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}