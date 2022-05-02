package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.models.CurrentLocation
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.models.Recent

@Database(
    entities = [Recent::class, Favourite::class, CurrentLocation::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherAppDatabase : RoomDatabase() {
    abstract fun weatherAppDao(): WeatherAppDao

    companion object {
        private var instance: WeatherAppDatabase? = null

        fun getDatabase(context: Context): WeatherAppDatabase? {
            if (instance == null) {
                instance = buildDatabase(context)
            }
            return instance
        }

        private fun buildDatabase(context: Context): WeatherAppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                WeatherAppDatabase::class.java,
                "WeatherAppDatabase"
            ).build()
        }
    }
}