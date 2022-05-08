package com.example.weatherapp.helpers

import android.content.Context
import android.preference.PreferenceManager

// extension function for conversion
fun Double.toKm() = this * 1.60934
fun Double.toMiles() = this / 1.60934
fun Float.toKm() = this * 1.60934
fun Float.toMiles() = this / 1.60934

private const val UNITS_PREFERENCE = "UNITS_PREFERENCE"
private const val DEFAULT_UNITS = "km"

class MeasurementUnitsHelper(val context: Context) {

    fun getUnits(): String {
        val units = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(UNITS_PREFERENCE, DEFAULT_UNITS) ?: DEFAULT_UNITS
        return units
    }

    fun saveUnits(units: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(UNITS_PREFERENCE, units).apply()
    }
}