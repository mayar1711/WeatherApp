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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.temptrack.data.database.DatabaseClient
import com.example.temptrack.data.database.FavoriteLocalDataSourceImo
import com.example.temptrack.data.network.ApiWeatherData
import com.example.temptrack.data.network.RetrofitClient
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSourceImpl
import com.example.temptrack.data.repositry.WeatherRepositoryImpl
import com.example.temptrack.databinding.FragmentHomeBinding
import com.example.temptrack.datastore.SettingDataStorePreferences
import com.example.temptrack.location.obtainLocation
import com.example.temptrack.ui.home.viewmodel.HomeViewModel
import com.example.temptrack.ui.home.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch
import com.example.temptrack.R
import com.example.temptrack.util.ResultCallBack
import com.example.temptrack.util.convertToDailyWeather
import com.example.temptrack.util.convertToHourlyWeather


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    val My_LOCATION_PERMISSION_ID = 5005
    private lateinit var settingSharedPreferences: SettingDataStorePreferences
    lateinit var  location :String
    private lateinit var adapter:DailyWeatherAdapter
    private lateinit var todayAdapter: HourlyWeatherAdapter
    lateinit var unit:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerForWeek.layoutManager = LinearLayoutManager(requireContext())
        adapter = DailyWeatherAdapter { dailyWeather ->
        }
        binding.recyclerForWeek.adapter = adapter

        binding.recyclerForToday.layoutManager=LinearLayoutManager(requireContext())

        todayAdapter= HourlyWeatherAdapter {hourlyWeather ->

        }
        binding.recyclerForToday.adapter=todayAdapter

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = WeatherRepositoryImpl.getInstance(WeatherRemoteDataSourceImpl.getInstance(RetrofitClient.weatherApiService),
            FavoriteLocalDataSourceImo.getInstance(DatabaseClient.getInstance(requireContext()).favoriteDao()))
        val factory = HomeViewModelFactory(requireActivity().application,repository)
        settingSharedPreferences = SettingDataStorePreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewLifecycleOwner.lifecycleScope.launch {
            settingSharedPreferences.getTempPref().collect { tempPref ->
                  unit = when(tempPref){
                      SettingDataStorePreferences.CELSIUS-> "metric"
                      SettingDataStorePreferences.FAHRENHEIT-> "imperial"
                      SettingDataStorePreferences.KELVIN-> "standard"
                      else-> "metric "
                  }
                     settingSharedPreferences.getLangPreferences().collect { langPref ->
                    val language =when(langPref){
                        SettingDataStorePreferences.ENGLISH->"en"
                        SettingDataStorePreferences.ARABIC->"ar"
                        else->"en"
                    }
                    viewModel.fetchWeatherForecast(44.34, 10.99, unit, language)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weatherForecast.collect { weatherData ->
                when (weatherData) {
                    is ResultCallBack.Success -> {
                        val forecast = weatherData.data
                        Log.i("HomeFragment", "Weather forecast data: $forecast")
                        val dailyItem=weatherData.data.daily
                        val data= convertToDailyWeather(dailyItem)
                        adapter.submitList(data)
                        val hourlyItem=weatherData.data.hourly
                        val homeData= convertToHourlyWeather(hourlyItem)
                        todayAdapter.submitList(homeData)
                    }

                    is ResultCallBack.Error -> {
                        val errorMessage = weatherData.message
                        Log.i("HomeFragment", "Error fetching weather forecast: $errorMessage")
                    }

                    is ResultCallBack.Loading -> {
                    }

                }
            }
        }

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

            }
            .show()
    }

}
