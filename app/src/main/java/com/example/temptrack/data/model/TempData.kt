package com.example.temptrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class TempData (
    @PrimaryKey(autoGenerate = true)
    var roomId:Long =0,
    val minTemp:Double,
    val maxTemp:Double,
    val temp:Double,
    val city:String,
    val icon:String,
    val lang:Double,
    val lat:Double
)