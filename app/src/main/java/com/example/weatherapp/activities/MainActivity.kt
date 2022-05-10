package com.example.weatherapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.fragments.MyCitiesFragment
import com.example.weatherapp.fragments.SearchFragment
import com.example.weatherapp.fragments.SettingsFragment
import com.example.weatherapp.helpers.LanguageHelper
import com.example.weatherapp.helpers.NetworkHelper
import com.example.weatherapp.models.CurrentLocation
import com.example.weatherapp.viewmodels.MainViewModel
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private val REQUEST_LOCATION: Int = 1

    // The minimum distance to change Updates in meters
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 0f

    // The minimum time between updates in milliseconds
    private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 30 minutes

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // postavimo jezik
        LanguageHelper(this).loadLocale()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // moramo pri ulasku u aplikaciju pozvati ovu funkciju
        // inace ce biti 0 pri prvom pozivu kasnije
        mainViewModel.getLastFavouritePosition(this)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search -> setCurrentFragment(SearchFragment())
                R.id.my_cities -> setCurrentFragment(MyCitiesFragment())
                R.id.settings -> setCurrentFragment(SettingsFragment())
            }
            true
        }

        if (!NetworkHelper().isNetworkConnected(this)) {
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

        // provjera dozvola aplikacije
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION
            )
        }

        // pri pokretanju dodajemo trenutnu lokaciju u bazu
        /*val locationListener = LocationListener { location ->
            mainViewModel.setCurrentLocation(this,
                CurrentLocation(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
        }
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val gpsEnabled: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled: Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.d("GPS_enabled", gpsEnabled.toString())
        if (gpsEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener
            )
        } else if (networkEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener
            )
        }*/

        // hardkodirana lokacija Zagreba TODO
        mainViewModel.setCurrentLocation(
            this,
            CurrentLocation(
                latitude = 45.807259,
                longitude = 15.967600
            )
        )
    }

    override fun onResume() {
        if (!NetworkHelper().isNetworkConnected(this)) {
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
}