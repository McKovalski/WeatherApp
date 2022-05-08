package com.example.weatherapp.network.model

import com.example.weatherapp.helpers.toKm
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

data class LocationDetails(
    val consolidated_weather: ArrayList<Weather>,
    val title: String,
    val location_type: String,
    val time: String, //2022-04-13T20:15:34.936607+01:00
    val timezone_name: String,
    val latt_long: String,
    val woeid: Int
) : Serializable {
    fun getFormattedTimeAndTimezone(): String {
        val dateAndTime = time.split(".")[0] // 2022-04-13T20:15:34
        val t = dateAndTime.split("T")[1] // 20:15:34
        val timeElements = t.split(":")
        val hours = timeElements[0]
        val minutes = timeElements[1]
        val afterNoonTag: String = if (hours.toInt() >= 12) {
            "PM"
        } else {
            "AM"
        }
        return "$hours:$minutes $afterNoonTag ($timezone_name)"
    }

    fun getFormattedTime(): String {
        val dateAndTime = time.split(".")[0] // 2022-04-13T20:15:34
        val t = dateAndTime.split("T")[1] // 20:15:34
        val timeElements = t.split(":")
        val hours = timeElements[0]
        val minutes = timeElements[1]
        val afterNoonTag: String = if (hours.toInt() >= 12) {
            "PM"
        } else {
            "AM"
        }
        return "$hours:$minutes $afterNoonTag"
    }

    fun getGMT(): String {
        val ending = time.takeLast(6).split(":")[0] // +01
        val prefix = ending.take(1) // +
        return if (ending.removeRange(0, 1).toInt() < 10) {
            "GMT$prefix${ending.removeRange(0,2)}"
        } else {
            "GMT$ending"
        }
    }

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
        return latt_long.split(",")[1].toDouble()
    }
}

data class Weather(
    val id: Long,
    val weather_state_name: String,
    val weather_state_abbr: String,
    val created: String, //2022-04-13T21:59:01.438548Z
    val applicable_date: String,
    val min_temp: Float,
    val max_temp: Float,
    val the_temp: Float,
    val wind_speed: Float,
    val wind_direction: Float,
    val wind_direction_compass: String,
    val air_pressure: Float,
    val humidity: Int,
    val visibility: Float,
    val predictability: Int,
) : Serializable {
    fun getFormattedDate(): String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(applicable_date)
        return SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(date!!)
    }

    fun getDayInWeek(): Int {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(applicable_date)
        val calendar = Calendar.getInstance()
        calendar.time = date!!
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun getTimeCreated(): String {
        return created.subSequence(11,16) as String
    }

    fun getLocalizedWeatherState(languageCode: String): String {
        return if (languageCode == "hr") {
            when (weather_state_name) {
                "Clear" -> "Vedro"
                "Light Cloud" -> "Mjestimice oblačno"
                "Heavy Cloud" -> "Oblačno"
                "Light Rain" -> "Blaga kiša"
                "Heavy Rain" -> "Jaka kiša"
                "Showers" -> "Pljuskovi"
                "Thunderstorm" -> "Olujno"
                "Sleet" -> "Susnježica"
                else -> "Snijeg"
            }
        } else {
            weather_state_name
        }
    }

    fun getVisibility(measurementUnits: String): String {
        return if (measurementUnits == "km") {
            "${visibility.toKm().roundToInt()} km"
        } else {
            "${visibility.roundToInt()} mi"
        }
    }

    fun getWindSpeed(measurementUnits: String): String {
        return if (measurementUnits == "km") {
            "${wind_speed.toKm().roundToInt()} km/h ($wind_direction_compass)"
        } else {
            "${wind_speed.roundToInt()} mi/h ($wind_direction_compass)"
        }
    }
}