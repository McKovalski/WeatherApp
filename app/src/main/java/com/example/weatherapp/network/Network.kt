package com.example.weatherapp.network

import com.example.weatherapp.network.service.WeatherService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Network {

    private val service: WeatherService
    private val baseUrl = "https://www.metaweather.com/api/location/"

    init {
        val interceptor = HttpLoggingInterceptor()
        val httpClient = OkHttpClient.Builder().addInterceptor(interceptor)
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        service = retrofit.create(WeatherService::class.java)
    }

    fun getService(): WeatherService = service
}