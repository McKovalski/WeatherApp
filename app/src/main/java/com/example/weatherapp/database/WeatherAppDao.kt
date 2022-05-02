package com.example.weatherapp.database

import androidx.room.*
import com.example.weatherapp.models.CurrentLocation
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.models.Recent
import com.example.weatherapp.network.model.LocationData

@Dao
interface WeatherAppDao {

    @Query("SELECT * FROM Recent LIMIT 10")
    suspend fun getLastTenRecent(): List<LocationData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(recent: Recent)

    @Query("DELETE FROM RECENT")
    suspend fun deleteAlLRecent()


    @Query("SELECT * FROM Favourite")
    suspend fun getAllFavourites(): List<LocationData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: Favourite)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFavourites(favourites: List<Favourite>)

    @Delete
    suspend fun deleteFavourite(favourite: Favourite)

    @Query("DELETE FROM Favourite")
    suspend fun deleteAllFavourites()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCurrentLocation(currentLocation: CurrentLocation)

    @Query("SELECT * FROM CurrentLocation WHERE id = 1")
    suspend fun getCurrentLocation(): CurrentLocation?
}