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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.temptrack.data.database.DatabaseClient
import com.example.temptrack.data.database.FavoriteLocalDataSourceImo
import com.example.temptrack.data.network.RetrofitClient
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSourceImpl
import com.example.temptrack.data.repositry.WeatherRepositoryImpl
import com.example.temptrack.databinding.FragmentHomeBinding
import com.example.temptrack.datastore.ENUM_LOCATION
import com.example.temptrack.datastore.ENUM_TEMP_PREF
import com.example.temptrack.datastore.SettingDataStorePreferences
import com.example.temptrack.location.obtainLocation
import com.example.temptrack.ui.home.viewmodel.HomeViewModel
import com.example.temptrack.ui.home.viewmodel.HomeViewModelFactory
import com.example.temptrack.util.ResultCallBack
import com.example.temptrack.util.convertToDailyWeather
import com.example.temptrack.util.convertToHourlyWeather
import com.example.temptrack.util.getImageIcon
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var settingSharedPreferences: SettingDataStorePreferences
    private lateinit var adapter: DailyWeatherAdapter
    private lateinit var todayAdapter: HourlyWeatherAdapter
    private  var unit: String="metric"
    private var language:String="en"
    private var _latitude: Double = 0.0
    private var _longitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerForWeek.layoutManager = LinearLayoutManager(requireContext())
        adapter = DailyWeatherAdapter { dailyWeather ->
        }
        binding.recyclerForWeek.adapter = adapter

        todayAdapter = HourlyWeatherAdapter { hourlyWeather ->

        }
        binding.recyclerForToday.adapter = todayAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = WeatherRepositoryImpl.getInstance(
            WeatherRemoteDataSourceImpl.getInstance(RetrofitClient.weatherApiService),
            FavoriteLocalDataSourceImo.getInstance(DatabaseClient.getInstance(requireContext()).favoriteDao())
        )
        val factory = HomeViewModelFactory(requireActivity().application, repository)
        settingSharedPreferences = SettingDataStorePreferences.getInstance(requireContext())

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val tempPref = settingSharedPreferences.getTempPref().first()
                val langPref = settingSharedPreferences.getLangPreferences().first()
                val locationPref = settingSharedPreferences.getLocationRadioGroupPreference().first()

                unit = when (tempPref) {
                    ENUM_TEMP_PREF.CELSIUS -> "metric"
                    ENUM_TEMP_PREF.FAHRENHEIT -> "imperial"
                    ENUM_TEMP_PREF.KELVIN -> "standard"
                    else -> "metric "
                }
                Log.d("HomeFragment", "onViewCreated: $unit")
                language = when (langPref) {
                    SettingDataStorePreferences.ENGLISH -> "en"
                    SettingDataStorePreferences.ARABIC -> "ar"
                    else -> "en"
                }
                Log.d("HomeFragment", "onViewCreated: $language")

                when (locationPref) {
                    ENUM_LOCATION.MAP -> {
                        Log.d("HomeFragment", "onViewCreated: MAP")
                    }
                    ENUM_LOCATION.GPS -> {
                        viewModel.latitude.collect { latitude ->
                            _latitude = latitude
                            Log.d("HomeFragment", "Latitude: $_latitude")

                            viewModel.longitude.collect { longitude ->
                                _longitude = longitude
                                Log.d("HomeFragment", "Longitude: $_longitude")

                                viewModel.fetchWeatherForecast(_latitude, _longitude, unit, language)
                            }
                        }
                    }
                    else -> {
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error fetching preferences: ${e.message}")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weatherForecast.collect { weatherData ->
                when (weatherData) {
                    is ResultCallBack.Success -> {
                        val forecast = weatherData.data
                        Log.i("HomeFragment", "Weather forecast data: $forecast")
                        val dailyItem = weatherData.data.daily
                        val data = convertToDailyWeather(dailyItem)
                        adapter.submitList(data)
                        val hourlyItem = weatherData.data.hourly
                        val homeData = convertToHourlyWeather(hourlyItem)
                        todayAdapter.submitList(homeData)
                        dailyItem.get(0).weather.get(0).icon
                        val icon = getImageIcon(dailyItem.get(0).weather.get(0).icon)
                        binding.iconforNow.setImageResource(icon)
                        binding.tvCity.text=weatherData.data.timezone
                        binding.tvTemp.text = weatherData.data.current.temp.toString()
                        binding.tvDescription.text = dailyItem.get(0).weather.get(0).description
                        binding.pressureMeasure.text= buildString {
                            append(weatherData.data.current.pressure.toString())
                            append(" pascal")
                        }
                        binding.cloudMeasure.text=weatherData.data.current.clouds.toString()
                        binding.humidityMeasure.text= buildString {
                            append(weatherData.data.current.humidity.toString())
                            append(" %")
                        }
                        binding.windMeasure.text= buildString {
                            append(weatherData.data.current.windSpeed.toString())
                            append(" ")
                            append(checkUnit(unit))
                        }
                        binding.visibilityMeasure.text=weatherData.data.current.visibility.toString()
                        binding.ultraVioMeasure.text=weatherData.data.current.uvi.toString()
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
    companion object{
        private const val My_LOCATION_PERMISSION_ID = 123
    }
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelCoroutines()
    }
    fun checkUnit(unit:String):String{
        return when(unit){
             "metric"->"m/s"
             "imperial"->"mm/h"
            "standard"->"m/s"
            else -> "m/s"
        }
    }
}

