package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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


    @Query("SELECT * FROM Favourite ORDER BY position")
    suspend fun getAllFavourites(): List<Favourite>

    @Query("SELECT * FROM Favourite WHERE woeid = :woeid")
    suspend fun getFavouriteById(woeid: Int): Favourite

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: Favourite)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFavourites(favourites: List<Favourite>)

    @Query("DELETE FROM Favourite WHERE woeid = :woeid")
    suspend fun deleteFavouriteById(woeid: Int)

    @Query("DELETE FROM Favourite")
    suspend fun deleteAllFavourites()

    @Query("SELECT MAX(position) FROM Favourite")
    suspend fun getLastFavouritePosition(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCurrentLocation(currentLocation: CurrentLocation)

    @Query("SELECT * FROM CurrentLocation WHERE id = 1")
    suspend fun getCurrentLocation(): CurrentLocation?
}