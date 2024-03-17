package com.example.temptrack.datastore

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

private const val SETTING_SHARED_PREFERENCES = "SETTING_SHARED_PREFERENCES"
private const val LOCATION_PREFERENCES = "LOCATION_PREFERENCES"
private const val TEMP_PREFERENCES = "TEMP_PREFERENCES"
private const val LANGUAGE_PREFERENCES = "LANGUAGE_PREFERENCES"
private const val NOTIFICATIONS_PREFERENCES = "NOTIFICATIONS_PREFERENCES"
private const val ALERT_PREFERENCES = "ALERT_PREFERENCES"
private const val COUNTRY_NAME_PREFERENCES = "COUNTRY_NAME_PREFERENCES"
private const val LATITUDE_PREFERENCE = "LATITUDE_PREFERENCE"
private const val LONGITUDE_PREFERENCE = "LONGITUDE_PREFERENCE"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTING_SHARED_PREFERENCES)

class SettingDataStorePreferences(private val context: Context) {

    private val locationPrefKey = stringPreferencesKey(LOCATION_PREFERENCES)
    private val tempPrefKey = stringPreferencesKey(TEMP_PREFERENCES)
    private val languagePrefKey = stringPreferencesKey(LANGUAGE_PREFERENCES)
    private val notificationsPrefKey = stringPreferencesKey(NOTIFICATIONS_PREFERENCES)
    private val alertPrefKey = stringPreferencesKey(ALERT_PREFERENCES)
    private val countryNamePrefKey = stringPreferencesKey(COUNTRY_NAME_PREFERENCES)
    private val latitudePrefKey = doublePreferencesKey(LATITUDE_PREFERENCE)
    private val longitudePrefKey = doublePreferencesKey(LONGITUDE_PREFERENCE)

    private val _locationPrefFlow = MutableStateFlow(ENUM_LOCATION.GPS)
    private val _tempPrefFlow = MutableStateFlow("")
    private val _languagePrefFlow = MutableStateFlow("")
    private val _notificationsPrefFlow = MutableStateFlow(ENUM_NOTIFICATIONS.Enabled)
    private val _alertPrefFlow = MutableStateFlow(Enum_ALERT.NOTIFICATION)
    private val _countryNamePrefFlow = MutableStateFlow("")

    val locationPrefFlow: Flow<ENUM_LOCATION> = _locationPrefFlow
    val tempPrefFlow: Flow<String> = _tempPrefFlow
    val languagePrefFlow: Flow<String> = _languagePrefFlow
    val notificationsPrefFlow: Flow<ENUM_NOTIFICATIONS> = _notificationsPrefFlow
    val alertPrefFlow: Flow<Enum_ALERT> = _alertPrefFlow
    val countryNamePrefFlow: Flow<String> = _countryNamePrefFlow

    suspend fun setLocationRadioGroupPreference(selectedLocation: ENUM_LOCATION) {
        context.dataStore.edit { preferences ->
            preferences[locationPrefKey] = selectedLocation.name
        }
        _locationPrefFlow.value = selectedLocation
    }

    suspend fun setLangPreference(selectLanguage: ENUM_LANGUAGE) {
        context.dataStore.edit { preferences ->
            preferences[languagePrefKey] = selectLanguage.name
        }
        _languagePrefFlow.value = selectLanguage.name
    }

    suspend fun setTempPref(tempPref: ENUM_TEMP_PREF) {
        context.dataStore.edit { preferences ->
            preferences[tempPrefKey] = tempPref.name
        }
        _tempPrefFlow.value = tempPref.name
    }
    fun getCountryNamePref(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[countryNamePrefKey] ?: "Ismailia Governorate"
        }
    }
    suspend fun setNotificationsPref(notificationsPref: ENUM_NOTIFICATIONS) {
        context.dataStore.edit { preferences ->
            preferences[notificationsPrefKey] = notificationsPref.name
        }
        _notificationsPrefFlow.value = notificationsPref
    }

    suspend fun setAlertPref(alertPref: Enum_ALERT) {
        context.dataStore.edit { preferences ->
            preferences[alertPrefKey] = alertPref.name
        }
        _alertPrefFlow.value = alertPref
    }

    suspend fun setCountryNamePref(countryName: String) {
        context.dataStore.edit { preferences ->
            preferences[countryNamePrefKey] = countryName
        }
        _countryNamePrefFlow.value = countryName
    }
    fun getLocationPreference(): Flow<ENUM_LOCATION> {
        return context.dataStore.data.map { preferences ->
            val locationPref = preferences[locationPrefKey] ?: ENUM_LOCATION.GPS.name
            ENUM_LOCATION.valueOf(locationPref)
        }
    }

    fun getNotificationsPref():Flow<ENUM_NOTIFICATIONS>{
        return context.dataStore.data.map { preferences ->
            val status=preferences[notificationsPrefKey]?:ENUM_NOTIFICATIONS.Disabled.name
            ENUM_NOTIFICATIONS.valueOf(status)
        }
    }
    fun getAlertPref():Flow<Enum_ALERT>{
        return context.dataStore.data.map {preferences ->
            val state=preferences[alertPrefKey]?:Enum_ALERT.ALARM.name
            Enum_ALERT.valueOf(state)
        }
    }
    fun getLangPreference(): Flow<ENUM_LANGUAGE> {
        return context.dataStore.data.map { preferences ->
            val languagePref = preferences[languagePrefKey] ?: ENUM_LANGUAGE.ENGLISH.name
            ENUM_LANGUAGE.valueOf(languagePref)
        }
    }


    fun getTempPreference(): Flow<ENUM_TEMP_PREF> {
        return context.dataStore.data.map { preferences ->
            val tempPref = preferences[tempPrefKey] ?: ENUM_TEMP_PREF.CELSIUS.name
            ENUM_TEMP_PREF.valueOf(tempPref)
        }
    }

    suspend fun setLatitude(latitude: Double) {
        context.dataStore.edit { preferences ->
            preferences[latitudePrefKey] = latitude
        }
    }

    suspend fun setLongitude(longitude: Double) {
        context.dataStore.edit { preferences ->
            preferences[longitudePrefKey] = longitude
        }
    }

    fun getLatitude(): Flow<Double> {
        return context.dataStore.data.map { preferences ->
            preferences[latitudePrefKey] ?: 0.0
        }
    }

    fun getLongitude(): Flow<Double> {
        return context.dataStore.data.map { preferences ->
            preferences[longitudePrefKey] ?: 0.0
        }
    }

    companion object {

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

enum class ENUM_LOCATION {
    MAP,
    GPS
}

enum class ENUM_TEMP_PREF {
    CELSIUS,
    FAHRENHEIT,
    KELVIN
}

enum class ENUM_NOTIFICATIONS {
    Enabled,
    Disabled
}

enum class Enum_ALERT {
    ALARM,
    NOTIFICATION
}
enum class ENUM_LANGUAGE{
    ENGLISH,
    ARABIC
}
