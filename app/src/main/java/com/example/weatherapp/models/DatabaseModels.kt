package com.example.weatherapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.abs

@Entity
data class Recent(
    @PrimaryKey
    val woeid: Int,
    val title: String,
    val location_type: String,
    override val latt_long: String
) : Location

@Entity
data class Favourite(
    @PrimaryKey
    val woeid: Int,
    val title: String,
    val location_type: String,
    override val latt_long: String
) : Location

@Entity
data class CurrentLocation(
    @PrimaryKey
    val id: Int = 1,
    val latitude: Double,
    val longitude: Double
)

interface Location {
    val latt_long: String

    fun getCoordinates(): String {
        val coordinates = latt_long.split(",")
        var xInt = coordinates[0].trim().split(".")[0]
        val xDecimal = coordinates[0].trim().split(".")[1]
        var yInt = coordinates[1].trim().split(".")[0]
        val yDecimal = coordinates[1].trim().split(".")[1]
        val x: String
        if (xInt.toInt() >= 0) {
            x = "N"
        } else {
            x = "S"
            xInt = (abs(xInt.toInt())).toString()
        }
        val y: String
        if (yInt.toInt() >= 0) {
            y = "E"
        } else {
            y = "W"
            yInt = (abs(yInt.toInt())).toString()
        }

        return "$xInt°${xDecimal.take(2)}$x', $yInt°${yDecimal.take(2)}$y'"
    }

    fun getLatitude(): Double {
        return latt_long.split(",")[0].toDouble()
    }

    fun getLongitude(): Double {
        return latt_long.split(",")[0].toDouble()
    }
}