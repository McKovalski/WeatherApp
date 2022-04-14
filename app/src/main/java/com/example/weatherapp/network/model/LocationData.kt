package com.example.weatherapp.network.model

import kotlin.math.abs

data class LocationData(
    val title: String,
    val location_type: String,
    val woeid: Int,
    val latt_long: String
) {
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
}