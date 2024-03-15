package com.example.temptrack.data.model

import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar


data class WeatherForecastResponse(
    val current: Current,
    val timezone: String,
    val timezoneOffset: Long,
    val daily: List<DailyItem>,
    val lon: Double,
    val hourly: List<HourlyItem>,
    val lat: Double,
    val alerts: List<Alert>?

)

data class Current(
    val sunrise: Long,
    val temp: Double,
    val visibility: Int,
    val uvi: Double,
    val pressure: Int,
    val clouds: Int,
    val feelsLike: Double,
    val dt: Long,
    val windDeg: Int,
    val dewPoint: Double,
    val sunset: Long,
    val weather: List<WeatherItem>,
    val humidity: Int,
    val windSpeed: Double
)

data class FeelsLike(
    val eve: Double,
    val night: Double,
    val day: Double,
    val morn: Double
)

data class HourlyItem(
    val temp: Double,
    val visibility: Int,
    val uvi: Double,
    val pressure: Int,
    val clouds: Int,
    val feelsLike: Double,
    val windGust: Double,
    val dt: Long,
    val pop: Double,
    val windDeg: Int,
    val dewPoint: Double,
    val weather: List<WeatherItem>,
    val humidity: Int,
    val windSpeed: Double
)

data class DailyItem(
    val moonset: Long,
    val summary: String,
    val sunrise: Long,
    val temp: Temp,
    val moonPhase: Double,
    val uvi: Double,
    val moonrise: Long,
    val pressure: Int,
    val clouds: Int,
    val feelsLike: FeelsLike,
    val windGust: Double,
    val dt: Long,
    val pop: Double,
    val windDeg: Int,
    val dewPoint: Double,
    val sunset: Long,
    val weather: List<WeatherItem>,
    val humidity: Int,
    val windSpeed: Double
)

data class WeatherItem(
    val icon: String,
    val description: String,
    val main: String,
    val id: Int
)

data class Temp(
    val min: Double,
    val max: Double,
    val eve: Double,
    val night: Double,
    val day: Double,
    val morn: Double
)
data class DailyWeather(
    val dayOfWeek: String,
    val date: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val weatherDescription: String,
    val weather: List<WeatherItem>
    )
data class Alert(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)
data class HourlyWeather(
    val hour: Int,
    val amPm: String,
    val temperature: Double,
    val weather: List<WeatherItem>
    )
