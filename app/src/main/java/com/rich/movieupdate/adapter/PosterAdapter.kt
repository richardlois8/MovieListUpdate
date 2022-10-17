package com.rich.movieupdate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.rich.movieupdate.databinding.ItemMovieBinding
import com.rich.movieupdate.response.MovieResult

class PosterAdapter(private val listMovie : List<MovieResult>,private val viewPager2: ViewPager2) : RecyclerView.Adapter<PosterAdapter.ViewHolder>() {
    var onClick : ((MovieResult) -> Unit)? = null

    class ViewHolder(var binding : ItemMovieBinding, var onClick : ((MovieResult) -> Unit)?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieResult) {
            with(itemView) {
                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w400${movie.posterPath}")
                    .into(binding.ivMoviePoster)
            }
            binding.movieCard.setOnClickListener {
                onClick?.invoke(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view,onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listMovie[position])
        if(position == listMovie.size - 1) {
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int = listMovie.size

    private val runnable = Runnable {
//        listMovie.addAll(listMovie)
        listMovie.plus(listMovie)
        notifyDataSetChanged()
    }
}
