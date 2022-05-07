package com.example.weatherapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.activities.CityDetailActivity
import com.example.weatherapp.databinding.FavouriteItemViewBinding
import com.example.weatherapp.fragments.MyCitiesFragment
import com.example.weatherapp.helpers.ImageLoader
import com.example.weatherapp.models.Recent
import com.example.weatherapp.network.model.LocationDetails
import java.util.*
import kotlin.math.roundToInt

private const val EXTRA_LOCATION: String = "location"
private const val EXTRA_IS_FAVOURITE: String = "is_favourite"

class FavouritesRecyclerAdapter(
    private val context: Context,
    private val favouritesDetailsList: ArrayList<LocationDetails>,
    private val fragment: MyCitiesFragment
) : RecyclerView.Adapter<FavouritesRecyclerAdapter.FavouriteViewHolder>() {

    var showReorder: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<LocationDetails>) {
        favouritesDetailsList.clear()
        favouritesDetailsList.addAll(list)
        notifyDataSetChanged()
    }

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FavouriteItemViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.favourite_item_view, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val location = favouritesDetailsList[position]

        holder.binding.favouriteCard.cityName.text = location.title
        holder.binding.favouriteCard.temperature.text =
            location.consolidated_weather[0].the_temp.roundToInt().toString()
        val imageResource =
            ImageLoader(location.consolidated_weather[0].weather_state_name).getImageId()
        holder.binding.favouriteCard.weatherIcon.setImageResource(imageResource)

        holder.binding.favouriteCard.firstDetail.text = location.getFormattedTime()
        holder.binding.favouriteCard.secondDetail.text = location.getGMT()

        holder.binding.favouriteCard.favouriteIcon.isSelected = true

        holder.binding.favouriteCard.favouriteIcon.setOnClickListener {
            fragment.removeFromFavourites(location.woeid)
            holder.binding.favouriteCard.favouriteIcon.isSelected = false
            favouritesDetailsList.removeAt(position)
            notifyItemRemoved(position)
            fragment.showRemovedFavouriteSnackbar(location.title)
        }

        if (showReorder) {
            holder.binding.reorderIcon.visibility = View.VISIBLE
        } else {
            holder.binding.reorderIcon.visibility = View.GONE
        }

        holder.binding.favouriteCard.root.setOnClickListener {
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
                // za extra favorita stavljamo true jer je to ovdje jedina opcija
                putExtra(EXTRA_IS_FAVOURITE, true)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return favouritesDetailsList.size
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(favouritesDetailsList, i, i + 1)
            }
        } else {
            for (i in toPosition until fromPosition) {
                Collections.swap(favouritesDetailsList, i, i + 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }
}