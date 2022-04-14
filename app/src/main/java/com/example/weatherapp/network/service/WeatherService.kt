package com.example.weatherapp.network.service

import com.example.weatherapp.network.model.LocationData
import com.example.weatherapp.network.model.LocationDetails
import com.example.weatherapp.network.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {
    @GET("search/")
    suspend fun getLocationsByQuery(@Query("query") query: String): Response<ArrayList<LocationData>>

    @GET("{woeid}")
    suspend fun getLocationById(@Path("woeid") woeid: Int): Response<LocationDetails>

    @GET("{woeid}/{date}")
    suspend fun getDailyForecast(
        @Path("woeid") woeid: Int,
        @Path("date") date: String
    ): Response<ArrayList<Weather>>
}