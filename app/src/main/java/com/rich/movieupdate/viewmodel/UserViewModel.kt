package com.rich.movieupdate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.rich.movieupdate.datastore.UserManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private var livedataUsername : MutableLiveData<String>
    private var livedataPassword : MutableLiveData<String>
    private var livedataEmail : MutableLiveData<String>
    private var liveDataUserPass : MutableLiveData<MutableMap<String,String>>
    private val userManager: UserManager = UserManager(application.applicationContext)

    init {
        livedataUsername = MutableLiveData()
        livedataPassword = MutableLiveData()
        livedataEmail = MutableLiveData()
        liveDataUserPass = MutableLiveData()
    }

    fun observerUsername() = livedataUsername
    fun observerEmail() = livedataEmail
    fun observerPassword() = livedataPassword
    fun observerUserPass() = liveDataUserPass

    fun getDataUser(lifecycle: LifecycleOwner){
        userManager.getUsername.asLiveData().observe(lifecycle) {
            livedataUsername.postValue(it)
        }
        userManager.getEmail.asLiveData().observe(lifecycle) {
            livedataEmail.postValue(it)
        }
        userManager.getPassword.asLiveData().observe(lifecycle) {
            livedataPassword.postValue(it)
        }
        liveDataUserPass.postValue(mutableMapOf("username" to livedataUsername.value.toString(), "password" to livedataPassword.value.toString()))
    }

    fun saveData(email : String, username : String, password : String){
        GlobalScope.launch {
            userManager.saveData(email, username, password)
        }
    }

    fun saveIsLoginStatus(status : Boolean){
        GlobalScope.launch {
            userManager.saveIsLoginStatus(status)
        }
    }

    fun removeIsLoginStatus(){
        GlobalScope.launch {
            userManager.removeIsLoginStatus()
        }
    }
}