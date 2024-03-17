package com.example.temptrack.ui.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.temptrack.MainRule
import com.example.temptrack.data.model.Current
import com.example.temptrack.data.model.TempData
import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.model.WeatherItem
import com.example.temptrack.data.network.datasource.FakeWeatherRepository
import com.example.temptrack.data.repositry.WeatherRepository
import com.example.temptrack.util.ResultCallBack
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class FavoriteViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()


   var fav1= TempData(minTemp = 20.0, maxTemp = 20.0, temp = 30.0, city ="cairo", icon = "01d", lang = 30.000, lat = 31.000)
   var fav2= TempData(minTemp = 18.0, maxTemp = 23.0, temp = 20.0, city = "City2",icon = "01d", lang = 30.000, lat = 31.000)
    val currentWeather = Current(
        sunrise = 1618076746,
        temp = 22.5,
        visibility = 10000,
        uvi = 3.5,
        pressure = 1010,
        clouds = 20,
        feelsLike = 23.5,
        dt = 1618100000,
        windDeg = 180,
        dewPoint = 15.0,
        sunset = 1618110000,
        weather = listOf(WeatherItem("01d", "Clear", "Clear sky", 800)),
        humidity = 50,
        windSpeed = 3.5
    )
   val response=WeatherForecastResponse(
       current = currentWeather,
       timezone = "America/New_York",
       timezoneOffset = -14400,
       daily = emptyList(),
       lon = -74.006,
       hourly = emptyList(),
       lat = 40.7128,
       alerts = emptyList()
   )

    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: FavoriteViewModel
    @Before
    fun setup() {
        repository = FakeWeatherRepository(response)
        viewModel = FavoriteViewModel(repository)
    }

    @Test
    fun `test getFavoriteList`() = runBlockingTest {
        val fakeFavorite1 =fav1
        val fakeFavorite2 = fav2
        repository.insertFavorite(fakeFavorite1)
        repository.insertFavorite(fakeFavorite2)

        viewModel.getFavoriteList()

        assertThat(viewModel.favoriteList.first(), `is`(equalTo(ResultCallBack.Success(listOf(fakeFavorite1, fakeFavorite2)))))
    }
    @Test
    fun `test deleteFavorite`() = runBlockingTest {
        val fakeFavorite = TempData(minTemp = 20.0, maxTemp = 20.0, temp = 30.0, city = "cairo", icon = "01d", lang = 30.000, lat = 31.000)
       repository.insertFavorite(fakeFavorite)

        viewModel.deleteFavorite(fakeFavorite)

        assertThat(repository.getAllFavorite().first(), `is`(emptyList()))
    }
}