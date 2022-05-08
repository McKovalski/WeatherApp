package com.example.weatherapp.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.database.WeatherAppDatabase
import com.example.weatherapp.models.CurrentLocation
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.models.Recent
import com.example.weatherapp.network.Network
import com.example.weatherapp.network.model.LocationData
import com.example.weatherapp.network.model.LocationDetails
import com.example.weatherapp.network.model.Weather
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel : ViewModel() {

    // lista gradova koju dobijemo iz query-a
    private val _searchLocationsList = MutableLiveData<Response<ArrayList<LocationData>>>()
    val searchLocationsList: LiveData<Response<ArrayList<LocationData>>>
        get() = _searchLocationsList

    // lista detaila od gradova koje dobijemo iz query-a
    private val _searchLocationsDetailsList = MutableLiveData<List<LocationDetails>>()
    val searchLocationsDetailsList: LiveData<List<LocationDetails>>
        get() = _searchLocationsDetailsList

    // detaljni podaci o nekome gradu
    private val _locationDetails = MutableLiveData<Response<LocationDetails>>()
    val locationDetails: LiveData<Response<LocationDetails>>
        get() = _locationDetails

    // dnevna prognoza za neki grad
    private val _dailyForecast = MutableLiveData<Response<ArrayList<Weather>>>()
    val dailyForecast: LiveData<Response<ArrayList<Weather>>>
        get() = _dailyForecast

    // lista nedavno pregledanih gradova
    val recentLocationDetails = MutableLiveData<List<LocationDetails>>()

    // lista favorita
    val favoriteLocationDetails = MutableLiveData<List<LocationDetails>>()

    // trenutna lokacija
    val currentLocation = MutableLiveData<CurrentLocation>()

    // tekst query-a pri izlasku iz search fragmenta
    var queryText: String? = null

    // zadnja pozicija favorita
    val favouritesLastPosition = MutableLiveData<Int>()

    private val currentLanguage = MutableLiveData<String>()


    fun getLocationList(query: String) {
        viewModelScope.launch {
            val searchResponse = Network().getService().getLocationsByQuery(query)
            val searchDetails = searchResponse.body()?.map { location ->
                async {
                    Network().getService().getLocationById(location.woeid).body()!!
                }
            }
            _searchLocationsList.value = searchResponse
            _searchLocationsDetailsList.value = searchDetails?.awaitAll()!!
        }
    }

    fun getLocationDetails(woeid: Int) {
        viewModelScope.launch {
            _locationDetails.value = Network().getService().getLocationById(woeid)
        }
    }

    fun getDailyForecast(woeid: Int, date: String) {
        viewModelScope.launch {
            _dailyForecast.value = Network().getService().getDailyForecast(woeid, date)
        }
    }

    fun getRecent(context: Context) {
        viewModelScope.launch {
            val recent =
                WeatherAppDatabase.getDatabase(context)?.weatherAppDao()?.getLastTenRecent()
            val recentDetailsResponse = recent?.map { r ->
                async {
                    Network().getService().getLocationById(r.woeid).body()!!
                }
            }
            recentLocationDetails.value = recentDetailsResponse?.awaitAll()
        }
    }

    fun addRecent(context: Context, recent: Recent) {
        viewModelScope.launch {
            WeatherAppDatabase.getDatabase(context)?.weatherAppDao()?.insertRecent(recent)
            getRecent(context)
        }
    }

    fun deleteAllRecent(context: Context) {
        viewModelScope.launch {
            WeatherAppDatabase.getDatabase(context)?.weatherAppDao()?.deleteAlLRecent()
            getRecent(context)
        }
    }

    fun getFavourites(context: Context) {
        viewModelScope.launch {
            val favorites =
                WeatherAppDatabase.getDatabase(context)?.weatherAppDao()?.getAllFavourites()
            val favoritesDetailsResponse = favorites?.map { f ->
                async {
                    Network().getService().getLocationById(f.woeid).body()!!
                }
            }
            favoriteLocationDetails.value = favoritesDetailsResponse?.awaitAll()
        }
    }

    fun addFavourite(context: Context, favourite: Favourite) {
        viewModelScope.launch {
            WeatherAppDatabase.getDatabase(context)?.weatherAppDao()?.insertFavourite(favourite)
            getFavourites(context)
        }
    }

    fun addAllFavourites(context: Context, favourites: List<Favourite>) {
        viewModelScope.launch {
            WeatherAppDatabase.getDatabase(context)?.weatherAppDao()
                ?.insertAllFavourites(favourites)
            getFavourites(context)
        }
    }

    fun deleteAllFavourites(context: Context) {
        viewModelScope.launch {
            WeatherAppDatabase.getDatabase(context)?.weatherAppDao()?.deleteAllFavourites()
            getFavourites(context)
        }
    }

    fun removeFavourite(context: Context, woeid: Int) {
        viewModelScope.launch {
            WeatherAppDatabase.getDatabase(context)?.weatherAppDao()?.deleteFavouriteById(woeid)
            getFavourites(context)
        }
    }

    fun getLastFavouritePosition(context: Context) {
        viewModelScope.launch {
            favouritesLastPosition.value =
                WeatherAppDatabase.getDatabase(context)?.weatherAppDao()?.getLastFavouritePosition()
        }
    }

    fun getCurrentLocation(context: Context) {
        viewModelScope.launch {
            currentLocation.value =
                WeatherAppDatabase.getDatabase(context)?.weatherAppDao()?.getCurrentLocation()
        }
    }

    fun setCurrentLocation(context: Context, currentLocation: CurrentLocation) {
        viewModelScope.launch {
            WeatherAppDatabase.getDatabase(context)?.weatherAppDao()?.setCurrentLocation(currentLocation)
            getCurrentLocation(context)
        }
    }
}