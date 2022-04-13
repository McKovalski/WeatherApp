package com.example.weatherapp.network.model

data class LocationData(
    val title: String,
    val locationType: String,
    val woeid: Int,
    val latt_long: String
)