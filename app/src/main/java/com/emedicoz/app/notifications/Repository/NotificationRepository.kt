package com.emedicoz.app.notifications.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emedicoz.app.api.ApiInterface
import com.emedicoz.app.notifications.model.CategoryNotification
import com.emedicoz.app.notifications.model.NotificationCategoryData

class NotificationRepository(private val apiInterface: ApiInterface) {
    private val categoryLiveData = MutableLiveData<NotificationCategoryData>()

    val categorys: LiveData<NotificationCategoryData>
        get() = categoryLiveData
    suspend fun getCategory(){
        val categoryResult = apiInterface.getNotificationCategory()
        if (categoryResult?.body()!= null){
            categoryLiveData.postValue(categoryResult.body())
        }
    }
}