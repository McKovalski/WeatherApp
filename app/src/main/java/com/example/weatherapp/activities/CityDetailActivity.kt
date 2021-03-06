package com.example.weatherapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.adapters.WeatherRecyclerAdapter
import com.example.weatherapp.databinding.ActivityCityDetailBinding
import com.example.weatherapp.helpers.ImageLoader
import com.example.weatherapp.helpers.LanguageHelper
import com.example.weatherapp.helpers.MeasurementUnitsHelper
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.network.model.LocationDetails
import com.example.weatherapp.viewmodels.MainViewModel
import kotlin.math.roundToInt

private const val EXTRA_LOCATION: String = "location"
private const val EXTRA_IS_FAVOURITE: String = "is_favourite"
private const val UNITS_METRIC = "metric"
private const val UNITS_IMPERIAL = "imperial"

class CityDetailActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityCityDetailBinding

    private var isFavourite: Boolean = false
    private lateinit var location: LocationDetails
    private lateinit var languageCode: String
    private lateinit var measurementUnits: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageCode = LanguageHelper(this).loadLocale()
        measurementUnits = MeasurementUnitsHelper(this).getUnits()

        binding = ActivityCityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_icons_android_ic_arrow_back)

        location = intent.extras?.getSerializable(EXTRA_LOCATION) as LocationDetails
        isFavourite = intent.extras?.getBoolean(EXTRA_IS_FAVOURITE) as Boolean

        setViews(location)

        // buduca prognoza
        var weatherRecyclerAdapter =
            WeatherRecyclerAdapter(this, location.consolidated_weather, false)
        binding.contentScrolling.nextDaysWeatherSequence.recyclerView.adapter =
            weatherRecyclerAdapter
        binding.contentScrolling.nextDaysWeatherSequence.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.contentScrolling.nextDaysWeatherSequence.today.text = getString(R.string.next_days)

        // danasnja prognoza
        val currentDate =
            location.consolidated_weather[0].applicable_date.replace("-", "/")

        mainViewModel.getDailyForecast(location.woeid, currentDate)
        mainViewModel.dailyForecast.observe(this) { res ->
            val weatherList = res.body()!!.subList(0, 8)
            weatherList.sortBy { it.created }
            weatherRecyclerAdapter = WeatherRecyclerAdapter(this, weatherList, true)
            binding.contentScrolling.todayWeatherSequence.recyclerView.adapter =
                weatherRecyclerAdapter
            binding.contentScrolling.todayWeatherSequence.recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.contentScrolling.todayWeatherSequence.today.text = getString(R.string.today)
        }
    }

    // overridamo ovu metodu kako bi se vratili na prosli activity u stanju kojem smo ga ostavili
    // tj. ne pokrecemo ponovno home activity, vec samo gasimo ovaj
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun setViews(locationDetails: LocationDetails) {
        // ikone vremenskih sastavnica
        binding.contentScrolling.masterInfoView.temperatureTile.iconType.setImageResource(R.drawable.ic_icons_android_ic_thermostat)
        binding.contentScrolling.masterInfoView.windTile.iconType.setImageResource(R.drawable.ic_icons_android_ic_wind)
        binding.contentScrolling.masterInfoView.humidityTile.iconType.setImageResource(R.drawable.ic_icons_android_ic_humidity)
        binding.contentScrolling.masterInfoView.pressureTile.iconType.setImageResource(R.drawable.ic_icons_android_ic_pressure)
        binding.contentScrolling.masterInfoView.visibilityTile.iconType.setImageResource(R.drawable.ic_icons_android_ic_visibility)
        binding.contentScrolling.masterInfoView.accuracyTile.iconType.setImageResource(R.drawable.ic_icons_android_ic_accuracy)

        // opisi vremenskih sastavnica
        binding.contentScrolling.masterInfoView.temperatureTile.typeDescription.text =
            getString(R.string.temperature)
        binding.contentScrolling.masterInfoView.windTile.typeDescription.text =
            getString(R.string.wind)
        binding.contentScrolling.masterInfoView.humidityTile.typeDescription.text =
            getString(R.string.humidity)
        binding.contentScrolling.masterInfoView.pressureTile.typeDescription.text =
            getString(R.string.pressure)
        binding.contentScrolling.masterInfoView.visibilityTile.typeDescription.text =
            getString(R.string.visibility)
        binding.contentScrolling.masterInfoView.accuracyTile.typeDescription.text =
            getString(R.string.accuracy)

        // podaci vremenskih sastavnica
        binding.contentScrolling.masterInfoView.temperatureTile.value.text =
            locationDetails.consolidated_weather[0].getMinMaxTemperature(measurementUnits)
        val windSpeed = locationDetails.consolidated_weather[0].getWindSpeed(measurementUnits)
        binding.contentScrolling.masterInfoView.windTile.value.text = windSpeed
        val humidity = locationDetails.consolidated_weather[0].humidity.toString()
        binding.contentScrolling.masterInfoView.humidityTile.value.text = "$humidity%"
        val pressure = locationDetails.consolidated_weather[0].air_pressure.roundToInt().toString()
        binding.contentScrolling.masterInfoView.pressureTile.value.text = "$pressure hPa"
        val visibility = locationDetails.consolidated_weather[0].getVisibility(measurementUnits)
        binding.contentScrolling.masterInfoView.visibilityTile.value.text = visibility
        val accuracy = locationDetails.consolidated_weather[0].predictability.toString()
        binding.contentScrolling.masterInfoView.accuracyTile.value.text = "$accuracy%"


        // osnovni info
        binding.toolbarLayout.title = locationDetails.title
        binding.toolbar.title = locationDetails.title
        binding.contentScrolling.masterInfoView.baseCityInfo.date.text =
            locationDetails.consolidated_weather[0].getFormattedDate()
        binding.contentScrolling.masterInfoView.baseCityInfo.time.text =
            locationDetails.getFormattedTimeAndTimezone()
        binding.contentScrolling.masterInfoView.baseCityInfo.forecastInfo.text =
            locationDetails.consolidated_weather[0].getLocalizedWeatherState(languageCode)
        binding.contentScrolling.masterInfoView.baseCityInfo.temperature.text =
            locationDetails.consolidated_weather[0].getCurrentTemperature(measurementUnits)
        val imageResource =
            ImageLoader(locationDetails.consolidated_weather[0].weather_state_name).getImageId()
        binding.contentScrolling.masterInfoView.baseCityInfo.forecastIcon.setImageResource(
            imageResource
        )

        // favoriti
        binding.iconFavourite.isSelected = isFavourite
        binding.iconFavourite.setOnClickListener {
            if (isFavourite) {
                isFavourite = false
                binding.iconFavourite.isSelected = false
                mainViewModel.removeFavourite(this, location.woeid)
            } else {
                isFavourite = true
                binding.iconFavourite.isSelected = true
                mainViewModel.getLastFavouritePosition(this)
                val newPosition: Int = mainViewModel.favouritesLastPosition.value?.plus(1) ?: 0
                val favourite = Favourite(
                    location.woeid,
                    location.title,
                    location.location_type,
                    location.latt_long,
                    newPosition
                )
                mainViewModel.addFavourite(this, favourite)
            }
        }
    }
}