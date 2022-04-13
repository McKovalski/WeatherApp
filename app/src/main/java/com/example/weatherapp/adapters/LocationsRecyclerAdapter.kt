package com.example.weatherapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.activities.CityDetailActivity
import com.example.weatherapp.databinding.LocationItemViewBinding
import com.example.weatherapp.network.model.LocationData

private const val EXTRA_WOEID: String = "woeid"

class LocationsRecyclerAdapter(
    private val context: Context,
    private val locationList: ArrayList<LocationData>
) : RecyclerView.Adapter<LocationsRecyclerAdapter.LocationViewHolder>() {

    class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = LocationItemViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.location_item_view, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]
        holder.binding.cityName.text = location.title
        holder.binding.coordinates.text = location.getCoordinates()
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CityDetailActivity::class.java).apply {
                putExtra(EXTRA_WOEID, location.woeid)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

}