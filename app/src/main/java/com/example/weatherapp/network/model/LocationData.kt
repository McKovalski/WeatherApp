package com.example.weatherapp.network.model

data class LocationData(
    val title: String,
    val location_type: String,
    val woeid: Int,
    val latt_long: String
)