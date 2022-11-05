package com.rich.movielistapi.viewmodel

import com.rich.movielistapi.data.local.FavoriteDAO
import com.rich.movielistapi.data.local.FavoriteMovie
import com.rich.movielistapi.data.remote.APIService
import com.rich.movielistapi.data.response.MovieDetailResponse
import com.rich.movielistapi.data.response.MovieResult
import com.rich.movielistapi.data.response.NowPlayingMovieItem
import io.mockk.*
import org.junit.Assert
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

    @Test
    fun `Success fetching Now Playing Movie`(){
        val resp = mockk<List<NowPlayingMovieItem>>()

        every {
            vm.callGetNowPlayingMovie()
            vm.nowPlayingMovie.value
        } returns resp

        val result = vm.nowPlayingMovie.value
        Assert.assertEquals(resp, result)
    }

    @Test
    fun `Success fetching Movie Detail`(){
        val resp = mockk<MovieDetailResponse>()

        every {
            vm.callGetMovieDetail(1)
            vm.movieDetail.value
        } returns resp

        val result = vm.movieDetail.value
        Assert.assertEquals(resp, result)
    }

    @Test
    fun `Success Get All Favorite Movie`(){
        val resp = mockk<List<FavoriteMovie>>()

        coEvery {
            vm.getAllFavoriteMovie("user")
            vm.allFavoriteMovie.value
        } returns resp

        val result = vm.allFavoriteMovie.value
        Assert.assertEquals(resp, result)
    }

    @Test
    fun `Success Add Movie To Favorite`(){
        val resp = mockk<FavoriteMovie>()
        val movie = mockk<FavoriteMovie>()

        coEvery {
            vm.addFavMovie(movie)
            vm.addFavoriteMovie.value
        } returns resp

        val result = vm.addFavoriteMovie.value
        Assert.assertEquals(resp, result)
    }

    @Test
    fun `Success Delete Movie From Favorite`(){
        var resp = mockk<FavoriteMovie>()
        val movie = mockk<FavoriteMovie>()

        coEvery {
            vm.deleteFavMovie(movie)
            vm.deleteFavoriteMovie.value!!
        }returns resp

        val result = vm.deleteFavoriteMovie.value
        Assert.assertEquals(resp, result)
    }

}