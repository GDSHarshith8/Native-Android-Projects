package com.profileviewer.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {

    @GET("https://raw.githubusercontent.com/android-assesment/profile/refs/heads/main/data.json")
    suspend fun getProfile(): ApiResponse
}

object RetrofitClient {

    private const val BASE_URL = "https://raw.githubusercontent.com/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
