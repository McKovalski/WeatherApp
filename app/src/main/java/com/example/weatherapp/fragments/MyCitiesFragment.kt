package com.example.weatherapp.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.adapters.FavouritesRecyclerAdapter
import com.example.weatherapp.databinding.FragmentMyCitiesBinding
import com.example.weatherapp.models.Favourite
import com.example.weatherapp.models.Recent
import com.example.weatherapp.network.model.LocationDetails
import com.example.weatherapp.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.system.exitProcess

class MyCitiesFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentMyCitiesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val favouritesAdapter by lazy {
        FavouritesRecyclerAdapter(
            requireContext(),
            arrayListOf(),
            this
        )
    }
    var favourites = ArrayList<LocationDetails>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCitiesBinding.inflate(inflater, container, false)
        var isEditing = false

        binding.favoritesRecyclerView.adapter = favouritesAdapter
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // postavimo item touch helper
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.favoritesRecyclerView)

        binding.iconEdit.setOnClickListener {
            if (favourites.isNotEmpty()) {
                // ako sad tek krecemo uredjivati
                if (!isEditing) {
                    isEditing = true
                    binding.iconEdit.setImageResource(R.drawable.ic_done)
                } else {
                    isEditing = false
                    binding.iconEdit.setImageResource(R.drawable.ic_edit)
                    // mijenjamo podatke u bazi
                    val favouritesDb = ArrayList<Favourite>()
                    favourites.forEachIndexed { index, element ->
                        favouritesDb.add(
                            Favourite(
                                element.woeid,
                                element.title,
                                element.location_type,
                                element.latt_long,
                                index + 1
                            )
                        )
                    }
                    mainViewModel.addAllFavourites(requireContext(), favouritesDb)
                }
                favouritesAdapter!!.apply { showReorder = !showReorder }
                favouritesAdapter!!.notifyDataSetChanged()
            }
        }

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

        mainViewModel.getFavourites(requireContext())
        mainViewModel.favoriteLocationDetails.observe(viewLifecycleOwner){ dets ->
            favourites.clear()
            favourites.addAll(dets)
            favouritesAdapter.updateList(favourites)
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

    fun removeFromFavourites(woeid: Int) {
        mainViewModel.removeFavourite(requireContext(), woeid)
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
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            Log.d("FROM POSITION:", fromPosition.toString())
            Log.d("TO POSITION:", toPosition.toString())
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(favourites, i, i+1)
                }
            } else {
                for (i in toPosition until fromPosition) {
                    Collections.swap(favourites, i, i+1)
                }
            }
            (binding.favoritesRecyclerView.adapter as FavouritesRecyclerAdapter).swapItems(
                viewHolder.adapterPosition,
                target.adapterPosition
            )
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // ne treba nam
        }
    }

    fun addToRecent(recent: Recent) {
        mainViewModel.addRecent(requireContext(), recent)
    }

    fun showRemovedFavouriteSnackbar(title: String) {
        Snackbar.make(
            requireView(),
            getString(R.string.removed_from_favourites, title),
            Snackbar.LENGTH_SHORT
        ).show()
    }
}