package com.rich.movielistapi.viewmodel

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rich.movielistapi.data.local.FavoriteDAO
import com.rich.movielistapi.data.local.FavoriteDatabase
import com.rich.movielistapi.data.local.FavoriteMovie
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch

class FavoriteMovieTest {
    private lateinit var db: FavoriteDatabase
    private lateinit var favDao : FavoriteDAO

    @Before
    fun setUp(){
        db = mockk()
        favDao = mockk()
    }

    @Test
    fun `Success Add Movie To Favorite`(){
        val movie = FavoriteMovie(1, "Avengers", "01/01/2001", 7.0, "url", "user")
        favDao.addFavorite(movie)

        val result = favDao.getAllFavorite("user")
        for(i in result){
            if(i.equals(movie)){
                assertTrue(true)
            }
        }
    }
}