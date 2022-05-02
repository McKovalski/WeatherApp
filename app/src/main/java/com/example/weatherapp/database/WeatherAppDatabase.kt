package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.weatherapp.models.CurrentLocation
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.models.Recent

@Database(
    entities = [Recent::class, Favourite::class, CurrentLocation::class],
    version = 2,
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
            val MIGRATION_1_2: Migration = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("CREATE TABLE IF NOT EXISTS CurrentLocation(" +
                            "id INTEGER PRIMARY KEY NOT NULL," +
                            "latitude DOUBLE NOT NULL," +
                            "longitude DOUBLE NOT NULL)")
                }
            }

            return Room.databaseBuilder(
                context.applicationContext,
                WeatherAppDatabase::class.java,
                "WeatherAppDatabase"
            ).addMigrations(MIGRATION_1_2).build()
        }
    }
}