package com.rich.movieupdate.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.rich.movieupdate.ui.activity.MainActivity
import com.rich.movieupdate.R
import com.rich.movieupdate.databinding.FragmentRegisterLoginBinding
import com.rich.movieupdate.viewmodel.BlurViewModel
import com.rich.movieupdate.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RegisterLoginFragment : Fragment() {
    private lateinit var binding : FragmentRegisterLoginBinding
    lateinit var userVM: UserViewModel
    private val blurVM : BlurViewModel by lazy {
        BlurViewModel(requireActivity().application)
    }
    private lateinit var savedUsername : String
    private lateinit var savedPassword : String
    private var image_uri : Uri? = null
    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            Log.d("URI_IMG", result.toString())
            binding.registerForm.imgProfile.setImageURI(result)
            image_uri = result!!
            blurVM.setImageUri(result)
        }
    private val REQUEST_CODE_PERMISSION = 100

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
        userVM = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        showHideForm()
        setButtonListener()

        (activity as MainActivity).binding.bottomNav.visibility = View.GONE
        getDataUser()
    }

    private fun getDataUser() {
        userVM.getDataUser(viewLifecycleOwner)
        userVM.observerUsername().observe(viewLifecycleOwner){
            savedUsername = it
        }
        userVM.observerPassword().observe(viewLifecycleOwner){
            savedPassword = it
        }
    }

    private fun setButtonListener() {
        binding.registerForm.btnRegister.setOnClickListener {
            registerUser()
        }
        binding.loginForm.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.registerForm.imgProfile.setOnClickListener {
            checkingPermissions()
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
                saveImage()
                gotoLogin()
            }
        }

    }

    private fun loginUser() {
        val username = binding.loginForm.usernameInput.text.toString()
        val password = binding.loginForm.passwordInput.text.toString()

        if (username.isEmpty()) {
            binding.loginForm.usernameInput.error = resources.getString(R.string.required_field)
        } else if (password.isEmpty()) {
            binding.loginForm.passwordInput.error = resources.getString(R.string.required_field)
        } else {
            if (username == savedUsername && password == savedPassword) {
                val sharedPref = requireActivity().getSharedPreferences(
                    "user",
                    Context.MODE_PRIVATE
                )
                with(sharedPref.edit()) {
                    putString("username", username)
                    apply()
                }
                userVM.saveIsLoginStatus(true)
                gotoHome()
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.login_failed),
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun saveImage(){
        val resolver = requireActivity().applicationContext.contentResolver
        val picture = BitmapFactory.decodeStream(
            resolver.openInputStream(Uri.parse(image_uri.toString())))
        saveImageProfile(requireContext(), picture)
        blurVM.applyBlur()
    }

    private fun checkingPermissions() {
        if (isGranted(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSION,)
        ){
            openGallery()
        }
    }

    private fun isGranted(
        activity: Activity,
        permission: String,
        permissions: Array<String>,
        request: Int,
    ): Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(activity, permissions, request)
            }
            false
        } else {
            true
        }
    }

    private fun showPermissionDeniedDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton(
                "App Settings"
            ) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun openGallery() {
        requireActivity().intent.type = "image/*"
        galleryResult.launch("image/*")
    }

    private fun saveImageProfile(applicationContext: Context, bitmap: Bitmap): Uri {
        val name = "img-profile.png"
        val outputDir = File(applicationContext.filesDir, "profiles")
        if (!outputDir.exists()) {
            outputDir.mkdirs() // should succeed
        }
        val outputFile = File(outputDir, name)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, out)
        } finally {
            out?.let {
                try {
                    it.close()
                } catch (ignore: IOException) {
                }

            }
        }
        Log.d("URI_IMG", Uri.fromFile(outputFile).toString())
        return Uri.fromFile(outputFile)
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