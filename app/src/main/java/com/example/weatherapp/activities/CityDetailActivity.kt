package com.example.weatherapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.adapters.NextDaysWeatherRecyclerAdapter
import com.example.weatherapp.adapters.TodayWeatherRecyclerAdapter
import com.example.weatherapp.databinding.ActivityCityDetailBinding
import com.example.weatherapp.helpers.ImageLoader
import com.example.weatherapp.network.model.LocationDetails
import com.example.weatherapp.viewmodels.MainViewModel
import kotlin.math.roundToInt

private const val EXTRA_WOEID: String = "woeid"

class CityDetailActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityCityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_icons_android_ic_arrow_back)

        val woeid: Int = intent.extras?.getInt(EXTRA_WOEID) as Int
        mainViewModel.getLocationDetails(woeid)
        mainViewModel.locationDetails.observe(this, Observer { it ->
            if (it.isSuccessful) {
                val locationDetails = it.body()!!

                setViews(locationDetails)

                val nextDaysWeatherRecyclerAdapter =
                    NextDaysWeatherRecyclerAdapter(this, locationDetails.consolidated_weather)
                binding.contentScrolling.nextDaysWeatherSequence.recyclerView.adapter =
                    nextDaysWeatherRecyclerAdapter
                binding.contentScrolling.nextDaysWeatherSequence.recyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.contentScrolling.nextDaysWeatherSequence.today.text = getString(R.string.next_days)

                val currentDate =
                    locationDetails.consolidated_weather[0].applicable_date.replace("-", "/")
                mainViewModel.getDailyForecast(woeid, currentDate)
                mainViewModel.dailyForecast.observe(this, Observer { res ->
                    val weatherList = res.body()!!.subList(0, 8)
                    weatherList.sortBy { it.created }
                    val todayWeatherRecyclerAdapter = TodayWeatherRecyclerAdapter(this, weatherList)
                    binding.contentScrolling.todayWeatherSequence.recyclerView.adapter =
                        todayWeatherRecyclerAdapter
                    binding.contentScrolling.todayWeatherSequence.recyclerView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    binding.contentScrolling.todayWeatherSequence.today.text = getString(R.string.today)
                })

            } else {
                AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("Something went wrong")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_warning)
                    .show()
            }
        })
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
        val minTemp = locationDetails.consolidated_weather[0].min_temp.roundToInt().toString()
        val maxTemp = locationDetails.consolidated_weather[0].max_temp.roundToInt().toString()
        binding.contentScrolling.masterInfoView.temperatureTile.value.text = "$minTemp° / $maxTemp°"
        val windSpeed = locationDetails.consolidated_weather[0].wind_speed.roundToInt().toString()
        val windDirection = locationDetails.consolidated_weather[0].wind_direction_compass
        binding.contentScrolling.masterInfoView.windTile.value.text =
            "$windSpeed km/h ($windDirection)"
        val humidity = locationDetails.consolidated_weather[0].humidity.toString()
        binding.contentScrolling.masterInfoView.humidityTile.value.text = "$humidity%"
        val pressure = locationDetails.consolidated_weather[0].air_pressure.roundToInt().toString()
        binding.contentScrolling.masterInfoView.pressureTile.value.text = "$pressure hPa"
        val visibility =
            (locationDetails.consolidated_weather[0].visibility * 1.61).roundToInt().toString()
        binding.contentScrolling.masterInfoView.visibilityTile.value.text = "$visibility km"
        val accuracy = locationDetails.consolidated_weather[0].predictability.toString()
        binding.contentScrolling.masterInfoView.accuracyTile.value.text = "$accuracy%"


        // basic info
        binding.toolbarLayout.title = locationDetails.title
        binding.toolbar.title = locationDetails.title
        binding.contentScrolling.masterInfoView.baseCityInfo.date.text =
            locationDetails.consolidated_weather[0].getFormattedDate()
        binding.contentScrolling.masterInfoView.baseCityInfo.time.text =
            locationDetails.getFormattedTime()
        binding.contentScrolling.masterInfoView.baseCityInfo.forecastInfo.text =
            locationDetails.consolidated_weather[0].weather_state_name
        binding.contentScrolling.masterInfoView.baseCityInfo.temperature.text =
            locationDetails.consolidated_weather[0].the_temp.roundToInt().toString().plus("°")
        val imageResource =
            ImageLoader(locationDetails.consolidated_weather[0].weather_state_name).getImageId()
        binding.contentScrolling.masterInfoView.baseCityInfo.forecastIcon.setImageResource(
            imageResource
        )
    }
}