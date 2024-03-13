package com.example.temptrack.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temptrack.datastore.SettingDataStorePreferences
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStorePreferences: SettingDataStorePreferences) : ViewModel() {

    fun setLocationPreference(selectedLocation: String) {
        viewModelScope.launch {
            dataStorePreferences.setLocationRadioGroupPreference(selectedLocation)
        }
    }

    fun setTempPreference(selectedTemp: String){
        viewModelScope.launch {
            dataStorePreferences.setTempPref(selectedTemp)
        }
    }

    fun setLangPreference(selectedLang: String){
        viewModelScope.launch {
            dataStorePreferences.setLangPreference(selectedLang)
        }
    }
}
