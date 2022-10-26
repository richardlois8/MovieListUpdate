package com.rich.movieupdate.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.rich.movieupdate.data.local.FavoriteDAO
import com.rich.movieupdate.data.remote.APIService
import com.rich.movieupdate.data.response.MovieResult
import com.rich.movieupdate.data.response.PopularMovieResponse
import io.mockk.Call
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MovieViewModelTest{
    lateinit var client : APIService
    lateinit var db : FavoriteDAO
    lateinit var vm: MovieViewModel

    @Before
    fun setUp(){
        client = mockk()
        db = mockk()
        vm = MovieViewModel(client, db)
    }

    @Test
    fun getPopularMovies(){
        val resp = mockk<List<MovieResult>>()

        every {
            client.getPopularMoviesTest()

        }returns resp

        //        System Under Test (WHEN)
        val result = client.getPopularMoviesTest()

        verify {
            client.getPopularMoviesTest()
        }
        Assert.assertEquals(result, resp)
    }

//    @Test
//    fun getPopularMovies(){
//        val resp = mockk<List<MovieResult>>()
//
//        every {
//            vm.callGetPopularMovieApi()
//        }returns resp
//
//        //        System Under Test (WHEN)
//        val result = vm.callGetPopularMovieApi()
//
//        verify {
//            vm.callGetPopularMovieApi()
//        }
//        Assert.assertEquals(result, resp)
//    }
}