package com.example.temptrack.ui.settings.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.temptrack.R
import com.example.temptrack.databinding.FragmentSettingsBinding
import com.example.temptrack.datastore.SettingDataStorePreferences
import com.example.temptrack.ui.settings.viewmodel.SettingsViewModel
import com.example.temptrack.ui.settings.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var viewModelFactory: SettingsViewModelFactory
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingDataStorePreferences = SettingDataStorePreferences.getInstance(requireContext())
        viewModelFactory = SettingsViewModelFactory(settingDataStorePreferences)
        viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner

        // Collect location preference
        CoroutineScope(Dispatchers.Main).launch {
            settingDataStorePreferences.getLocationRadioGroupPreference().collect { location ->
                Log.d("SettingsFragment", "Location preference: $location")
                when (location) {
                    SettingDataStorePreferences.MAP -> binding.location.check(R.id.rd_map)
                    SettingDataStorePreferences.GPS -> binding.location.check(R.id.switch_notification)
                }
            }
        }

        // Collect temperature preference
        CoroutineScope(Dispatchers.Main).launch {
            settingDataStorePreferences.getTempPref().collect { temp ->
                when (temp) {
                    SettingDataStorePreferences.KELVIN -> binding.rgTemp.check(R.id.rd_kelvin)
                    SettingDataStorePreferences.CELSIUS -> binding.rgTemp.check(R.id.rd_celsius)
                    SettingDataStorePreferences.FAHRENHEIT -> binding.rgTemp.check(R.id.rd_fahrenheit)
                }
            }
        }

        // Collect language preference
        CoroutineScope(Dispatchers.Main).launch {
            settingDataStorePreferences.getLangPreferences().collect { language ->
                when (language) {
                    SettingDataStorePreferences.ENGLISH -> binding.language.check(R.id.rd_english)
                    SettingDataStorePreferences.ARABIC -> binding.language.check(R.id.rd_arabic)
                }
            }
        }

        // Set listeners
        binding.location.setOnCheckedChangeListener { _, checkedId ->
            val selectedLocation = when (checkedId) {
                R.id.rd_map -> SettingDataStorePreferences.MAP
                R.id.switch_notification -> SettingDataStorePreferences.GPS
                else -> SettingDataStorePreferences.GPS
            }
            viewModel.setLocationPreference(selectedLocation)
        }

        binding.language.setOnCheckedChangeListener { _, checkedId ->
            val selectLanguage = when (checkedId) {
                R.id.rd_arabic -> SettingDataStorePreferences.ARABIC
                R.id.rd_english -> SettingDataStorePreferences.ENGLISH
                else -> SettingDataStorePreferences.ENGLISH
            }
            viewModel.setLangPreference(selectLanguage)
        }

        binding.rgTemp.setOnCheckedChangeListener { _, checkedId ->
            val selectTemp = when (checkedId) {
                R.id.rd_fahrenheit -> SettingDataStorePreferences.FAHRENHEIT
                R.id.rd_celsius -> SettingDataStorePreferences.CELSIUS
                R.id.rd_kelvin -> SettingDataStorePreferences.KELVIN
                else -> SettingDataStorePreferences.CELSIUS
            }
            viewModel.setTempPreference(selectTemp)
        }
    }
}
