package com.rich.movieupdate.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.rich.movieupdate.R
import com.rich.movieupdate.data.local.FavoriteMovie
import com.rich.movieupdate.databinding.FragmentDetailMovieBinding
import com.rich.movieupdate.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailMovieFragment : Fragment() {
    private lateinit var binding: FragmentDetailMovieBinding
    private lateinit var movieVM: MovieViewModel
    private lateinit var selectedMovie: FavoriteMovie
    private lateinit var username: String
    private var isFavorite by Delegates.notNull<Boolean>()
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
        username = requireActivity().getSharedPreferences("user", 0).getString("username", "")!!
        getMovieId()
        getMovieDetail()
        checkFavoriteMovie(movieId)
        setFavoriteListener()
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
                selectedMovie = FavoriteMovie(
                    it.id!!,
                    it.title!!,
                    it.releaseDate!!,
                    it.voteAverage!!,
                    it.posterPath!!,
                    username
                )
            }
        }
    }

    private fun setFavoriteListener(){
        binding.btnFavorite.apply {
            setOnClickListener {
                if(!isFavorite){
                    addToFavorite(selectedMovie)
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
                    isFavorite = true
                }else {
                    deleteFromFavorite(selectedMovie)
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
                    isFavorite = false
                }
            }
        }
    }

    private fun addToFavorite(movie : FavoriteMovie){
        movieVM.addFavMovie(movie)
        movieVM.observerAddFavoriteMovie().observe(viewLifecycleOwner){
            if(it != null){
                Toast.makeText(requireContext(), resources.getString(R.string.success_add_fav), Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), resources.getString(R.string.failed_add_fav), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteFromFavorite(movie: FavoriteMovie){
        movieVM.deleteFavMovie(movie)
        movieVM.observerDeleteFavoriteMovie().observe(viewLifecycleOwner){
            if(it != null){
                Toast.makeText(requireContext(), resources.getString(R.string.success_delete_fav), Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), resources.getString(R.string.failed_delete_fav), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkFavoriteMovie(movieId: Int){
        movieVM.isFavoriteMovie(movieId)
        movieVM.observerCheckIsFav().observe(viewLifecycleOwner){
            if(it != null){
                if (it){
                    isFavorite = true
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
                }else{
                    isFavorite = false
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
                }
            }else{
                Log.d("CHECK_FAV", "checkFavoriteMovie: ${it}")
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