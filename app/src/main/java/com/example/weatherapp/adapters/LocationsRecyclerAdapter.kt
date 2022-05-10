package com.example.weatherapp.adapters

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.activities.CityDetailActivity
import com.example.weatherapp.databinding.LocationItemViewBinding
import com.example.weatherapp.fragments.SearchFragment
import com.example.weatherapp.helpers.ImageLoader
import com.example.weatherapp.helpers.MeasurementUnitsHelper
import com.example.weatherapp.helpers.toMiles
import com.example.weatherapp.models.CurrentLocation
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.models.Recent
import com.example.weatherapp.network.model.LocationDetails
import kotlin.math.roundToInt

private const val EXTRA_LOCATION: String = "location"
private const val EXTRA_IS_FAVOURITE: String = "is_favourite"
private const val UNITS_METRIC = "metric"
private const val UNITS_IMPERIAL = "imperial"

class LocationsRecyclerAdapter(
    private val context: Context,
    private val locationDetailsList: ArrayList<LocationDetails>,
    private val favorites: ArrayList<LocationDetails>,
    private val fragment: SearchFragment,
    private var currentLocation: CurrentLocation? = null
) : RecyclerView.Adapter<LocationsRecyclerAdapter.LocationViewHolder>() {

    fun updateLocations(list: ArrayList<LocationDetails>) {
        locationDetailsList.clear()
        locationDetailsList.addAll(list)
        notifyDataSetChanged()
    }

    fun clearLocations() {
        locationDetailsList.clear()
        notifyDataSetChanged()
    }

    fun updateFavourites(list: ArrayList<LocationDetails>) {
        favorites.clear()
        favorites.addAll(list)
        notifyDataSetChanged()
    }

    fun updateCurrentLocation(cl: CurrentLocation) {
        this.currentLocation = cl
        notifyDataSetChanged()
    }

    class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = LocationItemViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.location_item_view, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationDetailsList[position]
        var isFavourite: Boolean
        val measurementUnits = MeasurementUnitsHelper(context).getUnits()

        holder.binding.cityName.text = location.title
        holder.binding.temperature.text =
            location.consolidated_weather[0].getCurrentTemperature(measurementUnits)
        val imageResource =
            ImageLoader(location.consolidated_weather[0].weather_state_name).getImageId()
        holder.binding.weatherIcon.setImageResource(imageResource)

        holder.binding.firstDetail.text = location.getCoordinates()
        if (currentLocation != null) {
            val startLatitude = location.getLatitude()
            val startLongitude = location.getLongitude()
            val results = FloatArray(1)
            Log.d(
                "Current location",
                "${currentLocation!!.latitude}, ${currentLocation!!.longitude}"
            )
            Location.distanceBetween(
                startLatitude,
                startLongitude,
                currentLocation!!.latitude,
                currentLocation!!.longitude,
                results
            )
            // distance je u metrima, moramo pretvoriti u kilometre
            val distance = results[0] / 1000
            if (measurementUnits == UNITS_METRIC) {
                holder.binding.secondDetail.text =
                    fragment.getString(
                        R.string.calculate_distance_km,
                        distance.roundToInt(),
                        "km"
                    )
            } else {
                holder.binding.secondDetail.text =
                    fragment.getString(
                        R.string.calculate_distance_miles,
                        distance.toMiles().roundToInt(),
                        "mi"
                    )
            }
        }

        if (location.woeid in favorites.map { favourite -> favourite.woeid }) {
            holder.binding.favouriteIcon.isSelected = true
            isFavourite = true
        } else {
            holder.binding.favouriteIcon.isSelected = false
            isFavourite = false
        }

        holder.binding.favouriteIcon.setOnClickListener {
            if (!holder.binding.favouriteIcon.isSelected) {
                holder.binding.favouriteIcon.isSelected = true
                isFavourite = true
                val newPosition: Int = fragment.getLastFavouritePosition() + 1
                val favourite = Favourite(
                    location.woeid,
                    location.title,
                    location.location_type,
                    location.latt_long,
                    newPosition
                )
                fragment.addToFavourites(favourite)
                favorites.add(location)
            } else {
                holder.binding.favouriteIcon.isSelected = false
                isFavourite = false
                fragment.removeFromFavourites(location.woeid)
                favorites.remove(location)
            }
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            // Dodajemo u Recent
            fragment.addToRecent(
                Recent(
                    location.woeid,
                    location.title,
                    location.location_type,
                    location.latt_long
                )
            )

            val intent = Intent(context, CityDetailActivity::class.java).apply {
                putExtra(EXTRA_LOCATION, location)
                putExtra(EXTRA_IS_FAVOURITE, isFavourite)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return locationDetailsList.size
    }
}