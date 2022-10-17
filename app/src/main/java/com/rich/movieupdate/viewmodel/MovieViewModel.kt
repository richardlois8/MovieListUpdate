package com.rich.movieupdate.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rich.movieupdate.response.*
import com.rich.movieupdate.service.APIConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieViewModel : ViewModel() {
    var liveDataMovie : MutableLiveData<List<MovieResult>>
    var livedataGetMovieDetail : MutableLiveData<MovieDetailResponse?>
    var livedataGetNowPlayingMovie : MutableLiveData<List<NowPlayingMovieItem>>

    init {
        liveDataMovie = MutableLiveData()
        livedataGetMovieDetail = MutableLiveData()
        livedataGetNowPlayingMovie = MutableLiveData()
    }

    fun getLDMovie() : MutableLiveData<List<MovieResult>> = liveDataMovie

    fun observerGetMovieDetail() : MutableLiveData<MovieDetailResponse?> = livedataGetMovieDetail

    fun observerGetNowPlayingMovie() : MutableLiveData<List<NowPlayingMovieItem>> = livedataGetNowPlayingMovie

    fun callGetPopularMovieApi() {
        val client = APIConfig.getMovieService().getPopularMovies()
        client.enqueue(object : Callback<PopularMovieResponse> {
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
        val client = APIConfig.getMovieService().getMovieDetail(movie_id)
        client.enqueue(object : Callback<MovieDetailResponse>{
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
        val client = APIConfig.getMovieService().getNowPlayingMovie()
        client.enqueue(object : Callback<NowPlayingResponse>{
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
}