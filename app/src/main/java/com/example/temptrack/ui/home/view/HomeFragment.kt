package com.example.temptrack.ui.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.temptrack.data.network.ApiWeatherData
import com.example.temptrack.data.repositry.WeatherRepositoryImpl
import com.example.temptrack.data.network.RetrofitClient
import com.example.temptrack.databinding.FragmentHomeBinding
import com.example.temptrack.ui.home.viewmodel.HomeViewModel
import com.example.temptrack.ui.home.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

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
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        viewModel.fetchWeatherForecast(44.34, 10.99)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is zero-based, so add 1
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
    }

}
