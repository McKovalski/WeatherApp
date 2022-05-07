package com.example.weatherapp.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.adapters.LocationsRecyclerAdapter
import com.example.weatherapp.databinding.FragmentSearchBinding
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.models.Recent
import com.example.weatherapp.network.model.LocationDetails
import com.example.weatherapp.viewmodels.MainViewModel
import kotlin.system.exitProcess

class SearchFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSearchBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val searchAdapter by lazy {
        LocationsRecyclerAdapter(
            requireContext(),
            arrayListOf(),
            arrayListOf(),
            this,
            null
        )
    }
    private val recentAdapter by lazy {
        LocationsRecyclerAdapter(
            requireContext(),
            arrayListOf(),
            arrayListOf(),
            this,
            null
        )
    }

    var favourites = ArrayList<LocationDetails>()
    var recents = ArrayList<LocationDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setRecentVisibility(true)

        binding.searchRecyclerView.adapter = searchAdapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recentRecyclerView.adapter = recentAdapter
        binding.recentRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    mainViewModel.queryText = query
                    mainViewModel.getLocationList(query.toString())
                } else {
                    mainViewModel.queryText = null
                    setRecentVisibility(true)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query.isNullOrEmpty()) {
                    mainViewModel.queryText = null
                    setRecentVisibility(true)
                }
                return false
            }
        })

        return binding.root
    }

    override fun onResume() {
        if (!isNetworkConnected()) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.no_internet_connection))
                .setMessage(getString(R.string.check_internet_connection))
                .setNegativeButton(android.R.string.ok) { _, _ ->
                    activity?.finish()
                    exitProcess(0)
                }
                .setIcon(R.drawable.ic_warning).show()
        }
        // vracamo query koji je bio prije izlaska iz fragmenta
        binding.searchBar.setQuery(mainViewModel.queryText, false)
        binding.searchBar.clearFocus()

        mainViewModel.getFavourites(requireContext())
        mainViewModel.favoriteLocationDetails.observe(viewLifecycleOwner) {
            favourites.clear()
            favourites.addAll(it)
            recentAdapter.updateFavourites(favourites)
            searchAdapter.updateFavourites(favourites)
        }

        // Postavimo Recent
        mainViewModel.getRecent(requireContext())
        mainViewModel.recentLocationDetails.observe(viewLifecycleOwner) { recentDetails ->
            recents.clear()
            recents.addAll(recentDetails)
            recentAdapter.updateLocations(recents)
        }

        mainViewModel.searchLocationsList.observe(viewLifecycleOwner) { searchResponse ->
            if (searchResponse.isSuccessful) {
                setRecentVisibility(false)

                mainViewModel.searchLocationsDetailsList.observe(viewLifecycleOwner) {
                    val searchLocations = ArrayList<LocationDetails>()
                    searchLocations.addAll(it)
                    searchAdapter.updateLocations(searchLocations)
                }
            } else {
                AlertDialog.Builder(requireContext()).setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.something_went_wrong))
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_warning)
                    .show()
            }
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

    fun addToFavourites(favourite: Favourite) {
        mainViewModel.addFavourite(requireContext(), favourite)
    }

    fun removeFromFavourites(woeid: Int) {
        mainViewModel.removeFavourite(requireContext(), woeid)
    }

    fun addToRecent(recent: Recent) {
        mainViewModel.addRecent(requireContext(), recent)
    }

    fun getLastFavouritePosition(): Int {
        mainViewModel.getLastFavouritePosition(requireContext())
        return mainViewModel.favouritesLastPosition.value ?: 0
    }

    private fun setRecentVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.searchRecyclerView.visibility = View.GONE
            binding.recentLabel.visibility = View.VISIBLE
            binding.recentRecyclerView.visibility = View.VISIBLE
        } else {
            binding.searchRecyclerView.visibility = View.VISIBLE
            binding.recentLabel.visibility = View.GONE
            binding.recentRecyclerView.visibility = View.GONE
        }
    }
}