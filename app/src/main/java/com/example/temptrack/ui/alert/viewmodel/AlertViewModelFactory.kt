package com.example.temptrack.ui.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.temptrack.data.repositry.WeatherRepository

class AlertViewModelFactory(private var repository: WeatherRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java))
        {
            AlertViewModel(repository) as T
        }else{
            throw IllegalAccessException("View Model Class not found")
        }
    }
}