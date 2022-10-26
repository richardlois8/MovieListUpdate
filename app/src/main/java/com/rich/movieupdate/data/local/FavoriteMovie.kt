package com.rich.movieupdate.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
class FavoriteMovie (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title : String,
    var releaseDate : String,
    var voteAverage : Double,
    var posterPath : String,
    var addedBy : String
) : Parcelable