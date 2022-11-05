package com.rich.movielistapi.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rich.movielistapi.R
import com.rich.movielistapi.databinding.FragmentSplashScreenBinding
import com.rich.movielistapi.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenFragment : Fragment() {
    private lateinit var binding: FragmentSplashScreenBinding
    private lateinit var userVM : UserViewModel
    private lateinit var sharedPref : SharedPreferences

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
        userVM = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        sharedPref = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        var isLogin = false
        userVM.checkIsLogin(viewLifecycleOwner)
        userVM.observerIsLogin().observe(viewLifecycleOwner) {
            isLogin = it
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val idToken = sharedPref.getString("idToken", "")
            if(isLogin || idToken != ""){
                findNavController().navigate(R.id.action_splashScreenFragment_to_movieListFragment)
            }else{
                findNavController().navigate(R.id.action_splashScreenFragment_to_registerLoginFragment)
            }
        }, 3000)
    }

}