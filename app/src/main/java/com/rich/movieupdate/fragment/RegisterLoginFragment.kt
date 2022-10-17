package com.rich.movieupdate.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.rich.movieupdate.MainActivity
import com.rich.movieupdate.R
import com.rich.movieupdate.databinding.FragmentRegisterLoginBinding
import com.rich.movieupdate.datastore.UserManager
import com.rich.movieupdate.viewmodel.UserViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class RegisterLoginFragment : Fragment() {
    private lateinit var binding: FragmentRegisterLoginBinding
    private val userVM: UserViewModel by lazy {
        UserViewModel(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideForm()
        setButtonListener()
    }

    private fun setButtonListener() {
        binding.registerForm.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.loginForm.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.btnEnglish.setOnClickListener {
            setLocale("en")
        }
        binding.btnIndonesia.setOnClickListener {
            setLocale("id")
        }
    }

    private fun registerUser() {
        val email = binding.registerForm.emailInput.text.toString()
        val username = binding.registerForm.usernameInput.text.toString()
        val password = binding.registerForm.passwordInput.text.toString()
        val passwordConfirm = binding.registerForm.passwordConfirmInput.text.toString()

        var isEmpty = false
        if (email.isEmpty()) {
            binding.registerForm.emailInput.error = resources.getString(R.string.required_field)
            isEmpty = true
        }
        if (username.isEmpty()) {
            binding.registerForm.usernameInput.error = resources.getString(R.string.required_field)
            isEmpty = true
        }
        if (password.isEmpty()) {
            binding.registerForm.passwordInput.error = resources.getString(R.string.required_field)
            isEmpty = true
        }
        if (passwordConfirm.isEmpty()) {
            binding.registerForm.passwordConfirmInput.error =
                resources.getString(R.string.required_field)
            isEmpty = true
        }

        if (!isEmpty) {
            if (password != passwordConfirm) {
                binding.registerForm.passwordConfirmInput.error =
                    resources.getString(R.string.pass_not_match)
            } else {
                userVM.saveData(email, username, password)
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.registration_success),
                    Toast.LENGTH_SHORT
                ).show()
                gotoLogin()
            }
        }

    }

    private fun loginUser() {
        val username = binding.loginForm.usernameInput.text.toString()
        val password = binding.loginForm.passwordInput.text.toString()
        var savedUsername = ""
        var savedPassword = ""
        var isFound = false

        if (username.isEmpty()) {
            binding.loginForm.usernameInput.error = resources.getString(R.string.required_field)
        } else if (password.isEmpty()) {
            binding.loginForm.passwordInput.error = resources.getString(R.string.required_field)
        } else {
            userVM.getDataUser(viewLifecycleOwner)
            userVM.observerUserPass().observe(viewLifecycleOwner) {
                if(it.getValue("username") == username && it.getValue("password") == password){
                    isFound = true
                    savedUsername = it.getValue("username").toString()
                    savedPassword = it.getValue("password").toString()
                    userVM.saveIsLoginStatus(true)
                }else if ((savedUsername != username || savedPassword != password) && (savedUsername != "")){
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
//                savedUsername = it.getValue("username")
//                savedPassword = it.getValue("password")
//                if ((username == savedUsername) && (password == savedPassword)) {
//                    userVM.saveIsLoginStatus(true)
//                    isFound = true
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        resources.getString(R.string.login_failed),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
            }
            if (isFound) {
                gotoHome()
            }
        }
    }

    private fun gotoHome() {
        findNavController().navigate(R.id.action_registerLoginFragment_to_movieListFragment)
    }

    private fun showHideForm() {
        binding.registerTitle.setOnClickListener {
            //Tampilkan form register ketika diklik
            gotoRegister()
        }
        binding.loginTitle.setOnClickListener {
            //Tampilkan form login ketika diklik
            gotoLogin()
        }
    }

    private fun gotoRegister() {
        if (binding.registerForm.root.visibility == View.GONE) {
            binding.registerForm.root.visibility = View.VISIBLE
            binding.loginForm.root.visibility = View.GONE
            binding.registerTitle.setTextColor(resources.getColor(R.color.white))
            binding.loginTitle.setTextColor(resources.getColor(R.color.description_color))
        } else {
            binding.registerForm.root.visibility = View.GONE
            binding.loginForm.root.visibility = View.VISIBLE
            binding.registerTitle.setTextColor(resources.getColor(R.color.description_color))
            binding.loginTitle.setTextColor(resources.getColor(R.color.white))
        }
    }

    private fun gotoLogin() {
        if (binding.loginForm.root.visibility == View.GONE) {
            binding.loginForm.root.visibility = View.VISIBLE
            binding.registerForm.root.visibility = View.GONE
            binding.loginTitle.setTextColor(resources.getColor(R.color.white))
            binding.registerTitle.setTextColor(resources.getColor(R.color.description_color))
        } else {
            binding.loginForm.root.visibility = View.GONE
            binding.registerForm.root.visibility = View.VISIBLE
            binding.loginTitle.setTextColor(resources.getColor(R.color.description_color))
            binding.registerTitle.setTextColor(resources.getColor(R.color.white))
        }
    }

    private fun setLocale(lang: String) {
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        activity?.startActivity(Intent(activity, MainActivity::class.java))
    }
}