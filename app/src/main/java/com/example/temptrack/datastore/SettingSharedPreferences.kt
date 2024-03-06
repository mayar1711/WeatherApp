package com.example.temptrack.datastore

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private const val SETTING_SHARED_PREFERENCES = "SETTING_SHARED_PREFERENCES"
private const val LOCATION_PREFERENCES = "LOCATION_PREFERENCES"
private const val TEMP_PREFERENCES = "TEMP_PREFERENCES"
private const val WIND_SPEED_PREFERENCES = "WIND_SPEED_PREFERENCES"
private const val MAP_LAT_PREFERENCES = "MAP_LAT_PREFERENCES"
private const val MAP_LONG_PREFERENCES = "MAP_LONG_PREFERENCES"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTING_SHARED_PREFERENCES)

class SettingSharedPreferences(private val context: Context) {

    private val locationPrefKey = stringPreferencesKey(LOCATION_PREFERENCES)
    private val tempPrefKey = stringPreferencesKey(TEMP_PREFERENCES)
    private val windSpeedPrefKey = stringPreferencesKey(WIND_SPEED_PREFERENCES)
    private val mapLatPrefKey = floatPreferencesKey(MAP_LAT_PREFERENCES)
    private val mapLongPrefKey = floatPreferencesKey(MAP_LONG_PREFERENCES)


    suspend fun setLocationPref(locationPref: String) {
        context.dataStore.edit { preferences ->
            preferences[locationPrefKey] = locationPref
        }
    }

    fun getLocationPref(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[locationPrefKey]
        }
    }

    suspend fun setTempPref(tempPref: String) {
        context.dataStore.edit { preferences ->
            preferences[tempPrefKey] = tempPref
        }
    }

    fun getTempPref(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[tempPrefKey]
        }
    }

    suspend fun setWindSpeedPref(windSpeedPref: String) {
        context.dataStore.edit { preferences ->
            preferences[windSpeedPrefKey] = windSpeedPref
        }
    }

    fun getWindSpeedPref(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[windSpeedPrefKey]
        }
    }

    suspend fun setMapPref(lat: Float, long: Float) {
        context.dataStore.edit { preferences ->
            preferences[mapLatPrefKey] = lat
            preferences[mapLongPrefKey] = long
        }
    }

    fun getMapPref(): Flow<Pair<Float, Float>?> {
        return context.dataStore.data.map { preferences ->
            val lat = preferences[mapLatPrefKey]
            val long = preferences[mapLongPrefKey]
            if (lat != null && long != null) {
                Pair(lat, long)
            } else {
                null
            }
        }
    }



    companion object {
        const val GPS = "GPS"
        const val MAP = "MAP"
        const val METER_PER_SECOND = "METER_PER_SECOND"
        const val MILE_PER_HOUR = "MILE_PER_HOUR"
        const val CELSIUS = "CELSIUS"
        const val KELVIN = "KELVIN"
        const val FAHRENHEIT = "FAHRENHEIT"
        const val SET_LOCATION_AS_MAIN_LOCATION = "SET_LOCATION_AS_MAIN_LOCATION"
        const val ADD_T0_FAV_IN_THIS_LOCATION = "ADD_T0_FAV_IN_THIS_LOCATION"
        const val ADD_T0_ALERTS_IN_THIS_LOCATION = "ADD_T0_ALERTS_IN_THIS_LOCATION"
        const val NAVIGATE_TO_MAP = "NAVIGATE_TO_MAP"

        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: SettingSharedPreferences

        fun getInstance(context: Context): SettingSharedPreferences {
            if (!::instance.isInitialized) {
                instance = SettingSharedPreferences(context.applicationContext)
            }
            return instance
        }
    }
}
