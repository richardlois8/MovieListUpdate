package com.rich.movielistapi.data.remote

import com.rich.movielistapi.data.response.*
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @GET("movie/popular?api_key=d0c0a4f115a9c38e70318ce7769ff669&language=en-US&page=3")
    fun getPopularMovies() : Call<PopularMovieResponse>

    @GET("movie/popular?api_key=d0c0a4f115a9c38e70318ce7769ff669&language=en-US&page=3")
    fun getPopularMoviesTest() : List<MovieResult>

    @GET("movie/now_playing?api_key=d0c0a4f115a9c38e70318ce7769ff669&language=en-US&page=2")
    fun getNowPlayingMovie() : Call<NowPlayingResponse>

    @GET("movie/{movie_id}?api_key=d0c0a4f115a9c38e70318ce7769ff669&language=en-US")
    fun getMovieDetail(@Path("movie_id") movie_id : Int) : Call<MovieDetailResponse>

    @GET("user")
    fun getAllUser() : Call<List<GetUserResponseItem>>

    @GET("user/{id}")
    fun getUserById(@Path("id") id : String) : Call<GetUserResponseItem>

    @FormUrlEncoded
    @PUT("user/{id}")
    fun updateUser(@Path("id") id : String, @Field("email") email : String, @Field("username") username : String, @Field("password") password : String) : Call<UserResponse>

    @FormUrlEncoded
    @POST("user")
    fun registerUser(@Field("email") email : String, @Field("username") username : String, @Field("password") password : String)
    : Call<UserResponse>
}