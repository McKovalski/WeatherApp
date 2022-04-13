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
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMyCitiesBinding
import com.example.weatherapp.viewmodels.MainViewModel
import kotlin.system.exitProcess

class MyCitiesFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentMyCitiesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCitiesBinding.inflate(inflater, container, false)
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
}