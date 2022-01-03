package com.emedicoz.app.notifications.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emedicoz.app.notifications.Repository.NotificationRepository
import com.emedicoz.app.notifications.model.NotificationCategoryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: NotificationRepository): ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCategory()
        }
    }

    val categoryData : LiveData<NotificationCategoryData>
        get() = repository.categorys

}