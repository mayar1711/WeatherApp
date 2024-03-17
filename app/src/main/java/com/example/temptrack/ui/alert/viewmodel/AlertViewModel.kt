package com.example.temptrack.ui.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.repositry.WeatherRepository
import com.example.temptrack.util.ResultCallBack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel (private var repository: WeatherRepository): ViewModel() {
    var alertResponse = MutableStateFlow<ResultCallBack<List<RoomAlert>>>(ResultCallBack.Loading)
    init {
        getAllAlert()
    }
    fun getAllAlert()=viewModelScope.launch {
        repository.getAllAlerts()
            .catch { e->
            alertResponse.value=ResultCallBack.Error(e)
           }
            .collect{result->
                alertResponse.value=ResultCallBack.Success(result)
            }
    }

    fun insertAlert(alert:RoomAlert){
        viewModelScope.launch {
            repository.insertAlert(alert)
        }
    }
    fun deleteAlert(alert: RoomAlert){
        viewModelScope.launch{
            repository.deleteAlert(alert)
        }
    }
}