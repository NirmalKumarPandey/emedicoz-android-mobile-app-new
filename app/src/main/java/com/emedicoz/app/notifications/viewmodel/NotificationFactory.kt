package com.emedicoz.app.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emedicoz.app.notifications.Repository.NotificationRepository

class NotificationFactory(private val repository: NotificationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationViewModel(repository) as T
    }
}