package com.example.temptrack.ui.home.view

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.temptrack.data.network.ApiWeatherData
import com.example.temptrack.data.repositry.WeatherRepositoryImpl
import com.example.temptrack.data.network.RetrofitClient
import com.example.temptrack.databinding.FragmentHomeBinding
import com.example.temptrack.datastore.SettingDataStorePreferences
import com.example.temptrack.location.obtainLocation
import com.example.temptrack.ui.home.viewmodel.HomeViewModel
import com.example.temptrack.ui.home.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    val My_LOCATION_PERMISSION_ID = 5005
    private lateinit var settingSharedPreferences: SettingDataStorePreferences
    lateinit var  location :String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = WeatherRepositoryImpl(RetrofitClient.weatherApiService)
        val factory = HomeViewModelFactory(requireActivity().application,repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        viewModel.fetchWeatherForecast(44.34, 10.99,"metric","ar")
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        Log.i("CurrentDate", "Year: $year, Month: $month, Day: $dayOfMonth")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weatherForecast.collect { weatherData ->
                when (weatherData) {
                    is ApiWeatherData.Success -> {
                        val forecast = weatherData.forecast
                        Log.i("HomeFragment", "Weather forecast data: $forecast")
                        Log.i("HomeFragment", "Weather forecast data:\n${forecast.list[30]}")
/*
                        val data=weatherData.forecast.list
                        viewModel.getWeatherDataForCurrentDate(data)
                        Log.i("HomeFragment", " data: $data")
*/

                        // Update UI with forecast data
                    }

                    is ApiWeatherData.Error -> {
                        val errorMessage = weatherData.message
                        Log.i("HomeFragment", "Error fetching weather forecast: $errorMessage")
                        // Show error message in UI
                    }

                    is ApiWeatherData.Loading -> {
                        // Show loading indicator
                    }
                }
            }
        }
        settingSharedPreferences = SettingDataStorePreferences.getInstance(requireContext())

        if (checkPermission()) {
            if (isLocationEnabled()) {
                viewModel.requestGPSLocation()
                obtainLocation(requireContext(), settingSharedPreferences)

            } else {
                showEnableLocationDialog()
            }
        } else {
            requestPermission()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            settingSharedPreferences.getLocationPref().collect { location ->
                // Handle the retrieved location
                Log.i("HomeFragment", "Retrieved location: $location")
                 var data=location

            }
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManger: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManger.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            My_LOCATION_PERMISSION_ID
        )
    }



    private fun checkPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED)
                ||
                (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                        PackageManager.PERMISSION_GRANTED)
    }
    private fun showEnableLocationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Location Services Disabled")
            .setMessage("Please enable location services to use this app.")
            .setPositiveButton("Enable") { dialog, which ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Handle cancel button click
            }
            .show()
    }

}
