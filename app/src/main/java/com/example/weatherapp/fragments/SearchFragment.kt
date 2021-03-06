package com.example.weatherapp.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.adapters.LocationsRecyclerAdapter
import com.example.weatherapp.databinding.FragmentSearchBinding
import com.example.weatherapp.helpers.NetworkHelper
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
                val imm = requireContext()
                    .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchBar.applicationWindowToken, 0)
                if (!query.isNullOrEmpty()) {
                    mainViewModel.queryText = query
                    mainViewModel.getLocationList(query.toString())
                } else {
                    mainViewModel.queryText = ""
                    setRecentVisibility(true)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                mainViewModel.queryText = query
                if (query.isNullOrEmpty()) {
                    setRecentVisibility(true)
                }
                return true
            }
        })

        return binding.root
    }

    override fun onResume() {
        if (!NetworkHelper().isNetworkConnected(activity)) {
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
                mainViewModel.searchLocationsDetailsList.observe(viewLifecycleOwner) {
                    val searchLocations = ArrayList<LocationDetails>()
                    searchLocations.addAll(it)
                    searchAdapter.updateLocations(searchLocations)
                }
                if (!mainViewModel.queryText.isNullOrEmpty()) {
                    setRecentVisibility(false)
                } else {
                    setRecentVisibility(true)
                }
            } else {
                AlertDialog.Builder(requireContext()).setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.something_went_wrong))
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_warning)
                    .show()
            }
        }

        mainViewModel.currentLocation.observe(viewLifecycleOwner) {
            val currentLocation = it
            recentAdapter.updateCurrentLocation(currentLocation)
            searchAdapter.updateCurrentLocation(currentLocation)
        }

        super.onResume()
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