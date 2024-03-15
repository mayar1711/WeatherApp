package com.example.temptrack.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.model.TempData


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FavoriteLocalDataSourceImoTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: FavoriteDataBase
    private lateinit var dataSource: FavoriteLocalDataSource

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            FavoriteDataBase::class.java
        ).allowMainThreadQueries()
            .build()

        dataSource = FavoriteLocalDataSourceImo.getInstance(database.favoriteDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertWeather_retrievesWeather() = runBlockingTest {
        //Given
         val tempData=TempData(
             minTemp = 20.0,
             maxTemp = 20.0,
             temp = 30.0,
             city ="cairo",
             icon = "01d",
             lang = 30.000,
             lat = 31.000
         )
         //when
        dataSource.insertFavorite(tempData)
        //then
        val result = dataSource.getAllFavorite().first()
        assertThat(result[0].city, `is`(tempData.city))
    }

    @Test
    fun deleteWeather_retrievesWeather() = runBlockingTest {
        //Given
        val tempData1=TempData(
            minTemp = 20.0,
            maxTemp = 20.0,
            temp = 30.0,
            city ="cairo",
            icon = "01d",
            lang = 30.000,
            lat = 31.000
        )
        val tempData2=TempData(
            minTemp = 20.0,
            maxTemp = 20.0,
            temp = 30.0,
            city ="cairo",
            icon = "01d",
            lang = 32.000,
            lat = 33.000
        )
        //when
        dataSource.insertFavorite(tempData1)
        dataSource.insertFavorite(tempData2)
        //then
       dataSource.deleteFavorite(tempData1)

        val result = dataSource.getAllFavorite().first()
        assertThat(result.size, `is`(1))
        assertThat(result[0].city, `is`(tempData2.city))
    }
    @Test
    fun insertAlert_retrievesWeather() = runBlockingTest {
        //Given
        val roomAlert=RoomAlert(
            1647294000000,
            1647380400000,
            1747320400000,
            "Country D",
            "Description C")
        //when
        dataSource.insertAlert(roomAlert)
        //then
        val result = dataSource.getAllAlerts().first()
        assertThat(result[0].time, `is`(roomAlert.time))
    }

    @Test
    fun deleteAlert_retrievesWeather() = runBlockingTest {
        //Given
        val roomAlert1=RoomAlert(
            1647294000000,
            1647380400000,
            1747320400000,
            "Country C",
            "Description A")

        val roomAlert2=RoomAlert(
            1647294000000,
            1647380400000,
            1847320400000,
            "Country C",
            "Description C")
        //when
        dataSource.insertAlert(roomAlert1)

        dataSource.insertAlert(roomAlert2)
        //then
        dataSource.deleteAlert(roomAlert1)

        val result = dataSource.getAllAlerts().first()
        assertThat(result.size, `is`(1))
        assertThat(result[0].time, `is`(roomAlert2.time))
    }

}
