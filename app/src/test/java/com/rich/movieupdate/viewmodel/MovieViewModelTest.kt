package com.rich.movieupdate.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.rich.movieupdate.data.local.FavoriteDAO
import com.rich.movieupdate.data.remote.APIService
import com.rich.movieupdate.data.response.MovieResult
import com.rich.movieupdate.data.response.PopularMovieResponse
import io.mockk.*
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
        vm = mockk()
    }

//    @Test
//    fun getPopularMovies(){
//        val resp = mockk<List<MovieResult>>()
//
//        every {
//            client.getPopularMoviesTest()
//
//        }returns resp
//
//        //        System Under Test (WHEN)
//        val result = client.getPopularMoviesTest()
//
//        verify {
//            client.getPopularMoviesTest()
//        }
//        Assert.assertEquals(result, resp)
//    }

    @Test
    fun `Success fetching Popular Movie`(){
        val resp = mockk<List<MovieResult>>()

        every {
            vm.callGetPopularMovieApi()
            vm.popularMovie.value
        } returns resp

        val result = vm.popularMovie.value
        Assert.assertEquals(resp, result)
    }
}