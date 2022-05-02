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
import com.example.weatherapp.fragments.SearchFragment
import com.example.weatherapp.helpers.ImageLoader
import com.example.weatherapp.models.CurrentLocation
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.models.Recent
import com.example.weatherapp.network.model.LocationData
import com.example.weatherapp.network.model.LocationDetails
import kotlin.math.*

private const val EXTRA_LOCATION: String = "location"
private const val EXTRA_IS_FAVOURITE: String = "is_favourite"

class LocationsRecyclerAdapter(
    private val context: Context,
    private val locationList: List<LocationData>,
    private val detailsList: List<LocationDetails>,
    private val favorites: ArrayList<LocationData>,
    private val fragment: SearchFragment,
    private val currentLocation: CurrentLocation? = null
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
        val locationDetails = detailsList[position]
        var isFavourite: Boolean

        holder.binding.cityName.text = location.title
        holder.binding.temperature.text = locationDetails.consolidated_weather[0].the_temp.roundToInt().toString()
        val imageResource = ImageLoader(locationDetails.consolidated_weather[0].weather_state_name).getImageId()
        holder.binding.weatherIcon.setImageResource(imageResource)

        holder.binding.firstDetail.text = location.getCoordinates()
        if (currentLocation != null) {
            val startLatitude = location.getLatitude()
            val startLongitude = location.getLongitude()
            holder.binding.secondDetail.text = calculateDistance(
                startLatitude,
                startLongitude,
                currentLocation.latitude,
                currentLocation.longitude
            )
        }

        if (location in favorites) {
            holder.binding.favouriteIcon.setImageResource(R.drawable.ic_icons_android_ic_star_1)
            holder.binding.favouriteIcon.tag = "isFavourite"
            isFavourite = true
        } else {
            holder.binding.favouriteIcon.setImageResource(R.drawable.ic_icons_android_ic_star_0)
            holder.binding.favouriteIcon.tag = "isNotFavourite"
            isFavourite = false
        }

        holder.binding.favouriteIcon.setOnClickListener {
            if (holder.binding.favouriteIcon.tag == "isNotFavourite") {
                holder.binding.favouriteIcon.tag = "isFavourite"
                isFavourite = true
                holder.binding.favouriteIcon.setImageResource(R.drawable.ic_icons_android_ic_star_1)
                fragment.addToFavourites(Favourite(
                    location.woeid,
                    location.title,
                    location.location_type,
                    location.latt_long
                ))
                favorites.add(location) // TODO provjera
            } else {
                holder.binding.favouriteIcon.tag = "isNotFavourite"
                isFavourite = false
                holder.binding.favouriteIcon.setImageResource(R.drawable.ic_icons_android_ic_star_0)
                fragment.removeFromFavourites(Favourite(
                    location.woeid,
                    location.title,
                    location.location_type,
                    location.latt_long
                ))
                favorites.remove(location) // TODO provjera
            }
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            // Dodajemo u Recent
            fragment.addToRecent(Recent(
                location.woeid,
                location.title,
                location.location_type,
                location.latt_long
            ))

            val intent = Intent(context, CityDetailActivity::class.java).apply {
                putExtra(EXTRA_LOCATION, location)
                putExtra(EXTRA_IS_FAVOURITE, isFavourite)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    private fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): String {
        val lat1 = Math.toRadians(startLatitude)
        val lat2 = Math.toRadians(endLatitude)
        val lon1 = Math.toRadians(startLongitude)
        val lon2 = Math.toRadians(endLongitude)

        // Haversine formula
        val dlat: Double = lat2 - lat1
        val dlon: Double = lon2 - lon1

        val a: Double = (sin(dlat / 2).pow(2.0)
                + (cos(lat1) * cos(lat2)
                * sin(dlon / 2).pow(2.0)))

        val c: Double = 2 * asin(sqrt(a))

        // Radius of earth in kilometers. Use 3956
        // for miles
        val r = 6371.0

        return "Distance: ${(c * r).roundToInt()} km"
    }
}