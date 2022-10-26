package com.rich.movieupdate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteDAO {
    @Query("SELECT * FROM favoritemovie WHERE addedBy = :username")
    fun getAllFavorite(username : String) : List<FavoriteMovie>

    @Query("SELECT EXISTS(SELECT id FROM favoritemovie WHERE id = :id)")
    fun isFavoriteMovie(id : Int) : Boolean

    @Insert
    fun addFavorite(favoriteMovie: FavoriteMovie)

    @Delete
    fun deleteFavorite(favoriteMovie: FavoriteMovie)
}