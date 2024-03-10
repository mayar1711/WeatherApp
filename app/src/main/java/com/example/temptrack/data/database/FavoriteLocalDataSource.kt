package com.example.temptrack.data.database

import com.example.temptrack.data.model.TempData
import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {
    suspend fun insertFavorite(favorite:TempData)
    suspend fun deleteFavorite(favorite:TempData)
   fun getAllFavorite(): Flow<List<TempData>>
}