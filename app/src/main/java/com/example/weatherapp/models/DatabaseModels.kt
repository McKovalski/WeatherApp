package com.example.weatherapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recent(
    @PrimaryKey
    val woeid: Int,
    val title: String,
    val location_type: String,
    val latt_long: String
)

@Entity
data class Favourite(
    @PrimaryKey
    val woeid: Int,
    val title: String,
    val location_type: String,
    val latt_long: String,
    val position: Int
)

@Entity
data class CurrentLocation(
    @PrimaryKey
    val id: Int = 1,
    val latitude: Double,
    val longitude: Double
)