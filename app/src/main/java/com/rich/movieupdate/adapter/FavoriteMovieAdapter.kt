package com.rich.movieupdate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rich.movieupdate.data.local.FavoriteMovie
import com.rich.movieupdate.data.response.NowPlayingMovieItem
import com.rich.movieupdate.databinding.ItemMovieFavoriteBinding
import com.rich.movieupdate.databinding.ItemMovieNowPlayingBinding

class FavoriteMovieAdapter(private val listMovie: List<FavoriteMovie>) : RecyclerView.Adapter<FavoriteMovieAdapter.ViewHolder>() {
    var onClick : ((FavoriteMovie) -> Unit)? = null
    var onDeleteClick : ((FavoriteMovie) -> Unit)? = null
    class ViewHolder(var binding : ItemMovieFavoriteBinding, var onClick : ((FavoriteMovie) -> Unit)?,var onDeleteClick : ((FavoriteMovie) -> Unit)?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movieParam : FavoriteMovie) {
            with(itemView) {
                binding.apply {
                    btnDelete.visibility = View.VISIBLE
                    movie = movieParam
                    imgCard.setOnClickListener {
                        onClick?.invoke(movieParam)
                    }
                    btnDelete.setOnClickListener {
                        onDeleteClick?.invoke(movieParam)
                    }
                }
                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w400${movieParam.posterPath}")
                    .into(binding.ivMoviePoster)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemMovieFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view,onClick,onDeleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listMovie[position])
    }

    override fun getItemCount(): Int = listMovie.size
}