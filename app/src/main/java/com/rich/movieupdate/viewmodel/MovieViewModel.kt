package com.rich.movieupdate.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rich.movieupdate.data.local.FavoriteDAO
import com.rich.movieupdate.data.local.FavoriteMovie
import com.rich.movieupdate.data.response.*
import com.rich.movieupdate.data.remote.APIConfig
import com.rich.movieupdate.data.remote.APIService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(val client : APIService, val db : FavoriteDAO): ViewModel() {
    var liveDataMovie : MutableLiveData<List<MovieResult>>
    var livedataGetMovieDetail : MutableLiveData<MovieDetailResponse?>
    var livedataGetNowPlayingMovie : MutableLiveData<List<NowPlayingMovieItem>>
    var livedataGetAllFavorite : MutableLiveData<List<FavoriteMovie>>
    var livedataCheckIsFav : MutableLiveData<Boolean>
    var livedataAddFavorite : MutableLiveData<FavoriteMovie>
    var livedataDeleteFavorite : MutableLiveData<FavoriteMovie>

    init {
        liveDataMovie = MutableLiveData()
        livedataGetMovieDetail = MutableLiveData()
        livedataGetNowPlayingMovie = MutableLiveData()
        livedataGetAllFavorite = MutableLiveData()
        livedataCheckIsFav = MutableLiveData()
        livedataAddFavorite = MutableLiveData()
        livedataDeleteFavorite = MutableLiveData()
    }

    fun getLDMovie() : MutableLiveData<List<MovieResult>> = liveDataMovie
    fun observerGetMovieDetail() : MutableLiveData<MovieDetailResponse?> = livedataGetMovieDetail
    fun observerGetNowPlayingMovie() : MutableLiveData<List<NowPlayingMovieItem>> = livedataGetNowPlayingMovie
    fun observerGetAllFavoriteMovie() : MutableLiveData<List<FavoriteMovie>> = livedataGetAllFavorite
    fun observerCheckIsFav() : MutableLiveData<Boolean> = livedataCheckIsFav
    fun observerAddFavoriteMovie() : MutableLiveData<FavoriteMovie> = livedataAddFavorite
    fun observerDeleteFavoriteMovie() : MutableLiveData<FavoriteMovie> = livedataDeleteFavorite

    fun callGetPopularMovieApi() {
        client.getPopularMovies().enqueue(object : Callback<PopularMovieResponse> {
            override fun onResponse(
                call: Call<PopularMovieResponse>,
                response: Response<PopularMovieResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        liveDataMovie.postValue(data.results as List<MovieResult>?)
                    }
                } else {
                    Log.e("Error : ", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PopularMovieResponse>, t: Throwable) {
                Log.e("Error ; ", "onFailure: ${t.message}")
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
                        livedataGetMovieDetail.postValue(data)
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
                        livedataGetNowPlayingMovie.postValue(data.results as List<NowPlayingMovieItem>)
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

    fun getAllFavoriteMovie() {
        GlobalScope.launch {
            livedataGetAllFavorite.postValue(db.getAllFavorite())
        }
    }

    fun isFavoriteMovie(id : Int) {
        GlobalScope.launch {
            livedataCheckIsFav.postValue(db.isFavoriteMovie(id))
        }
    }

    fun addFavMovie(favMovie : FavoriteMovie) {
        GlobalScope.launch {
            db.addFavorite(favMovie)
            livedataAddFavorite.postValue(favMovie)
        }
    }

    fun deleteFavMovie(favMovie : FavoriteMovie) {
        GlobalScope.launch {
            db.deleteFavorite(favMovie)
            livedataDeleteFavorite.postValue(favMovie)
        }
    }
}