package com.rich.movieupdate.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.rich.movieupdate.data.local.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(@ApplicationContext appContext : Context) : ViewModel() {

    private var livedataUsername : MutableLiveData<String>
    private var livedataPassword : MutableLiveData<String>
    private var livedataEmail : MutableLiveData<String>
    private var liveDataUserPass : MutableLiveData<List<String?>>
    private val livedataIsLogin : MutableLiveData<Boolean>
    private val userManager: UserManager = UserManager(appContext)

    init {
        livedataUsername = MutableLiveData()
        livedataPassword = MutableLiveData()
        livedataEmail = MutableLiveData()
        liveDataUserPass = MutableLiveData()
        livedataIsLogin = MutableLiveData()
    }

    fun observerUsername() : MutableLiveData<String> = livedataUsername
    fun observerEmail() : MutableLiveData<String> = livedataEmail
    fun observerPassword() : MutableLiveData<String> = livedataPassword
    fun observerUserPass() : MutableLiveData<List<String?>> = liveDataUserPass
    fun observerIsLogin() : MutableLiveData<Boolean> = livedataIsLogin

    fun getDataUser(lifecycle: LifecycleOwner){
        getUsername(lifecycle)
        getEmail(lifecycle)
        getPassword(lifecycle)
    }

    fun saveData(email : String, username : String, password : String){
        GlobalScope.launch {
            userManager.saveData(username,email, password)
        }
    }

    fun getUsername(lifecycle: LifecycleOwner){
        userManager.getUsername.asLiveData().observe(lifecycle) {
            livedataUsername.postValue(it)
        }
    }

    fun getEmail(lifecycle: LifecycleOwner){
        userManager.getEmail.asLiveData().observe(lifecycle) {
            livedataEmail.postValue(it)
        }
    }

    fun getPassword(lifecycle: LifecycleOwner){
        userManager.getPassword.asLiveData().observe(lifecycle) {
            livedataPassword.postValue(it)
        }
    }

    fun checkIsLogin(lifecycle: LifecycleOwner){
        userManager.getIsLogin.asLiveData().observe(lifecycle) {
            livedataIsLogin.postValue(it)
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