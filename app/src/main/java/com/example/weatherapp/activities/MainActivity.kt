package com.example.weatherapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.fragments.MyCitiesFragment
import com.example.weatherapp.fragments.SearchFragment
import com.example.weatherapp.models.CurrentLocation
import com.example.weatherapp.viewmodels.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // moramo pri ulasku u aplikaciju pozvati ovu funkciju
        // inace ce biti 0 pri prvom pozivu kasnije
        mainViewModel.getLastFavouritePosition(this)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search -> setCurrentFragment(SearchFragment())
                R.id.my_cities -> setCurrentFragment(MyCitiesFragment())

            }
            true
        }

        if (!isNetworkConnected()) {
            AlertDialog.Builder(this).setTitle(getString(R.string.no_internet_connection))
                .setMessage(getString(R.string.check_internet_connection))
                .setNegativeButton(android.R.string.ok) { _, _ ->
                    finish()
                    exitProcess(0)
                }
                .setIcon(R.drawable.ic_warning).show()
        } else {
            setCurrentFragment(SearchFragment())
        }

        // pri pokretanju dodajemo trenutnu lokaciju u bazu
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        /*if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }*/
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                mainViewModel.setCurrentLocation(
                    this,
                    CurrentLocation(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                )
            }
        }
    }

    override fun onResume() {
        if (!isNetworkConnected()) {
            AlertDialog.Builder(this).setTitle(getString(R.string.no_internet_connection))
                .setMessage(getString(R.string.check_internet_connection))
                .setNegativeButton(android.R.string.ok) { _, _ ->
                    finish()
                    exitProcess(0)
                }
                .setIcon(R.drawable.ic_warning).show()
        }
        super.onResume()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}