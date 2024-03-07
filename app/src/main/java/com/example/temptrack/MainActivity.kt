package com.example.temptrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.temptrack.databinding.ActivityMainBinding
import com.example.temptrack.datastore.SettingDataStorePreferences
import com.example.temptrack.location.obtainLocation

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
      val settingDataStorePreferences = SettingDataStorePreferences.getInstance(this)
        obtainLocation(this, settingDataStorePreferences)
    }
}