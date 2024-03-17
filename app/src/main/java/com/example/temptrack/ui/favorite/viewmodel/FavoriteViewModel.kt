package com.example.temptrack.ui.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temptrack.data.model.TempData
import com.example.temptrack.data.repositry.WeatherRepository
import com.example.temptrack.util.ResultCallBack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _favoriteList = MutableStateFlow<ResultCallBack<List<TempData>>>(ResultCallBack.Loading)
    val favoriteList = _favoriteList.asStateFlow()

    fun getFavoriteList() = viewModelScope.launch {
        repository.getAllFavorite()
            .catch { e ->
                _favoriteList.value = ResultCallBack.Error(e)
            }
            .collect { list ->

                _favoriteList.value = ResultCallBack.Success(list)
            }
    }

    fun deleteFavorite(tempData: TempData) {
        viewModelScope.launch {
            repository.deleteFavorite(tempData)
            getFavoriteList()
        }
    }
    fun updateAllFavorite1(tempData: TempData){
        viewModelScope.launch {
            repository.updateFavorite(tempData)
        }
    }
}