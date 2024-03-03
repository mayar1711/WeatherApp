package com.example.temptrack.ui.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.temptrack.data.repositry.WeatherRepositoryImpl
import com.example.temptrack.data.network.RetrofitClient
import com.example.temptrack.databinding.FragmentHomeBinding
import com.example.temptrack.ui.home.viewmodel.HomeViewModel
import com.example.temptrack.ui.home.viewmodel.HomeViewModelFactory

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
        viewModel.weatherForecast.observe(viewLifecycleOwner) { weatherForecast ->
            Log.i("HomeFragment", "Weather forecast data: $weatherForecast")
        }
       val data =viewModel.fetchWeatherForecast(44.34, 10.99)
    }
}
