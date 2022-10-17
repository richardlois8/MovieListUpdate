package com.rich.movieupdate.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.rich.movieupdate.databinding.FragmentDetailMovieBinding
import com.rich.movieupdate.viewmodel.MovieViewModel
import kotlin.math.round
import kotlin.properties.Delegates

class DetailMovieFragment : Fragment() {
    private lateinit var binding: FragmentDetailMovieBinding
    private lateinit var movieVM: MovieViewModel
    private var movieId by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movieVM = ViewModelProvider(this).get(MovieViewModel::class.java)
        getMovieId()
        getMovieDetail()
    }

    private fun getMovieId() {
        movieId = arguments?.getInt("movieId")!!
    }

    private fun getMovieDetail() {
        showLoading(true)
        movieVM.callGetMovieDetail(movieId)
        movieVM.observerGetMovieDetail().observe(viewLifecycleOwner) {
            if (it != null) {
                showLoading(false)
                Glide.with(requireContext())
                    .load("https://image.tmdb.org/t/p/w500${it.posterPath}")
                    .into(binding.imgPoster)
                binding.tvMovieTitle.text = it.title
                binding.tvMovieTagline.text = it.tagline
                binding.tvMovieGenre.text = it.genres!!.get(0)!!.name
                binding.tvMovieVoteAvg.text = round(it.voteAverage!!).toString() + "/10"
                binding.tvMovieRuntime.text = it.runtime.toString()
                binding.tvMovieOverview.text = it.overview
            }
        }
    }

    private fun showLoading(isLoading : Boolean) {
        if(isLoading){
            binding.lottieLoading.visibility = View.VISIBLE
            binding.progressBarContainer.visibility = View.VISIBLE
        }else{
            binding.lottieLoading.visibility = View.GONE
            binding.progressBarContainer.visibility = View.GONE
        }
    }
}