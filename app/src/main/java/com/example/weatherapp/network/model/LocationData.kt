package com.example.weatherapp.network.model

data class LocationData(
    val title: String,
    val location_type: String,
    val woeid: Int,
    val latt_long: String
) {
    fun getCoordinates(): String {
        val coordinates = latt_long.split(",")
        val xInt = coordinates[0].trim().split(".")[0].toInt()
        val xDecimal = coordinates[0].trim().split(".")[1].toInt()
        val yInt = coordinates[1].trim().split(".")[0].toInt()
        val yDecimal = coordinates[1].trim().split(".")[1].toInt()
        val x: String = if (xInt >= 0) {
            "N"
        } else {
            "S"
        }
        val y: String = if (yInt >= 0) {
            "E"
        } else {
            "W"
        }

        return "$xInt°$xDecimal$x', $yInt°$yDecimal$y'"
    }
}