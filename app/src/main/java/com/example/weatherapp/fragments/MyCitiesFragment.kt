package com.example.weatherapp.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.adapters.FavouritesRecyclerAdapter
import com.example.weatherapp.databinding.FragmentMyCitiesBinding
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.models.Recent
import com.example.weatherapp.network.model.LocationData
import com.example.weatherapp.network.model.LocationDetails
import com.example.weatherapp.viewmodels.MainViewModel
import java.util.*
import kotlin.system.exitProcess

class MyCitiesFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentMyCitiesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var favourites = ArrayList<LocationData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCitiesBinding.inflate(inflater, container, false)

        var favouritesAdapter: FavouritesRecyclerAdapter? = null
        var isEditing = false
        val details = ArrayList<LocationDetails>()

        mainViewModel.getFavourites(requireContext())
        mainViewModel.favoriteLocations.observe(viewLifecycleOwner, Observer { favs ->
            if (favs.isNotEmpty()) {
                favourites = mainViewModel.favoriteLocations.value!!
                mainViewModel.favoriteLocationDetails.observe(viewLifecycleOwner, Observer { dets ->
                    details.addAll(dets)
                    favouritesAdapter = FavouritesRecyclerAdapter(
                        requireContext(),
                        favourites,
                        details,
                        this
                    )
                    binding.favoritesRecyclerView.adapter = favouritesAdapter
                    binding.favoritesRecyclerView.layoutManager =
                        LinearLayoutManager(requireContext())
                })
            }
        })

        binding.iconEdit.setOnClickListener {
            if (favouritesAdapter != null) {
                // ako sad tek krecemo uredjivati
                if (!isEditing) {
                    isEditing = true
                    binding.iconEdit.setImageResource(R.drawable.ic_done)
                } else {
                    isEditing = false
                    binding.iconEdit.setImageResource(R.drawable.ic_edit)
                }
                // postavimo item touch helper
                val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                itemTouchHelper.attachToRecyclerView(binding.favoritesRecyclerView)
                favouritesAdapter!!.apply { showReorder = !showReorder }
                favouritesAdapter!!.notifyDataSetChanged()
            }
        }

        return binding.root
    }

    override fun onResume() {
        if (!isNetworkConnected()) {
            AlertDialog.Builder(requireContext()).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setNegativeButton(android.R.string.ok) { _, _ ->
                    activity?.finish()
                    exitProcess(0)
                }
                .setIcon(R.drawable.ic_warning).show()
        }
        super.onResume()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun removeFromFavourites(favourite: Favourite) {
        mainViewModel.removeFavourite(requireContext(), favourite)
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val flags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(flags, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            Collections.swap(favourites, viewHolder.adapterPosition, target.adapterPosition)
            binding.favoritesRecyclerView.adapter?.notifyItemMoved(
                viewHolder.adapterPosition,
                target.adapterPosition
            )
            /*(binding.favoritesRecyclerView.adapter as FavouritesRecyclerAdapter).swapItems(
                viewHolder.adapterPosition,
                target.adapterPosition,
                viewHolder
            )*/
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // ne treba nam
        }
    }

    fun addToRecent(recent: Recent) {
        mainViewModel.addRecent(requireContext(), recent)
    }
}