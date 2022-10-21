package com.rich.movieupdate.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.rich.movieupdate.R
import com.rich.movieupdate.databinding.ActivityMainBinding
import com.rich.movieupdate.ui.fragment.ProfileFragment
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
        binding.bottomNav.selectedItemId = R.id.bottom_menu_home
        binding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.bottom_menu_home -> {
                    Navigation.findNavController(this,R.id.fragmentContainerView).navigate(R.id.action_global_mainFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_menu_fav -> {
                    Navigation.findNavController(this,R.id.fragmentContainerView).navigate(R.id.action_global_favoriteFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }
    }
}