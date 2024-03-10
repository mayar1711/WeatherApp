package com.example.temptrack.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.temptrack.data.repositry.WeatherRepository
import com.example.temptrack.location.WeatherLocationManager

class MapsViewModelFactory
    (private val _loc: WeatherLocationManager,private val repository: WeatherRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
                MapsViewModel( _loc,repository) as T
            } else {
                throw java.lang.IllegalArgumentException("ViewModel Class not found")
            }
        }
    }
