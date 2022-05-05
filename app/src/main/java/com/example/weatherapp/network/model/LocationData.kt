package com.example.weatherapp.network.model

import com.example.weatherapp.models.Location
import java.io.Serializable

data class LocationData(
    val title: String,
    val location_type: String,
    val woeid: Int,
    override val latt_long: String
) : Location, Serializable