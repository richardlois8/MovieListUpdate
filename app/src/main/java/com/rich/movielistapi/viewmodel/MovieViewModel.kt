package com.rich.movielistapi.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rich.movielistapi.data.local.FavoriteDAO
import com.rich.movielistapi.data.local.FavoriteMovie
import com.rich.movielistapi.data.response.*
import com.rich.movielistapi.data.remote.APIService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(val client : APIService, val db : FavoriteDAO): ViewModel() {
    private val _popularMovie = MutableLiveData<List<MovieResult>>()
    val popularMovie : LiveData<List<MovieResult>> = _popularMovie

    private val _nowPlayingMovie = MutableLiveData<List<NowPlayingMovieItem>>()
    val nowPlayingMovie : LiveData<List<NowPlayingMovieItem>> = _nowPlayingMovie

    private val _movieDetail = MutableLiveData<MovieDetailResponse>()
    val movieDetail : LiveData<MovieDetailResponse> = _movieDetail

    private val _allFavoriteMovie = MutableLiveData<List<FavoriteMovie>>()
    val allFavoriteMovie : LiveData<List<FavoriteMovie>> = _allFavoriteMovie

    private val _addFavoriteMovie = MutableLiveData<FavoriteMovie>()
    val addFavoriteMovie : LiveData<FavoriteMovie> = _addFavoriteMovie

    private val _deleteFavoriteMovie = MutableLiveData<FavoriteMovie>()
    val deleteFavoriteMovie : LiveData<FavoriteMovie> = _deleteFavoriteMovie

    private val _checkIsFavorite = MutableLiveData<Boolean>()
    val checkIsFavorite : LiveData<Boolean> = _checkIsFavorite

    fun callGetPopularMovieApi() {
        client.getPopularMovies().enqueue(object : Callback<PopularMovieResponse> {
            override fun onResponse(
                call: Call<PopularMovieResponse>,
                response: Response<PopularMovieResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _popularMovie.postValue(data.results as List<MovieResult>?)
                    }
                } else {
                    Log.e("Error not successful : ", response.message())
                }
            }

            override fun onFailure(call: Call<PopularMovieResponse>, t: Throwable) {
                Log.e("Error onFailure :", t.message!!)
            }
        })
    }

    fun callGetMovieDetail(movie_id : Int) {
        client.getMovieDetail(movie_id).enqueue(object : Callback<MovieDetailResponse>{
            override fun onResponse(
                call: Call<MovieDetailResponse>,
                response: Response<MovieDetailResponse>
            ) {
                if (response.isSuccessful){
                    val data = response.body()
                    if (data != null){
                        _movieDetail.postValue(data!!)
                    }
                } else {
                    Log.e("Error : ", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MovieDetailResponse>, t: Throwable) {
                Log.e("Error ; ", "onFailure: ${t.message}")
            }
        })
    }

    fun callGetNowPlayingMovie(){
        client.getNowPlayingMovie().enqueue(object : Callback<NowPlayingResponse>{
            override fun onResponse(
                call: Call<NowPlayingResponse>,
                response: Response<NowPlayingResponse>
            ) {
                if (response.isSuccessful){
                    val data = response.body()
                    if (data != null){
                        _nowPlayingMovie.postValue(data.results as List<NowPlayingMovieItem>)
                    }
                } else {
                    Log.e("Error : ", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<NowPlayingResponse>, t: Throwable) {
                Log.d("Error : ", "onFailure: ${t.message}")
            }


        })
    }

    fun getAllFavoriteMovie(username : String) {
        CoroutineScope(Dispatchers.IO).launch {
            _allFavoriteMovie.postValue(db.getAllFavorite(username))
        }
    }

    fun isFavoriteMovie(id : Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _checkIsFavorite.postValue(db.isFavoriteMovie(id))
        }
    }

    fun addFavMovie(favMovie : FavoriteMovie) {
        CoroutineScope(Dispatchers.IO).launch {
            db.addFavorite(favMovie)
            _addFavoriteMovie.postValue(favMovie)
        }
    }

    fun deleteFavMovie(favMovie : FavoriteMovie) {
        CoroutineScope(Dispatchers.IO).launch {
            db.deleteFavorite(favMovie)
            _deleteFavoriteMovie.postValue(favMovie)
        }
    }
}