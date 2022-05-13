package com.example.weatherapp.helpers

import android.content.Context
import android.preference.PreferenceManager

// extension function for conversion
fun Double.toKm() = this * 1.60934
fun Double.toMiles() = this / 1.60934
fun Float.toKm() = this * 1.60934
fun Float.toMiles() = this / 1.60934
fun Float.toFahrenheit() = (this * 1.8) + 32
fun Float.toCelsius() = (this - 32) / 1.8

private const val UNITS_PREFERENCE = "UNITS_PREFERENCE"
private const val UNITS_METRIC = "metric"
private const val UNITS_IMPERIAL = "imperial"
private const val DEFAULT_UNITS = UNITS_METRIC

class MeasurementUnitsHelper(val context: Context) {

    fun getUnits(): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(UNITS_PREFERENCE, DEFAULT_UNITS) ?: DEFAULT_UNITS
    }

    fun saveUnits(units: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(UNITS_PREFERENCE, units).apply()
    }
}