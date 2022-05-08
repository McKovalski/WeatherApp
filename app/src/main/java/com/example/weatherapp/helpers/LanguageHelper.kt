package com.example.weatherapp.helpers

import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import java.util.*

private const val LANGUAGE_PREFERENCE = "LANGUAGE_PREFERENCE"
private const val DEFAULT_LANGUAGE = "en"

class LanguageHelper(val context: Context) {

    fun loadLocale(): String {
        val languageCode = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(LANGUAGE_PREFERENCE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        changeLanguage(languageCode)
        return languageCode
    }

    fun changeLanguage(languageCode: String) {
        saveLocale(languageCode)
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    private fun saveLocale(languageCode: String) =
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(LANGUAGE_PREFERENCE, languageCode).apply()
}