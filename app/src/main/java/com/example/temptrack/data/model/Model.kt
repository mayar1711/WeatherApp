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
    val lat: Double
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
    val weatherDescription: String
)

fun convertToDailyWeather(dailyItems: List<DailyItem>): List<DailyWeather> {
    val dailyWeatherList = mutableListOf<DailyWeather>()

    for (dailyItem in dailyItems) {
        val dayOfWeek = getDayOfWeek(dailyItem.dt)
        val date = getDate(dailyItem.dt)
        val maxTemperature = dailyItem.temp.max
        val minTemperature = dailyItem.temp.min
        val weatherDescription = dailyItem.weather.firstOrNull()?.description ?: ""

        val dailyWeather = DailyWeather(dayOfWeek, date, maxTemperature, minTemperature, weatherDescription)
        dailyWeatherList.add(dailyWeather)
    }

    return dailyWeatherList
}

private fun getDayOfWeek(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp * 1000
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
    return dayOfWeek ?: ""
}

private fun getDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = Date(timestamp * 1000)
    return dateFormat.format(date)
}
data class HourlyWeather(
    val hour: Int,
    val amPm: String,
    val temperature: Double
)

fun convertToHourlyWeather(hourlyWeather: List<HourlyItem>): List<HourlyWeather> {
    val hourlyWeatherList = mutableListOf<HourlyWeather>()
    val calendar = Calendar.getInstance()

    for (hourlyItem in hourlyWeather) {
        calendar.timeInMillis = hourlyItem.dt * 1000
        val hour = calendar.get(Calendar.HOUR)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        val temperature = hourlyItem.temp
        hourlyWeatherList.add(HourlyWeather(hour, amPm, temperature))
    }

    return hourlyWeatherList
}