package com.example.weatherapp.network.model

import java.util.*

data class LocationDetails(
    val consolidatedWeather: ArrayList<Weather>,
    val title: String,
    val locationType: String,
    val time: Date,
    val sun_set: Date,
    val sun_rise: Date,
    val timezone_name: String,
    val timezone: String
)

data class Weather(
    val id: Int,
    val weather_state_name: String,
    val weather_state_abbr: String,
    val applicable_date: String,
    val min_temp: Float,
    val max_temp: Float,
    val the_temp: Float,
    val wind_speed: Float,
    val wind_direction: Float,
    val air_pressure: Float,
    val humidity: Int,
    val visibility: Float,
    val predictability: Int,
) {

}