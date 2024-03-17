package com.example.temptrack.ui.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.temptrack.R
import com.example.temptrack.databinding.FragmentSettingsBinding
import com.example.temptrack.datastore.ENUM_LANGUAGE
import com.example.temptrack.datastore.ENUM_LOCATION
import com.example.temptrack.datastore.ENUM_TEMP_PREF
import com.example.temptrack.datastore.SettingDataStorePreferences
import com.example.temptrack.ui.settings.viewmodel.SettingsViewModel
import com.example.temptrack.ui.settings.viewmodel.SettingsViewModelFactory
import com.example.temptrack.util.changeLanguageLocaleTo
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


        CoroutineScope(Dispatchers.Main).launch {
            settingDataStorePreferences.getLocationPreference().collect { location ->
                when (location) {
                    ENUM_LOCATION.MAP -> binding.location.check(R.id.rd_map)
                    ENUM_LOCATION.GPS -> binding.location.check(R.id.rd_gps)
                }
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            settingDataStorePreferences.getTempPreference().collect() { temp ->
                when (temp) {
                    ENUM_TEMP_PREF.KELVIN -> binding.rgTemp.check(R.id.rd_kelvin)
                    ENUM_TEMP_PREF.CELSIUS -> binding.rgTemp.check(R.id.rd_celsius)
                    ENUM_TEMP_PREF.FAHRENHEIT -> binding.rgTemp.check(R.id.rd_fahrenheit)
                    null -> binding.rgTemp.check(R.id.rd_celsius)
                }
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            settingDataStorePreferences.getLangPreference().collect { language ->
                when (language) {
                    ENUM_LANGUAGE.ENGLISH -> binding.language.check(R.id.rd_english)
                    ENUM_LANGUAGE.ARABIC -> binding.language.check(R.id.rd_arabic)
                    null -> binding.language.check(R.id.rd_english)
                }
            }
        }

        binding.location.setOnCheckedChangeListener { _, checkedId ->
            val selectedLocation = when (checkedId) {
                R.id.rd_map -> ENUM_LOCATION.MAP
                R.id.rd_gps -> ENUM_LOCATION.GPS
                else -> return@setOnCheckedChangeListener
            }
            viewModel.setLocationPreference(selectedLocation)
        }

        binding.language.setOnCheckedChangeListener { _, checkedId ->
            val selectLanguage = when (checkedId) {
                R.id.rd_arabic -> {
                    changeLanguageLocaleTo("ar")
                    ENUM_LANGUAGE.ARABIC

                }

                R.id.rd_english -> {
                    changeLanguageLocaleTo("en")

                    ENUM_LANGUAGE.ENGLISH

                }

                else -> return@setOnCheckedChangeListener
            }
            viewModel.setLangPreference(selectLanguage)
        }

        binding.rgTemp.setOnCheckedChangeListener { _, checkedId ->
            val selectTemp = when (checkedId) {
                R.id.rd_fahrenheit -> ENUM_TEMP_PREF.FAHRENHEIT
                R.id.rd_celsius -> ENUM_TEMP_PREF.CELSIUS
                R.id.rd_kelvin -> ENUM_TEMP_PREF.KELVIN
                else -> return@setOnCheckedChangeListener
            }
            viewModel.setTempPreference(selectTemp)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.cancelCoroutines()
    }
}


