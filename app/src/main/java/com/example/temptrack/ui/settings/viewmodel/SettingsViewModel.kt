package com.example.temptrack.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun setLangPreference(selectedLang: String){
        viewModelScope.launch {
            dataStorePreferences.setLangPreference(selectedLang)
        }
    }
    fun cancelCoroutines() {
        viewModelScope.coroutineContext.cancel()
    }

    fun saveData() {
        viewModelScope.launch {
            // Collect the latest values from the flows
            val location = dataStorePreferences.locationPrefFlow.firstOrNull() ?: return@launch
            val temp = dataStorePreferences.tempPrefFlow.firstOrNull() ?: return@launch
            val language = dataStorePreferences.languagePrefFlow.firstOrNull() ?: return@launch

            // Save location preference
            dataStorePreferences.setLocationRadioGroupPreference(location)

            // Save temperature preference
           // dataStorePreferences.setTempPref(temp)

            // Save language preference
            dataStorePreferences.setLangPreference(language)
        }
    }

}
