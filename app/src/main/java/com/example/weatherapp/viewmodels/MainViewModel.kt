package com.example.weatherapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.network.Network
import com.example.weatherapp.network.model.LocationData
import com.example.weatherapp.network.model.LocationDetails
import com.example.weatherapp.network.model.Weather
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel: ViewModel() {

    private val _locationsList = MutableLiveData<Response<ArrayList<LocationData>>>()
    val locationsList: LiveData<Response<ArrayList<LocationData>>>
        get() = _locationsList

    private val _locationDetails = MutableLiveData<Response<LocationDetails>>()
    val locationDetails: LiveData<Response<LocationDetails>>
        get() = _locationDetails

    private val _dailyForecast = MutableLiveData<Response<ArrayList<Weather>>>()
    val dailyForecast: LiveData<Response<ArrayList<Weather>>>
        get() = _dailyForecast

    fun getLocationList(query: String) {
        viewModelScope.launch {
            _locationsList.value = Network().getService().getLocationsByQuery(query)
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
}