package com.rich.movielistapi.viewmodel

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rich.movielistapi.data.response.MovieResult
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

class UserViewModelTest{
    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val userVM: UserViewModel = UserViewModel(testContext)

    @Before
    fun setup(){
        userVM.saveData("richard@gmailcom","richard","richard")
    }

    @Test
    fun `Success get username`(){
        val username = "richard"
        val resp = userVM.observerUsername().value
        assertEquals(username, resp)
    }
}