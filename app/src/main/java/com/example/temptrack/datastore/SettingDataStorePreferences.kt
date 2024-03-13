package com.example.temptrack.datastore

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

private const val SETTING_SHARED_PREFERENCES = "SETTING_SHARED_PREFERENCES"
private const val LOCATION_PREFERENCES = "LOCATION_PREFERENCES"
private const val TEMP_PREFERENCES = "TEMP_PREFERENCES"
private const val WIND_SPEED_PREFERENCES = "WIND_SPEED_PREFERENCES"
private const val MAP_LAT_PREFERENCES = "MAP_LAT_PREFERENCES"
private const val MAP_LONG_PREFERENCES = "MAP_LONG_PREFERENCES"
private const val NOTIFICATION_PREFERENCES="NOTIFICATION_PREFERENCES"
private const val LANGUAGE_PREFERENCES="LANGUAGE_PREFERENCES"
private const val ALERT_TYPE_PREFERENCES = "ALERT_TYPE_PREFERENCES"
private const val COUNTRY_NAME_PREFERENCES = "COUNTRY_NAME_PREFERENCES"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTING_SHARED_PREFERENCES)

@Suppress("UNREACHABLE_CODE")
class SettingDataStorePreferences(private val context: Context) {

    private val locationPrefKey = stringPreferencesKey(LOCATION_PREFERENCES)
    private val tempPrefKey = stringPreferencesKey(TEMP_PREFERENCES)
    private val windSpeedPrefKey = stringPreferencesKey(WIND_SPEED_PREFERENCES)
    private val mapLatPrefKey = doublePreferencesKey(MAP_LAT_PREFERENCES)
    private val mapLongPrefKey = doublePreferencesKey(MAP_LONG_PREFERENCES)
    private val notificationPrefKey = booleanPreferencesKey(NOTIFICATION_PREFERENCES)
    private val languagePrefKey= stringPreferencesKey(LANGUAGE_PREFERENCES)

    private val _locationPrefFlow = MutableStateFlow("")
    private val _tempPrefFlow = MutableStateFlow("")
    private val _languagePrefFlow = MutableStateFlow("")
    private val alertTypePrefKey = stringPreferencesKey(ALERT_TYPE_PREFERENCES)
    private val countryNamePrefKey = stringPreferencesKey(COUNTRY_NAME_PREFERENCES)
    suspend fun setNotificationPref(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[notificationPrefKey] = isEnabled
        }
    }

    fun getNotificationPref(): Flow<Boolean?> {
        return context.dataStore.data.map { preferences ->
            preferences[notificationPrefKey]
        }
    }

    suspend fun setLocationRadioGroupPreference(selectedLocation: String) {
        context.dataStore.edit { preferences ->
            preferences[locationPrefKey] = selectedLocation
            _locationPrefFlow.value = selectedLocation
        }
    }

    fun getLocationRadioGroupPreference(): Flow<String> {
        return _locationPrefFlow
    }

    suspend fun setLangPreference(selectLanguage: String){
        context.dataStore.edit {
                preferences ->
            preferences[languagePrefKey]=selectLanguage
            _languagePrefFlow.value = selectLanguage
        }
    }
    fun getLangPreferences(): Flow<String> {
        return _languagePrefFlow
    }
    suspend fun setTempPref(tempPref: String) {
        context.dataStore.edit { preferences ->
            preferences[tempPrefKey] = tempPref
            _tempPrefFlow.value = tempPref
        }
    }

    fun getTempPref(): Flow<String?> {
        return _tempPrefFlow
    }

    suspend fun setMapPref(lat: Double, long: Double) {
        context.dataStore.edit { preferences ->
            preferences[mapLatPrefKey] = lat
            preferences[mapLongPrefKey] = long
        }
    }

    fun getMapPref(): Flow<Pair<Double,Double>> {
        return context.dataStore.data.map { preferences ->
            val lat = preferences[mapLatPrefKey]
            val long = preferences[mapLongPrefKey]
            if (lat != null && long != null) {
                Pair(lat, long)
            } else {
                Pair(0.0,0.0)
            }
        }
        fun getAlertType(): Flow<String?> {
            return context.dataStore.data.map { preferences ->
                preferences[alertTypePrefKey]
            }
        }

        suspend fun setAlertType(alertType: String) {
            context.dataStore.edit { preferences ->
                preferences[alertTypePrefKey] = alertType
            }
        }

        fun getCountryName(): Flow<String?> {
            return context.dataStore.data.map { preferences ->
                preferences[countryNamePrefKey]
            }
        }

        suspend fun setCountryName(countryName: String) {
            context.dataStore.edit { preferences ->
                preferences[countryNamePrefKey] = countryName
            }
        }

    }

    companion object {
        const val GPS = "GPS"
        const val MAP = "MAP"
        const val ENGLISH="ENGLISH"
        const val ARABIC="ARABIC"
        const val METER_PER_SECOND = "METER_PER_SECOND"
        const val MILE_PER_HOUR = "MILE_PER_HOUR"
        const val CELSIUS = "CELSIUS"
        const val KELVIN = "KELVIN"
        const val FAHRENHEIT = "FAHRENHEIT"
        const val SET_LOCATION_AS_MAIN_LOCATION = "SET_LOCATION_AS_MAIN_LOCATION"
        const val ADD_T0_FAV_IN_THIS_LOCATION = "ADD_T0_FAV_IN_THIS_LOCATION"
        const val ADD_T0_ALERTS_IN_THIS_LOCATION = "ADD_T0_ALERTS_IN_THIS_LOCATION"
        const val NAVIGATE_TO_MAP = "NAVIGATE_TO_MAP"
        const val LANGUAGE_PREFERENCES="LANGUAGE_PREFERENCES"

        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: SettingDataStorePreferences

        fun getInstance(context: Context): SettingDataStorePreferences {
            if (!::instance.isInitialized) {
                instance = SettingDataStorePreferences(context.applicationContext)
            }
            return instance
        }
    }
}
