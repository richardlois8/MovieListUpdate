package com.rich.movieupdate.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.adapters.ViewGroupBindingAdapter.setListener
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rich.movieupdate.R
import com.rich.movieupdate.databinding.FragmentProfileBinding
import com.rich.movieupdate.viewmodel.BlurViewModel
import com.rich.movieupdate.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val userVM : UserViewModel by lazy {
        UserViewModel(requireActivity().application)
    }
    private val blurVM : BlurViewModel by lazy {
        BlurViewModel(requireActivity().application)
    }
    private lateinit var sharedPref : SharedPreferences
    private lateinit var oldPassword : String
    private var image_uri : Uri? = null
    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            Log.d("URI_IMG", result.toString())
            binding.imgProfile.setImageURI(result)
            image_uri = result!!
            blurVM.setImageUri(result)
        }
    private val REQUEST_CODE_PERMISSION = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        getDataUser()
        setImageProfileBackground()
        setListener()
    }


    private fun getDataUser(){
        //Untuk login via google
        if(sharedPref.getString("idToken","") != ""){
            Glide.with(requireContext())
                .load(sharedPref.getString("photoUri",""))
                .into(binding.imgProfile)
            Glide.with(requireContext())
                .load(sharedPref.getString("photoUri",""))
                .into(binding.imgProfileBackground)
            binding.apply {
                emailInput.setText(sharedPref.getString("email",""))
                usernameInput.setText(sharedPref.getString("username",""))
                emailInput.setEnabled(false)
                usernameInput.setEnabled(false)
                passwordInput.setEnabled(false)
                btnEditPhoto.setEnabled(false)
                btnSaveUpdate.setEnabled(false)
            }
        }else{
            userVM.getDataUser(viewLifecycleOwner)
            userVM.observerEmail().observe(viewLifecycleOwner) {
                binding.emailInput.setText(it)
            }
            userVM.observerUsername().observe(viewLifecycleOwner) {
                binding.usernameInput.setText(it)
            }
            userVM.observerPassword().observe(viewLifecycleOwner) {
                oldPassword = it
                binding.passwordInput.setText(it)
            }
            var image = BitmapFactory.decodeFile(requireActivity().applicationContext.filesDir.path + File.separator +"profiles"+ File.separator +"img-profile.png")
            binding.imgProfile.setImageBitmap(image)
        }
    }

    private fun setListener() {
        binding.apply {
            topAppBar.menu.findItem(R.id.action_logout).setOnMenuItemClickListener {
                logout()
                true
            }
            topAppBar.setNavigationOnClickListener(View.OnClickListener {
                requireActivity().onBackPressed()
            })
            btnSaveUpdate.setOnClickListener {
                updateUser()
            }
            imgProfile.setOnClickListener {
                checkingPermissions()
            }
            btnEditPhoto.setOnClickListener{
                checkingPermissions()
            }
        }
    }

    private fun setImageProfileBackground(){
        var image = BitmapFactory.decodeFile(requireActivity().applicationContext.filesDir.path + File.separator +"blur_outputs"+ File.separator +"IMG-BLURRED.png")
        binding.imgProfileBackground.setImageBitmap(image)
    }

    private fun logout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure want to logout?")
            .setPositiveButton("Yes") { dialog, which ->
                if(sharedPref.getString("idToken","") != ""){
                    sharedPref.edit().clear().apply()
                    Firebase.auth.signOut()
                }else{
                    userVM.removeIsLoginStatus()
                }
                findNavController().navigate(R.id.action_profileFragment_to_registerLoginFragment)
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateUser(){
        val email = binding.emailInput.text.toString()
        val username = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()
        if(image_uri != null){
            saveImage()
        }
        userVM.saveData(email, username, password)

        if(oldPassword != password){
            findNavController().navigate(R.id.action_profileFragment_to_registerLoginFragment)
        }
    }

    private fun saveImage(){
        val resolver = requireActivity().applicationContext.contentResolver
        val picture = BitmapFactory.decodeStream(
            resolver.openInputStream(Uri.parse(image_uri.toString())))
        saveImageProfile(requireContext(), picture)
        blurVM.applyBlur()
    }

    private fun showLoading(isLoading : Boolean) {
        if(isLoading){
            binding.lottieLoading.visibility = View.VISIBLE
            binding.progressBarContainer.visibility = View.VISIBLE
        }else{
            binding.lottieLoading.visibility = View.GONE
            binding.progressBarContainer.visibility = View.GONE
        }
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
}