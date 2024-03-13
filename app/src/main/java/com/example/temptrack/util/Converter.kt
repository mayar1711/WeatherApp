package com.example.temptrack.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.temptrack.R
import com.example.temptrack.data.model.DailyItem
import com.example.temptrack.data.model.DailyWeather
import com.example.temptrack.data.model.HourlyItem
import com.example.temptrack.data.model.HourlyWeather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun getDayFormat(dt: Long, lang: String): String? {
    val date = Date(dt * 1000)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale(lang))
}
fun getAddress(context: Context, lat: Double, lon: Double):String{
    try {
        val address: MutableList<Address>?
        val geocoder = Geocoder(context)
        address = geocoder.getFromLocation(lat, lon, 1)
        return if (address?.isEmpty() == true) {
            "Unkown location"
        } else if (address?.get(0)?.countryName.isNullOrEmpty()) {
            "Unkown Country"
        } else if (address?.get(0)?.adminArea.isNullOrEmpty()) {
            address?.get(0)?.countryName.toString()
        } else {
            address?.get(0)?.countryName.toString() + ", " + address?.get(0)?.adminArea + ", " + address?.get(
                0
            )?.locality
        }
    }catch (e: IOException) {
        e.printStackTrace()
        return "Error fetching address"
    }
}
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

fun getTimeHourlyFormat(dt: Long,lang: String): String {
    val date = Date(dt * 1000)
    val format = SimpleDateFormat("h:mm a", Locale(lang))
    return format.format(date)
}

fun getImageIcon(icon: String): Int {
    val iconValue: Int
    when (icon) {
        "01d" -> iconValue = R.drawable.ic_clear_sky_morning
        "01n" -> iconValue = R.drawable.clear_sky
        "02d" -> iconValue = R.drawable.ic_few_cloud_morning
        "02n" -> iconValue = R.drawable.ic_few_cloud_night
        "03n" -> iconValue = R.drawable.ic_scattered_clouds
        "03d" -> iconValue = R.drawable.ic_scattered_clouds
        "04d" -> iconValue = R.drawable.ic_broken_cloud
        "04n" -> iconValue = R.drawable.ic_broken_cloud
        "09d" -> iconValue = R.drawable.ic_shower_raint
        "09n" -> iconValue = R.drawable.ic_shower_raint
        "10d" -> iconValue = R.drawable.ic_rain
        "10n" -> iconValue = R.drawable.ic_rain
        "11d" -> iconValue = R.drawable.ic_thunderstorm
        "11n" -> iconValue = R.drawable.ic_thunderstorm
        "13d" -> iconValue = R.drawable.ic_snow
        "13n" -> iconValue = R.drawable.ic_snow
        "50d" -> iconValue = R.drawable.ic_mist
        "50n" -> iconValue = R.drawable.ic_mist
        else -> iconValue = R.drawable.icon
    }
    return iconValue
}

fun getDateToAlert(timestamp: Long, language: String): String{
    return SimpleDateFormat("dd MMM, yyyy",Locale(language)).format(timestamp)
}
fun getTimeToAlert(timestamp: Long, language: String): String{
    return SimpleDateFormat("h:mm a",Locale(language)).format(timestamp)
}
fun convertDateToLong(date:String): Long {
    val format=SimpleDateFormat("dd MMM, yyyy")
   return format.parse(date).time
}
fun convertTimeToLong(time:String):Long{
    val format = SimpleDateFormat("hh:mm a")
    return format.parse(time).time
}
