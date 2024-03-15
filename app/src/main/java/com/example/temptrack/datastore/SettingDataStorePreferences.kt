package com.example.temptrack.datastore

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

private const val SETTING_SHARED_PREFERENCES = "SETTING_SHARED_PREFERENCES"
private const val LOCATION_PREFERENCES = "LOCATION_PREFERENCES"
private const val TEMP_PREFERENCES = "TEMP_PREFERENCES"
private const val LANGUAGE_PREFERENCES = "LANGUAGE_PREFERENCES"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTING_SHARED_PREFERENCES)

class SettingDataStorePreferences(private val context: Context) {

    private val locationPrefKey = stringPreferencesKey(LOCATION_PREFERENCES)
    private val tempPrefKey = stringPreferencesKey(TEMP_PREFERENCES)
    private val languagePrefKey = stringPreferencesKey(LANGUAGE_PREFERENCES)

    private val _locationPrefFlow = MutableStateFlow(ENUM_LOCATION.GPS)
    private val _tempPrefFlow = MutableStateFlow("")
    private val _languagePrefFlow = MutableStateFlow("")

    val locationPrefFlow: Flow<ENUM_LOCATION> = _locationPrefFlow
    val tempPrefFlow: Flow<String> = _tempPrefFlow
    val languagePrefFlow: Flow<String> = _languagePrefFlow


    suspend fun setLocationRadioGroupPreference(selectedLocation: ENUM_LOCATION) {
        context.dataStore.edit { preferences ->
            preferences[locationPrefKey] = selectedLocation.name
        }
        _locationPrefFlow.value = selectedLocation
    }

    suspend fun setLangPreference(selectLanguage: String) {
        context.dataStore.edit { preferences ->
            preferences[languagePrefKey] = selectLanguage
        }
        _languagePrefFlow.value = selectLanguage
    }

    suspend fun setTempPref(tempPref: ENUM_TEMP_PREF) {
        context.dataStore.edit { preferences ->
            preferences[tempPrefKey] = tempPref.name
        }
        _tempPrefFlow.value = tempPref.name
    }

    fun getLocationRadioGroupPreference(): Flow<ENUM_LOCATION> {
        return _locationPrefFlow
    }

    fun getLangPreferences(): Flow<String> {
        return _languagePrefFlow
    }
    fun getTempPref(): Flow<ENUM_TEMP_PREF?> {
        return _tempPrefFlow.map { tempPref ->
            try {
                ENUM_TEMP_PREF.valueOf(tempPref)
            } catch (e: IllegalArgumentException) {
                Log.i("TAG", "getTempPref: $e")
                null
            }
        }
    }

    companion object {
        const val ENGLISH = "ENGLISH"
        const val ARABIC = "ARABIC"

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
