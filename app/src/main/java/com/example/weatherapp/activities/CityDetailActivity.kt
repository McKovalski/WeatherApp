package com.example.weatherapp.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityCityDetailBinding
import com.example.weatherapp.viewmodels.MainViewModel

private const val EXTRA_WOEID: String = "woeid"

class CityDetailActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityCityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val woeid: Int = intent.extras?.getInt(EXTRA_WOEID) as Int
        mainViewModel.getLocationDetails(woeid)
        mainViewModel.locationDetails.observe(this, Observer {
            if (it.isSuccessful) {
                val locationDetails = it.body()!!
                val wholeText: String = locationDetails.title + " " + locationDetails.locationType + " " + locationDetails.timezone
                binding.everything.text = wholeText
                binding.everything.textSize = 16f
                binding.everything.setTextColor(getColor(R.color.black))
                // TODO observe location details

                /*val currentDate = locationDetails.consolidatedWeather[0].applicable_date.replace("-", "/")
                mainViewModel.getDailyForecast(woeid, currentDate)
                mainViewModel.dailyForecast.observe(this, Observer {
                    TODO("observe daily forecast and bind data to view")
                })*/

            } else {
                AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("Something went wrong")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_warning)
                    .show()
            }
        })

    }
}