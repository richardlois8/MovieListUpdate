package com.rich.movielistapi.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation
import com.rich.movielistapi.R
import com.rich.movielistapi.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavListener()
    }

    private fun setBottomNavListener() {
        binding.bottomNavigaton.selectedItemId = R.id.bottom_menu_home
        binding.bottomNavigaton.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_menu_home -> {
                    Navigation.findNavController(this, R.id.fragmentContainerView)
                        .navigate(R.id.action_global_mainFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_menu_fav -> {
                    Navigation.findNavController(this, R.id.fragmentContainerView)
                        .navigate(R.id.action_global_favoriteFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }
    }
}