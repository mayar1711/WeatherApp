package com.example.temptrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.temptrack.databinding.ActivityMainBinding
import com.example.temptrack.databinding.SheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.menu.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {

        val bottomSheetDialog = BottomSheetDialog(this)
        val sheetBinding:SheetBinding = SheetBinding.inflate(layoutInflater)
       bottomSheetDialog.setContentView(sheetBinding.root)
        bottomSheetDialog.show()

        sheetBinding.cgHome.setOnClickListener {
            navController.navigate(R.id.homeFragment2)
            bottomSheetDialog.dismiss()
        }

        sheetBinding.cgAlert.setOnClickListener {
            navController.navigate(R.id.alert)
            bottomSheetDialog.dismiss()
        }

        sheetBinding.cgFavorite.setOnClickListener {
            navController.navigate(R.id.favorite)
            bottomSheetDialog.dismiss()
        }

        sheetBinding.cgSettings.setOnClickListener {
            navController.navigate(R.id.settings)
            bottomSheetDialog.dismiss()
        }

    }
}

