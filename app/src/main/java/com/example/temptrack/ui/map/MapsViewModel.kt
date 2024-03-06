package com.example.temptrack.ui.map

import androidx.lifecycle.ViewModel
import com.example.temptrack.location.WeatherLocationManager

class MapsViewModel(  private val _loc: WeatherLocationManager) :ViewModel(){
    val location = _loc.location

    fun requestLocationByGPS() {
        _loc.requestLocationByGPS()
    }


}