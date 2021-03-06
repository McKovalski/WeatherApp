package com.example.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DailyTimeItemBinding
import com.example.weatherapp.helpers.ImageLoader
import com.example.weatherapp.helpers.MeasurementUnitsHelper
import com.example.weatherapp.network.model.Weather

private const val UNITS_METRIC = "metric"
private const val UNITS_IMPERIAL = "imperial"

class WeatherRecyclerAdapter(
    private val context: Context,
    private val weatherList: List<Weather>,
    private val isToday: Boolean
) : RecyclerView.Adapter<WeatherRecyclerAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DailyTimeItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.daily_time_item, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherList[position]
        val measurementUnits = MeasurementUnitsHelper(context).getUnits()

        holder.binding.timeOrDay.text = if (isToday) {
            weather.getTimeCreated()
        } else {
            when (weather.getDayInWeek()) {
                0 -> context.getString(R.string.mon)
                1 -> context.getString(R.string.tue)
                2 -> context.getString(R.string.wed)
                3 -> context.getString(R.string.thu)
                4 -> context.getString(R.string.fri)
                5 -> context.getString(R.string.sat)
                else -> context.getString(R.string.sun)
            }
        }
        holder.binding.temperature.text = weather.getCurrentTemperature(measurementUnits)
        val imageResource = ImageLoader(weather.weather_state_name).getImageId()
        holder.binding.iconWeather.setImageResource(imageResource)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }
}