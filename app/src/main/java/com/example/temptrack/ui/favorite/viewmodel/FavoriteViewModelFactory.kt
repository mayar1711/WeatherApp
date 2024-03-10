package com.example.temptrack.ui.favorite.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.temptrack.data.repositry.WeatherRepository

class FavoriteViewModelFactory(private val application: Application,private val repository: WeatherRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            return FavoriteViewModel(application,repository)as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}