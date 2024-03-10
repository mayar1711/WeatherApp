package com.example.temptrack.data.database

import com.example.temptrack.data.model.TempData

sealed class DataBaseResult {

    data class Success(val forecast: List<TempData>) : DataBaseResult()
    data class Error(val message: Throwable) : DataBaseResult()
    object Loading:DataBaseResult()

}