package com.example.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DailyTimeItemBinding
import com.example.weatherapp.helpers.ImageLoader
import com.example.weatherapp.network.model.Weather
import kotlin.math.roundToInt

class TodayWeatherRecyclerAdapter(
    private val context: Context,
    private val weatherList: List<Weather>
) : RecyclerView.Adapter<TodayWeatherRecyclerAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DailyTimeItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.daily_time_item, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherList[position]

        holder.binding.timeOrDay.text = weather.getTimeCreated()
        holder.binding.temperature.text = weather.the_temp.roundToInt().toString().plus("°")
        val imageResource = ImageLoader(weather.weather_state_name).getImageId()
        holder.binding.iconWeather.setImageResource(imageResource)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }
}