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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.adapters.LocationsRecyclerAdapter
import com.example.weatherapp.databinding.FragmentSearchBinding
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.models.Recent
import com.example.weatherapp.viewmodels.MainViewModel
import kotlin.system.exitProcess

class SearchFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSearchBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var searchAdapter: LocationsRecyclerAdapter
    private lateinit var recentAdapter: LocationsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        setRecentVisibility(true)

        binding.searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
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

        var favorites = ArrayList<Favourite>()
        mainViewModel.getFavourites(requireContext())
        mainViewModel.favoriteLocations.observe(viewLifecycleOwner, Observer {
            favorites = mainViewModel.favoriteLocations.value!!
        })

        // Postavimo Recent
        mainViewModel.getRecent(requireContext())
        mainViewModel.recentLocations.observe(viewLifecycleOwner, Observer { recentData ->
            mainViewModel.recentLocationDetails.observe(viewLifecycleOwner, Observer { recentDetails ->
                recentAdapter = LocationsRecyclerAdapter(
                    requireContext(),
                    recentData,
                    recentDetails,
                    favorites,
                    this
                )
                binding.recentRecyclerView.adapter = recentAdapter
                binding.recentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            })
        })

        mainViewModel.searchLocationsList.observe(viewLifecycleOwner, Observer { searchResponse ->
            if (searchResponse.isSuccessful) {
                setRecentVisibility(false)

                mainViewModel.searchLocationsDetailsList.observe(viewLifecycleOwner, Observer {
                    searchAdapter = LocationsRecyclerAdapter(
                        requireContext(),
                        searchResponse.body()!!,
                        it,
                        favorites,
                        this
                    )
                    binding.searchRecyclerView.adapter = searchAdapter
                    binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                })
            } else {
                AlertDialog.Builder(requireContext()).setTitle("Error")
                    .setMessage("Something went wrong")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_warning)
                    .show()
            }
        })

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
        // vracamo query koji je bio prije izlaska iz fragmenta
        binding.searchBar.setQuery(mainViewModel.queryText, false)
        binding.searchBar.clearFocus()
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

    fun removeFromFavourites(favourite: Favourite) {
        mainViewModel.removeFavourite(requireContext(), favourite)
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