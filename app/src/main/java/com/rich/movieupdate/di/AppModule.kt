package com.rich.movieupdate.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.rich.movieupdate.data.local.FavoriteDAO
import com.rich.movieupdate.data.local.FavoriteDatabase
import com.rich.movieupdate.data.local.UserManager
import com.rich.movieupdate.data.remote.APIConfig
import com.rich.movieupdate.data.remote.APIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun getMovieService() : APIService {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(APIService::class.java)
    }

    @Provides
    fun getUserManager(@ApplicationContext context: Context) : UserManager = UserManager(context)

    @Provides
    fun provideFavoriteDAO(db : FavoriteDatabase) : FavoriteDAO = db.favoriteDao()

    @Singleton
    @Provides
    fun provideDB(@ApplicationContext context: Context) : FavoriteDatabase{
        return Room.databaseBuilder(context, FavoriteDatabase::class.java, "favorite.db").build()
    }
}