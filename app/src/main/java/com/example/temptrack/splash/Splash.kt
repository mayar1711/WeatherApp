package com.example.temptrack.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.temptrack.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.splashLottie.animate().setDuration(10000).setStartDelay(1500);
    }
}