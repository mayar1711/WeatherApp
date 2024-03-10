package com.example.temptrack.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.temptrack.data.model.TempData

@Database(entities = [TempData::class], version = 1)
abstract class FavoriteDataBase : RoomDatabase() {
    abstract fun favoriteDao():FavoriteDao
}
object DatabaseClient {
    private var instance:FavoriteDataBase? = null

    fun getInstance(context: Context): FavoriteDataBase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
               FavoriteDataBase::class.java,
                "ProductDB"
            ).build().also { instance = it }
        }
    }
}
