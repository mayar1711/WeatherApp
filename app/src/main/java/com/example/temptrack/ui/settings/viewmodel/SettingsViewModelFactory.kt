package com.example.temptrack.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.temptrack.datastore.SettingDataStorePreferences

class SettingsViewModelFactory(private val dataStorePreferences: SettingDataStorePreferences) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(dataStorePreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
