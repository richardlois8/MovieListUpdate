package com.rich.movielistapi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rich.movielistapi.databinding.ItemMovieNowPlayingBinding
import com.rich.movielistapi.data.response.NowPlayingMovieItem

class NowPlayingAdapter(private val listMovie: List<NowPlayingMovieItem>) : RecyclerView.Adapter<NowPlayingAdapter.ViewHolder>() {
    var onClick : ((NowPlayingMovieItem) -> Unit)? = null
    class ViewHolder(var binding : ItemMovieNowPlayingBinding, var onClick : ((NowPlayingMovieItem) -> Unit)?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie : NowPlayingMovieItem) {
            with(itemView) {
                binding.movie = movie
                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w400${movie.posterPath}")
                    .into(binding.ivMoviePoster)
            }
            binding.imgCard.setOnClickListener {
                onClick?.invoke(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemMovieNowPlayingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view,onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listMovie[position])
    }

    override fun getItemCount(): Int = listMovie.size
}