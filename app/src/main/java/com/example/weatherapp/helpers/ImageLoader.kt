package com.example.weatherapp.helpers

import com.example.weatherapp.R

class ImageLoader(private val weatherStateName: String) {

    fun getImageId(): Int {
        return when(weatherStateName) {
            "Clear" -> R.drawable.ic_icons_weather_c
            "Light Cloud" -> R.drawable.ic_icons_weather_lc
            "Heavy Cloud" -> R.drawable.ic_icons_weather_hc
            "Light Rain" -> R.drawable.ic_icons_weather_lr
            "Heavy Rain" -> R.drawable.ic_icons_weather_hr
            "Showers" -> R.drawable.ic_icons_weather_s
            "Thunderstorm" -> R.drawable.ic_icons_weather_t
            "Sleet" -> R.drawable.ic_icons_weather_sl
            else -> {
                R.drawable.ic_icons_weather_sn // Snow
            }
        }
    }
}