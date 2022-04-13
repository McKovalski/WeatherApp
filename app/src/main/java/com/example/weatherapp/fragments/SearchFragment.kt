package com.example.weatherapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.adapters.LocationsRecyclerAdapter
import com.example.weatherapp.databinding.FragmentSearchBinding
import com.example.weatherapp.viewmodels.MainViewModel

class SearchFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSearchBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: LocationsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mainViewModel.locationsList.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                adapter = LocationsRecyclerAdapter(requireContext(), it.body()!!)
                binding.recyclerView.adapter = adapter
            } else {
                AlertDialog.Builder(requireContext()).setTitle("Error")
                    .setMessage("Something went wrong")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_warning)
                    .show()
            }

        })
        // privremena funkcija kojom punimo recycler view
        //mainViewModel.getLocationList("san") // TODO("izbrisati")

        return binding.root
    }
}