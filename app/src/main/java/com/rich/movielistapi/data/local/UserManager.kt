package com.rich.movielistapi.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserManager(@ApplicationContext val context: Context) {
    val DATA_USERNAME = stringPreferencesKey(USER_USERNAME)
    val DATA_EMAIL = stringPreferencesKey(USER_EMAIL)
    val DATA_PASSWORD = stringPreferencesKey(USER_PASSWORD)
    val DATA_ISLOGIN = booleanPreferencesKey(IS_LOGIN)

    val getUsername : Flow<String> = context.dataStore.data.map {
        it[DATA_USERNAME] ?: ""
    }

    val getEmail : Flow<String> = context.dataStore.data.map {
        it[DATA_EMAIL] ?: ""
    }

    val getPassword : Flow<String> = context.dataStore.data.map {
        it[DATA_PASSWORD] ?: ""
    }

    val getIsLogin : Flow<Boolean> = context.dataStore.data.map {
        it[DATA_ISLOGIN] ?: false
    }

    suspend fun saveData(paramUsername : String, paramEmail : String, paramPass : String){
        context.dataStore.edit {
            it[DATA_USERNAME] = paramUsername
        }
        context.dataStore.edit {
            it[DATA_EMAIL] = paramEmail
        }
        context.dataStore.edit {
            it[DATA_PASSWORD] = paramPass
        }
    }

    suspend fun saveIsLoginStatus(paramIsLogin : Boolean){
        context.dataStore.edit {
            it[DATA_ISLOGIN] = paramIsLogin
        }
    }

    suspend fun removeIsLoginStatus(){
        context.dataStore.edit {
            it.remove(DATA_ISLOGIN)
        }
    }

    companion object {
        const val USER_USERNAME = "username"
        const val USER_EMAIL = "email"
        const val USER_PASSWORD = "password"
        const val IS_LOGIN = "isLogin"
        val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "dataUser")
    }
}