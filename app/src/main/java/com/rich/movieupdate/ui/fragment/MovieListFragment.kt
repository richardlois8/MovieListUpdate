package com.rich.movieupdate.ui.fragment

import android.app.ProgressDialog.show
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.rich.movieupdate.R
import com.rich.movieupdate.adapter.NowPlayingAdapter
import com.rich.movieupdate.adapter.PosterAdapter
import com.rich.movieupdate.databinding.FragmentMovieListBinding
import com.rich.movieupdate.data.local.UserManager
import com.rich.movieupdate.data.response.MovieResult
import com.rich.movieupdate.data.response.NowPlayingMovieItem
import com.rich.movieupdate.ui.activity.MainActivity
import com.rich.movieupdate.viewmodel.MovieViewModel
import com.rich.movieupdate.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.lang.Math.abs
import javax.inject.Inject

@AndroidEntryPoint
class MovieListFragment : Fragment() {
    private lateinit var binding : FragmentMovieListBinding
    private lateinit var movieVM : MovieViewModel
    private lateinit var userVM : UserViewModel
    private lateinit var handler: Handler
    private lateinit var sharedPref : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movieVM = ViewModelProvider(requireActivity()).get(MovieViewModel::class.java)
        userVM = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        sharedPref = requireActivity().getSharedPreferences("user", 0)
        setUsernameProfile()
        gotoProfile()
        getPopularMovies()
        getNowPlayingMovies()
        setupTransformer()
        showBottomNav()
        binding.posterViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 3000)
            }
        })

        binding.imgProfile.setOnClickListener {
            gotoProfile()
        }
    }

    private fun showBottomNav() {
        (activity as MainActivity).binding.bottomNav.visibility = View.VISIBLE
    }

    private fun gotoProfile() {
        binding.imgProfile.setOnClickListener {
            findNavController().navigate(R.id.action_movieListFragment_to_profileFragment)
        }
    }

    private fun setUsernameProfile() {
        if(sharedPref.getString("idToken","") != "") {
            binding.username = sharedPref.getString("username", "")
            Glide.with(requireContext())
                .load(sharedPref.getString("photoUri", ""))
                .into(binding.imgProfile)
        }else{
            userVM.getUsername(viewLifecycleOwner)
            userVM.observerUsername().observe(viewLifecycleOwner) {
                binding.username = it
            }
            var image = BitmapFactory.decodeFile(requireActivity().applicationContext.filesDir.path + File.separator +"profiles"+ File.separator +"img-profile.png")
            binding.imgProfile.setImageBitmap(image)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 3000)
    }

    private fun setupTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        binding.posterViewPager.setPageTransformer(transformer)
    }

    private val runnable = Runnable {
        binding.posterViewPager.currentItem = binding.posterViewPager.currentItem + 1
    }

    private fun getPopularMovies() {
        handler = Handler(Looper.myLooper()!!)
        showLoading(true)
        movieVM.callGetPopularMovieApi()
        movieVM.popularMovie.observe(viewLifecycleOwner){
            if (it != null) {
                Log.d("RESULT",it.toString())
                showLoading(false)
                showPopularMovies(it)
            }
        }
    }

    private fun showPopularMovies(data: List<MovieResult>) {
        val adapter = PosterAdapter(data,binding.posterViewPager)
        binding.posterViewPager.adapter = adapter
        adapter.onClick = {
            gotoDetailMovie(it.id!!)
        }
        binding.posterViewPager.offscreenPageLimit = 2
        binding.posterViewPager.clipToPadding = false
        binding.posterViewPager.clipChildren = false
        binding.posterViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    private fun getNowPlayingMovies(){
        movieVM.callGetNowPlayingMovie()
        movieVM.nowPlayingMovie.observe(viewLifecycleOwner){
            if (it != null) {
                showLoading(false)
                showNowPlayingMovies(it)
            }
        }
    }

    private fun showNowPlayingMovies(data : List<NowPlayingMovieItem>) {
        val nowPlayAdapter = NowPlayingAdapter(data)
        binding.recViewNowPlaying.adapter = nowPlayAdapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val itemDecor = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.recViewNowPlaying.layoutManager = layoutManager
        binding.recViewNowPlaying.addItemDecoration(itemDecor)
        nowPlayAdapter.onClick = {
            gotoDetailMovie(it.id!!)
        }
    }

    private fun gotoDetailMovie(idMovie : Int) {
        val action =
            MovieListFragmentDirections.actionMovieListFragmentToDetailMovieFragment(
                idMovie
            )
        findNavController().navigate(action)
    }

    private fun showLoading(isLoading : Boolean){
        if(isLoading){
            binding.shimmerLayout.visibility = View.VISIBLE
            binding.shimmerNowPlayLayout.visibility = View.VISIBLE
            binding.shimmerLayout.startShimmerAnimation()
            binding.shimmerNowPlayLayout.startShimmerAnimation()
        }else{
            binding.shimmerLayout.stopShimmerAnimation()
            binding.shimmerNowPlayLayout.stopShimmerAnimation()
            binding.shimmerLayout.visibility = View.GONE
            binding.shimmerNowPlayLayout.visibility = View.GONE
        }
    }
}