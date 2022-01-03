package com.emedicoz.app.retrofit

import com.emedicoz.app.utilso.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    val MAIN_SERVER_URL = Constants.BASE_API_URL
    val BASE_URL2 = Constants.BASE_API_URL

    fun getInstance() : Retrofit {
        return Retrofit.Builder().baseUrl(MAIN_SERVER_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }
}