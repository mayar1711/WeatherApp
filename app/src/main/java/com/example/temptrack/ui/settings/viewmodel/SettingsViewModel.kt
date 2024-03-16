package com.example.temptrack.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temptrack.datastore.ENUM_LANGUAGE
import com.example.temptrack.datastore.ENUM_LOCATION
import com.example.temptrack.datastore.ENUM_TEMP_PREF
import com.example.temptrack.datastore.SettingDataStorePreferences
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStorePreferences: SettingDataStorePreferences) : ViewModel() {

    fun setLocationPreference(selectedLocation: ENUM_LOCATION) {
        viewModelScope.launch {
            dataStorePreferences.setLocationRadioGroupPreference(selectedLocation)
        }
    }

    fun setTempPreference(selectedTemp: ENUM_TEMP_PREF) {
        viewModelScope.launch {
            dataStorePreferences.setTempPref(selectedTemp)
        }
    }

    fun setLangPreference(selectedLang: ENUM_LANGUAGE){
        viewModelScope.launch {
            dataStorePreferences.setLangPreference(selectedLang)
        }
    }
    fun cancelCoroutines() {
        viewModelScope.coroutineContext.cancel()
    }



}
