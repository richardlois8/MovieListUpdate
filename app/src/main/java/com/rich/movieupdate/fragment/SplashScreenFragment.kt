package com.rich.movieupdate.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.rich.movieupdate.MainActivity
import com.rich.movieupdate.R
import com.rich.movieupdate.databinding.FragmentSplashScreenBinding
import com.rich.movieupdate.datastore.UserManager

class SplashScreenFragment : Fragment() {
    private lateinit var binding: FragmentSplashScreenBinding
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userManager = UserManager(requireContext())
        var isLogin = false
        userManager.getIsLogin.asLiveData().observe(viewLifecycleOwner) {
            isLogin = it
        }
        Handler(Looper.getMainLooper()).postDelayed({
            if(isLogin){
                findNavController().navigate(R.id.action_splashScreenFragment_to_movieListFragment)
            }else{
                findNavController().navigate(R.id.action_splashScreenFragment_to_registerLoginFragment)
            }
        }, 3000)
    }

}